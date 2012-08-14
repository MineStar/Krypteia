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
import java.sql.Statement;

import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.database.DatabaseUtils;

public class DatabaseHandler extends AbstractMySQLHandler {

    public DatabaseHandler(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(Class.class.getResourceAsStream("/structure.sql"), con, pluginName);
        System.out.println("lol");
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {

    }
    
    public void flushQueue(String sqlString) {

        try {
            Statement st = dbConnection.getConnection().createStatement();
            st.executeUpdate(sqlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
