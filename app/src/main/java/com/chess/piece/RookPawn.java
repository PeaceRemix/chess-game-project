package com.chess.piece;

import com.chess.main.Type;

import java.util.ArrayList;

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

        else if (isASameSquare(targetColumn, targetRow) == true && GamePanel.rightClick == true) {
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

    public static ArrayList<Piece> PawnFusionWithRook(PieceImfo pi) {
        int FusionColumn = pi.actCol;
        int FusionRow = pi.actRow;
        ArrayList<Piece> removePiece = new ArrayList<>();
        for (Piece piece : pi.simPiece)
            if (FusionColumn == piece.column && FusionRow == piece.row)
                removePiece.add(piece);
                pi.simPiece.removeAll(removePiece);
                pi.simPiece.add(new RookPawn(pi.color, FusionColumn, FusionRow));
        return pi.simPiece;
    }

    public static boolean PawnCanFusionWithRook(PieceImfo pi) {// 可以往後走 所以 可以融合 條件限制寫在了pawn
        if (pi.actPiece.type == Type.Pawn)
            if ((pi.color == "white" && (pi.actRow > pi.actPreRow)) || ((pi.color == "black") && (pi.actRow < pi.actPreRow)))
                return true;
        return false;
    }

    public static boolean RookPawnCanDifuse(PieceImfo pi) {
        System.out.println(pi.actRow + "," + pi.actPreRow);
        if (pi.actRow == pi.actPreRow && pi.actCol == pi.actPreCol) {
            if (pi.color.equals("white"))
                if (pi.actPiece.isAnEmptySquare(pi.actCol, pi.actRow + 1))
                    return true;
            if (pi.color.equals("black"))
                if (pi.actPiece.isAnEmptySquare(pi.actCol, pi.actRow - 1))
                    return true;
        }
        return false;
    }

}
