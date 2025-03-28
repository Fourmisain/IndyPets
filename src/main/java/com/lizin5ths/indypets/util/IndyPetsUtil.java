package com.lizin5ths.indypets.util;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class IndyPetsUtil {
	public static boolean sneakInteract(TameableEntity tameable, ServerPlayerEntity player) {
		Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
		if (!config.sneakInteract)
			return false;

		if (config.interactItem != null) {
			// only interact when holding the chosen item
			Identifier itemId = Registries.ITEM.getId(player.getMainHandStack().getItem());
			if (!itemId.equals(config.interactItem))
				return false;
		}

		// don't interact with blocked pets
		if (config.interactBlocklist.isBlocked(EntityType.getId(tameable.getType())))
			return false;

		toggleIndependence(tameable);
		showPetStatus(player, tameable, true);
		return true; // block further interactions
	}

	public static boolean isActiveIndependent(Entity entity) {
		return isActive(entity) && isIndependent((TameableEntity) entity);
	}

	/** Whether the entity is affected by IndyPets */
	public static boolean isActive(Entity entity) {
		if (!(entity instanceof TameableEntity tameable))
			return false;

		if (!tameable.isTamed())
			return false;

		Config config = ServerConfig.getDefaultedPlayerConfig(tameable.getOwnerReference());
		return !config.blocklist.isBlocked(EntityType.getId(tameable.getType()));
	}

	/** Whether the player can change the independence of the entity */
	public static boolean canInteract(ServerPlayerEntity player, @Nullable Entity entity) {
		return isActive(entity) && ((TameableEntity) entity).isOwner(player);
	}

	public static void toggleIndependence(TameableEntity tameable) {
		((Independence) tameable).indypets$toggleIndependence();

		// immediately finish the Glare's WalkTowardsLookTargetTask
		Identifier id = Registries.ENTITY_TYPE.getId(tameable.getType());
		if (FabricLoader.getInstance().isModLoaded("friendsandfoes") && id.getNamespace().equals("friendsandfoes") && id.getPath().equals("glare")) {
			Brain<?> brain = tameable.getBrain();
			brain.forget(MemoryModuleType.WALK_TARGET);
			brain.forget(MemoryModuleType.LOOK_TARGET);
		}
	}

	public static void showPetStatus(ServerPlayerEntity player, TameableEntity tameable, boolean singlePet) {
		Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());

		if (!config.silentMode) {
			sendPetStatusMessage(player, tameable, singlePet);
		} else {
			if (isIndependent(tameable)) {
				player.getServerWorld().spawnParticles(player, ParticleTypes.ANGRY_VILLAGER, true, true,
					tameable.getX(), tameable.getBodyY(0.5), tameable.getZ(),
					7, 0.4, 0.4, 0.4, 0.3);
			} else {
				player.getServerWorld().spawnParticles(player, ParticleTypes.HAPPY_VILLAGER, true, true,
					tameable.getX(), tameable.getBodyY(0.5), tameable.getZ(),
					11, 0.5, 0.5, 0.5, 2);
			}
		}
	}

	public static void sendPetStatusMessage(ServerPlayerEntity player, TameableEntity tameable, boolean overlay) {
		MutableText text;

		if (ServerConfig.HAS_MOD_INSTALLED.contains(player.getUuid())) {
			// Send a translatable text
			String key = isIndependent(tameable) ? "text.indypets.independent" : "text.indypets.following";
			if (tameable.hasCustomName()) {
				key += "_named";
			}

			// This is a workaround for not being able to nest TranslatableText (the name especially)
			text = Text.translatable(key + "_prefix");
			text.append(tameable.getName());
			text.append(Text.translatable(key + "_suffix"));

			if (tameable.isSitting())
				text.append(Text.translatable("text.indypets.but_sits"));
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
			sb.append(isIndependent(tameable) ? " is independent" : " is following you");
			if (tameable.isSitting())
				sb.append(" (but sits)");

			text = Text.translatable(sb.toString());
		}

		player.sendMessage(text, overlay);
	}

	public static boolean isIndependent(TameableEntity tameable) {
		return ((Independence) tameable).indypets$isIndependent();
	}

	public static BlockPos getHomePos(TameableEntity tameable) {
		return ((Independence) tameable).indypets$getHomePos();
	}

	public static void setHome(TameableEntity tameable) {
		((Independence) tameable).indypets$setHome();
	}

	public static boolean shouldHeadHome(MobEntity mob) {
		if (!(mob instanceof TameableEntity tameable))
			return false;

		if (!isActiveIndependent(tameable))
			return false;

		// distance to home
		float d = (float) Math.sqrt(tameable.getBlockPos().getSquaredDistance(getHomePos(tameable)));

		Config config = ServerConfig.getDefaultedPlayerConfig(tameable.getOwnerReference());
		float start = config.homeRadius * config.innerHomePercentage;
		float end   = config.homeRadius;

		// probability to head home, starts at 0 and grows to 1 over the interval from start to end
		float p;
		if (d < start)     p = 0;
		else if (d >= end) p = 1;
		else               p = (d - start) / (end - start);

		return tameable.getRandom().nextFloat() < p;
	}

	@Nullable
	public static Vec3d findTowardsHome(PathAwareEntity mob) {
		return findTowardsHome(mob, false);
	}

	@Nullable
	public static Vec3d findTowardsHome(PathAwareEntity mob, boolean ignorePenality) {
		return findTowardsHome(mob, ignorePenality, 15, 7);
	}

	@Nullable
	public static Vec3d findTowardsHome(PathAwareEntity mob, boolean ignorePenality, int horizontalRange, int verticalRange) {
		// assert mob instanceof TameableEntity
		BlockPos homePos = getHomePos((TameableEntity) mob);

		if (ignorePenality)
			return NoPenaltyTargeting.findTo(mob, horizontalRange, verticalRange, Vec3d.ofBottomCenter(homePos), Math.PI / 2);

		return FuzzyTargeting.findTo(mob, horizontalRange, verticalRange, Vec3d.ofBottomCenter(homePos));
	}

	// cycle sit -> follow -> independent
	public static void cycleState(boolean wasSitting, TameableEntity tameable) {
		// sit -> follow (& stand up)
		if (wasSitting) {
			if (isIndependent(tameable))
				toggleIndependence(tameable);

			tameable.setSitting(false);
		} else {
			// follow -> independent (& keep standing)
			if (!isIndependent(tameable)) {
				toggleIndependence(tameable);
				tameable.setSitting(false);
			} else {
				// independent -> sit
				tameable.setSitting(true);
			}
		}
	}
}
