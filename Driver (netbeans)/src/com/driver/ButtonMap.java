/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

/**
 *
 * @author carlo
 */
public class ButtonMap{
   
    
    private String controllerSelected;
    
    private String forwardComponent;
    private String backComponent;
    private String leftComponent;
    private String rightComponent;

    private String upServoComponent;
    private String downServoComponent;
    private String leftServoComponent;
    private String rightServoComponent;

    ButtonMap(){}
    
    
    public void setController(String controllerSelected){
        this.controllerSelected=controllerSelected;
    }
    
    public String getContoller(){
        return this.controllerSelected;
    }
    
    
    public void setForward(String component){
        this.forwardComponent=component;
    }
    
    public void setBack(String component){
        this.backComponent=component;
    }

    public void setLeft(String component){
        this.leftComponent=component;
    }
    
    public void setRight(String component){
        this.rightComponent=component;
    }

    
    
    
    public void setUpServo(String component){
        this.upServoComponent=component;
    }
    
    public void setDownServo(String component){
        this.downServoComponent=component;
    }

    public void setLeftServo(String component){
        this.leftServoComponent=component;
    }
    
    public void setRightServo(String component){
        this.rightServoComponent=component;
    }


    public String getForward(){
        return this.forwardComponent;
    }

    public String getBack(){
        return this.backComponent;
    }
    
    public String getLeft(){
        return this.leftComponent;
    }
    
    public String getRight(){
        return this.rightComponent;
    }
    
    
    
    public String getUpServo(){
       return this.upServoComponent;
    }
     
    public String getDownServo(){
       return this.downServoComponent;
    }
  
    
    public String getLeftServo(){
       return this.leftServoComponent;
    }
    
    public String getRightServo(){
       return this.rightServoComponent;
    }
  
    
    public boolean isSet(){
    
        boolean isSet=true;

        if(     this.backComponent==null || this.downServoComponent == null ||
                this.forwardComponent==null || this.leftComponent == null ||
                this.leftServoComponent == null || this.rightComponent ==null ||
                this.rightServoComponent ==null || this.upServoComponent ==null 
                //|| this.controllerSelected ==null
                ) {
            isSet=false;
        }
        
        return isSet;
    }
    
    
}
