package com.lizin5ths.indypets.client;

import com.lizin5ths.indypets.IndyPetsClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.lizin5ths.indypets.IndyPetsClient.UNWHISTLE;
import static com.lizin5ths.indypets.IndyPetsClient.WHISTLE;

public class Keybindings {
    private static boolean whistleToggle = false;

    public static void init() {
        KeyBinding whistle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.indypets.whistle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.indypets"));

        KeyBinding unwhistle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.indypets.unwhistle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.categories.indypets"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // detect keys
            int whistleCount = 0;
            int unwhistleCount = 0;

            while (whistle.wasPressed()) {
                whistleCount++;
            }
            while (unwhistle.wasPressed()) {
                unwhistleCount++;
            }

            if (whistleCount + unwhistleCount == 0)
                return;

            boolean shouldWhistle; // else unwhistle

            // when both actions are bound to the same key, switch between the actions (whistle -> unwhistle -> whistle etc)
            if (!whistle.isUnbound() && whistle.equals(unwhistle)) {
                if (whistleCount % 2 == 1) {
                    whistleToggle = !whistleToggle;
                    }
                shouldWhistle = whistleToggle;
            } else {
                // else just do the actions normally
                shouldWhistle = (whistleCount > 0);
                }

            if (shouldWhistle) {
                client.player.sendChatMessage("/indypets whistle");
                IndyPetsClient.playLocalPlayerSound(WHISTLE);
            } else {
                client.player.sendChatMessage("/indypets unwhistle");
                IndyPetsClient.playLocalPlayerSound(UNWHISTLE);
            }
        });
    }
}
