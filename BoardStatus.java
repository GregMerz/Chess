import java.util.HashMap;
import java.util.List;

public class BoardStatus {
    private int[] board;
    private int colorTurn;
    private boolean canBlackCastleKingSide;
    private boolean canBlackCastleQueenSide;
    private boolean canWhiteCastleKingSide;
    private boolean canWhiteCastleQueenSide;
    private int enPassantSquare;
    private int halfMoveClock;
    private int fullMoveNumber;
    private int inCheck;
    private HashMap<Integer, List<Integer>> moves;

    public BoardStatus() {
        this.setBoard(new int[64]);
        this.setColorTurn(-1);
        this.setBlackCanCastleKingSide(true);
        this.setBlackCanCastleQueenSide(true);
        this.setWhiteCanCastleKingSide(true);
        this.setWhiteCanCastleQueenSide(true);
        this.setEnPassantSquare(-1);
        this.setHalfMoveClock(0);
        this.setFullMoveNumber(0);
        this.setInCheck(-1);
        this.setMoves(new HashMap<>());
    }

    public BoardStatus(BoardStatus bs) {
        this.setBoard(bs.getBoard());
        this.setColorTurn(bs.getColorTurn());
        this.setBlackCanCastleKingSide(bs.isBlackCanCastleKingSide());
        this.setBlackCanCastleQueenSide(bs.isBlackCanCastleQueenSide());
        this.setWhiteCanCastleKingSide(bs.isWhiteCanCastleKingSide());
        this.setWhiteCanCastleQueenSide(bs.isWhiteCanCastleQueenSide());
        this.setEnPassantSquare(bs.getEnPassantSquare());
        this.setHalfMoveClock(bs.getHalfMoveClock());
        this.setFullMoveNumber(bs.getFullMoveNumber());
        this.setInCheck(bs.getInCheck());
        this.setMoves(bs.getMoves());
    }

    public void fakeMove(int startSquare, int targetSquare) {
        int piece = board[startSquare];
        int rank = 8 - (targetSquare / 8);

        // Reset check since you are moving to a new board state
        inCheck = -1;

        // Deals with removing the en passant'd piece
        removeEnPassantPiece(targetSquare);

        // If pawn is moving to last rank, make a queen
        if (Piece.getType(piece) == Piece.Pawn && (rank == 1 || rank == 8)) {
            piece = colorTurn + Piece.Queen;
        }

        // Moves the piece to the target square
        board[startSquare] = Piece.Empty;
        board[targetSquare] = piece;

        // Moves rook if castling
        moveRookForCastling(piece, startSquare, targetSquare);

        Move.loadMoves(this);

        movedInCheck(colorTurn);
    }

    public void move(int startSquare, int targetSquare) {
        int piece = board[startSquare];
        int rank = 8 - (targetSquare / 8);
        int file = (targetSquare % 8) + 1;

        // Reset check since you are moving to a new board state
        inCheck = -1;

        // Deals with removing the en passant'd piece
        removeEnPassantPiece(targetSquare);

        setCastling(piece, file);

        // If a pawn just moved twice, set the en passant square
        if (Piece.getType(piece) == Piece.Pawn && Math.abs(targetSquare - startSquare) == 16) {
            setEnPassant(startSquare, Piece.getColor(piece));
        }

        // If pawn is moving to last rank, make a queen
        if (Piece.getType(piece) == Piece.Pawn && (rank == 1 || rank == 8)) {
            piece = colorTurn + Piece.Queen;
        }

        // Moves the piece to the target square
        board[startSquare] = Piece.Empty;
        board[targetSquare] = piece;

        // Moves rook if castling
        moveRookForCastling(piece, startSquare, targetSquare);

        // Generates all the moves for both colored pieces
        Move.loadMoves(this);
        Move.validateMoves(this);
        setCheck();

        // If in checkmate, end the game
        if (moves.size() == 1) {
            System.out.println("Checkmate");
        }

        nextTurn();
    }

    public void moveRookForCastling(int piece, int startSquare, int targetSquare) {
        if (piece == Piece.King + Piece.White) {
            if (startSquare - targetSquare == 2) {
                int castlingRook = board[56];
                board[startSquare - 1] = castlingRook;
                board[56] = Piece.Empty;
            }

            if (startSquare - targetSquare == -2) {
                int castlingRook = board[63];
                board[startSquare + 1] = castlingRook;
                board[63] = Piece.Empty;
            }
        }

        else if (piece == Piece.King + Piece.Black) {
            if (startSquare - targetSquare == 2) {
                int castlingRook = board[0];
                board[startSquare - 1] = castlingRook;
                board[0] = Piece.Empty;
            }

            if (startSquare - targetSquare == -2) {
                int castlingRook = board[7];
                board[startSquare + 1] = castlingRook;
                board[7] = Piece.Empty;
            }
        }
    }

