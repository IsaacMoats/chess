package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    private ChessPosition findKing(TeamColor team){
        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition positionCheck = new ChessPosition(row, col);
                if (board.getPiece(positionCheck) != null &&
                    board.getPiece(positionCheck).getPieceType() == ChessPiece.PieceType.KING &&
                    board.getPiece(positionCheck).getTeamColor() == team){
                    return positionCheck;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition position = new ChessPosition(i, j);
                sb.append(board.getPiece(position).getPieceType());
            }
        }
        return sb.toString();
    }

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        /*
         * Only valid moves while in check is getting the king out of check - can move the king, capture the piece
         * causing the check, or block the move
         * simulate move with a piece and then check to see if the king is in danger after the proposed move
         * then for valid moves for the king simulate the move and then make sure that the king is no longer in check
         * if it is already in check
         *
         * */
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return null;
        moves.addAll(piece.pieceMoves(this.board, startPosition));
        for (ChessMove move : moves){
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece capturePiece = board.getPiece(end);
            if (teamTurn == TeamColor.WHITE){
                this.board.addPiece(end, piece);
                this.board.addPiece(start, null);
                if (isInCheck(TeamColor.WHITE)){
                    moves.remove(move);
                }
            } else {
                this.board.addPiece(end, piece);
                this.board.addPiece(start, null);
                if (isInCheck(TeamColor.BLACK)){
                    moves.remove(move);
                }
            }
            this.board.addPiece(end, capturePiece);
            this.board.addPiece(start, piece);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        if (validMoves(start).contains(move)) {
            ChessPiece piece = board.getPiece(start);
            this.board.addPiece(end, piece);
            this.board.addPiece(start, null);
        } else {
            throw new InvalidMoveException("Bad move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        /*
         * Find where the king is
         * Keep a member variable of the location of both kings - will be updated every time the king moves
         * Determine if that position is in the list of possible movements of the opposite team
         * The opposite team will move, then the list of possible movements should be updated to account for the movement
         * Make a copy of the board and remove the king. if the space ends up in a possible move of the other team, the king is in check
         */
        ChessPosition kingPosition = findKing(teamColor);
        ChessPiece kingPiece = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        board.addPiece(kingPosition, null);
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition checkPosition = new ChessPosition(row, col);
                if(board.getPiece(checkPosition) != null){
                    if(board.getPiece(checkPosition).getTeamColor() != teamColor){
                        moves.addAll(board.getPiece(checkPosition).pieceMoves(this.board, checkPosition));
                        for (ChessMove move : moves) {
                            if (move.getEndPosition().getColumn() == kingPosition.getColumn() &&
                                move.getEndPosition().getRow() == kingPosition.getRow()) {
                                board.addPiece(kingPosition, kingPiece);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        board.addPiece(kingPosition, kingPiece);
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        // Call valid moves and make if empty return true
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
