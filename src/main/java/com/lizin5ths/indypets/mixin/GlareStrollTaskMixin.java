package com.lizin5ths.indypets.mixin;

import com.faboslav.friendsandfoes.entity.GlareEntity;
import com.faboslav.friendsandfoes.entity.ai.brain.task.glare.GlareStrollTask;
import com.lizin5ths.indypets.util.Follower;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(GlareStrollTask.class)
public abstract class GlareStrollTaskMixin {
	@Inject(method = "run", at = @At("HEAD"), cancellable = true, require = 0)
	public void indypets$dontStrayFromHome(ServerWorld world, GlareEntity glare, long time, CallbackInfo ci) {
		if (shouldHeadHome(glare)) {
			BlockPos homePos = ((Follower) glare).getHomePos();
			Path pathHome = glare.getNavigation().findPathTo(homePos, 1, 12);
			glare.getNavigation().startMovingAlong(pathHome, 1);
			ci.cancel();
		}
	}
}
