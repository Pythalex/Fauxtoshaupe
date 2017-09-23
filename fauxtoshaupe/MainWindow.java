package fauxtoshaupe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainWindow extends JFrame {

    /// Constructor

    /**
     * Default constructor for MainWindow
     */
    public MainWindow(){
        super("Fauxtoshaupe");
        
        createMenu();

        display();
    }

    /// Methods

    private void display(){
        nwSetSize(500, 400);
        setVisible(true); // shows window
    }

    private void nwSetSize(int width, int height){
        setSize(new Dimension(width, height)); // Sizes the frame
    }
    
    private void createMenu(){
        
        _menubar = new JMenuBar();
        _menu = new JMenu();
        _menu.setMnemonic(KeyEvent.VK_A);
        _menu.getAccessibleContext().setAccessibleDescription("File");
        
        _menubar.add(_menu);
        
        setJMenuBar(_menubar);
    }

    /// Attributes

    private JMenuBar _menubar; // Menu
    private JMenu _menu;
    private JMenuItem _menuItem;
    
}
