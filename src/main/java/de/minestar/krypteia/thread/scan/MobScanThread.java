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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import de.minestar.krypteia.core.KrypteiaCore;

public class MobScanThread extends ScanThread {

    public MobScanThread(World world, int size) {
        super(world, size);
    }

    private Entity[] entities;
    private Location loc;

    @Override
    protected void scanChunk(Chunk chunk, String worldName) {

        entities = chunk.getEntities();
        if (entities == null)
            return;

        for (Entity mob : entities) {
            if (isInterestingID(mob.getType().getTypeId())) {
                loc = mob.getLocation();
                KrypteiaCore.queue.addData(loc.getBlockX() + (chunk.getX() << 4), loc.getBlockY(), loc.getBlockZ() + (chunk.getZ() << 4), worldName, mob.getEntityId());
            }
        }
    }

    @Override
    protected boolean isInterestingID(int id) {
        return mobIDs.contains((short) id);
    }

    private static Set<Short> mobIDs;

    static {
        mobIDs = new HashSet<Short>();
        mobIDs.add(EntityType.COW.getTypeId());
        mobIDs.add(EntityType.CHICKEN.getTypeId());
        mobIDs.add(EntityType.SHEEP.getTypeId());
        mobIDs.add(EntityType.MUSHROOM_COW.getTypeId());
        mobIDs.add(EntityType.PIG.getTypeId());
    }
}
