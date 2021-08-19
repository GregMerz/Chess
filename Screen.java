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
    int selectedSquare = -1;
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

    public void renderAllPieces(BoardStatus bs) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int piece = bs.getBoard(x + y * 8);

                if (piece == 0) {
                    continue;
                }

                // Pawns
                if (Piece.getType(piece) == Piece.Pawn) {
                    if (Piece.isColor(piece, Piece.White)) {
                        renderPiece(x, y, Sprite.whitePawn.pixels);
                    } else {
                        renderPiece(x, y, Sprite.blackPawn.pixels);
                    }
                }

                // Rook
                if (Piece.getType(piece) == Piece.Rook) {
                    if (Piece.isColor(piece, Piece.White)) {
                        renderPiece(x, y, Sprite.whiteRook.pixels);
                    } else {
                        renderPiece(x, y, Sprite.blackRook.pixels);
                    }
                }

                // Knight
                if (Piece.getType(piece) == Piece.Knight) {
                    if (Piece.isColor(piece, Piece.White)) {
                        renderPiece(x, y, Sprite.whiteKnight.pixels);
                    } else {
                        renderPiece(x, y, Sprite.blackKnight.pixels);
                    }
                }

                // Bishop
                if (Piece.getType(piece) == Piece.Bishop) {
                    if (Piece.isColor(piece, Piece.White)) {
                        renderPiece(x, y, Sprite.whiteBishop.pixels);
                    } else {
                        renderPiece(x, y, Sprite.blackBishop.pixels);
                    }
                }

                // Queen
                if (Piece.getType(piece) == Piece.Queen) {
                    if (Piece.isColor(piece, Piece.White)) {
                        renderPiece(x, y, Sprite.whiteQueen.pixels);
                    } else {
                        renderPiece(x, y, Sprite.blackQueen.pixels);
                    }
                }

                // King
                if (Piece.getType(piece) == Piece.King) {
                    if (Piece.isColor(piece, Piece.White)) {
                        renderPiece(x, y, Sprite.whiteKing.pixels);
                    } else {
                        renderPiece(x, y, Sprite.blackKing.pixels);
                    }
                }
            }
        }
    }

    public void renderPiece(int xOffset, int yOffset, int[] piecePixels) {
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

    public void update(BoardStatus bs) {
        int mousePosX = Mouse.getX();
        int mousePosY = Mouse.getY();

        // Checks if the mouse position is on the board
        if (mousePosX > startX && mousePosX < endX && mousePosY > startY && mousePosY < endY) {
            int file = (mousePosX - startX) / 64;
            int rank = (mousePosY - startY) / 64;
            int pieceIndex = file + rank * 8;
            int piece = bs.getBoard(file + rank * 8);

            // Checks if you left-click your mouse
            if (Mouse.getButton() == 1) {
                // Checks if piece is not selected
                if (selectedSquare == -1) {

                    // Checks if you are clicking a piece for your color
                    if (piece != 0 && Piece.sameColor(piece, bs.getColorTurn())) {
                        initTiles();

                        List<Integer> targetSquares = bs.getMoves().get(pieceIndex);

                        // Checks that the piece you clicked on has valid moves
                        if (targetSquares != null && targetSquares.size() > 0) {
                            // Selects a piece and highlights its tile
                            selectedSquare = pieceIndex;
                            tiles[pieceIndex] = 0xFF0000;

                            // Highlights possible moves in grey
                            for (int i = 0; i < targetSquares.size(); i++) {
                                tiles[targetSquares.get(i)] = 0x808080;
                            }
                        }
                    }
                }

                else {
                    // Checks if you can move your selected piece to where you left-clicked
                    if (bs.getMoves().get(selectedSquare).contains(pieceIndex)) {
                        bs.move(selectedSquare, pieceIndex);
                        selectedSquare = -1;
                        initTiles();
                    }
                }
            }

            else if (selectedSquare == -1 && Piece.sameColor(piece, bs.getColorTurn())) {
                if (piece != 0) {

                    initTiles();
                    List<Integer> targetSquares = bs.getMoves().get(pieceIndex);

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

        // Checks if you right-click your mouse
        if (Mouse.getButton() == 3) {
            initTiles();
            selectedSquare = -1;
        }

        // Makes the king's tile blue if you are in check
        if (bs.getInCheck() != -1) {
            tiles[bs.getInCheck()] = 0x0000FF;
        }
    }
}