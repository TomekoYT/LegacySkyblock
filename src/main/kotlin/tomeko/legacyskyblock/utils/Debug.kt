package tomeko.legacyskyblock.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tomeko.legacyskyblock.config.LegacySkyblockConfig

object Debug {
    private val LOGGER: Logger = LoggerFactory.getLogger(Constants.MOD_ID)

    @JvmStatic
    fun print(message: String) {
        if (!LegacySkyblockConfig.debugModeEnabled) return

        forcePrint(message)
    }

    fun forcePrint(message: String) {
        LOGGER.info("[${Constants.MOD_NAME}] $message")
    }
}