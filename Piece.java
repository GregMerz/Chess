public class Piece {
    public static final int Empty = 0;
    public static final int King = 1;
    public static final int Pawn = 2;
    public static final int Knight = 3;
    public static final int Bishop = 4;
    public static final int Rook = 5;
    public static final int Queen = 6;

    public static final int White = 0b01000;
    public static final int Black = 0b10000;

    static final int typeMask = 0b00111;
    static final int colorMask = 0b11000;

    public static int getType(int piece) {
        return piece & typeMask;
    }

    public static int getColor(int piece) {
        return piece & colorMask;
    }

    public static boolean isColor(int piece, int color) {
        return (piece & colorMask) == color;
    }

    public static boolean isSlidingPiece(int piece) {
        return (piece & 0b100) != 0;
    }

    // check later
    public static boolean sameColor(int piece1, int piece2) {
        return ((piece1 ^ piece2) & colorMask) == 0;
    }

}