package tomeko.legacyskyblock.misc

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
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
            || !stack.has(DataComponents.CUSTOM_DATA)
        ) return

        val nbt = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return

        for (key in nbt.keySet()) {
            if (!LegacySkyblockConfig.NBTDataEnabledTypes[NBTTypes.fromId(key).ordinal]) continue

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

            is StringTag -> {
                val text = value.toString().removePrefix("'").removeSuffix("'")

                if (text.startsWith("{") || text.startsWith("[")) {
                    try {
                        addFormattedJson(
                            lines,
                            key,
                            JsonParser.parseString(text),
                            indent
                        )
                    } catch (_: JsonSyntaxException) {
                        lines.add(
                            Component.literal("$prefix$key: $text")
                                .withStyle(ChatFormatting.DARK_GRAY)
                        )
                    }
                } else {
                    lines.add(
                        Component.literal("$prefix$key: $text")
                            .withStyle(ChatFormatting.DARK_GRAY)
                    )
                }
            }

            else -> {
                lines.add(
                    Component.literal("$prefix$key: $value")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }
        }
    }

    private fun addFormattedJson(
        lines: MutableList<Component>,
        key: String,
        element: JsonElement,
        indent: Int = 0
    ) {
        val prefix = "    ".repeat(indent)

        when {
            element.isJsonObject -> {
                lines.add(
                    Component.literal("$prefix$key: {")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )

                for ((childKey, childValue) in element.asJsonObject.entrySet()) {
                    addFormattedJson(lines, childKey, childValue, indent + 1)
                }

                lines.add(
                    Component.literal("$prefix}")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }

            element.isJsonArray -> {
                lines.add(
                    Component.literal("$prefix$key: [")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )

                for (child in element.asJsonArray) {
                    if (child.isJsonPrimitive) {
                        lines.add(
                            Component.literal("$prefix    ${child.asString}")
                                .withStyle(ChatFormatting.DARK_GRAY)
                        )
                    } else {
                        addFormattedJson(lines, "-", child, indent + 1)
                    }
                }

                lines.add(
                    Component.literal("$prefix]")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }

            element.isJsonPrimitive -> {
                val value = element.asJsonPrimitive.let {
                    when {
                        it.isString -> it.asString
                        it.isBoolean -> it.asBoolean.toString()
                        it.isNumber -> it.asNumber.toString()
                        else -> it.toString()
                    }
                }

                lines.add(
                    Component.literal("$prefix$key: $value")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }

            element.isJsonNull -> {
                lines.add(
                    Component.literal("$prefix$key: null")
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }
        }
    }
}