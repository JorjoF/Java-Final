import javax.swing.*;

public class SettingsGui {

    private JButton button1;
    private JPanel panel1;

    public SettingsGui(){
        JFrame frame = new JFrame();
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
