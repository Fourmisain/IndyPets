package com.lizin5ths.indypets.util;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// note: most methods are unguarded and should only be run after a isSupported(), isActive()/isActiveIndependent(), or canInteract() check
// canInteract() implies isActive() implies isSupported().
public class IndyPetsUtil {
	// to add support: update all methods from isTamed() to setHome()
	public static boolean isSupported(Entity entity) {
		return entity instanceof TameableEntity || FoxFriendCompat.isIFoxTamed(entity);
	}

	// guard with isSupported()
	public static boolean isTamed(Entity entity) {
		if (entity instanceof TameableEntity tameable) {
			return tameable.isTamed();
		}

		if (FoxFriendCompat.isIFoxTamed(entity)) {
			return FoxFriendCompat.isTamed(entity);
		}

		throw new AssertionError("entity %s does not implement isTamed".formatted(entity));
	}

	// guard with isSupported()
	@Nullable
	public static UUID getOwner(Entity entity) {
		if (entity instanceof TameableEntity tameable) {
			var owner = tameable.getOwnerReference();
			return owner != null ? owner.getUuid() : null;
		}

		if (FoxFriendCompat.isIFoxTamed(entity)) {
			return FoxFriendCompat.getOwner(entity);
		}

		throw new AssertionError("entity %s does not implement getOwner".formatted(entity));
	}

	// guard with isSupported()
	public static boolean isOwner(Entity entity, ServerPlayerEntity player) {
		if (entity instanceof TameableEntity tameable) {
			return tameable.isOwner(player);
		}

		if (FoxFriendCompat.isIFoxTamed(entity)) {
			return FoxFriendCompat.isOwner(entity, player);
		}

		throw new AssertionError("entity %s does not implement isOwner".formatted(entity));
	}

	// guard with isSupported()
	public static boolean isSitting(Entity entity) {
		if (entity instanceof TameableEntity tameable) {
			return tameable.isSitting();
		}

		if (FoxFriendCompat.isIFoxTamed(entity)) {
			return FoxFriendCompat.isSitting(entity);
		}

		throw new AssertionError("entity %s does not implement isSitting".formatted(entity));
	}

	// guard with isSupported()
	public static void setSitting(Entity entity, boolean sitting) {
		if (entity instanceof TameableEntity tameable) {
			tameable.setSitting(sitting);
		} else if (FoxFriendCompat.isIFoxTamed(entity)) {
			FoxFriendCompat.setSitting(entity, sitting);
		} else {
			throw new AssertionError("entity %s does not implement setSitting".formatted(entity));
		}
	}

	// guard with isSupported()
	public static boolean isIndependent(Entity entity) {
		if (entity instanceof IndependentPet indy)
			return indy.indypets$isIndependent();

		throw new AssertionError("entity %s does not implement IndependentPet".formatted(entity));
	}

	// guard with isSupported()
	public static BlockPos getHomePos(Entity entity) {
		if (entity instanceof IndependentPet indy)
			return indy.indypets$getHomePos();

		throw new AssertionError("entity %s does not implement IndependentPet".formatted(entity));
	}

	// guard with isSupported()
	public static void setHome(Entity entity) {
		if (entity instanceof IndependentPet indy) {
			indy.indypets$setHome();
		} else {
			throw new AssertionError("entity %s does not implement IndependentPet".formatted(entity));
		}
	}

	/** Whether the entity is affected by IndyPets */
	public static boolean isActive(Entity entity) {
		if (!isSupported(entity))
			return false;

		if (!isTamed(entity))
			return false;

		var config = ServerConfig.getDefaultedPlayerConfig(getOwner(entity));
		return !config.blocklist.isBlocked(EntityType.getId(entity.getType()));
	}

	public static boolean isActiveIndependent(Entity entity) {
		return isActive(entity) && isIndependent(entity);
	}

	/** Whether the player can change the independence of the entity */
	public static boolean canInteract(ServerPlayerEntity player, @Nullable Entity entity) {
		return isActive(entity) && isOwner(entity, player);
	}

	// guard by canInteract()
	public static boolean sneakInteract(Entity entity, ServerPlayerEntity player) {
		var config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
		if (!config.sneakInteract)
			return false;

		if (config.interactItem != null) {
			// only interact when holding the chosen item
			var itemId = Registries.ITEM.getId(player.getMainHandStack().getItem());
			if (!itemId.equals(config.interactItem))
				return false;
		}

		// don't interact with blocked pets
		if (config.interactBlocklist.isBlocked(EntityType.getId(entity.getType())))
			return false;

		toggleIndependence(entity);
		showPetStatus(player, entity, true);
		return true; // block further interactions
	}

	// guard with isSupported()
	public static void toggleIndependence(Entity entity) {
		toggleIndependence(((IndependentPet) entity));

		// immediately finish the Glare's WalkTowardsLookTargetTask
		var id = Registries.ENTITY_TYPE.getId(entity.getType());
		if (FabricLoader.getInstance().isModLoaded("friendsandfoes") && id.getNamespace().equals("friendsandfoes") && id.getPath().equals("glare")) {
			var brain = ((LivingEntity) entity).getBrain();
			brain.forget(MemoryModuleType.WALK_TARGET);
			brain.forget(MemoryModuleType.LOOK_TARGET);
		}
	}

