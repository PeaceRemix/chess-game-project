package com.chess.piece;
import java.util.ArrayList;
import com.chess.main.Type;


public class Queen extends Piece{


    public Queen(String color, int col ,int row){
        super(color,  col ,row);
        type = Type.Queen;
            image = getImage("/PieceImage/"+((color=="white")?"w":"b")+"-queen");
    }


    public boolean canMove(int targetCol, int targetRow){
        if(isIntheBoard(targetCol,targetRow) && !isASameSquare(targetCol,targetRow)){
            if(getHittingPiece(targetCol, targetRow)!=null)
                if(getHittingPiece(targetCol, targetRow).type == Type.Queen && isAnAllySquare(targetCol, targetRow))
                    return false;
            if(isAnAllySquare(targetCol, targetRow))
                return true;
            if(((targetCol-this.previous_Column)*(targetRow - this.previous_Row) == 0 || (Math.abs(targetCol-this.previous_Column) == Math.abs(targetRow - previous_Row))))
                return !there_Is_An_obstacle_On_The_Path(targetCol,targetRow)&&!there_Is_A_Obstacle_On_The_Diagonal_Grid_Path(targetCol,targetRow);
        }
        return false;
    }
   
    public static ArrayList<Piece> ChangeWithQueen(PieceInfo pi){
        ArrayList<Piece> ChangePiece= new ArrayList<>();
        for(Piece piece : pi.simPiece)
            if(pi.actPiece.column == piece.column && pi.actPiece.row == piece.row && (piece.type != Type.Queen))
                ChangePiece.add(piece);
        if(ChangePiece.size()<=0)
            return pi.simPiece;              
        pi.simPiece.remove(ChangePiece.get(0));
        switch(ChangePiece.get(0).type){
                case Rook: pi.simPiece.add(new Rook(pi.color,pi.oriCol,pi.oriRow));break;
                case Knight: pi.simPiece.add(new Knight(pi.color,pi.oriCol,pi.oriRow));break;
                case King : pi.simPiece.add(new King(pi.color,pi.oriCol,pi.oriRow));break;
                case Bishop: pi.simPiece.add(new Bishop(pi.color,pi.oriCol,pi.oriRow));break;
                case RookPawn: pi.simPiece.add(new RookPawn(pi.color,pi.oriCol,pi.oriRow));break;
                case Pawn: pi.simPiece.add(new Pawn(pi.color,pi.oriCol,pi.oriRow));break;
                case Queen: pi.simPiece.add(new Queen(pi.color,pi.oriCol,pi.oriRow));break;
                default : pi.simPiece.add(new Queen(pi.color,pi.oriCol,pi.oriRow));break;
        }
        return pi.simPiece;
    }
}

