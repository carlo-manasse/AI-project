/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.driver;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Observable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

/**
 *
 * @author carlo
 */
public class JoystickFrame extends javax.swing.JFrame implements Runnable, ActionListener {

    private Thread t;
    private Controller[] ca;
    private JLabel[] labels;
    private Controller controllerSelected;
    private EventQueue queue;
    private ButtonMap buttonMap;
    private Component forwardComponent;
    private Component backComponent;
    private Component leftComponent;
    private Component rightComponent;
    private Component upServoComponent;
    private Component downServoComponent;
    private Component leftServoComponent;
    private Component rightServoComponent;
    private boolean controllerFound = false;
    private boolean isRunning = true;
    private ButtonHandler buttonHandler;

    public JoystickFrame() {

        buttonHandler = new ButtonHandler();

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        initComponents();
        Ok.setFocusable(false);
        Cancel.setFocusable(false);

        forward.setActionCommand("forward");
        back.setActionCommand("back");
        left.setActionCommand("left");
        right.setActionCommand("right");

        forward.addActionListener(this);
        back.addActionListener(this);
        left.addActionListener(this);
        right.addActionListener(this);

        upServo.setActionCommand("upServo");
        downServo.setActionCommand("downServo");
        leftServo.setActionCommand("leftServo");
        rightServo.setActionCommand("rightServo");

        upServo.addActionListener(this);
        downServo.addActionListener(this);
        leftServo.addActionListener(this);
        rightServo.addActionListener(this);

        jPanel1.setSize(jScrollPane1.getSize());

        initialize();

        this.start();

    }

