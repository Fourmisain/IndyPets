package com.lizin5ths.indypets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static boolean changeFollowing(PlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (IndyPetsConfig.getSelectiveFollowing() && tameable.isOwner(player)) {
			follower.setFollowing(!follower.isFollowing());

			if (!IndyPetsConfig.getSilentMode()) {
				String key = follower.isFollowing() ? "text.indypets.following" : "text.indypets.independent";
				Text text = new TranslatableText(key, tameable.getName().getString());
				player.sendSystemMessage(text, Util.NIL_UUID);
			}

			return true;
		}

		return false;
	}

	@Override
	public void onInitialize() {
		IndyPetsConfig.load();
	}
}