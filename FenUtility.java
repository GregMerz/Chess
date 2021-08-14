import java.util.HashMap;

public class FenUtility {
    public static HashMap<Character, Integer> pieceTypeFromSymbol = new HashMap<>();

    public static void chessSetup(String fen) {
        pieceTypeFromSymbol.put('p', Piece.Pawn);
        pieceTypeFromSymbol.put('n', Piece.Knight);
        pieceTypeFromSymbol.put('b', Piece.Bishop);
        pieceTypeFromSymbol.put('r', Piece.Rook);
        pieceTypeFromSymbol.put('q', Piece.Queen);
        pieceTypeFromSymbol.put('k', Piece.King);

        String[] fenParts = fen.split(" ");

        String fenBoard = fenParts[0];

        // BoardStatus.board = new int[64];

        int file = 0, rank = 0;

        for (char symbol : fenBoard.toCharArray()) {
            if (Character.isDigit(symbol)) {
                file += Character.getNumericValue(symbol);
            }

            else if (symbol == '/') {
                rank++;
                file = 0;
            }

            else {
                int pieceColor = (Character.isUpperCase(symbol)) ? Piece.White : Piece.Black;
                int pieceType = pieceTypeFromSymbol.get(Character.toLowerCase(symbol));
                BoardStatus.setBoard(file + rank * 8, pieceColor | pieceType);
                file++;
            }
        }

        BoardStatus.colorTurn = (fenParts[1].equals("w")) ? Piece.White : Piece.Black;

        String canCastle = fenParts[2];
        if (canCastle.equals("-")) {
        } else {
            for (char letter : canCastle.toCharArray()) {
                switch (letter) {
                    case 'K':

                        break;
                    case 'Q':

                        break;
                    case 'k':

                        break;
                    case 'q':

                        break;
                }

            }
        }

        String square = fenParts[3];
        if (square.equals("-")) {
            BoardStatus.enPassantSquare = -1;
        } else {
            char squareFile = square.charAt(0);
            int squareRank = square.charAt(1);
            int squareIdx = (squareFile - 'A') + ((squareRank - 1) * 8);
            BoardStatus.enPassantSquare = squareIdx;
        }

        BoardStatus.halfMoveClock = Character.getNumericValue(fenParts[4].charAt(0));
        BoardStatus.fullMoveNumber = Character.getNumericValue(fenParts[5].charAt(0));
    }
}