import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * ตรวจสอบอาจารย์จากหน้าเว็บ https://sci.skru.ac.th/about/academic_staff.php
 * ซึ่งเป็นรายการบุคลากรสายวิชาการ (ชื่อ + ตำแหน่ง + อีเมล @skru.ac.th)
 * login id ของอาจารย์ = ส่วนหน้าของอีเมล เช่น jaksit.ol@skru.ac.th -> jaksit.ol
 */
public class TeacherLookup {

    private static final String PAGE = "https://sci.skru.ac.th/about/academic_staff.php";
    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36";

    private static final String[] TITLES = {
            "ศาสตราจารย์ ดร.", "รองศาสตราจารย์ ดร.", "ผู้ช่วยศาสตราจารย์ ดร.",
            "ศาสตราจารย์", "รองศาสตราจารย์", "ผู้ช่วยศาสตราจารย์",
            "อาจารย์ ดร.", "อาจารย์"
    };

    // cache ในหน่วยความจำ: ตอนสมัครโหลดไปแล้ว ตอน login เอาไปใช้ได้ทันที
    private static final Map<String, Teacher> LOOKUP_CACHE = new HashMap<String, Teacher>();
    private static final Map<String, byte[]> PHOTO_CACHE = new HashMap<String, byte[]>();

    /** ข้อมูลอาจารย์ที่พบ */
    public static class Teacher {
        public String name;
        public String position;
        public String email;
        public String id;
        public String photoUrl;

        Teacher(String name, String position, String email, String photoUrl) {
            this.name = name;
            this.position = position;
            this.email = email;
            this.photoUrl = photoUrl;
            this.id = (email != null && email.indexOf('@') > 0)
                    ? email.substring(0, email.indexOf('@')) : "";
        }
    }

    /**
     * ค้นหาอาจารย์ด้วยชื่อ หรือ id/อีเมล เช่น "aumnat.to"
     * พิมพ์ชื่อมี/ไม่มีคำนำหน้า หรือคำนำหน้าต่างกันก็เจอ
     * @return ข้อมูลอาจารย์ หรือ null ถ้าไม่พบ
     */
    public static Teacher lookup(String keyword) throws IOException {
        String key = normalize(keyword);
        if (key.isEmpty()) {
            return null;
        }
        // ถ้าค้นไปแล้ว (เช่น ตอนสมัคร) เอากลับมาใช้ได้เลย ไม่ต้องยิงเว็บซ้ำ
        String cacheKey = key.toLowerCase();
        Teacher hit = LOOKUP_CACHE.get(cacheKey);
        if (hit != null) {
            return hit;
        }
        Teacher t = find(key);
        if (t != null) {
            LOOKUP_CACHE.put(cacheKey, t);
            if (t.id != null && !t.id.isEmpty()) {
                LOOKUP_CACHE.put(t.id.toLowerCase(), t);
            }
            if (t.email != null && !t.email.isEmpty()) {
                LOOKUP_CACHE.put(t.email.toLowerCase(), t);
            }
        }
        return t;
    }

    /** ค้นอาจารย์จากหน้าเว็บ (ยังไม่ใช้ cache) */
    private static Teacher find(String key) throws IOException {
        String html = fetch();
        List<Teacher> all = parse(html);
        String keyCore = stripTitle(key);

        // ค้นด้วย id หรืออีเมล (aumnat.to)
        for (Teacher t : all) {
            if (t.id.equalsIgnoreCase(key) || t.email.equalsIgnoreCase(key)) {
                return t;
            }
        }
        // ค้นด้วยชื่อเต็ม
        for (Teacher t : all) {
            String n = normalize(t.name);
            if (n.equals(key)) {
                return t;
            }
        }
        // ค้นด้วยชื่อโดยไม่สนใจคำนำหน้า (อาจารย์/ผศ.ดร. ต่างกันก็ได้)
        for (Teacher t : all) {
            String core = stripTitle(normalize(t.name));
            if (core.equals(keyCore)) {
                return t;
            }
        }
        // ค้นแบบคำบางส่วน (เฉพาะชื่อ ยาวพอ)
        if (keyCore.length() >= 4) {
            for (Teacher t : all) {
                String core = stripTitle(normalize(t.name));
                if (core.contains(keyCore) || keyCore.contains(core)) {
                    return t;
                }
            }
        }
        return null;
    }

