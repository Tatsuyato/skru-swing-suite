import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ตัวเชื่อมต่อฐานข้อมูล MySQL (freeSQLdatabase)
 * Host : sql12.freesqldatabase.com:3306
 * DB   : sql12838569
 */
public class MySQLDB {

    private static final String HOST = "sql12.freesqldatabase.com";
    private static final int PORT = 3306;
    private static final String DB = "sql12838569";
    private static final String USER = "sql12838569";
    private static final String PASS = "H6WtnPMGAu";

    private static String jdbcUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl(), USER, PASS);
    }

    /**
     * ตรวจ login คืนค่า type ("1"=admin, "2"=user)
     * คืน null ถ้า username/password ไม่ถูกต้อง
     */
    public static String checkLogin(String id, String password) throws SQLException {
        String sql = "SELECT `type` FROM `user` WHERE `id` = ? AND `password` = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("type");
                }
            }
        }
        return null;
    }

    /** ตรวจว่า username นี้มีในระบบแล้วหรือยัง */
    public static boolean userExists(String id) throws SQLException {
        String sql = "SELECT 1 FROM `user` WHERE `id` = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** สมัครสมาชิกใหม่ (type = "2" ผู้ใช้ทั่วไป/นักศึกษา) */
    public static void registerUser(String id, String password) throws SQLException {
        registerUser(id, password, "2");
    }

    /**
     * สมัครสมาชิกใหม่ ระบุ type ได้
     * 1 = admin, 2 = นักศึกษา/ผู้ใช้ทั่วไป, 3 = อาจารย์
     */
    public static void registerUser(String id, String password, String type) throws SQLException {
        String sql = "INSERT INTO `user` (`id`, `password`, `type`) VALUES (?, ?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, password);
            ps.setString(3, type);
            ps.executeUpdate();
        }
    }
}
