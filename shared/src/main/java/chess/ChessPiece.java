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
//        throw new RuntimeException("Not implemented");
//        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
//        /* Breakdown of the pawn movements:
//         * What color is the piece?
//         * If it is white -> will be moving upwards
//         * If it is black -> will be moving downwards
//         * If it is the first move -> It can move forward one space or two
//         * If it is the middle of the board -> It can move forward one space
//         * If there is any piece in front of it -> It cannot move forward
//         * If there is a piece of the opposite color forward to the right or to the left -> it can capture the piece
//         * If the pawn makes it to the edge of the board -> Promote the pawn
//         * How to implement:
//         * Least specific to most specific:
//         *  Color
//         *      Blocked in front?
//         *          At starting place?
//         *              Can capture?
//         *                  Will promote?
//         *
//         * Most specific to lest specific
//         *
//         */
//        if (this.piece == PieceType.PAWN) {
//
//            if (this.color == ChessGame.TeamColor.WHITE) {
//                ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
//                ChessPosition forwardLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
//
//                if (myPosition.getRow() == 2) {
//                    ChessPosition forwardTwo = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
//                    if (board.getPiece(forwardTwo) == null && board.getPiece(forwardOne) == null) {
//                        ChessMove forwardTwoMove = new ChessMove(myPosition, forwardTwo, PieceType.PAWN);
//                        moves.add(forwardTwoMove);
//                    }
//                }
//                if (board.getPiece(forwardOne) == null) {
//                    ChessMove forwardOneMove = new ChessMove(myPosition, forwardOne, PieceType.PAWN);
//                    moves.add(forwardOneMove);
//                }
//            }
//        }
//        return moves;
        MoveCalculator calculateMoves = new MoveCalculator(board, myPosition, color);
        if (this.piece == PieceType.PAWN) {
            return calculateMoves.calculatePawnMoves();
        }
        if (this.piece == PieceType.KING)
            return calculateMoves.calculateKingMoves();
        if (this.piece == PieceType.ROOK) {
            return calculateMoves.calculateRookMoves();
        }
        if (this.piece == PieceType.BISHOP) {
            return calculateMoves.calculateBishopMoves();
        }
        if (this.piece == PieceType.QUEEN) {
            return calculateMoves.calculateQueenMoves();
        }
        if (this.piece == PieceType.KNIGHT) {
            return calculateMoves.calculateKnightMoves();
        }
        return calculateMoves.calculatePawnMoves();
    }
}
