/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;


/**
 *
 * @author carlo
 */
public class Common {
    
    //neural functions 
    public static String retrieveOutput(double[] out) {
        String s = "255_090";
        double max = 0.0;
        int index = 0;
        for (int i = 0; i < out.length; i++) {
            if (out[i] > max) {
                max = out[i];
                index = i;
            }
        }
        switch (index) {
            case 0:
                s = "255_060";
                break;
            case 1:
                s = "255_070";
                break;
            case 2:
                s = "255_080";
                break;
            case 3:
                s = "255_090";
                break;
            case 4:
                s = "255_100";
                break;
            case 5:
                s = "255_110";
                break;
            case 6:
                s = "255_120";
                break;
        }
        return s;
    }
       
    //images functions
    static public void decodeYUV420SPGrayscale(byte[] rgb, byte[] yuv420sp,
            int height, int width) {
        final int frameSize = height * width;

        for (int pix = 0; pix < frameSize; pix++) {
            int pixVal = (0xff & ((int) yuv420sp[pix])) - 16;
            if (pixVal < 0) {
                pixVal = 0;
            }
            if (pixVal > 255) {
                pixVal = 255;
            }
            rgb[pix] = (byte) pixVal;
        }
    }
    
        

    public static int[] byte2int(byte[] src, int height, int width) {
        int dstLength = src.length;
        int[] dst = new int[dstLength];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Byte b = new Byte(src[height * (143 - j) + i]);
                dst[(width * i) + j] = b.intValue() & 0x000000ff;
                if (dst[(width * i) + j] < 0) {
                    System.out.println(dst[(width * i) + j]);
                }
            }
        }
        return dst;
    }
    
    public static int[] getFeature3(int[] pixels, int width, int height, int bWidth, int bHeight) {

        int[] feature = null;
        if (height % bHeight == 0 && width % bWidth == 0) {
            int numBlockH = height / bHeight;
            int numBlockW = width / bWidth;
            int totalBlocks = numBlockH * numBlockW;
            feature = new int[totalBlocks];
            for (int k = 0; k < numBlockH; k++) {
                for (int r = 0; r < numBlockW; r++) {
                    int value = 0;
                    for (int i = 0; i < bHeight; i++) {
                        for (int j = 0; j < bWidth; j++) {
                            int index=i * width + j + r * bWidth + k*width*bHeight;
                            value += pixels[index];
                        }
                    }
                    feature[k*numBlockW+r] = Math.round((float) value / (bWidth * bHeight));
                }
            }
        } else {
            System.out.println("errore");
        }

        return feature;
    }


        
}
