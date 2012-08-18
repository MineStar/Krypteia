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
import de.minestar.krypteia.data.ScanType;
import de.minestar.krypteia.data.ScannedData;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class AnalyzeThread implements Runnable {

    private String worldName;
    private int radius;
    private ScanType type;

    private ScannedData[] data;

    public AnalyzeThread(String worldName, int radius, ScanType type) {
        this.worldName = worldName;
        this.radius = radius;
        this.type = type;
    }

    @Override
    public void run() {
        if (data == null)
            loadData();
        if (data != null)
            calculateNeighbors();
        else
            ConsoleUtils.printError(KrypteiaCore.NAME, "Can't analyze data because data is null!");

        sortData();

        summarizeData();

        writeData();
    }

    private void loadData() {
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Fetch data from database...");
        ArrayList<ScannedData> temp = null;
        switch (type) {
            case BLOCK :
                temp = KrypteiaCore.dbHandler.getScannedDataBlock(worldName);
                break;
            case MOB :
                temp = KrypteiaCore.dbHandler.getScannedDataMobs(worldName);
                break;
        }
        data = (ScannedData[]) temp.toArray(new ScannedData[temp.size()]);
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Finished fetching data from database!");
    }

    private final static NumberFormat FORMAT = DecimalFormat.getPercentInstance();
    private String curPercent = "";
    private String oldPercent = "";

    private void calculateNeighbors() {
        int tempRadius = radius * radius;
        double percent = 0.0;
        ScannedData block = null;

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

    private final static Comparator<ScannedData> COMPARATOR = new Comparator<ScannedData>() {

        @Override
        public int compare(ScannedData o1, ScannedData o2) {
            if (o1.getNeighbors() == o2.getNeighbors()) {
                if (o1.getID() == o2.getID()) {
                    if (o1.getX() == o2.getX()) {
                        if (o1.getY() == o2.getY()) {
                            return o1.getZ() - o2.getZ();
                        } else
                            return o1.getY() - o2.getY();
                    } else
                        return o1.getX() - o2.getX();
                } else
                    return o1.getID() - o2.getID();
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
        ScannedData block;
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

        ScannedData[] tempData = new ScannedData[count];
        for (int i = 0, j = 0; i < data.length; ++i)
            if (data[i] != null)
                tempData[j++] = data[i];

        data = tempData;
        tempData = null;

        Arrays.sort(data, COMPARATOR);
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread: Finished summarizing neighbors!");
    }

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("HH_mm__dd_MM_yyyy");

    private void writeData() {
        try {

            ConsoleUtils.printInfo(KrypteiaCore.NAME, "Start writing analyzed data to output...!");
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(KrypteiaCore.INSTANCE.getDataFolder(), this.worldName + "_" + type.getTypeName() + "_" + DATE_FORMAT.format(new Date()) + "_output.txt")));
            ScannedData block = null;
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
