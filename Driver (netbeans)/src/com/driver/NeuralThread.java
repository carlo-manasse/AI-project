/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;


import com.NeuralNetLibrary.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author carlo
 */
public class NeuralThread extends Thread implements Serializable {

    //private int width;
    //private int height;
    //private int[] pixels;
    private int numEpoche = 300;
    private int outputdim = 7;
    private int inputdim;
    private int inputdimFeatures;
    private int hdim = 32;
    private double epsilon = 0.3;
    private int[] nodi = new int[3];
    private NeuralNetwork net;
    private String[] strTraining;
    private String[] strTest;
    private File neuralNetworkFile = null;

    NeuralThread(double epsilon, int numEpoche, int hdim) {
        this.epsilon = epsilon;
        this.hdim = hdim;
        this.numEpoche = numEpoche;


        Window.settaArea(this.getName() + " training parameters:");
        Window.settaArea(this.getName() + " epoche " + Integer.toString(numEpoche));
        Window.settaArea(this.getName() + " hidden layer nodes " + Integer.toString(hdim));
        Window.settaArea(this.getName() + " epsilon " + Double.toString(epsilon));

    }

    private boolean selectFile() {

        boolean isPath = true;
        //File f = null;

        JFileChooser fileChooser = new JFileChooser();
        //indica che dobbiamo scegliere solo le cartelle ( se non specificato, potranno essere selezionati solo i file)
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //mostra la finestra per scegliere la cartella
        //restituisce l'intero JFileChooser.APPROVE_OPTION solo se si ha premuto su "Apri"
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println(this.getName() + " file selezionato: " + fileChooser.getSelectedFile());
            neuralNetworkFile = fileChooser.getSelectedFile();
            if (neuralNetworkFile.isFile()) {
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Sostituire il file\n" + neuralNetworkFile.getName()
                        + "?",
                        "Conferma",
                        JOptionPane.YES_NO_OPTION);
                switch (n) {
                    case JOptionPane.NO_OPTION:
                        isPath = false;
                        break;
                    case JOptionPane.CLOSED_OPTION:
                        isPath = false;
                        break;
                    case JOptionPane.YES_OPTION:
                        neuralNetworkFile.delete();
                        break;

                }
            }
        } else {
            System.out.println(this.getName() + " Operazione annullata");
            isPath = false;
        }

        return isPath;
    }

    @Override
    public void run() {

        long currentTime = System.currentTimeMillis();

        boolean isPath = selectFile();

        if (isPath) {
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
            
            double[][] featuresTraining=null;
            featuresTraining = new double[numFilesTraining][]; 
            
            double[][] featuresTest=null;
            featuresTest = new double[numFilesTest][];
            
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
                //inputdimFeatures = width + height + inputdim;
                inputdimFeatures = 8;
    
                
                
                //System.out.println("width " + width);
                //System.out.println("height " + height);
                //System.out.println("input dim " + inputdim);

                int[] pixels = new int[inputdim];

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixels[i * width + j] = img.getRGB(j, i) & 0x000000FF;
                    }
                }
                images[count] = new double[inputdim];

                for(int k=0;k<pixels.length;k++) {
                    images[count][k]=((double)pixels[k])/255;
                    if((double)pixels[k]<0)
                        System.out.println("errore");
                }
                
                //featuresTraining[count]=new double[inputdimFeatures];
                //featuresTraining[count] = Common.getCartesianMomentsFeature(pixels, width, height);
                

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

                //System.out.println("width " + width);
                //System.out.println("height " + height);
                //System.out.println("input dim " + inputdim);
                //double[][] features1 = new double[numFiles][inputdim];

                int[] pixels = new int[inputdim];

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixels[i * width + j] = img.getRGB(j, i) & 0x000000FF;
                    }
                }
                imagesTest[count] = new double[inputdim];

                //System.arraycopy(pixels, 0, imagesTest[count], 0, pixels.length);
                for(int k=0;k<pixels.length;k++) {
                    imagesTest[count][k]=((double)pixels[k])/255;
                }
                
                //featuresTest[count]=new double[inputdimFeatures];
                //featuresTest[count] = Common.getCartesianMomentsFeature(pixels, width, height);
                
            }


            nodi[0] = inputdim;
            //nodi[0] = inputdimFeatures;
            nodi[1] = hdim;
            nodi[2] = outputdim;
            net = new NeuralNetwork(epsilon, nodi);


            System.out.println(this.getName() + " training using pixels");
            pixelBasedTrain(numFilesTraining, images, numFilesTest, imagesTest);

            //System.out.println(this.getName() + " training using features");
            //featureBasedTrain(numFilesTraining, featuresTraining, numFilesTest, featuresTest);

            System.out.println(this.getName() + " saving network ...");
            saveNetwork(net, neuralNetworkFile.getAbsolutePath());
            System.out.println(this.getName() + " network saved");
        }
        System.out.println((System.currentTimeMillis() - currentTime) / 1000 + " sec");
    }

    private void pixelBasedTrain(int numFilesTraining, double[][] imagesTraining, int numFilesTest, double[][] imagesTest) {


        for (int epoca = 1; epoca < numEpoche + 1; epoca++) {

            System.out.println(this.getName() + " epoca " + epoca);

            for (int count = 0; count < numFilesTraining; count++) {
                String[] str2 = strTraining[count].split("\\.|_");

                if (str2.length != 4) {
                    System.out.println(this.getName() + " errore nello split del nome file!!!");
                    System.exit(0);
                }

                //view.sePixelsTest();
                double[] y = Common.prepareOutput(str2[1], str2[2], outputdim);
                double[] input = new double[inputdim];

                for (int j = 0; j < input.length; j++) {
                    input[j] = imagesTraining[count][j];
                }
                net.train(input, y);

                /*System.out.println("immagine selezionata "+str[count]);
                 System.out.println("azione "+str2[1]+"  "+ str2[2]);
                 System.out.print("output = ");
                 for(int k=0;k<y.length;k++) {
                 System.out.print(" "+y[k]+" ");
                 }*/
            }

            //test
            if (epoca % 100 == 0 || epoca == numEpoche) {
                System.out.println("errore di risostituzione");
                test(numFilesTraining, imagesTraining,strTraining,inputdim);
                System.out.println("errore di generalizzazione");
                test(numFilesTest, imagesTest,strTest,inputdim);
            }
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

    private void featureBasedTrain(int numFilesTraining, double [][] feature, int numFilesTest, double [][] featureTest) {
        for (int epoca = 1; epoca < numEpoche + 1; epoca++) {

            System.out.println(this.getName() + " epoca " + epoca);

            for (int count = 0; count < numFilesTraining; count++) {
                String[] str2 = strTraining[count].split("\\.|_");

                if (str2.length != 4) {
                    System.out.println(this.getName() + " errore nello split del nome file!!!");
                    System.exit(0);
                }

                //view.sePixelsTest();
                double[] y = Common.prepareOutput(str2[1], str2[2], outputdim);
                double[] input = new double[feature[0].length];

                for (int j = 0; j < input.length; j++) {
                    input[j] = feature[count][j];
                }
                net.train(input, y);

            }

            //test
            if (epoca % 100 == 0 || epoca == numEpoche) {
                System.out.println("errore di risostituzione");
                test(numFilesTraining, feature , strTraining,inputdimFeatures);
                System.out.println("errore di generalizzazione");
                test(numFilesTest, featureTest, strTest, inputdimFeatures);
            }
        }
    }
  
    public void saveNetwork(NeuralNetwork netw, String path) {
        FileOutputStream saveFile;
        try {
            saveFile = new FileOutputStream(path);
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(netw);
            save.close();
        } catch (Exception exc) {
            System.out.println(this.getName() + " Error saving network ...");
            exc.getMessage();
        }
    }
}
