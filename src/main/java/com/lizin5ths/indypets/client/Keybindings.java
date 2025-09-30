package com.lizin5ths.indypets.client;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.IndyPetsClient;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.mixin.access.KeyBindingAccessor;
import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import static com.lizin5ths.indypets.IndyPetsClient.UNWHISTLE;
import static com.lizin5ths.indypets.IndyPetsClient.WHISTLE;

public class Keybindings {
	public static InputUtil.Key getBoundKey(KeyBinding keyBinding) {
		return ((KeyBindingAccessor) keyBinding).getBoundKey();
	}

	public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(IndyPets.id("indypets"));
	public static final KeyBinding INTERACT_KEY  = new KeyBinding("key.indypets.interact",  GLFW.GLFW_KEY_H, CATEGORY);
	public static final KeyBinding WHISTLE_KEY   = new KeyBinding("key.indypets.whistle",   GLFW.GLFW_KEY_J, CATEGORY);
	public static final KeyBinding UNWHISTLE_KEY = new KeyBinding("key.indypets.unwhistle", GLFW.GLFW_KEY_J, CATEGORY);

	private static boolean whistleToggle = false;

	public static void init() {
		KeyBindingHelper.registerKeyBinding(INTERACT_KEY);
		KeyBindingHelper.registerKeyBinding(WHISTLE_KEY);
		KeyBindingHelper.registerKeyBinding(UNWHISTLE_KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			while (INTERACT_KEY.wasPressed()) {
				if (client.targetedEntity instanceof TameableEntity tameable) {
					Networking.sendPetInteract(tameable); // note: is checked server-side
					client.player.swingHand(Hand.MAIN_HAND); // give some visual feedback
				}
			}

			// detect whistle key presses
			int whistleCount = 0;
			int unwhistleCount = 0;

			while (WHISTLE_KEY.wasPressed()) {
				whistleCount++;
			}
			while (UNWHISTLE_KEY.wasPressed()) {
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
				client.getNetworkHandler().sendChatCommand("indypets whistle");
				IndyPetsClient.playLocalPlayerSound(client.player, WHISTLE, Config.local().positionedWhistleSound);
			} else {
				client.getNetworkHandler().sendChatCommand("indypets unwhistle");
				IndyPetsClient.playLocalPlayerSound(client.player, UNWHISTLE, Config.local().positionedWhistleSound);
			}
		});
	}
}
