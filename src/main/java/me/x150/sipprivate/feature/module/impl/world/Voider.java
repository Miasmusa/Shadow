package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

public class Voider extends Module {
    DoubleSetting radius   = this.config.create(new DoubleSetting.Builder(100).precision(0).name("Radius").description("How much to erase on X and Z").min(20).max(500).get());
    DoubleSetting delay    = this.config.create(new DoubleSetting.Builder(30).precision(0).name("Delay").description("How much delay to use while erasing").min(0).max(1000).get());
    Thread        runner;
    AtomicBoolean cancel   = new AtomicBoolean(false);
    Vec3d         startPos = null;
    Vec3d         latest   = null;

    public Voider() {
        super("Voider", "Transforms a radius around you to void", ModuleType.WORLD);
    }

    @Override public void tick() {

    }

    void run() {
        for (double ox = -radius.getValue(); ox < radius.getValue(); ox += 4) {
            for (double oz = -radius.getValue(); oz < radius.getValue(); oz += 4) {
                if (cancel.get()) {
                    return;
                }
                Vec3d root = startPos.add(ox, 0, oz);
                BlockPos pp = new BlockPos(root);
                latest = Vec3d.of(pp);
                String chat = String.format("/fill %d %d %d %d %d %d minecraft:air", pp.getX() - 2, CoffeeClientMain.client.world.getBottomY(), pp.getZ() - 2, pp.getX() + 2, CoffeeClientMain.client.world.getTopY() - 1, pp.getZ() + 2);
                CoffeeClientMain.client.player.sendChatMessage(chat);
                Utils.sleep((long) (delay.getValue() + 0));
            }
        }
        setEnabled(false);
    }

    @Override public void enable() {
        startPos = CoffeeClientMain.client.player.getPos();
        cancel.set(false);
        runner = new Thread(this::run);
        runner.start();
    }

    @Override public void disable() {
        Notification.create(6000, "Voider", "Waiting for cleanup...");
        cancel.set(true);
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (latest != null) {
            Renderer.R3D.renderFilled(new Vec3d(latest.x - 2, CoffeeClientMain.client.world.getBottomY(), latest.z - 2), new Vec3d(5, 0.001, 5), Utils.getCurrentRGB(), matrices);
            Renderer.R3D.line(new Vec3d(latest.x + .5, CoffeeClientMain.client.world.getBottomY(), latest.z + .5), new Vec3d(latest.x + .5, CoffeeClientMain.client.world.getTopY(), latest.z + .5), Color.RED, matrices);
        }
    }

    @Override public void onHudRender() {

    }

    @Override public void onFastTick() {
        super.onFastTick();
    }
}