import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

    private String path;
    public final int WIDTH;
    public final int HEIGHT;
    public int[] pixels;

    public static SpriteSheet chessPieces = new SpriteSheet("ChessPieces.png", 384, 128);

    public SpriteSheet(String path, int width, int height) {
        this.path = path;
        this.WIDTH = width;
        this.HEIGHT = height;
        pixels = new int[WIDTH * HEIGHT];
        load();
    }

    private void load() {
        try {
            BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0, 0, w, h, pixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
