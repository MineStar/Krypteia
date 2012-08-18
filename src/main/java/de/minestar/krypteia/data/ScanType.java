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

package de.minestar.krypteia.data;

import java.util.HashMap;
import java.util.Map;

public enum ScanType {

    // @formatter:off
    MOB     ("mob"),
    BLOCK   ("block");
    // @formatter:on

    private final String typeName;

    private ScanType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    private static Map<String, ScanType> mapByName = new HashMap<String, ScanType>();

    static {
        for (ScanType type : values())
            mapByName.put(type.typeName, type);
    }

    public static ScanType getType(String name) {
        return mapByName.get(name.toLowerCase());
    }
}
