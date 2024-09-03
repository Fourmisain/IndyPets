package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.command.Commands.WhistleCommand;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.HornSetting;
import com.lizin5ths.indypets.config.ServerConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GoatHornItem.class)
public abstract class GoatHornItemMixin {
	@Shadow @Final
	private TagKey<Instrument> instrumentTag;

	@ModifyArg(
		method = "use",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/item/GoatHornItem;playSound(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/Instrument;)V"
		),
		index = 2
	)
	public Instrument indypets$togglePets(Instrument instrument, @Local(argsOnly = true) World world, @Local(argsOnly = true) PlayerEntity user) {
		if (world instanceof ServerWorld serverWorld && user instanceof ServerPlayerEntity serverPlayer) {
			Config config = ServerConfig.getDefaultedPlayerConfig(user.getUuid());

			Identifier id = Registries.INSTRUMENT.getId(instrument);
			HornSetting hornSetting = config.hornConfig.getOrDefault(id, HornSetting.DISABLED);

			switch (hornSetting) {
				case TOGGLE -> {
					WhistleCommand.untargeted(config.hornState).run(serverWorld, serverPlayer);
					config.hornState = !config.hornState; // TODO save config on exit
				}
				case WHISTLE   -> WhistleCommand.untargeted(false).run(serverWorld, serverPlayer);
				case UNWHISTLE -> WhistleCommand.untargeted(true).run(serverWorld, serverPlayer);
			}
		}

		return instrument;
	}
}
