package com.chess.piece;

import com.chess.main.Type;

public class Rook extends Piece {

    public Rook(String color, int column, int row) {
        super(color, column, row);
        type = Type.Rook;
        getImage("/PieceImage/"+((color=="white")?"w":"b")+"-rook");
    }

    public boolean canMove(int targetColumn, int targetRow) {
        if (isIntheBoard(targetColumn, targetRow) && !isASameSquare(targetColumn, targetRow))
            if ((targetColumn - this.previous_Column) * (targetRow - this.previous_Row) == 0 && !isAnAllySquare(targetColumn, targetRow))
                return !there_Is_An_obstacle_On_The_Path(targetColumn, targetRow);
        return false;
    }
}