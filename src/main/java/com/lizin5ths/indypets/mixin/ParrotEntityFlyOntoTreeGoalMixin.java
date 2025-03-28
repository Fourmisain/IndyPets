package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(ParrotEntity.FlyOntoTreeGoal.class)
public abstract class ParrotEntityFlyOntoTreeGoalMixin extends FlyGoal {
	public ParrotEntityFlyOntoTreeGoalMixin(PathAwareEntity pathAwareEntity, double d) {
		super(pathAwareEntity, d);
	}

	@Unique
	private static final int indypets$POSITION_COUNT = 7 * 13 * 7;
	@Unique
	private static final List<BlockPos.Mutable> indypets$positions = new ArrayList<>(indypets$POSITION_COUNT);
	static {
		for (int i = 0; i < indypets$POSITION_COUNT; i++) {
			indypets$positions.add(new BlockPos.Mutable());
		}
	}

	// block positions are iterated in x, y, z order, meaning parrots will always wander northwest
	@SuppressWarnings("unchecked")
	@ModifyExpressionValue(method = "locateTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;iterate(IIIIII)Ljava/lang/Iterable;"))
	private Iterable<BlockPos> indypets$shuffledIterable(Iterable<BlockPos> original) {
		int x = MathHelper.floor(mob.getX());
		int y = MathHelper.floor(mob.getY());
		int z = MathHelper.floor(mob.getZ());

		int i = 0;
		for (var pos : BlockPos.iterate(-3, -6, -3, 3, 6, 3)) {
			int dx = pos.getX();
			int dy = pos.getY();
			int dz = pos.getZ();

			indypets$positions.get(i).set(x + dx, y + dy, z + dz);
			i++;
		}

		Util.shuffle(indypets$positions, mob.getRandom());

		return (List<BlockPos>) (List<?>) indypets$positions;
	}

	@Inject(method = "getWanderTarget", at = @At("HEAD"), cancellable = true)
	private void indypets$dontStrayFromHome(CallbackInfoReturnable<Vec3d> cir) {
		if (shouldHeadHome(mob)) {
			Vec3d target = indypets$findTreeClosestToHome();

			if (target == null && mob.isTouchingWater()) {
				target = findTowardsHome(mob, false, 15, 15);
			}

			if (target != null) {
				cir.setReturnValue(target);
			}
		}
	}

	@Unique @Nullable
	private Vec3d indypets$findTreeClosestToHome() {
		BlockPos homePos = getHomePos((TameableEntity) mob);
		BlockPos.Mutable temp = new BlockPos.Mutable();

		BlockPos.Mutable closest = new BlockPos.Mutable();
		double minDist = Double.POSITIVE_INFINITY;

		for (BlockPos pos : BlockPos.iterate(
			MathHelper.floor(mob.getX() - 3.0),
			MathHelper.floor(mob.getY() - 6.0),
			MathHelper.floor(mob.getZ() - 3.0),
			MathHelper.floor(mob.getX() + 3.0),
			MathHelper.floor(mob.getY() + 6.0),
			MathHelper.floor(mob.getZ() + 3.0)
		)) {
			if (pos.equals(mob.getBlockPos()))
				continue;

			BlockState state = mob.getWorld().getBlockState(temp.set(pos, Direction.DOWN));
			boolean isTree = state.getBlock() instanceof LeavesBlock || state.isIn(BlockTags.LOGS);

			if (isTree && mob.getWorld().isAir(pos) && mob.getWorld().isAir(temp.set(pos, Direction.UP))) {
				double dist = pos.getSquaredDistance(homePos);
				if (dist < minDist) {
					minDist = dist;
					closest.set(pos);
				}
			}
		}

		return minDist != Double.POSITIVE_INFINITY ? Vec3d.ofBottomCenter(closest) : null;
	}
}
