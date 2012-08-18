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

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;

import de.minestar.krypteia.core.KrypteiaCore;

public class BlockScanThread extends ScanThread {

    public BlockScanThread(World world, int size) {
        super(world, size);
    }

    private ChunkSnapshot snapShot;

    @Override
    protected void scanChunk(Chunk chunk, String worldName) {
        snapShot = chunk.getChunkSnapshot(true, false, false);
        if (snapShot == null)
            return;
        int blockID = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < 255; ++y) {

                    blockID = snapShot.getBlockTypeId(x, y, z);
                    if (isInterestingID(blockID))
                        KrypteiaCore.queue.addData(x + (snapShot.getX() << 4), y, z + (snapShot.getZ() << 4), worldName, blockID);
                }
            }
        }
    }

    @Override
    protected boolean isInterestingID(int id) {
        return blockIDs.contains(id);
    }

    private static Set<Integer> blockIDs;

    static {
        blockIDs = new HashSet<Integer>();
        blockIDs.add(Material.CROPS.getId());
        blockIDs.add(Material.CACTUS.getId());
        blockIDs.add(Material.PUMPKIN_STEM.getId());
        blockIDs.add(Material.SUGAR_CANE_BLOCK.getId());
        blockIDs.add(Material.MELON_STEM.getId());
        blockIDs.add(Material.COCOA.getId());
    }
}
