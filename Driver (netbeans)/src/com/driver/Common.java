/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author carlo
 */
public class Common {

    private final static Object wait = new Object();

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

    public static double[] prepareOutput(String speed, String gradi, int outputdim) {
        double[] out = new double[outputdim];
        for (int i = 0; i < out.length; i++) {
            out[i] = 0.3;
        }
        int selected = 0;
        switch (gradi) {
            case "060":
                selected = 0;
                break;
            case "070":
                selected = 1;
                break;
            case "080":
                selected = 2;
                break;
            case "090":
                selected = 3;
                break;
            case "100":
                selected = 4;
                break;
            case "110":
                selected = 5;
                break;
            case "120":
                selected = 6;
                break;
        }
        out[selected] = 0.6;
        return out;
    }

//files and directories functions
    public static int findNextImage(String path) {

        int numeroImm = 0;

        File dir = new File(path);
        String[] str = dir.list();
        int numFiles = str.length;
        for (int count = 0; count < numFiles; count++) {
            String[] str2 = str[count].split("\\.|_");
            if (str2.length != 4) {
                System.out.println("errore nello split del nome file!!!");
                System.exit(0);
            }
            int currentNumImage = Integer.parseInt(str2[0]);
            if (currentNumImage > numeroImm) {
                numeroImm = currentNumImage;
            }
        }

        return numeroImm + 1;
    }

    public static int FindK(int numFiles, Integer pic, String[] str) throws NotFound {
        String pattern = pic.toString() + "_";
        int k;
        PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:" + pattern + "*");
        //System.out.println("pattern ="+pattern);
        for (k = 0; k < numFiles; k++) {
            //System.out.println("pattern ="+pattern);
            Path filename = new File(str[k]).toPath();
            //System.out.println(filename);
            if (matcher.matches(filename)) {
                //System.out.println("yes pattern " + filename);
                break;
            }
        }
        if (k == numFiles || numFiles == 0) {
            throw new NotFound("k non trovato");
        }
        return k;
    }

