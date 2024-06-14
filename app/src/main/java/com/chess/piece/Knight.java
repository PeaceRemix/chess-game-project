package com.chess.piece;

import com.chess.main.Type;

public class Knight extends Piece {

    public Knight(String color, int column, int row) {
        super(color, column, row);
        type = Type.Knight;
        if (color == "white") {
            image = getImage("/PieceImage/w-knight");// 怎么读取啊 路径是不是怪怪的
        } else {
            image = getImage("/PieceImage/b-knight");// 怎么读取啊 路径是不是怪怪的
        }
    }

    public boolean canMove(int targetColumn, int targetRow) {

        if (isIntheBoard(targetColumn, targetRow)) {
            if (Math.abs(targetColumn - previous_Column) * Math.abs(targetRow - previous_Row) == 2) {

                if (Math.abs(targetColumn - previous_Column) == 1 && (targetRow - previous_Row) == 2) { // 往上左右移動
                    if (isAnEmptySquare(this.previous_Column, this.previous_Row + 1)) {
                        if (isAnAllySquare(targetColumn, targetRow) == false) {
                            return true;
                        }
                    }
                }

                if (Math.abs(targetColumn - previous_Column) == 1 && (targetRow - previous_Row) == -2) { // 往下左右移動
                    if (isAnEmptySquare(this.previous_Column, this.previous_Row - 1)) {
                        if (isAnAllySquare(targetColumn, targetRow) == false) {
                            return true;
                        }
                    }
                }

                if ((targetColumn - previous_Column) == 2 && Math.abs(targetRow - previous_Row) == 1) { // 往右上下移動
                    if (isAnEmptySquare(this.previous_Column + 1, this.previous_Row)) {
                        if (isAnAllySquare(targetColumn, targetRow) == false) {
                            return true;
                        }
                    }
                }

                if ((targetColumn - previous_Column) == -2 && Math.abs(targetRow - previous_Row) == 1) { // 往左上下移動
                    if (isAnEmptySquare(this.previous_Column - 1, this.previous_Row)) {
                        if (isAnAllySquare(targetColumn, targetRow) == false) {
                            return true;
                        }
                    }
                }
            }

            if (Math.abs(targetColumn - previous_Column) == 1 && Math.abs(targetRow - previous_Row) == 1
                    && isAnEmptySquare(targetColumn, targetRow)) {
                return true;
            }
        }

        return false;
    }

}