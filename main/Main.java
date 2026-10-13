package main;

import java.io.IOException;

import javax.swing.JFrame;

public class Main {

  public static void main(String[] args) throws IOException {

    JFrame window = new JFrame();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    window.setResizable(true);
    window.setTitle("Dungeon Ninja");

    window.setMinimumSize(GamePanel.screenDimension);
    window.setMaximumSize(GamePanel.screenDimension);

    GamePanel gamePanel = new GamePanel();
    window.add(gamePanel);

    window.pack();

    window.setLocationRelativeTo(null);
    window.setVisible(true);

    gamePanel.setupGame(1);
    gamePanel.startGameThread();

  }
}