package fauxtoshaupe;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;

public class ImageFileFilter extends FileFilter {

    public ImageFileFilter(){

    }

    /**
     * Returns the file parameter's extension in lowercase.
     *
     * @param f File
     * @return f's extension
     */
    private String getExtension(File f){
        int idx = 0;
        String name = f.getName();
        while (idx < name.length() && name.charAt(idx) != '.') idx++;

        if (idx < name.length())
            return (name.substring(idx + 1).toLowerCase());
        else
            return "";
    }

    /**
     * Returns whether elm is in elms or not.
     *
     * @param elms String[] base
     * @param elm String
     * @return elm in elms
     */
    private boolean in(String[] elms, String elm){
        for (String e: elms)
            if (e.equals(elm))
                return true;
        return false;
    }

    @Override
    public boolean accept(File f){
        String ext = getExtension(f);
        String[] exts = {"bmp", "gif", "jpe", "jpeg", "jpg", "png"};
        return ext.equals("") || in(exts, ext);
    }

    @Override
    public String getDescription(){
        return "File filter for picture files : \nBMP \nGIF \nJPE \nJPEG \nJPG \nPNG \n";
    }

}