    public static boolean DeleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] contenuto = dir.list();
            for (int i = 0; i < contenuto.length; i++) {
                boolean success = DeleteDirectory(new File(dir, contenuto[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void createDirecory(String imagePath) {
        if (!Common.DeleteDirectory(new File(imagePath))) {
            System.out.println("la directory per le immagini non esiste");
            Window.settaArea("la directory per le immagini non esiste");
            //System.exit(0);
        } else {
            System.out.println("la directory per le immagini è stata creata");
            Window.settaArea("la directory per le immagini è stata creata");
        }

        int k = 0;
        boolean creata = false;
        while (k < 6 && !creata) {
            synchronized (wait) {
                try {
                    wait.wait(100);
                } catch (InterruptedException ex) {
                }
            }
            if (!(new File(imagePath)).mkdirs()) {
                System.out.println("errore nel creare la directory. riprovo " + k + " ....");
                Window.settaArea("errore nel creare la directory. riprovo " + k + " ... ");
                //System.exit(0);
            } else {
                creata = true;
            }
            k = k + 1;
        }

        if (!creata) {
            System.out.println("errore nel creare la directory. exit....");
            Window.settaArea("errore nel creare la directory. exit... ");
            System.exit(0);
        }

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

    public static int[] byte2int2(byte[] src, int height, int width) {
        int dstLength = src.length;
        int[] dst = new int[dstLength];

        for (int i = 0; i < height*width; i++) {
                Byte b = new Byte(src[i]);
                dst[i] = b.intValue() & 0x000000ff;
        }
        return dst;
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
public static double[][] getFeature1(int[] pixels,int width,int height){
        
        double[] sommeRighe=new double[height];
        double[] sommeColonne=new double[width];
        
        double[][] feature=new double[2][];
        feature[0]=new double[sommeRighe.length];
        feature[1]=new double[sommeColonne.length];
        
        
        for(int i=0;i<width;i++){
            sommeColonne[i]=0.0;
        }
        
        for(int i=0;i<height;i++) {
            double sommaRiga=0;
            for(int j=0;j<width;j++){
                sommaRiga+=pixels[i*width+j];
                double val=(double)pixels[i*width+j];
                sommeColonne[j]+=val/(height*255);
            }
            sommeRighe[i]=sommaRiga/(width*255);
        }
        
        System.arraycopy(sommeRighe, 0, feature[0], 0, sommeRighe.length);
        System.arraycopy(sommeColonne, 0, feature[1], 0, sommeColonne.length);
        
        return feature;
    }

    
    public static double[][] getFeature2(int[] pixels,int width,int height){
    
        double[] sommeNOSE=new double[width+height-1];
        double[] sommeNESO=new double[width+height-1];
        
        double[][] feature=new double[2][];
        feature[0]=new double[sommeNOSE.length];
        feature[1]=new double[sommeNESO.length];
        
        //voglio che ogni pixel abbia lo stesso peso.
        //divido per il numero max di pixel per diagonale.
        //questo è min(width,height)
        double div=(double)Math.min(width, height)*255;
        
        for(int k=0;k<width;k++){
            double somma=0;
            for(int i=0;i<height&&i+k<width;i++){
                somma+=pixels[i*width+i+k];
            }
            sommeNOSE[k]=somma/div;
        }
        
        for(int k=1;k<height;k++){
            double somma=0;
            for(int i=0;i<width&&i+k<height;i++){
                somma+=pixels[(i+k)*width+i];
            }
            sommeNOSE[k+width-1]=somma/div;
        }

        for(int k=0;k<width;k++){
            double somma=0;
            for(int i=0;i<height&&k-i>-1;i++){
                somma+=pixels[i*width+k-i];
            }
            sommeNESO[k]=somma/div;
        }
        
        for(int k=1;k<height;k++){
            double somma=0;
            for(int i=0;i<width&&i+k<height;i++){
                somma+=pixels[(i+k)*width+width-i-1];
            }
            sommeNESO[k+width-1]=somma/div;
        }

        
        System.arraycopy(sommeNOSE, 0, feature[0], 0, sommeNOSE.length);
        System.arraycopy(sommeNESO, 0, feature[1], 0, sommeNESO.length);
        //System.out.println("Nose length "+sommeNOSE.length);
        //System.out.println("Neso length "+sommeNESO.length);
        return feature;
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
                    
                    if(feature[k*numBlockW+r]<0)
                        System.out.println("errore");
                    
                    if(feature[k*numBlockW+r] > 50) {
                        feature[k*numBlockW+r]=0;
                    }
                    else{
                        feature[k*numBlockW+r]=255;
                    }
                    
                        
                }
            }
        } else {
            System.out.println("errore");
        }

        return feature;
    }


    
    public static int[] getFeature3copia(int[] pixels, int width, int height, int bWidth, int bHeight) {

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
                    
                    if(feature[k*numBlockW+r]<0)
                        System.out.println("errore");
                    
                    
                        
                }
            }
        } else {
            System.out.println("errore");
        }

        return feature;
    }
    
    public static double[] getCartesianMomentsFeature(int[] pixels, int width, int height) {
        double[] feature = new double[8];
        double m00=0;
        double m01=0;
        double m10=0;
        double m11=0;
        double m02=0;
        double m20=0;
        double m30=0;
        double m03=0;
        
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                m00+=pixels[x*width+y];
                m10+=pixels[x*width+y]*x;
                m01+=pixels[x*width+y]*y;
                m11+=pixels[x*width+y]*x*y;
                
                m20+=pixels[x*width+y]*Math.pow(x,2);
                m02+=pixels[x*width+y]*Math.pow(y,2);
                m03+=pixels[x*width+y]*Math.pow(x,3);
                m30+=pixels[x*width+y]*Math.pow(y,3);
            }
        }
        
        //normalize output
        m00/=(255*height*width);
        m10/=(255*(height*(height+1))/2*width);
        m01/=(255*(width*(width+1))/2*height);
        m11/=(255*(width*(width+1))/2*(height*(height+1))/2);
        m02/=(255*(height*(height+1)*(2*height+1))/6*width);
        m20/=(255*(width*(width+1)*(2*width+1))/6*height);
        m30/=(255*Math.pow((height*(height+1))/2,2)*width);
        m03/=(255*Math.pow((width*(width+1))/2,2)*height);
        System.out.println("m00="+m00);
        System.out.println("m01="+m01);
        System.out.println("m10="+m10);
        System.out.println("m11="+m11);
        System.out.println("m20="+m20);
        System.out.println("m02="+m02);
        System.out.println("m03="+m03);
        System.out.println("m30="+m30);
        
        
        //m00
        feature[0]=m00;
        //m01
        feature[1]=m01;
        //m10
        feature[2]=m10;
        //m11
        feature[3]=m11;
        //m02
        feature[4]=m02;
        //m20
        feature[5]=m20;
        //m03
        feature[6]=m03;
        //m30
        feature[7]=m30;
        
        return feature;
    }
    
    public static int[] getHuFeature(int[] pixels, int width, int height) {
        int[] feature = null;
        return feature;
    }
    
    public static int[] modifiedImage(byte [] temp,int height, int width
            //commentare
            ,Integer num
                    
                    ){
    
        //commentare 
        
   String imagePath = "Training";
    //fino qui
        
                    byte dst[] = new byte[temp.length];

                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            Byte b = new Byte(temp[height * (143 - j) + i]);
                            dst[(width * i) + j] = b;
                        }
                    }

                    Mat m1 = new Mat(height, width, CvType.CV_8UC1);
                    m1.put(0, 0, dst);
                    Mat m2 = new Mat(height, width, CvType.CV_8UC1);

                    
                    //org.opencv.photo.Photo.fastNlMeansDenoising(m1, m1);
                    //Imgproc.threshold(m1, m2, 30, 255, Imgproc.THRESH_BINARY_INV);

            
                    Imgproc.erode(m1, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)), new Point(-1, -1), 10);
                    Imgproc.dilate(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)), new Point(-1, -1), 5);
                    
                    //commentare
                                        String filename2 = imagePath + File.separator
                            + num.toString()+  "_" + //azione 
                            "resized1"
                            + ".bmp";

                    Highgui.imwrite(filename2, m2);
                    //fino qui
                    
                    Imgproc.threshold(m2, m2, 50, 255, Imgproc.THRESH_BINARY_INV);
                    
                    
                    //commentare 
                    
                    filename2 = imagePath + File.separator
                            + num.toString()+  "_" + //azione 
                            "resized2"
                            + ".bmp";

                    Highgui.imwrite(filename2, m2);
                    //fino qui
                    
                    Imgproc.dilate(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 5);
                    Imgproc.erode(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 5);
                    
                    
                    
//commentare                    
                    filename2 = imagePath + File.separator
                            + num.toString()+  "_" + //azione 
                            "resized3"
                            + ".bmp";

                    Highgui.imwrite(filename2, m2);
                            
//fino a qui
                    
                    
                   /* 
                    Imgproc.dilate(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 3);
                    Imgproc.erode(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 3);

                    Imgproc.dilate(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 4);
                    Imgproc.erode(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 4);

                    Imgproc.dilate(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 5);
                    Imgproc.erode(m2, m2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)), new Point(-1, -1), 5);
                    */
                    byte[] tmp2 = new byte[height * width];
                    
                    m2.get(0, 0, tmp2);
                    
                    byte[] tmp3 = new byte[height * width];

                    for (int j = 0; j < width; j++) {
                        int count=0;
                        for (int i = height-1; i >= 0; i--) {
                            int ind=i*width+j;
                            tmp3[ind]=(byte) 255;
                            if (tmp2[ind] != 0 ){
                                count++;
                                if(count > 7) {
                                    break;
                                }
                            }
                        }
                    }
                    
                    int []frame=Common.byte2int2(tmp3, height, width);
                    return frame;

    }
    
}
