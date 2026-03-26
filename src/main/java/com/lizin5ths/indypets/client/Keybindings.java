package com.lizin5ths.indypets.client;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.IndyPetsClient;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.mixin.access.KeyBindingAccessor;
import com.lizin5ths.indypets.network.Networking;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import org.lwjgl.glfw.GLFW;

import static com.lizin5ths.indypets.IndyPetsClient.UNWHISTLE;
import static com.lizin5ths.indypets.IndyPetsClient.WHISTLE;
import static com.lizin5ths.indypets.util.IndyPetsUtil.isSupported;

public class Keybindings {
	public static InputConstants.Key getBoundKey(KeyMapping keyBinding) {
		return ((KeyBindingAccessor) keyBinding).getKey();
	}

	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(IndyPets.id("indypets"));
	public static final KeyMapping INTERACT_KEY  = new KeyMapping("key.indypets.interact",  GLFW.GLFW_KEY_H, CATEGORY);
	public static final KeyMapping WHISTLE_KEY   = new KeyMapping("key.indypets.whistle",   GLFW.GLFW_KEY_J, CATEGORY);
	public static final KeyMapping UNWHISTLE_KEY = new KeyMapping("key.indypets.unwhistle", GLFW.GLFW_KEY_J, CATEGORY);

	private static boolean whistleToggle = false;

	@SuppressWarnings("DataFlowIssue")
	public static void init() {
		KeyBindingHelper.registerKeyBinding(INTERACT_KEY);
		KeyBindingHelper.registerKeyBinding(WHISTLE_KEY);
		KeyBindingHelper.registerKeyBinding(UNWHISTLE_KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			while (INTERACT_KEY.consumeClick()) {
				if (isSupported(client.crosshairPickEntity)) {
					Networking.sendPetInteract(client.crosshairPickEntity); // note: is checked server-side
					client.player.swing(InteractionHand.MAIN_HAND); // give some visual feedback
				}
			}

			// detect whistle key presses
			int whistleCount = 0;
			int unwhistleCount = 0;

			while (WHISTLE_KEY.consumeClick()) {
				whistleCount++;
			}
			while (UNWHISTLE_KEY.consumeClick()) {
				unwhistleCount++;
			}

			if (whistleCount + unwhistleCount == 0)
				return;

			boolean shouldWhistle; // else unwhistle

			// when both actions are bound to the same key, switch between the actions (whistle -> unwhistle -> whistle etc)
			if (!WHISTLE_KEY.isUnbound() && getBoundKey(WHISTLE_KEY).equals(getBoundKey(UNWHISTLE_KEY))) {
				// with Amecs, both key bindings count, in vanilla only one counts, so we check the max of the two
				if (Math.max(whistleCount, unwhistleCount) % 2 == 1) {
					whistleToggle = !whistleToggle;
				}
				shouldWhistle = whistleToggle;
			} else {
				// else just do the actions normally
				shouldWhistle = (whistleCount > 0);
			}

			if (shouldWhistle) {
				client.getConnection().sendCommand("indypets whistle");
				IndyPetsClient.playLocalPlayerSound(client.player, WHISTLE, Config.local().positionedWhistleSound);
			} else {
				client.getConnection().sendCommand("indypets unwhistle");
				IndyPetsClient.playLocalPlayerSound(client.player, UNWHISTLE, Config.local().positionedWhistleSound);
			}
		});
	}
}
