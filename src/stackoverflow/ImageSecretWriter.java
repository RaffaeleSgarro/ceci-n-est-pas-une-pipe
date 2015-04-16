package stackoverflow;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

public class ImageSecretWriter {

    private final Cipher cipher;
    private final Map<String, Serializable> headers;
    private final byte[] secret;

    /**
     * @param cipher must be initialized
     * @param headers must be non null
     * @param secret the message
     */
    ImageSecretWriter(Cipher cipher, Map<String, Serializable> headers, byte[] secret) {
        this.cipher = cipher;
        this.headers = headers;
        this.secret = secret;
    }

    public void encryptAndWriteAsPNGTo(OutputStream out) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream headerOut = new ObjectOutputStream(buffer);
        headerOut.writeInt(secret.length);
        headerOut.writeObject(headers);

        CipherOutputStream cipherOutputStream = new CipherOutputStream(buffer, cipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.flush();

        RenderedImage image = encodeMessage(buffer.toByteArray());

        ImageIO.write(image, "PNG", out);
    }

    /**
     * Store the bytes in messageBytes in an image in the RGB color space using the
     * blue channel
     *
     * @param messageBytes
     * @return an image in RGB co
     */
    public RenderedImage encodeMessage(byte[] messageBytes) {

        int side = (int) Math.ceil(Math.sqrt(messageBytes.length));

        BufferedImage image = new BufferedImage(side, side, BufferedImage.TYPE_INT_RGB);
        int counter = 0;

        for (int y = 0; y < side; y++) {
            for (int x = 0; x < side; x++) {
                int pixel;
                if (counter < messageBytes.length) {
                    // store byte in blue channel - bytes can be negative
                    pixel = messageBytes[counter] & 0x0000FF;
                } else {
                    // padding. zero. easy
                    pixel = 0x000000;
                }
                image.setRGB(x, y, pixel);
                counter++;
            }
        }

        return image;
    }
}
