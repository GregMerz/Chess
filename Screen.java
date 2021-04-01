public class Screen {
    private int width, height;
    public int[] pixels;

    public int[] tiles = new int[64];

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                tiles[x + y * 8] = ((x + y) % 2 == 1) ? 0 : 0xFFFFFF;
            }
        }
    }

    public void renderBackground() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[x + y * width] = 0xff00ff;
            }
        }
    }

    public void renderTiles() {
        int boardSide = 512;
        int startY = (height - boardSide) / 2;
        int startX = (width - boardSide) / 2;

        for (int y = startY; y < (height + boardSide) / 2; y++) {
            for (int x = startX; x < (width + boardSide) / 2; x++) {
                int tileIndex = (int) ((x - startX) / (boardSide / 8.0)) + (int) ((y - startY) / (boardSide / 8.0)) * 8;
                pixels[x + y * width] = tiles[tileIndex];
            }
        }
    }

    public void renderPieces(int xOffset, int yOffset, int[] piecePixels) {
        int boardSide = 512;
        int startY = (height - boardSide) / 2;
        int startX = (width - boardSide) / 2;

        for (int y = startY + (yOffset * 64); y < startY + (yOffset * 64) + 64; y++) {
            for (int x = startX + (xOffset * 64); x < startX + (xOffset * 64) + 64; x++) {
                if (piecePixels[(x - (startX + (xOffset * 64))) + (y - (startY + (yOffset * 64))) * 64] != 0) {
                    pixels[x + y * width] = piecePixels[(x - (startX + (xOffset * 64)))
                            + (y - (startY + (yOffset * 64))) * 64];
                }
            }
        }
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
    }
}