    // Fix mixed up colorTurn
    public void setCastling(int piece, int file) {
        if (colorTurn == Piece.Black) {
            if (Piece.getType(piece) == Piece.King) {
                // whiteKingMoved = true;
                canWhiteCastleKingSide = false;
                canWhiteCastleQueenSide = false;
            }

            if (Piece.getType(piece) == Piece.Rook) {
                if (file == 1) {
                    // leftWhiteRookMoved = true;
                    canWhiteCastleQueenSide = false;
                }
                if (file == 8) {
                    // rightWhiteRookMoved = true;
                    canWhiteCastleKingSide = false;
                }
            }
        }

        else {
            if (Piece.getType(piece) == Piece.King) {
                // blackKingMoved = true;
                canBlackCastleKingSide = false;
                canBlackCastleQueenSide = false;
            }

            if (Piece.getType(piece) == Piece.Rook) {
                if (file == 1) {
                    // leftBlackRookMoved = true;
                    canBlackCastleQueenSide = false;
                }
                if (file == 8) {
                    // rightBlackRookMoved = true;
                    canBlackCastleKingSide = false;
                }
            }
        }
    }

    public void nextTurn() {
        if (colorTurn == Piece.Black)
            fullMoveNumber++;

        // Switches the color turn
        colorTurn = colorTurn ^ Piece.colorMask;
    }

    public void removeEnPassantPiece(int targetSquare) {
        if (targetSquare == enPassantSquare) {
            if (colorTurn == Piece.White) {
                board[targetSquare + 8] = 0;
            } else {
                board[targetSquare - 8] = 0;
            }
        }

        // Resets en passant square
        enPassantSquare = -1;
    }

    public void setEnPassant(int startSquare, int color) {
        if (color == Piece.White) {
            enPassantSquare = startSquare - 8;
        }

        else {
            enPassantSquare = startSquare + 8;
        }
    }

    public void setCheck() {
        moves.forEach((startSquare, targetSquares) -> {
            int startPiece = board[startSquare];

            for (int targetSquare : targetSquares) {
                int targetPiece = board[targetSquare];
                if (Piece.getType(targetPiece) == Piece.King && !Piece.sameColor(startPiece, targetPiece)) {
                    inCheck = targetSquare;
                    break;
                }
            }
        });
    }

    public void movedInCheck(int color) {
        moves.forEach((startSquare, targetSquares) -> {
            int startPiece = board[startSquare];

            for (int targetSquare : targetSquares) {
                int targetPiece = board[targetSquare];

                if (Piece.sameColor(startPiece, color)) {
                    if (Piece.getType(targetPiece) == Piece.King && !Piece.sameColor(startPiece, targetPiece)) {
                        inCheck = targetSquare;
                        break;
                    }
                }
            }

        });
    }

    public int[] getBoard() {
        return board;
    }

    public int getBoard(int square) {
        return this.board[square];
    }

    public void setBoard(int[] board) {
        this.board = new int[64];

        for (int i = 0; i < 64; i++) {
            this.board[i] = board[i];
        }
    }

    public void setBoard(int square, int piece) {
        this.board[square] = piece;
    }

    public int getColorTurn() {
        return colorTurn;
    }

    public void setColorTurn(int colorTurn) {
        this.colorTurn = colorTurn;
    }

    public boolean isBlackCanCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public void setBlackCanCastleKingSide(boolean canBlackCastleKingSide) {
        this.canBlackCastleKingSide = canBlackCastleKingSide;
    }

    public boolean isBlackCanCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    public void setBlackCanCastleQueenSide(boolean canBlackCastleQueenSide) {
        this.canBlackCastleQueenSide = canBlackCastleQueenSide;
    }

    public boolean isWhiteCanCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public void setWhiteCanCastleKingSide(boolean canWhiteCastleKingSide) {
        this.canWhiteCastleKingSide = canWhiteCastleKingSide;
    }

    public boolean isWhiteCanCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public void setWhiteCanCastleQueenSide(boolean canWhiteCastleQueenSide) {
        this.canWhiteCastleQueenSide = canWhiteCastleQueenSide;
    }

    public int getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(int enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public void setHalfMoveClock(int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
    }

    public int getFullMoveNumber() {
        return fullMoveNumber;
    }

    public void setFullMoveNumber(int fullMoveNumber) {
        this.fullMoveNumber = fullMoveNumber;
    }

    public int getInCheck() {
        return inCheck;
    }

    public void setInCheck(int inCheck) {
        this.inCheck = inCheck;
    }

    public HashMap<Integer, List<Integer>> getMoves() {
        return moves;
    }

    public void setMoves(HashMap<Integer, List<Integer>> moves) {
        this.moves = moves;
    }
}
