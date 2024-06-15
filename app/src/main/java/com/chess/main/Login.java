package com.chess.main;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.*;

public class Login extends JPanel {
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    final int FPS = 60;
    Mouse mouse = new Mouse();
    JButton loginButton = new JButton("Start Game");
    JButton exitButton = new JButton("Quit Game");
    JPanel titlePanel = new JPanel();
    static JLabel title = new JLabel("Kirby Chess Game");

    public Login() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseListener(mouse);
        setLayout(null);

        title.setForeground(Color.white);
        title.setFont(new Font("Comic Sans", Font.BOLD, 36));
        titlePanel.setBounds(350, 50,400, 100);
        titlePanel.setBackground(Color.black);
        titlePanel.add(title);

        loginButton.setBounds(400, 200, 300, 50);
        exitButton.setBounds(400, 300, 300, 50);

        add(loginButton);
        add(exitButton);
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
