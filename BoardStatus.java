import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardStatus {
    public static int[] board = new int[64];
    public static int colorTurn;
    public static int enPassantSquare;
    public static int halfMoveClock;
    public static int fullMoveNumber;
    public static int inCheck = -1;
    public static boolean checkmate = false;

    public static void nextTurn() {
        if (colorTurn == Piece.Black)
            fullMoveNumber++;

        // Switches the color turn
        colorTurn = colorTurn ^ Piece.colorMask;
    }

    public static void move(int startSquare, int targetSquare, int[] board) {
        inCheck = -1;

        // Deals with removing the en passant'd piece
        if (targetSquare == enPassantSquare) {
            if (colorTurn == Piece.White) {
                board[targetSquare + 8] = 0;
            } else {
                board[targetSquare - 8] = 0;
            }
        }

        // Resets en passant square
        enPassantSquare = -1;

        // If a pawn just moved twice, set the en passant square
        int piece = board[startSquare];
        if (Piece.getType(piece) == Piece.Pawn && Math.abs(targetSquare - startSquare) == 16) {
            setEnPassant(startSquare, Piece.getColor(piece));
        }

        // If pawn is moving to last rank, make a queen
        int rank = 8 - (targetSquare / 8);
        if (Piece.getType(piece) == Piece.Pawn && (rank == 1 || rank == 8)) {
            piece = colorTurn + Piece.Queen;
        }

        // Moves the piece to the target square
        board[startSquare] = Piece.Empty;
        board[targetSquare] = piece;

        // Generates all the moves for both colored pieces
        Move.moves = Move.loadMoves(Move.moves, board);
        Move.validateMoves();
        setCheck();

        // If in checkmate, end the game
        if (Move.moves.size() == 1) {
            System.out.println("Checkmate");
        }
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
        Move.moves.forEach((startSquare, targetSquares) -> {
            int startPiece = board[startSquare];

            for (int targetSquare : targetSquares) {
                int targetPiece = board[targetSquare];
                if (Piece.getType(targetPiece) == Piece.King && !Piece.sameColor(startPiece, targetPiece)) {
                    inCheck = targetSquare;
                    break;
                }
            }
        });

        return (inCheck == -1) ? false : true;
    }
}