    private static String fetch() throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(PAGE).openConnection();
        c.setRequestProperty("User-Agent", UA);
        c.setConnectTimeout(15000);
        c.setReadTimeout(20000);
        trustAllHttps(c);
        return new String(readAll(c.getInputStream()), StandardCharsets.UTF_8);
    }

    /** หน้าเว็บนี้ cert ไม่ครบ chain -> ยอมรับ cert ทุกใบเฉพาะการเชื่อมนี้ */
    private static void trustAllHttps(HttpURLConnection c) {
        if (!(c instanceof HttpsURLConnection)) {
            return;
        }
        try {
            TrustManager[] tm = { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] x, String a) { }
                public void checkServerTrusted(X509Certificate[] x, String a) { }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, tm, new SecureRandom());
            HttpsURLConnection h = (HttpsURLConnection) c;
            h.setSSLSocketFactory(sc.getSocketFactory());
            h.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String host, SSLSession s) { return true; }
            });
        } catch (Exception ignore) { }
    }

    /** หา "ชื่อ | ตำแหน่ง | อีเมล | รูป" จากทุกการ์ดอาจารย์ในหน้า */
    private static List<Teacher> parse(String html) {
        List<Teacher> result = new ArrayList<>();
        Pattern div = Pattern.compile("<div([^>]*align=\"center\"[^>]*)>(.*?)</div>", Pattern.DOTALL);
        Matcher m = div.matcher(html);

        String lastName = null;
        String lastPos = "";
        String lastPhoto = null;
        boolean lastIsPositionDiv = false;
        Pattern emailP = Pattern.compile("[\\w.+-]+@skru\\.ac\\.th");
        Pattern srcP = Pattern.compile("(?i)src\\s*=\\s*['\"]?([^'\"\\s>]+)");

        while (m.find()) {
            String tag = m.group(1);
            String raw = m.group(2);
            boolean isPosition = tag.contains("text-danger");
            String text = raw.replaceAll("<[^>]+>", "").trim();
            text = normalize(text);

            if (raw.contains("fa fa-envelope")) {
                Matcher em = emailP.matcher(raw);
                if (em.find() && lastName != null) {
                    result.add(new Teacher(lastName,
                            lastIsPositionDiv ? lastPos : "",
                            em.group(),
                            lastPhoto));
                }
                lastName = null;
                lastIsPositionDiv = false;
                lastPhoto = null;
            } else if (raw.contains("<img")) {
                Matcher sm = srcP.matcher(raw);
                if (sm.find()) {
                    lastPhoto = absoluteUrl(sm.group(1));
                }
                lastIsPositionDiv = false;
            } else if (isPosition) {
                lastPos = text;
                lastIsPositionDiv = true;
            } else if (!text.isEmpty()) {
                lastName = text; // div ถัดไปที่ไม่มีรูป/อีเมล คือชื่อ
                lastIsPositionDiv = false;
            }
        }
        return result;
    }

    /** แปลง relative path เช่น ../mis/picteacher/jaksit.png เป็น URL เต็ม */
    private static String absoluteUrl(String rel) {
        if (rel.startsWith("http://") || rel.startsWith("https://")) {
            return rel;
        }
        String p = rel;
        while (p.startsWith("../")) {
            p = p.substring(3);
        }
        while (p.startsWith("./")) {
            p = p.substring(2);
        }
        return "https://sci.skru.ac.th/" + p;
    }

    /** รับไฟล์รูปอาจารย์มาเป็น byte[] (null ถ้าไม่มี) */
    public static byte[] loadPhotoBytes(Teacher t) throws IOException {
        if (t == null || t.photoUrl == null || t.photoUrl.isEmpty()) {
            return null;
        }
        byte[] hit = PHOTO_CACHE.get(t.photoUrl);
        if (hit != null) {
            return hit;
        }
        HttpURLConnection c = (HttpURLConnection) new URL(t.photoUrl).openConnection();
        c.setRequestProperty("User-Agent", UA);
        c.setConnectTimeout(15000);
        c.setReadTimeout(20000);
        trustAllHttps(c);
        byte[] b = readAll(c.getInputStream());
        if (b != null && b.length > 0) {
            PHOTO_CACHE.put(t.photoUrl, b);
        }
        return b;
    }

    /** จัดช่องว่างให้ปกติ (Thai ใช้เว้น 2 จังหวะ) */
    private static String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    /** ตัดคำนำหน้าออก เช่น "อาจารย์ จักสิทธิ์ โอฬาริกชาติ" -> "จักสิทธิ์ โอฬาริกชาติ" */
    private static String stripTitle(String name) {
        for (String t : TITLES) {
            if (name.startsWith(t)) {
                return name.substring(t.length()).trim();
            }
        }
        return name;
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[16384];
        int n;
        while ((n = in.read(buf)) != -1) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}