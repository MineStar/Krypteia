/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of Krypteia.
 * 
 * Krypteia is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * Krypteia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Krypteia.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.krypteia.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Queue;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.krypteia.data.QueuedData;
import de.minestar.krypteia.data.ScannedData;
import de.minestar.krypteia.thread.queues.BlockQueue;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class DatabaseHandler extends AbstractMySQLHandler {

    public DatabaseHandler(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(this.getClass().getResourceAsStream("/structure.sql"), con, pluginName);
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {

        // BLOCK
        hasBlockData = con.prepareStatement("SELECT 1 FROM blocks WHERE world = ?");
        getDataBlocks = con.prepareStatement("SELECT blockId, x, y, z FROM blocks WHERE world = ? ORDER BY blockId, y, x, z");

        createBlockQueueStatement(con);

        // MOB
        hasMobData = con.prepareStatement("SELECT 1 FROM mobs WHERE world = ?");
        getDataMobs = con.prepareStatement("SELECT mobId, x, y , z FROM mobs WHERE world = ? ORDER BY mobId, y, x, z");

        createMobQueueStatement(con);
    }

    // BLOCK
    private PreparedStatement hasBlockData;
    private PreparedStatement getDataBlocks;
    private PreparedStatement blockQueueStatement;

    private void createBlockQueueStatement(Connection con) throws Exception {

        StringBuilder sBuilder = new StringBuilder("INSERT INTO blocks (blockID, x, y, z, world) VALUES ");
        for (int i = 0; i < BlockQueue.QUEUE_SIZE; ++i)
            sBuilder.append("(?, ?, ? , ?, ?),");

        sBuilder.deleteCharAt(sBuilder.length() - 1);

        blockQueueStatement = con.prepareStatement(sBuilder.toString());
    }

    public void flushBlockQueue(Queue<QueuedData> tempQueue) {

        try {
            int index = 1;
            QueuedData block = null;
            while (!tempQueue.isEmpty()) {
                block = tempQueue.poll();
                blockQueueStatement.setInt(index++, block.getID());
                blockQueueStatement.setInt(index++, block.getX());
                blockQueueStatement.setInt(index++, block.getY());
                blockQueueStatement.setInt(index++, block.getZ());
                blockQueueStatement.setString(index++, block.getWorldName());
            }
            tempQueue.clear();
            tempQueue = null;
            block = null;
            blockQueueStatement.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't flush queue to database!");
        }
    }

    public boolean hasBlockData(String worldName) {

        try {

            hasBlockData.setString(1, worldName);

            return hasBlockData.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't check if there is data for world '" + worldName + "'!");
            return false;
        }
    }

    public ArrayList<ScannedData> getScannedDataBlock(String worldName) {

        ArrayList<ScannedData> result = new ArrayList<ScannedData>(8192);
        try {
            getDataBlocks.setString(1, worldName);

            // TEMP VARS
            int x, y, z;
            int blockID;

            ResultSet rs = getDataBlocks.executeQuery();

            while (rs.next()) {
                blockID = rs.getInt(1);
                x = rs.getInt(2);
                y = rs.getInt(3);
                z = rs.getInt(4);

                result.add(new ScannedData(x, y, z, blockID));
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't fetch data blocks for world '" + worldName + "'!");
            result.clear();
        }

        return result;
    }

    public void finishBlockQueue(String sqlString) {
        try {
            Statement st = dbConnection.getConnection().createStatement();
            st.executeUpdate(sqlString);
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't finish queue to database!");
        }
    }

    // MOB
    private PreparedStatement hasMobData;
    private PreparedStatement getDataMobs;
    private PreparedStatement mobQueueStatement;

    private void createMobQueueStatement(Connection con) throws Exception {

        StringBuilder sBuilder = new StringBuilder("INSERT INTO mobs (mobID, x, y, z, world) VALUES ");
        for (int i = 0; i < BlockQueue.QUEUE_SIZE; ++i)
            sBuilder.append("(?, ?, ? , ?, ?),");

        sBuilder.deleteCharAt(sBuilder.length() - 1);

        mobQueueStatement = con.prepareStatement(sBuilder.toString());
    }

    public void flushMobQueue(Queue<QueuedData> tempQueue) {

        try {
            int index = 1;
            QueuedData block = null;
            while (!tempQueue.isEmpty()) {
                block = tempQueue.poll();
                mobQueueStatement.setInt(index++, block.getID());
                mobQueueStatement.setInt(index++, block.getX());
                mobQueueStatement.setInt(index++, block.getY());
                mobQueueStatement.setInt(index++, block.getZ());
                mobQueueStatement.setString(index++, block.getWorldName());
            }
            tempQueue.clear();
            tempQueue = null;
            block = null;
            mobQueueStatement.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't flush queue to database!");
        }
    }

    public boolean hasMobData(String worldName) {

        try {

            hasMobData.setString(1, worldName);

            return hasMobData.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't check if there is data for world '" + worldName + "'!");
            return false;
        }
    }

    public ArrayList<ScannedData> getScannedDataMobs(String worldName) {

        ArrayList<ScannedData> result = new ArrayList<ScannedData>(8192);
        try {
            getDataMobs.setString(1, worldName);

            // TEMP VARS
            int x, y, z;
            int blockID;

            ResultSet rs = getDataMobs.executeQuery();

            while (rs.next()) {
                blockID = rs.getInt(1);
                x = rs.getInt(2);
                y = rs.getInt(3);
                z = rs.getInt(4);

                result.add(new ScannedData(x, y, z, blockID));
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't fetch data blocks for world '" + worldName + "'!");
            result.clear();
        }

        return result;
    }

    public void finishMobQueue(String sqlString) {
        try {
            Statement st = dbConnection.getConnection().createStatement();
            st.executeUpdate(sqlString);
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't finish queue to database!");
        }
    }
}
