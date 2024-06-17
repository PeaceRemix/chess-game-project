package com.chess.piece;

import com.chess.main.Type;
import com.chess.main.GamePanel;

public class Pawn extends Piece {

    public boolean AEnpassantmove = false;

    public Pawn(String color, int column, int row) {
        super(color, column, row);
        type = Type.Pawn;

        if (color == "white") {
            image = getImage("/PieceImage/w-pawn");
        } else {
            image = getImage("/PieceImage/b-pawn");
        }
    }

    public boolean canMove(int targetColumn, int targetRow) {
        AEnpassantmove = false;

        int moveValueBlackOrWhite;
        if (color == "white") {
            moveValueBlackOrWhite = -1;
        } else {
            moveValueBlackOrWhite = 1;
        }

        hittingP = this.getHittingPiece(targetColumn, targetRow);
        if ((targetColumn == previous_Column) && (targetRow == previous_Row + moveValueBlackOrWhite)
                && hittingP == null) {//
            return true;
        }

        if ((targetColumn == previous_Column) && (targetRow == previous_Row + 2 * moveValueBlackOrWhite)
                && hittingP == null && moved == false) {
            if (there_Is_An_obstacle_On_The_Path(targetColumn, targetRow) == false) {
                return true;
            }
        }

        if (Math.abs(targetColumn - previous_Column) == 1 && targetRow == previous_Row + moveValueBlackOrWhite) {
            if (isAnEnemySquare(targetColumn, targetRow)) {
                return true;
            }
        }

        // En passant
        if (Math.abs(targetColumn - previous_Column) == 1 && targetRow == previous_Row + moveValueBlackOrWhite) {
            for (Piece piece : GamePanel.pieceInfo.simPiece) {
                if (piece.column == targetColumn && piece.row == this.previous_Row
                        && piece.color.equals(this.color) == false && piece.twoStep == true) {
                    this.hittingP = piece;
                    AEnpassantmove = true;
                    return true;
                }
            }
        }

        if ((targetColumn == previous_Column) && (targetRow == previous_Row - moveValueBlackOrWhite)) {
            for (Piece piece : GamePanel.pieceInfo.simPiece) {
                if (piece.column == targetColumn && piece.row == targetRow && piece.color.equals(this.color) == true
                        && piece.type == Type.Rook) {
                    return true;
                }
            }

        }

        return false;

    }

    public static boolean canPromote(PieceInfo pi) {
        for (Piece piece : pi.Pieces)
            if (piece.type == Type.Pawn && piece.row == ((piece.color=="white")?0:7))
                return true;
        return false;
    }
}