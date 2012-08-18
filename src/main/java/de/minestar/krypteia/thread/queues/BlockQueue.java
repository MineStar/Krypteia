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

public class BlockQueue extends DatabaseQueue {

    public final static int QUEUE_SIZE = 256;

    public BlockQueue() {
        super(QUEUE_SIZE);
    }

    @Override
    protected void flushQueueExecute(Queue<QueuedData> queue) {
        KrypteiaCore.dbHandler.flushBlockQueue(queue);
    }

    @Override
    protected void finishQueueExecute() {
        KrypteiaCore.dbHandler.finishBlockQueue(createQueueStatement());
    }

    @Override
    protected String createQueueStatement() {
        StringBuilder sBuilder = new StringBuilder("INSERT INTO blocks (blockID, x, y, z, world) VALUES ");
        while (!queue.isEmpty()) {
            sBuilder.append(queue.poll().convertToString());
            sBuilder.append(',');
        }

        sBuilder.deleteCharAt(sBuilder.length() - 1);

        return sBuilder.toString();
    }
}
