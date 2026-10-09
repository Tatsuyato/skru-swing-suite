import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;

/**
 * ธีมกลางของโปรแกรม (สี ฟอนต์ ปุ่ม การ์ด)
 * ใช้ FlatLaf เป็นฐาน แล้วแต่ง component เองให้ดูโมเดิร์น
 */
public final class UiTheme {

    // ===== จานสี (Palette) =====
    public static final Color PRIMARY     = new Color(0x25, 0x63, 0xEB);
    public static final Color PRIMARY_DK  = new Color(0x1D, 0x4E, 0xD8);
    public static final Color ACCENT      = new Color(0x05, 0x96, 0x69);
    public static final Color ACCENT_DK   = new Color(0x04, 0x78, 0x57);
    public static final Color DANGER      = new Color(0xDC, 0x26, 0x26);
    public static final Color DANGER_DK   = new Color(0xB9, 0x1C, 0x1C);
    public static final Color BG          = new Color(0xF1, 0xF5, 0xF9);
    public static final Color CARD        = Color.WHITE;
    public static final Color TEXT        = new Color(0x0F, 0x17, 0x2A);
    public static final Color MUTED       = new Color(0x64, 0x74, 0x8B);
    public static final Color BORDER      = new Color(0xE2, 0xE8, 0xF0);
    public static final Color FIELD_BG    = new Color(0xF8, 0xFA, 0xFC);
    public static final Color FIELD_FOCUS = new Color(0xDB, 0xEA, 0xFE);

    // ===== ฟอนต์ =====
    public static final Font UI      = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font UI_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TITLE   = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BIG     = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    /** ฟอนต์สำหรับข้อความไทย (Tahoma มี glyph ไทยครบ) */
    public static final Font THAI    = new Font("Tahoma", Font.PLAIN, 15);
    public static final Font THAI_B  = new Font("Tahoma", Font.BOLD, 15);

    private UiTheme() { }

