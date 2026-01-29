package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MoveCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessGame.TeamColor color;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoveCalculator that = (MoveCalculator) o;
        return Objects.equals(board, that.board) && Objects.equals(position, that.position) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, position, color);
    }

    public MoveCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        this.board = board;
        this.position = position;
        this.color = color;
    }

    private Collection<ChessMove> teamChecker(ChessPosition nextPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (board.getPiece(nextPosition) == null) {
            ChessMove nextMove = new ChessMove(position, nextPosition, null);
            moves.add(nextMove);
        } else {
            if (board.getPiece(nextPosition).getTeamColor() != color) {
                ChessMove nextMove = new ChessMove(position, nextPosition, null);
                moves.add(nextMove);
            }
        }
        return moves;
    }

    private Collection<ChessMove> kingHelper(int rowMove, int colMove){
        ChessPosition nextPosition = new ChessPosition(position.getRow() + rowMove, position.getColumn() + colMove);
        return teamChecker(nextPosition);
    }

    public Collection<ChessMove> moveKing(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() < 8){
            moves.addAll(kingHelper(1, 0));
            if (position.getColumn() < 8){
                moves.addAll(kingHelper(1, 1));
            }
            if (position.getColumn() > 1){
                moves.addAll(kingHelper(1, -1));
            }
        }
        if (position.getRow() > 1){
            moves.addAll(kingHelper(-1, 0));
            if (position.getColumn() < 8){
                moves.addAll(kingHelper(-1, 1));
            }
            if (position.getColumn() > 1){
                moves.addAll(kingHelper(-1, -1));
            }
        }
        if (position.getColumn() > 1){
            moves.addAll(kingHelper(0, -1));
        }
        if (position.getColumn() < 8){
            moves.addAll(kingHelper(0, 1));
        }
        return moves;
    }

    private Collection<ChessMove> knightHelper(int rowMove, int colMove){
        ChessPosition nextPosition = new ChessPosition(position.getRow() + rowMove, position.getColumn() + colMove);
        return teamChecker(nextPosition);
    }

    public Collection<ChessMove> moveKnight(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() <= 6){
            if (position.getColumn() >= 2){
                moves.addAll(knightHelper(2, -1));
            }
            if (position.getColumn() <= 7){
                moves.addAll(knightHelper(2, 1));
            }
        }
        if (position.getRow() >= 3){
            if (position.getColumn() >= 2){
                moves.addAll(knightHelper(-2, -1));
            }
            if (position.getColumn() <= 7){
                moves.addAll(knightHelper(-2, 1));
            }
        }
        if (position.getColumn() >= 3){
            if (position.getRow() >= 2){
                moves.addAll(knightHelper(-1, -2));
            }
            if (position.getRow() <= 7){
                moves.addAll(knightHelper(1, -2));
            }
        }
        if (position.getColumn() <= 6){
            if (position.getRow() >= 2){
                moves.addAll(knightHelper(-1, 2));
            }
            if (position.getRow() <= 7){
                moves.addAll(knightHelper(1, 2));
            }
        }
        return moves;
    }

    private Collection<ChessMove> lateralMovement(int rowMove, int colMove){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition nextPosition = new ChessPosition(position.getRow(), position.getColumn());
        while ((nextPosition.getColumn() < 8 || colMove != 1) &&
                (nextPosition.getColumn() > 1 || colMove != -1) &&
                (nextPosition.getRow() < 8 || rowMove != 1) &&
                (nextPosition.getRow() > 1 || rowMove != -1)){
            nextPosition = new ChessPosition(nextPosition.getRow() + rowMove, nextPosition.getColumn() + colMove);
            if (board.getPiece(nextPosition) != null){
                if (board.getPiece(nextPosition).getTeamColor() != color) {
                    ChessMove nextMove = new ChessMove(position, nextPosition, null);
                    moves.add(nextMove);
                }
                break;
            }
            ChessMove nextMove = new ChessMove(position, nextPosition, null);
            moves.add(nextMove);
        }
        return moves;
    }

    public Collection<ChessMove> moveRook(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(lateralMovement(1, 0));
        moves.addAll(lateralMovement(-1, 0));
        moves.addAll(lateralMovement(0, 1));
        moves.addAll(lateralMovement(0, -1));
        return moves;
    }

    public Collection<ChessMove> moveBishop(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(lateralMovement(1, 1));
        moves.addAll(lateralMovement(-1, -1));
        moves.addAll(lateralMovement(1, -1));
        moves.addAll(lateralMovement(-1, 1));
        return moves;
    }

    public Collection<ChessMove> moveQueen(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(lateralMovement(1, 0));
        moves.addAll(lateralMovement(-1, 0));
        moves.addAll(lateralMovement(0, 1));
        moves.addAll(lateralMovement(0, -1));
        moves.addAll(lateralMovement(1, 1));
        moves.addAll(lateralMovement(-1, -1));
        moves.addAll(lateralMovement(1, -1));
        moves.addAll(lateralMovement(-1, 1));
        return moves;
    }

    private Collection<ChessMove> promotion(ChessPosition nextPosition){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessMove knightPromotion = new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT);
        ChessMove queenPromotion = new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN);
        ChessMove bishopPromotion = new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP);
        ChessMove rookPromotion = new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK);
        moves.add(knightPromotion);
        moves.add(queenPromotion);
        moves.add(bishopPromotion);
        moves.add(rookPromotion);
        return moves;
    }

    private Collection<ChessMove> movePawnHelper(int row){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition forward = new ChessPosition(position.getRow() + row, position.getColumn());
        if (board.getPiece(forward) == null){
            if (forward.getRow() == 8 || forward.getRow() == 1){
                moves.addAll(promotion(forward));
            } else{
                ChessMove forwardMove = new ChessMove(position, forward, null);
                moves.add(forwardMove);
            }
            if ((forward.getRow() == 3 && row == 1) || (forward.getRow() == 6 && row == -1)){
                ChessPosition forwardTwo = new ChessPosition(position.getRow() + row * 2, position.getColumn());
                if (board.getPiece(forwardTwo) == null){
                    ChessMove forwardTwoMove = new ChessMove(position, forwardTwo, null);
                    moves.add(forwardTwoMove);
                }
            }
        }
        if (position.getColumn() > 1){
            ChessPosition forwardLeft = new ChessPosition(position.getRow() + row, position.getColumn() - 1);
            if (board.getPiece(forwardLeft) != null && board.getPiece(forwardLeft).getTeamColor() != color) {
                if (forwardLeft.getRow() == 8 || forwardLeft.getRow() == 1){
                    moves.addAll(promotion(forwardLeft));
                } else{
                    ChessMove forwardLeftMove = new ChessMove(position, forwardLeft, null);
                    moves.add(forwardLeftMove);
                }
            }
        }
        if (position.getColumn() < 8){
            ChessPosition forwardRight = new ChessPosition(position.getRow() + row, position.getColumn() + 1);
            if (board.getPiece(forwardRight) != null && board.getPiece(forwardRight).getTeamColor() != color) {
                if (forwardRight.getRow() == 8 || forwardRight.getRow() == 1){
                    moves.addAll(promotion(forwardRight));
                } else {
                    ChessMove forwardRightMove = new ChessMove(position, forwardRight, null);
                    moves.add(forwardRightMove);
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> movePawn() {
        if (color == ChessGame.TeamColor.BLACK){
            return movePawnHelper(-1);
        }
        return movePawnHelper(1);
    }
}
