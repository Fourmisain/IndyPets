package com.lizin5ths.indypets.util;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class IndyPetsUtil {
	public static boolean changeFollowing(ServerPlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (tameable.isOwner(player)) {
			follower.setFollowing(!follower.isFollowing());

			Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
			if (!config.silentMode) {
				sendPetStatusMessage(player, tameable, follower);
			} else {
				if (follower.isFollowing()) {
					player.getWorld().spawnParticles(player, ParticleTypes.HAPPY_VILLAGER, true,
						tameable.getX(), tameable.getBodyY(0.5), tameable.getZ(),
						11, 0.5, 0.5, 0.5, 2);
				} else {
					player.getWorld().spawnParticles(player, ParticleTypes.ANGRY_VILLAGER, true,
						tameable.getX(), tameable.getBodyY(0.5), tameable.getZ(),
						7, 0.4, 0.4, 0.4, 0.3);
				}
			}

			return true;
		}

		return false;
	}

	public static void sendPetStatusMessage(PlayerEntity player, TameableEntity tameable, Follower follower) {
		MutableText text;

		if (ServerConfig.HAS_MOD_INSTALLED.contains(player.getUuid())) {
			// Send a translatable text
			String key = follower.isFollowing() ? "text.indypets.following" : "text.indypets.independent";
			if (tameable.hasCustomName()) {
				key += "_named";
			}

			// This is a workaround for not being able to nest TranslatableText (the name especially)
			text = Text.translatable(key + "_prefix");
			text.append(tameable.getName());
			text.append(Text.translatable(key + "_suffix"));
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

			text = Text.translatable(sb.toString());
		}

		player.sendMessage(text, false);
	}

	public static boolean isIndependent(TameableEntity tameable) {
		Identifier id = EntityType.getId(tameable.getType());

		Config config = ServerConfig.getDefaultedPlayerConfig(tameable.getOwnerUuid());
		if (config.blocklist.isBlocked(id))
			return false;

		return !((Follower) tameable).isFollowing();
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
