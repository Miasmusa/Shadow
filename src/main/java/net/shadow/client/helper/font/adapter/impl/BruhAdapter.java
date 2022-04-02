/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper.font.adapter.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.helper.font.adapter.FontAdapter;
import net.shadow.client.helper.font.renderer.FontRenderer;

public class BruhAdapter implements FontAdapter {
    final FontRenderer renderer;

    public BruhAdapter(FontRenderer fr) {
        this.renderer = fr;
    }

    public FontRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void drawString(MatrixStack matrices, String text, float x, float y, int color) {
        if ((color & 0xfc000000) == 0) {
            color |= 0xff000000;
        }
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        renderer.drawString(matrices, text, x, y, r, g, b, alpha);
    }

    @Override
    public void drawString(MatrixStack matrices, String text, double x, double y, int color) {
        if ((color & 0xfc000000) == 0) {
            color |= 0xff000000;
        }
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        renderer.drawString(matrices, text, (float) x, (float) y, r, g, b, alpha);
    }

    @Override
    public void drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float b, float a) {
        renderer.drawString(matrices, text, x, y, r, g, b, a);
    }

    @Override
    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        if ((color & 0xfc000000) == 0) {
            color |= 0xff000000;
        }
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        renderer.drawCenteredString(matrices, text, (float) x, (float) y, r, g, b, alpha);
    }

    @Override
    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, float r, float g, float b, float a) {
        renderer.drawCenteredString(matrices, text, (float) x, (float) y, r, g, b, a);
    }

    @Override
    public float getStringWidth(String text) {
        return renderer.getStringWidth(text);
    }

    @Override
    public float getFontHeight() {
        return renderer.getFontHeight();
    }

    @Override
    public float getFontHeight(String text) {
        return renderer.getFontHeight();
    }

    @Override
    public float getMarginHeight() {
        return getFontHeight();
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
        if ((color & 0xfc000000) == 0) {
            color |= 0xff000000;
        }
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        drawString(matrices, s, x, y, r, g, b, alpha, dropShadow);
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a, boolean dropShadow) {
        renderer.drawString(matrices, s, x, y, r, g, b, a);
    }

    @Override
    public String trimStringToWidth(String in, double width) {
        return renderer.trimStringToWidth(in, (float) width);
    }

    @Override
    public String trimStringToWidth(String in, double width, boolean reverse) {
        return trimStringToWidth(in, width);
    }

    public int getSize() {
        return renderer.getSize();
    }
}
