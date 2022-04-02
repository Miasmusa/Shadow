/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.world.GameMode;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.exception.CommandException;

import java.util.Arrays;

public class Gamemode extends Command {

    public Gamemode() {
        super("Gamemode", "Switch gamemodes client side", "gamemode", "gm", "gmode");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(GameMode.values()).map(GameMode::getName).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        if (ShadowMain.client.interactionManager == null) {
            return;
        }
        validateArgumentsLength(args, 1);
        GameMode gm = GameMode.byName(args[0], null);
        if (gm == null) throw new CommandException("Invalid gamemode", "Specify a valid gamemode");
        ShadowMain.client.interactionManager.setGameMode(gm);
    }
}
