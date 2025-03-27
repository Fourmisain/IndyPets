package com.lizin5ths.indypets;

import com.lizin5ths.indypets.client.Keybindings;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class IndyPetsClient implements ClientModInitializer {
	public static final SoundEvent WHISTLE = registerSoundEvent("whistle");
	public static final SoundEvent UNWHISTLE = registerSoundEvent("unwhistle");

	private static SoundEvent registerSoundEvent(String path) {
		Identifier whistleId = IndyPets.id(path);
		// note: we technically don't need to register the event
		return Registry.register(Registries.SOUND_EVENT, whistleId, SoundEvent.of(whistleId));
	}

	public static void playLocalPlayerSound(PlayerEntity player, SoundEvent soundEvent, boolean positioned) {
		if (positioned) {
			player.getWorld().playSound((Entity) null, player.getX(), player.getY(), player.getZ(), soundEvent, player.getSoundCategory(), 1.0F, 1.0F);
		} else {
			// non-positioned sound (just like music, but with players category)
			SoundInstance sound = new PositionedSoundInstance(soundEvent.id(), SoundCategory.PLAYERS,
				1.0F, 1.0F, SoundInstance.createRandom(), false, 0,
				SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);

			MinecraftClient.getInstance().getSoundManager().play(sound);
		}
	}

	@Override
	public void onInitializeClient() {
		Config.clientInit();
		Keybindings.init();
		Networking.clientInit();
	}
}
