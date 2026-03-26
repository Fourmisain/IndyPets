package com.lizin5ths.indypets.mixin.compat.friendsandfoes;

import com.faboslav.friendsandfoes.common.entity.GlareEntity;
import com.faboslav.friendsandfoes.common.entity.ai.brain.task.glare.GlareStrollTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lizin5ths.indypets.util.IndyPetsUtil.getHomePos;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(GlareStrollTask.class)
public abstract class GlareStrollTaskMixin {
	@Inject(method = "start", at = @At("HEAD"), cancellable = true, require = 0)
	public void indypets$dontStrayFromHome(ServerLevel world, GlareEntity glare, long time, CallbackInfo ci) {
		if (shouldHeadHome(glare)) {
			Path pathHome = glare.getNavigation().createPath(getHomePos(glare), 1, 12);
			glare.getNavigation().moveTo(pathHome, 1);
			ci.cancel();
		}
	}
}
