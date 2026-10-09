import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * หน้าสมัครสมาชิก
 * - Student : ใส่รหัสนักศึกษา -> ตรวจสอบจากทะเบียน มรภ.สงขลา (type 2)
 * - Teacher : ใส่ชื่ออาจารย์ -> ตรวจสอบจากเว็บคณะวิทย์ + โชว์รูป (type 3)
 * ตรวจสอบผ่านแล้วจึงตั้งรหัสผ่านเพื่อสร้างบัญชีได้
 */
public class RegisterGUI extends JFrame {

    private JRadioButton rbStudent, rbTeacher;
    private JLabel lblField;
    private JTextField txtInput;
    private JButton btnVerify;
    private JLabel lblWelcome;
    private JLabel lblPhoto;
    private JPasswordField txtPass, txtConfirm;
    private JButton btnCreate, btnCancel;

    private String verifiedId = null;
    private String verifiedType = null;

    public RegisterGUI() {
        super("Register - MiniProject");
        initComponents();
        setSize(660, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel page = UiTheme.page();
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0; wc.gridy = 0;
        wc.insets = new Insets(8, 8, 8, 8);

        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(20, 0));
        card.add(buildForm(), BorderLayout.CENTER);

        // ช่องรูปอาจารย์ (ฝั่งขวา)
        JPanel photoPanel = new JPanel();
        photoPanel.setOpaque(false);
        photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
        lblPhoto = new JLabel("", SwingConstants.CENTER);
        lblPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPhoto.setPreferredSize(new Dimension(150, 200));
        lblPhoto.setMaximumSize(new Dimension(150, 200));
        lblPhoto.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(UiTheme.BORDER, 24, 1.5f),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        lblPhoto.setOpaque(true);
        lblPhoto.setBackground(UiTheme.FIELD_BG);
        lblPhoto.setForeground(UiTheme.MUTED);
        lblPhoto.setFont(UiTheme.SMALL);
        lblPhoto.setVisible(false);
        photoPanel.add(Box.createVerticalGlue());
        photoPanel.add(lblPhoto);
        photoPanel.add(Box.createVerticalGlue());
        card.add(photoPanel, BorderLayout.EAST);

        wrap.add(card);
        page.add(wrap, BorderLayout.CENTER);
        setContentPane(page);

        setCredentialsEnabled(false);
        wireEvents();
    }

    /** แบบฟอร์มด้านซ้าย */
    private JPanel buildForm() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("NEW ACCOUNT");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(UiTheme.ACCENT);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(badge);

        JLabel title = new JLabel("Register");
        title.setFont(UiTheme.BIG);
        title.setForeground(UiTheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(title);
        center.add(Box.createVerticalStrut(4));

        // เลือกประเภทผู้สมัคร
        rbStudent = new JRadioButton("Student");
        rbTeacher = new JRadioButton("Teacher");
        ButtonGroup group = new ButtonGroup();
        group.add(rbStudent);
        group.add(rbTeacher);
        rbStudent.setSelected(true);
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        rolePanel.setOpaque(false);
        rolePanel.add(rbStudent);
        rolePanel.add(rbTeacher);
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(rolePanel);
        center.add(Box.createVerticalStrut(6));

        // ช่องกรอก + ปุ่มตรวจสอบ
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblField = new JLabel("Student ID :");
        lblField.setFont(UiTheme.UI_BOLD);
        row1.add(lblField);
        txtInput = new JTextField(16);
        UiTheme.styleField(txtInput);
        row1.add(txtInput);
        btnVerify = UiTheme.primaryButton("Verify");
        row1.add(btnVerify);
        center.add(row1);

        // ป้ายแสดงชื่อที่ดึงมา
        lblWelcome = new JLabel("Please enter Student ID / Teacher Name and click Verify");
        lblWelcome.setFont(UiTheme.THAI);
        lblWelcome.setForeground(UiTheme.ACCENT);
        lblWelcome.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(UiTheme.BORDER, 18, 1.5f),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        lblWelcome.setOpaque(true);
        lblWelcome.setBackground(UiTheme.FIELD_BG);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblWelcome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        center.add(lblWelcome);
        center.add(Box.createVerticalStrut(8));

        // รหัสผ่าน
        JPanel pw = new JPanel(new GridLayout(2, 2, 8, 8));
        pw.setOpaque(false);
        pw.setAlignmentX(Component.LEFT_ALIGNMENT);
        pw.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        JLabel l1 = new JLabel("Password :");
        l1.setFont(UiTheme.UI_BOLD);
        txtPass = new JPasswordField(16);
        UiTheme.styleField(txtPass);
        JLabel l2 = new JLabel("Confirm :");
        l2.setFont(UiTheme.UI_BOLD);
        txtConfirm = new JPasswordField(16);
        UiTheme.styleField(txtConfirm);
        pw.add(l1); pw.add(txtPass);
        pw.add(l2); pw.add(txtConfirm);
        center.add(pw);
        center.add(Box.createVerticalStrut(10));

        // ปุ่ม
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCreate = UiTheme.successButton("Create Account");
        btnCancel = UiTheme.dangerButton("Cancel");
        row2.add(btnCreate);
        row2.add(btnCancel);
        center.add(row2);

        return center;
    }

