package com.chess.piece;
import com.chess.main.GamePanel;
import com.chess.main.Type;

public class Bishop extends Piece{

    public Bishop(String color, int column ,int row){
        super(color, column ,row);
        type = Type.Bishop;
        if(color == "white"){
            image = getImage("/PieceImage/w-bishop");//怎么读取啊 路径是不是怪怪的
        }
        else{
            image = getImage("/PieceImage/b-bishop");//怎么读取啊 路径是不是怪怪的
        }
    
    }

    public boolean canMove(int targetColumn, int targetRow){
        
        if(isIntheBoard(targetColumn,targetRow)){

            if(isAnAllySquare(targetColumn,targetRow)== false){

                if((Math.abs(targetColumn - this.previous_Column) == Math.abs(targetRow - previous_Row)) && this.isASameSquare(targetColumn,targetRow)== false){

                    //普通斜著走
                    if(there_Is_A_Obstacle_On_The_Diagonal_Grid_Path(targetColumn,targetRow) == false){//todo
                        return true;
                    }

                   //炮規則移動
                    if(CanonRule_is_Valid(targetColumn,targetRow)== true){//todo
                        return true;
                    }
                }


            }
        
        
        }

        return false;
    }

    public boolean CanonRule_is_Valid(int targetColumn,int targetRow){
        //狀況一 不吃兵
        int Nearest_Ally_Obstacle_Position_Column =  get_Nearest_Ally_Obstacle_Position_On_The_Diagonal_Grid_Path(targetColumn,targetRow,"Column");
        int Nearest_Ally_Obstacle_Position_Row = get_Nearest_Ally_Obstacle_Position_On_The_Diagonal_Grid_Path(targetColumn,targetRow,"Row");
        Piece piece=null;
        for(Piece TransitPiece : GamePanel.simPieces){
            if(Nearest_Ally_Obstacle_Position_Column  == TransitPiece.previous_Column && Nearest_Ally_Obstacle_Position_Row == TransitPiece.previous_Row &&  TransitPiece != this){
                piece  = TransitPiece ;
                break;
            }
        }
        if(piece.there_Is_A_Obstacle_On_The_Diagonal_Grid_Path(targetColumn,targetRow) == false && piece.color.equals(this.color)){
            if(isAnEmptySquare(targetColumn,targetRow) ){
                if((targetRow -piece.previous_Row)/Math.abs(targetRow -piece.previous_Row) ==  (targetRow - this.previous_Row)/Math.abs(targetRow -this.previous_Row)){
                    if((targetColumn -piece.previous_Column)/Math.abs(targetColumn -piece.previous_Column) ==  (targetColumn - this.previous_Column)/Math.abs(targetColumn - this.previous_Column)){
                        if(Math.abs(targetColumn -piece.previous_Column) == 1 && Math.abs(targetRow -piece.previous_Row) == 1){
                            return true;
                        }

                        else{
                            return false;
                        }
                    }

                }

            }
            if(isAnEnemySquare(targetColumn,targetRow)){
                return true;
            }
        }

        return false;
    }

  
}