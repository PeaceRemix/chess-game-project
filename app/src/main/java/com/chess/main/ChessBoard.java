package com.chess.main;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;//啥区别

public class ChessBoard{
    
    public static final int Max_Column = 5;
    public static final int Max_Row = 8;
    public static final int SQUARE_SIZE = 95;
    public static final int HALF_SQUARE_SIZE = 100 / 2;

    public void drawTheBoard(Graphics2D g2){

        boolean isBrightGrid = true;
        for(int row = 0 ; row < Max_Row ; row++){

            for(int column = 0 ; column < Max_Column ; column++){

                if(isBrightGrid){
                    g2.setColor(new Color(210,165,125));
                    isBrightGrid = false;
                }
                else{
                    g2.setColor(new Color(175,115,70));
                    isBrightGrid = true;
                }

                g2.fillRect(column*SQUARE_SIZE,row*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);// 绘制一个填充的矩形，其中 (x, y) 是矩形的左上角坐标，width 和 height 分别是矩形的宽度和高度。
            }
        }

    } 

}