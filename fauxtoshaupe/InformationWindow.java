package fauxtoshaupe;

import javax.swing.*;

public class InformationWindow extends JOptionPane{

    public static void showError(String title, String message, MainWindow parent){
        showMessageDialog(parent, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

}
