package com.chess.piece;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.Graphics2D;
import com.chess.main.ChessBoard;
import com.chess.main.GamePanel;
import com.chess.main.Type;

public class Piece {// super class for all pieces
    public Type type;
    public BufferedImage image;
    public int coordinate_x, coordinate_y;
    public int column, row, previous_Column, previous_Row;
    public String color;
    public Piece hittingP;
    public boolean moved = false;
    public boolean twoStep;// for pawn

    public Piece(String color, int column, int row) {// constructor
        this.color = color;
        this.column = column;
        this.row = row;
        previous_Column = column;
        previous_Row = row;
        coordinate_x = getCoordinate_x(column);
        coordinate_y = getCoordinate_y(row);
    }

    public Piece(Piece other) {
        this.color = other.color;
        this.column = other.column;
        this.row = other.row;
        this.previous_Column = other.previous_Column;
        this.previous_Row = other.previous_Row;
        this.coordinate_x = other.coordinate_x;
        this.coordinate_y = other.coordinate_y;
        this.image = other.image;

    }

    public BufferedImage getImage(String imagePath) {

        BufferedImage PiecesImage = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));// getclass why 需要
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getCoordinate_x(int column) {// 坐标不居中
        return column * ChessBoard.SQUARE_SIZE;
    }

    public int getCoordinate_y(int row) {// 坐标不居中
        return row * ChessBoard.SQUARE_SIZE;
    }

    public int getColumn(int coordinate_x) {// 居中处理 待处理逻辑
        return (coordinate_x + ChessBoard.HALF_SQUARE_SIZE) / ChessBoard.SQUARE_SIZE;
    }

    public int getRow(int coordinate_y) {// 居中处理 待处理逻辑
        return (coordinate_y + ChessBoard.HALF_SQUARE_SIZE) / ChessBoard.SQUARE_SIZE;
    }

    /*
     * public void draw(Graphics2D g2){ //合作展现
     * g2.drawImage(image,coordinate_x,
     * coordinate_y,ChessBoard.SQUARE_SIZE,ChessBoard.SQUARE_SIZE,null);//图片来源 坐标
     * 大小缩放 观察者 通常是null；
     * }
     */

    public boolean canMove(int targetColumn, int targetRow) {// need to overide
        return true;
    }

    public boolean isIntheBoard(int targetColumn, int targetRow) {// 理論上 這個判斷有可能要修改 地點。
        if (targetColumn >= 0 && targetColumn < ChessBoard.Max_Column && targetRow >= 0
                && targetRow < ChessBoard.Max_Row) {
            return true;
        }
        return false;
    }

    public boolean isAnEnemySquare(int targetColumn, int targetRow) {

        hittingP = getHittingPiece(targetColumn, targetRow);
        if (hittingP != null && hittingP.color.equals(this.color) == false) {// logic first and after
            return true;
        }
        return false;
    }

    public boolean isAnEmptySquare(int targetColumn, int targetRow) {
        hittingP = getHittingPiece(targetColumn, targetRow);
        if (hittingP == null) {
            return true;
        } else
            return false;
    }

    public boolean isAnAllySquare(int targetColumn, int targetRow) {
        hittingP = getHittingPiece(targetColumn, targetRow);
        if (hittingP == null || hittingP.color.equals(this.color) == false) {
            return false;
        } else {
            return true;
        }
    }

    public Piece getHittingPiece(int targetColumn, int targetRow) {

        for (Piece piece : GamePanel.pieceImfo.simPiece) {
            if (targetColumn == piece.previous_Column && targetRow == piece.previous_Row && piece != this) {// logic 研究
                return piece;
            }
        }
        return null;
    }

    public boolean isASameSquare(int targetColumn, int targetRow) {

        if ((targetColumn == this.previous_Column) && (targetRow == this.previous_Row)) {
            return true;
        }

        else
            return false;
    }

    public boolean there_Is_An_obstacle_On_The_Path(int targetColumn, int targetRow) {
        boolean An_obstacle_On_The_Path = false;
        if ((targetColumn - this.previous_Column) == 0 && (targetRow - this.previous_Row) < 0) {// to up
            for (int i = targetRow + 1; i < this.previous_Row; i++) {
                if (isAnEmptySquare(targetColumn, i) == false) {
                    An_obstacle_On_The_Path = true;
                    break;
                }
            }
        }

        if ((targetColumn - this.previous_Column) == 0 && (targetRow - this.previous_Row) > 0) {// to down
            for (int i = targetRow - 1; i > this.previous_Row; i--) {
                if (isAnEmptySquare(targetColumn, i) == false) {
                    An_obstacle_On_The_Path = true;
                    break;
                }
            }
        }

        if ((targetColumn - this.previous_Column) > 0 && (targetRow - this.previous_Row) == 0) {// to right
            for (int i = targetColumn - 1; i > this.previous_Column; i--) {
                if (isAnEmptySquare(i, targetRow) == false) {
                    An_obstacle_On_The_Path = true;
                    break;
                }
            }
        }

        if ((targetColumn - this.previous_Column) < 0 && (targetRow - this.previous_Row) == 0) {// to left
            for (int i = targetColumn + 1; i < this.previous_Column; i++) {
                if (isAnEmptySquare(i, targetRow) == false) {
                    An_obstacle_On_The_Path = true;
                    break;
                }
            }
        }

        return An_obstacle_On_The_Path;
    }

    public boolean there_Is_A_Obstacle_On_The_Diagonal_Grid_Path(int targetColumn, int targetRow) {
        if ((targetColumn - this.previous_Column) > 0 && (targetRow - this.previous_Row > 0)) { // 右下角
            for (int i = targetColumn - 1; i > previous_Column; i--) {
                for (int j = targetRow - 1; j > previous_Row; j--) {
                    if (isAnEmptySquare(i, j) == false) {
                        return true;
                    }
                }
            }
        }

        if ((targetColumn - this.previous_Column) < 0 && (targetRow - this.previous_Row > 0)) { // 左下角
            for (int i = targetColumn + 1; i < previous_Column; i++) {
                for (int j = targetRow - 1; j > previous_Row; j--) {
                    if (isAnEmptySquare(i, j) == false) {
                        return true;
                    }
                }
            }
        }

        if ((targetColumn - this.previous_Column) > 0 && (targetRow - this.previous_Row < 0)) { // 左上角
            for (int i = targetColumn - 1; i > previous_Column; i--) {
                for (int j = targetRow + 1; j < previous_Row; j++) {
                    if (isAnEmptySquare(i, j) == false) {
                        return true;
                    }
                }
            }
        }

        if ((targetColumn - this.previous_Column) < 0 && (targetRow - this.previous_Row < 0)) { // 右上角
            for (int i = targetColumn + 1; i < previous_Column; i++) {
                for (int j = targetRow + 1; j < previous_Row; j++) {
                    if (isAnEmptySquare(i, j) == false) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int get_Nearest_Ally_Obstacle_Position_On_The_Diagonal_Grid_Path(int targetColumn, int targetRow,
            String ColumnOrRow) {

        if ((targetColumn - this.previous_Column) > 0 && (targetRow - this.previous_Row > 0)) { // 右下角
            for (int i = targetColumn - 1; i > previous_Column; i--) {
                for (int j = targetRow - 1; j > previous_Row; j--) {
                    if (isAnAllySquare(i, j)) {
                        if (ColumnOrRow.equals("Column")) {
                            return i;
                        }

                        if (ColumnOrRow.equals("Row")) {
                            return j;
                        }
                    }
                }
            }
        }

        if ((targetColumn - this.previous_Column) < 0 && (targetRow - this.previous_Row > 0)) { // 左下角
            for (int i = targetColumn + 1; i < previous_Column; i++) {
                for (int j = targetRow - 1; j > previous_Row; j--) {
                    if (isAnAllySquare(i, j)) {
                        if (ColumnOrRow.equals("Column")) {
                            return i;
                        }

                        if (ColumnOrRow.equals("Row")) {
                            return j;
                        }
                    }
                }
            }
        }

        if ((targetColumn - this.previous_Column) > 0 && (targetRow - this.previous_Row < 0)) { // 左上角
            for (int i = targetColumn - 1; i > previous_Column; i--) {
                for (int j = targetRow + 1; j < previous_Row; j++) {
                    if (isAnAllySquare(i, j)) {
                        if (ColumnOrRow.equals("Column")) {
                            return i;
                        }

                        if (ColumnOrRow.equals("Row")) {
                            return j;
                        }
                    }
                }
            }
        }

        if ((targetColumn - this.previous_Column) < 0 && (targetRow - this.previous_Row < 0)) { // 右上角
            for (int i = targetColumn + 1; i < previous_Column; i++) {
                for (int j = targetRow + 1; j < previous_Row; j++) {
                    if (isAnAllySquare(i, j)) {
                        if (ColumnOrRow.equals("Column")) {
                            return i;
                        }

                        if (ColumnOrRow.equals("Row")) {
                            return j;
                        }
                    }
                }
            }
        }
        return 0;
    }

}