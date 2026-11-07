package com.bigbro.tpcrosshair.mixin;

import com.bigbro.tpcrosshair.HeadHiddenState;
import com.bigbro.tpcrosshair.TpCrosshairClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Помечаем голову локального игрока как скрытую только при рендере в мире,
 * чтобы превью в инвентаре оставалось обычным.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "extractEntity", at = @At("RETURN"))
	private void tpcrosshair$hideLocalPlayerHead(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> cir) {
		if (entity != Minecraft.getInstance().player || !TpCrosshairClient.isActive()) {
			return;
		}
		if (cir.getReturnValue() instanceof AvatarRenderState state) {
			((HeadHiddenState) state).tpcrosshair$setHeadHidden(true);
			// Шлем, тыква, черепа и прочее, что надето на голову
			state.headEquipment = ItemStack.EMPTY;
			state.headItem.clear();
			state.wornHeadType = null;
		}
	}
}
