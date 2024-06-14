package com.chess.piece;

import com.chess.main.Type;
import com.chess.main.GamePanel;

public class RookPawn extends Piece {

    public boolean AEnpassantmove = false;

    public RookPawn(String color, int column, int row) {
        super(color, column, row);
        type = Type.RookPawn;

        if (color == "white") {
            image = getImage("/PieceImage/w-rookpawn");// 怎么读取啊 路径是不是怪怪的
        } else {
            image = getImage("/PieceImage/b-rookpawn");// 怎么读取啊 路径是不是怪怪的
        }
    }

    public boolean canMove(int targetColumn, int targetRow) {
        if (isIntheBoard(targetColumn, targetRow) && isASameSquare(targetColumn, targetRow) == false) {
            if (Math.abs(targetColumn - this.previous_Column) * Math.abs(targetRow - this.previous_Row) == 0
                    && isAnAllySquare(targetColumn, targetRow) == false) {
                return !there_Is_An_obstacle_On_The_Path(targetColumn, targetRow);
            }
        }

        else if (isASameSquare(targetColumn, targetRow) == true) {
            if (targetRow == this.previous_Row && targetColumn == this.previous_Column) {
                if (this.color.equals("white") && isIntheBoard(targetColumn, targetRow + 1)) {
                    if (isAnEmptySquare(this.previous_Column, this.previous_Row + 1)) {
                        return true;
                    }
                }
                if (this.color.equals("black") && isIntheBoard(targetColumn, targetRow - 1)) {
                    if (this.isAnEmptySquare(this.previous_Column, this.previous_Row - 1)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

}
