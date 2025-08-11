package com.rtvnewsnetwork.file.common.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class ImageCompressor {

    public static InputStream compress(InputStream inputStream) throws IOException {
        return compress(inputStream, 0.4f);
    }

    public static InputStream compress(InputStream inputStream, float quality) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);

        int targetWidth = 1024;
        int targetHeight = (int) (targetWidth * (double) originalImage.getHeight() / originalImage.getWidth());

        Image resultingImage = originalImage.getScaledInstance(
                targetWidth, targetHeight, Image.SCALE_FAST
        );

        BufferedImage outputImage = new BufferedImage(
                targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = outputImage.createGraphics();
        graphics.drawImage(resultingImage, 0, 0, null);
        graphics.dispose();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found for JPG format.");
        }
        ImageWriter writer = writers.next();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        writer.setOutput(ImageIO.createImageOutputStream(output));

        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(quality);

        writer.write(null, new IIOImage(outputImage, null, null), params);

        output.close();
        writer.dispose();

        return new ByteArrayInputStream(output.toByteArray());
    }
}
