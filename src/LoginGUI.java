import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * หน้า Login ก่อนเข้า Main Menu
 * ตรวจรหัสผ่านกับฐานข้อมูล MySQL (MySQLDB.checkLogin)
 * ปุ่ม Register เปิดหน้าสมัครสมาชิก (RegisterGUI)
 */
public class LoginGUI extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister, btnExit;

    public LoginGUI() {
        super("Login - MiniProject");
        initComponents();
        setSize(440, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        JPanel page = UiTheme.page();

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 0;
        wc.gridy = 0;
        wc.insets = new Insets(10, 10, 10, 10);

        JPanel card = UiTheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        JLabel badge = new JLabel("MINI PROJECT");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(UiTheme.PRIMARY);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        card.add(badge, c);

        JLabel title = new JLabel("Login");
        title.setFont(UiTheme.BIG);
        title.setForeground(UiTheme.TEXT);
        c.gridy = 1;
        card.add(title, c);

        JLabel sub = UiTheme.hint("Sign in with your account");
        c.gridy = 2;
        card.add(sub, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 3; c.anchor = GridBagConstraints.EAST;
        card.add(new JLabel("Username :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        txtUser = new JTextField(16);
        UiTheme.styleField(txtUser);
        card.add(txtUser, c);

        c.gridx = 0; c.gridy = 4; c.anchor = GridBagConstraints.EAST;
        card.add(new JLabel("Password :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        txtPassword = new JPasswordField(16);
        UiTheme.styleField(txtPassword);
        card.add(txtPassword, c);

        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER; c.fill = GridBagConstraints.HORIZONTAL;
        btnLogin = UiTheme.primaryButton("Login");
        card.add(btnLogin, c);
        c.gridwidth = 1; c.fill = GridBagConstraints.NONE;

        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        btnRegister = UiTheme.outlineButton("Register");
        btnExit = UiTheme.dangerButton("Exit");
        row.add(btnRegister);
        row.add(btnExit);
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        card.add(row, c);

        wrap.add(card);
        page.add(wrap, BorderLayout.CENTER);
        setContentPane(page);

        getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new RegisterGUI().setVisible(true);
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int option = JOptionPane.showConfirmDialog(LoginGUI.this,
                        "Do you want to exit?", "Confirm exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void btnLoginActionPerformed(ActionEvent evt) {
        final String inpUser = txtUser.getText().trim();
        final String inpPass = new String(txtPassword.getPassword());
        if (inpUser.isEmpty() || inpPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter user or password before login !!!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // freeSQLdatabase ตอบช้า -> ตรวจผ่าน background thread กันจอค้าง
        btnLogin.setEnabled(false);
        btnLogin.setText("Checking...");
        new SwingWorker<String, Void>() {
            private Exception error;
            @Override
            protected String doInBackground() {
                try {
                    return MySQLDB.checkLogin(inpUser, inpPass);
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }
            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                if (error != null) {
                    JOptionPane.showMessageDialog(LoginGUI.this,
                            "Cannot connect to database:\n" + error.getMessage(),
                            "Database error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String userType = get();
                    if (userType != null) {
                        new MainMenuGUI(inpUser, userType).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginGUI.this,
                                "Wrong Username and Password",
                                "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginGUI.this,
                            "Login failed: " + e.getMessage(),
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
                new LoginGUI().setVisible(true);
            }
        });
    }
}
