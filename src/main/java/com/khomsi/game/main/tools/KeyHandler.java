package main.java.com.khomsi.game.main.tools;

import main.java.com.khomsi.game.main.GamePanel;

import java.awt.event.*;

public class KeyHandler implements KeyListener {
    GamePanel gamePanel;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    //Debug
    public boolean debugMode = false;

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //none
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //returns int, associated with the key in this event
        //for example, 17-ctrl, 8-backspace, 10-enter
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        //TODO Debug
        // if it's false, make it true, else make it false
        if (code == KeyEvent.VK_F9) debugMode = !debugMode;
        if (code == KeyEvent.VK_F8) gamePanel.tileManager.loadMap("/maps/world01.txt");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
    }
}
