import java.util.List;

public class Choice {
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
