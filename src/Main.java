import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        try{
            UIManager.setLookAndFeel(new FlatDarkLaf());
        }catch (UnsupportedLookAndFeelException e){
            JOptionPane.showMessageDialog(new JFrame(), "Error theme not supported.", "Theme error",JOptionPane.ERROR_MESSAGE);
        }

        new GUI();
    }
}