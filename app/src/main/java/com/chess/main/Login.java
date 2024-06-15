package com.chess.main;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.AlphaComposite;

public class Login extends JPanel {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    public static final int PANELWIDTH = 500;
    public static final int PANELHEIGHT = 400;
    final int FPS = 60;
    Mouse mouse = new Mouse();
    JButton loginButton = new JButton("Strat Game");
    JButton exitButton = new JButton("Quite Game");
    JPanel titlePanel = new JPanel();
    static JLabel title = new JLabel("kirby Chess Game");

    public Login() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.white);
        addMouseListener(mouse);


        add(loginButton);
        add(exitButton);
        titlePanel.add(title);
        add(titlePanel);
      

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.switch2Game();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.exitGame();
            }
        });
    }
}
