package tomeko.legacyskyblock.misc

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.HypixelPackets

object ShowNBTData {
    fun register() {
        ItemTooltipCallback.EVENT.register(::modifyTooltip)
    }

    private fun modifyTooltip(
        stack: ItemStack,
        context: Item.TooltipContext,
        type: TooltipFlag,
        lines: MutableList<Component>
    ) {
        if (!HypixelPackets.inSkyblock
            || !LegacySkyblockConfig.showItemNBTDataInTooltip
            || !stack.has(DataComponents.CUSTOM_DATA)
        ) return

        val nbt = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return

        for (key in nbt.keySet()) {
            val value = nbt.get(key) ?: continue

            addFormattedNBT(lines, key, value)
        }
    }

    private fun addFormattedNBT(
        lines: MutableList<Component>,
        key: String,
        value: Tag,
        indent: Int = 0
    ) {
        val prefix = "  ".repeat(indent)

        when (value) {
            is CompoundTag -> {
                lines.add(
                    Component.literal("$prefix$key: {")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )

                for (childKey in value.keySet()) {
                    addFormattedNBT(
                        lines,
                        childKey,
                        value.get(childKey) ?: continue,
                        indent + 1
                    )
                }

                lines.add(
                    Component.literal("$prefix}")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }

            is ListTag -> {
                lines.add(
                    Component.literal("$prefix$key: [")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )

                for (element in value) {
                    lines.add(
                        Component.literal(
                            "$prefix  ${element.asString()}"
                        ).withStyle(ChatFormatting.DARK_GRAY)
                    )
                }

                lines.add(
                    Component.literal("$prefix]")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }

            else -> {
                lines.add(
                    Component.literal("$prefix$key: $value")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }
        }
    }
}