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
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    Piece activePiece;
    ArrayList<Piece> promoPieces = new ArrayList<>();
    private int curPieceRow = -1;
    private int curPieceCol = -1;

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
        copyPieces(pieces, simPieces);

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
        pieces.add(new Knight(white, 3, 3));
        pieces.add(new Pawn(black, 2, 2));
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
                                simPieces.add(new Rook(currentColor, activePiece.column, activePiece.row));
                                break;
                            case Knight:
                                simPieces.add(new Knight(currentColor, activePiece.column, activePiece.row));
                                break;
                            case Queen:
                                simPieces.add(new Queen(currentColor, activePiece.column, activePiece.row));
                                break;
                            case Bishop:
                                simPieces.add(new Bishop(currentColor, activePiece.column, activePiece.row));
                                break;
                            default:
                                break;
                        }
                        simPieces.remove(activePiece);
                        copyPieces(simPieces, pieces);
                        promotion = false;
                        changePlayer();
                    }

                    if (CanChangeWithQueen && piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE
                            && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                        switch (piece.type) {
                            case Rook:
                                simPieces.add(new Rook(currentColor, curPieceCol, curPieceRow));
                                break;
                            case Knight:
                                simPieces.add(new Knight(currentColor, curPieceCol, curPieceRow));
                                break;
                            case Queen:
                                simPieces.add(new Queen(currentColor, curPieceCol, curPieceRow));
                                break;
                            case Bishop:
                                simPieces.add(new Bishop(currentColor, curPieceCol, curPieceRow));
                                break;
                            default:
                                break;
                        }

                        for (Piece piece1 : pieces) {
                            if (piece1.type == Type.Pawn && ((piece1.color == "white" && piece1.row == 0)
                                    || (piece1.color == "black" && piece1.row == 7))) {
                                System.out.println("tester");
                                simPieces.remove(piece1);
                            }
                        }
                        copyPieces(simPieces, pieces);
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
                    for (Piece piece : simPieces)// 择取行动方的棋子
                        if (piece.color == currentColor && piece.column == mouse.coordinate_x / ChessBoard.SQUARE_SIZE
                                && piece.row == mouse.coordinate_y / ChessBoard.SQUARE_SIZE) {
                            activePiece = piece;
                            curPieceCol = piece.column;
                            curPieceRow = piece.row;
                        }
                } else
                    simulate();// 更改成点亮的模式 todo+
            }
            if (mouse.pressed == false && activePiece != null) {
                if (validSquare) {// validSquare 可能有 邏輯重複的問題
                    if (activePiece.type == Type.Pawn)// 合作邏輯
                        if (Math.abs(activePiece.row - activePiece.previous_Row) == 2)
                            activePiece.twoStep = true;

                    System.out.println(activePiece.type + " " + rightClick + " " + RookPawnCanDifuse());
                    if (activePiece.type == Type.RookPawn && rightClick == true && RookPawnCanDifuse() == true) {// click
                                                                                                                 // logic
                        int UnfuseRow = activePiece.row;
                        int UnfuseColumn = activePiece.column;
                        if (activePiece.color.equals("white")) {
                            simPieces.remove(activePiece);
                            simPieces.add(new Rook(white, UnfuseColumn, UnfuseRow));
                            Piece pawn = new Pawn(white, UnfuseColumn, UnfuseRow + 1);
                            pawn.moved = true;
                            simPieces.add(pawn);
                        }
                        if (activePiece.color.equals("black")) {
                            simPieces.remove(activePiece);
                            simPieces.add(new Rook(black, UnfuseColumn, UnfuseRow));
                            Piece pawn = new Pawn(black, UnfuseColumn, UnfuseRow - 1);
                            pawn.moved = true;
                            simPieces.add(pawn);
                        }

                    }
                    copyPieces(simPieces, pieces);// 更新棋子
                    if (activePiece != null) {
                        activePiece.coordinate_x = activePiece.getCoordinate_x(activePiece.column);// update position
                        activePiece.coordinate_y = activePiece.getCoordinate_y(activePiece.row);// update position
                        activePiece.previous_Column = activePiece.getColumn(activePiece.coordinate_x);
                        activePiece.previous_Row = activePiece.getRow(activePiece.coordinate_y);
                        activePiece.moved = true;// 合作邏輯
                    }

                    if (FusionPawnAndCar) {
                        PawnFusionWithRook();
                    } else if (CanChangeWithQueen) {
                        ChangeWithQueen(curPieceCol, curPieceRow);
                    }

                    if (canPromote()) {
                        promotion = true;
                    } else {
                        changePlayer();
                    }

                } else {
                    copyPieces(pieces, simPieces);// 似乎是冗程式碼 可以考慮拿掉，不過先暫時保留。
                    activePiece.coordinate_x = activePiece.getCoordinate_x(activePiece.previous_Column);
                    activePiece.coordinate_y = activePiece.getCoordinate_y(activePiece.previous_Row);
                    activePiece.column = activePiece.previous_Column;
                    activePiece.row = activePiece.previous_Row;
                    activePiece = null;
                }

            }
        }
        return -1;
    }

    private void simulate() {// 拖拉程式码 d
        canMove = false;
        validSquare = false;
        copyPieces(pieces, simPieces);// 重新更新
        activePiece.coordinate_x = mouse.coordinate_x - 45;// to do
        activePiece.coordinate_y = mouse.coordinate_y - 45;// to do
        activePiece.column = activePiece.getColumn(activePiece.coordinate_x);// 計算落點的column 和 row
        activePiece.row = activePiece.getRow(activePiece.coordinate_y);// 計算落點的column 和 row
        if (activePiece.canMove(activePiece.column, activePiece.row)) {// 檢查落點是否可走。
            canMove = true;
            validSquare = true;

            if ((activePiece.type == Type.Pawn && ((Pawn) activePiece).AEnpassantmove == false)
                    || activePiece.type != Type.Pawn)
                activePiece.hittingP = activePiece.getHittingPiece(activePiece.column, activePiece.row);// 更新

            else
                for (Piece piece : GamePanel.simPieces)
                    if (piece.column == activePiece.column && piece.row == activePiece.previous_Row
                            && piece.color.equals(activePiece.color) == false && piece.twoStep == true)
                        activePiece.hittingP = piece;

            FusionPawnAndCar = PawnCanFusionWithRook();
            CanChangeWithQueen = (activePiece.type == Type.Queen);

            if (activePiece.hittingP != null && activePiece.color.equals(activePiece.hittingP.color) == false)
                for (int i = 0; i < simPieces.size(); i++)
                    if (simPieces.get(i).equals(activePiece.hittingP))
                        simPieces.remove(i);
        }
    }

    private void changePlayer() {
        if (currentColor.equals("white")) {
            currentColor = "black";
            for (Piece piece : pieces)
                if (piece.color == "black")
                    piece.twoStep = false;
            activePiece = null;
        } else {
            currentColor = "white";
            for (Piece piece : pieces)
                if (piece.color == "white")
                    piece.twoStep = false;
            activePiece = null;
        }
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

    private boolean PawnCanFusionWithRook() {// 可以往後走 所以 可以融合 條件限制寫在了pawn
        if (activePiece.type == Type.Pawn)
            if ((currentColor == "white" && (activePiece.row > activePiece.previous_Row))
                    || ((currentColor == "black") && (activePiece.row < activePiece.previous_Row)))
                return true;
        return false;
    }

    private void PawnFusionWithRook() {
        int FusionColumn = activePiece.column;
        int FusionRow = activePiece.row;
        ArrayList<Piece> removePiece = new ArrayList<>();
        for (Piece piece : simPieces)
            if (FusionColumn == piece.column && FusionRow == piece.row)
                removePiece.add(piece);
        simPieces.removeAll(removePiece);
        simPieces.add(new RookPawn(currentColor, FusionColumn, FusionRow));
        copyPieces(simPieces, pieces);
    }

    private boolean RookPawnCanDifuse() {
        if (activePiece.row == activePiece.previous_Row && activePiece.column == activePiece.previous_Column) {
            if (activePiece.color.equals("white"))
                if (activePiece.isAnEmptySquare(activePiece.column, activePiece.row + 1))
                    return true;
            if (activePiece.color.equals("black"))
                if (activePiece.isAnEmptySquare(activePiece.column, activePiece.row - 1))
                    return true;
        }
        return false;
    }

    private void ChangeWithQueen(int OrgC, int OrgR) {
        int ChangeColumn = activePiece.column;
        int ChangeRow = activePiece.row;
        ArrayList<Piece> ChangePiece = new ArrayList<>();
        for (Piece piece : simPieces)
            if (ChangeColumn == piece.column && ChangeRow == piece.row && (piece.type != Type.Queen))
                ChangePiece.add(piece);
        if (ChangePiece.size() <= 0)
            return;
        simPieces.remove(ChangePiece.get(0));
        switch (ChangePiece.get(0).type) {
            case Rook:
                simPieces.add(new Rook(currentColor, OrgC, OrgR));
                break;
            case Knight:
                simPieces.add(new Knight(currentColor, OrgC, OrgR));
                break;
            case King:
                simPieces.add(new King(currentColor, OrgC, OrgR));
                break;
            case Bishop:
                simPieces.add(new Bishop(currentColor, OrgC, OrgR));
                break;
            case RookPawn:
                simPieces.add(new RookPawn(currentColor, OrgC, OrgR));
                break;
            case Pawn:
                simPieces.add(new Pawn(currentColor, OrgC, OrgR));
                break;
            case Queen:
                simPieces.add(new Queen(currentColor, OrgC, OrgR));
                break;
            default:
                System.out.println("wr");
                break;
        }
        copyPieces(simPieces, pieces);
    }

    public void paintComponent(Graphics g) {// handle drawing component g automatik transfer form swing //
                                            // 算是一种overide？？？
        super.paintComponent(g);// 没有搞懂在干嘛
        Graphics2D g2 = (Graphics2D) g;
        chessboard.drawTheBoard(g2);

        // Piece
        for (Piece piece : simPieces)// initialize
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