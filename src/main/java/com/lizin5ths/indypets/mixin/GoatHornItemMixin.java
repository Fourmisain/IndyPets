package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.command.Commands.WhistleCommand;
import com.lizin5ths.indypets.config.ServerConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InstrumentItem.class)
public abstract class GoatHornItemMixin {
	@ModifyArg(
		method = "use",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/InstrumentItem;play(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/Instrument;)V"
		),
		index = 2
	)
	public Instrument indypets$togglePets(Instrument instrument, @Local(argsOnly = true) Level world, @Local(argsOnly = true) Player user) {
		if (world instanceof ServerLevel serverWorld && user instanceof ServerPlayer serverPlayer) {
			var config = ServerConfig.getDefaultedPlayerConfig(user.getUUID());
			var hornId = serverWorld.registryAccess().lookupOrThrow(Registries.INSTRUMENT).getKey(instrument);

			switch (config.getHornSetting(hornId)) {
				case WHISTLE   -> WhistleCommand.untargeted(false).run(serverWorld, serverPlayer);
				case UNWHISTLE -> WhistleCommand.untargeted(true).run(serverWorld, serverPlayer);
				case TOGGLE -> {
					WhistleCommand.untargeted(config.hornState).run(serverWorld, serverPlayer);
					config.hornState = !config.hornState;
				}
				case WHISTLE_OR_SNEAK_UNWHISTLE -> WhistleCommand.untargeted(serverPlayer.isShiftKeyDown()).run(serverWorld, serverPlayer);
			}
		}

		return instrument;
	}
}
