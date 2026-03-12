package tomeko.legacyskyblock.utils;

import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class Debug {
    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);

    public static void print(String message) {
        if (!LegacySkyblockConfig.debugModeEnabled) return;

        LOGGER.info("[" + Constants.MOD_NAME + "] " + message);
    }

    public static void print(Text message) {
        if (!LegacySkyblockConfig.debugModeEnabled) return;
        
        LOGGER.info("[" + Constants.MOD_NAME + "] " + message.getString());
    }
}
