package cn.ms22.interfaces;

import org.junit.Test;

import javax.swing.*;
import java.io.File;

public class OpenFile {
    /**
     *
     */
    @Test
    public void test() {
        JFileChooser fd = new JFileChooser();
        fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fd.showOpenDialog(null);
        File f = fd.getSelectedFile();
        if (f != null) {
            System.out.println(f.getPath());
        }
    }
}
