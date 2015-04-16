package stackoverflow;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IOUtils {
    public static byte[] read(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int read;
        while ((read = in.read(buff)) > -1) {
            out.write(buff, 0, read);
        }
        return out.toByteArray();
    }
}
