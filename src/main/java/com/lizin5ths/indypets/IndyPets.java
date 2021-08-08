package com.lizin5ths.indypets;

import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.HashSet;
import java.util.UUID;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";
	public static IndyPetsConfig CONFIG;

	public static final Identifier MOD_INSTALLED = new Identifier(MOD_ID, "mod_installed");
	public static final HashSet<UUID> hasModInstalled = new HashSet<>();

	public static boolean changeFollowing(PlayerEntity player, TameableEntity tameable) {
		Follower follower = (Follower) tameable;

		if (IndyPets.CONFIG.selectiveFollowing && tameable.isOwner(player)) {
			follower.setFollowing(!follower.isFollowing());

			if (!IndyPets.CONFIG.silentMode) {
				BaseText text;

				if (hasModInstalled.contains(player.getUuid())) {
					// Send a translatable text
					String key = follower.isFollowing() ? "text.indypets.following" : "text.indypets.independent";
					if (tameable.hasCustomName()) {
						key += "_named";
					}

					// This is a workaround for not being able to nest TranslatableText (the name especially)
					text = new TranslatableText(key + "_prefix");
					text.append(tameable.getName());
					text.append(new TranslatableText(key + "_suffix"));
				} else {
					// Default to sending an English message
					String name = tameable.getName().getString();

					StringBuilder sb = new StringBuilder();
					if (tameable.hasCustomName()) {
						sb.append('\"');
						sb.append(name);
						sb.append('\"');
					} else {
						sb.append("Your ");
						sb.append(name);
					}
					sb.append(follower.isFollowing() ? " is following you" : " is independent");

					text = new LiteralText(sb.toString());
				}

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

		ServerPlayNetworking.registerGlobalReceiver(MOD_INSTALLED, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				hasModInstalled.add(player.getUuid());
			});
		});
	}
}