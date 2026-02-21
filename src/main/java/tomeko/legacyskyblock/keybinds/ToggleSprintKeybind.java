package tomeko.legacyskyblock.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class ToggleSprintKeybind {
    private static KeyBinding toggleSprintKey;

    public static void register() {
        toggleSprintKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.category.legacyskyblock.togglesprint",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                KeyBinding.Category.create(Identifier.of("legacyskyblock"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleSprintKey.wasPressed()) {
                LegacySkyblockConfig.toggleSprintEnabled = !LegacySkyblockConfig.toggleSprintEnabled;
                LegacySkyblockConfig.CONFIG.save();
            }
        });
    }
}
