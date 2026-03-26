package com.lizin5ths.indypets;

import com.lizin5ths.indypets.client.Keybindings;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class IndyPetsClient implements ClientModInitializer {
	public static final SoundEvent WHISTLE = registerSoundEvent("whistle");
	public static final SoundEvent UNWHISTLE = registerSoundEvent("unwhistle");

	private static SoundEvent registerSoundEvent(String path) {
		Identifier whistleId = IndyPets.id(path);
		// note: we technically don't need to register the event
		return Registry.register(BuiltInRegistries.SOUND_EVENT, whistleId, SoundEvent.createVariableRangeEvent(whistleId));
	}

	public static void playLocalPlayerSound(Player player, SoundEvent soundEvent, boolean positioned) {
		if (positioned) {
			player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), soundEvent, player.getSoundSource(), 1.0F, 1.0F, true);
		} else {
			// non-positioned sound (just like music, but with players category)
			SoundInstance sound = new SimpleSoundInstance(soundEvent.location(), SoundSource.PLAYERS,
				1.0F, 1.0F, SoundInstance.createUnseededRandom(), false, 0,
				SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);

			Minecraft.getInstance().getSoundManager().play(sound);
		}
	}

	@Override
	public void onInitializeClient() {
		Config.clientInit();
		Keybindings.init();
		Networking.clientInit();
	}
}
