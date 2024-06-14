package com.chess.main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {

    public int coordinate_x, coordinate_y;
    public boolean pressed;
    public boolean leftButtonPressed;
    public boolean rightButtonPressed;

    public void mousePressed(MouseEvent e) {
        pressed = true;
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftButtonPressed = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightButtonPressed = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        pressed = false;
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftButtonPressed = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightButtonPressed = false;
        }
    }

    public void mouseDragged(MouseEvent e) {
        coordinate_x = e.getX();
        coordinate_y = e.getY();
    }

    public void mouseMoved(MouseEvent e) {
        coordinate_x = e.getX();
        coordinate_y = e.getY();
    }
}