    private void wireEvents() {
        rbStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                onRoleChanged();
            }
        });
        rbTeacher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                onRoleChanged();
            }
        });
        btnVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        getRootPane().setDefaultButton(btnVerify);
    }

    private boolean isTeacherMode() {
        return rbTeacher.isSelected();
    }

    private void onRoleChanged() {
        verifiedId = null;
        verifiedType = null;
        txtInput.setText("");
        txtInput.setEditable(true);
        txtPass.setText("");
        txtConfirm.setText("");
        lblPhoto.setVisible(false);
        lblPhoto.setIcon(null);
        setCredentialsEnabled(false);
        lblField.setText(isTeacherMode() ? "Teacher Name :" : "Student ID :");
        lblWelcome.setForeground(UiTheme.ACCENT);
        lblWelcome.setText("Please enter Student ID / Teacher Name and click Verify");
    }

    private void setCredentialsEnabled(boolean enabled) {
        txtPass.setEnabled(enabled);
        txtConfirm.setEnabled(enabled);
        btnCreate.setEnabled(enabled);
    }

    /** ผลลัพธ์การตรวจสอบ: ข้อความแสดง + (รูปอาจารย์) */
    private static class VerifyResult {
        String display;
        Image photo;
    }

    private void btnVerifyActionPerformed(ActionEvent evt) {
        final String input = txtInput.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    isTeacherMode() ? "Please enter Teacher Name" : "Please enter Student ID",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnVerify.setEnabled(false);
        btnVerify.setText("Checking...");
        new SwingWorker<VerifyResult, Void>() {
            private Exception error;
            @Override
            protected VerifyResult doInBackground() {
                try {
                    VerifyResult r = new VerifyResult();
                    if (isTeacherMode()) {
                        TeacherLookup.Teacher t = TeacherLookup.lookup(input);
                        if (t == null) {
                            return null;
                        }
                        verifiedId = t.id;
                        verifiedType = "3";
                        r.display = (t.position == null || t.position.isEmpty())
                                ? t.name : t.name + " (" + t.position + ")";
                        try {
                            byte[] b = TeacherLookup.loadPhotoBytes(t);
                            if (b != null) {
                                BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
                                r.photo = scaleToFit(img, 145, 200);
                            }
                        } catch (Exception e) {
                            r.photo = null;
                        }
                    } else {
                        String n = StudentLookup.lookupName(input);
                        if (n == null) {
                            return null;
                        }
                        verifiedId = input;
                        verifiedType = "2";
                        r.display = n;
                    }
                    return r;
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }
            @Override
            protected void done() {
                btnVerify.setEnabled(true);
                btnVerify.setText("Verify");
                if (error != null) {
                    JOptionPane.showMessageDialog(RegisterGUI.this,
                            "Cannot connect to server:\n" + error.getMessage(),
                            "Connection error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                VerifyResult r;
                try {
                    r = get();
                } catch (Exception e) {
                    r = null;
                }
                if (r == null) {
                    verifiedId = null;
                    verifiedType = null;
                    setCredentialsEnabled(false);
                    lblWelcome.setForeground(UiTheme.DANGER);
                    lblWelcome.setText("Not found");
                    JOptionPane.showMessageDialog(RegisterGUI.this,
                            isTeacherMode()
                                    ? "Teacher not found"
                                    : "Student ID not found in registrar system",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                txtInput.setEditable(false);
                setCredentialsEnabled(true);
                lblWelcome.setForeground(UiTheme.PRIMARY);
                if (isTeacherMode()) {
                    lblWelcome.setText("สวัสดีอาจารย์ " + r.display);
                    lblPhoto.setVisible(true);
                    if (r.photo != null) {
                        lblPhoto.setIcon(new ImageIcon(r.photo));
                        lblPhoto.setText("");
                    } else {
                        lblPhoto.setIcon(null);
                        lblPhoto.setText("No photo");
                    }
                } else {
                    lblWelcome.setText("ยินดีต้อนรับ " + r.display);
                }
                txtPass.requestFocus();
            }
        }.execute();
    }

    /** ย่อรูปให้พอดีกรอบ (รักษาสัดส่วน) */
    private Image scaleToFit(BufferedImage img, int maxW, int maxH) {
        if (img == null) {
            return null;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        if (w <= 0 || h <= 0) {
            return img;
        }
        double s = Math.min((double) maxW / w, (double) maxH / h);
        if (s >= 1) {
            return img;
        }
        return img.getScaledInstance((int) (w * s), (int) (h * s), Image.SCALE_SMOOTH);
    }

    private void btnCreateActionPerformed(ActionEvent evt) {
        if (verifiedId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please verify first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final String p1 = new String(txtPass.getPassword());
        final String p2 = new String(txtConfirm.getPassword());
        if (p1.isEmpty() || p2.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter password", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (p1.length() > 20) {
            JOptionPane.showMessageDialog(this,
                    "Password must be 20 characters or less",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match", "Warning", JOptionPane.ERROR_MESSAGE);
            txtConfirm.requestFocus();
            return;
        }
        btnCreate.setEnabled(false);
        btnCreate.setText("Saving...");
        final String id = verifiedId;
        final String type = verifiedType;
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    if (MySQLDB.userExists(id)) {
                        return "EXISTS";
                    }
                    MySQLDB.registerUser(id, p1, type);
                    return "OK";
                } catch (Exception e) {
                    return "ERR:" + e.getMessage();
                }
            }
            @Override
            protected void done() {
                btnCreate.setEnabled(true);
                btnCreate.setText("Create Account");
                try {
                    String r = get();
                    if ("OK".equals(r)) {
                        JOptionPane.showMessageDialog(RegisterGUI.this,
                                "Account created! Please login. (ID: " + id + ")",
                                "Done", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else if ("EXISTS".equals(r)) {
                        JOptionPane.showMessageDialog(RegisterGUI.this,
                                "This account is already registered",
                                "Warning", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(RegisterGUI.this,
                                "Cannot connect to database:\n" + r.substring(4),
                                "Database error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RegisterGUI.this,
                            "Register failed: " + e.getMessage(),
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiTheme.setup();
                new RegisterGUI().setVisible(true);
            }
        });
    }
}
