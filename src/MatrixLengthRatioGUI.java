import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * ข้อ 6. Matrix Length Ratio : แปลงหน่วยความยาวในระบบเมตริก
 * 10 mm = 1 cm, 100 cm = 1 m, 1,000 m = 1 km
 */
public class MatrixLengthRatioGUI extends JInternalFrame {

    private JRadioButton rdoMm, rdoCm, rdoM, rdoKm;
    private JTextField txtMm, txtCm, txtM, txtKm;
    private JButton btnConvert, btnClear, btnExit;
    private JLabel lblResult;
    private final DecimalFormat df = new DecimalFormat("#,##0.####");

    public MatrixLengthRatioGUI() {
        super("Matrix Length Ratio", true, true, true, true);
        initComponents();
        setSize(580, 520);
    }

    private void initComponents() {
        JPanel page = UiTheme.page();
        page.setLayout(new BorderLayout(12, 12));
        page.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(page);

        // ===== แถบหัวเรื่อง =====
        page.add(UiTheme.headerBar("No.6   Matrix Length Ratio",
                UiTheme.ACCENT), BorderLayout.NORTH);

        // ===== การ์ดเลือกหน่วย =====
        JPanel card = UiTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel t1 = UiTheme.title("Metric Length Units");
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(t1);
        card.add(Box.createVerticalStrut(4));
        JLabel guide = new JLabel("Select unit and enter a number   *** Number only");
        guide.setFont(UiTheme.SMALL);
        guide.setForeground(UiTheme.DANGER);
        guide.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(guide);
        card.add(Box.createVerticalStrut(12));

        ButtonGroup g = new ButtonGroup();
        rdoMm = new JRadioButton("Millimeter", true);
        rdoCm = new JRadioButton("Centimeter");
        rdoM = new JRadioButton("Meter");
        rdoKm = new JRadioButton("Kilometer");
        g.add(rdoMm); g.add(rdoCm); g.add(rdoM); g.add(rdoKm);
        rdoMm.setFont(UiTheme.UI); rdoCm.setFont(UiTheme.UI);
        rdoM.setFont(UiTheme.UI); rdoKm.setFont(UiTheme.UI);

        txtMm = new JTextField("500", 10);
        txtCm = new JTextField(10);
        txtM = new JTextField(10);
        txtKm = new JTextField(10);
        UiTheme.styleField(txtMm);
        UiTheme.styleField(txtCm);
        UiTheme.styleField(txtM);
        UiTheme.styleField(txtKm);

        // lock ตัวเลขเท่านั้น (ตัวเลข + จุดทศนิยม)
        KeyAdapter numLock = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char inp = evt.getKeyChar();
                if (!(Character.isDigit(inp) || inp == '.'
                        || inp == KeyEvent.VK_BACK_SPACE || inp == KeyEvent.VK_DELETE)) {
                    evt.consume();
                }
            }
        };
        txtMm.addKeyListener(numLock);
        txtCm.addKeyListener(numLock);
        txtM.addKeyListener(numLock);
        txtKm.addKeyListener(numLock);

        card.add(rowPanel(rdoMm, txtMm, "mm"));
        card.add(rowPanel(rdoCm, txtCm, "cm"));
        card.add(rowPanel(rdoM, txtM, "m"));
        card.add(rowPanel(rdoKm, txtKm, "km"));

        // เปิดให้กรอกเฉพาะช่องที่เลือก
        updateEnabled();
        ActionListener radioSync = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnabled();
            }
        };
        rdoMm.addActionListener(radioSync);
        rdoCm.addActionListener(radioSync);
        rdoM.addActionListener(radioSync);
        rdoKm.addActionListener(radioSync);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        btnRow.setOpaque(false);
        btnClear = UiTheme.outlineButton("Clear");
        btnConvert = UiTheme.primaryButton("Convert");
        btnRow.add(btnClear);
        btnRow.add(btnConvert);
        card.add(btnRow);

        page.add(card, BorderLayout.CENTER);

        // ===== ล่าง : ผลลัพธ์ + Exit =====
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
        lblResult.setForeground(UiTheme.ACCENT);
        resultPanel.add(lblResult, BorderLayout.CENTER);
        bottom.add(resultPanel, BorderLayout.CENTER);

        btnExit = UiTheme.dangerButton("Exit");
        JPanel exitWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        exitWrap.setOpaque(false);
        exitWrap.add(btnExit);
        bottom.add(exitWrap, BorderLayout.EAST);

        page.add(bottom, BorderLayout.SOUTH);

        btnConvert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnConvertActionPerformed(evt);
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                txtMm.setText("");
                txtCm.setText("");
                txtM.setText("");
                txtKm.setText("");
                lblResult.setText(" ");
                rdoMm.setSelected(true);
                updateEnabled();
                txtMm.requestFocus();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String message = "Do you want to exit?";
                String title2 = "Confirm exit";
                int option = JOptionPane.showConfirmDialog(
                        MatrixLengthRatioGUI.this, message, title2,
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }

    private JPanel rowPanel(JRadioButton radio, JTextField field, String unit) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 5));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        radio.setPreferredSize(new Dimension(110, 24));
        field.setPreferredSize(new Dimension(140, 36));
        p.add(radio);
        p.add(field);
        JLabel u = new JLabel(unit);
        u.setFont(UiTheme.UI_BOLD);
        u.setForeground(UiTheme.MUTED);
        p.add(u);
        return p;
    }

    private void updateEnabled() {
        txtMm.setEnabled(rdoMm.isSelected());
        txtCm.setEnabled(rdoCm.isSelected());
        txtM.setEnabled(rdoM.isSelected());
        txtKm.setEnabled(rdoKm.isSelected());
    }

    /**
     * แปลงค่าจากหน่วยต้นทางเป็น [mm, cm, m, km]
     * @param value ค่าที่ป้อน
     * @param fromUnit "mm" | "cm" | "m" | "km"
     */
    public static double[] convertMetric(double value, String fromUnit) {
        double mm;
        switch (fromUnit) {
            case "cm": mm = value * 10; break;
            case "m":  mm = value * 1000; break;
            case "km": mm = value * 1000000; break;
            case "mm":
            default:   mm = value; break;
        }
        return new double[]{mm, mm / 10.0, mm / 1000.0, mm / 1000000.0};
    }

    private void btnConvertActionPerformed(ActionEvent evt) {
        String fromUnit;
        String s;
        if (rdoMm.isSelected())      { fromUnit = "mm"; s = txtMm.getText().trim(); }
        else if (rdoCm.isSelected()) { fromUnit = "cm"; s = txtCm.getText().trim(); }
        else if (rdoM.isSelected())  { fromUnit = "m";  s = txtM.getText().trim(); }
        else                         { fromUnit = "km"; s = txtKm.getText().trim(); }

        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a number",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double v;
        try {
            v = Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numbers only (Number only)",
                    "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double[] r = convertMetric(v, fromUnit);
        lblResult.setText(df.format(v) + " " + fromUnit
                + " = " + df.format(r[1]) + " cm = "
                + df.format(r[2]) + " m = "
                + df.format(r[3]) + " km");
    }

    /** รันเดี่ยวเพื่อทดสอบ */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiTheme.setup();
                JFrame f = new JFrame("Matrix Length Ratio");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(600, 550);
                MatrixLengthRatioGUI inner = new MatrixLengthRatioGUI();
                inner.setVisible(true);
                f.add(inner);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
