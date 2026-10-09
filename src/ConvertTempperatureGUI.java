import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * ข้อ 4. Convert Degree Celsius <---> Degree Fahrenheit
 * ชื่อโปรแกรมตามสเปค: ConvertTempperatureGUI (คงตัวสะกดตามเอกสาร)
 * - C --> F : F = (9/5*C) + 32
 * - F --> C : C = 5/9*(F - 32)
 */
public class ConvertTempperatureGUI extends JInternalFrame {

    private JTextField txtTemp;
    private JRadioButton rdoCtoF, rdoFtoC;
    private JButton btnConvert, btnClear, btnExit;
    private JLabel lblResult;
    private final DecimalFormat df = new DecimalFormat("#,##0.##");

    public ConvertTempperatureGUI() {
        super("Convert Degree Celsius <---> Degree Fahrenheit",
                true, true, true, true);
        initComponents();
        setSize(660, 440);
    }

    private void initComponents() {
        JPanel page = UiTheme.page();
        page.setLayout(new BorderLayout(12, 12));
        page.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(page);

        // ===== แถบหัวเรื่อง =====
        page.add(UiTheme.headerBar(
                "No.4   Convert Degree Celsius  <->  Degree Fahrenheit",
                UiTheme.PRIMARY), BorderLayout.NORTH);

        // ===== กลาง : การ์ดกรอกค่า + การ์ดเลือกเงื่อนไข =====
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setOpaque(false);

        // การ์ดซ้าย : กรอกอุณหภูมิ
        JPanel inputPanel = UiTheme.card();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        JLabel t1 = UiTheme.title("Input");
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(t1);
        inputPanel.add(Box.createVerticalStrut(6));
        JLabel lblIn = new JLabel("Please Enter Temperature :");
        lblIn.setFont(UiTheme.UI);
        lblIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(lblIn);
        inputPanel.add(Box.createVerticalStrut(6));
        txtTemp = new JTextField(10);
        UiTheme.styleField(txtTemp);
        txtTemp.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTemp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        inputPanel.add(txtTemp);
        inputPanel.add(Box.createVerticalStrut(6));
        JLabel warn = new JLabel("*** Number only");
        warn.setForeground(UiTheme.DANGER);
        warn.setFont(UiTheme.SMALL);
        warn.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(warn);
        inputPanel.add(Box.createVerticalGlue());

        // lock : พิมพ์ได้เฉพาะตัวเลข จุดทศนิยม และเครื่องหมายลบ
        txtTemp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char inp = evt.getKeyChar();
                if (!(Character.isDigit(inp) || inp == '.' || inp == '-'
                        || inp == KeyEvent.VK_BACK_SPACE || inp == KeyEvent.VK_DELETE)) {
                    evt.consume();
                }
            }
        });

        // การ์ดขวา : เลือกทางแปลง + ปุ่ม Convert
        JPanel optPanel = UiTheme.card();
        optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.Y_AXIS));
        JLabel t2 = UiTheme.title("Convert Options");
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);
        optPanel.add(t2);
        optPanel.add(Box.createVerticalStrut(8));
        rdoCtoF = new JRadioButton("C --> F (Celsius to Fahrenheit)", true);
        rdoFtoC = new JRadioButton("F --> C (Fahrenheit to Celsius)");
        rdoCtoF.setFont(UiTheme.UI);
        rdoFtoC.setFont(UiTheme.UI);
        ButtonGroup g = new ButtonGroup();
        g.add(rdoCtoF);
        g.add(rdoFtoC);
        JPanel radios = new JPanel();
        radios.setOpaque(false);
        radios.setLayout(new BoxLayout(radios, BoxLayout.Y_AXIS));
        rdoCtoF.setAlignmentX(Component.LEFT_ALIGNMENT);
        rdoFtoC.setAlignmentX(Component.LEFT_ALIGNMENT);
        radios.add(rdoCtoF);
        radios.add(Box.createVerticalStrut(8));
        radios.add(rdoFtoC);
        optPanel.add(radios);
        optPanel.add(Box.createVerticalStrut(14));
        btnConvert = UiTheme.primaryButton("Convert");
        btnConvert.setAlignmentX(Component.LEFT_ALIGNMENT);
        optPanel.add(btnConvert);
        optPanel.add(Box.createVerticalGlue());

        center.add(inputPanel);
        center.add(optPanel);
        page.add(center, BorderLayout.CENTER);

        // ===== ล่าง : ผลลัพธ์ + ปุ่ม =====
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setOpaque(false);

        JPanel resultPanel = UiTheme.card();
        resultPanel.setLayout(new BorderLayout(0, 4));
        JLabel rl = new JLabel("RESULT");
        rl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rl.setForeground(UiTheme.MUTED);
        resultPanel.add(rl, BorderLayout.NORTH);
        lblResult = new JLabel(" ");
        lblResult.setFont(UiTheme.THAI_B);
        lblResult.setForeground(UiTheme.PRIMARY);
        resultPanel.add(lblResult, BorderLayout.CENTER);
        bottom.add(resultPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        btnPanel.setOpaque(false);
        btnClear = UiTheme.outlineButton("Clear");
        btnExit = UiTheme.dangerButton("Exit");
        btnPanel.add(btnClear);
        btnPanel.add(btnExit);
        bottom.add(btnPanel, BorderLayout.EAST);

        page.add(bottom, BorderLayout.SOUTH);

        // ===== Events =====
        btnConvert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnConvertActionPerformed(evt);
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                txtTemp.setText("");
                lblResult.setText(" ");
                rdoCtoF.setSelected(true);
                txtTemp.requestFocus();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
    }

    // ===== สูตรหลัก (เรียกทดสอบได้โดยไม่ต้องเปิด GUI) =====
    public static double celsiusToFahrenheit(double c) {
        return (9.0 / 5.0 * c) + 32;
    }

    public static double fahrenheitToCelsius(double f) {
        return 5.0 / 9.0 * (f - 32);
    }

    private void btnConvertActionPerformed(ActionEvent evt) {
        String s = txtTemp.getText().trim();
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter temperature",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            txtTemp.requestFocus();
            return;
        }
        double input;
        try {
            input = Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numbers only",
                    "Warning", JOptionPane.ERROR_MESSAGE);
            txtTemp.requestFocus();
            txtTemp.selectAll();
            return;
        }
        if (rdoCtoF.isSelected()) {
            double f = celsiusToFahrenheit(input);
            lblResult.setText("Tempperature " + df.format(input)
                    + " converts from Celsius to Fahrenheit is "
                    + df.format(f) + " F");
        } else {
            double c = fahrenheitToCelsius(input);
            lblResult.setText("Tempperature " + df.format(input)
                    + " converts from Fahrenheit to Celsius is "
                    + df.format(c) + " C");
        }
    }

    private void btnExitActionPerformed(ActionEvent evt) {
        String message = "Do you want to exit?";
        String title = "Confirm exit";
        int option = JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    /** รันเดี่ยวเพื่อทดสอบ (ไม่ผ่าน MainMenu) */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiTheme.setup();
                JFrame f = new JFrame("Convert Degree Celsius <---> Degree Fahrenheit");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(680, 470);
                ConvertTempperatureGUI inner = new ConvertTempperatureGUI();
                inner.setVisible(true);
                f.add(inner);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
