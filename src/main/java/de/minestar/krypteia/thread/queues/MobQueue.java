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

package de.minestar.krypteia.thread.queues;

import java.util.Queue;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.krypteia.data.QueuedData;

public class MobQueue extends DatabaseQueue {

    public final static int QUEUE_SIZE = 256;

    public MobQueue() {
        super(QUEUE_SIZE);
    }

    @Override
    protected void flushQueueExecute(Queue<QueuedData> queue) {
        KrypteiaCore.dbHandler.flushMobQueue(queue);

    }

    @Override
    protected void finishQueueExecute() {
        KrypteiaCore.dbHandler.finishMobQueue(createQueueStatement());

    }

    @Override
    protected String createQueueStatement() {
        StringBuilder sBuilder = new StringBuilder("INSERT INTO mobs (mobID, x, y, z, world) VALUES ");
        while (!queue.isEmpty()) {
            sBuilder.append(queue.poll().convertToString());
            sBuilder.append(',');
        }

        sBuilder.deleteCharAt(sBuilder.length() - 1);

        return sBuilder.toString();
    }
}
