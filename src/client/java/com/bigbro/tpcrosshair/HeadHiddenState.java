package com.bigbro.tpcrosshair;

/** Duck-интерфейс для AvatarRenderState: нужно ли скрыть голову у этой модели. */
public interface HeadHiddenState {
	boolean tpcrosshair$isHeadHidden();

	void tpcrosshair$setHeadHidden(boolean hidden);
}
