import java.util.List;
import java.util.Scanner;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;

public class Chess extends Canvas implements Runnable {
    public static int width = 1000;
    public static int height = width / 16 * 9;

    private Thread thread;
    private JFrame frame;
    private Screen screen;
    private boolean isRunning = false;

    private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public static Scanner scan = new Scanner(System.in);

    public Chess() {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);

        frame = new JFrame();
        screen = new Screen(width, height);
    }

    public synchronized void start() {
        isRunning = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (isRunning) {
            update();
            render();
        }
    }

    public void update() {
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        screen.clear();

        screen.renderBackground();
        screen.renderTiles();

        // rook
        screen.renderPieces(0, 0, Sprite.blackRook.pixels);
        screen.renderPieces(7, 0, Sprite.blackRook.pixels);
        screen.renderPieces(0, 7, Sprite.whiteRook.pixels);
        screen.renderPieces(7, 7, Sprite.whiteRook.pixels);

        // knight
        screen.renderPieces(1, 0, Sprite.blackKnight.pixels);
        screen.renderPieces(6, 0, Sprite.blackKnight.pixels);
        screen.renderPieces(1, 7, Sprite.whiteKnight.pixels);
        screen.renderPieces(6, 7, Sprite.whiteKnight.pixels);

        // bishop
        screen.renderPieces(2, 0, Sprite.blackBishop.pixels);
        screen.renderPieces(5, 0, Sprite.blackBishop.pixels);
        screen.renderPieces(2, 7, Sprite.whiteBishop.pixels);
        screen.renderPieces(5, 7, Sprite.whiteBishop.pixels);

        // queen
        screen.renderPieces(3, 0, Sprite.blackQueen.pixels);
        screen.renderPieces(3, 7, Sprite.whiteQueen.pixels);

        // king
        screen.renderPieces(4, 0, Sprite.blackKing.pixels);
        screen.renderPieces(4, 7, Sprite.whiteKing.pixels);

        // pawns
        for (int i = 0; i < 8; i++) {
            screen.renderPieces(i, 1, Sprite.blackPawn.pixels);
            screen.renderPieces(i, 6, Sprite.whitePawn.pixels);
        }

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen.pixels[i];
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        Chess game = new Chess();
        game.frame.setResizable(false);
        game.frame.setTitle("Chess Game");
        game.frame.add(game);
        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);

        game.start();
        /*
         * FenUtility.
         * chessSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
         * PrecomputedData.computeMoveData();
         * 
         * play();
         */
    }

    public static void play() {
        while (true) {
            // Graphics.displayBoard();

            String color = (BoardStatus.colorTurn == Piece.White) ? "White" : "Black";
            System.out.println(color + "'s turn. Choose a piece. ");

            Move.loadMoves();
            // Graphics.printMovablePieces();

            int startingSquare;
            List<Integer> targetSquares;

            do {
                // find the square the user inputted
                String movingPiece = scan.nextLine();
                startingSquare = findPiece(movingPiece);

                targetSquares = Move.moves.get(startingSquare);

            } while (targetSquares == null || targetSquares.size() == 0);

            System.out.println("Where do you want to move your piece to? ");
            // Graphics.printTargetSquares(startingSquare);

            int targetSquare;

            do {
                String square = scan.nextLine();
                targetSquare = findPiece(square);

                for (int i = 0; i < targetSquares.size(); i++) {
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

    // this will later improve the movement of pieces
    public static void parseMoveInput(String moveInput) {
        char symbol = moveInput.charAt(0);
        int piece;

        // find out what type of piece we are moving
        if (Character.isLowerCase(symbol)) {
            int file = symbol - 'A';
            int rank = moveInput.charAt(0) - 1;
            int square = file + rank * 8;

            piece = Piece.Pawn | BoardStatus.colorTurn;
        }

        else {

        }

        for (int inputIdx = 1; inputIdx < moveInput.length(); inputIdx++) {

        }
    }
}
