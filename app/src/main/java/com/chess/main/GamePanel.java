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
    public static PieceImfo pieceImfo = new PieceImfo();

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
        copyPieces(pieceImfo.Pieces, pieceImfo.simPiece);
    }

    public void launchGame() {// gameThread 是顯示和運算在不同 Thread 上，像這裡畫面FPS 60，但程式執行不可能只有 60 times PerScond
        gameThread = new Thread(this);
        gameThread.start(); // call run?
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
                repaint();// update window.  why can called paint component? inheritent???
                delta = 0;
                if (gameStatic > -1)
                    break;
            }
        }
    }

    private int update() { // handle updating stuff piece position number of the piece.

        // check King exist
        pieceImfo.color = currentColor;
        boolean whiteKingE = false;
        boolean blackKingE = false;
        for (Piece piece1 : pieceImfo.Pieces) {
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
                            pieceImfo.simPiece.add(new Rook(currentColor,CCQ?pieceImfo.oriCol:pieceImfo.actPiece.column,CCQ?pieceImfo.oriRow:pieceImfo.actPiece.row));break;
                        case Knight:
                            pieceImfo.simPiece.add(new Knight(currentColor,CCQ?pieceImfo.oriCol:pieceImfo.actPiece.column,CCQ?pieceImfo.oriRow:pieceImfo.actPiece.row));break;
                        case Queen:
                            pieceImfo.simPiece.add(new Queen(currentColor,CCQ?pieceImfo.oriCol:pieceImfo.actPiece.column,CCQ?pieceImfo.oriRow:pieceImfo.actPiece.row));break;
                        case Bishop:
                            pieceImfo.simPiece.add(new Bishop(currentColor,CCQ?pieceImfo.oriCol:pieceImfo.actPiece.column,CCQ?pieceImfo.oriRow:pieceImfo.actPiece.row));break;
                        default: break;
                    }
                    if(!CCQ)
                        pieceImfo.simPiece.remove(pieceImfo.actPiece);
                    else
                        for (Piece piece1 : pieceImfo.Pieces)
                            if (piece1.type == Type.Pawn && ((piece1.color == "white" && piece1.row == 0)||(piece1.color == "black" && piece1.row == 7)))
                                pieceImfo.simPiece.remove(piece1);
                    copyPieces(pieceImfo.simPiece, pieceImfo.Pieces);
                    promotion = false;
                    changePlayer();
                }
            }
        }

        else {
            if (mouse.pressed) {// 按下按钮的判断
                rightClick = mouse.rightButtonPressed;
                leftClick = mouse.leftButtonPressed;
                if (pieceImfo.actPiece == null) {
                    for (Piece piece : pieceImfo.simPiece)// 择取行动方的棋子
                        if (piece.color == currentColor && piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                            pieceImfo.actPiece = piece;
                            pieceImfo.oriCol = piece.column;
                            pieceImfo.oriRow = piece.row;
                            pieceImfo.color = pieceImfo.actPiece.color;
                        }
                } else
                    simulate();// 更改成点亮的模式 todo+
            }
            if (!mouse.pressed && pieceImfo.actPiece != null) {
                if (validSquare) { 
                    if (pieceImfo.actPiece.type == Type.Pawn && Math.abs(pieceImfo.actPiece.row - pieceImfo.actPiece.previous_Row) == 2)
                        pieceImfo.actPiece.twoStep = true;
                    if (pieceImfo.actPiece.type == Type.RookPawn && rightClick &&RookPawn.RookPawnCanDifuse(pieceImfo))// click
                        pieceImfo.simPiece = RookPawn.RookPawnDifuse(pieceImfo);
                    if (pieceImfo.actPiece != null) {
                        pieceImfo.actPiece.coordinate_x = pieceImfo.actPiece.getCoordinate_x(pieceImfo.actPiece.column);// update position
                        pieceImfo.actPiece.coordinate_y = pieceImfo.actPiece.getCoordinate_y(pieceImfo.actPiece.row);// update position
                        pieceImfo.actPiece.previous_Column = pieceImfo.actPiece.getColumn(pieceImfo.actPiece.coordinate_x);
                        pieceImfo.actPiece.previous_Row = pieceImfo.actPiece.getRow(pieceImfo.actPiece.coordinate_y);
                        pieceImfo.actPiece.moved = true;// 合作邏輯
                    }
                    if (FusionPawnAndCar)
                        pieceImfo.simPiece = RookPawn.PawnFusionWithRook(pieceImfo);
                    else if (CanChangeWithQueen)
                        pieceImfo.simPiece = Queen.ChangeWithQueen(pieceImfo);
                    copyPieces(pieceImfo.simPiece,pieceImfo.Pieces);
                    if (Pawn.canPromote(pieceImfo)) {
                        PromotePawnList();
                        promotion = true;
                    } else
                        changePlayer();
                } else {
                    copyPieces(pieceImfo.Pieces, pieceImfo.simPiece);// 似乎是冗程式碼 可以考慮拿掉，不過先暫時保留。
                    pieceImfo.actPiece.coordinate_x = pieceImfo.actPiece.getCoordinate_x(pieceImfo.actPiece.previous_Column);
                    pieceImfo.actPiece.coordinate_y = pieceImfo.actPiece.getCoordinate_y(pieceImfo.actPiece.previous_Row);
                    pieceImfo.actPiece.column = pieceImfo.actPiece.previous_Column;
                    pieceImfo.actPiece.row = pieceImfo.actPiece.previous_Row;
                    pieceImfo.actPiece = null;
                }
            }
        }
        return -1;
    }

    private void simulate() {// 拖拉程式码 d
        canMove = false;
        validSquare = false;
        copyPieces(pieceImfo.Pieces, pieceImfo.simPiece);// 重新更新
        pieceImfo.actPiece.coordinate_x = mouse.coordinate_x - 45;// to do
        pieceImfo.actPiece.coordinate_y = mouse.coordinate_y - 45;// to do
        pieceImfo.actPiece.column = pieceImfo.actPiece.getColumn(pieceImfo.actPiece.coordinate_x);// check want move column
        pieceImfo.actPiece.row = pieceImfo.actPiece.getRow(pieceImfo.actPiece.coordinate_y);// check want move row
        if (pieceImfo.actPiece.canMove(pieceImfo.actPiece.column, pieceImfo.actPiece.row)) {// check canmove or not
            canMove = true;
            validSquare = true;
            if ((pieceImfo.actPiece.type == Type.Pawn && ((Pawn) pieceImfo.actPiece).AEnpassantmove == false) || pieceImfo.actPiece.type != Type.Pawn)
                pieceImfo.actPiece.hittingP = pieceImfo.actPiece.getHittingPiece(pieceImfo.actPiece.column, pieceImfo.actPiece.row);
            else
                for (Piece piece : pieceImfo.simPiece)
                    if (piece.column == pieceImfo.actPiece.column && piece.row == pieceImfo.actPiece.previous_Row && !piece.color.equals(pieceImfo.actPiece.color) && piece.twoStep)
                        pieceImfo.actPiece.hittingP = piece;

            FusionPawnAndCar = RookPawn.PawnCanFusionWithRook(pieceImfo);
            CanChangeWithQueen = (pieceImfo.actPiece.type == Type.Queen);

            if (pieceImfo.actPiece.hittingP != null && pieceImfo.actPiece.color.equals(pieceImfo.actPiece.hittingP.color) == false)
                for (int i = 0; i < pieceImfo.simPiece.size(); i++)
                    if (pieceImfo.simPiece.get(i).equals(pieceImfo.actPiece.hittingP))
                        pieceImfo.simPiece.remove(i);
        }
    }

    private void changePlayer() {
        currentColor = (currentColor.equals("white"))?"black":"white"; 
        for (Piece piece : pieceImfo.Pieces)
        if (piece.color == currentColor)
            piece.twoStep = false;
        pieceImfo.actPiece = null;
        pieceImfo.color = currentColor;
    }

    private void PromotePawnList() { // show promotion
        if(!promoPieces.get(0).color.equals(pieceImfo.color)){
            promoPieces.clear();
            promoPieces.add(new Rook(pieceImfo.color, 9, 2));
            promoPieces.add(new Queen(pieceImfo.color, 9, 3));
            promoPieces.add(new Bishop(pieceImfo.color, 9, 4));
            promoPieces.add(new Knight(pieceImfo.color, 9, 5));
        }
    }

    private void setPromotePiece(){
        promoPieces.add(new Rook(white, 9, 2));
        promoPieces.add(new Queen(white, 9, 3));
        promoPieces.add(new Bishop(white, 9, 4));
        promoPieces.add(new Knight(white, 9, 5));
    }
    
    public void setPieces() {
        pieceImfo.Pieces.add(new Pawn(white, 0, 6));
        pieceImfo.Pieces.add(new Pawn(white, 1, 6));
        pieceImfo.Pieces.add(new Pawn(white, 2, 6));
        pieceImfo.Pieces.add(new Pawn(white, 3, 6));
        pieceImfo.Pieces.add(new Pawn(white, 4, 6));
        pieceImfo.Pieces.add(new Rook(white, 0, 7));
        pieceImfo.Pieces.add(new Queen(white, 1, 7));
        pieceImfo.Pieces.add(new Bishop(white, 2, 7));
        pieceImfo.Pieces.add(new King(white, 3, 7));
        pieceImfo.Pieces.add(new Knight(white, 4, 7));
        pieceImfo.Pieces.add(new Pawn(black, 0, 1));
        pieceImfo.Pieces.add(new Pawn(black, 1, 1));
        pieceImfo.Pieces.add(new Pawn(black, 2, 1));
        pieceImfo.Pieces.add(new Pawn(black, 3, 1));
        pieceImfo.Pieces.add(new Pawn(black, 4, 1));
        pieceImfo.Pieces.add(new Rook(black, 4, 0));
        pieceImfo.Pieces.add(new Queen(black, 3, 0));
        pieceImfo.Pieces.add(new Bishop(black, 2, 0));
        pieceImfo.Pieces.add(new King(black, 1, 0));
        pieceImfo.Pieces.add(new Knight(black, 0, 0));
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
        for (Piece piece : pieceImfo.simPiece)// initialize
            g2.drawImage(piece.image, piece.coordinate_x, piece.coordinate_y, ChessBoard.SQUARE_SIZE,
                    ChessBoard.SQUARE_SIZE, null);

        if (pieceImfo.actPiece != null) {
            if (canMove) {
                g2.setColor(Color.white);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));// 设置绘画工具的透明度
                g2.fillRect(pieceImfo.actPiece.column * ChessBoard.SQUARE_SIZE, pieceImfo.actPiece.row * ChessBoard.SQUARE_SIZE,
                        ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));// 设置绘画工具的透明度
            g2.drawImage(pieceImfo.actPiece.image, pieceImfo.actPiece.coordinate_x, pieceImfo.actPiece.coordinate_y, ChessBoard.SQUARE_SIZE,
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