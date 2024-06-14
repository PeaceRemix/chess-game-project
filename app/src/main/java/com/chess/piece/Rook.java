package com.chess.piece;
import com.chess.main.Type;
public class Rook extends Piece{

    public Rook(String color, int column ,int row){
         super(color,  column ,row);
        type = Type.Rook;
        if(color.equals("white")){
            image = getImage("/PieceImage/w-rook");//怎么读取啊 路径是不是怪怪的
        }
        else{
            image = getImage("/PieceImage/b-rook");//怎么读取啊 路径是不是怪怪的
        }

    }

    public boolean canMove(int targetColumn, int targetRow){
        if(isIntheBoard(targetColumn,targetRow) && isASameSquare(targetColumn,targetRow)==false){
            if(Math.abs(targetColumn - this.previous_Column) * Math.abs(targetRow - this.previous_Row) == 0 && isAnAllySquare(targetColumn,targetRow) == false){
                return !there_Is_An_obstacle_On_The_Path(targetColumn,targetRow);
            }
        } 

        return false;
    }
}