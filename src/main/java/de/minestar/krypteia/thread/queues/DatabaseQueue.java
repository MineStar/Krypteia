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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import de.minestar.krypteia.data.QueuedData;

public abstract class DatabaseQueue implements Runnable {

    protected Queue<QueuedData> queue = new LinkedBlockingDeque<QueuedData>();
    private final int queueSize;

    public DatabaseQueue(int queueSize) {
        this.queueSize = queueSize;
    }

    public synchronized void addData(int x, int y, int z, String worldName, int ID) {
        queue.add(new QueuedData(x, y, z, worldName, ID));
    }

    @Override
    public void run() {
        if (queue.size() >= queueSize)
            flushQueue();
    }

    public void flushQueue() {
        if (!queue.isEmpty()) {
            Queue<QueuedData> tempQueue = new LinkedList<QueuedData>();
            for (int i = 0; i < queueSize; ++i)
                tempQueue.offer(queue.poll());

            flushQueueExecute(tempQueue);
        }
    }

    protected abstract void flushQueueExecute(Queue<QueuedData> queue);

    public void finishQueue() {
        if (!queue.isEmpty())
            finishQueueExecute();
    }

    protected abstract void finishQueueExecute();

    protected abstract String createQueueStatement();

}
