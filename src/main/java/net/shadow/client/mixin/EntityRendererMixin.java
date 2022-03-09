package net.shadow.client.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.misc.AntiCrash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), index = 2, argsOnly = true)
    Text real(Text text) {
        AntiCrash ac = AntiCrash.instance();
        if (ac.isEnabled() && ac.getCapNames().getValue()) {
            String t = text.getString();
            int maxlen = (int) Math.floor(ac.getNameMax().getValue());
            int len = t.length();
            if (len > maxlen) {
                t = t.substring(0, maxlen) + "§r...";
            }
            text = Text.of(t);
        }
        return text;
    }
}
