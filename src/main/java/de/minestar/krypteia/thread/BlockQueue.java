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

package de.minestar.krypteia.thread;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.krypteia.data.QueuedBlock;

public class BlockQueue implements Runnable {

    public final static int QUEUE_SIZE = 64;

    private Queue<QueuedBlock> queue = new LinkedBlockingDeque<QueuedBlock>();

    public synchronized void addBlock(int x, int y, int z, String worldName, int blockId) {
        queue.add(new QueuedBlock(x, y, z, worldName, blockId));
    }

    @Override
    public void run() {
        if (queue.size() >= QUEUE_SIZE)
            flushQueue();

    }

    public void flushQueue() {
        if (!queue.isEmpty())
            KrypteiaCore.dbHandler.flushQueue(createQueueStatement());
    }

    private String createQueueStatement() {

        StringBuilder sBuilder = new StringBuilder("INSERT INTO blocks (blockID, x, y, z, world) VALUES ");
        while (!queue.isEmpty()) {
            sBuilder.append(queue.poll().convertToString());
            sBuilder.append(',');
        }

        sBuilder.deleteCharAt(sBuilder.length() - 1);

        return sBuilder.toString();
    }
}
