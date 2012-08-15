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
import de.minestar.krypteia.data.DataBlock;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class AnalyzeThread implements Runnable {

    private String worldName;
    private int radius;

    private DataBlock[] data;

    public AnalyzeThread(String worldName, int radius) {
        this.worldName = worldName;
        this.radius = radius;
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

    @Override
    public void run() {
        if (data == null)
            loadDataBlocks();
        if (data != null)
            analyzeData();
        else
            ConsoleUtils.printError(KrypteiaCore.NAME, "Can't analyze data because data is null!");

        Arrays.sort(data, COMPARATOR);

        writeData();
    }

    private void loadDataBlocks() {
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Fetch data from database");
        ArrayList<DataBlock> temp = KrypteiaCore.dbHandler.getDataBlocks(worldName);
        data = (DataBlock[]) temp.toArray(new DataBlock[temp.size()]);
        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Finished fetching data from database");
    }

    private final static NumberFormat FORMAT = DecimalFormat.getPercentInstance();
    private String curPercent = "";
    private String oldPercent = "";

    private void analyzeData() {
        int tempRadius = radius * radius;
        double percent = 0.0;
        DataBlock block = null;
        for (int i = 0; i < data.length; ++i) {
            block = data[i];
            for (int j = i + 1; j < data.length; ++j) {
                if (block.isInRange(data[j], tempRadius))
                    block.addNeighbor();
            }
            percent = (double) i / (double) data.length;
            curPercent = FORMAT.format(percent);
            if (!oldPercent.equals(curPercent)) {
                ConsoleUtils.printInfo(KrypteiaCore.NAME, "Analyze Thread Status = " + curPercent);
                oldPercent = curPercent;
            }
        }

        ConsoleUtils.printInfo(KrypteiaCore.NAME, "Finished Analyzing!");
    }

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("HH_mm_ss__dd_MM_yyyy");

    private void writeData() {
        try {

            ConsoleUtils.printInfo(KrypteiaCore.NAME, "Start writing analyzed data to output...!");
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(KrypteiaCore.INSTANCE.getDataFolder(), DATE_FORMAT.format(new Date()) + "_output.txt")));

            for (int i = 0; i < data.length; ++i) {
                bWriter.append(data[i].toString());
                bWriter.newLine();
            }

            bWriter.close();
            ConsoleUtils.printInfo(KrypteiaCore.NAME, "Finished writing analyzed data to output!");
        } catch (Exception e) {
            ConsoleUtils.printException(e, KrypteiaCore.NAME, "Can't write result file!");
        }
    }
}
