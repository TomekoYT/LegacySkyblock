package tomeko.legacyskyblock.utils;

public class Functions {
    public static String removeFormatting(String string) {
        return string.replaceAll("§.", "");
    }
}
