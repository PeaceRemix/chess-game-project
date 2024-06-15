package com.chess.piece;

import com.chess.main.Type;

public class King extends Piece {

    public King(String color, int column, int row) {
        super(color, column, row);
        type = Type.King;
        if (color == "white") {
            image = getImage("/PieceImage/w-king");// 怎么读取啊 路径是不是怪怪的
        } else {
            image = getImage("/PieceImage/b-king");// 怎么读取啊 路径是不是怪怪的
        }
    }

    public boolean canMove(int targetColumn, int targetRow) {// 先判斷能不能移動
        if (isIntheBoard(targetColumn, targetRow)) {// 先判斷移動在不在棋盤内
            if ((Math.abs(targetColumn - previous_Column) + Math.abs(targetRow - previous_Row) == 1) ||
                    ((Math.abs(targetColumn - previous_Column) == 1) && (Math.abs(targetRow - previous_Row) == 1))) {// 判斷走的是不是九宮格的距離
                if (isAnAllySquare(targetColumn, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}