package fauxtoshaupe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class MainWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener,
        KeyListener, MouseWheelListener, Runnable {

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
        _scrollpane.addMouseWheelListener(this);

        setLayout(new BorderLayout());
        add(_scrollpane, BorderLayout.CENTER);
    }

    /**
     * Initiates shortcuts for commands
     */
    private void createShortcuts(){

        _keyspressed = new Vector<>(0);

        comparator = new ComparatorInteger();
    }

    // Image display management

    /**
     * Displays the image in the work frame.
     *
     * @param image BufferedImage
     */
    private void displayImage(BufferedImage image){

        _canvasPicture = image;
        _pictureWidth = _canvasPicture.getWidth();
        _pictureHeight = _canvasPicture.getHeight();

        // Picture is displayed with its normal size
        _pictureVisibleWidth = _pictureWidth;
        _pictureVisibleHeight = _pictureHeight;

        boolean resized = false;

        // If canvas too small
        if (_pictureWidth > _canvas.getWidth() && _pictureHeight > _canvas.getHeight()) {
            _canvas.setPreferredSize(new Dimension(_pictureWidth, _pictureHeight));
            resized = true;
        }
        // If not enough large
        else if (_pictureWidth > _canvas.getWidth()) {
            _canvas.setPreferredSize(new Dimension(_pictureWidth, _canvas.getHeight()));
            resized = true;
        }
        // If not enough tall
        else if (_pictureHeight > _canvas.getHeight()) {
            _canvas.setPreferredSize(new Dimension(_canvas.getWidth(), _pictureHeight));
            resized = true;
        }
        // If canvas is too small on any part, resize canvas to image size
        if (resized)
            pack();


        centerCanvasPosition();
        _canvas.repaint();
    }

    /**
     * Set x and y canvas picture's anchor so that
     * the picture's central pixel is the canvas' central pixel.
     */
    private void centerCanvasPosition(){

        System.out.println("Center");

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

    /**
     * Zooms or dezooms picture on canvas depending on
     * the sign given in parameter. Negative sign gives zoom
     * and Positive sign gives dezoom.
     *
     * @param sign int
     */
    private void zoom(int sign){

        if (sign < 0) sign = 1;
        else          sign = -1;

        _pictureVisibleWidth += sign*(_pictureVisibleWidth/10 + 1);
        _pictureVisibleHeight += sign*(_pictureVisibleHeight/10 + 1);

        BufferedImage zoomed = new BufferedImage(_pictureVisibleWidth, _pictureVisibleHeight, Transparency.OPAQUE);
        Graphics2D g = zoomed.createGraphics();
        g.drawImage(_canvasPicture, 0, 0, _pictureVisibleWidth, _pictureVisibleHeight, null);
        g.dispose();

        _canvasPicture = zoomed;
        _canvas.repaint();
    }

    // Button push management

    @Override
    public void actionPerformed(ActionEvent e){

        String cmd = e.getActionCommand();

        if (cmd.equals("App:Exit")){
            stop(); // thread for shortcut
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
            _keyspressed.clear();
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
            stop(); // thread for shortcut
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
            _keyspressed.clear();
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
            _originalPicture = image;
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
        System.out.println(me.getButton());
    }

    @Override
    public void mouseEntered(MouseEvent me){

    }

    @Override
    public void mouseExited(MouseEvent me){

    }

    @Override
    public void mousePressed(MouseEvent me){
        // Left click
        if(me.getButton() == 1) {
            _oldXDragging = me.getX();
            _oldYDragging = me.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me){

    }

    @Override
    public void mouseDragged(MouseEvent me){

        // Left click
        _xCanvas += me.getX() - _oldXDragging;
        _yCanvas += me.getY() - _oldYDragging;

        _oldXDragging = me.getX();
        _oldYDragging = me.getY();

        _canvas.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me){

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent me){
        // If user press Ctrl
        if (_canvasPicture != null && equal(_keyspressed, new Integer[]{17})){
            zoom(me.getWheelRotation());
        } else {
            System.out.println("nope");
        }
    }

    // Key management

    @Override
    public void keyPressed(KeyEvent e){
        if (!in(_keyspressed, e.getExtendedKeyCode())) {
            _keyspressed.add(e.getExtendedKeyCode());
            _keyspressed.sort(comparator);
        }
    }

    @Override
    public void keyReleased(KeyEvent e){

        if (in(_keyspressed, e.getExtendedKeyCode())) {
            _keyspressed.remove(_keyspressed.indexOf(e.getExtendedKeyCode()));
            _keyspressed.sort(comparator);
        } else {
            System.out.println("false");
        }

    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    public void updateKeyCommand(){

        // Gets command value for key shortcut
        Integer[] keys = new Integer[_keyspressed.size()];
        for(int i = 0; i < _keyspressed.size(); i++) keys[i] = _keyspressed.get(i);

        String cmd = getShortcut(keys);

        // If shortcut exists, executes command. Else, do nothing
        if (cmd != null)
            actionPerformed(cmd);
    }

    // Other

    private boolean in(Integer[] i, Integer value){
        for(Integer k: i)
            if (k == value)
                return true;
        return false;
    }

    private boolean in(Vector<Integer> v, Integer value){
        for(Integer k: v)
            if (k == value)
                return true;
        return false;
    }

    private String getShortcut(Integer[] keys){

        // Enter shortcuts HERE

        if (equal(keys, new Integer[]{67})){
            return "View:Center";
        }

        ///////////////////////

        return null;
    }

    private boolean equal(Integer[] i, Integer[] j) {
        if (i.length != j.length)
            return false;
        for (int k = 0; k < i.length; k++) {
            if (i[k] != j[k])
                return false;
        }
        return true;
    }

    private boolean equal(Vector<Integer> v, Integer[] j) {
        if (v.size() != j.length)
            return false;
        for (int k = 0; k < v.size(); k++) {
            if (v.get(k) != j[k])
                return false;
        }
        return true;
    }

    // Runnable

    @Override
    public void run(){
        _stopRun = false;
        while(!_stopRun) {
            updateKeyCommand();
            try {
                Thread.sleep(30);
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
    private BufferedImage _canvasPicture, _originalPicture;
    private int _xCanvas, _yCanvas; // coordinates of TopLeft user canvas position
    private int _pictureWidth, _pictureHeight, _pictureVisibleWidth, _pictureVisibleHeight;

    private int _oldXDragging, _oldYDragging;

    private Vector<Integer> _keyspressed;
    private ComparatorInteger comparator;

    // thread
    private boolean _stopRun;
}
