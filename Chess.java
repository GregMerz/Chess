import java.util.List;
import java.util.Scanner;

public class Chess {
    public static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {
        FenUtility.chessSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        PrecomputedData.computeMoveData();

        play();
    }

    public static void play() {
        while (true) {
            Graphics.displayBoard();
            
            String color = (BoardStatus.colorTurn == Piece.White) ? "White" : "Black";
            System.out.println(color + "'s turn. Choose a piece. ");

            Move.loadMoves();
            Graphics.printMovablePieces();

            int startingSquare;
            List<Integer> targetSquares;

            do {
                // find the square the user inputted
                String movingPiece = scan.nextLine();
                startingSquare = findPiece(movingPiece);
                
                targetSquares = Move.moves.get(startingSquare);

            } while (targetSquares == null || targetSquares.size() == 0);
            
            System.out.println("Where do you want to move your piece to? ");
            Graphics.printTargetSquares(startingSquare);

            int targetSquare;

            do {
                String square = scan.nextLine();
                targetSquare = findPiece(square);

                for (int i = 0 ; i < targetSquares.size(); i++) {
                    int validTargetSquare = targetSquares.get(i);

                    if (targetSquare == validTargetSquare) {
                        BoardStatus.move(startingSquare, targetSquare);
                        break;
                    }
                }
            } while (BoardStatus.peek(startingSquare) != 0);
            
            BoardStatus.nextTurn();
        }
    }

    public static int findPiece(String movingPiece) {
        if (movingPiece.length() == 2) {
            char file = movingPiece.charAt(0);
            int rank = Character.getNumericValue(movingPiece.charAt(1));

            // file is from A to H and rank is from 1 to 8
            if (file >= 'A' && file <= 'H' && rank > 0 && rank < 9) 
                return (file - 'A') + (rank - 1) * 8;
        }

        return -1;
    }
} 
