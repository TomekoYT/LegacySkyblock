package tomeko.legacyskyblock.utils;

public class SkyblockItem {
    public String name;
    public String id;
    public int maxStackSize;

    public SkyblockItem(String name, String id, int maxStackSize) {
        this.name = name;
        this.id = id;
        this.maxStackSize = maxStackSize;
    }
}