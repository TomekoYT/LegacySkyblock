package tomeko.legacyskyblock.screenshots;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import net.minecraft.client.texture.NativeImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class ScreenshotManager {
    public static ArrayList<NativeImage> screenshotImages = new ArrayList<>();
    public static ArrayList<File> screenshotFiles = new ArrayList<>();

    public static void copyScreenshot(int pos) {
        if (pos >= screenshotImages.toArray().length) return;

        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac")) {
            Client client = Client.getInstance();
            Proxy url = client.sendProxy("NSURL", "fileURLWithPath:", screenshotFiles.get(pos).getPath());

            Proxy image = client.sendProxy("NSImage", "alloc");
            image.send("initWithContentsOfURL:", url);

            Proxy array = client.sendProxy("NSArray", "array");
            array = array.sendProxy("arrayByAddingObject:", image);

            Proxy pasteboard = client.sendProxy("NSPasteboard", "generalPasteboard");
            pasteboard.send("clearContents");
            pasteboard.sendBoolean("writeObjects:", array);
            return;
        }

        BufferedImage image = convert(screenshotImages.get(pos));
        ImageContent content = new ImageContent(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, null);
    }

    public static void deleteScreenshot(int pos) {
        if (pos >= screenshotFiles.toArray().length || !screenshotFiles.get(pos).exists()) return;

        screenshotFiles.get(pos).delete();
    }

    private static BufferedImage convert(NativeImage image) {
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                out.setRGB(x, y, image.getColorArgb(x, y));
            }
        }
        return out;
    }
}
