/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;
import net.shadow.client.mixin.WorldRendererAccessor;

import java.awt.*;
import java.util.SortedSet;

public class BlockHighlighting extends Module {
    public BlockHighlighting() {
        super("BlockHighlighting", "Renders better block breaking animations", ModuleType.RENDER);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, p -> {
            if (!this.isEnabled())
                return;
            PacketEvent event = (PacketEvent) p;
            if (event.getPacket() instanceof BlockUpdateS2CPacket packet) {
                BlockPos real = packet.getPos();
                Renderer.R3D.renderFadingBlock(Renderer.Util.modify(Utils.getCurrentRGB(), -1, -1, -1, 255), Renderer.Util.modify(Utils.getCurrentRGB(), -1, -1, -1, 100).darker(), Vec3d.of(real), new Vec3d(1, 1, 1), 1000);
            }
        });
    }

    @Override
    public void tick() {

    }

    public void renderEntry(MatrixStack stack, Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>> e) {
        SortedSet<BlockBreakingInfo> bbrs = e.getValue();
        if (bbrs == null || bbrs.isEmpty())
            return;

        long k = e.getLongKey();
        BlockPos kv = BlockPos.fromLong(k);
        int stage = bbrs.last().getStage() + 1;
        double stageProg = stage / 10d;
        BlockState bs = ShadowMain.client.world.getBlockState(kv);
        VoxelShape vs = bs.getOutlineShape(ShadowMain.client.world, kv);
        if (vs.isEmpty())
            return;
        Box bb = vs.getBoundingBox();
        double invProg = 1 - stageProg;
        double lenX = bb.getXLength();
        double lenY = bb.getYLength();
        double lenZ = bb.getZLength();
        bb = bb.shrink(bb.getXLength() * invProg, bb.getYLength() * invProg, bb.getZLength() * invProg);
        Vec3d start = new Vec3d(bb.minX, bb.minY, bb.minZ).add(Vec3d.of(kv)).add(lenX * invProg / 2d, lenY * invProg / 2d, lenZ * invProg / 2d);
        Vec3d len = new Vec3d(bb.getXLength(), bb.getYLength(), bb.getZLength());
        Color outline = new Color(50, 50, 50, 255);
        Color fill = new Color(20, 20, 20, 100);
        Renderer.R3D.renderEdged(stack, start, len, fill, outline);

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        for (Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>> sortedSetEntry : ((WorldRendererAccessor) ShadowMain.client.worldRenderer).getBlockBreakingProgressions().long2ObjectEntrySet()) {
            renderEntry(matrices, sortedSetEntry);
        }
    }

    @Override
    public void onHudRender() {

    }
}
