/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.argument;

import net.minecraft.entity.player.PlayerEntity;
import net.shadow.client.feature.command.exception.CommandException;

public class StreamlineArgumentParser {
    String[] args;
    int index = 0;
    public StreamlineArgumentParser(String[] args) {
        this.args = args;
    }
    public String consumeString() throws CommandException {
        if (index >= args.length) throw new CommandException("Not enough arguments", null);
        String el = args[index];
        index++;
        return el;
    }
    public int consumeInt() throws CommandException {
        return new IntegerArgumentParser().parse(consumeString());
    }
    public double consumeDouble() throws CommandException {
        return new DoubleArgumentParser().parse(consumeString());
    }
    public PlayerEntity consumePlayerEntityFromName(boolean ignoreCase) throws CommandException {
        return new PlayerFromNameArgumentParser(ignoreCase).parse(consumeString());
    }
    public PlayerEntity consumePlayerEntityFromUuid() throws CommandException {
        return new PlayerFromUuidArgumentParser().parse(consumeString());
    }
}
