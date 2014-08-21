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
public class Neurone implements Serializable{
 
    //Activation è data dalla funzione d'uscita dei nodi sul layer precedente.
    //Per i nodi del primo layer(input) è settato dalla funzione setInputs()
    double output,delta,bias;
    //Edge[] edges;
    double[] edges;
    
    Neurone() {
        this.bias=Math.random()/100000;
        //output=0.0;
        //delta=0.0;
    }
 
    public void setEdges(int EdgesNum){
        edges = new double[EdgesNum];
        for (int i = 0; i < EdgesNum; i++) {
            edges[i]=Math.random()/100000;
        }
    }
    
    public double getEdge(int index) {
        return edges[index];
    }
    
    public void setOutput(double output){
        this.output=output;
    }
    
    public double getOutput(){
        return output;
    }
    
}
