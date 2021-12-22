package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.util.Transitions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class Fullbright extends Module {

    double og;

    public Fullbright() {
        super("Fullbright", "Allows you to see in complete darkness", ModuleType.RENDER);
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        og = MathHelper.clamp(SipoverPrivate.client.options.gamma, 0, 1);
    }

    @Override public void disable() {
        SipoverPrivate.client.options.gamma = og;
    }

    @Override public void onFastTick() {
        SipoverPrivate.client.options.gamma = Transitions.transition(SipoverPrivate.client.options.gamma, 10, 300);
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}