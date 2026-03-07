package com.lizin5ths.indypets.util;

import com.lizin5ths.indypets.IndyPets;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import suike.suikefoxfriend.api.IFoxTamed;

import java.util.UUID;

public class FoxFriendCompat {
	public static boolean IS_LOADED = FabricLoader.getInstance().isModLoaded("suikefoxfriend");

	public static boolean isIFoxTamed(Entity entity) {
		 if (!IS_LOADED)
			 return false;

		try {
			return entity instanceof IFoxTamed;
		} catch (NoClassDefFoundError e) {
			IndyPets.LOGGER.error("IndyPets' Fox Friend compat failed!", e);
			return false;
		}
	}

	public static boolean isTamed(Entity entity) {
		return ((IFoxTamed) entity).isTamed();
	}

	@Nullable
	public static UUID getOwner(Entity entity) {
		return ((IFoxTamed) entity).getOwnerUuid();
	}

	public static boolean isOwner(Entity entity, PlayerEntity player) {
		return ((IFoxTamed) entity).isOwner(player);
	}

	public static boolean isSitting(Entity entity) {
		return ((IFoxTamed) entity).isWaiting();
	}

	public static void setSitting(Entity entity, boolean sitting) {
		var fox = (IFoxTamed) entity;

		boolean sleeping = fox.isSleepingWaiting();

		fox.setWaiting(sitting); // note: this resets sleeping

		if (sitting) {
			// keep original sleeping state
			fox.setSleepingWaiting(sleeping);
		}
	}
}
