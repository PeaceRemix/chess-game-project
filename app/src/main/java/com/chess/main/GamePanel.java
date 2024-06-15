package com.chess.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import com.chess.piece.Piece;
import com.chess.piece.Pawn;
import com.chess.piece.Rook;
import com.chess.piece.Queen;
import com.chess.piece.Bishop;
import com.chess.piece.King;
import com.chess.piece.Knight;
import com.chess.piece.RookPawn;
import com.chess.piece.PieceImfo;
import java.awt.AlphaComposite;

//version2 修理

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    public int gameStatic = -1;
    public boolean gameOver = false;
    final int FPS = 60;
    Thread gameThread;// 没搞懂
    ChessBoard chessboard = new ChessBoard();
    Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();// 为啥需要两行
    Piece activePiece;
    ArrayList<Piece> promoPieces = new ArrayList<>();
    public static PieceImfo pieceImfo = new PieceImfo();

    // 颜色
    /*
     * public static final int white = 0;
     * public static final int black = 1;
     * int currentColor = white;
     */

    public static final String white = "white";
    public static final String black = "black";
    String currentColor = white;

    // 移動
    private boolean canMove = false;
    private boolean validSquare = false;
    private boolean promotion = false;
    private boolean FusionPawnAndCar = false;
    private boolean CanChangeWithQueen = false;
    public static boolean rightClick = false;
    public static boolean leftClick = false;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));// 视窗大小
        setBackground(Color.black);// 背景颜色
        addMouseMotionListener(mouse);// 同理
        addMouseListener(mouse);// 偵測滑鼠事件的 listener

        setPieces();
        copyPieces(pieces, pieceImfo.simPiece);
    }

    public void launchGame() {// gameThread 是顯示和運算在不同 Thread 上，像這裡畫面FPS 60，但程式執行不可能只有 60 times PerScond
        gameThread = new Thread(this);
        gameThread.start();// 呼唤 run？？？
    }

    public void setPieces() {

        pieces.add(new Pawn(white, 0, 6));
        pieces.add(new Pawn(white, 1, 6));
        pieces.add(new Pawn(white, 2, 6));
        pieces.add(new Pawn(white, 3, 6));
        pieces.add(new Pawn(white, 4, 6));
        pieces.add(new Rook(white, 0, 7));
        pieces.add(new Queen(white, 1, 7));
        pieces.add(new Bishop(white, 2, 7));
        pieces.add(new King(white, 3, 7));
        pieces.add(new Knight(white, 4, 7));
        pieces.add(new Pawn(black, 0, 1));
        pieces.add(new Pawn(black, 1, 1));
        pieces.add(new Pawn(black, 2, 1));
        pieces.add(new Pawn(black, 3, 1));
        pieces.add(new Pawn(black, 4, 1));
        pieces.add(new Rook(black, 4, 0));
        pieces.add(new Queen(black, 3, 0));
        pieces.add(new Bishop(black, 2, 0));
        pieces.add(new King(black, 1, 0));
        pieces.add(new Knight(black, 0, 0));

    }

    private void copyPieces(ArrayList<Piece> Beginning, ArrayList<Piece> Destination) {
        Destination.clear();
        for (int i = 0; i < Beginning.size(); i++)
            Destination.add(Beginning.get(i));
    }

    public void run() {// override runnan
        double drawInterval = 1000000000 / FPS;// 每一帧的时间间隔
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while (gameThread != null && gameStatic == -1) {
            currentTime = System.nanoTime();
            delta = delta + (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {// 刷新
                gameStatic = update();// 调用update 更细 画面
                repaint();// 调用repaint 更新 画面 为什么 可以 召唤到 paint component 是不是 因为 基继承了
                delta = 0;
                if (gameStatic > -1)
                    break;
            }

            // end panel need to show, gameStatic == 0 -> whitewin, 1 -> blackwin

        }
    }

    private int update() {// handle updating stuff piece position number of the piece.

        boolean whiteKingE = false;
        boolean blackKingE = false;
        pieceImfo.color = currentColor;

        for (Piece piece1 : pieces) {
            if (piece1.color == "white" && piece1.type == Type.King)
                whiteKingE = true;
            if (piece1.color == "black" && piece1.type == Type.King)
                blackKingE = true;
        }
        if (!whiteKingE) {
            gameOver = true;
            System.out.println("********Blue Win!********");
            return 0;
        }
        if (!blackKingE) {
            gameOver = true;
            System.out.println("********Pink Win!********");
            return 1;
        }
        if (promotion) {
            if (mouse.pressed) {
                for (Piece piece : promoPieces) {

                    if (piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE
                            && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE && !CanChangeWithQueen) {
                        switch (piece.type) {
                            case Rook:
                                pieceImfo.simPiece.add(new Rook(currentColor, activePiece.column, activePiece.row));
                                break;
                            case Knight:
                                pieceImfo.simPiece.add(new Knight(currentColor, activePiece.column, activePiece.row));
                                break;
                            case Queen:
                                pieceImfo.simPiece.add(new Queen(currentColor, activePiece.column, activePiece.row));
                                break;
                            case Bishop:
                                pieceImfo.simPiece.add(new Bishop(currentColor, activePiece.column, activePiece.row));
                                break;
                            default:
                                break;
                        }
                        pieceImfo.simPiece.remove(activePiece);
                        copyPieces(pieceImfo.simPiece, pieces);
                        promotion = false;
                        changePlayer();
                    }

                    if (CanChangeWithQueen && piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE
                            && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                        switch (piece.type) {
                            case Rook:
                                pieceImfo.simPiece.add(new Rook(currentColor, pieceImfo.oriCol, pieceImfo.oriRow));
                                break;
                            case Knight:
                                pieceImfo.simPiece.add(new Knight(currentColor, pieceImfo.oriCol, pieceImfo.oriRow));
                                break;
                            case Queen:
                                pieceImfo.simPiece.add(new Queen(currentColor, pieceImfo.oriCol, pieceImfo.oriRow));
                                break;
                            case Bishop:
                                pieceImfo.simPiece.add(new Bishop(currentColor, pieceImfo.oriCol, pieceImfo.oriRow));
                                break;
                            default:
                                break;
                        }

                        for (Piece piece1 : pieces) {
                            if (piece1.type == Type.Pawn && ((piece1.color == "white" && piece1.row == 0)
                                    || (piece1.color == "black" && piece1.row == 7))) {
                                System.out.println("tester");
                                    pieceImfo.simPiece.remove(piece1);
                            }
                        }
                        copyPieces(pieceImfo.simPiece, pieces);
                        promotion = false;
                        changePlayer();
                    }
                }
            }

        }

        else {
            if (mouse.pressed) {// 按下按钮的判断
                rightClick = mouse.rightButtonPressed;
                leftClick = mouse.leftButtonPressed;
                if (activePiece == null) {
                    for (Piece piece : pieceImfo.simPiece)// 择取行动方的棋子
                        if (piece.color == currentColor && piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE
                                && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                            activePiece = piece;
                            pieceImfo.actPiece = activePiece;
                            pieceImfo.oriCol = piece.column;
                            pieceImfo.oriRow = piece.row;
                            pieceImfo.actPreCol = activePiece.previous_Column;
                            pieceImfo.actPreRow = activePiece.previous_Row;
                            pieceImfo.color = activePiece.color;
                        }
                } else
                    simulate();// 更改成点亮的模式 todo+
            }
            if (mouse.pressed == false && activePiece != null) {
                if (validSquare) {// validSquare 可能有 邏輯重複的問題
                    if (activePiece.type == Type.Pawn)// 合作邏輯
                        if (Math.abs(activePiece.row - activePiece.previous_Row) == 2)
                            activePiece.twoStep = true;
                    if (activePiece.type == Type.RookPawn && rightClick &&RookPawn.RookPawnCanDifuse(pieceImfo)) {// click
                        int UnfuseRow = activePiece.row;
                        int UnfuseColumn = activePiece.column;
                        if (activePiece.color.equals("white")) {
                            pieceImfo.simPiece.remove(activePiece);
                            pieceImfo.simPiece.add(new Rook(white, UnfuseColumn, UnfuseRow));
                            Piece pawn = new Pawn(white, UnfuseColumn, UnfuseRow + 1);
                            pawn.moved = true;
                            pieceImfo.simPiece.add(pawn);
                        }
                        if (activePiece.color.equals("black")) {
                            pieceImfo.simPiece.remove(activePiece);
                            pieceImfo.simPiece.add(new Rook(black, UnfuseColumn, UnfuseRow));
                            Piece pawn = new Pawn(black, UnfuseColumn, UnfuseRow - 1);
                            pawn.moved = true;
                            pieceImfo.simPiece.add(pawn);
                        }

                    }
                    copyPieces(pieceImfo.simPiece, pieces);// 更新棋子
                    if (activePiece != null) {
                        activePiece.coordinate_x = activePiece.getCoordinate_x(activePiece.column);// update position
                        activePiece.coordinate_y = activePiece.getCoordinate_y(activePiece.row);// update position
                        activePiece.previous_Column = activePiece.getColumn(activePiece.coordinate_x);
                        activePiece.previous_Row = activePiece.getRow(activePiece.coordinate_y);
                        pieceImfo.actPreCol = activePiece.previous_Column;
                        pieceImfo.actPreRow = activePiece.previous_Row;
                        activePiece.moved = true;// 合作邏輯
                    }

                    if (FusionPawnAndCar) {
                        pieceImfo.simPiece = RookPawn.PawnFusionWithRook(pieceImfo);
                        copyPieces(pieceImfo.simPiece, pieces);
                    } else if (CanChangeWithQueen){
                        pieceImfo.simPiece = Queen.ChangeWithQueen(pieceImfo);
                        copyPieces(pieceImfo.simPiece,pieces);
                    }

                    if (canPromote()) {
                        promotion = true;
                    } else {
                        changePlayer();
                    }

                } else {
                    copyPieces(pieces, pieceImfo.simPiece);// 似乎是冗程式碼 可以考慮拿掉，不過先暫時保留。
                    activePiece.coordinate_x = activePiece.getCoordinate_x(activePiece.previous_Column);
                    activePiece.coordinate_y = activePiece.getCoordinate_y(activePiece.previous_Row);
                    activePiece.column = activePiece.previous_Column;
                    activePiece.row = activePiece.previous_Row;
                    pieceImfo.actRow =  activePiece.row;
                    pieceImfo.actCol = activePiece.column;
                    activePiece = null;
                }

            }
        }
        return -1;
    }

    private void simulate() {// 拖拉程式码 d
        canMove = false;
        validSquare = false;
        copyPieces(pieces, pieceImfo.simPiece);// 重新更新
        activePiece.coordinate_x = mouse.coordinate_x - 45;// to do
        activePiece.coordinate_y = mouse.coordinate_y - 45;// to do
        activePiece.column = activePiece.getColumn(activePiece.coordinate_x);// 計算落點的column 和 row
        activePiece.row = activePiece.getRow(activePiece.coordinate_y);// 計算落點的column 和 row
        pieceImfo.actRow = activePiece.row;
        pieceImfo.actCol = activePiece.column;
        if (activePiece.canMove(activePiece.column, activePiece.row)) {// 檢查落點是否可走。
            canMove = true;
            validSquare = true;

            if ((activePiece.type == Type.Pawn && ((Pawn) activePiece).AEnpassantmove == false)
                    || activePiece.type != Type.Pawn)
                activePiece.hittingP = activePiece.getHittingPiece(activePiece.column, activePiece.row);// 更新

            else
                for (Piece piece : pieceImfo.simPiece) // Gamepel.???
                    if (piece.column == activePiece.column && piece.row == activePiece.previous_Row
                            && piece.color.equals(activePiece.color) == false && piece.twoStep == true)
                        activePiece.hittingP = piece;

            FusionPawnAndCar = RookPawn.PawnCanFusionWithRook(pieceImfo);
            CanChangeWithQueen = (activePiece.type == Type.Queen);

            if (activePiece.hittingP != null && activePiece.color.equals(activePiece.hittingP.color) == false)
                for (int i = 0; i < pieceImfo.simPiece.size(); i++)
                    if (pieceImfo.simPiece.get(i).equals(activePiece.hittingP))
                        pieceImfo.simPiece.remove(i);
        }
    }

    private void changePlayer() {
        currentColor = (currentColor.equals("white"))?"black":"white"; 
        for (Piece piece : pieces)
        if (piece.color == currentColor)
            piece.twoStep = false;
        activePiece = null;
        pieceImfo.actPiece = null;
        pieceImfo.color = currentColor;
    }

    private boolean canPromote() {
        for (Piece piece : pieces) {
            if (piece.type == Type.Pawn
                    && ((piece.color == "white" && piece.row == 0) || (piece.color == "black" && piece.row == 7))) {
                promoPieces.clear();
                promoPieces.add(new Rook(piece.color, 9, 2));
                promoPieces.add(new Queen(piece.color, 9, 3));
                promoPieces.add(new Bishop(piece.color, 9, 4));
                promoPieces.add(new Knight(piece.color, 9, 5));
                return true;
            }
        }
        return false;
    }

    public void paintComponent(Graphics g) {// handle drawing component g automatik transfer form swing //
                                            // 算是一种overide？？？
        super.paintComponent(g);// 没有搞懂在干嘛
        Graphics2D g2 = (Graphics2D) g;
        chessboard.drawTheBoard(g2);

        // Piece
        for (Piece piece : pieceImfo.simPiece)// initialize
            g2.drawImage(piece.image, piece.coordinate_x, piece.coordinate_y, ChessBoard.SQUARE_SIZE,
                    ChessBoard.SQUARE_SIZE, null);

        if (activePiece != null) {// 可以删除 不影响运作
            if (canMove) {
                g2.setColor(Color.white);// 设置绘画工具的基础颜色
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));// 设置绘画工具的透明度
                g2.fillRect(activePiece.column * ChessBoard.SQUARE_SIZE, activePiece.row * ChessBoard.SQUARE_SIZE,
                        ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));// 设置绘画工具的透明度
            g2.drawImage(activePiece.image, activePiece.coordinate_x, activePiece.coordinate_y, ChessBoard.SQUARE_SIZE,
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