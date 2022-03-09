package net.shadow.client.feature.module.impl.movement;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.CoffeeClientMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Backtrack extends Module {
    final List<PositionEntry> entries = new ArrayList<>();
    boolean committed = false;

    public Backtrack() {
        super("Backtrack", "Allows you to redo your movement if you messed up", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled() || committed) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof PlayerMoveC2SPacket) {
                event.setCancelled(true);
            }
        });
    }

    boolean shouldBacktrack() {
        return InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT) && CoffeeClientMain.client.currentScreen == null;
    }

    void shouldCommit() {
        boolean a = !committed && InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), GLFW.GLFW_KEY_ENTER) && CoffeeClientMain.client.currentScreen == null;
        if (a) {
            committed = true;
        }
    }

    @Override
    public void tick() {

    }

    void moveTo(PositionEntry e) {
        CoffeeClientMain.client.player.updatePosition(e.pos.x, e.pos.y, e.pos.z);
        CoffeeClientMain.client.player.setPitch((float) e.pitch);
        CoffeeClientMain.client.player.setYaw((float) e.yaw);
        CoffeeClientMain.client.player.setVelocity(e.vel);
    }

    @Override
    public void enable() {
        Utils.Logging.message("To backtrack, use the left alt key");
        Utils.Logging.message("To do the movement, press enter");
        Utils.Logging.message("To cancel, disable the module");
    }

    @Override
    public void disable() {
        entries.clear();
        committed = false;
        CoffeeClientMain.client.player.setNoGravity(false);
    }

    @Override
    public void onFastTick() {

        if (shouldBacktrack() && !committed && !entries.isEmpty()) {
            entries.remove(entries.size() - 1);
            moveTo(entries.get(entries.size() - 1));
        }
        shouldCommit();

        if (!shouldBacktrack() && !committed) {
            entries.add(new PositionEntry(Utils.getInterpolatedEntityPosition(CoffeeClientMain.client.player), CoffeeClientMain.client.player.getVelocity(), CoffeeClientMain.client.player.getPitch(), CoffeeClientMain.client.player.getYaw()));
        } else if (committed) {
            CoffeeClientMain.client.player.setNoGravity(true);
            moveTo(entries.get(0));
            entries.remove(0);
            if (entries.isEmpty()) {
                setEnabled(false);
            }
        }
        super.onFastTick();
    }

    @Override
    public String getContext() {
        return entries.size() + "";
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        for (int i = Math.max(1, entries.size() - 30); i < entries.size(); i++) {
            Renderer.R3D.renderLine(entries.get(i - 1).pos(), entries.get(i).pos(), Color.RED, matrices);
        }
    }

    @Override
    public void onHudRender() {

    }

    record PositionEntry(Vec3d pos, Vec3d vel, double pitch, double yaw) {
    }
}