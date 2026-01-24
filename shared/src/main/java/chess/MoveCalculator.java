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

    public Collection<ChessMove> calculatePawnMoves(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (this.color == ChessGame.TeamColor.WHITE) {
            ChessPosition forwardOne = new ChessPosition(this.position.getRow() + 1, this.position.getColumn());
            if (position.getColumn() != 1) {
                ChessPosition forwardLeft = new ChessPosition(this.position.getRow() + 1, this.position.getColumn() - 1);
                if (board.getPiece(forwardLeft) != null) {
                    if (board.getPiece(forwardLeft).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (forwardLeft.getRow() == 8) {
                            ChessMove forwardLeftMoveQueen = new ChessMove(position, forwardLeft, ChessPiece.PieceType.QUEEN);
                            ChessMove forwardLeftMoveRook = new ChessMove(position, forwardLeft, ChessPiece.PieceType.ROOK);
                            ChessMove forwardLeftMoveBishop = new ChessMove(position, forwardLeft, ChessPiece.PieceType.BISHOP);
                            ChessMove forwardLeftMoveKnight = new ChessMove(position, forwardLeft, ChessPiece.PieceType.KNIGHT);
                            moves.add(forwardLeftMoveKnight);
                            moves.add(forwardLeftMoveBishop);
                            moves.add(forwardLeftMoveRook);
                            moves.add(forwardLeftMoveQueen);
                        } else {
                            ChessMove forwardLeftMove = new ChessMove(position, forwardLeft, null);
                            moves.add(forwardLeftMove);
                        }
                    }
                }
            }
            if (position.getColumn() != 8) {
                ChessPosition forwardRight = new ChessPosition(this.position.getRow() + 1, this.position.getColumn() + 1);
                if (board.getPiece(forwardRight) != null) {
                    if (board.getPiece(forwardRight).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (forwardRight.getRow() == 8) {
                            ChessMove forwardRightMoveQueen = new ChessMove(position, forwardRight, ChessPiece.PieceType.QUEEN);
                            ChessMove forwardRightMoveRook = new ChessMove(position, forwardRight, ChessPiece.PieceType.ROOK);
                            ChessMove forwardRightMoveBishop = new ChessMove(position, forwardRight, ChessPiece.PieceType.BISHOP);
                            ChessMove forwardRightMoveKnight = new ChessMove(position, forwardRight, ChessPiece.PieceType.KNIGHT);
                            moves.add(forwardRightMoveKnight);
                            moves.add(forwardRightMoveBishop);
                            moves.add(forwardRightMoveRook);
                            moves.add(forwardRightMoveQueen);
                        } else {
                            ChessMove forwardRightMove = new ChessMove(position, forwardRight, null);
                            moves.add(forwardRightMove);
                        }
                    }
                }
            }
            if (this.position.getRow() == 2) {
                ChessPosition forwardTwo = new ChessPosition(this.position.getRow() + 2, this.position.getColumn());
                if (board.getPiece(forwardTwo) == null && board.getPiece(forwardOne) == null) {
                    ChessMove forwardTwoMove = new ChessMove(position, forwardTwo, null);
                    moves.add(forwardTwoMove);
                }
            }
            if (board.getPiece(forwardOne) == null) {
                if (forwardOne.getRow() == 8) {
                    ChessMove forwardOneMoveQueen = new ChessMove(position, forwardOne, ChessPiece.PieceType.QUEEN);
                    ChessMove forwardOneMoveRook = new ChessMove(position, forwardOne, ChessPiece.PieceType.ROOK);
                    ChessMove forwardOneMoveBishop = new ChessMove(position, forwardOne, ChessPiece.PieceType.BISHOP);
                    ChessMove forwardOneMoveKnight = new ChessMove(position, forwardOne, ChessPiece.PieceType.KNIGHT);
                    moves.add(forwardOneMoveKnight);
                    moves.add(forwardOneMoveBishop);
                    moves.add(forwardOneMoveRook);
                    moves.add(forwardOneMoveQueen);

                } else {
                    ChessMove forwardOneMove = new ChessMove(position, forwardOne, null);
                    moves.add(forwardOneMove);
                }
            }
        }

        if (this.color == ChessGame.TeamColor.BLACK) {
            if (position.getRow() != 1) {
                ChessPosition forwardOne = new ChessPosition(position.getRow() - 1, position.getColumn());
                if (position.getColumn() != 1) {
                    ChessPosition forwardLeft = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
                    if (board.getPiece(forwardLeft) != null) {
                        if (board.getPiece(forwardLeft).getTeamColor() == ChessGame.TeamColor.WHITE) {
                            if (forwardLeft.getRow() == 1) {
                                ChessMove forwardLeftMoveQueen = new ChessMove(position, forwardLeft, ChessPiece.PieceType.QUEEN);
                                ChessMove forwardLeftMoveRook = new ChessMove(position, forwardLeft, ChessPiece.PieceType.ROOK);
                                ChessMove forwardLeftMoveBishop = new ChessMove(position, forwardLeft, ChessPiece.PieceType.BISHOP);
                                ChessMove forwardLeftMoveKnight = new ChessMove(position, forwardLeft, ChessPiece.PieceType.KNIGHT);
                                moves.add(forwardLeftMoveKnight);
                                moves.add(forwardLeftMoveBishop);
                                moves.add(forwardLeftMoveRook);
                                moves.add(forwardLeftMoveQueen);
                            } else {
                                ChessMove forwardLeftMove = new ChessMove(position, forwardLeft, null);
                                moves.add(forwardLeftMove);
                            }
                        }
                    }
                }
                if (position.getColumn() != 8) {
                    ChessPosition forwardRight = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
                    if (board.getPiece(forwardRight) != null) {
                        if (forwardRight.getRow() == 1) {
                            ChessMove forwardRightMoveQueen = new ChessMove(position, forwardRight, ChessPiece.PieceType.QUEEN);
                            ChessMove forwardRightMoveRook = new ChessMove(position, forwardRight, ChessPiece.PieceType.ROOK);
                            ChessMove forwardRightMoveBishop = new ChessMove(position, forwardRight, ChessPiece.PieceType.BISHOP);
                            ChessMove forwardRightMoveKnight = new ChessMove(position, forwardRight, ChessPiece.PieceType.KNIGHT);
                            moves.add(forwardRightMoveKnight);
                            moves.add(forwardRightMoveBishop);
                            moves.add(forwardRightMoveRook);
                            moves.add(forwardRightMoveQueen);
                        } else {
                            ChessMove forwardRightMove = new ChessMove(position, forwardRight, null);
                            moves.add(forwardRightMove);
                        }
                    }
                }
                if (this.position.getRow() == 7) {
                    ChessPosition forwardTwo = new ChessPosition(this.position.getRow() - 2, this.position.getColumn());
                    if (board.getPiece(forwardTwo) == null && board.getPiece(forwardOne) == null) {
                        ChessMove forwardTwoMove = new ChessMove(position, forwardTwo, null);
                        moves.add(forwardTwoMove);
                    }
                }
                if (board.getPiece(forwardOne) == null) {
                    if (forwardOne.getRow() == 1) {
                        ChessMove forwardOneMoveQueen = new ChessMove(position, forwardOne, ChessPiece.PieceType.QUEEN);
                        ChessMove forwardOneMoveRook = new ChessMove(position, forwardOne, ChessPiece.PieceType.ROOK);
                        ChessMove forwardOneMoveBishop = new ChessMove(position, forwardOne, ChessPiece.PieceType.BISHOP);
                        ChessMove forwardOneMoveKnight = new ChessMove(position, forwardOne, ChessPiece.PieceType.KNIGHT);
                        moves.add(forwardOneMoveKnight);
                        moves.add(forwardOneMoveBishop);
                        moves.add(forwardOneMoveRook);
                        moves.add(forwardOneMoveQueen);

                    } else {
                        ChessMove forwardOneMove = new ChessMove(position, forwardOne, null);
                        moves.add(forwardOneMove);
                    }
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> calculateKingMoves(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() != 8) {
            ChessPosition forward = new ChessPosition(position.getRow() + 1, position.getColumn());
            if (board.getPiece(forward) == null) {
                ChessMove forwardMove = new ChessMove(position, forward, null);
                moves.add(forwardMove);
            } else {
                if (board.getPiece(forward).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, forward, null);
                    moves.add(forwardMove);
                }
            }
            if (position.getColumn() != 1) {
                ChessPosition forwardLeft = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
                if (board.getPiece(forwardLeft) == null) {
                    ChessMove forwardLeftMove = new ChessMove(position, forwardLeft, null);
                    moves.add(forwardLeftMove);
                } else {
                    if (board.getPiece(forwardLeft).getTeamColor() != color) {
                        ChessMove forwardLeftMove = new ChessMove(position, forwardLeft, null);
                        moves.add(forwardLeftMove);
                    }
                }
            }
            if (position.getColumn() != 8) {
                ChessPosition forwardRight = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
                if (board.getPiece(forwardRight) == null) {
                    ChessMove forwardRightMove = new ChessMove(position, forwardRight, null);
                    moves.add(forwardRightMove);
                } else {
                    if (board.getPiece(forwardRight).getTeamColor() != color) {
                        ChessMove forwardRightMove = new ChessMove(position, forwardRight, null);
                        moves.add(forwardRightMove);
                    }
                }
            }
        }
        if (position.getRow() != 1) {
            ChessPosition backward = new ChessPosition(position.getRow() - 1, position.getColumn());
            if (board.getPiece(backward) == null) {
                ChessMove forwardMove = new ChessMove(position, backward, null);
                moves.add(forwardMove);
            } else {
                if (board.getPiece(backward).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, backward, null);
                    moves.add(forwardMove);
                }
            }
            if (position.getColumn() != 1) {
                ChessPosition backwardLeft = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
                if (board.getPiece(backwardLeft) == null) {
                    ChessMove backwardLeftMove = new ChessMove(position, backwardLeft, null);
                    moves.add(backwardLeftMove);
                } else {
                    if (board.getPiece(backwardLeft).getTeamColor() != color) {
                        ChessMove backwardLeftMove = new ChessMove(position, backwardLeft, null);
                        moves.add(backwardLeftMove);
                    }
                }
            }
            if (position.getColumn() != 8) {
                ChessPosition backwardRight = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
                if (board.getPiece(backwardRight) == null) {
                    ChessMove backwardRightMove = new ChessMove(position, backwardRight, null);
                    moves.add(backwardRightMove);
                } else {
                    if (board.getPiece(backwardRight).getTeamColor() != color) {
                        ChessMove backwardRightMove = new ChessMove(position, backwardRight, null);
                        moves.add(backwardRightMove);
                    }
                }
            }
        }
        if (position.getColumn() != 1) {
            ChessPosition left = new ChessPosition(position.getRow(), position.getColumn() - 1);
            if (board.getPiece(left) == null) {
                ChessMove leftMove = new ChessMove(position, left, null);
                moves.add(leftMove);
            } else {
                if (board.getPiece(left).getTeamColor() != color) {
                    ChessMove leftMove = new ChessMove(position, left, null);
                    moves.add(leftMove);
                }
            }
        }
        if (position.getColumn() != 8) {
            ChessPosition right = new ChessPosition(position.getRow(), position.getColumn() + 1);
            if (board.getPiece(right) == null) {
                ChessMove rightMove = new ChessMove(position, right, null);
                moves.add(rightMove);
            } else {
                if (board.getPiece(right).getTeamColor() != color) {
                    ChessMove rightMove = new ChessMove(position, right, null);
                    moves.add(rightMove);
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculateForwardContinuous(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() == 8) {
            return moves;
        }
        ChessPosition forward = new ChessPosition(position.getRow() + 1, position.getColumn());
        if (board.getPiece(forward) != null) {
            if (board.getPiece(forward).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, forward, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, forward, null);
            moves.add(forwardMove);
        }
        while (forward.getRow() != 8) {
            forward =  new ChessPosition(forward.getRow() + 1, position.getColumn());
            if (board.getPiece(forward) != null) {
                if (board.getPiece(forward).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, forward, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, forward, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    private Collection<ChessMove> calculateBackwardContinuous(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() == 1) {
            return moves;
        }
        ChessPosition backward = new ChessPosition(position.getRow() - 1, position.getColumn());
        if (board.getPiece(backward) != null) {
            if (board.getPiece(backward).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, backward, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, backward, null);
            moves.add(forwardMove);
        }
        while (backward.getRow() != 1) {
            backward =  new ChessPosition(backward.getRow() - 1, position.getColumn());
            if (board.getPiece(backward) != null) {
                if (board.getPiece(backward).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, backward, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, backward, null);
            moves.add(forwardMove);

        }
        return moves;
    }

    private Collection<ChessMove> calculateLeftContinuous(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getColumn() == 1) {
            return moves;
        }
        ChessPosition left = new ChessPosition(position.getRow(), position.getColumn() - 1);
        if (board.getPiece(left) != null) {
            if (board.getPiece(left).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, left, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        while (left.getColumn() != 1) {
            left =  new ChessPosition(left.getRow(), left.getColumn() - 1);
            if (board.getPiece(left) != null) {
                if (board.getPiece(left).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, left, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    private Collection<ChessMove> calculateRightContinuous(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getColumn() == 8) {
            return moves;
        }
        ChessPosition right = new ChessPosition(position.getRow(), position.getColumn() + 1);
        if (board.getPiece(right) != null) {
            if (board.getPiece(right).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, right, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, right, null);
            moves.add(forwardMove);
        }
        while (right.getColumn() != 8) {
            right =  new ChessPosition(right.getRow(), right.getColumn() + 1);
            if (board.getPiece(right) != null) {
                if (board.getPiece(right).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, right, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, right, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    private Collection<ChessMove> calculateForwardLeft(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getColumn() == 1 || position.getRow() == 8) {
            return moves;
        }
        ChessPosition left = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
        if (board.getPiece(left) != null) {
            if (board.getPiece(left).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, left, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        while (left.getColumn() != 1 && left.getRow() != 8) {
            left =  new ChessPosition(left.getRow() + 1, left.getColumn() - 1);
            if (board.getPiece(left) != null) {
                if (board.getPiece(left).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, left, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    private Collection<ChessMove> calculateForwardRight(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getColumn() == 8 || position.getRow() == 8) {
            return moves;
        }
        ChessPosition left = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
        if (board.getPiece(left) != null) {
            if (board.getPiece(left).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, left, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        while (left.getColumn() != 8 && left.getRow() != 8) {
            left =  new ChessPosition(left.getRow() + 1, left.getColumn() + 1);
            if (board.getPiece(left) != null) {
                if (board.getPiece(left).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, left, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    private Collection<ChessMove> calculateBackwardLeft(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getColumn() == 1 || position.getRow() == 1) {
            return moves;
        }
        ChessPosition left = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
        if (board.getPiece(left) != null) {
            if (board.getPiece(left).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, left, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        while (left.getColumn() != 1 && left.getRow() != 1) {
            left =  new ChessPosition(left.getRow() - 1, left.getColumn() - 1);
            if (board.getPiece(left) != null) {
                if (board.getPiece(left).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, left, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    private Collection<ChessMove> calculateBackwardRight(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getColumn() == 8 || position.getRow() == 1) {
            return moves;
        }
        ChessPosition left = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
        if (board.getPiece(left) != null) {
            if (board.getPiece(left).getTeamColor() != color) {
                ChessMove forwardMove = new ChessMove(position, left, null);
                moves.add(forwardMove);
            }
            return moves;
        } else {
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        while (left.getColumn() != 8 && left.getRow() != 1) {
            left =  new ChessPosition(left.getRow() - 1, left.getColumn() + 1);
            if (board.getPiece(left) != null) {
                if (board.getPiece(left).getTeamColor() != color) {
                    ChessMove forwardMove = new ChessMove(position, left, null);
                    moves.add(forwardMove);
                }
                break;
            }
            ChessMove forwardMove = new ChessMove(position, left, null);
            moves.add(forwardMove);
        }
        return moves;
    }

    public Collection<ChessMove> calculateRookMoves(){
        ArrayList<ChessMove> moves = new ArrayList<>(calculateForwardContinuous());
        moves.addAll(calculateBackwardContinuous());
        moves.addAll(calculateLeftContinuous());
        moves.addAll(calculateRightContinuous());
        return moves;
    }

    public Collection<ChessMove> calculateBishopMoves(){
        ArrayList<ChessMove> moves = new ArrayList<>(calculateForwardRight());
        moves.addAll(calculateForwardLeft());
        moves.addAll(calculateBackwardLeft());
        moves.addAll(calculateBackwardRight());
        return moves;
    }

    public Collection<ChessMove> calculateQueenMoves(){
        ArrayList<ChessMove> moves = new ArrayList<>(calculateBishopMoves());
        moves.addAll(calculateRookMoves());
        return moves;
    }

    public Collection<ChessMove> calculateKnightMoves(){
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (position.getRow() <= 6) {
            if (position.getColumn() >= 2) {
                ChessPosition forwardLeft = new ChessPosition(position.getRow() + 2, position.getColumn() - 1);
                ChessMove forwardLeftMove = new ChessMove(position, forwardLeft, null);
                if (board.getPiece(forwardLeft) != null) {
                    if (board.getPiece(forwardLeft).getTeamColor() != color) {
                        moves.add(forwardLeftMove);
                    }
                } else {
                    moves.add(forwardLeftMove);
                }
            }
            if (position.getColumn() <= 7) {
                ChessPosition forwardRight = new ChessPosition(position.getRow() + 2, position.getColumn() + 1);
                ChessMove forwardRightMove = new ChessMove(position, forwardRight, null);
                if (board.getPiece(position) != null) {
                    if (board.getPiece(position).getTeamColor() != color) {
                        moves.add(forwardRightMove);
                    }
                } else {
                    moves.add(forwardRightMove);
                }
            }
        }
        if (position.getRow() >= 3) {
            if (position.getColumn() >= 2) {
                ChessPosition backwardLeft = new ChessPosition(position.getRow() - 2, position.getColumn() - 1);
                ChessMove backwardLeftMove = new ChessMove(position, backwardLeft, null);
                if (board.getPiece(backwardLeft) != null) {
                    if (board.getPiece(backwardLeft).getTeamColor() != color) {
                        moves.add(backwardLeftMove);
                    }
                } else {
                    moves.add(backwardLeftMove);
                }
            }
            if (position.getColumn() <= 7) {
                ChessPosition backwardRight = new ChessPosition(position.getRow() - 2, position.getColumn() + 1);
                ChessMove backwardRightMove = new ChessMove(position, backwardRight, null);
                if (board.getPiece(position) != null) {
                    if (board.getPiece(position).getTeamColor() != color) {
                        moves.add(backwardRightMove);
                    }
                } else {
                    moves.add(backwardRightMove);
                }
            }
        }
        if (position.getColumn() <= 6) {
            if (position.getRow() >= 2) {
                ChessPosition rightDown = new ChessPosition(position.getRow() - 1, position.getColumn() + 2);
                ChessMove rightDownMove = new ChessMove(position, rightDown, null);
                if (board.getPiece(rightDown) != null) {
                    if (board.getPiece(rightDown).getTeamColor() != color) {
                        moves.add(rightDownMove);
                    }
                } else {
                    moves.add(rightDownMove);
                }
            }
            if (position.getRow() <= 7) {
                ChessPosition rightUp = new ChessPosition(position.getRow() + 1, position.getColumn() + 2);
                ChessMove rightUpMove = new ChessMove(position, rightUp, null);
                if (board.getPiece(rightUp) != null) {
                    if (board.getPiece(rightUp).getTeamColor() != color) {
                        moves.add(rightUpMove);
                    }
                } else {
                    moves.add(rightUpMove);
                }
            }
        }
        if (position.getColumn() >= 3) {
            if (position.getRow() >= 2) {
                ChessPosition leftDown = new ChessPosition(position.getRow() - 1, position.getColumn() - 2);
                ChessMove leftDownMove = new ChessMove(position, leftDown, null);
                if (board.getPiece(leftDown) != null) {
                    if (board.getPiece(leftDown).getTeamColor() != color) {
                        moves.add(leftDownMove);
                    }
                } else {
                    moves.add(leftDownMove);
                }
            }
            if (position.getRow() <= 7) {
                ChessPosition leftUp = new ChessPosition(position.getRow() + 1, position.getColumn() - 2);
                ChessMove leftUpMove = new ChessMove(position, leftUp, null);
                if (board.getPiece(leftUp) != null) {
                    if (board.getPiece(leftUp).getTeamColor() != color) {
                        moves.add(leftUpMove);
                    }
                } else {
                    moves.add(leftUpMove);
                }
            }
        }
        return moves;
    }
//    public Collection<ChessMove> calculateMoves() {
//        return;
//    }

}
