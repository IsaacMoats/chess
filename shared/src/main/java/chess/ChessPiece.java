package chess;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    final private ChessGame.TeamColor color;
    final private ChessPiece.PieceType piece;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && piece == that.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, piece);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", piece=" + piece +
                '}';
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.piece = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return piece;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MoveCalculator calculateMoves = new MoveCalculator(board, myPosition, color);
        if (this.piece == PieceType.PAWN) {
            return calculateMoves.movePawn();
        }
        if (this.piece == PieceType.KING)
            return calculateMoves.moveKing();
        if (this.piece == PieceType.ROOK) {
            return calculateMoves.moveRook();
        }
        if (this.piece == PieceType.BISHOP) {
            return calculateMoves.moveBishop();
        }
        if (this.piece == PieceType.QUEEN) {
            return calculateMoves.moveQueen();
        }
        if (this.piece == PieceType.KNIGHT) {
            return calculateMoves.moveKnight();
        }
        return calculateMoves.movePawn();
    }
}
