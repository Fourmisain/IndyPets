package com.lizin5ths.indypets.mixin;

import com.faboslav.friendsandfoes.common.entity.GlareEntity;
import com.faboslav.friendsandfoes.common.entity.ai.brain.task.glare.GlareStrollTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lizin5ths.indypets.util.IndyPetsUtil.getHomePos;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(GlareStrollTask.class)
public abstract class GlareStrollTaskMixin {
	@Inject(method = {"run", "start"}, at = @At("HEAD"), cancellable = true, require = 0)
	public void indypets$dontStrayFromHome(ServerWorld world, GlareEntity glare, long time, CallbackInfo ci) {
		if (shouldHeadHome(glare)) {
			Path pathHome = glare.getNavigation().findPathTo(getHomePos(glare), 1, 12);
			glare.getNavigation().startMovingAlong(pathHome, 1);
			ci.cancel();
		}
	}
}
