/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.helper.render.ClipStack;
import net.shadow.client.helper.render.Rectangle;
import net.shadow.client.helper.render.Renderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class EntryListWidgetMixin {
    @Shadow protected int left;

    @Shadow protected int top;

    @Shadow protected int right;

    @Shadow protected int bottom;

    @Shadow protected int height;

    @Shadow protected int width;

    @Redirect(method="render",at=@At(
            value="FIELD",
            target="Lnet/minecraft/client/gui/widget/EntryListWidget;renderHorizontalShadows:Z",
            opcode = Opcodes.GETFIELD
    ))
    boolean r(EntryListWidget<?> instance, MatrixStack stack) {
//        ClipStack.globalInstance.addWindow(stack,new Rectangle(left,top,width-right,height-bottom));
        return false;
    }
//    @Inject(method="render",at=@At("RETURN"))
//    void e(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        ClipStack.globalInstance.popWindow();
//    }
    @Redirect(method="render",at=@At(
            value="FIELD",
            target="Lnet/minecraft/client/gui/widget/EntryListWidget;renderBackground:Z",
            opcode = Opcodes.GETFIELD
    ))
    boolean r1(EntryListWidget<?> instance) {

        return false;
    }
}
