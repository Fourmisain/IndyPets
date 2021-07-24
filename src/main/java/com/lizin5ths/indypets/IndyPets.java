package com.lizin5ths.indypets;

import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";

	public static IndyPetsConfig CONFIG;

	public static boolean changeFollowing(PlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (IndyPets.CONFIG.selectiveFollowing && tameable.isOwner(player)) {
			follower.setFollowing(!follower.isFollowing());

			if (!IndyPets.CONFIG.silentMode) {
				String key = follower.isFollowing() ? "text.indypets.following" : "text.indypets.independent";
				if (tameable.hasCustomName()) {
					key += "_named";
				}

				// This is a workaround for not being able to nest TranslatableText (the name especially)
				TranslatableText text = new TranslatableText(key + "_prefix");
				text.append(tameable.getName());
				text.append(new TranslatableText(key + "_suffix"));
				player.sendSystemMessage(text, Util.NIL_UUID);
			}

			return true;
		}

		return false;
	}

	@Override
	public void onInitialize() {
		CONFIG = AutoConfig.register(IndyPetsConfig.class, (definition, configClass) -> new GsonConfigSerializer<>(
			definition, configClass, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create())
		).getConfig();
	}
}