import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Move {
    public static HashMap<Integer, List<Integer>> moves;
    public static PrecomputedData data = new PrecomputedData();
    public static boolean inCheck = false;

    public static HashMap<Integer, List<Integer>> loadMoves(HashMap<Integer, List<Integer>> moves, int[] board) {
        moves = new HashMap<Integer, List<Integer>>();

        for (int square = 0; square < 64; square++) {
            int piece = board[square];
            if (piece == 0) {
                continue;
            }
            int pieceType = Piece.getType(piece);

            if (Piece.isSlidingPiece(piece)) {
                List<Integer> targetSquares = generateSlidingMoves(piece, square, board);
                if (targetSquares.size() != 0) {
                    moves.put(square, targetSquares);
                }
            } else if (pieceType == Piece.Pawn) {
                List<Integer> targetSquares = generatePawnMoves(piece, square, board);
                if (targetSquares.size() != 0) {
                    moves.put(square, targetSquares);
                }
            } else if (pieceType == Piece.Knight) {
                List<Integer> targetSquares = generateKnightMoves(piece, square, board);
                if (targetSquares.size() != 0) {
                    moves.put(square, targetSquares);
                }
            } else {
                List<Integer> targetSquares = generateKingMoves(piece, square, board);
                if (targetSquares.size() != 0) {
                    moves.put(square, targetSquares);
                }
            }
        }

        return moves;
    }

    public static void validateMoves() {
        HashMap<Integer, List<Integer>> newStateMoves = new HashMap<Integer, List<Integer>>();
        HashMap<Integer, List<Integer>> newMoves = new HashMap<Integer, List<Integer>>();

        Iterator it = moves.entrySet().iterator();
        while (it.hasNext()) {
            // Getting where each piece can move to
            Map.Entry pair = (Map.Entry) it.next();
            int startSquare = (int) pair.getKey();
            List<Integer> targetSquares = (List<Integer>) pair.getValue();

            // Variable to include only valid moves to keep out of check
            List<Integer> newTargetSquares = new ArrayList<Integer>();

            // Remove all moves that leave you in check
            for (int targetSquare : targetSquares) {
                int[] newStateBoard = copyBoard(BoardStatus.board);
                newStateMoves = fakeMove(startSquare, targetSquare, newStateBoard);

                if (!inCheck(newStateBoard, newStateMoves)) {
                    newTargetSquares.add(targetSquare);
                } else {
                    inCheck = false;
                }
            }

            if (newTargetSquares.size() != 0) {
                newMoves.put(startSquare, newTargetSquares);
            }

            it.remove();
        }

        moves = newMoves;
    }

    public static HashMap<Integer, List<Integer>> fakeMove(int startSquare, int targetSquare, int[] board) {
        // Deals with removing the en passant'd piece
        if (targetSquare == BoardStatus.enPassantSquare) {
            if (BoardStatus.colorTurn == Piece.White) {
                board[targetSquare + 8] = 0;
            } else {
                board[targetSquare - 8] = 0;
            }
        }

        int piece = board[startSquare];

        // If pawn is moving to last rank, make a queen
        int rank = 8 - (targetSquare / 8);
        if (Piece.getType(piece) == Piece.Pawn && (rank == 1 || rank == 8)) {
            piece = BoardStatus.colorTurn + Piece.Queen;
        }

        // Moves the piece to the target square
        board[startSquare] = Piece.Empty;
        board[targetSquare] = piece;

        // Generates all the moves for both colored pieces
        return Move.loadMoves(moves, board);
    }

    public static int[] copyBoard(int[] board) {
        int[] copiedBoard = new int[64];

        for (int i = 0; i < 64; i++) {
            copiedBoard[i] = BoardStatus.board[i];
        }

        return copiedBoard;
    }

    public static boolean inCheck(int[] board, HashMap<Integer, List<Integer>> moves) {
        moves.forEach((startSquare, targetSquares) -> {
            int startPiece = board[startSquare];

            if (Piece.getColor(startPiece) == BoardStatus.colorTurn) {
                for (int targetSquare : targetSquares) {
                    int targetPiece = board[targetSquare];
                    if (Piece.getType(targetPiece) == Piece.King) {
                        inCheck = true;
                        break;
                    }
                }
            }
        });

        return inCheck;
    }

    public static List<Integer> generateSlidingMoves(int piece, int startSquare, int[] board) {
        List<Integer> possibleMoves = new ArrayList<>();

        int pieceType = Piece.getType(piece);
        // 0 - 4 is rook and queen cardinal moves while 4 - 8 are bishop and queen
        // diagonal moves
        int startingDirection = (pieceType == Piece.Bishop) ? 4 : 0;
        int endingDirection = (pieceType == Piece.Rook) ? 4 : 8;

        for (int directionIdx = startingDirection; directionIdx < endingDirection; directionIdx++) {

            // checks square until piece reaches the end of the board
            for (int n = 1; n <= PrecomputedData.numSquaresToEdge[startSquare][directionIdx]; n++) {

                int targetSquare = startSquare + data.cardinalOffset[directionIdx] * n;
                int pieceOnTargetSquare = board[targetSquare];

                // if piece on target square is same color, then you can't move to that square
                if (Piece.sameColor(piece, pieceOnTargetSquare))
                    break;

                possibleMoves.add(targetSquare);

                // if piece on target square is opposite color, then you can't slide past that
                // piece
                if (pieceOnTargetSquare != Piece.Empty)
                    break;
            }
        }

        return possibleMoves;
    }

    public static List<Integer> generateKnightMoves(int piece, int startSquare, int[] board) {
        List<Integer> possibleMoves = new ArrayList<>();
        int startingDirection;
        int endingDirection;

        int file = startSquare % 8;

        switch (file) {
            case 0:
                startingDirection = 4;
                endingDirection = 8;
                break;
            case 1:
                startingDirection = 2;
                endingDirection = 8;
                break;
            case 6:
                startingDirection = 0;
                endingDirection = 6;
                break;
            case 7:
                startingDirection = 0;
                endingDirection = 4;
                break;
            default:
                startingDirection = 0;
                endingDirection = 8;
                break;
        }

        // iterate through all possible knight moves
        for (int moveIdx = startingDirection; moveIdx < endingDirection; moveIdx++) {
            int targetSquare = startSquare + data.knightMovement[moveIdx];

            // checks if targetSquare a number from 0-63
            if (!(targetSquare < 0 || targetSquare > 63)) {
                int pieceOnTargetSquare = board[targetSquare];

                // check other moves if there is piece on target square is same color
                if (Piece.sameColor(piece, pieceOnTargetSquare))
                    continue;

                possibleMoves.add(targetSquare);
            }
        }

        return possibleMoves;
    }

    // ADD TO SEE IF KING WOULD BE PUT IN CHECK
    public static List<Integer> generateKingMoves(int piece, int startSquare, int[] board) {
        List<Integer> possibleMoves = new ArrayList<>();

        // iterate through all cardinal directions
        for (int directionIdx = 0; directionIdx < 8; directionIdx++) {
            int targetSquare = startSquare + data.cardinalOffset[directionIdx];

            // checks that king isn't moving off the board
            if (!(targetSquare < 0 || targetSquare > 63)) {
                int pieceOnTargetSquare = board[targetSquare];

                // as long as square is not occupied by same color piece, it can move there
                if (!Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }
        }

        return possibleMoves;
    }

    // have to impliment au passant and simplify captures
    public static List<Integer> generatePawnMoves(int piece, int startSquare, int[] board) {
        List<Integer> possibleMoves = new ArrayList<>();

        // checks if pawns moves up or down the board
        int lateralDirection = (Piece.isColor(piece, Piece.White)) ? 0 : 1;

        int targetSquare = startSquare + data.cardinalOffset[lateralDirection];
        int pieceOnTargetSquare = board[targetSquare];

        // check if pawn can move up one square
        if (pieceOnTargetSquare == Piece.Empty) {
            possibleMoves.add(targetSquare);

            targetSquare += data.cardinalOffset[lateralDirection];

            if (targetSquare >= 0 && targetSquare < 64) {
                pieceOnTargetSquare = board[targetSquare];

                // check if pawn can move up twice
                if (!hasPawnMoved(piece, startSquare) && pieceOnTargetSquare == Piece.Empty) {
                    possibleMoves.add(targetSquare);
                }
            }
        }

        if (startSquare % 8 != 7) {
            // data.cardinalOffset[2] is East
            targetSquare = startSquare + data.cardinalOffset[lateralDirection] + data.cardinalOffset[2];
            pieceOnTargetSquare = board[targetSquare];

            if (pieceOnTargetSquare != 0 && !Piece.sameColor(piece, pieceOnTargetSquare)) {
                possibleMoves.add(targetSquare);
            }

            // Check for en passant
            if (targetSquare == BoardStatus.enPassantSquare) {
                possibleMoves.add(targetSquare);
            }
        }

        if (startSquare % 8 != 0) {
            // data.cardinalOffset[2] is East
            targetSquare = startSquare + data.cardinalOffset[lateralDirection] + data.cardinalOffset[3];
            pieceOnTargetSquare = board[targetSquare];

            if (pieceOnTargetSquare != 0 && !Piece.sameColor(piece, pieceOnTargetSquare)) {
                possibleMoves.add(targetSquare);
            }

            // Check for en passant
            if (targetSquare == BoardStatus.enPassantSquare) {
                possibleMoves.add(targetSquare);
            }
        }

        return possibleMoves;
    }

    public static boolean hasPawnMoved(int piece, int square) {
        // (square / 8) ranges from 0 to 7 so adding one to go from 1 and 8
        int rank = (square / 8) + 1;

        if (Piece.isColor(piece, Piece.White) && rank == 7) {
            return false;
        }

        if (Piece.isColor(piece, Piece.Black) && rank == 2) {
            return false;
        }

        return true;
    }
}