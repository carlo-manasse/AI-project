/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import com.NeuralNetLibrary.*;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author carlo
 */
public class AutoModeThread extends Thread {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    /**
     *
     * @author carlo
     */
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream inputStream = null;
    private PrintWriter outputStream = null;
    private int size = 38016;
    private int scale = 16;
    private int width = 144;
    private int height = 176;
    private boolean isRunning = true;
    private Viewer view;
    int inputdim;
    int outputdim;
    int[] nodiPerLayer;
    NeuralNetwork net = null;
    boolean fileExist = false;
    String[] str;
    Integer numeroImmagine = 0;

    public void stopServer() {
        isRunning = false;
    }

    AutoModeThread(Viewer view) {
        super();
        System.out.println(com.NeuralNetLibrary.NeuralNetwork.class);
        this.view = view;
        System.out.println("read net");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("File selezionato: " + fileChooser.getSelectedFile());
            try {
                FileInputStream fin = new FileInputStream(fileChooser.getSelectedFile());
                try (ObjectInputStream ois = new ObjectInputStream(fin)) {
                    net = (NeuralNetwork) ois.readObject();
                    nodiPerLayer = net.getNodiPerLayer();
                    inputdim = nodiPerLayer[0];
                    outputdim = nodiPerLayer[nodiPerLayer.length - 1];
                    fileExist = true;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata");
        }

        if (fileExist) {
            try {
                server = new ServerSocket(8900);
                System.out.println("Aperta la porta 8900...");
                Window.settaArea("Aperta la porta 8900...");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                Window.settaArea(ex.getMessage());
            }
        }
    }

    @Override
    public void run() {
        long PrecTime = System.currentTimeMillis();
        if (fileExist) {
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
                int contatore = 0;
                byte[] messageIn = new byte[size];

                while (isRunning) {
                    try {
                        inputStream.readFully(messageIn, 0, size);
                        view.setConnected(true);
                        byte[] temp = new byte[height * width];
                        Common.decodeYUV420SPGrayscale(temp, messageIn, height, width);
                        int[] frame = Common.byte2int(temp, height, width);

                        //frame=Common.modifiedImage(temp, height, width);
                        view.setPixel(frame);

                        int frameResized[] = Common.getFeature3(frame, width, height, scale, scale);//Common.averageImage(frame, 2, width, height);

                        numeroImmagine = numeroImmagine + 1;

                        double[] input = new double[inputdim];

                        //normalizzazione input
                        for (int j = 0; j < input.length; j++) {
                            input[j] = (double) frameResized[j] / 255;
                        }

                        net.setInput(input);
                        double[] risultato = net.getOutputs();
                        String action = Common.retrieveOutput(risultato);

                        outputStream.println(action);
                        Window.settaLabel(action);
                        PrecTime = System.currentTimeMillis();
                    } catch (SocketTimeoutException e) {
                        System.out.println(e.getMessage());
                        Window.settaArea(e.getMessage());
                        isRunning = false;
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        Window.settaArea(e.getMessage());
                        if (System.currentTimeMillis() - PrecTime > 3000) {
                            isRunning = false;
                        }
                    }

                    //da commentare
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
        }


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
            Logger.getLogger(com.driver.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        Window.settaBottone();
        view.setConnected(false);
    }
}
