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

public class DataBlock {

    private int x, y, z;
    private String worldName;
    private int blockID;

    private int neighbors = 0;

    public DataBlock(int x, int y, int z, String worldName, int blockID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.blockID = blockID;
    }

    public boolean sameBlockID(DataBlock other) {
        return sameBlockID(other.blockID);
    }

    public boolean sameBlockID(int blockID) {
        return this.blockID == blockID;
    }

    public boolean isInRange(DataBlock other, int r) {
        return isInRange(other.x, other.y, other.z, r);
    }

    public boolean isInRange(int x, int y, int z, int r) {
        return r >= Math.sqrt(Math.abs(this.x - x) * Math.abs(this.y - y) * Math.abs(this.z - z));
    }

    public void addNeighbor() {
        ++neighbors;
    }

    public int getNeighbors() {
        return neighbors;
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

    public int getBlockID() {
        return blockID;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append("Neighbors=");
        sBuilder.append(neighbors);
        sBuilder.append(", ID=");
        sBuilder.append(blockID);
        sBuilder.append(", X=");
        sBuilder.append(x);
        sBuilder.append(", Y=");
        sBuilder.append(y);
        sBuilder.append(", Z=");
        sBuilder.append(z);
        sBuilder.append(", World=");
        sBuilder.append(worldName);

        return sBuilder.toString();
    }
}
