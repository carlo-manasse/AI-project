/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import com.NeuralNetLibrary.NeuralNetwork;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 *
 * @author carlo
 */
public class TestNetwork extends Thread {

    int height = 176 / 2;
    int width = 144 / 2;
    int size = width * height;
    int inputdim;
    int outputdim;
    double[] y;
    int[] nodiPerLayer;
    NeuralNetwork net = null;
    boolean fileExist = false;
    String[] str;
    
    
    private String[] strTraining;
    private String[] strTest;

    TestNetwork() {

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
                    y = new double[outputdim];
                    fileExist = true;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Operazione annullata");
        }
    }

    private void test(int numFiles, double[][] images, String strImage[],int inputDimension) {

        
        System.out.println("test su "+numFiles+" files");
        //System.out.println("test su "+images.length+" files");
        //System.out.println("test su "+strImage.length+" files");
        double sommadiff=0;
        
        if (numFiles != 0) {
            int numCorrect = 0;
            for (int count = 0; count < numFiles; count++) {
                double[] input = new double[inputDimension];

                for (int j = 0; j < input.length; j++) {
                    input[j] =images[count][j];
                }
                net.setInput(input);
                double[] risultato = net.getOutputs();
                String action = Common.retrieveOutput(risultato);

                String[] str2 = strImage[count].split("\\.|_");
                String[] str3 = action.split("\\.|_");
                String actionDone = str2[1] + "_" + str2[2];
                
                
                if (action.equals(actionDone)) {
                    numCorrect++;
                } else {
                    int diff=Math.abs(Integer.parseInt(str2[2])-Integer.parseInt(str3[1]));
                    sommadiff+=(double) diff;
                    //System.out.println(this.getName() + " immagine selezionata " + strImage[count]);
                    //System.out.println(this.getName() + " azione calcolata " + action);
                }

            }
            
            sommadiff/=(double)(numFiles-numCorrect);
            sommadiff/=60;
            int correctPercent = (numCorrect * 100) / numFiles;
            System.out.println(this.getName() + " percentuale corretti  " + correctPercent + " %");
            System.out.println(this.getName() + " peso errore  " + sommadiff);
        }
    }


    @Override
    public void run() {
        if (fileExist) {
            File trainingDir = new File("Training");
            File testDir = new File("Test");
            strTraining = trainingDir.list();
            strTest = testDir.list();
            int numFilesTraining = strTraining.length;
            int numFilesTest = strTest.length;
            BufferedImage img = null;
            double images[][] = null;
            images = new double[numFilesTraining][];


            double imagesTest[][] = null;
            imagesTest = new double[numFilesTest][];
            
            //reading training files
            System.out.println(this.getName() + " reading " + numFilesTraining + " files for training");
            for (int count = 0; count < numFilesTraining; count++) {
                String[] str2 = strTraining[count].split("\\.|_");
                if (str2.length != 4) {
                    System.out.println(this.getName() + " errore nello split del nome file!!!");
                    System.exit(0);
                }

                System.out.println(this.getName() + " reading file " + strTraining[count]);
                File file = new File(trainingDir + File.separator + strTraining[count]);
                try {
                    img = ImageIO.read(file);
                } catch (IOException ex) {
                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
                }
                int width = img.getWidth();
                int height = img.getHeight();

                inputdim = width * height;
                int[] pixels = new int[inputdim];

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixels[i * width + j] = img.getRGB(j, i) & 0x000000FF;
                    }
                }
                
                images[count] = new double[inputdim];
                for(int k=0;k<pixels.length;k++) {
                    images[count][k]=((double)pixels[k])/255;
                }
            }

            //reading test files
            System.out.println(this.getName() + " reading " + numFilesTest + " files for test");

            for (int count = 0; count < numFilesTest; count++) {
                String[] str2 = strTest[count].split("\\.|_");
                if (str2.length != 4) {
                    System.out.println(this.getName() + " errore nello split del nome file!!!");
                    System.exit(0);
                }

                System.out.println(this.getName() + " reading file " + strTest[count]);
                File file = new File(testDir + File.separator + strTest[count]);
                try {
                    img = ImageIO.read(file);
                } catch (IOException ex) {
                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
                }
                int width = img.getWidth();
                int height = img.getHeight();
                int[] pixels = new int[inputdim];

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixels[i * width + j] = img.getRGB(j, i) & 0x000000FF;
                    }
                }
                
                imagesTest[count] = new double[inputdim];
                for(int k=0;k<pixels.length;k++) {
                    imagesTest[count][k]=((double)pixels[k])/255;
                }
            }

            
                System.out.println("errore di risostituzione");
                test(numFilesTraining, images,strTraining,inputdim);
                System.out.println("errore di generalizzazione");
                test(numFilesTest, imagesTest,strTest,inputdim);

        }
    }
}
