package com.bigbro.tpcrosshair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TpCrosshairConfig {
	public static final int MIN_FOV = 10;
	public static final int MAX_FOV = 110;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("tpcrosshair.json");

	private static TpCrosshairConfig instance = new TpCrosshairConfig();

	/** FOV (в градусах), к которому камера приближается, пока зажата кнопка. */
	public int zoomFov = 50;

	/** Плавное приближение (true) или мгновенное (false). */
	public boolean smoothZoom = true;

	public static TpCrosshairConfig get() {
		return instance;
	}

	public static void load() {
		if (Files.exists(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH)) {
				TpCrosshairConfig loaded = GSON.fromJson(reader, TpCrosshairConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			} catch (Exception e) {
				TpCrosshairClient.LOGGER.warn("Failed to read {}, using defaults", PATH, e);
			}
		}
		instance.zoomFov = Math.clamp(instance.zoomFov, MIN_FOV, MAX_FOV);
		save();
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(PATH)) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			TpCrosshairClient.LOGGER.warn("Failed to write {}", PATH, e);
		}
	}
}
