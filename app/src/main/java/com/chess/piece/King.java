package com.chess.piece;

import com.chess.main.Type;

public class King extends Piece {

    public King(String color, int column, int row) {
        super(color, column, row);
        type = Type.King;
        getImage("/PieceImage/"+((color=="white")?"w":"b")+"-king");
    }

    public boolean canMove(int targetColumn, int targetRow){
        int colChange = targetColumn - previous_Column;
        int rowChange = targetRow - previous_Row;
        if (isIntheBoard(targetColumn, targetRow) && !isAnAllySquare(targetColumn, targetRow) )
            if ((Math.abs(colChange)<=1 && Math.abs(rowChange)<= 1) && ((~colChange &~rowChange) !=-1))
                return true;
        return false;
    }
}

