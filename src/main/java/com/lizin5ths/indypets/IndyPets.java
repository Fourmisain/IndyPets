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

	private static final Text INDEPENDENT_TEXT = new TranslatableText("text.indypets.independent");
	private static final Text FOLLOWING_TEXT = new TranslatableText("text.indypets.following");

	public static boolean changeFollowing(PlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (IndyPetsConfig.getSelectiveFollowing() && tameable.isOwner(player) && player.isSneaking()) {
			if (!IndyPetsConfig.getSilentMode()) {
				player.sendSystemMessage(follower.isFollowing() ? INDEPENDENT_TEXT : FOLLOWING_TEXT, Util.NIL_UUID);
			}

			follower.setFollowing(!follower.isFollowing());
			return true;
		}

		return false;
	}

	@Override
	public void onInitialize() {
		IndyPetsConfig.load();
	}
}