/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.coloring;

public class StaticArgumentServer {
    public static PossibleArgument serveFromStatic(int index, PossibleArgument... types) {
        if (index >= types.length)
            return new PossibleArgument(null);
        return types[index];
    }
}
