package tomeko.legacyskyblock.dungeons

enum class DungeonItems(
    val itemID: String,
    val itemName: String,
    val itemStackSize: Int
) {
    ENDER_PEARL("ENDER_PEARL", "Ender Pearl", 16),
    SPIRIT_LEAP("SPIRIT_LEAP", "Spirit Leap", 16),
    SUPERBOOM_TNT("SUPERBOOM_TNT", "Superboom TNT", 64),
    DECOY("DUNGEON_DECOY", "Decoy", 64),
    INFLATABLE_JERRY("INFLATABLE_JERRY", "Inflatable Jerry", 64);

    companion object {
        private val byId = DungeonItems.entries.associateBy { it.itemID }

        fun fromId(id: String): DungeonItems? = byId[id]
    }
}