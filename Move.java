import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Move {
    public static HashMap<Integer, List<Integer>> moves;
    public static HashMap<Integer, List<Integer>> dependencies;
    public static PrecomputedData data = new PrecomputedData();

    public static HashMap<Integer, List<Integer>> loadMoves() {
        moves = new HashMap<Integer, List<Integer>>();
        dependencies = new HashMap<>();

        for (int square = 0; square < 64; square++) {
            int piece = BoardStatus.peek(square);
            if (piece == 0) {
                continue;
            }
            int pieceType = Piece.getType(piece);

            if (Piece.isSlidingPiece(piece)) {
                List<Integer> targetSquares = generateSlidingMoves(piece, square);
                moves.put(square, targetSquares);
                for (int targetSquare : targetSquares) {
                    if (dependencies.containsKey(targetSquare)) {
                        dependencies.get(targetSquare).add(square);
                    } else {
                        List<Integer> pieceIndexes = new ArrayList<>();
                        pieceIndexes.add(square);
                        dependencies.put(targetSquare, pieceIndexes);
                    }
                }
            } else if (pieceType == Piece.Pawn) {
                List<Integer> targetSquares = generatePawnMoves(piece, square);
                moves.put(square, targetSquares);
                for (int targetSquare : targetSquares) {
                    if (dependencies.containsKey(targetSquare)) {
                        dependencies.get(targetSquare).add(square);
                    } else {
                        List<Integer> pieceIndexes = new ArrayList<>();
                        pieceIndexes.add(square);
                        dependencies.put(targetSquare, pieceIndexes);
                    }
                }
            } else if (pieceType == Piece.Knight) {
                List<Integer> targetSquares = generateKnightMoves(piece, square);
                moves.put(square, targetSquares);
                for (int targetSquare : targetSquares) {
                    if (dependencies.containsKey(targetSquare)) {
                        dependencies.get(targetSquare).add(square);
                    } else {
                        List<Integer> pieceIndexes = new ArrayList<>();
                        pieceIndexes.add(square);
                        dependencies.put(targetSquare, pieceIndexes);
                    }
                }
            } else {
                List<Integer> targetSquares = generateKingMoves(piece, square);
                moves.put(square, targetSquares);
                for (int targetSquare : targetSquares) {
                    if (dependencies.containsKey(targetSquare)) {
                        dependencies.get(targetSquare).add(square);
                    } else {
                        List<Integer> pieceIndexes = new ArrayList<>();
                        pieceIndexes.add(square);
                        dependencies.put(targetSquare, pieceIndexes);
                    }
                }
            }
        }

        return moves;
    }

    public static List<Integer> generateSlidingMoves(int piece, int startSquare) {
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
                int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

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

    public static List<Integer> generateKnightMoves(int piece, int startSquare) {
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
                int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                // check other moves if there is piece on target square is same color
                if (Piece.sameColor(piece, pieceOnTargetSquare))
                    continue;

                possibleMoves.add(targetSquare);
            }
        }

        return possibleMoves;
    }

    // ADD TO SEE IF KING WOULD BE PUT IN CHECK
    public static List<Integer> generateKingMoves(int piece, int startSquare) {
        List<Integer> possibleMoves = new ArrayList<>();

        // iterate through all cardinal directions
        for (int directionIdx = 0; directionIdx < 8; directionIdx++) {
            int targetSquare = startSquare + data.cardinalOffset[directionIdx];

            // checks that king isn't moving off the board
            if (!(targetSquare < 0 || targetSquare > 63)) {
                int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                // as long as square is not occupied by same color piece, it can move there
                if (!Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }
        }

        return possibleMoves;
    }

    // have to impliment au passant and simplify captures
    public static List<Integer> generatePawnMoves(int piece, int startSquare) {
        List<Integer> possibleMoves = new ArrayList<>();

        // checks if pawns moves up or down the board
        int lateralDirection = (Piece.isColor(piece, Piece.White)) ? 0 : 1;

        int targetSquare = startSquare + data.cardinalOffset[lateralDirection];
        int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

        // check if pawn can move up one square
        if (pieceOnTargetSquare == Piece.Empty) {
            possibleMoves.add(targetSquare);

            targetSquare += data.cardinalOffset[lateralDirection];
            pieceOnTargetSquare = BoardStatus.peek(targetSquare);

            // check if pawn can move up twice
            if (!hasPawnMoved(piece, startSquare) && pieceOnTargetSquare == Piece.Empty) {
                possibleMoves.add(targetSquare);
            }
        }

        if (startSquare % 8 != 7) {
            // data.cardinalOffset[2] is East
            targetSquare = startSquare + data.cardinalOffset[lateralDirection] + data.cardinalOffset[2];
            pieceOnTargetSquare = BoardStatus.peek(targetSquare);

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
            pieceOnTargetSquare = BoardStatus.peek(targetSquare);

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