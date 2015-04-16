package stackoverflow;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;

// http://stackoverflow.com/questions/29669104
public class CeciNEstPasUnePipe {

    public static void main(String... args) throws Exception {
        File encryptedPNG = new File("Encrypted.png");
        File decryptDestination = new File("Decrypted.jpeg");

        // Setup cipher
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(new SecureRandom());
        SecretKey key = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES");

        try (InputStream in = CeciNEstPasUnePipe.class.getResourceAsStream("ceci-n-est-pas-une-pipe.jpg")) {
            byte[] secret = IOUtils.read(in);
            LinkedHashMap<String, Serializable> headers = new LinkedHashMap<>();
            headers.put("Filename", "hamster.jpeg");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ImageSecretWriter writer = new ImageSecretWriter(cipher, headers, secret);
            writer.encryptAndWriteAsPNGTo(new FileOutputStream(encryptedPNG));
            System.out.println("Encrypted secret and written to " + encryptedPNG.getAbsolutePath());
        }

        try (InputStream in = new FileInputStream(encryptedPNG);
             OutputStream out = new FileOutputStream(decryptDestination)) {
            cipher.init(Cipher.DECRYPT_MODE, key);
            ImageSecretReader reader = new ImageSecretReader(cipher, IOUtils.read(in));
            reader.decrypt();
            out.write(reader.getImageBytes());
            out.flush();
            System.out.println("Headers dump");
            for (Map.Entry<String, Serializable> entry : reader.getHeaders().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("Decrypted secret and written original image to " + decryptDestination.getAbsolutePath());
        }
    }

}
