package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece [][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition position = new ChessPosition(i, j);
                if (this.getPiece(position) != null){
                    sb.append(this.getPiece(position).getPieceType());
                }
            }
        }
        return sb.toString();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Use loop to populate with pawns
        for (int pawnCol = 0; pawnCol < 8; pawnCol++) {
            board[1][pawnCol] = new ChessPiece(WHITE, ChessPiece.PieceType.PAWN);
            board[6][pawnCol] = new ChessPiece(BLACK, ChessPiece.PieceType.PAWN);
        }
        // Initialize white pieces other than pawns
        board[0][0] = new ChessPiece(WHITE, ChessPiece.PieceType.ROOK);
        board[0][7] = new ChessPiece(WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][6] = new ChessPiece(WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(WHITE, ChessPiece.PieceType.BISHOP);
        board[0][5] = new ChessPiece(WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(WHITE, ChessPiece.PieceType.KING);

        // Initialize black pieces other than pawns
        board[7][0] = new ChessPiece(BLACK, ChessPiece.PieceType.ROOK);
        board[7][7] = new ChessPiece(BLACK, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][6] = new ChessPiece(BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(BLACK, ChessPiece.PieceType.BISHOP);
        board[7][5] = new ChessPiece(BLACK, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(BLACK, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(BLACK, ChessPiece.PieceType.KING);

    }
}
