package com.chess.main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Simple Chess");// 视窗，simple chess 为 game title
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 关闭视窗即关闭游戏
        window.setResizable(false);// 不能更改视窗大小

        GamePanel gp = new GamePanel();
        window.add(gp);// window 是个容器，gamepanel则是个 组件可以 装上这个容器
        window.pack();// 让window依据gp的来调整大小

        window.setLocationRelativeTo(null); // 居中视窗
        window.setVisible(true);// 视窗可见

        gp.launchGame();

    }
}