    // ===== เริ่มต้น Look & Feel =====
    public static void setup() {
        // ไม่ให้ FlatLaf โหลด native library -> ตัด WARNING
        // "A restricted method in java.lang.System has been called" (System::load)
        // ฟังก์ชันที่หายไปมีแค่ มุมหน้าต่าง Win11 / สี title bar แบบ DWM (ไม่ได้ใช้)
        if (System.getProperty("flatlaf.useNativeLibrary") == null) {
            System.setProperty("flatlaf.useNativeLibrary", "false");
        }
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Throwable t) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) { }
        }
        UIManager.put("Label.font", UI);
        UIManager.put("OptionPane.messageFont", UI);
        UIManager.put("RadioButton.font", UI);
        UIManager.put("TitledBorder.font", UI_BOLD);
    }

    // ===== ผู้ช่วยสร้าง component =====

    /** พื้นหลังหน้า (ไล่เฉดอ่อน) */
    public static JPanel page() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        return p;
    }

    /** การ์ดสีขาวมุมโค้ง */
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(
                new RoundedBorder(BORDER, 24, 1.5f),
                new EmptyBorder(18, 22, 18, 22)));
        return p;
    }

    /** แถบหัวเรื่องสีพื้น */
    public static JPanel headerBar(String text, Color bg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(TITLE);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    /** ตัวหนังสือหัวเรื่องในหน้า */
    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(TITLE);
        l.setForeground(TEXT);
        return l;
    }

    /** ตัวหนังสือเล็กสีเทา */
    public static JLabel hint(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SMALL);
        l.setForeground(MUTED);
        return l;
    }

    /** ปุ่มหลัก (น้ำเงิน) */
    public static JButton primaryButton(String text) {
        return new FlatButton(text, PRIMARY, PRIMARY_DK, Color.WHITE, null);
    }

    /** ปุ่มยืนยัน (เขียว) */
    public static JButton successButton(String text) {
        return new FlatButton(text, ACCENT, ACCENT_DK, Color.WHITE, null);
    }

    /** ปุ่มเส้นขอบ (พื้นขาวขอบน้ำเงิน) */
    public static JButton outlineButton(String text) {
        return new FlatButton(text, CARD, new Color(0xEF, 0xF6, 0xFF), PRIMARY, PRIMARY);
    }

    /** ปุ่มเส้นขอบสีแดง (ยกเลิก/ออก) */
    public static JButton dangerButton(String text) {
        return new FlatButton(text, CARD, new Color(0xFE, 0xF2, 0xF2), DANGER, DANGER);
    }

    /** แต่งช่องกรอกข้อความให้สวย */
    public static void styleField(JTextComponent f) {
        f.setFont(UI);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setOpaque(true);
        f.setBorder(new CompoundBorder(
                new RoundedBorder(BORDER, 18, 1.5f),
                new EmptyBorder(7, 12, 7, 12)));
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                ((JTextComponent) e.getSource()).setBackground(FIELD_FOCUS);
                ((JTextComponent) e.getSource()).setBorder(new CompoundBorder(
                        new RoundedBorder(PRIMARY, 18, 2f),
                        new EmptyBorder(7, 12, 7, 12)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                ((JTextComponent) e.getSource()).setBackground(FIELD_BG);
                ((JTextComponent) e.getSource()).setBorder(new CompoundBorder(
                        new RoundedBorder(BORDER, 18, 1.5f),
                        new EmptyBorder(7, 12, 7, 12)));
            }
        });
    }

    /** ตั้งพื้นหลังให้ทุก component ภายใน (ให้เข้ากับหน้า) */
    public static void tint(Component root, Color color) {
        if (root instanceof JPanel || root instanceof JFrame
                || root instanceof JDesktopPane || root instanceof JMenuBar
                || root instanceof JInternalFrame) {
            root.setBackground(color);
        }
    }

    /** รูปตัวแทน (คน) ใช้ตอนยังโหลดรูปอาจารย์จากเว็บไม่เสร็จ */
    public static BufferedImage avatarPlaceholder(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xE2, 0xE8, 0xF0));
        g.fillOval(0, 0, size, size);
        g.setColor(new Color(0x94, 0xA3, 0xB8));
        g.fillOval((int) (size * 0.34), (int) (size * 0.17),
                (int) (size * 0.32), (int) (size * 0.32));   // หัว
        g.fillOval((int) (size * 0.20), (int) (size * 0.55),
                (int) (size * 0.60), (int) (size * 0.60));   // ลำตัว
        g.dispose();
        return img;
    }

    // ===== Component พิเศษ =====

    /** ปุ่มมุมโค้ง ไล่สีตอนวางเม้าส์ */
    public static class FlatButton extends JButton {
        private final Color base;
        private final Color hover;
        private final Color textColor;
        private final Color borderColor;
        private static final int RADIUS = 14;

        public FlatButton(String text, Color base, Color hover,
                          Color textColor, Color borderColor) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.textColor = textColor;
            this.borderColor = borderColor;
            setForeground(textColor);
            setFont(UI_BOLD);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(8, 18, 8, 18));
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            String t = getText() == null ? "" : getText();
            int w = Math.max(120, fm.stringWidth(t) + 40);
            int h = Math.max(36, fm.getHeight() + 16);
            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg;
            if (!isEnabled()) {
                bg = BORDER;
            } else if (getModel().isPressed()) {
                bg = hover.darker();
            } else if (getModel().isRollover()) {
                bg = hover;
            } else {
                bg = base;
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS, RADIUS);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3,
                        RADIUS - 2, RADIUS - 2);
            }
            if (hasFocus()) {
                g2.setColor(PRIMARY);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        RADIUS, RADIUS);
            }
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? textColor : MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String t = getText();
            if (t != null) {
                int x = (getWidth() - fm.stringWidth(t)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(t, x, y);
            }
            g2.dispose();
        }
    }

    /** เส้นขอบมุมโค้ง (ใช้กับการ์ดและช่องกรอก) */
    public static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int arc;
        private final float thickness;
        private final int pad;

        public RoundedBorder(Color color, int arc, float thickness) {
            this(color, arc, thickness, 0);
        }

        public RoundedBorder(Color color, int arc, float thickness, int pad) {
            this.color = color;
            this.arc = arc;
            this.thickness = thickness;
            this.pad = pad;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(pad, pad, pad, pad);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, arc, arc);
            g2.dispose();
        }
    }

    /** ชื่อชนิด type ในฐานข้อมูล -> ข้อความแสดงผล */
    public static String roleName(String type) {
        if ("1".equals(type)) {
            return "Admin";
        }
        if ("3".equals(type)) {
            return "Teacher";
        }
        return "Student";
    }

    // ===== ไอคอนสำหรับเดสก์ท็อป (วาดเอง ไม่ต้องมีไฟล์รูป) =====

    /** ไอคอนอุณหภูมิ (ปุ่มสีน้ำเงิน) */
    public static BufferedImage tileTemperature() {
        return drawTile(new Color(0x25, 0x63, 0xEB), 1);
    }

    /** ไอคอนความยาว (ปุ่มสีเขียว) */
    public static BufferedImage tileLength() {
        return drawTile(new Color(0x05, 0x96, 0x69), 2);
    }

    /** ไอคอนออกจากระบบ (ปุ่มสีแดง) */
    public static BufferedImage tileExit() {
        return drawTile(new Color(0xDC, 0x26, 0x26), 3);
    }

    /** ไอคอน logout / กลับไปหน้าล็อกอิน (ปุ่มสีส้ม) */
    public static BufferedImage tileLogout() {
        return drawTile(new Color(0xF5, 0x9E, 0x0B), 4);
    }

    private static BufferedImage drawTile(Color bg, int glyph) {
        int s = 76;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        // พื้นสี่เหลี่ยมมุมโค้ง + ไล่เฉดอ่อนด้านบน
        GradientPaint gp = new GradientPaint(0, 0, bg.brighter(),
                0, s, bg);
        g.setPaint(gp);
        g.fillRoundRect(0, 0, s, s, 22, 22);

        int cx = s / 2;
        if (glyph == 1) {
            drawThermometer(g, cx);
        } else if (glyph == 2) {
            drawRuler(g, bg);
        } else if (glyph == 4) {
            drawLogout(g);
        } else {
            drawPower(g, cx, bg);
        }
        g.dispose();
        return img;
    }

    /** รูปเทอร์โมมิเตอร์ */
    private static void drawThermometer(Graphics2D g, int cx) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(cx - 7, 12, 14, 40, 14, 14);   // ลำตัว
        g.fillOval(cx - 13, 44, 26, 26);                // หลอดกลม
        g.setColor(new Color(0xFB, 0x92, 0x3C));         // ปรอทส้ม
        g.fillRoundRect(cx - 3, 22, 6, 32, 6, 6);
        g.fillOval(cx - 8, 50, 16, 16);
        g.setColor(Color.WHITE);                         // ขีดสเกล
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            int y = 20 + i * 9;
            g.drawLine(cx + 9, y, cx + 14, y);
        }
    }

    /** รูปไม้บรรทัด */
    private static void drawRuler(Graphics2D g, Color bg) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(8, 27, 60, 22, 10, 10);
        g.setColor(bg);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int[] lx = {18, 30, 42, 54};
        for (int x : lx) {
            g.drawLine(x, 30, x, 41);
        }
        int[] sx = {24, 36, 48, 60};
        for (int x : sx) {
            g.drawLine(x, 30, x, 35);
        }
    }

    /** รูปปุ่มเปิด/ปิด (power) */
    private static void drawPower(Graphics2D g, int cx, Color bg) {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(cx - 17, 25, 34, 34);   // วงกลม
        // เว้นช่องว่างด้านบน (ระบายทับด้วยสีพื้น)
        g.setColor(bg);
        g.setStroke(new BasicStroke(9f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.drawLine(cx, 21, cx, 33);
        // เส้นตั้งสีขาว
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx, 15, cx, 42);
    }

    /** รูป logout (ประตู + ลูกศรชี้ออก) */
    private static void drawLogout(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(22, 22, 22, 54);   // ผนังซ้าย
        g.drawLine(22, 22, 42, 22);   // ขอบบน
        g.drawLine(22, 54, 42, 54);   // ขอบล่าง
        g.drawLine(30, 38, 56, 38);   // ลำตัวลูกศร
        g.drawLine(48, 30, 56, 38);   // หัวลูกศรบน
        g.drawLine(48, 46, 56, 38);   // หัวลูกศรล่าง
    }
}
