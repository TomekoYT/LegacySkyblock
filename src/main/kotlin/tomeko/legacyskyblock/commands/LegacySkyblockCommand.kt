package tomeko.legacyskyblock.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext
import tomeko.legacyskyblock.utils.Constants
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.utils.v1.dsl.openUI
import tomeko.legacyskyblock.config.LegacySkyblockConfig

object LegacySkyblockCommand {
    private var shouldOpenConfig: Boolean = false

    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher: CommandDispatcher<FabricClientCommandSource>, _: CommandBuildContext ->
            dispatcher.register(
                literal(Constants.MOD_ID)
                    .executes { _: CommandContext<FabricClientCommandSource> ->
                        shouldOpenConfig = true
                        return@executes 1
                    }
            )
        }

        ClientTickEvents.END_CLIENT_TICK.register { _: Minecraft ->
            if (!shouldOpenConfig) return@register

            LegacySkyblockConfig.openUI()
            shouldOpenConfig = false
        }
    }
}