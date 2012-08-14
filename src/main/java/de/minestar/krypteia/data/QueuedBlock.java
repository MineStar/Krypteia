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

package de.minestar.krypteia.data;

import de.minestar.minestarlibrary.database.DatabaseUtils;

public class QueuedBlock {

    private final int x;
    private final int y;
    private final int z;
    private final String worldName;
    private final int blockID;

    public QueuedBlock(int x, int y, int z, String worldName, int blockID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.blockID = blockID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getID() {
        return blockID;
    }

    public String convertToString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append('(');

        sBuilder.append(blockID);
        sBuilder.append(',');
        sBuilder.append(x);
        sBuilder.append(',');
        sBuilder.append(y);
        sBuilder.append(',');
        sBuilder.append(z);
        sBuilder.append(',');
        DatabaseUtils.appendSQLString(sBuilder, worldName);

        sBuilder.append(')');

        return sBuilder.toString();
    }

}
