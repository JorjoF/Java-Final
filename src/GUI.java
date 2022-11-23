import com.fazecast.jSerialComm.SerialPort;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.*;
import org.jfree.data.general.DefaultValueDataset;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Scanner;

public class GUI {
    private final DefaultValueDataset dataset = new DefaultValueDataset(0);
    private SerialPort cp;
    private String dialLabel = "Light Intensity";
    private int minimumValue = 0;
    private int maximumValue = 100;
    private int majorTick = 10;
    private JPanel panel1;
    private JButton refresh;
    private JComboBox<String> portList;
    private JButton connectButton;
    private ChartPanel Gauge;


    public GUI() {
        JFrame frame = new JFrame();
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setIconImage(new ImageIcon("res/img/logo_transparent.png").getImage());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("<html><u>S</u>ettings</html>");
        menu.setMnemonic(KeyEvent.VK_S);
        menu.getAccessibleContext().setAccessibleDescription("Settings Menu");
        menuBar.add(menu);

        JMenuItem preferences = new JMenuItem("<html><u>P</u>references</html>");
        preferences.setMnemonic(KeyEvent.VK_C);
        preferences.getAccessibleContext().setAccessibleDescription("Preferences");
        menu.add(preferences);

        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portList.addItem(port.getSystemPortName());
        }

        refresh.addActionListener(e -> {
            portList.removeAllItems();
            SerialPort[] ports1 = SerialPort.getCommPorts();
            for (SerialPort port : ports1) {
                portList.addItem(port.getSystemPortName());
            }
        });

        frame.setJMenuBar(menuBar);

        preferences.addActionListener(e -> {
            settingsDialog(frame);
        });

        Gauge = buildDialPlot(dialLabel,minimumValue,maximumValue,majorTick);
        frame.add(Gauge);

        connectButton.addActionListener(e -> {
            if (connectButton.getText().equals("Connect")) {
                cp = SerialPort.getCommPort((Objects.requireNonNull(portList.getSelectedItem()).toString()));
                cp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
                if (cp.openPort()) {
                    connectButton.setText("Disconnect");
                    portList.setEnabled(false);
                    menu.setEnabled(false);
                    refresh.setEnabled(false);
                }
                Thread thread = new Thread(() -> {
                    Scanner scanner = new Scanner(cp.getInputStream());
                    while (scanner.hasNextLine()) {
                        int num = Integer.parseInt(scanner.nextLine());
                        int newNum = 0;
                        if (num != newNum) newNum = num;
                        dataset.setValue(newNum);
                    }
                });
                thread.start();
            } else {
                cp.closePort();
                portList.setEnabled(true);
                menu.setEnabled(true);
                refresh.setEnabled(true);
                connectButton.setText("Connect");
                dataset.setValue(minimumValue);
            }
        });


        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ChartPanel buildDialPlot(String fDialLabel,int fMinimumValue, int fMaximumValue, int majorTickGap) {

        DialPlot plot = new DialPlot(dataset);
        plot.setDialFrame(new StandardDialFrame());

        plot.addLayer(new DialTextAnnotation(fDialLabel));



        plot.addLayer(new DialPointer.Pin());


        DialBackground background = new DialBackground(Color.white);
        plot.setBackground(background);

        StandardDialScale scale = new StandardDialScale(fMinimumValue,
                fMaximumValue, -120, -300, majorTickGap, majorTickGap - 1);
        scale.setTickRadius(0.88);
        scale.setTickLabelOffset(0.20);
        scale.setTickLabelPaint(Color.RED);
        scale.setTickLabelFormatter(new DecimalFormat("####"));
        plot.addScale(0, scale);

        return new ChartPanel(new JFreeChart(plot)) {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 400);
            }
        };
    }

    public void settingsDialog(JFrame frame){
        JFrame frame2 = new JFrame();
        JDialog d2 = new JDialog(frame2, "Settings", Dialog.ModalityType.APPLICATION_MODAL);
        d2.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d2.setSize(300, 300);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout());
        Border border = BorderFactory.createEmptyBorder(10,10,10,10);
        settingsPanel.setBorder(border);

        JPanel inputs = new JPanel();
        inputs.setLayout(new GridLayout(4,2,10,10));

        JLabel dialLabelLabel = new JLabel("Dial Label: ");
        JTextField dialLabelInput = new JTextField();
        JLabel minimumValueLabel = new JLabel("Minimum value:");
        JSpinner minimumValueInput = new JSpinner();
        JLabel maximumValueLabel = new JLabel("Maximum value:");
        JSpinner maximumValueInput = new JSpinner();
        JLabel majorTickValueLabel = new JLabel("Major Tick value:");
        JSpinner majorTickValueInput = new JSpinner();
        inputs.add(dialLabelLabel);
        inputs.add(dialLabelInput);
        inputs.add(minimumValueLabel);
        inputs.add(minimumValueInput);
        inputs.add(maximumValueLabel);
        inputs.add(maximumValueInput);
        inputs.add(majorTickValueLabel);
        inputs.add(majorTickValueInput);
        settingsPanel.add(inputs,BorderLayout.NORTH);

        JPanel applyPanel = new JPanel();
        applyPanel.setLayout(new BorderLayout());
        JButton applyButton = new JButton("Apply");
        applyPanel.add(applyButton,BorderLayout.EAST);
        settingsPanel.add(applyPanel,BorderLayout.SOUTH);

        dialLabelInput.setText(dialLabel);
        minimumValueInput.setValue(minimumValue);
        maximumValueInput.setValue(maximumValue);
        majorTickValueInput.setValue(majorTick);

        applyButton.addActionListener(e->{
            dialLabel = dialLabelInput.getText();
            minimumValue = (int) minimumValueInput.getValue();
            maximumValue = (int) maximumValueInput.getValue();
            majorTick = (int) majorTickValueInput.getValue();
            refresh(frame);
            d2.dispose();
        });

        d2.setContentPane(settingsPanel);
        d2.setLocationRelativeTo(frame);
        d2.setResizable(false);
        d2.setVisible(true);
    }

    public void refresh(JFrame frame){
        frame.remove(Gauge);
        dataset.setValue(minimumValue);
        Gauge = buildDialPlot(dialLabel,minimumValue,maximumValue,majorTick);
        frame.add(Gauge);
        SwingUtilities.updateComponentTreeUI(frame);
    }
}



