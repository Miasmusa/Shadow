/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module;

public abstract class AddonModule extends Module {
    public AddonModule(String n, String d) {
        super(n, d, ModuleType.ADDON_PROVIDED);
    }
}
