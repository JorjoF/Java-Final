import javax.swing.*;
import java.awt.event.KeyEvent;

public class GUI {
    private JPanel panel1;
    private JButton refresh;
    private JComboBox comboBox1;
    private JButton button2;

    public GUI(){
        JFrame frame = new JFrame();
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("<html><u>S</u>ettings</html>");
        menu.setMnemonic(KeyEvent.VK_S);
        menu.getAccessibleContext().setAccessibleDescription("Settings Menu");
        menuBar.add(menu);

        JMenuItem preferences = new JMenuItem("<html><u>P</u>references</html>");
        preferences.setMnemonic(KeyEvent.VK_C);
        preferences.getAccessibleContext().setAccessibleDescription("Preferences");
        menu.add(preferences);

        frame.setJMenuBar(menuBar);

        preferences.addActionListener( e->{
            new SettingsGui();

        });

        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
