import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Move {
    public static HashMap<Integer, List<Integer>> moves;
    public static PrecomputedData data = new PrecomputedData();
    
    public static void loadMoves() {
        moves = new HashMap<>();

        for (int square = 0; square < 64; square++) {
            int piece = BoardStatus.peek(square);
            if ( !Piece.isColor(piece, BoardStatus.colorTurn) ) continue;
            int pieceType = Piece.getType(piece);

            if (piece == 0) continue;
            else if (Piece.isSlidingPiece(piece)) generateSlidingMoves(piece, square);
            else if (pieceType == Piece.Pawn) generatePawnMoves(piece, square);
            else if (pieceType == Piece.Knight) generateKnightMoves(piece, square);
            else generateKingMoves(piece, square);
        }

    }

    public static void generateSlidingMoves(int piece, int startSquare) {
        List<Integer> possibleMoves = new ArrayList<>();

        int pieceType = Piece.getType(piece);
        // 0 - 4 is rook and queen cardinal moves while 4 - 8 are bishop and queen diagonal moves
        int startingDirection = (pieceType == Piece.Bishop) ? 4 : 0;
        int endingDirection = (pieceType == Piece.Rook) ? 4 : 8;

        for (int directionIdx = startingDirection; directionIdx < endingDirection; directionIdx++) {

            // checks square until piece reaches the end of the board
            for (int n = 1; n <= PrecomputedData.numSquaresToEdge[startSquare][directionIdx]; n++) {

                int targetSquare = startSquare + data.cardinalOffset[directionIdx] * n;
                int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                // if piece on target square is same color, then you can't move to that square
                if (Piece.sameColor(piece, pieceOnTargetSquare)) break;

                possibleMoves.add(targetSquare);

                // if piece on target square is opposite color, then you can't slide past that piece
                if (pieceOnTargetSquare != Piece.Empty) break;
            }
        }

        if (possibleMoves.size() != 0) moves.put(startSquare, possibleMoves);
    }

    public static void generateKnightMoves(int piece, int startSquare) {
        List<Integer> possibleMoves = new ArrayList<>();

        // iterate through all possible knight moves
        for (int moveIdx = 0; moveIdx < 8; moveIdx++) {
            int targetSquare = startSquare + data.knightMovement[moveIdx];
            
            // checks if targetSquare a number from 0-63
            if ( !(targetSquare < 0 || targetSquare > 63) ) {
                int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                // check other moves if there is piece on target square is same color
                if (Piece.sameColor(piece, pieceOnTargetSquare)) continue;

                possibleMoves.add(targetSquare);
            }
        }

        if (possibleMoves.size() != 0) moves.put(startSquare, possibleMoves);
    }

    // ADD TO SEE IF KING WOULD BE PUT IN CHECK
    public static void generateKingMoves(int piece, int startSquare) {
        List<Integer> possibleMoves = new ArrayList<>();

        // iterate through all cardinal directions
        for (int directionIdx = 0; directionIdx < 8; directionIdx++) {
            int targetSquare = startSquare + data.cardinalOffset[directionIdx];

            // checks that king isn't moving off the board
            if ( !(targetSquare < 0 || targetSquare > 63) ) {
                int pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                // as long as square is not occupied by same color piece, it can move there
                if (!Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }
        }

        if (possibleMoves.size() != 0) moves.put(startSquare, possibleMoves);
    }

    // have to impliment au passant and simplify captures
    public static void generatePawnMoves(int piece, int startSquare) {    
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
            if (pieceOnTargetSquare == Piece.Empty) {
                possibleMoves.add(targetSquare);
            }
        }

        // 4 6
        if (lateralDirection == 0) {
            if (startSquare % 8 != 0) {
                targetSquare = startSquare + data.cardinalOffset[4];
                pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                if (pieceOnTargetSquare != 0 && !Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }

            if (startSquare % 8 != 7) {
                targetSquare = startSquare + data.cardinalOffset[6];
                pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                if (pieceOnTargetSquare != 0 && !Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }
        }

        // 5, 7
        if (lateralDirection == 1) {
            if (startSquare % 8 != 7) {
                targetSquare = startSquare + data.cardinalOffset[5];
                pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                if (pieceOnTargetSquare != 0 && !Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }

            if (startSquare % 8 != 0) {
                targetSquare = startSquare + data.cardinalOffset[7];
                pieceOnTargetSquare = BoardStatus.peek(targetSquare);

                if (pieceOnTargetSquare != 0 && !Piece.sameColor(piece, pieceOnTargetSquare)) {
                    possibleMoves.add(targetSquare);
                }
            }
        }

        if (possibleMoves.size() != 0) moves.put(startSquare, possibleMoves);
    }
}