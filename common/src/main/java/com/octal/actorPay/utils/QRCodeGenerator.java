package com.octal.actorPay.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.octal.actorPay.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeGenerator {

    private final Map QRParam = new HashMap<>();
    private int BARCODE_SIZE = 300;
    public QRCodeGenerator() {
            QRParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRParam.put(EncodeHintType.MARGIN, 1);
            QRParam.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
            QRParam.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            QRParam.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
    }

    public byte[] generateQRCodeImage(String upiQrCode) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(upiQrCode, BarcodeFormat.QR_CODE, BARCODE_SIZE, BARCODE_SIZE,QRParam);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageData = baos.toByteArray();
//        ImageIO.write(bufferedImage, "png", new File("qrcode.png"));
        return imageData;
    }

    void saveQRCode(String email) {

    }
}
