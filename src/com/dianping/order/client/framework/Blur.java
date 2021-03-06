package com.dianping.order.client.framework;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * User: NimbuS
 * Date: 13-12-20
 * Time: 23:48
 */
public class Blur extends Task<byte[]> {

    private byte[] jpgRaw;

    public Blur(byte[] jpgRaw) {
        this.jpgRaw = jpgRaw;
    }

    @Override
    protected byte[] doInBackground() {
        Bitmap raw = BitmapFactory.decodeByteArray(jpgRaw, 0, jpgRaw.length);
        jpgRaw = null;
        Bitmap filtered = BoxBlurFilter(raw);
        raw.recycle();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        filtered.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        filtered.recycle();
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

//    private static final int n = 10;
//    private static final double sigma = 10;
//
//    static final double[][] gaussianTemplate =
//            {
//                    {0.00000067, 0.00002292, 0.00019117, 0.00038771, 0.00019117, 0.00002292, 0.00000067},
//                    {0.00002292, 0.00078633, 0.00655965, 0.01330373, 0.00655965, 0.00078633, 0.00002292},
//                    {0.00019117, 0.00655965, 0.05472157, 0.11098164, 0.05472157, 0.00655965, 0.00019117},
//                    {0.00038771, 0.01330373, 0.11098164, 0.22508352, 0.11098164, 0.01330373, 0.00038771},
//                    {0.00019117, 0.00655965, 0.05472157, 0.11098164, 0.05472157, 0.00655965, 0.00019117},
//                    {0.00002292, 0.00078633, 0.00655965, 0.01330373, 0.00655965, 0.00078633, 0.00002292},
//                    {0.00000067, 0.00002292, 0.00019117, 0.00038771, 0.00019117, 0.00002292, 0.00000067}
//            };
//
//    public static double[][] get2DKernalData(int n, double sigma) {
//        int size = 2 * n + 1;
//        double sigma22 = 2 * sigma * sigma;
//        double sigma22PI = (float) Math.PI * sigma22;
//        double[][] kernalData = new double[size][size];
//        int row = 0;
//        for (int i = -n; i <= n; i++) {
//            int column = 0;
//            for (int j = -n; j <= n; j++) {
//                float xDistance = i * i;
//                float yDistance = j * j;
//                kernalData[row][column] = (float) Math.exp(-(xDistance + yDistance) / sigma22) / sigma22PI;
//                column++;
//            }
//            row++;
//        }
//
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                System.out.print("\t" + kernalData[i][j]);
//            }
//            System.out.println();
//            System.out.println("\t ---------------------------");
//        }
//        return kernalData;
//    }
//
////    public static Bitmap blur(Bitmap bitmap) {
////        ImageInputStream imageInputStream = ImageIO.creatImageInputStream(object bitmap);
////        BufferedImage bufferedImage = ImageIO.read(imageInputStream);
////        GaussianFilter gaussianFilter = new GaussianFilter();
////        java.awt.image.BufferedImage filter = gaussianFilter.filter();
////        // normalization
////        float rate = inMax / outMax;
////        System.out.println("Rate = " + rate);
////        for (int row = 0; row < height; row++) {
////            for (int col = 0; col < width; col++) {
////                index = row * width + col;
////                int rgb1 = tempoutPixels[index];
////                int red = (rgb1 >> 16) & 0xff;
////                int green = (rgb1 >> 8) & 0xff;
////                int blue = rgb1 & 0xff;
////                red = (int) (rate * red);
////                green = (int) (rate * green);
////                blue = (int) (rate * blue);
////                outPixels[index] = (rgb1 & 0xff000000) | (red << 16) | (green << 8) | blue;
////            }
////        }
////    }

    /** 水平方向模糊度 */
    private static final float hRadius = 10;
    /** 竖直方向模糊度 */
    private static final float vRadius = 10;
    /** 模糊迭代度 */
    private static final int iterations = 1;
    /**
     * 高斯模糊
     */
    public static Bitmap BoxBlurFilter(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        return             bitmap;
    }

    public static void blur(int[] in, int[] out, int width, int height,
                            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width,
                                      int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

//    /**
//     * 柔化效果(高斯模糊)(优化后比上面快三倍)
//     *
//     * @param bmp
//     * @return
//     */
//    public static Bitmap blurImageAmeliorate(Bitmap bmp) {
//        long start = System.currentTimeMillis();
//        // 高斯矩阵
//        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
//
//        double[][] gaussData = get2DKernalData(n, sigma);
//
//
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//
//        int pixR = 0;
//        int pixG = 0;
//        int pixB = 0;
//
//        int pixColor = 0;
//
//        int newR = 0;
//        int newG = 0;
//        int newB = 0;
//
//        int delta = 16; // 值越小图片会越亮，越大则越暗
//
//        int idx = 0;
//        int[] pixels = new int[width * height];
//        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
//        for (int i = 1, length = height - 1; i < length; i++) {
//            for (int k = 1, len = width - 1; k < len; k++) {
//                idx = 0;
//                for (int m = -1; m <= 1; m++) {
//                    for (int n = -1; n <= 1; n++) {
//                        pixColor = pixels[(i + m) * width + k + n];
//                        pixR = Color.red(pixColor);
//                        pixG = Color.green(pixColor);
//                        pixB = Color.blue(pixColor);
//
//                        newR = newR + (pixR * gauss[idx]);
//                        newG = newG + (pixG * gauss[idx]);
//                        newB = newB + (pixB * gauss[idx]);
//                        idx++;
//                    }
//                }
//
//                newR /= delta;
//                newG /= delta;
//                newB /= delta;
//
//                newR = Math.min(255, Math.max(0, newR));
//                newG = Math.min(255, Math.max(0, newG));
//                newB = Math.min(255, Math.max(0, newB));
//
//                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
//
//                newR = 0;
//                newG = 0;
//                newB = 0;
//            }
//        }
//
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        long end = System.currentTimeMillis();
//        Log.d("may", "used time=" + (end - start));
//        return bitmap;
//    }
}
