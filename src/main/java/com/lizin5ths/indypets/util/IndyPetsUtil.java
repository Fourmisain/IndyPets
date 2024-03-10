package com.lizin5ths.indypets.util;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class IndyPetsUtil {

	public static boolean sneakInteract(Entity entity, PlayerEntity player, Hand hand) {
		if (!(player.isSneaking() && hand == Hand.MAIN_HAND && entity instanceof TameableEntity))
			return false;

		Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
		if (config.interactItem != null) {
			// only interact when holding the chosen item
			Identifier itemId = Registry.ITEM.getId(player.getMainHandStack().getItem());
			if (!itemId.equals(config.interactItem))
				return false;
		}

		// don't interact with blocked pets
		if (config.interactBlocklist.isBlocked(EntityType.getId(entity.getType())))
			return false;

		return IndyPetsUtil.changeFollowing((ServerPlayerEntity) player, (TameableEntity) entity);
	}

	public static boolean changeFollowing(ServerPlayerEntity player, TameableEntity tameable) {
		if (!tameable.isOwner(player))
			return false;

		Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());

		// don't change blocked pets
		if (config.blocklist.isBlocked(EntityType.getId(tameable.getType())))
			return false;

		Follower follower = (Follower) tameable;
		follower.setFollowing(!follower.isFollowing());

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

	public static boolean shouldHeadHome(PathAwareEntity mob) {
		if (!(mob instanceof TameableEntity))
			return false;

		TameableEntity tameable = (TameableEntity) mob;
		Follower follower = (Follower) mob;

		if (!tameable.isTamed() || follower.isFollowing())
			return false;

		// distance to home
		float d = (float) Math.sqrt(tameable.getBlockPos().getSquaredDistance(follower.getHomePos()));

		Config config = ServerConfig.getDefaultedPlayerConfig(tameable.getOwnerUuid());
		float start = config.homeRadius * config.innerHomePercentage;
		float end   = config.homeRadius;

		// probability to head home, starts at 0 and grows to 1 over the interval from start to end
		float p;
		if (d < start)     p = 0;
		else if (d >= end) p = 1;
		else               p = (d - start) / (end - start);

		return tameable.getRandom().nextFloat() < p;
	}

	public static Vec3d findTowardsHome(PathAwareEntity mob) {
		return findTowardsHome(mob, false);
	}

	public static Vec3d findTowardsHome(PathAwareEntity mob, boolean ignorePenality) {
		// assert mob instanceof TameableEntity
		BlockPos homePos = ((Follower) mob).getHomePos();

		if (ignorePenality)
			return NoPenaltyTargeting.findTo(mob, 15, 7, Vec3d.ofBottomCenter(homePos), Math.PI / 2);

		return FuzzyTargeting.findTo(mob, 15, 7, Vec3d.ofBottomCenter(homePos));
	}
}
