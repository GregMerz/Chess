public class BoardStatus {
    private static int[] board = new int[64];
    public static int colorTurn;
    public static int enPassantSquare;
    public static int halfMoveClock;
    public static int fullMoveNumber;

    public static void nextTurn() {
        if (colorTurn == Piece.Black) fullMoveNumber++;
        // colorTurn xor colorMask changes the color
        colorTurn = colorTurn ^ Piece.colorMask;
    }

    public static void move(int startSquare, int targetSquare) {
        int piece = board[startSquare];
        board[startSquare] = Piece.Empty;
        board[targetSquare] = piece;
    }

    public static int peek(int square) {
        return board[square];
    }

    public static void setBoard(int square, int piece) {
        board[square] = piece;
    }
}
