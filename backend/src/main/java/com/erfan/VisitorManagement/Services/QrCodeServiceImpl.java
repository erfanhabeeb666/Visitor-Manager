package com.erfan.VisitorManagement.Services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeServiceImpl {

    private static final String QR_BASE_PATH = "qr-codes/";


    public String generateVisitQr(Long visitId) {

        try {
            String qrContent = "VISIT-" + visitId;
            int size = 350;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, size, size);

            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    int color = (bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    image.setRGB(x, y, color);
                }
            }

            File directory = new File(QR_BASE_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = QR_BASE_PATH + "visit_" + visitId + ".png";
            File qrFile = new File(filePath);
            ImageIO.write(image, "PNG", qrFile);

            return filePath;

        } catch (WriterException | IOException e) {
            log.error("QR generation failed", e);
            throw new RuntimeException("Failed to generate QR code");
        }
    }
}
