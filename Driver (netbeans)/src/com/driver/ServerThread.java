/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 *
 * @author carlo
 */
public class ServerThread extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream inputStream = null;
    private PrintWriter outputStream = null;
    private int size = 38016;
    //private int scale = 4;
    private int bWidth = 16;
    private int bHeight = 16;
    private int width = 144;
    private int height = 176;
    private boolean isRunning = true;
    private String imagePath = "Training";
    private Integer numeroImmagine;
    private Viewer view;

    public void stopServer() {
        isRunning = false;
    }

    ServerThread(Viewer view) {
        super();

        this.view = view;
        numeroImmagine = 0;
        try {
            server = new ServerSocket(8900);
            System.out.println("Aperta la porta 8900...");
            Window.settaArea("Aperta la porta 8900...");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Window.settaArea(ex.getMessage());
        }

        int n = JOptionPane.showConfirmDialog(
                null,
                "Cancellare le immagini precedenti\n"
                + "?",
                "Conferma",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            Common.createDirecory(imagePath);

            //modifica
            Common.createDirecory("Test");
        } else {
            numeroImmagine = Common.findNextImage(imagePath);

            //modifica
            int numImmTest = Common.findNextImage("Test");
            if (numImmTest > numeroImmagine) {
                numeroImmagine = numImmTest;
            }

        }
        Window.settaArea("prima immagine " + numeroImmagine);
    }

    @Override
    public void run() {

        try {
            System.out.println("In attesa della connessione...");
            Window.settaArea("In attesa della connessione...");
            server.setSoTimeout(15000);
            socket = server.accept();
            System.out.println("client connesso");
            Window.settaArea("client connesso");
            try {
                outputStream = new PrintWriter(socket.getOutputStream(), true);
                inputStream = new DataInputStream(socket.getInputStream());
                socket.setSoTimeout(15000);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
                Window.settaArea(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                Window.settaArea(e.getMessage());
            }

            //da commentare    
            long elapsed = 0;
            long CurrentTime = System.currentTimeMillis();
            long PrecTime = System.currentTimeMillis();
            int contatore = 0;
            byte[] messageIn = new byte[size];

            while (isRunning) {
                try {
                    inputStream.readFully(messageIn, 0, size);
                    view.setConnected(true);
                    byte[] temp = new byte[height * width];
                    Common.decodeYUV420SPGrayscale(temp, messageIn, height, width);
                    
                    //image to be visualized
                    int[] frame = Common.byte2int(temp, height, width);
                    
                    
                    view.setPixel(frame);
                   
                    //frame=Common.modifiedImage(temp, height, width);
                    
//da commentare
                    byte dst[] = new byte[temp.length];

                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            Byte b = new Byte(temp[height * (143 - j) + i]);
                            dst[(width * i) + j] = b;
                        }
                    }

                    Mat m1 = new Mat(height, width, CvType.CV_8UC1);
                    m1.put(0, 0, dst);
                    
                    String filename = imagePath + File.separator
                            + numeroImmagine.toString() + "_" + //azione 
                            "original"
                            + ".bmp";
                    String filename2 = imagePath + File.separator
                            + numeroImmagine.toString() + "_" + //azione 
                            "resized"
                            + ".bmp";

//fino qui
                    
                    //Highgui.imwrite(filename2, m3);
                    numeroImmagine = numeroImmagine + 1;

                    if (Window.getAzione() != null) {
                        String azione = Window.getAzione();
                        outputStream.println(azione);
                        String[] str2 = azione.split("\\_");
                        if (str2.length != 2) {
                            System.out.println("errore nello split del nome file!!!");
                        } else {
                            if (!str2[0].equals("000")) {
                                
 //da commentare 
                    int[] frame2=Common.modifiedImage(temp, height, width,numeroImmagine);
                       
                    System.out.println(String.format("Writing %s", filename));
                    Highgui.imwrite(filename, m1);
                    int frameResized2[] = Common.getFeature3copia(frame2, width, height, bWidth, bHeight);
                        BufferedImage img2 = new BufferedImage((int) width / bWidth,
                                (int) height / bHeight, BufferedImage.TYPE_BYTE_GRAY);
                        WritableRaster raster2 = img2.getRaster();
                        raster2.setPixels(0, 0, (int) width / bWidth, (int) height / bHeight, frameResized2);
                        try {
                            File outputfile = new File(imagePath + File.separator
                                    + numeroImmagine.toString() + "_" + //azione 
                                    "resized" + str2[1]
                                    + ".bmp");
                            ImageIO.write(img2, "bmp", outputfile);
                        } catch (IOException e) {
                        }
                    
      //fino qui              
                                int frameResized[] = Common.getFeature3(frame, width, height, bWidth, bHeight);

                                
                                BufferedImage img = new BufferedImage((int) width / bWidth,
                                        (int) height / bHeight, BufferedImage.TYPE_BYTE_GRAY);
                                WritableRaster raster = img.getRaster();
                                raster.setPixels(0, 0, (int) width / bWidth, (int) height / bHeight, frameResized);
                                try {
                                    File outputfile = new File(imagePath + File.separator
                                            + numeroImmagine.toString() + "_" + //azione 
                                            "255_" + str2[1]
                                            + ".bmp");
                                    ImageIO.write(img, "bmp", outputfile);
                                } catch (IOException e) {
                                }
                                numeroImmagine = numeroImmagine + 1;

                                //modifica
                                if (Math.random() < 0.3) {
                                    imagePath = "Test";
                                } else {
                                    imagePath = "Training";
                                }
                            }
                        }
                    }

                    /*
                    long diff = System.currentTimeMillis() - PrecTime;
                    if (diff < 180) {
                        
                        try {
                            Thread.sleep(180 - diff);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }*/
                    PrecTime = System.currentTimeMillis();

                } catch (SocketTimeoutException e) {
                    System.out.println(e.getMessage());
                    Window.settaArea(e.getMessage());
                    isRunning = false;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    Window.settaArea(e.getMessage());
                    //isRunning = false;
                    if (System.currentTimeMillis() - PrecTime > 3000) {
                        isRunning = false;
                    }
                }

                elapsed = System.currentTimeMillis() - CurrentTime;

                if (elapsed < 1000) {
                    contatore = contatore + 1;
                } else {
                    System.out.println("circa " + contatore + " imm al sec");
                    contatore = 0;
                    CurrentTime = System.currentTimeMillis();
                }
            }

            //ferma auto
            outputStream.println("stop");

            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                Window.settaArea(ex.getMessage());
            }
        } catch (IOException exc) {
            //timeout accept
            System.out.println(exc.getMessage());
            Window.settaArea(exc.getMessage());
        }

        disconnect();
        // Window.settaBottone(true);
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("socket chiuso");
                Window.settaArea("socket chiuso");
            }
        } catch (IOException e) {
            System.out.println("errore nel chiudere il socket : " + e.getMessage());
            Window.settaArea(e.getMessage());
        }
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        Window.settaBottone();
        view.setConnected(false);
        //Viewer.setConnected(false, 2);
    }
}
