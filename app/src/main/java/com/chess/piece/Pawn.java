package com.chess.piece;
import com.chess.main.Type;
import com.chess.main.GamePanel;

public class Pawn extends Piece{
    
    public boolean AEnpassantmove = false;
    public Pawn(String color, int column ,int row){
        super(color,  column ,row);
        type = Type.Pawn;
        
        if(color == "white"){
            image = getImage("/PieceImage/w-pawn");//怎么读取啊 路径是不是怪怪的
        }
        else{
            image = getImage("/PieceImage/b-pawn");//怎么读取啊 路径是不是怪怪的
        }
    }

    public boolean canMove(int targetColumn, int targetRow){
        AEnpassantmove = false;

        int moveValueBlackOrWhite;
        if(color == "white"){
            moveValueBlackOrWhite = -1 ;
        }
        else{
            moveValueBlackOrWhite = 1;
        }

        
        hittingP = this.getHittingPiece(targetColumn, targetRow);
        if((targetColumn == previous_Column) && (targetRow == previous_Row + moveValueBlackOrWhite) && hittingP ==null){// 
            return true;
        }

        if((targetColumn == previous_Column) && (targetRow == previous_Row + 2*moveValueBlackOrWhite) && hittingP ==null && moved == false ){
            if(there_Is_An_obstacle_On_The_Path(targetColumn, targetRow)== false){
                return true;
            }
        }

        if(Math.abs(targetColumn -previous_Column) == 1 && targetRow == previous_Row + moveValueBlackOrWhite ){
            if(isAnEnemySquare(targetColumn, targetRow)){
                return true;
            }
        }

        //En passant
        if(Math.abs(targetColumn -previous_Column) == 1 && targetRow == previous_Row + moveValueBlackOrWhite ){
            for(Piece piece : GamePanel.simPieces){
                if(piece.column == targetColumn && piece.row == this.previous_Row && piece.color.equals(this.color)==false && piece.twoStep == true){
                    this.hittingP = piece;
                    AEnpassantmove = true;
                    return true;
                }
            }
        }

        if((targetColumn == previous_Column) && (targetRow == previous_Row - moveValueBlackOrWhite)){
            for(Piece piece : GamePanel.simPieces){
                if(piece.column == targetColumn && piece.row == targetRow && piece.color.equals(this.color)==true && piece.type == Type.Rook){
                    return true;
                }
            }

        }



        

        return false;
    
    }
    
}