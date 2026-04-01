package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(Parrot.ParrotWanderGoal.class)
public abstract class ParrotWanderGoalMixin extends WaterAvoidingRandomFlyingGoal {
	public ParrotWanderGoalMixin(PathfinderMob pathAwareEntity, double d) {
		super(pathAwareEntity, d);
	}

	@Unique
	private static final int indypets$POSITION_COUNT = 7 * 13 * 7;
	@Unique
	private static final List<MutableBlockPos> indypets$positions = new ArrayList<>(indypets$POSITION_COUNT);
	static {
		for (int i = 0; i < indypets$POSITION_COUNT; i++) {
			indypets$positions.add(new MutableBlockPos());
		}
	}

	// block positions are iterated in x, y, z order, meaning parrots will always wander northwest
	@SuppressWarnings("unchecked")
	@ModifyExpressionValue(method = "getTreePos", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;betweenClosed(IIIIII)Ljava/lang/Iterable;"))
	private Iterable<BlockPos> indypets$shuffledIterable(Iterable<BlockPos> original) {
		int x = Mth.floor(mob.getX());
		int y = Mth.floor(mob.getY());
		int z = Mth.floor(mob.getZ());

		int i = 0;
		for (var pos : BlockPos.betweenClosed(-3, -6, -3, 3, 6, 3)) {
			int dx = pos.getX();
			int dy = pos.getY();
			int dz = pos.getZ();

			indypets$positions.get(i).set(x + dx, y + dy, z + dz);
			i++;
		}

		Util.shuffle(indypets$positions, mob.getRandom());

		return (List<BlockPos>) (List<?>) indypets$positions;
	}

	@Inject(method = "getPosition", at = @At("HEAD"), cancellable = true)
	private void indypets$dontStrayFromHome(CallbackInfoReturnable<Vec3> cir) {
		if (shouldHeadHome(mob)) {
			Vec3 target = indypets$findTreeClosestToHome();

			if (target == null && mob.isInWater()) {
				target = findTowardsHome(mob, false, 15, 15);
			}

			if (target != null) {
				cir.setReturnValue(target);
			}
		}
	}

	@Unique @Nullable
	private Vec3 indypets$findTreeClosestToHome() {
		BlockPos homePos = getHomePos(mob);
		var temp = new MutableBlockPos();

		var closest = new MutableBlockPos();
		double minDist = Double.POSITIVE_INFINITY;

		for (BlockPos pos : BlockPos.betweenClosed(
			Mth.floor(mob.getX() - 3.0),
			Mth.floor(mob.getY() - 6.0),
			Mth.floor(mob.getZ() - 3.0),
			Mth.floor(mob.getX() + 3.0),
			Mth.floor(mob.getY() + 6.0),
			Mth.floor(mob.getZ() + 3.0)
		)) {
			if (pos.equals(mob.blockPosition()))
				continue;

			BlockState state = mob.level().getBlockState(temp.setWithOffset(pos, Direction.DOWN));
			boolean isTree = state.getBlock() instanceof LeavesBlock || state.is(BlockTags.LOGS);

			if (isTree && mob.level().isEmptyBlock(pos) && mob.level().isEmptyBlock(temp.setWithOffset(pos, Direction.UP))) {
				double dist = pos.distSqr(homePos);
				if (dist < minDist) {
					minDist = dist;
					closest.set(pos);
				}
			}
		}

		return minDist != Double.POSITIVE_INFINITY ? Vec3.atBottomCenterOf(closest) : null;
	}
}
