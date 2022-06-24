package com.lizin5ths.indypets.client;

import com.lizin5ths.indypets.IndyPetsClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
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
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            // detect keys
            boolean pressedWhistle = false;
            boolean pressedUnwhistle = false;

            while (whistle.wasPressed()) {
                pressedWhistle = true;
            }
            while (unwhistle.wasPressed()) {
                pressedUnwhistle = true;
            }

            boolean doWhistle = false;
            boolean doUnwhistle = false;

            // when both actions are bound to the same key, switch between the actions (whistle -> unwhistle -> whistle etc)
            boolean shouldToggle = !whistle.isUnbound() && whistle.equals(unwhistle);
            if (shouldToggle) {
                if (pressedWhistle || pressedUnwhistle) {
                    whistleToggle = !whistleToggle;

                    if (whistleToggle) {
                        doWhistle = true;
                    } else {
                        doUnwhistle = true;
                    }
                }
            } else {
                // else just do the actions normally
                if (pressedWhistle) {
                    doWhistle = true;
                }
                if (pressedUnwhistle) {
                    doUnwhistle = true;
                }
            }

            if (doWhistle) {
                player.sendChatMessage("/indypets whistle");
                IndyPetsClient.playLocalPlayerSound(WHISTLE);
            } else if (doUnwhistle) {
                player.sendChatMessage("/indypets unwhistle");
                IndyPetsClient.playLocalPlayerSound(UNWHISTLE);
            }
        });
    }
}
