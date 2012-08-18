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

package de.minestar.krypteia.command;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.AbstractSuperCommand;

public class cmdKrypteia extends AbstractSuperCommand {

    public cmdKrypteia(String pluginName, String syntax, String arguments, String node, AbstractCommand... subCommands) {
        super(pluginName, syntax, arguments, node, subCommands);
    }

    @Override
    public void execute(String[] args, Player player) {
        // DO NOTHING - IS SUPER COMMAND
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        // DO NOTHING - IS SUPER COMMAND
    }

}
