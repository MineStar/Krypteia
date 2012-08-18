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

package de.minestar.krypteia.thread.scan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public abstract class ScanThread implements Runnable {

    private World world;

    private int startX;
    private int startZ;

    private int endX;
    private int endZ;

    private int curX;
    private int curZ;

    private int counter = 1;
    private int chunkCount;
    float percent = 0.0F;

    private int threadId;

    private List<Chunk> chunksToUnload = new LinkedList<Chunk>();

    public ScanThread(World world, int size) {
        this.world = world;

        Location spawn = world.getSpawnLocation();
        startX = spawn.getBlockX() - size;
        startZ = spawn.getBlockZ() - size;
        endX = spawn.getBlockX() + size;
        endZ = spawn.getBlockZ() + size;

        startX = startX >> 4;
        startZ = startZ >> 4;
        endX = endX >> 4;
        endZ = endZ >> 4;

        curX = startX;

        chunkCount = calculateSize(startX, startZ, endX, endZ);
    }

    @Override
    public void run() {
        if (curX == endX) {
            KrypteiaCore.queue.finishQueue();
            Bukkit.getScheduler().cancelTask(threadId);
            ConsoleUtils.printInfo(KrypteiaCore.NAME, "Scan finished");
        } else {
            unloadChunks();
            scanWorld();
        }
    }

    private void unloadChunks() {
        for (Chunk c : chunksToUnload)
            c.unload(false, false);

        chunksToUnload.clear();
    }

    private final static NumberFormat FORMAT = DecimalFormat.getPercentInstance();

    private void scanWorld() {

        Chunk chunk = null;
        String worldName = world.getName().toLowerCase();
        for (int z = startZ; z <= endZ; ++z) {
            curZ = z;
            chunk = world.getChunkAt(curX, curZ);
            if (chunk != null) {
                scanChunk(chunk, worldName);
                chunksToUnload.add(chunk);
            }

            ++counter;
        }
        percent = (float) counter / (float) chunkCount;
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Scan Thread Status = " + FORMAT.format(percent));
        ++curX;

    }

    private int calculateSize(int x1, int z1, int x2, int z2) {
        int x = Math.abs(x1 - x2);
        int z = Math.abs(z1 - z2);

        return x * z;
    }

    protected abstract void scanChunk(Chunk chunk, String worldName);

    protected abstract boolean isInterestingID(int id);

    public void setThreadId(int id) {
        this.threadId = id;
    }

}
