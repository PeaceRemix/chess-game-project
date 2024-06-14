package com.chess.piece;
import com.chess.main.Type;

public class Queen extends Piece{

    public Queen(String color, int column ,int row){
        super(color,  column ,row);
        type = Type.Queen;
        if(color == "white"){
            image = getImage("/PieceImage/w-queen");//怎么读取啊 路径是不是怪怪的
        }
        else{
            image = getImage("/PieceImage/b-queen");//怎么读取啊 路径是不是怪怪的
        }
    }

    public boolean canMove(int targetColumn, int targetRow){
        if(isIntheBoard(targetColumn,targetRow) && !isASameSquare(targetColumn,targetRow)){
            if(isAnAllySquare(targetColumn, targetRow))
                return true;
            if(((targetColumn-this.previous_Column)*(targetRow - this.previous_Row) == 0 || (Math.abs(targetColumn-this.previous_Column) == Math.abs(targetRow - previous_Row))))
                return !there_Is_An_obstacle_On_The_Path(targetColumn,targetRow)&&!there_Is_A_Obstacle_On_The_Diagonal_Grid_Path(targetColumn,targetRow); 
        }
        return false;
    }
    
}