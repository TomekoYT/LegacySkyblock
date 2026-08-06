package tomeko.legacyskyblock.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

object ChatHelper {
    @JvmStatic
    fun addChatMessage(message: Component) {
        if (Minecraft.getInstance().player == null) return

        val component = Component.empty().append(
            gradientText(
                "[${Constants.MOD_NAME}] ",
                0x00FF2F,
                0x00FFD0
            )
        ).append(message)

        //? if >= 26.2 {
        /*Minecraft.getInstance().gui.hud.chat.addClientSystemMessage(component)
        *///?} else {
        Minecraft.getInstance().gui.chat.addClientSystemMessage(component)
        //?}
    }

    fun gradientText(text: String, startColor: Int, endColor: Int): MutableComponent {
        val result = Component.empty()

        val length = text.length

        for (i in text.indices) {
            val progress = if (length <= 1) 0f else i.toFloat() / (length - 1)

            val r = (((startColor shr 16) and 0xFF) * (1 - progress) +
                    ((endColor shr 16) and 0xFF) * progress).toInt()

            val g = (((startColor shr 8) and 0xFF) * (1 - progress) +
                    ((endColor shr 8) and 0xFF) * progress).toInt()

            val b = (((startColor and 0xFF) * (1 - progress)) +
                    ((endColor and 0xFF) * progress)).toInt()

            val color = (r shl 16) or (g shl 8) or b

            result.append(
                Component.literal(text[i].toString())
                    .setStyle(Style.EMPTY.withColor(color))
            )
        }

        return result
    }
}