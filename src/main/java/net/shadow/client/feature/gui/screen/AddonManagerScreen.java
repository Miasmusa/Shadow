/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.shadow.client.feature.addon.Addon;
import net.shadow.client.feature.addon.AddonManager;
import net.shadow.client.feature.gui.FastTickable;
import net.shadow.client.feature.gui.clickgui.ClickGUI;
import net.shadow.client.feature.gui.widget.RoundButton;
import net.shadow.client.helper.GameTexture;
import net.shadow.client.helper.Timer;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.font.adapter.FontAdapter;
import net.shadow.client.helper.render.ClipStack;
import net.shadow.client.helper.render.Rectangle;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.render.Scroller;
import org.lwjgl.opengl.GL40C;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddonManagerScreen extends ClientScreen implements FastTickable {
    Timer discoverTimer = new Timer();
    Scroller scroller = new Scroller(0);
    double WIDGET_WIDTH = 600;
    double WIDGET_HEIGHT = 300;
    List<AddonViewer> viewerList = new ArrayList<>();

    @Override
    public void onFastTick() {
        scroller.tick();
        for (AddonViewer addonViewer : viewerList) {
            addonViewer.onFastTick();
        }
        if (discoverTimer.hasExpired(5000)) {
            discoverTimer.reset();
            AddonManager.INSTANCE.discoverNewAddons();
            for (Addon loadedAddon : AddonManager.INSTANCE.getLoadedAddons()) {
                if (viewerList.stream().noneMatch(addonViewer -> addonViewer.addon == loadedAddon)) {
                    viewerList.add(new AddonViewer(loadedAddon, WIDGET_WIDTH - 10));
                }
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (new Rectangle(width / 2d - WIDGET_WIDTH / 2d, height / 2d - WIDGET_HEIGHT / 2d, width / 2d + WIDGET_WIDTH / 2d, height / 2d + WIDGET_HEIGHT / 2d).contains(mouseX, mouseY)) {
            double contentHeight = viewerList.stream().map(addonViewer -> addonViewer.getHeight() + 5).reduce(Double::sum).orElse(0d) + 5;
            double entitledScroll = Math.max(0, contentHeight - WIDGET_HEIGHT);
            scroller.setBounds(0, entitledScroll);
            scroller.scroll(amount);

        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    protected void init() {
        reInitViewers();
    }

    void reInitViewers() {
        viewerList.clear();
        for (Addon loadedAddon : AddonManager.INSTANCE.getLoadedAddons()) {
            viewerList.add(new AddonViewer(loadedAddon, WIDGET_WIDTH - 10));
        }
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20), width / 2d - WIDGET_WIDTH / 2d, height / 2d - WIDGET_HEIGHT / 2d, width / 2d + WIDGET_WIDTH / 2d, height / 2d + WIDGET_HEIGHT / 2d, 5, 20);
        ClipStack.globalInstance.addWindow(stack, new Rectangle(width / 2d - WIDGET_WIDTH / 2d, height / 2d - WIDGET_HEIGHT / 2d, width / 2d + WIDGET_WIDTH / 2d, height / 2d + WIDGET_HEIGHT / 2d));
        double yOffset = 0;
        double xRoot = width / 2d - WIDGET_WIDTH / 2d + 5;
        double yRoot = height / 2d - WIDGET_HEIGHT / 2d + 5;
        for (AddonViewer addonViewer : viewerList) {
            addonViewer.render(stack, xRoot, yRoot + yOffset + scroller.getScroll(), mouseX, mouseY);
            yOffset += addonViewer.getHeight() + 5;
        }
        ClipStack.globalInstance.popWindow();
        super.renderInternal(stack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AddonViewer addonViewer : new ArrayList<>(viewerList)) {
            addonViewer.clicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    class AddonViewer implements FastTickable {
        static final double iconDimensions = 64;
        static final double padding = 5;
        Addon addon;
        double width;
        double lastX, lastY;
        RoundButton disable, reload;

        public AddonViewer(Addon addon, double width) {
            this.addon = addon;
            this.width = width;
            disable = new RoundButton(RoundButton.STANDARD, 0, 0, 60, 20, addon.isEnabled() ? "Disable" : "Enable", () -> {
                if (addon.isEnabled()) AddonManager.INSTANCE.disableAddon(addon);
                else AddonManager.INSTANCE.enableAddon(addon);
                disable.setText(addon.isEnabled() ? "Disable" : "Enable");
                ClickGUI.reInit();
            });
            reload = new RoundButton(RoundButton.STANDARD, 0, 0, 60, 20, "Reload", () -> {
                AddonManager.INSTANCE.reload(addon);
                reInitViewers();
                ClickGUI.reInit();
            });
        }

        public void render(MatrixStack stack, double x, double y, int mouseX, int mouseY) {
            lastX = x;
            lastY = y;

            Color background = new Color(30, 30, 30);
            Renderer.R2D.renderRoundedQuad(stack, background, x, y, x + width, y + getHeight(), 5, 20);
            RenderSystem.enableBlend();
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Renderer.R2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), background.getRed() / 255f, background.getGreen() / 255f, background.getBlue() / 255f, 1, x + padding, y + padding, x + padding + iconDimensions, y + padding + iconDimensions, 6, 10);

            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            Identifier icon = addon.getIcon();
            if (icon == null) icon = GameTexture.ICONS_ADDON_PROVIDED.getWhere();
            RenderSystem.setShaderTexture(0, addon.getIcon());
            if (!addon.isEnabled()) RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, 1f);
            Renderer.R2D.renderTexture(stack, x + padding, y + padding, iconDimensions, iconDimensions, 0, 0, iconDimensions, iconDimensions, iconDimensions, iconDimensions);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            FontAdapter title = FontRenderers.getCustomSize(30);
            FontAdapter normal = FontRenderers.getRenderer();
            double entireHeight = title.getFontHeight() + normal.getFontHeight() * 2d;
            if (addon.isEnabled())
                title.drawString(stack, addon.name, (float) (x + padding + iconDimensions + padding), (float) (y + getHeight() / 2d - entireHeight / 2d), 0xFFFFFF);
            else
                title.drawString(stack, addon.name, (float) (x + padding + iconDimensions + padding), (float) (y + getHeight() / 2d - entireHeight / 2d), 0.6f, 0.6f, 0.6f, 1f);
            normal.drawString(stack, addon.description, (float) (x + padding + iconDimensions + padding), (float) (y + getHeight() / 2d - entireHeight / 2d + title.getFontHeight()), 0.6f, 0.6f, 0.6f, 1f);
            normal.drawString(stack, "Developer(s): " + String.join(", ", addon.developers), (float) (x + padding + iconDimensions + padding), (float) (y + getHeight() / 2d - entireHeight / 2d + title.getFontHeight() + normal.getFontHeight()), 0.6f, 0.6f, 0.6f, 1f);

            double buttonRowHeight = disable.getHeight() + padding + reload.getHeight();

            disable.setX(x + width - disable.getWidth() - padding);
            disable.setY(y + getHeight() / 2d - buttonRowHeight / 2d);
            disable.render(stack, mouseX, mouseY, 0);
            reload.setX(x + width - disable.getWidth() - padding);
            reload.setY(y + getHeight() / 2d - buttonRowHeight / 2d + disable.getHeight() + padding);
            reload.render(stack, mouseX, mouseY, 0);
        }

        public void clicked(double mouseX, double mouseY, int button) {
            disable.mouseClicked(mouseX, mouseY, button);
            reload.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void onFastTick() {
            disable.onFastTick();
            reload.onFastTick();
        }

        public double getHeight() {
            return iconDimensions + padding * 2;
        }
    }
}
