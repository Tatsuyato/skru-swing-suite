import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Menu แบบเดสก์ท็อป (ไอคอนสไตล์ Windows แทนเมนูบาร์เดิม)
 * - คลิกไอคอนเพื่อเปิดฟอร์มข้อ 4 / ข้อ 6 / ออก
 * - ถ้าล็อกอินเป็นอาจารย์ แสดงรูปที่มุมขวาล่างของจอ
 */
public class MainMenuGUI extends JFrame {

    private JDesktopPane dpShow;
    private final List<DesktopIcon> icons = new ArrayList<DesktopIcon>();

    ConvertTempperatureGUI frmTemp;
    MatrixLengthRatioGUI frmMetric;

    private String currentUser = "";
    private String userType = "";

    private JPanel avatarPanel;
    private JLabel avatarPhoto;
    private JLabel avatarName;

    public MainMenuGUI() {
        this("", "");
    }

    public MainMenuGUI(String username, String type) {
        super("Main Menu - MiniProject");
        this.currentUser = username;
        this.userType = type;
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if ("3".equals(userType)) {
            // ขึ้นการ์ดทันทีด้วยรูปตัวแทน ไม่ต้องรอโหลดจากเว็บ
            showTeacherAvatar(UiTheme.avatarPlaceholder(84), currentUser);
            loadTeacherAvatar();
        }
    }

