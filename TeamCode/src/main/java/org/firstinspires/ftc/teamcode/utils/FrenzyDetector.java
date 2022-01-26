package org.firstinspires.ftc.teamcode.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FrenzyDetector extends Thread {
    private VuforiaLocalizerImplSubclass vuforia;
    private Telemetry telemetry;
    private int position;
    private ElapsedTime elapsedTime = new ElapsedTime();

    public FrenzyDetector(VuforiaLocalizerImplSubclass vuforia, Telemetry telemetry) {
        this.vuforia = vuforia;
        this.telemetry = telemetry;
    }

    public void run() {
        while (true) {
            processImage();
            position = (int) Math.random() * 3;
        }
    }

    public static Bitmap resizeImage(Bitmap original, int newWidth, int newHeight) {
        Bitmap resized = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
        return resized;
    }

    public int getPosition() {
        return position;
    }

    public static int convertColorToGrayscale(int rgbColor) {
        int r = Color.red(rgbColor);
        int g = Color.green(rgbColor);
        int b = Color.blue(rgbColor);
        int newVal = (r + g + b) / 3;
        int newColor = Color.rgb(newVal, newVal, newVal);
        return newColor;
    }

    public static Bitmap grayscaleFilter(Bitmap original) {
        Bitmap newImg = original;
        long width = original.getWidth();
        long height = original.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newImg.setPixel(x, y, convertColorToGrayscale(original.getPixel(x, y)));
            }
        }
        return newImg;
    }

    public static int calculateBrightnessEstimate(Bitmap bitmap, int pixelSpacing) {
        int R = 0; int G = 0; int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return (R + B + G) / (n * 3);
    }

    public static double truncate(double x) {
        return Math.min(255, Math.max(0, x));
    }

    public static Bitmap contrastFilter(Bitmap original, Integer beta) {
        Bitmap newImg = original;
        long width = original.getWidth();
        long height = original.getHeight();
        double alpha;
        int m_mean = calculateBrightnessEstimate(original, 1);

        if (beta == 255) {
            alpha = Double.POSITIVE_INFINITY;

        } else {
            alpha = (255 + beta) / (double) (255 - beta);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = original.getPixel(x, y);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int r_ = (int) truncate(alpha*(r - m_mean) + m_mean);
                int g_ = (int) truncate(alpha*(g - m_mean) + m_mean);
                int b_ = (int) truncate(alpha*(b - m_mean) + m_mean);
                int newColor = Color.rgb(r_, g_, b_);
                newImg.setPixel(x, y, newColor);
            }
        }

        return newImg;
    }

    public static void saveBitmap(String path, String bitName,
                                  Bitmap mBitmap) {//  ww  w.j  a va 2s.c  o  m

        File f = new File(Environment.getExternalStorageDirectory()
                .toString() + "/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bitmap[] splitImageIntoChunks(Bitmap image, int chunks) {
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap[] bitmaps = new Bitmap[chunks];
        if (width % chunks == 0) {
            for (int chunk = 0; chunk < chunks; chunk++) {
                bitmaps[chunk] = Bitmap.createBitmap(width / chunks, height, Bitmap.Config.RGB_565);

                for (int x = 0; x < width / chunks; x++) {
                    for (int y = 0; y < height; y++) {
                        int newX = x + (chunk * (width / chunks));
                        bitmaps[chunk].setPixel(x, y, image.getPixel(newX, y));
                    }
                }

            }
        }
        return bitmaps;
    }

    public void processImage() {
        Bitmap bmp = vuforia.getLatestBitmap();
        if (bmp == null) {
            return;
        }
        bmp = resizeImage(bmp, 210, 210);
        bmp = grayscaleFilter(bmp);
        bmp = contrastFilter(bmp, 100);
        Bitmap[] chunksOfImages = splitImageIntoChunks(bmp, 3);
        for (Bitmap chunk : chunksOfImages) {
            saveBitmap("/storage/emmc/DCIM/FTC_IMAGES", "MY_IMAGE" + elapsedTime.toString(), chunk);
        }
    }

}
