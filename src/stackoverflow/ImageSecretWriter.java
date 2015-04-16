package stackoverflow;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

public class ImageSecretWriter {

    private final Cipher cipher;
    private final Map<String, Serializable> headers;
    private final byte[] secret;

    ImageSecretWriter(Cipher cipher, Map<String, Serializable> headers, byte[] secret) {
        this.cipher = cipher;
        this.headers = headers;
        this.secret = secret;
    }

    public void encryptAndWriteAsPNGTo(OutputStream out) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream headerOut = new ObjectOutputStream(byteArrayOutputStream);
        headerOut.writeInt(secret.length);
        headerOut.writeObject(headers);

        CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.flush();

        byte[] messageBytes = byteArrayOutputStream.toByteArray();

        int side = (int) Math.ceil(Math.sqrt(messageBytes.length));

        BufferedImage image = new BufferedImage(side, side, BufferedImage.TYPE_INT_RGB);
        int counter = 0;

        for (int y = 0; y < side; y++) {
            for (int x = 0; x < side; x++) {
                int pixel;
                if (counter < messageBytes.length) {
                    pixel = messageBytes[counter] & 0x0000FF;
                } else {
                    pixel = 0x000000;
                }
                image.setRGB(x, y, pixel);
                counter++;
            }
        }

        ImageIO.write(image, "PNG", out);
    }
}
