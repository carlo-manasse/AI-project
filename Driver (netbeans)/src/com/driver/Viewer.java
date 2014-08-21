/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author carlo
 */
public class Viewer extends javax.swing.JPanel implements Runnable {

    private BufferedImage img = null;
    private int width = 144 * 2;
    private int height = 176 * 2;
    private int size = height * width / 4;
    private int[] pixels = new int[size];
    private boolean isRunning=true;
    private boolean isConnected = false;
    private boolean newImage = false;
    
    public void setConnected(boolean value){
        isConnected=value;
        newImage=true;
    }
    
    synchronized int[] getPixel() {
        return pixels;
    }

    public void setNewImage(){
        newImage=true;
    }
    
    public synchronized void setPixel(int[] frame) {
        System.arraycopy(frame, 0, pixels, 0, size);
    }

    public synchronized void setPixelTest(int[] frame) {
        pixels = new int[frame.length];
        System.arraycopy(frame, 0, pixels, 0, size);
    }

    Viewer() {
        super(new FlowLayout());
    }

    
    public void start(){
        Thread t=new Thread(this);
        t.start();
    }
    
    public void stop(){
        isRunning=false;
    }

    @Override
    public void run() {
        while (isRunning) {
            if(newImage){
                repaint();
                newImage=false;
            }

            if(!isConnected){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                newImage=true;
            }else{
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!isConnected) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);//10 -> grayscale
            int[] pixels2 = new int[width * height];
            for (int j = 0; j < width * height; j++) {
                if ((j + 1) % 3 == 0) {
                    pixels2[j] = (int) (255 * Math.random());
                } else {
                    pixels2[j] = 100;
                }
            }
            WritableRaster raster = img.getRaster();
            raster.setPixels(0, 0, width, height, pixels2);
            g.drawImage(img, (int) (this.getWidth() - width) / 2, (int) (this.getHeight() - height) / 2, null);
        } else {
            img = new BufferedImage(width / 2, height / 2, BufferedImage.TYPE_BYTE_GRAY);
            int[] temp;
            temp = getPixel();
            WritableRaster raster = img.getRaster();
            raster.setPixels(0, 0, width / 2, height / 2, temp);
            double ridimensionamento = 2.0;
            BufferedImage bufferDestinazione = new BufferedImage((int) (img.getWidth() * ridimensionamento),
                    (int) (img.getHeight() * ridimensionamento), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2 = bufferDestinazione.createGraphics();
            AffineTransform attribute = AffineTransform.getScaleInstance(ridimensionamento, ridimensionamento);
            g2.drawRenderedImage(img, attribute);
            g.drawImage(bufferDestinazione, (int) (this.getWidth() - width) / 2, (int) (this.getHeight() - height) / 2, null);
        }

    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
}
