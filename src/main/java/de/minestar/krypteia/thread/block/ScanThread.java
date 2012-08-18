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

package de.minestar.krypteia.thread.block;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class ScanThread implements Runnable {

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
            KrypteiaCore.blockQueue.finishQueue();
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

        ChunkSnapshot snapShot = null;
        Chunk chunk = null;
        for (int z = startZ; z <= endZ; ++z) {
            curZ = z;
            chunk = world.getChunkAt(curX, curZ);
            if (chunk != null) {
                snapShot = chunk.getChunkSnapshot();
                if (snapShot != null)
                    scanChunk(snapShot, world.getName().toLowerCase());
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

    private void scanChunk(ChunkSnapshot snapShot, String worldName) {
        int blockID = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < 255; ++y) {

                    blockID = snapShot.getBlockTypeId(x, y, z);
                    if (interestingBlockIDs.contains(blockID))
                        KrypteiaCore.blockQueue.addBlock(x + (snapShot.getX() << 4), y, z + (snapShot.getZ() << 4), worldName, blockID);
                }
            }
        }

    }

    private static Set<Integer> interestingBlockIDs;

    static {
        interestingBlockIDs = new HashSet<Integer>();
        interestingBlockIDs.add(Material.CROPS.getId());
        interestingBlockIDs.add(Material.CACTUS.getId());
        interestingBlockIDs.add(Material.PUMPKIN_STEM.getId());
        interestingBlockIDs.add(Material.SUGAR_CANE_BLOCK.getId());
        interestingBlockIDs.add(Material.MELON_STEM.getId());
        interestingBlockIDs.add(Material.COCOA.getId());
    }

    public void setThreadId(int id) {
        this.threadId = id;
    }

}
