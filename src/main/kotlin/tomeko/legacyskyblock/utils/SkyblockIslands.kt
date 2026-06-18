package tomeko.legacyskyblock.utils

enum class SkyblockIslands(
    val islandId: String,
    val islandName: String
) {
    PRIVATE_ISLAND("dynamic", "Private Island"),
    SKYBLOCK_HUB("hub", "SkyBlock Hub"),
    DUNGEON_HUB("dungeon_hub", "Dungeon Hub"),
    CATACOMBS("dungeon", "Catacombs"),
    THE_BARN("farming_1", "The Barn"),
    THE_PARK("foraging_1", "The Park"),
    GALATEA("foraging_2", "Galatea"),
    GOLD_MINE("mining_1", "Gold Mine"),
    DEEP_CAVERNS("mining_2", "Deep Caverns"),
    DWARVEN_MINES("mining_3", "Dwarven Mines"),
    CRYSTAL_HOLLOWS("crystal_hollows", "Crystal Hollows"),
    SPIDERS_DEN("combat_1", "Spider's Den"),
    THE_END("combat_3", "The End"),
    CRIMSON_ISLE("crimson_isle", "Crimson Isle"),
    KUUDRA("kuudra", "Kuudra"),
    THE_GARDEN("garden", "The Garden"),
    THE_RIFT("rift", "The Rift"),
    BACKWATER_BAYOU("fishing_1", "Backwater Bayou"),
    LOTUS_ATOLL("lotus_atoll", "Lotus Atoll"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop");

    companion object {
        private val byId = entries.associateBy { it.islandId }

        fun fromId(id: String): SkyblockIslands? = byId[id]
    }
}