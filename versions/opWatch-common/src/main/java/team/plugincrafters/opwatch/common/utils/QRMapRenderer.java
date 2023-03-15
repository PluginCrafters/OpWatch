package team.plugincrafters.opwatch.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class QRMapRenderer extends MapRenderer {

    private BufferedImage qrCode;

    public QRMapRenderer(String secret, String serverName, String userName) throws WriterException, IOException {
        String url = "otpauth://totp/" + serverName + ":" + userName + "?secret=" + secret + "&issuer=" + serverName;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 150, 150);
        qrCode = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 150; x++) {
            for (int y = 0; y < 150; y++) {
                qrCode.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        int x = (128 - qrCode.getWidth()) / 2;
        int y = (128 - qrCode.getHeight()) / 2;
        mapCanvas.drawImage(x, y, qrCode);
    }
}