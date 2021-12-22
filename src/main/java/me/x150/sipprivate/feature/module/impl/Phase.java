/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.helper.event.events.PlayerNoClipQueryEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;

import java.util.Objects;

public class Phase extends Module {

    public Phase() {
        super("Phase", "Go through walls when flying (works best with creative)", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled() || SipoverPrivate.client.player == null || !SipoverPrivate.client.player.getAbilities().flying) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            Box p = SipoverPrivate.client.player.getBoundingBox(SipoverPrivate.client.player.getPose()).offset(0, 0.27, 0).expand(0.25);
            if (p.getYLength() < 2) {
                p = p.expand(0, 1, 0);
            }
            p = p.offset(SipoverPrivate.client.player.getPos());
            if (pe.getPacket() instanceof PlayerMoveC2SPacket && !Objects.requireNonNull(SipoverPrivate.client.world).isSpaceEmpty(SipoverPrivate.client.player, p)) {
                event.setCancelled(true);
            }
        });
        Events.registerEventHandler(EventType.NOCLIP_QUERY, event -> {
            if (!getNoClipState(((PlayerNoClipQueryEvent) event).getPlayer())) {
                return;
            }
            ((PlayerNoClipQueryEvent) event).setNoClipState(PlayerNoClipQueryEvent.NoClipState.ACTIVE);
        });
    }

    @Override public void tick() {
    }

    public boolean getNoClipState(PlayerEntity pe) {
        return this.isEnabled() && pe.getAbilities().flying;
    }

    @Override public void enable() {
        Objects.requireNonNull(SipoverPrivate.client.player).setPose(EntityPose.STANDING);
        SipoverPrivate.client.player.setOnGround(false);
        SipoverPrivate.client.player.fallDistance = 0;
        SipoverPrivate.client.player.setVelocity(0, 0, 0);
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return getNoClipState(SipoverPrivate.client.player) ? "Active" : null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (Objects.requireNonNull(SipoverPrivate.client.player).getAbilities().flying) {
            SipoverPrivate.client.player.setPose(EntityPose.STANDING);
            SipoverPrivate.client.player.setOnGround(false);
            SipoverPrivate.client.player.fallDistance = 0;
            //SipoverPrivate.client.player.setVelocity(0,0,0);
        }
    }

    @Override public void onHudRender() {

    }
}
