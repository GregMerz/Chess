import java.util.ArrayList;
import java.util.List;

public class Screen {
    private int width, height;
    public int[] pixels;

    int boardSide = 512;
    int startY;
    int startX;
    int endY;
    int endX;
    int hoveredSquare = -1;
    List<Integer> moveableMoves = new ArrayList<Integer>();
    public int[] tiles = new int[64];

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        this.startY = (height - boardSide) / 2;
        this.startX = (width - boardSide) / 2;
        this.endY = (height + boardSide) / 2;
        this.endX = (width + boardSide) / 2;
        pixels = new int[width * height];

        initTiles();
    }

    public void initTiles() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                tiles[x + y * 8] = ((x + y) % 2 == 1) ? 0 : 0xFFFFFF;
            }
        }
    }

    public void renderBackground() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[x + y * width] = 0xff00ff;
            }
        }
    }

    public void renderTiles() {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int tileIndex = (int) ((x - startX) / (boardSide / 8.0)) + (int) ((y - startY) / (boardSide / 8.0)) * 8;
                pixels[x + y * width] = tiles[tileIndex];
            }
        }
    }

    public void renderPieces(int xOffset, int yOffset, int[] piecePixels) {
        for (int y = startY + (yOffset * 64); y < startY + (yOffset * 64) + 64; y++) {
            for (int x = startX + (xOffset * 64); x < startX + (xOffset * 64) + 64; x++) {
                if (piecePixels[(x - (startX + (xOffset * 64))) + (y - (startY + (yOffset * 64))) * 64] != 0) {
                    pixels[x + y * width] = piecePixels[(x - (startX + (xOffset * 64)))
                            + (y - (startY + (yOffset * 64))) * 64];
                }
            }
        }
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
    }

    public void update() {
        int mousePosX = Mouse.getX();
        int mousePosY = Mouse.getY();

        if (mousePosX > startX && mousePosX < endX && mousePosY > startY && mousePosY < endY) {
            int file = (mousePosX - startX) / 64;
            int rank = (mousePosY - startY) / 64;
            int pieceIndex = file + rank * 8;
            int piece = BoardStatus.peek(file + rank * 8);

            if (Mouse.getButton() == 1) {
                if (hoveredSquare == -1) {

                    if (piece != 0 && Piece.sameColor(piece, BoardStatus.colorTurn)) {
                        initTiles();

                        List<Integer> targetSquares = Move.moves.get(pieceIndex);

                        if (targetSquares != null && targetSquares.size() > 0) {
                            hoveredSquare = pieceIndex;
                            tiles[pieceIndex] = 0xFF0000;

                            for (int i = 0; i < targetSquares.size(); i++) {
                                tiles[targetSquares.get(i)] = 0x808080;
                            }
                        }
                    }
                }

                else {
                    if (tiles[pieceIndex] == 0x808080) {
                        BoardStatus.move(hoveredSquare, pieceIndex);
                        BoardStatus.nextTurn();
                        hoveredSquare = -1;
                        initTiles();
                    }
                    /*
                     * if (tiles[pieceIndex] == 0xFF0000) { initTiles(); hoveredSquare = -1; }
                     */
                }
            }

            else if (hoveredSquare == -1 && Piece.sameColor(piece, BoardStatus.colorTurn)) {
                if (piece != 0) {

                    initTiles();
                    List<Integer> targetSquares = Move.moves.get(pieceIndex);

                    if (targetSquares != null && targetSquares.size() > 0) {
                        tiles[pieceIndex] = 0x00FF00;

                        for (int i = 0; i < targetSquares.size(); i++) {
                            tiles[targetSquares.get(i)] = 0x808080;
                        }
                    }
                }
                if (piece == 0) {
                    initTiles();
                }
            }
        }

        if (Mouse.getButton() == 3) {
            initTiles();
            hoveredSquare = -1;
        }
    }
}
