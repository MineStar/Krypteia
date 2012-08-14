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
import de.minestar.krypteia.data.DataBlock;
import de.minestar.krypteia.data.QueuedBlock;
import de.minestar.krypteia.thread.BlockQueue;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class DatabaseHandler extends AbstractMySQLHandler {

    private PreparedStatement hasData;
    private PreparedStatement getDataBlocks;
    private PreparedStatement queue;

    public DatabaseHandler(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(Class.class.getResourceAsStream("/structure.sql"), con, pluginName);
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {

        hasData = con.prepareStatement("SELECT 1 FROM blocks WHERE world = ?");
        getDataBlocks = con.prepareStatement("SELECT blockId, x, y, z FROM blocks WHERE world = ? ORDER BY blockId, y, x, z");

        createQueueStatement(con);
    }

    private void createQueueStatement(Connection con) throws Exception {

        StringBuilder sBuilder = new StringBuilder("INSERT INTO blocks (blockID, x, y, z, world) VALUES ");
        for (int i = 0; i < BlockQueue.QUEUE_SIZE; ++i)
            sBuilder.append("(?, ?, ? , ?, ?),");

        sBuilder.deleteCharAt(sBuilder.length() - 1);

        queue = con.prepareStatement(sBuilder.toString());
    }

    public void flushQueue(Queue<QueuedBlock> tempQueue) {

        try {
            int index = 1;
            QueuedBlock block = null;
            while (!tempQueue.isEmpty()) {
                block = tempQueue.poll();
                queue.setInt(index++, block.getID());
                queue.setInt(index++, block.getX());
                queue.setInt(index++, block.getY());
                queue.setInt(index++, block.getZ());
                queue.setString(index++, block.getWorldName());
            }
            tempQueue.clear();
            tempQueue = null;
            block = null;
            queue.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't flush queue to database!");
        }
    }

    public boolean hasData(String worldName) {

        try {

            hasData.setString(1, worldName);

            return hasData.executeQuery().next();
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't check if there is data for world '" + worldName + "'!");
            return false;
        }
    }

    public ArrayList<DataBlock> getDataBlocks(String worldName) {

        ArrayList<DataBlock> result = new ArrayList<DataBlock>(8192);
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

                result.add(new DataBlock(x, y, z, worldName, blockID));
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't fetch data blocks for world '" + worldName + "'!");
            result.clear();
        }

        return result;
    }

    public void finishQueue(String sqlString) {
        try {
            Statement st = dbConnection.getConnection().createStatement();
            st.executeUpdate(sqlString);
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't finish queue to database!");
        }
    }
}
