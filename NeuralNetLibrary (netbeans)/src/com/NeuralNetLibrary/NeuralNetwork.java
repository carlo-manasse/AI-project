package com.NeuralNetLibrary;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;

/**
 *
 * @author carlo
 */
public class NeuralNetwork implements Serializable{

    private double epsilon;
    private Layer[] layers;
    private int numLayers;
    private int[] NodiPerLayer;

    //l'array NodiPerLayer contiene in posizione i il numero di nodi del layer i.
    //di conseguenza NodiPerLayer.length() è il numero di layer della rete neurale
    public NeuralNetwork(double epsilon, int[] NodiPerLayer) {
        numLayers = NodiPerLayer.length;
        if (numLayers < 2) {
            throw new IllegalArgumentException(
                    "Layer's count cannot be less than 2");
        }

        this.epsilon = epsilon;
        this.layers = new Layer[numLayers];
        this.NodiPerLayer = NodiPerLayer;

        //for(int j=0;j<NodiPerLayer.length;j++)
        //  System.out.println("Layer "+j+" = "+NodiPerLayer[j]);


        initializeNetwork();

    }

    private void initializeNetwork() {


        System.out.println("initialize network...");
        for (int i = 0; i < NodiPerLayer.length; i++) {
            layers[i] = new Layer(NodiPerLayer[i]);

            /*for (int j = 0; j < NodiPerLayer[i]; j++) {
             System.out.println("bias: layer " + i + " neurone " + " = " + layers[i].getNeuron(j).bias);
             }*/
        }
        //inizializzo i valori per i layers, tranne il primo(input) 
        for (int i = 1; i < numLayers; i++) {
            for (int j = 0; j < NodiPerLayer[i]; j++) {
                layers[i].getNeuron(j).setEdges(NodiPerLayer[i - 1]);
                // System.out.println("peso bias entrante in "+i+" "+j+" = "+layers[i].getNeuron(j).bias);
            }
        }


        /*for (int i = 1; i < numLayers; i++) {
         System.out.println("Layer "+i);
         for (int j = 0; j < NodiPerLayer[i]; j++) {
         System.out.println("Nodo "+j);
         for (int k = 0; k < NodiPerLayer[i - 1]; k++) {
         System.out.println("peso arco entrante "+k+" = "
         + layers[i].getNeuron(j).getEdge(k).weight);
         }
         }
         }*/

    }

    public void train(double[] in, double[] out) {
        // System.out.println("set input...");
        setInput(in);
        // System.out.println("get output...");
        getOutputs();
        // System.out.println("backprop...");
        back_propagation(out);

    }

    public void setInput(double[] input) {
        if (input.length != NodiPerLayer[0]) {
            System.out.println("errore! input di dimensione diversa dalla dimensione del primo layer ");
            System.exit(0);

        }

        for (int j = 0; j < NodiPerLayer[0]; j++) {
            layers[0].getNeuron(j).setOutput(input[j]);
            //if(layers[0].getNeuron(j).getOutput()<0.0) {
            //    System.out.println("valori input(layer 0)" + layers[0].getNeuron(j).getOutput());
            //}

        }
    }

    public double[] getOutputs() {
        double[] outputs = new double[NodiPerLayer[numLayers - 1]];

        //calcolo strato H e Y
        for (int i = 1; i < numLayers; i++) {
            for (int j = 0; j < NodiPerLayer[i]; j++) {
                double A = 0.0;
                for (int k = 0; k < NodiPerLayer[i - 1]; k++) {
                    A += layers[i - 1].getNeuron(k).getOutput()
                            * layers[i].getNeuron(j).edges[k];
                }
                A += layers[i].getNeuron(j).bias;
                layers[i].getNeuron(j).setOutput(activation(A));
            }
        }

        //return strato Y
        for (int i = 0; i < NodiPerLayer[numLayers - 1]; i++) {
            outputs[i] = layers[numLayers - 1].getNeuron(i).getOutput();
        }
        return outputs;
    }

    public void back_propagation(double[] D) {
        double actual, delta;
        // per ogno livello i a partire dall'ultimo
        for (int i = numLayers - 1; i > 0; i--) {
            // per ogni neurone j del livello i
            for (int j = 0; j < NodiPerLayer[i]; j++) {
                actual = layers[i].getNeuron(j).getOutput();
                // calcolo delta
                if (i == numLayers - 1) {
                    delta = (D[j] - actual) * df(actual);

                } else {
                    double error = 0.0;
                    for (int r = 0; r < NodiPerLayer[i + 1]; r++) {
                        error += layers[i + 1].getNeuron(r).delta * layers[i + 1].getNeuron(r).edges[j];
                    }

                    delta = df(actual) * error;
                }
                layers[i].getNeuron(j).delta = delta;
            }
        }


        //modifica dei pesi

        // per ogno livello i a partire dall'ultimo
        for (int i = numLayers - 1; i > 0; i--) {
            // per ogni neurone j del livello i
            for (int j = 0; j < NodiPerLayer[i]; j++) {

                // per ogni arco dal livello i-1
                for (int k = 0; k < NodiPerLayer[i - 1]; k++) {
                    // calcolo nuovo peso del dendrite
                    double w = layers[i].getNeuron(j).edges[k];
                    w += layers[i].getNeuron(j).delta * epsilon
                            * layers[i - 1].getNeuron(k).getOutput();
                    layers[i].getNeuron(j).edges[k]=w;
                }
                // aggiorno il bias per tutti i livelli tranne per l'input
                //if (i != 0){
                layers[i].getNeuron(j).bias += layers[i].getNeuron(j).delta * epsilon;

                //}
            }
        }
    }


    private double activation(double a) {
        return (1.0 / (1.0 + Math.exp(-a)));
    }

    private double df(double y) {
        return y * (1 - y);
    }
    
    
    public int[] getNodiPerLayer(){
        return NodiPerLayer;
    }
    
    public int getNumLayers(){
        return numLayers;
    }
}
