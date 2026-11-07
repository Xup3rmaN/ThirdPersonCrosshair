package com.bigbro.tpcrosshair.mixin;

import com.bigbro.tpcrosshair.HeadHiddenState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements HeadHiddenState {
	@Unique
	private boolean tpcrosshair$headHidden;

	@Override
	public boolean tpcrosshair$isHeadHidden() {
		return this.tpcrosshair$headHidden;
	}

	@Override
	public void tpcrosshair$setHeadHidden(boolean hidden) {
		this.tpcrosshair$headHidden = hidden;
	}
}
