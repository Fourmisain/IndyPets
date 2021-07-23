package com.lizin5ths.indypets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class IndyPets implements ModInitializer {

	public static boolean changeFollowing(PlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (IndyPetsConfig.getSelectiveFollowing() && tameable.isOwner(player)) {
			follower.setFollowing(!follower.isFollowing());

			if (!IndyPetsConfig.getSilentMode()) {
				String message = follower.isFollowing()
					? "(IndyPets) Follow+teleport now allowed for target."
					: "(IndyPets) Follow+teleport now forbidden for target.";
				Text text = new LiteralText(message);
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