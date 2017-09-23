package fauxtoshaupe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class MainWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener,
        KeyListener, Runnable {

    /// Constructor

    /**
     * Default constructor for MainWindow
     */
    public MainWindow(){

        super("Fauxtoshaupe");

        createMenu();
        createCanvas();
        createShortcuts();
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();

        display();
    }

    /// Methods

    /**
     * Shows the window.
     */
    private void display(){
        pack();
        setVisible(true); // shows window
    }

    /**
     * New setSize method, requires int width and height instead of
     * Dimension object.
     *
     * @param width int
     * @param height int
     */
    private void nwSetSize(int width, int height){
        setSize(new Dimension(width, height)); // Sizes the frame
    }

    /**
     * Initiates variables for menubar and menu items.
     */
    private void createMenu(){
        
        _menubar    = new JMenuBar();
        _menuFile   = new JMenu("File");
        _menuView   = new JMenu("View");
        _menuFilter = new JMenu("Filter");

        // BUTTONS

            // File

        _menuFileOpen   = new JMenuItem("Open");
        _menuFileOpen.setActionCommand("File:Open");
        _menuFileOpen.addActionListener(this);
        _menuFile.add(_menuFileOpen);

        _menuFileSave   = new JMenuItem("Save");
        _menuFileSave.setActionCommand("File:Save");
        _menuFileSave.addActionListener(this);
        _menuFile.add(_menuFileSave);

        _menuFileSaveAs = new JMenuItem("Save as");
        _menuFileSaveAs.setActionCommand("File:SaveAs");
        _menuFileSaveAs.addActionListener(this);
        _menuFile.add(_menuFileSaveAs);

        _menuFile.addSeparator();
        _menuFileExit   = new JMenuItem("Exit");
        _menuFileExit.setActionCommand("App:Exit");
        _menuFileExit.addActionListener(this);
        _menuFile.add(_menuFileExit);

            // --- File

            // View

        _menuViewCenter = new JMenuItem("Center canvas");
        _menuViewCenter.setActionCommand("View:Center");
        _menuViewCenter.addActionListener(this);
        _menuView.add(_menuViewCenter);

        _menuViewResetSize = new JMenuItem("Set image view 100%");
        _menuViewResetSize.setActionCommand("View:ResetSize");
        _menuViewResetSize.addActionListener(this);
        _menuView.add(_menuViewResetSize);

            // --- View

        ////

        _menubar.add(_menuFile);
        _menubar.add(_menuView);
        _menubar.add(_menuFilter);
        setJMenuBar(_menubar);
    }

    /**
     * Initiates variables for canvas.
     */
    private void createCanvas(){
        _canvasPicture = null;
        _xCanvas = 0;
        _yCanvas = 0;
        _oldXDragging = 0;
        _oldYDragging = 0;

        _canvas = new JPanel(){

            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(_canvasPicture, _xCanvas, _yCanvas, null);
            }
            //private static final long serialVersionUID = 1L;
        };

        _scrollpane = new JScrollPane(_canvas);
        _canvas.setBackground(new Color(50,50,50));
        _scrollpane.addMouseListener(this);
        _scrollpane.addMouseMotionListener(this);

        setLayout(new BorderLayout());
        add(_scrollpane, BorderLayout.CENTER);
    }

    /**
     * Initiates shortcuts for commands
     */
    private void createShortcuts(){
        _shortcuts = new HashMap<Integer[], String>();
        _shortcuts.put(new Integer[]{62}, "View:Center");
        _shortcuts.put(new Integer[]{17, 62}, "View:Cente");

        _keyspressed = new Vector<>(0);

        comparator = new ComparatorInteger();
    }

    /**
     * Displays the image in the work frame.
     *
     * @param image BufferedImage
     */
    private void displayImage(BufferedImage image){

        _canvasPicture = image;
        boolean resized = false;

        if (image.getWidth() > _canvas.getWidth() && image.getHeight() > _canvas.getHeight()) {
            _canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            resized = true;
        }
        else if (image.getWidth() > _canvas.getWidth()) {
            _canvas.setPreferredSize(new Dimension(image.getWidth(), _canvas.getHeight()));
            resized = true;
        }
        else if (image.getHeight() > _canvas.getHeight()) {
            _canvas.setPreferredSize(new Dimension(_canvas.getWidth(), image.getHeight()));
            resized = true;
        }
        if (resized)
            pack();

        centerCanvasPosition();
        _canvas.repaint();
    }

    private void centerCanvasPosition(){
        int halfW = 0, halfH = 0;
        if (_canvasPicture != null){
            halfW = _canvasPicture.getWidth() / 2;
            halfH = _canvasPicture.getHeight() / 2;
        }

        System.out.println(_canvas.getWidth());
        System.out.println(_canvas.getHeight());
        _xCanvas = _canvas.getWidth() / 2 - halfW;
        _yCanvas = _canvas.getHeight() / 2 - halfH;
    }

    // Button push management

    @Override
    public void actionPerformed(ActionEvent e){

        String cmd = e.getActionCommand();

        if (cmd.equals("App:Exit")){
            dispose();
        }
        else if (cmd.equals("File:Open")){
            FileOpen();
        }
        else if (cmd.equals("File:Save")){
            FileSave();
        }
        else if (cmd.equals("File:SaveAs")){
            FileSaveAs();
        }
        else if (cmd.equals("View:Center")){
            centerCanvasPosition();
            _canvas.repaint();
        }
        else {
            InformationWindow.showError("Error on command",
                    "A command has been passed to actionPerformed but doesn't exist : "
                            + e.getActionCommand(), this);
        }

    }

    /**
     * Manual action performed method.
     * @param cmd String
     */
    public void actionPerformed(String cmd){

        if (cmd.equals("App:Exit")){
            dispose();
        }
        else if (cmd.equals("File:Open")){
            FileOpen();
        }
        else if (cmd.equals("File:Save")){
            FileSave();
        }
        else if (cmd.equals("File:SaveAs")){
            FileSaveAs();
        }
        else if (cmd.equals("View:Center")){
            centerCanvasPosition();
            _canvas.repaint();
        }
        else {
            InformationWindow.showError("Error on command",
                    "A command has been passed to actionPerformed but doesn't exist : "
                            + cmd, this);
        }

    }

    // File management

    private BufferedImage getImageFile(){
        JFileChooser jc = new JFileChooser();
        jc.setFileFilter(new ImageFileFilter());

        if (jc.showOpenDialog(this) == jc.APPROVE_OPTION){
            try {
                return ImageIO.read(jc.getSelectedFile());
            } catch(Exception e){
                InformationWindow.showError("Error", e.getMessage(), this);
            }
        }

        return null;
    }

    private String getFilePath(){
        JFileChooser jc = new JFileChooser();

        if (jc.showOpenDialog(this) == jc.APPROVE_OPTION){
            try {
                return jc.getSelectedFile().getAbsolutePath();
            } catch(Exception e){
                JOptionPane opt = new JOptionPane(e.getMessage());
                opt.showMessageDialog(this, opt.getMessage(), "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        return "";
    }

    private void FileOpen(){

        BufferedImage image = getImageFile();

        // If file selection has succeeded, opens the file
        if (image != null){
            displayImage(image);
        }
    }

    private void FileSave(){
        System.out.println("Saving work");
    }

    private void FileSaveAs(){
        String path = getFilePath();
        System.out.println("Saving work at " + path);
    }

    // Mouse event management

    @Override
    public void mouseClicked(MouseEvent me){

    }

    @Override
    public void mouseEntered(MouseEvent me){

    }

    @Override
    public void mouseExited(MouseEvent me){

    }

    @Override
    public void mousePressed(MouseEvent me){
        _oldXDragging = me.getX();
        _oldYDragging = me.getY();
    }

    @Override
    public void mouseReleased(MouseEvent me){

    }

    @Override
    public void mouseDragged(MouseEvent me){

        _xCanvas += me.getX() - _oldXDragging;
        _yCanvas += me.getY() - _oldYDragging;

        _oldXDragging = me.getX();
        _oldYDragging = me.getY();

        _canvas.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me){

    }

    // Key management

    @Override
    public void keyPressed(KeyEvent e){
        if (!_keyspressed.contains(e.getExtendedKeyCode())) {
            _keyspressed.add(e.getExtendedKeyCode());
            _keyspressed.sort(comparator);
        }
    }

    @Override
    public void keyReleased(KeyEvent e){

        if (_keyspressed.contains(e.getExtendedKeyCode())) {
            _keyspressed.remove(_keyspressed.indexOf(e.getExtendedKeyCode()));
            _keyspressed.sort(comparator);
        }

    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    public void updateKeyCommand(){
        int[] keys = toArray(_keyspressed);
        for(int i: keys)
            System.out.println(i);
        Set<Integer[]> keys2 = _shortcuts.keySet();
        for(Integer[] i: keys2){
            for(int j: i){
                System.out.println(j);
            }
            System.out.println();
        }
        String cmd = _shortcuts.get(keys);
        if (cmd != null)
            actionPerformed(cmd);
    }

    // Other

    private boolean in(Vector<Integer> v, int i){
        for (int j: v){
            if (i == j)
                return true;
        }
        return false;
    }

    private int[] toArray(Vector<Integer> v){
        int[] a = new int[v.size()];
        for(int i = 0; i < v.size(); i++){
            a[i] = v.get(i);
        }
        return a;
    }

    // Runnable

    @Override
    public void run(){
        _stopRun = false;
        while(!_stopRun) {
            updateKeyCommand();
            try {
                Thread.sleep(500);
            } catch (Exception e){
                InformationWindow.showError("Error", e.getMessage(), this);
            }
        }
    }

    public void stop(){
        _stopRun = true;
    }

    /// Attributes

    private JMenuBar _menubar; // Menu
    private JMenu _menuFile, _menuView, _menuFilter;
    private JMenuItem _menuFileOpen, _menuFileSave, _menuFileSaveAs, _menuFileExit,
        _menuViewCenter, _menuViewResetSize;

    private JPanel _canvas;
    private JScrollPane _scrollpane;
    private BufferedImage _canvasPicture;
    private int _xCanvas, _yCanvas; // coordinates of TopLeft user canvas position

    private int _oldXDragging, _oldYDragging;

    private Map<Integer[], String> _shortcuts;
    private Vector<Integer> _keyspressed;
    private ComparatorInteger comparator;
    private boolean _isAccessing;

    // thread
    private boolean _stopRun;
}
