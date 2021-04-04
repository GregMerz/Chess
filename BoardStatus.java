import java.util.HashMap;
import java.util.List;

public class BoardStatus {
    private static int[] board = new int[64];
    public static int colorTurn;
    public static int enPassantSquare;
    public static int halfMoveClock;
    public static int fullMoveNumber;
    public static boolean inCheck;

    public static void nextTurn() {
        if (colorTurn == Piece.Black)
            fullMoveNumber++;
        // colorTurn xor colorMask changes the color
        colorTurn = colorTurn ^ Piece.colorMask;
    }

    public static void move(int startSquare, int targetSquare) {
        if (targetSquare == enPassantSquare) {
            if (colorTurn == Piece.White) {
                board[targetSquare + 8] = 0;
            }

            else {
                board[targetSquare - 8] = 0;
            }
        }

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

    public static boolean setCheck() {
        colorTurn = colorTurn ^ Piece.colorMask;
        HashMap<Integer, List<Integer>> moves = Move.loadMoves();
        colorTurn = colorTurn ^ Piece.colorMask;

        moves.forEach((startSquare, targetSquares) -> {
            for (int i = 0; i < targetSquares.size(); i++) {
                int piece = BoardStatus.peek(targetSquares.get(i));
                if (Piece.getType(piece) == Piece.King && !Piece.sameColor(piece, BoardStatus.colorTurn)) {
                    inCheck = true;
                }
            }
        });

        return false;
    }
}
