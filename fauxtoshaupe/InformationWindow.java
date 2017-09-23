package fauxtoshaupe;

import javax.swing.*;

public class InformationWindow {

    public static void showError(String title, String message, JFrame parent){
        JOptionPane opt = new JOptionPane(message);
        opt.showMessageDialog(parent, opt.getMessage(), title,
                JOptionPane.WARNING_MESSAGE);
    }

}