	// shouldn't be used directly, use toggleIndependence(Entity) instead
	public static void toggleIndependence(IndependentPet indy) {
		indy.indypets$setIndependent(!indy.indypets$isIndependent());
		if (indy.indypets$isIndependent()) {
			indy.indypets$setHome();
		}
	}

	// guard with isSupported()
	public static void resetFollowData(Entity entity) {
		IndyPets.LOGGER.debug("resetFollowData()");
		var indy = (IndependentPet) entity;
		indy.indypets$setIndependent(false);
		indy.indypets$setHome();
	}

	// guard with canInteract()
	public static void showPetStatus(ServerPlayerEntity player, Entity entity, boolean singlePet) {
		var config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());

		if (!config.silentMode) {
			sendPetStatusMessage(player, entity, singlePet);
		} else {
			if (isIndependent(entity)) {
				player.getEntityWorld().spawnParticles(player, ParticleTypes.ANGRY_VILLAGER, true, true,
					entity.getX(), entity.getBodyY(0.5), entity.getZ(),
					7, 0.4, 0.4, 0.4, 0.3);
			} else {
				player.getEntityWorld().spawnParticles(player, ParticleTypes.HAPPY_VILLAGER, true, true,
					entity.getX(), entity.getBodyY(0.5), entity.getZ(),
					11, 0.5, 0.5, 0.5, 2);
			}
		}
	}

	// guard with canInteract()
	public static void sendPetStatusMessage(ServerPlayerEntity player, Entity entity, boolean overlay) {
		MutableText text;

		if (ServerConfig.HAS_MOD_INSTALLED.contains(player.getUuid())) {
			// Send a translatable text
			String key = isIndependent(entity) ? "text.indypets.independent" : "text.indypets.following";
			if (entity.hasCustomName()) {
				key += "_named";
			}

			// This is a workaround for not being able to nest TranslatableText (the name especially)
			text = Text.translatable(key + "_prefix");
			text.append(entity.getName());
			text.append(Text.translatable(key + "_suffix"));

			if (isSitting(entity))
				text.append(Text.translatable("text.indypets.but_sits"));
		} else {
			// Default to sending an English message
			String name = entity.getName().getString();

			StringBuilder sb = new StringBuilder();
			if (entity.hasCustomName()) {
				sb.append('\"');
				sb.append(name);
				sb.append('\"');
			} else {
				sb.append("Your ");
				sb.append(name);
			}
			sb.append(isIndependent(entity) ? " is independent" : " is following you");
			if (isSitting(entity))
				sb.append(" (but sits)");

			text = Text.translatable(sb.toString());
		}

		player.sendMessage(text, overlay);
	}

	// guarded, safe to use
	public static boolean shouldHeadHome(MobEntity entity) {
		if (!isActiveIndependent(entity))
			return false;

		// vanilla homes have priority over IndyPets homes
		if (entity instanceof TameableEntity tameable && tameable.hasPositionTarget())
			return false;

		// distance to home
		float d = (float) Math.sqrt(entity.getBlockPos().getSquaredDistance(getHomePos(entity)));

		Config config = ServerConfig.getDefaultedPlayerConfig(getOwner(entity));
		float start = config.homeRadius * config.innerHomePercentage;
		float end   = config.homeRadius;

		// probability to head home, starts at 0 and grows to 1 over the interval from start to end
		float p;
		if (d < start)     p = 0;
		else if (d >= end) p = 1;
		else               p = (d - start) / (end - start);

		return entity.getRandom().nextFloat() < p;
	}

	// guard with shouldHeadHome()
	@Nullable
	public static Vec3d findTowardsHome(PathAwareEntity mob) {
		return findTowardsHome(mob, false);
	}

	// guard with shouldHeadHome()
	@Nullable
	public static Vec3d findTowardsHome(PathAwareEntity mob, boolean ignorePenality) {
		return findTowardsHome(mob, ignorePenality, 15, 7);
	}

	// guard with shouldHeadHome()
	@Nullable
	public static Vec3d findTowardsHome(PathAwareEntity mob, boolean ignorePenality, int horizontalRange, int verticalRange) {
		BlockPos homePos = getHomePos(mob);

		if (ignorePenality)
			return NoPenaltyTargeting.findTo(mob, horizontalRange, verticalRange, Vec3d.ofBottomCenter(homePos), Math.PI / 2);

		return FuzzyTargeting.findTo(mob, horizontalRange, verticalRange, Vec3d.ofBottomCenter(homePos));
	}

	// guard with canInteract()
	// cycle sit -> follow -> independent
	public static void cycleState(boolean wasSitting, Entity entity) {
		// sit -> follow (& stand up)
		if (wasSitting) {
			if (isIndependent(entity))
				toggleIndependence(entity);

			setSitting(entity, false);
		} else {
			// follow -> independent (& keep standing)
			if (!isIndependent(entity)) {
				toggleIndependence(entity);
				setSitting(entity, false);
			} else {
				// independent -> sit
				setSitting(entity, true);
			}
		}
	}
}
