package team.plugincrafters.opwatch.utils;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class QRMapRenderer extends MapRenderer {

    private final BufferedImage cacheImage;

    private boolean hasRendered = false;

    public QRMapRenderer(String secret, String serverName, String userName) throws IOException {
        String url = getQRCodeURL(secret, serverName, userName);
        cacheImage = this.getImage(url);
    }

    private String getQRCodeURL(String secret, String serverName, String userName) {
        return "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/"
                + serverName + "(" + userName + ")" + "?secret=" + secret + "&issuer=" + serverName;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if(this.hasRendered) return;

        view.setScale(MapView.Scale.CLOSEST);
        if(this.cacheImage != null)
            canvas.drawImage(0, 0, this.cacheImage);

        this.hasRendered = true;
    }

    private BufferedImage getImage(String url) throws IOException {
        boolean useCache = ImageIO.getUseCache();
        ImageIO.setUseCache(false);

        BufferedImage image = resize(new URL(url), new Dimension(128, 128));

        ImageIO.setUseCache(useCache);
        return image;
    }

    private BufferedImage resize(final URL url, final Dimension size) throws IOException {
        final BufferedImage image = ImageIO.read(url.openStream());
        final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resized.createGraphics();

        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }
}