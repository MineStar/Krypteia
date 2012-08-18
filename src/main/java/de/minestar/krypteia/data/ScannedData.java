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

public class ScannedData {

    private int x, y, z;
    private String worldName;
    private int ID;

    private int neighbors = 0;

    public ScannedData(int x, int y, int z, String worldName, int ID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.ID = ID;
    }

    public boolean sameID(ScannedData other) {
        return sameBlockID(other.ID);
    }

    public boolean sameBlockID(int ID) {
        return this.ID == ID;
    }

    public boolean isInRange(ScannedData other, int r) {
        return isInRange(other.x, other.y, other.z, r);
    }

    public boolean isInRange(int x, int y, int z, int r) {
        return r >= (Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2));
    }

    public void addNeighbor() {
        ++neighbors;
    }

    public void addNeighbors(ScannedData other) {
        this.neighbors += other.neighbors;
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

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append("Neighbors=");
        sBuilder.append(neighbors);
        sBuilder.append(", ID=");
        sBuilder.append(ID);
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
