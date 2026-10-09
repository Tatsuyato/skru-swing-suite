import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * ดึงชื่อ-นามสกุลนักศึกษาจากระบบทะเบียน มรภ.สงขลา
 * โดย POST ไปที่ https://reg.skru.ac.th/registrar/learn_time.asp
 * หน้าตอบกลับเป็น windows-874 (TIS-620) และชื่อจะฝังอยู่ใน
 * พารามิเตอร์ studentname ของลิงก์ (URL-encoded)
 */
public class StudentLookup {

    private static final String PAGE = "https://reg.skru.ac.th/registrar/learn_time.asp";
    private static final Charset TIS620 = Charset.forName("windows-874");
    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36";

    /**
     * @return ชื่อ-นามสกุล (มีคำนำหน้า) เช่น "นาย ชยางกูร สุวิชญางกูร"
     *         หรือ null ถ้าไม่พบรหัสนักศึกษานี้
     */
    public static String lookupName(String studentId) throws IOException {
        CookieHandler.setDefault(new CookieManager());

        HttpURLConnection g = (HttpURLConnection) new URL(PAGE).openConnection();
        g.setRequestProperty("User-Agent", UA);
        g.setConnectTimeout(15000);
        g.setReadTimeout(20000);
        try { readAll(g.getInputStream()); } catch (IOException ignore) { }

        String body = "f_cmd=1"
                + "&f_studentcode=" + URLEncoder.encode(studentId, "UTF-8")
                + "&f_studentname="
                + "&f_studentsurname="
                + "&f_studentstatus=all"
                + "&f_facultyid=all"
                + "&f_maxrows=25";

        HttpURLConnection p = (HttpURLConnection) new URL(PAGE + "?backto=home").openConnection();
        p.setRequestMethod("POST");
        p.setDoOutput(true);
        p.setRequestProperty("User-Agent", UA);
        p.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        p.setRequestProperty("Referer", PAGE);
        p.setConnectTimeout(15000);
        p.setReadTimeout(20000);
        try (OutputStream os = p.getOutputStream()) {
            os.write(body.getBytes("US-ASCII"));
        }
        String html = new String(readAll(p.getInputStream()), TIS620);
        p.disconnect();

        return extractName(html);
    }

    /** ดึงค่าพารามิเตอร์ studentname=... ออกจาก HTML แล้ว decode เป็นข้อความไทย */
    private static String extractName(String html) {
        int i = html.indexOf("studentname=");
        if (i < 0) {
            return null; // "ไม่พบข้อมูล" -> ไม่มีชื่ออยู่ในหน้า
        }
        int start = i + "studentname=".length();
        int end = html.indexOf('>', start);
        if (end < 0) {
            end = html.indexOf('&', start);
        }
        if (end < 0) {
            end = html.length();
        }
        String enc = html.substring(start, end);
        if (enc.isEmpty()) {
            return null;
        }
        String name = urlDecodeThai(enc).trim();
        return name.isEmpty() ? null : name;
    }

    private static String urlDecodeThai(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '+') {
                bos.write(' ');
            } else if (c == '%' && i + 2 < s.length()) {
                try {
                    bos.write(Integer.parseInt(s.substring(i + 1, i + 3), 16));
                    i += 2;
                } catch (NumberFormatException e) {
                    bos.write(c);
                }
            } else {
                bos.write(c);
            }
        }
        return new String(bos.toByteArray(), TIS620);
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}
