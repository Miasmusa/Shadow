/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.coloring.ArgumentType;
import net.shadow.client.feature.command.coloring.StaticArgumentServer;
import net.shadow.client.feature.command.exception.CommandException;

import java.util.Random;

public class FloodLuckperms extends Command {
    public FloodLuckperms() {
        super("FloodLuckperms", "Spam luckperms groups", "floodLp", "lpFlood", "floodLuckperms", "luckpermsFlood");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(amount)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {
        return StaticArgumentServer.serveFromStatic(lookingAtArgIndex, ArgumentType.NUMBER);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1, "Provide amount");
        int pp = new IntegerArgumentParser().parse(args[0]);
        Random r = new Random();
        for (int i = 0; i < pp; i++) {
            ShadowMain.client.player.sendChatMessage("/lp creategroup " + i + r.nextInt(100));
        }
    }
}
