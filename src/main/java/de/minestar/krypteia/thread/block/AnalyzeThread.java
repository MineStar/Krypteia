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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.krypteia.data.block.DataBlock;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class AnalyzeThread implements Runnable {

    private String worldName;
    private int radius;

    private DataBlock[] data;

    public AnalyzeThread(String worldName, int radius) {
        this.worldName = worldName;
        this.radius = radius;
    }

    @Override
    public void run() {
        if (data == null)
            loadDataBlocks();
        if (data != null)
            calculateNeighbors();
        else
            ConsoleUtils.printError(KrypteiaCore.NAME, "Can't analyze data because data is null!");

        sortData();

        summarizeData();

        writeData();
    }

    private void loadDataBlocks() {
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Fetch data from database...");
        ArrayList<DataBlock> temp = KrypteiaCore.dbHandler.getDataBlocks(worldName);
        data = (DataBlock[]) temp.toArray(new DataBlock[temp.size()]);
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Finished fetching data from database!");
    }

    private final static NumberFormat FORMAT = DecimalFormat.getPercentInstance();
    private String curPercent = "";
    private String oldPercent = "";

    private void calculateNeighbors() {
        int tempRadius = radius * radius;
        double percent = 0.0;
        DataBlock block = null;

        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Start Calculating neighbors...");
        for (int i = 0; i < data.length; ++i) {
            block = data[i];
            for (int j = i + 1; j < data.length; ++j) {
                if (block.isInRange(data[j], tempRadius))
                    block.addNeighbor();
            }
            percent = (double) i / (double) data.length;
            curPercent = FORMAT.format(percent);
            if (!oldPercent.equals(curPercent)) {
                ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Calculate neighbors: " + curPercent);
                oldPercent = curPercent;
            }
        }

        curPercent = "";
        oldPercent = "";
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Calculate neighbors finished!");
    }

    private final static Comparator<DataBlock> COMPARATOR = new Comparator<DataBlock>() {

        @Override
        public int compare(DataBlock o1, DataBlock o2) {
            if (o1.getNeighbors() == o2.getNeighbors()) {
                if (o1.getBlockID() == o2.getBlockID()) {
                    if (o1.getX() == o2.getX()) {
                        if (o1.getY() == o2.getY()) {
                            return o1.getZ() - o2.getZ();
                        } else
                            return o1.getY() - o2.getY();
                    } else
                        return o1.getX() - o2.getX();
                } else
                    return o1.getBlockID() - o2.getBlockID();
            } else
                return o2.getNeighbors() - o1.getNeighbors();
        }
    };

    private void sortData() {
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Start sorting data...");
        Arrays.sort(data, COMPARATOR);
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Finished sorting data!");
    }

    private void summarizeData() {
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Start summarizing neighbors...");
        DataBlock block;
        int tempRadius = radius * radius;
        double percent = 0.0;

        for (int i = 0; i < data.length; ++i) {
            if (data[i] == null)
                continue;
            block = data[i];
            for (int j = i + 1; j < data.length; ++j) {
                if (data[j] == null)
                    continue;
                if (block.isInRange(data[j], tempRadius)) {
                    block.addNeighbors(data[j]);
                    data[j] = null;
                }
                percent = (double) i / (double) data.length;
                curPercent = FORMAT.format(percent);
                if (!oldPercent.equals(curPercent)) {
                    ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Summarize neighbors: " + curPercent);
                    oldPercent = curPercent;
                }
            }
        }

        int count = 0;
        for (int i = 0; i < data.length; ++i)
            if (data[i] != null)
                ++count;

        DataBlock[] tempData = new DataBlock[count];
        for (int i = 0, j = 0; i < data.length; ++i)
            if (data[i] != null)
                tempData[j++] = data[i];

        data = tempData;
        tempData = null;

        Arrays.sort(data, COMPARATOR);
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Finished summarizing neighbors!");
    }

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("HH_mm_ss__dd_MM_yyyy");

    private void writeData() {
        try {

            ConsoleUtils.printInfo(KrypteiaCore.NAME, "Start writing analyzed data to output...!");
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(KrypteiaCore.INSTANCE.getDataFolder(), DATE_FORMAT.format(new Date()) + "_output.txt")));
            DataBlock block = null;
            for (int i = 0; i < data.length; ++i) {
                block = data[i];
                if (block == null)
                    continue;
                bWriter.append(block.toString());
                bWriter.newLine();
            }

            bWriter.close();
            ConsoleUtils.printInfo(KrypteiaCore.NAME, "Finished writing analyzed data to output!");
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't write result file!");
        }
    }
}
