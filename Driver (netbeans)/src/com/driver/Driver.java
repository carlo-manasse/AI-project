/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.opencv.core.Core;

/**
 *
 * @author carlo
 */
public class Driver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here java.awt.EventQueue.invokeLater(new Runnable() {
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.setProperty("jinput.useDefaultPlugin", "false");
        System.setProperty("jinput.plugins", "net.java.games.input.DirectInputEnvironmentPlugin");

        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
        
        Window win=new Window();
        win.setLocation(100, 100);
        win.setVisible(true);
    }
}
