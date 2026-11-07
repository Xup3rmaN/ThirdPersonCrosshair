package com.bigbro.tpcrosshair.mixin;

import com.bigbro.tpcrosshair.HeadHiddenState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin extends HumanoidModel<AvatarRenderState> {
	private PlayerModelMixin(ModelPart root) {
		super(root);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
	private void tpcrosshair$hideHead(AvatarRenderState state, CallbackInfo ci) {
		if (((HeadHiddenState) state).tpcrosshair$isHeadHidden()) {
			this.head.visible = false;
			this.hat.visible = false;
		}
	}
}
