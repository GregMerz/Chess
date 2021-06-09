public class Sprite {
    private final int SIZE;
    private int x, y;
    public int[] pixels;
    private SpriteSheet sheet;

    public static Sprite blackKing = new Sprite(64, 0, 0, SpriteSheet.chessPieces);
    public static Sprite whiteKing = new Sprite(64, 0, 1, SpriteSheet.chessPieces);
    public static Sprite blackQueen = new Sprite(64, 1, 0, SpriteSheet.chessPieces);
    public static Sprite whiteQueen = new Sprite(64, 1, 1, SpriteSheet.chessPieces);
    public static Sprite blackRook = new Sprite(64, 2, 0, SpriteSheet.chessPieces);
    public static Sprite whiteRook = new Sprite(64, 2, 1, SpriteSheet.chessPieces);
    public static Sprite blackKnight = new Sprite(64, 3, 0, SpriteSheet.chessPieces);
    public static Sprite whiteKnight = new Sprite(64, 3, 1, SpriteSheet.chessPieces);
    public static Sprite blackBishop = new Sprite(64, 4, 0, SpriteSheet.chessPieces);
    public static Sprite whiteBishop = new Sprite(64, 4, 1, SpriteSheet.chessPieces);
    public static Sprite blackPawn = new Sprite(64, 5, 0, SpriteSheet.chessPieces);
    public static Sprite whitePawn = new Sprite(64, 5, 1, SpriteSheet.chessPieces);

    public static Sprite whitePawn1 = new Sprite(64, 0, 0, SpriteSheet.newChessPieces);
    public static Sprite blackPawn1 = new Sprite(64, 0, 1, SpriteSheet.newChessPieces);
    public static Sprite whiteKnight1 = new Sprite(64, 1, 0, SpriteSheet.newChessPieces);
    public static Sprite blackKnight1 = new Sprite(64, 1, 1, SpriteSheet.newChessPieces);
    public static Sprite whiteBishop1 = new Sprite(64, 2, 0, SpriteSheet.newChessPieces);
    public static Sprite blackBishop1 = new Sprite(64, 2, 1, SpriteSheet.newChessPieces);
    public static Sprite whiteRook1 = new Sprite(64, 3, 0, SpriteSheet.newChessPieces);
    public static Sprite blackRook1 = new Sprite(64, 3, 1, SpriteSheet.newChessPieces);
    public static Sprite whiteQueen1 = new Sprite(64, 4, 0, SpriteSheet.newChessPieces);
    public static Sprite blackQueen1 = new Sprite(64, 4, 1, SpriteSheet.newChessPieces);
    public static Sprite whiteKing1 = new Sprite(64, 5, 0, SpriteSheet.newChessPieces);
    public static Sprite blackKing1 = new Sprite(64, 5, 1, SpriteSheet.newChessPieces);

    public Sprite(int size, int x, int y, SpriteSheet sheet) {
        SIZE = size;
        pixels = new int[SIZE * SIZE];
        this.x = x * size;
        this.y = y * size;
        this.sheet = sheet;
        load();
    }

    private void load() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                pixels[x + y * SIZE] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.WIDTH];
            }
        }
    }
}
