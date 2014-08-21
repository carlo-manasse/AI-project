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
public class Layer implements Serializable{
   private Neurone[] neurons;
 
    public Layer(int size) {
        neurons = new Neurone[size];
        for (int i = 0; i < size; i++) {
            neurons[i] = new Neurone();// fittizia
            
        }
    }
 
    public Neurone getNeuron(int i) {
        return neurons[i];
    }
 
    // getters and setters
  
}
