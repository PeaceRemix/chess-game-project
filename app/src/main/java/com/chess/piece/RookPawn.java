package com.chess.piece;

import com.chess.main.Type;
import java.util.ArrayList;
import com.chess.main.GamePanel;

public class RookPawn extends Piece {

    public boolean AEnpassantmove = false;

    public RookPawn(String color, int column, int row) {
        super(color, column, row);
        type = Type.RookPawn;
        getImage("/PieceImage/"+((color=="white")?"w":"b")+"-rookpawn");
    }

    public boolean canMove(int targetColumn, int targetRow) {
        if (isIntheBoard(targetColumn, targetRow) && !isASameSquare(targetColumn, targetRow)) {
            if ((targetColumn - this.previous_Column)*(targetRow - this.previous_Row) == 0 && !isAnAllySquare(targetColumn, targetRow))
                return !there_Is_An_obstacle_On_The_Path(targetColumn, targetRow);
        }
        else if (isASameSquare(targetColumn, targetRow) == true && GamePanel.rightClick == true) {
            boolean WorB = this.color.equals("white");
            if (targetRow == this.previous_Row && targetColumn == this.previous_Column)
                if (isIntheBoard(targetColumn, targetRow + (WorB?1:-1)) && isAnEmptySquare(this.previous_Column, this.previous_Row + (WorB?1:-1)))
                        return true;
        }
        return false;
    }

    public static ArrayList<Piece> PawnFusionWithRook(PieceImfo pi) {
        int FusionColumn = pi.actPiece.column;
        int FusionRow = pi.actPiece.row;
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
            if ((pi.color == "white" && (pi.actPiece.row > pi.actPiece.previous_Row)) || ((pi.color == "black") && (pi.actPiece.row < pi.actPiece.previous_Row)))
                return true;
        return false;
    }

    public static boolean RookPawnCanDifuse(PieceImfo pi) {
        System.out.println(pi.actPiece.row + "," + pi.actPiece.previous_Row);
        if (pi.actPiece.row == pi.actPiece.previous_Row && pi.actPiece.column == pi.actPiece.previous_Column) {
            if (pi.color.equals("white"))
                if (pi.actPiece.isAnEmptySquare(pi.actPiece.column, pi.actPiece.row + 1))
                    return true;
            if (pi.color.equals("black"))
                if (pi.actPiece.isAnEmptySquare(pi.actPiece.column, pi.actPiece.row - 1))
                    return true;
        }
        return false;
    }

    public static ArrayList<Piece> RookPawnDifuse(PieceImfo pi){
        boolean WorB = pi.actPiece.color.equals("white");
        pi.simPiece.remove(pi.actPiece);
        pi.simPiece.add(new Rook(pi.actPiece.color, pi.actPiece.column,  pi.actPiece.row));
        Piece pawn = new Pawn(pi.actPiece.color, pi.actPiece.column,  pi.actPiece.row + (WorB?1:-1));
        pawn.moved = true;
        pi.simPiece.add(pawn);
        return pi.simPiece;
    }

}
