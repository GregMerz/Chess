import java.util.HashMap;
import java.util.List;

public class Graphics {
    
    public static void displayBoard() {
        HashMap<Integer, Character> pieceSymbolFromType = new HashMap<>();
        pieceSymbolFromType.put(Piece.Pawn, 'p');
        pieceSymbolFromType.put(Piece.Knight, 'n');
        pieceSymbolFromType.put(Piece.Bishop, 'b');
        pieceSymbolFromType.put(Piece.Rook, 'r');
        pieceSymbolFromType.put(Piece.Queen, 'q');
        pieceSymbolFromType.put(Piece.King, 'k');

        System.out.println("   A  B  C  D  E  F  G  H");

        for (int rank = 8; rank > 0; rank--) {
            System.out.print(rank + "  ");

            for (int file = 0; file < 8; file++) {
                int piece = BoardStatus.peek(file + (rank - 1) * 8);
                char printChar = ' ';

                if (piece == Piece.Empty) printChar = 'X';
                else {
                    // AND operation looks at first 3 bits
                    int pieceType = piece & 0b00111;
                    // XOR operation with pieceType leaves you with pieceColor
                    int pieceColor = piece ^ pieceType;

                    printChar = pieceSymbolFromType.get(pieceType);
                    if (pieceColor == Piece.White) printChar = Character.toUpperCase(printChar);
                }

                System.out.print(printChar + "  ");
            }

            System.out.println();
        }
    }

    public static void printMovablePieces() {
        Move.moves.forEach((k, v) -> System.out.print(formattedSquares(k) + " "));
        System.out.println();
    }

    public static void printTargetSquares(int square) {
        List<Integer> targetSquares = Move.moves.get(square);

        for (int i = 0; i < targetSquares.size(); i++) {
            System.out.print(formattedSquares(targetSquares.get(i)) + " ");
        }

        System.out.println();
    }

    public static String formattedSquares(int square) {
        String formattedSquare = "";

        int rank = (square >> 3) + 1;
        char file = (char) ((square % 8) + 'A');

        formattedSquare += file;
        formattedSquare += rank;

        return formattedSquare;
    }
}
