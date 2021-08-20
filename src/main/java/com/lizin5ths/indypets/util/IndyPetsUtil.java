package com.lizin5ths.indypets.util;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import java.util.UUID;

public class IndyPetsUtil {
	public static boolean changeFollowing(ServerPlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (tameable.isOwner(player)) {
			follower.setFollowing(!follower.isFollowing());

			Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
			if (!config.silentMode) {
				sendPetStatusMessage(player, tameable, follower);
			}

			return true;
		}

		return false;
	}

	public static void sendPetStatusMessage(PlayerEntity player, TameableEntity tameable, Follower follower) {
		BaseText text;

		if (ServerConfig.HAS_MOD_INSTALLED.contains(player.getUuid())) {
			// Send a translatable text
			String key = follower.isFollowing() ? "text.indypets.following" : "text.indypets.independent";
			if (tameable.hasCustomName()) {
				key += "_named";
			}

			// This is a workaround for not being able to nest TranslatableText (the name especially)
			text = new TranslatableText(key + "_prefix");
			text.append(tameable.getName());
			text.append(new TranslatableText(key + "_suffix"));
		} else {
			// Default to sending an English message
			String name = tameable.getName().getString();

			StringBuilder sb = new StringBuilder();
			if (tameable.hasCustomName()) {
				sb.append('\"');
				sb.append(name);
				sb.append('\"');
			} else {
				sb.append("Your ");
				sb.append(name);
			}
			sb.append(follower.isFollowing() ? " is following you" : " is independent");

			text = new LiteralText(sb.toString());
		}

		player.sendSystemMessage(text, Util.NIL_UUID);
	}

	public static boolean isFollowing(Entity entity) {
		if (entity instanceof Follower) {
			Follower follower = (Follower) entity;
			return follower.isFollowing();
		}

		return false;
	}

	public static boolean isPetOf(Entity entity, PlayerEntity player) {
		if (entity instanceof TameableEntity) {
			TameableEntity tameable = (TameableEntity) entity;
			UUID owner = tameable.getOwnerUuid();
			return owner != null && owner.equals(player.getUuid());
		}

		return false;
	}
}
