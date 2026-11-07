package com.bigbro.tpcrosshair;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TpCrosshairClient implements ClientModInitializer {
	public static final String MOD_ID = "tpcrosshair";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"));
	private static KeyMapping holdKey;

	/** Зажата ли кнопка (режим активен). */
	private static boolean held;
	/** Прогресс приближения 0..1: текущий и предыдущего тика, для плавности между кадрами. */
	private static float zoomProgress;
	private static float oldZoomProgress;

	@Override
	public void onInitializeClient() {
		TpCrosshairConfig.load();

		holdKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.tpcrosshair.hold",
				InputConstants.Type.MOUSE,
				GLFW.GLFW_MOUSE_BUTTON_5,
				CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(TpCrosshairClient::tick);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
				ClientCommandManager.literal(MOD_ID)
						.then(ClientCommandManager.literal("fov")
								.executes(ctx -> {
									ctx.getSource().sendFeedback(Component.translatable("tpcrosshair.fov.current", TpCrosshairConfig.get().zoomFov));
									return 1;
								})
								.then(ClientCommandManager.argument("value", IntegerArgumentType.integer(TpCrosshairConfig.MIN_FOV, TpCrosshairConfig.MAX_FOV))
										.executes(ctx -> {
											int value = IntegerArgumentType.getInteger(ctx, "value");
											TpCrosshairConfig.get().zoomFov = value;
											TpCrosshairConfig.save();
											ctx.getSource().sendFeedback(Component.translatable("tpcrosshair.fov.set", value));
											return 1;
										})))
						.then(ClientCommandManager.literal("smooth")
								.executes(ctx -> {
									ctx.getSource().sendFeedback(smoothFeedback());
									return 1;
								})
								.then(ClientCommandManager.literal("on").executes(ctx -> setSmooth(ctx.getSource(), true)))
								.then(ClientCommandManager.literal("off").executes(ctx -> setSmooth(ctx.getSource(), false))))
		));
	}

	private static int setSmooth(FabricClientCommandSource source, boolean smooth) {
		TpCrosshairConfig.get().smoothZoom = smooth;
		TpCrosshairConfig.save();
		source.sendFeedback(smoothFeedback());
		return 1;
	}

	private static Component smoothFeedback() {
		return Component.translatable(TpCrosshairConfig.get().smoothZoom ? "tpcrosshair.smooth.on" : "tpcrosshair.smooth.off");
	}

	private static void tick(Minecraft client) {
		// Клики нам не нужны, только состояние «зажата / отпущена»
		while (holdKey.consumeClick()) {
		}

		boolean down = holdKey.isDown() && client.player != null;
		if (down != held) {
			held = down;
			setCameraType(client, down ? CameraType.THIRD_PERSON_BACK : CameraType.FIRST_PERSON);
		}

		float target = held ? 1.0F : 0.0F;
		if (!TpCrosshairConfig.get().smoothZoom) {
			oldZoomProgress = target;
			zoomProgress = target;
			return;
		}

		oldZoomProgress = zoomProgress;
		zoomProgress += (target - zoomProgress) * 0.5F;
		if (Math.abs(target - zoomProgress) < 0.001F) {
			zoomProgress = target;
		}
	}

	private static void setCameraType(Minecraft client, CameraType next) {
		CameraType previous = client.options.getCameraType();
		if (previous == next) {
			return;
		}
		client.options.setCameraType(next);
		if (previous.isFirstPerson() != next.isFirstPerson()) {
			client.gameRenderer.checkEntityPostEffect(next.isFirstPerson() ? client.getCameraEntity() : null);
		}
	}

	/** Режим активен, когда камера стоит сзади за спиной локального игрока. */
	public static boolean isActive() {
		Minecraft client = Minecraft.getInstance();
		return client.options.getCameraType() == CameraType.THIRD_PERSON_BACK
				&& client.player != null
				&& client.getCameraEntity() == client.player;
	}

	/** Множитель FOV с учётом плавного приближения. */
	public static float getFovMultiplier(float partialTick) {
		float progress = Mth.lerp(partialTick, oldZoomProgress, zoomProgress);
		if (progress <= 0.0F) {
			return 1.0F;
		}
		float baseFov = Minecraft.getInstance().options.fov().get();
		return Mth.lerp(progress, 1.0F, TpCrosshairConfig.get().zoomFov / baseFov);
	}
}