    private void initialize() {
        
        System.out.println("initialize Joystick");
        System.out.println("read button map");
        this.buttonMap = new ButtonMap();
        XStream xstream = new XStream(new DomDriver());
        try {
            FileInputStream fin = new FileInputStream("buttonMap.sav");
            try (ObjectInputStream ois = new ObjectInputStream(fin)) {
                String xml = (String) ois.readObject();
                buttonMap = (ButtonMap) xstream.fromXML(xml);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        jComboBox1.removeAllItems();
        ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        int j = 0;
        for (int i = 0; i < ca.length; i++) {
            jComboBox1.insertItemAt(ca[i], i);
            if (buttonMap != null && ca[i].getName().equals(buttonMap.getContoller())) {
                controllerFound = true;
                j = i;
            }
        }
        controllerSelected = ca[j];
        jComboBox1.setSelectedIndex(j);
    }

    private void resetComponent(ButtonMap buttonMap, Component[] components) {
        
        System.out.println("reset components");
        int i;
        forwardComponent = null;
        backComponent = null;
        leftComponent = null;
        rightComponent = null;

        upServoComponent = null;
        downServoComponent = null;
        leftServoComponent = null;
        rightServoComponent = null;

        if (controllerFound) {
            i = findComponent(buttonMap.getForward(), components);
            if (i != -1) {
                forwardComponent = components[i];
            }
            i = findComponent(buttonMap.getBack(), components);
            if (i != -1) {
                backComponent = components[i];
            }
            i = findComponent(buttonMap.getLeft(), components);
            if (i != -1) {
                leftComponent = components[i];
            }
            i = findComponent(buttonMap.getRight(), components);
            if (i != -1) {
                rightComponent = components[i];
            }

            i = findComponent(buttonMap.getUpServo(), components);
            if (i != -1) {
                upServoComponent = components[i];
            }
            i = findComponent(buttonMap.getDownServo(), components);
            if (i != -1) {
                downServoComponent = components[i];
            }
            i = findComponent(buttonMap.getLeftServo(), components);
            if (i != -1) {
                leftServoComponent = components[i];
            }
            i = findComponent(buttonMap.getRightServo(), components);
            if (i != -1) {
                rightServoComponent = components[i];
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        forward = new javax.swing.JButton();
        forwardLabel = new javax.swing.JLabel();
        back = new javax.swing.JButton();
        backLabel = new javax.swing.JLabel();
        left = new javax.swing.JButton();
        rightLabel = new javax.swing.JLabel();
        leftLabel = new javax.swing.JLabel();
        right = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        downServo = new javax.swing.JButton();
        leftServo = new javax.swing.JButton();
        downServoLabel = new javax.swing.JLabel();
        rightServo = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        rightServoLabel = new javax.swing.JLabel();
        leftServoLabel = new javax.swing.JLabel();
        upServo = new javax.swing.JButton();
        upServoLabel = new javax.swing.JLabel();
        Ok = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Select input device");

        jPanel2.setMaximumSize(new java.awt.Dimension(580, 320));
        jPanel2.setMinimumSize(new java.awt.Dimension(580, 320));
        jPanel2.setPreferredSize(new java.awt.Dimension(580, 320));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );

        forward.setText("forward");
        forward.setFocusable(false);

        forwardLabel.setText("vuoto");

        back.setText("back");
        back.setFocusable(false);

        backLabel.setText("vuoto");

        left.setText("left");
        left.setFocusable(false);

        rightLabel.setText("vuoto");

        leftLabel.setText("vuoto");

        right.setText("right");
        right.setFocusable(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Action Commands");

        downServo.setText("down");
        downServo.setFocusable(false);

        leftServo.setText("left");
        leftServo.setFocusable(false);

        downServoLabel.setText("vuoto");

        rightServo.setText("right");
        rightServo.setFocusable(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Servo Commands");

        rightServoLabel.setText("vuoto");

        leftServoLabel.setText("vuoto");

        upServo.setText("up");
        upServo.setFocusable(false);

        upServoLabel.setText("vuoto");

        Ok.setText("Ok");
        Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkActionPerformed(evt);
            }
        });

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Ok, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(Cancel)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(29, 29, 29)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(right, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(left, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(forward, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(back, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addComponent(leftLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(rightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(forwardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                                        .addComponent(backLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(rightServo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(leftServo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(upServo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(downServo))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(upServoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                            .addComponent(leftServoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(rightServoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(downServoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(37, 37, 37))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 549, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(forward, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(forwardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(back, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(backLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(left, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(leftLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(right, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(upServo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(upServoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(downServo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(downServoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(leftServo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(leftServoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rightServo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rightServoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Ok)
                    .addComponent(Cancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void start() {
        
        System.out.println("Joystick thread start");
        if (t == null) {
            t = new Thread(this);
        }

        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    public void stop() {
        
        System.out.println("Joystick thread stop");
        isRunning = false;
    }

    @Override
    public void run() {

        System.out.println("Joystick thread is running");
        Event event = new Event();
        
        while (isRunning) {
            if (controllerSelected != null) {
                controllerSelected.poll();
                while (queue.getNextEvent(event)) {
                    Component comp = event.getComponent();
                    int i;
                    for (i = 0; i < controllerSelected.getComponents().length; i++) {
                        if (controllerSelected.getComponents()[i] == comp) {
                            break;
                        }
                    }
                    float value = event.getValue();
                    try{
                        buttonHandler.setAzione(comp, buttonMap, value, controllerSelected.getName());
                    }
                    catch(java.lang.NullPointerException e){
                        System.out.println(e.getMessage());
                    }
                    if (this.isShowing()) {
                        DecimalFormat df = new DecimalFormat("##.###");
                        
                        if (comp.isAnalog()) {
                            if (Math.abs(value) < comp.getDeadZone() + 0.01f) {
                                labels[i].setForeground(Color.red);
                            } else {
                                labels[i].setForeground(new Color(0, 100, 0));
                            }

                            labels[i].setText("<html>" + comp.getName() + "<br> value=" + df.format(value) + "</html>");
                        } else {
                            if (value != 0.0f) {
                                labels[i].setForeground(new Color(0, 100, 0));
                            } else {
                                labels[i].setForeground(Color.red);
                            }
                        }
                    }

                    this.repaint();
                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        System.out.println("Joystick changed");
        jComboAction();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_CancelActionPerformed

    private void OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkActionPerformed
        // TODO add your handling code here:
        if (saveButtonMap(this.buttonMap)) {
            setVisible(false);
        }
    }//GEN-LAST:event_OkActionPerformed

    private synchronized void jComboAction() {

        if (controllerSelected != (Controller) jComboBox1.getSelectedItem()) {
            controllerSelected = (Controller) jComboBox1.getSelectedItem();
            this.buttonMap = new ButtonMap();
        }
        jPanel1.removeAll();
        Component[] components = controllerSelected.getComponents();
        resetComponent(this.buttonMap, components);
        setLabels();
        labels = new JLabel[components.length];
        for (int j = 0; j < components.length; j++) {
            jPanel1.setLayout(new GridLayout((int) components.length / 5 + 1, 5));
            if (components[j].isAnalog()) {
                labels[j] = new JLabel("<html>" + components[j].getName()
                        + "<br> value=" + components[j].getDeadZone() + "</html>");
                jPanel1.add(labels[j]);
            } else {
                labels[j] = new JLabel("<html>" + components[j].getName() + "<br> </html>");
                jPanel1.add(labels[j]);
            }
            labels[j].setForeground(Color.red);
        }
        queue = controllerSelected.getEventQueue();
        jPanel1.updateUI();
        jComboBox1.setFocusable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        button.setEnabled(false);
        Event event = new Event();
        long currentTime = System.currentTimeMillis();
        while (!queue.getNextEvent(event) && System.currentTimeMillis() - currentTime < 5000) {
        }
        Component comp = event.getComponent();
        switch (e.getActionCommand()) {
            case "upServo":
                upServoComponent = comp;
                break;
            case "downServo":
                downServoComponent = comp;
                break;
            case "rightServo":
                rightServoComponent = comp;
                break;
            case "leftServo":
                leftServoComponent = comp;
                break;
            case "forward":
                forwardComponent = comp;
                break;
            case "back":
                backComponent = comp;
                break;
            case "left":
                leftComponent = comp;
                break;
            case "right":
                rightComponent = comp;
                break;
        }
        setLabels();
        button.setEnabled(true);
    }

    private void setLabels() {

        if (forwardComponent != null) {
            forwardLabel.setText(forwardComponent.getName());
        } else {
            forwardLabel.setText("vuoto");
        }

        if (backComponent != null) {
            backLabel.setText(backComponent.getName());
        } else {
            backLabel.setText("vuoto");
        }

        if (leftComponent != null) {
            leftLabel.setText(leftComponent.getName());
        } else {
            leftLabel.setText("vuoto");
        }

        if (rightComponent != null) {
            rightLabel.setText(rightComponent.getName());
        } else {
            rightLabel.setText("vuoto");
        }


        if (upServoComponent != null) {
            upServoLabel.setText(upServoComponent.getName());
        } else {
            upServoLabel.setText("vuoto");
        }

        if (downServoComponent != null) {
            downServoLabel.setText(downServoComponent.getName());
        } else {
            downServoLabel.setText("vuoto");
        }

        if (leftServoComponent != null) {
            leftServoLabel.setText(leftServoComponent.getName());
        } else {
            leftServoLabel.setText("vuoto");
        }

        if (rightServoComponent != null) {
            rightServoLabel.setText(rightServoComponent.getName());
        } else {
            rightServoLabel.setText("vuoto");
        }

    }
    
    
    private boolean saveButtonMap(ButtonMap btnMap) {


        boolean isSet = true;

        if (this.backComponent == null || this.downServoComponent == null
                || this.forwardComponent == null || this.leftComponent == null
                || this.leftServoComponent == null || this.rightComponent == null
                || this.rightServoComponent == null || this.upServoComponent == null
                || this.controllerSelected == null) {
            isSet = false;
        }

        if (isSet) {
            buttonMap.setController(controllerSelected.getName());

            buttonMap.setForward(forwardComponent.getName());
            buttonMap.setBack(backComponent.getName());
            buttonMap.setLeft(leftComponent.getName());
            buttonMap.setRight(rightComponent.getName());

            buttonMap.setDownServo(downServoComponent.getName());
            buttonMap.setUpServo(upServoComponent.getName());
            buttonMap.setLeftServo(leftServoComponent.getName());
            buttonMap.setRightServo(rightServoComponent.getName());


            XStream xstream = new XStream(new DomDriver());
            String xml = xstream.toXML(btnMap);

            FileOutputStream saveFile;
            try {
                saveFile = new FileOutputStream("buttonMap.sav");
                ObjectOutputStream save = new ObjectOutputStream(saveFile);
                save.writeObject(xml);
                save.close();
            } catch (Exception exc) {
                System.out.println("Error saving button mapping file ...");
                System.out.println(exc.getMessage());

            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "some buttons are not setted", "Dialog",
                    JOptionPane.ERROR_MESSAGE);
        }

        return isSet;

    }

    public int findComponent(String component, Component[] components) {

        int i;
        boolean trovato = false;
        for (i = 0; i < components.length; i++) {
            if (components[i].getName().equals(component)) {
                trovato = true;
                break;
            }
        }


        if (trovato) {
            return i;
        } else {
            return -1;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Cancel;
    private javax.swing.JButton Ok;
    private javax.swing.JButton back;
    private javax.swing.JLabel backLabel;
    private javax.swing.JButton downServo;
    private javax.swing.JLabel downServoLabel;
    private javax.swing.JButton forward;
    private javax.swing.JLabel forwardLabel;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton left;
    private javax.swing.JLabel leftLabel;
    private javax.swing.JButton leftServo;
    private javax.swing.JLabel leftServoLabel;
    private javax.swing.JButton right;
    private javax.swing.JLabel rightLabel;
    private javax.swing.JButton rightServo;
    private javax.swing.JLabel rightServoLabel;
    private javax.swing.JButton upServo;
    private javax.swing.JLabel upServoLabel;
    // End of variables declaration//GEN-END:variables

    public  ButtonHandler getButtonHandler() {
        return buttonHandler;
    }
}

class ButtonHandler extends Observable {

    Integer speed;
    Integer angle;

    ButtonHandler() {
        super();
        speed = new Integer(0);
        angle = new Integer(90);
    }

    public void setAzione(Component comp, ButtonMap map, float value, String controllerSelected) {
        if (controllerSelected.equals(map.getContoller())) {
            if (comp.isAnalog()) {
                if (comp.getName().equals(map.getForward())) {
                        speed = (int) (Math.abs(value) * 255);
                }
                if (comp.getName().equals(map.getLeft())) {
                    int difference = 0;
                    int valuex30 = (int) (value * 30);

                    if (Math.abs(valuex30) > 8) {
                        difference += 10;
                        if (Math.abs(valuex30) > 18) {
                            difference += 10;
                            if (Math.abs(valuex30) == 30.0) {
                                difference += 10;
                            }
                        }
                    }
                    difference = difference * (int) Math.signum(value);
                    angle = 90 + difference;
                }
            }
            //non è analogico
            else{
                if (comp.getName().equals(map.getForward())) {
                    if (value == 0.0f) {
                        speed = 0;
                    } else {
                        speed = 255;
                    }
                }
                
                if (comp.getName().equals(map.getLeft())) {
                    if (value == 0.0f) {
                        angle = 90;
                    } else {
                        angle = 60;
                    }
                }
                
                if (comp.getName().equals(map.getRight())) {
                    if (value == 0.0f) {
                        angle = 90;
                    } else {
                        angle = 120;
                    }
                }

            }
            
            String strSpeed="";
            String strAngle="";
            strAngle=String.format("%03d", angle);
            strSpeed=String.format("%03d", speed);
            
            changedState(strSpeed + "_" + strAngle);
        }
    }

    private void changedState(String azione) {
        setChanged();
        notifyObservers(azione);
    }
}