    private void initComponents() {
        // ===== แถบต้อนรับ (แทนเมนูบาร์เดิม) =====
        JPanel banner = new JPanel(new BorderLayout(15, 0));
        banner.setBackground(UiTheme.PRIMARY);
        banner.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        String who = currentUser.isEmpty() ? "" : currentUser
                + "  (" + UiTheme.roleName(userType) + ")";
        JLabel welcome = new JLabel(who);
        welcome.setFont(UiTheme.THAI_B);
        welcome.setForeground(Color.WHITE);
        banner.add(welcome, BorderLayout.WEST);

        JLabel hint = new JLabel("Click an icon below to open a form");
        hint.setFont(UiTheme.SMALL);
        hint.setForeground(new Color(0xDB, 0xEA, 0xFE));

        JButton btnLogout = new UiTheme.FlatButton("Logout",
                Color.WHITE, new Color(0xEF, 0xF6, 0xFF),
                UiTheme.PRIMARY, Color.WHITE);
        btnLogout.setToolTipText("Log out and return to login screen");
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                doLogout();
            }
        });

        JPanel bannerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        bannerRight.setOpaque(false);
        bannerRight.add(hint);
        bannerRight.add(btnLogout);
        banner.add(bannerRight, BorderLayout.EAST);

        // ===== พื้นที่เดสก์ท็อป =====
        dpShow = new JDesktopPane();
        dpShow.setBackground(UiTheme.BG);

        // ไอคอนวางชิดซ้ายบน (ชั้นต่ำกว่าฟอร์มย่อย ให้ฟอร์มเปิดมาทับได้)
        addDesktopIcons();

        // คลิกพื้นที่ว่าง = ยกเลิกการเลือกไอคอน
        dpShow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                for (DesktopIcon ic : icons) {
                    ic.selected = false;
                    ic.repaint();
                }
            }
        });

        JPanel center = new JPanel(new BorderLayout());
        center.add(banner, BorderLayout.NORTH);
        center.add(dpShow, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ตำแหน่งรูปมุมขวาล่าง (ปรับตามขนาดหน้าต่าง)
        getLayeredPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionAvatar();
            }
        });
    }

    /** สร้างไอคอนบนเดสก์ท็อป */
    private void addDesktopIcons() {
        addIcon(UiTheme.tileTemperature(), "No.4  Temperature", new Runnable() {
            @Override
            public void run() {
                openTemp();
            }
        });
        addIcon(UiTheme.tileLength(), "No.6  Length Ratio", new Runnable() {
            @Override
            public void run() {
                openMetric();
            }
        });
        addIcon(UiTheme.tileLogout(), "Logout", new Runnable() {
            @Override
            public void run() {
                doLogout();
            }
        });
        addIcon(UiTheme.tileExit(), "Exit", new Runnable() {
            @Override
            public void run() {
                int option = JOptionPane.showConfirmDialog(MainMenuGUI.this,
                        "Do you want to exit?", "Confirm exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void addIcon(Image tile, String label, Runnable action) {
        DesktopIcon ic = new DesktopIcon(tile, label, action);
        int index = icons.size();
        ic.setBounds(24, 20 + index * (DesktopIcon.H + 10),
                DesktopIcon.W, DesktopIcon.H);
        icons.add(ic);
        // ชั้นต่ำกว่า DEFAULT_LAYER (0) -> ฟอร์มย่อยเปิดมาทับไอคอนได้แบบ Windows
        dpShow.add(ic, Integer.valueOf(-100));
    }

    /** ออกจากระบบ -> กลับไปหน้า Login (ฟอร์มย่อยถูกปิดพร้อมหน้าต่างนี้) */
    private void doLogout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to log out?", "Confirm logout",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            new LoginGUI().setVisible(true);
            dispose();
        }
    }

    private void openTemp() {
        if (frmTemp == null || frmTemp.isClosed()) {
            frmTemp = new ConvertTempperatureGUI();
            dpShow.add(frmTemp);
            centerFrame(frmTemp);
            frmTemp.setVisible(true);
            try { frmTemp.setSelected(true); } catch (PropertyVetoException ignored) { }
        } else {
            try {
                frmTemp.setIcon(false);
                frmTemp.setSelected(true);
                frmTemp.toFront();
            } catch (PropertyVetoException ignored) { }
        }
    }

    private void openMetric() {
        if (frmMetric == null || frmMetric.isClosed()) {
            frmMetric = new MatrixLengthRatioGUI();
            dpShow.add(frmMetric);
            centerFrame(frmMetric);
            frmMetric.setVisible(true);
            try { frmMetric.setSelected(true); } catch (PropertyVetoException ignored) { }
        } else {
            try {
                frmMetric.setIcon(false);
                frmMetric.setSelected(true);
                frmMetric.toFront();
            } catch (PropertyVetoException ignored) { }
        }
    }

    /** วางฟอร์มให้อยู่กึ่งกลางพื้นที่ทำงาน */
    private void centerFrame(JInternalFrame f) {
        int x = Math.max(0, (dpShow.getWidth() - f.getWidth()) / 2);
        int y = Math.max(0, (dpShow.getHeight() - f.getHeight()) / 2);
        f.setLocation(x, y);
    }

    // ===== รูปอาจารย์มุมขวาล่าง =====

    private void loadTeacherAvatar() {
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                try {
                    TeacherLookup.Teacher t = TeacherLookup.lookup(currentUser);
                    if (t == null) {
                        return null;
                    }
                    byte[] b = TeacherLookup.loadPhotoBytes(t);
                    BufferedImage img = (b == null) ? null
                            : ImageIO.read(new ByteArrayInputStream(b));
                    return new Object[]{t, img};
                } catch (Exception e) {
                    System.err.println("teacher avatar load error (" + currentUser
                            + "): " + e);
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Object[] r = get();
                    if (r == null) {
                        // หาไม่เจอ/โหลดไม่ได้ -> คงรูปตัวแทนไว้ (ไม่หายไปไหน)
                        System.err.println("teacher avatar: no data for ["
                                + currentUser + "], keep placeholder");
                        return;
                    }
                    TeacherLookup.Teacher t = (TeacherLookup.Teacher) r[0];
                    BufferedImage img = (BufferedImage) r[1];
                    showTeacherAvatar(img != null ? img : UiTheme.avatarPlaceholder(84),
                            t.name);
                } catch (Exception e) {
                    System.err.println("teacher avatar error: " + e);
                }
            }
        }.execute();
    }

    /** สร้าง/อัปเดตการ์ดรูปอาจารย์ (เรียกซ้ำได้ สร้างแค่ครั้งเดียว) */
    private void showTeacherAvatar(BufferedImage photo, String name) {
        Image circle = circleCrop(photo, 92);

        if (avatarPanel == null) {
            final int sh = 7;   // พื้นที่รอบการ์ดไว้วาดเงา

            avatarPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    int x = sh, y = sh;
                    int w = getWidth() - sh * 2;
                    int h = getHeight() - sh * 2;
                    int r = 18;
                    // เงานุ่มรอบการ์ด (วาดก่อนการ์ด ให้โผล่รอบนอก)
                    for (int i = 6; i >= 1; i--) {
                        g2.setColor(new Color(15, 23, 42, 7));
                        g2.fill(new RoundRectangle2D.Float(x - i, y - i + 3,
                                w + i * 2f, h + i * 2f, r + i * 2f, r + i * 2f));
                    }
                    // การ์ด
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(x, y, w, h, r, r));
                    // เส้นขอบบาง ๆ
                    g2.setColor(new Color(0xE6, 0xEC, 0xF4));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(x + .5f, y + .5f,
                            w - 1, h - 1, r, r));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            avatarPanel.setOpaque(false);
            avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
            avatarPanel.setBorder(BorderFactory.createEmptyBorder(
                    sh + 16, sh + 18, sh + 14, sh + 18));

            avatarPhoto = new JLabel(new ImageIcon(circle));
            avatarPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            avatarPanel.add(avatarPhoto);
            avatarPanel.add(Box.createVerticalStrut(10));

            avatarName = new JLabel("<html><center>" + name + "</center></html>");
            avatarName.setFont(new Font("Tahoma", Font.BOLD, 14));
            avatarName.setForeground(UiTheme.TEXT);
            avatarName.setAlignmentX(Component.CENTER_ALIGNMENT);
            avatarPanel.add(avatarName);
            avatarPanel.add(Box.createVerticalStrut(8));

            avatarPanel.add(makeRolePill());

            getLayeredPane().add(avatarPanel, Integer.valueOf(300));
            avatarPanel.setVisible(true);
        } else {
            // รูปจริงโหลดเสร็จ -> เปลี่ยนจากรูปตัวแทน
            avatarPhoto.setIcon(new ImageIcon(circle));
            avatarName.setText("<html><center>" + name + "</center></html>");
        }
        positionAvatar();
    }

    /** ป้ายสถานะแบบเม็ดยา (Teacher / Student / Admin) */
    private JLabel makeRolePill() {
        JLabel pill = new JLabel(UiTheme.roleName(userType)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(new Color(0xEF, 0xF6, 0xFF));
                g2.fillRoundRect(0, 0, getWidth(), h, h, h);
                g2.setColor(new Color(0xBF, 0xDB, 0xFE));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, h - 1, h, h);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pill.setForeground(UiTheme.PRIMARY);
        pill.setBorder(BorderFactory.createEmptyBorder(4, 12, 5, 12));
        pill.setAlignmentX(Component.CENTER_ALIGNMENT);
        return pill;
    }

    /** ตัดรูปให้เป็นวงกลม + ขอบขาว + เส้นขอบบางรอบนอก */
    private static Image circleCrop(BufferedImage photo, int size) {
        BufferedImage circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = circle.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new Ellipse2D.Float(0, 0, size, size));
        g.drawImage(photo, 0, 0, size, size, null);
        g.setClip(null);
        g.setStroke(new BasicStroke(4f));
        g.setColor(Color.WHITE);
        g.draw(new Ellipse2D.Float(2f, 2f, size - 4, size - 4));
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(0xCB, 0xD5, 0xE1));
        g.draw(new Ellipse2D.Float(0.75f, 0.75f, size - 1.5f, size - 1.5f));
        g.dispose();
        return circle;
    }

    /** วางรูปให้ชิดมุมขวาล่างของจอ */
    private void positionAvatar() {
        if (avatarPanel == null || !avatarPanel.isVisible()) {
            return;
        }
        JLayeredPane lp = getLayeredPane();
        Dimension d = avatarPanel.getPreferredSize();
        int x = lp.getWidth() - d.width - 24;
        int y = lp.getHeight() - d.height - 24;
        avatarPanel.setBounds(Math.max(0, x), Math.max(0, y), d.width, d.height);
    }

    // ===== ไอคอนเดสก์ท็อป =====

    private class DesktopIcon extends JComponent {
        static final int W = 124;
        static final int H = 122;

        private final Image tile;
        private final String label;
        private final Runnable action;
        private boolean selected;

        DesktopIcon(Image tile, String label, Runnable action) {
            this.tile = tile;
            this.label = label;
            this.action = action;
            setSize(W, H);
            setPreferredSize(new Dimension(W, H));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    // คลิกเดียวเปิดได้เลย (ง่ายต่อการใช้งาน) + ไฮไลต์ไอคอนที่เลือก
                    for (DesktopIcon ic : icons) {
                        ic.selected = false;
                    }
                    selected = true;
                    repaint();
                    action.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // กรอบไฮไลต์เมื่อถูกเลือก
            if (selected) {
                g2.setColor(new Color(0x25, 0x63, 0xEB, 36));
                g2.fillRoundRect(0, 0, W, H, 16, 16);
                g2.setColor(UiTheme.PRIMARY);
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, W - 3, H - 3, 15, 15);
            }

            // รูปไอคอน (จัดกึ่งกลางด้านบน)
            int tw = tile.getWidth(this);
            int th = tile.getHeight(this);
            g2.drawImage(tile, (W - tw) / 2, 8, null);

            // ข้อความใต้ไอคอน (ตัดคำขึ้นบรรทัดใหม่ถ้ายาว)
            g2.setFont(UiTheme.SMALL);
            FontMetrics fm = g2.getFontMetrics();
            List<String> lines = wrap(label, fm, W - 10);
            int ly = 8 + th + 6 + fm.getAscent();
            g2.setColor(UiTheme.TEXT);
            for (String line : lines) {
                int lw = fm.stringWidth(line);
                g2.drawString(line, (W - lw) / 2, ly);
                ly += fm.getHeight();
            }
            g2.dispose();
        }

        /** ตัดคำให้พอดีความกว้าง */
        private List<String> wrap(String text, FontMetrics fm, int maxW) {
            List<String> out = new ArrayList<String>();
            for (String part : text.split(" ")) {
                if (out.isEmpty()) {
                    out.add(part);
                    continue;
                }
                String last = out.get(out.size() - 1);
                String trial = last + " " + part;
                if (fm.stringWidth(trial) <= maxW) {
                    out.set(out.size() - 1, trial);
                } else {
                    out.add(part);
                }
            }
            return out;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiTheme.setup();
                new MainMenuGUI().setVisible(true);
            }
        });
    }
}
