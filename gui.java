
import javax.swing.*;

import org.w3c.dom.events.Event;

import java.awt.*;
import java.awt.event.*;

public class gui {
    public static void main(String[] args){
    }


    public void createWindow() { 
        JFrame frame = new JFrame("Simple GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel textLabel = new JLabel("Enter path to document below: ",SwingConstants.CENTER); textLabel.setPreferredSize(new Dimension(300, 100));
        frame.getContentPane().add(textLabel, BorderLayout.CENTER);

        JTextField jt = new JTextField("",SwingConstants.BOTTOM); textLabel.setPreferredSize(new Dimension(300,100));
        frame.getContentPane().add(jt, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        String var = jt.getText();
        System.out.println(var);
        

    }       

}


