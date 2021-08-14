import java.util.ArrayList;
import java.util.List;

public class BoardStatus {
    public static int[] board = new int[64];
    public static int colorTurn;
    public static int enPassantSquare;
    public static int halfMoveClock;
    public static int fullMoveNumber;
    public static int inCheck = -1;

    public static int whiteKingSquare = 60;
    public static int blackKingSquare = 4;

    public static List<Integer> changedBoardSquares;

    // Variables for castling
    public static boolean leftWhiteRookMoved = false;
    public static boolean rightWhiteRookMoved = false;
    public static boolean leftBlackRookMoved = false;
    public static boolean rightBlackRookMoved = false;
    public static boolean whiteKingMoved = false;
    public static boolean blackKingMoved = false;

    public static boolean checkmate = false;

    public static void move(int startSquare, int targetSquare, int[] board) {
        changedBoardSquares = new ArrayList<>();

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
        changeBoard(startSquare, Piece.Empty);
        changeBoard(targetSquare, piece);

        // Moves rook if castling
        moveRookForCastling(piece, startSquare, targetSquare);

        // Generates all the moves for both colored pieces
        Move.updateMoves(changedBoardSquares, board);
        // Move.validateMoves();
        setCheck();

        // // If in checkmate, end the game
        // if (Move.moves.size() == 1) {
        // System.out.println("Checkmate");
        // }

        nextTurn();
    }

    public static void moveRookForCastling(int piece, int startSquare, int targetSquare) {
        if (piece == Piece.King + Piece.White) {
            if (startSquare - targetSquare == 2) {
                int castlingRook = board[56];

                changeBoard(startSquare - 1, castlingRook);
                changeBoard(56, Piece.Empty);
            }

            if (startSquare - targetSquare == -2) {
                int castlingRook = board[63];
                changeBoard(startSquare + 1, castlingRook);
                changeBoard(63, Piece.Empty);
            }
        }

        else if (piece == Piece.King + Piece.Black) {
            if (startSquare - targetSquare == 2) {
                int castlingRook = board[0];
                changeBoard(startSquare - 1, castlingRook);
                changeBoard(0, Piece.Empty);
            }

            if (startSquare - targetSquare == -2) {
                int castlingRook = board[7];
                changeBoard(startSquare + 1, castlingRook);
                changeBoard(7, Piece.Empty);
            }
        }
    }

    // Fix mixed up colorTurn
    public static void setCastling(int piece, int file) {
        if (colorTurn == Piece.Black) {
            if (Piece.getType(piece) == Piece.King) {
                whiteKingMoved = true;
            }

            if (Piece.getType(piece) == Piece.Rook) {
                if (file == 1) {
                    leftWhiteRookMoved = true;
                }
                if (file == 8) {
                    rightWhiteRookMoved = true;
                }
            }
        }

        else {
            if (Piece.getType(piece) == Piece.King) {
                blackKingMoved = true;
            }

            if (Piece.getType(piece) == Piece.Rook) {
                if (file == 1) {
                    leftBlackRookMoved = true;
                }
                if (file == 8) {
                    rightBlackRookMoved = true;
                }
            }
        }
    }

    public static void nextTurn() {
        if (colorTurn == Piece.Black)
            fullMoveNumber++;

        // Switches the color turn
        colorTurn = colorTurn ^ Piece.colorMask;
    }

    public static void removeEnPassantPiece(int targetSquare) {
        if (targetSquare == enPassantSquare) {
            if (colorTurn == Piece.White) {
                changeBoard(targetSquare + 8, 0);
            } else {
                changeBoard(targetSquare - 8, 0);
            }
        }

        // Resets en passant square
        enPassantSquare = -1;
    }

    public static void setEnPassant(int startSquare, int color) {
        if (color == Piece.White) {
            enPassantSquare = startSquare - 8;
        }

        else {
            enPassantSquare = startSquare + 8;
        }
    }

    public static int peek(int square) {
        return board[square];
    }

    public static void setBoard(int square, int piece) {
        board[square] = piece;
    }

    public static boolean setCheck() {
        Move.moves.forEach((startSquare, targetSquares) -> {
            int startPiece = board[startSquare];

            for (int targetSquare : targetSquares) {
                int targetPiece = board[targetSquare];
                if (Piece.getType(targetPiece) == Piece.King && !Piece.sameColor(startPiece, targetPiece)) {
                    inCheck = targetSquare;
                    break;
                }
            }
        });

        return (inCheck == -1) ? false : true;
    }

    public static void changeBoard(int square, int piece) {
        board[square] = piece;
        changedBoardSquares.add(square);

        if (piece == Piece.King + Piece.White) {
            whiteKingSquare = square;
        }

        if (piece == Piece.King + Piece.Black) {
            blackKingSquare = square;
        }
    }
}
