package com.chess.main;

import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import com.chess.piece.*;

public class GamePanel extends JPanel implements Runnable {

    // about show and run
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    public int gameStatic = -1;
    public boolean gameOver = false;
    Thread gameThread;
    ChessBoard chessboard = new ChessBoard();
    Mouse mouse = new Mouse();

    // pieces
    ArrayList<Piece> promoPieces = new ArrayList<>();
    public static PieceInfo pieceInfo = new PieceInfo();

    // color
    public static final String white = "white";
    public static final String black = "black";
    String currentColor = white;

    // about moving
    private boolean canMove = false;
    private boolean validSquare = false;
    private boolean promotion = false;
    private boolean FusionPawnAndCar = false;
    private boolean CanChangeWithQueen = false;
    public static boolean rightClick = false;
    public static boolean leftClick = false;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // set the window size
        setBackground(Color.black); // background color 
        addMouseMotionListener(mouse);
        addMouseListener(mouse);// detect mouse click
        setPieces();
        setPromotePiece();
        copyPieces(pieceInfo.Pieces, pieceInfo.simPiece);
    }

    public void launchGame() {// gameThread 是顯示和運算在不同 Thread 上，像這裡畫面FPS 60，但程式執行不可能只有 60 times PerScond
        gameThread = new Thread(this);
        gameThread.start(); // call run
    }

    public void run() { // override runnan
        double drawInterval = 1000000000 / FPS;// frame
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while (gameThread != null && gameStatic == -1) {
            currentTime = System.nanoTime();
            delta = delta + (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) { // renew
                gameStatic = update();// update window
                repaint();// 、
                delta = 0;
                if (gameStatic > -1)
                    break;
            }
        }
    }

    private int update() { // handle updating stuff piece position number of the piece.

        // check King exist
        pieceInfo.color = currentColor;
        boolean whiteKingE = false;
        boolean blackKingE = false;
        for (Piece piece1 : pieceInfo.Pieces) {
            if (piece1.color == "white" && piece1.type == Type.King)
                whiteKingE = true;
            if (piece1.color == "black" && piece1.type == Type.King)
                blackKingE = true;
        }
        if (!whiteKingE || !blackKingE) {
            gameOver = true;
            return !whiteKingE?0:1;
        }

        if (promotion && mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                    boolean CCQ = CanChangeWithQueen;
                    switch (piece.type) {
                        case Rook:
                            pieceInfo.simPiece.add(new Rook(currentColor,CCQ?pieceInfo.oriCol:pieceInfo.actPiece.column,CCQ?pieceInfo.oriRow:pieceInfo.actPiece.row));break;
                        case Knight:
                            pieceInfo.simPiece.add(new Knight(currentColor,CCQ?pieceInfo.oriCol:pieceInfo.actPiece.column,CCQ?pieceInfo.oriRow:pieceInfo.actPiece.row));break;
                        case Queen:
                            pieceInfo.simPiece.add(new Queen(currentColor,CCQ?pieceInfo.oriCol:pieceInfo.actPiece.column,CCQ?pieceInfo.oriRow:pieceInfo.actPiece.row));break;
                        case Bishop:
                            pieceInfo.simPiece.add(new Bishop(currentColor,CCQ?pieceInfo.oriCol:pieceInfo.actPiece.column,CCQ?pieceInfo.oriRow:pieceInfo.actPiece.row));break;
                        default: break;
                    }
                    if(!CCQ)
                        pieceInfo.simPiece.remove(pieceInfo.actPiece);
                    else
                        for (Piece piece1 : pieceInfo.Pieces)
                            if (piece1.type == Type.Pawn && ((piece1.color == "white" && piece1.row == 0)||(piece1.color == "black" && piece1.row == 7)))
                                pieceInfo.simPiece.remove(piece1);
                    copyPieces(pieceInfo.simPiece, pieceInfo.Pieces);
                    promotion = false;
                    changePlayer();
                }
            }
        }

        else {
            if (mouse.pressed) {// 按下按钮的判断
                rightClick = mouse.rightButtonPressed;
                leftClick = mouse.leftButtonPressed;
                if (pieceInfo.actPiece == null) {
                    for (Piece piece : pieceInfo.simPiece)// 择取行动方的棋子
                        if (piece.color == currentColor && piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                            pieceInfo.actPiece = piece;
                            pieceInfo.oriCol = piece.column;
                            pieceInfo.oriRow = piece.row;
                            pieceInfo.color = pieceInfo.actPiece.color;
                        }
                } else
                    simulate();
            }
            if (!mouse.pressed && pieceInfo.actPiece != null) {
                if (validSquare) { 
                    if (pieceInfo.actPiece.type == Type.Pawn && Math.abs(pieceInfo.actPiece.row - pieceInfo.actPiece.previous_Row) == 2)
                        pieceInfo.actPiece.twoStep = true;
                    if (pieceInfo.actPiece.type == Type.RookPawn && rightClick &&RookPawn.RookPawnCanDifuse(pieceInfo))// click
                        pieceInfo.simPiece = RookPawn.RookPawnDifuse(pieceInfo);
                    if (pieceInfo.actPiece != null) {
                        pieceInfo.actPiece.coordinate_x = pieceInfo.actPiece.getCoordinate_x(pieceInfo.actPiece.column);// update position
                        pieceInfo.actPiece.coordinate_y = pieceInfo.actPiece.getCoordinate_y(pieceInfo.actPiece.row);// update position
                        pieceInfo.actPiece.previous_Column = pieceInfo.actPiece.getColumn(pieceInfo.actPiece.coordinate_x);
                        pieceInfo.actPiece.previous_Row = pieceInfo.actPiece.getRow(pieceInfo.actPiece.coordinate_y);
                        pieceInfo.actPiece.moved = true;
                    }
                    if (FusionPawnAndCar)
                        pieceInfo.simPiece = RookPawn.PawnFusionWithRook(pieceInfo);
                    else if (CanChangeWithQueen)
                        pieceInfo.simPiece = Queen.ChangeWithQueen(pieceInfo);
                    copyPieces(pieceInfo.simPiece,pieceInfo.Pieces);
                    if (Pawn.canPromote(pieceInfo)) {
                        PromotePawnList();
                        promotion = true;
                    } else
                        changePlayer();
                } else {
                    copyPieces(pieceInfo.Pieces, pieceInfo.simPiece);
                    pieceInfo.actPiece.coordinate_x = pieceInfo.actPiece.getCoordinate_x(pieceInfo.actPiece.previous_Column);
                    pieceInfo.actPiece.coordinate_y = pieceInfo.actPiece.getCoordinate_y(pieceInfo.actPiece.previous_Row);
                    pieceInfo.actPiece.column = pieceInfo.actPiece.previous_Column;
                    pieceInfo.actPiece.row = pieceInfo.actPiece.previous_Row;
                    pieceInfo.actPiece = null;
                }
            }
        }
        return -1;
    }

    private void simulate() {// 拖拉程式码 d
        canMove = false;
        validSquare = false;
        copyPieces(pieceInfo.Pieces, pieceInfo.simPiece);// 重新更新
        pieceInfo.actPiece.coordinate_x = mouse.coordinate_x - 45;
        pieceInfo.actPiece.coordinate_y = mouse.coordinate_y - 45;
        pieceInfo.actPiece.column = pieceInfo.actPiece.getColumn(pieceInfo.actPiece.coordinate_x);// check want move column
        pieceInfo.actPiece.row = pieceInfo.actPiece.getRow(pieceInfo.actPiece.coordinate_y);// check want move row
        if (pieceInfo.actPiece.canMove(pieceInfo.actPiece.column, pieceInfo.actPiece.row)) {// check canmove or not
            canMove = true;
            validSquare = true;
            if ((pieceInfo.actPiece.type == Type.Pawn && ((Pawn) pieceInfo.actPiece).AEnpassantmove == false) || pieceInfo.actPiece.type != Type.Pawn)
                pieceInfo.actPiece.hittingP = pieceInfo.actPiece.getHittingPiece(pieceInfo.actPiece.column, pieceInfo.actPiece.row);
            else
                for (Piece piece : pieceInfo.simPiece)
                    if (piece.column == pieceInfo.actPiece.column && piece.row == pieceInfo.actPiece.previous_Row && !piece.color.equals(pieceInfo.actPiece.color) && piece.twoStep)
                        pieceInfo.actPiece.hittingP = piece;

            FusionPawnAndCar = RookPawn.PawnCanFusionWithRook(pieceInfo);
            CanChangeWithQueen = (pieceInfo.actPiece.type == Type.Queen);

            if (pieceInfo.actPiece.hittingP != null && pieceInfo.actPiece.color.equals(pieceInfo.actPiece.hittingP.color) == false)
                for (int i = 0; i < pieceInfo.simPiece.size(); i++)
                    if (pieceInfo.simPiece.get(i).equals(pieceInfo.actPiece.hittingP))
                        pieceInfo.simPiece.remove(i);
        }
    }

    private void changePlayer() {
        currentColor = (currentColor.equals("white"))?"black":"white"; 
        for (Piece piece : pieceInfo.Pieces)
        if (piece.color == currentColor)
            piece.twoStep = false;
        pieceInfo.actPiece = null;
        pieceInfo.color = currentColor;
    }

    private void PromotePawnList() { // show promotion
        if(!promoPieces.get(0).color.equals(pieceInfo.color)){
            promoPieces.clear();
            promoPieces.add(new Rook(pieceInfo.color, 9, 2));
            promoPieces.add(new Queen(pieceInfo.color, 9, 3));
            promoPieces.add(new Bishop(pieceInfo.color, 9, 4));
            promoPieces.add(new Knight(pieceInfo.color, 9, 5));
        }
    }

    private void setPromotePiece(){
        promoPieces.add(new Rook(white, 9, 2));
        promoPieces.add(new Queen(white, 9, 3));
        promoPieces.add(new Bishop(white, 9, 4));
        promoPieces.add(new Knight(white, 9, 5));
    }
    
    public void setPieces() {
        pieceInfo.Pieces.add(new Pawn(white, 0, 6));
        pieceInfo.Pieces.add(new Pawn(white, 1, 6));
        pieceInfo.Pieces.add(new Pawn(white, 2, 6));
        pieceInfo.Pieces.add(new Pawn(white, 3, 6));
        pieceInfo.Pieces.add(new Pawn(white, 4, 6));
        pieceInfo.Pieces.add(new Rook(white, 0, 7));
        pieceInfo.Pieces.add(new Queen(white, 1, 7));
        pieceInfo.Pieces.add(new Bishop(white, 2, 7));
        pieceInfo.Pieces.add(new King(white, 3, 7));
        pieceInfo.Pieces.add(new Knight(white, 4, 7));
        pieceInfo.Pieces.add(new Pawn(black, 0, 1));
        pieceInfo.Pieces.add(new Pawn(black, 1, 1));
        pieceInfo.Pieces.add(new Pawn(black, 2, 1));
        pieceInfo.Pieces.add(new Pawn(black, 3, 1));
        pieceInfo.Pieces.add(new Pawn(black, 4, 1));
        pieceInfo.Pieces.add(new Rook(black, 4, 0));
        pieceInfo.Pieces.add(new Queen(black, 3, 0));
        pieceInfo.Pieces.add(new Bishop(black, 2, 0));
        pieceInfo.Pieces.add(new King(black, 1, 0));
        pieceInfo.Pieces.add(new Knight(black, 0, 0));
    }

    private void copyPieces(ArrayList<Piece> Beginning, ArrayList<Piece> Destination) {
        Destination.clear();
        for (int i = 0; i < Beginning.size(); i++)
            Destination.add(Beginning.get(i));
    }

    public void paintComponent(Graphics g) {// handle drawing component g automatik transfer form swing //
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        chessboard.drawTheBoard(g2);

        // Piece
        for (Piece piece : pieceInfo.simPiece)// initialize
            g2.drawImage(piece.image, piece.coordinate_x, piece.coordinate_y, ChessBoard.SQUARE_SIZE,
                    ChessBoard.SQUARE_SIZE, null);

        if (pieceInfo.actPiece != null) {
            if (canMove) {
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));// 设置绘画工具的透明度
                g2.fillRect(pieceInfo.actPiece.column * ChessBoard.SQUARE_SIZE, pieceInfo.actPiece.row * ChessBoard.SQUARE_SIZE,
                        ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));// 设置绘画工具的透明度
            g2.drawImage(pieceInfo.actPiece.image, pieceInfo.actPiece.coordinate_x, pieceInfo.actPiece.coordinate_y, ChessBoard.SQUARE_SIZE,
                    ChessBoard.SQUARE_SIZE, null);

        }

        // todo renew the huamian

        if (promotion) {
            g2.drawString("Promote to:", 840, 150);
            for (Piece piece : promoPieces)
                g2.drawImage(piece.image, piece.getCoordinate_x(piece.column), piece.getCoordinate_y(piece.row),
                        ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
        }

        // Status Messages
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Comic Sans", Font.PLAIN, 40));

        if (currentColor == white) {
            g2.setColor(Color.PINK);
            g2.drawString("Pink's turn", 680, 550);
        } else {
            g2.setColor(Color.BLUE);
            g2.drawString("Blue's turn", 680, 250);
        }

        if (gameOver) {
            String Winner = "";
            g2.setFont(new Font("Comic Sans", Font.PLAIN, 90));
            if (gameStatic == 0) {
                Winner = "Blue Win!";
                g2.setColor(Color.BLUE);
                g2.drawString(Winner, 600, 400);
            } else {
                Winner = "Pink Win!";
                g2.setColor(Color.PINK);
                g2.drawString(Winner, 600, 500);
            }
        }
    }
}