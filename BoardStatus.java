public class BoardStatus {
    private static int[] board = new int[64];
    public static int colorTurn;
    public static int enPassantSquare;
    public static int halfMoveClock;
    public static int fullMoveNumber;

    public static void nextTurn() {
        if (colorTurn == Piece.Black)
            fullMoveNumber++;
        // colorTurn xor colorMask changes the color
        colorTurn = colorTurn ^ Piece.colorMask;
    }

    public static void move(int startSquare, int targetSquare) {
        enPassantSquare = -1;

        int piece = board[startSquare];
        if (Piece.getType(piece) == Piece.Pawn && Math.abs(targetSquare - startSquare) == 16) {
            setEnPassant(startSquare, Piece.getColor(piece));
        }

        board[startSquare] = Piece.Empty;
        board[targetSquare] = piece;
    }

    static void setEnPassant(int startSquare, int color) {
        if (color == Piece.White) {
            enPassantSquare = startSquare - 8;
        }

        else {
            enPassantSquare = startSquare + 8;
        }
    }

    public static int peek(int square) {
        return board[square];
    }

    public static void setBoard(int square, int piece) {
        board[square] = piece;
    }
}
