package com.chess.main;

import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
    static JPanel mainPanel;
    static CardLayout layout;
    static Login login;
    static GamePanel gp;
    static JFrame window;

    public static void switch2Game(){
        layout.show(mainPanel, "page1");
        gp.launchGame();
    }

    public static void exitGame(){
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.exit(0);
    }

    public static void main(String[] args) {
        mainPanel = new JPanel();
        layout = new CardLayout();
        gp = new GamePanel();
        login = new Login();
        window = new JFrame("Simple Chess");// 视窗，simple chess 为 game title
        
        mainPanel.setLayout(layout);//可以選擇要顯示哪個Panel
        mainPanel.add("page0", login);
        mainPanel.add("page1", gp);

        layout.show(mainPanel, "page0");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 关闭视窗即关闭游戏
        window.setResizable(false);// 不能更改视窗大小

        window.add(mainPanel);
        window.pack();// 让window依据gp的来调整大小

        window.setLocationRelativeTo(null); // 居中视窗
        window.setVisible(true);// 视窗可见
    }
}