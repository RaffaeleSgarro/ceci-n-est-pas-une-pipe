package stackoverflow;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

public class ImageSecretReader {

    private final Cipher cipher;
    private final byte[] envelope;

    private byte[] image;
    private Map<String, Serializable> headers;

    public ImageSecretReader(Cipher cipher, byte[] envelope) {
        this.cipher = cipher;
        this.envelope = envelope;
    }

    @SuppressWarnings("unchecked")
    public void decrypt() throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(envelope));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        byte[] rgb = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int indexInMessage = y * width + x;
                rgb[indexInMessage] = (byte) (bufferedImage.getRGB(x, y));
            }
        }

        ByteArrayInputStream in = new ByteArrayInputStream(rgb);
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        int length = objectInputStream.readInt();
        headers = (Map<String, Serializable>) objectInputStream.readObject();

        CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
        byte[] paddedContent = IOUtils.read(cipherInputStream);

        image = new byte[length];
        System.arraycopy(paddedContent, 0, image, 0, length);
    }

    public byte[] getImageBytes() {
        return image;
    }

    public Map<String, Serializable> getHeaders() {
        return headers;
    }
}
