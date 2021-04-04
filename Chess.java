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

    public Chess() {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);

        screen = new Screen(width, height);
        frame = new JFrame();

        Mouse mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
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
        Move.moves = Move.loadMoves();
        screen.update();
        BoardStatus.setCheck();
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

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int piece = BoardStatus.peek(x + y * 8);

                if (piece == 0) {
                    continue;
                }

                // Pawns
                if (Piece.getType(piece) == Piece.Pawn) {
                    if (Piece.isColor(piece, Piece.White)) {
                        screen.renderPieces(x, y, Sprite.whitePawn.pixels);
                    } else {
                        screen.renderPieces(x, y, Sprite.blackPawn.pixels);
                    }
                }

                // Rook
                if (Piece.getType(piece) == Piece.Rook) {
                    if (Piece.isColor(piece, Piece.White)) {
                        screen.renderPieces(x, y, Sprite.whiteRook.pixels);
                    } else {
                        screen.renderPieces(x, y, Sprite.blackRook.pixels);
                    }
                }

                // Knight
                if (Piece.getType(piece) == Piece.Knight) {
                    if (Piece.isColor(piece, Piece.White)) {
                        screen.renderPieces(x, y, Sprite.whiteKnight.pixels);
                    } else {
                        screen.renderPieces(x, y, Sprite.blackKnight.pixels);
                    }
                }

                // Bishop
                if (Piece.getType(piece) == Piece.Bishop) {
                    if (Piece.isColor(piece, Piece.White)) {
                        screen.renderPieces(x, y, Sprite.whiteBishop.pixels);
                    } else {
                        screen.renderPieces(x, y, Sprite.blackBishop.pixels);
                    }
                }

                // Queen
                if (Piece.getType(piece) == Piece.Queen) {
                    if (Piece.isColor(piece, Piece.White)) {
                        screen.renderPieces(x, y, Sprite.whiteQueen.pixels);
                    } else {
                        screen.renderPieces(x, y, Sprite.blackQueen.pixels);
                    }
                }

                // King
                if (Piece.getType(piece) == Piece.King) {
                    if (Piece.isColor(piece, Piece.White)) {
                        screen.renderPieces(x, y, Sprite.whiteKing.pixels);
                    } else {
                        screen.renderPieces(x, y, Sprite.blackKing.pixels);
                    }
                }
            }
        }

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen.pixels[i];
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.setColor(Color.CYAN);

        if (BoardStatus.inCheck == true) {
            g.fillRect(0, 0, 64, 64);
        }

        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        FenUtility.chessSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        PrecomputedData.computeMoveData();

        Chess game = new Chess();
        game.frame.setResizable(false);
        game.frame.setTitle("Chess Game");
        game.frame.add(game);
        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);

        game.start();
    }
}