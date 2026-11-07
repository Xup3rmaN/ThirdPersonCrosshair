package com.bigbro.tpcrosshair.mixin;

import com.bigbro.tpcrosshair.TpCrosshairClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Приближение FOV, пока зажата кнопка. */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyReturnValue(method = "getFov", at = @At("RETURN"))
	private float tpcrosshair$zoom(float fov, Camera camera, float partialTick, boolean useFovSetting) {
		if (!useFovSetting) {
			return fov;
		}
		return fov * TpCrosshairClient.getFovMultiplier(partialTick);
	}
}
