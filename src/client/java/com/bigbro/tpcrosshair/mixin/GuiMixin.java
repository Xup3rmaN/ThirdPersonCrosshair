package com.bigbro.tpcrosshair.mixin;

import com.bigbro.tpcrosshair.TpCrosshairClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Показываем прицел в режиме от третьего лица сзади. */
@Mixin(Gui.class)
public class GuiMixin {
	@WrapOperation(
			method = "renderCrosshair",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z")
	)
	private boolean tpcrosshair$showCrosshair(CameraType cameraType, Operation<Boolean> original) {
		return original.call(cameraType) || TpCrosshairClient.isActive();
	}
}
