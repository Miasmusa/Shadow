/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.Rotations;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class InstaBow extends Module {

    DoubleSetting  it       = this.config.create(new DoubleSetting.Builder(40).precision(0).name("Iterations").description("How often to spoof velocity (more = bigger damage)").min(5).max(100).get());
    BooleanSetting autoFire = this.config.create(new BooleanSetting.Builder(false).name("Auto fire").description("Automatically fire the bow when its held and an entity is on the same Y").get());

    public InstaBow() {
        super("BowOneTap", "Exploits the velocity handler on the server to give your arrow near infinite velocity", ModuleType.EXPLOIT);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof PlayerActionC2SPacket packet && packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {
                Vec3d a = SipoverPrivate.client.player.getPos().subtract(0, 1e-10, 0);
                Vec3d b = SipoverPrivate.client.player.getPos().add(0, 1e-10, 0);
                SipoverPrivate.client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(SipoverPrivate.client.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                //                ModuleRegistry.getByClass(NoFall.class).enabled = false; // disable nofall modifying packets when we send these
                for (int i = 0; i < it.getValue(); i++) {
                    PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.PositionAndOnGround(a.x, a.y, a.z, true);
                    PlayerMoveC2SPacket p1 = new PlayerMoveC2SPacket.PositionAndOnGround(b.x, b.y, b.z, false);
                    SipoverPrivate.client.getNetworkHandler().sendPacket(p);
                    SipoverPrivate.client.getNetworkHandler().sendPacket(p1);
                }
                //                ModuleRegistry.getByClass(NoFall.class).enabled = true;
            }
        });
    }

    @Override public void tick() {
        if (!autoFire.getValue()) {
            return;
        }
        Vec3d ep = SipoverPrivate.client.player.getEyePos();
        Entity nearestApplicable = null;
        for (Entity entity : SipoverPrivate.client.world.getEntities()) {
            if (entity.getType() == EntityType.ENDERMAN) {
                continue;
            }
            if (!(entity instanceof LivingEntity ent) || !ent.isAttackable() || ent.isDead() || entity.equals(SipoverPrivate.client.player)) {
                continue;
            }
            Vec3d origin = entity.getPos();
            float h = entity.getHeight();
            Vec3d upper = origin.add(0, h, 0);
            Vec3d center = entity.getPos().add(0, h / 2f, 0);
            if (Utils.Math.isABObstructed(ep, center, SipoverPrivate.client.world, SipoverPrivate.client.player)) {
                continue;
            }
            if (ep.y < upper.y && ep.y > origin.y) { // entity's on our Y
                if (nearestApplicable == null || nearestApplicable.distanceTo(SipoverPrivate.client.player) > origin.distanceTo(SipoverPrivate.client.player.getPos())) {
                    nearestApplicable = entity;
                }
            }
        }
        if (nearestApplicable == null) {
            return;
        }
        if (SipoverPrivate.client.player.isUsingItem() && SipoverPrivate.client.player.getMainHandStack().getItem() == Items.BOW) {
            BowItem be = (BowItem) SipoverPrivate.client.player.getMainHandStack().getItem();
            int p = be.getMaxUseTime(null) - SipoverPrivate.client.player.getItemUseTimeLeft();
            if (BowItem.getPullProgress(p) > 0.1) {
                Rotations.lookAtV3(nearestApplicable.getPos().add(0, nearestApplicable.getHeight() / 2f, 0));
                SipoverPrivate.client.getNetworkHandler()
                        .sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(Rotations.getClientYaw(), Rotations.getClientPitch(), SipoverPrivate.client.player.isOnGround()));
                SipoverPrivate.client.interactionManager.stopUsingItem(SipoverPrivate.client.player);
            }
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
