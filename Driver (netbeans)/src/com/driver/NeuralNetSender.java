/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import com.NeuralNetLibrary.NeuralNetwork;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class NeuralNetSender extends Thread {

    private boolean isRunning = true;
    private Socket socket = null;
    private ServerSocket server = null;
    private OutputStream output = null;
    NeuralNetwork net = null;

    NeuralNetSender(NeuralNetwork net) {
        super();
        this.net = net;
    }

    public void stopServer() {
        isRunning = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(8900);
            System.out.println("Aperta la porta 8900...");
            Window.settaArea("Aperta la porta 8900...");
            System.out.println("In attesa della connessione...");
            Window.settaArea("In attesa della connessione...");
            server.setSoTimeout(15000);
            socket = server.accept();
            System.out.println("client connesso");
            Window.settaArea("client connesso");
            output = socket.getOutputStream();


            System.out.println("open stream");
            ObjectOutputStream send = new ObjectOutputStream(output);
            if (net != null) {
                System.out.println("send net");
                send.writeObject(net);

                System.out.println("done");
            }
            send.close();

        } catch (SocketTimeoutException e) {
            System.out.println(e.getMessage());
            Window.settaArea(e.getMessage());
            isRunning = false;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Window.settaArea(ex.getMessage());
            isRunning = false;
        }


        disconnect();

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
    }
}
