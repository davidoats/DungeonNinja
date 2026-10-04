package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;


import javax.swing.JPanel;
import entity.Player;
import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
  
  //SREEN SETTINGS
  static final int originalTileSize =16; //16x16 Tile
  static int scale = 4;

  public static int tileSize = originalTileSize * scale; //48x48 Tiles
  public static int maxScreenCol = 16;
  public static int maxScreenRow = 12;
  public static int screenWidth = maxScreenCol * tileSize;
  public static int screenHeight = maxScreenRow * tileSize;

  //WORLD SETTINGS
  public final int maxWorldCol = 15;
  public final int maxWorldRow = 15;

  //FPS
  int FPS = 120;

  static Dimension screenDimension = new Dimension(screenWidth, screenHeight);

  public CollisionChecker cChecker = new CollisionChecker(this);
  public AssetSetter aSetter = new AssetSetter(this);
  public UI ui = new UI(this);
  TileManager tileM = new TileManager(this);
  KeyHandler keyH = new KeyHandler(this);
  Thread gameThread;
  public Player player = new Player(this,keyH);
  public SuperObject obj[] = new SuperObject[10];
  Sound music = new Sound();
  Sound se = new Sound();

  public GamePanel() {
    this.setPreferredSize(screenDimension);
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyH);
    this.setFocusable(true);
  }

  public void setupGame(int level) {
    aSetter.setObject(level);
    player.setValues(level);
    tileM.loadMap(level+".txt");

    //playMusic(0);
  }

 
  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {
    
    while(gameThread != null) {
      // Set FPS
     
      //Using Sleep Method
      double drawInterval = 1000000000/FPS; // 1 second = 1 bil nanosec  0.016666 seconds per frame
      double nextDrawTime = System.nanoTime() + drawInterval;

     //UPDATE: update information like charactor pos.
     update();

     //DRAW: draw screen with updated info
     repaint();

      try {
        double remainingTime = nextDrawTime - System.nanoTime();
        remainingTime = remainingTime/1000000;

        if (remainingTime < 0) {remainingTime = 0;} //incase takes longer to draw than out sleeptime **FPS TOO high??

        Thread.sleep((long) remainingTime); //SLeep takes millisecs MUST Convert

        nextDrawTime += drawInterval;
      } catch (InterruptedException e) {
      
        }
    }
  }

  public void update() {
      player.update();

  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D)g;

    tileM.draw(g2);
    
    for(int i =0; i<obj.length; i++) {
      if (obj[i] != null) {
        obj[i].draw(g2, this);
      }
    }

    player.draw(g2);

    ui.draw(g2);
    
    g2.dispose();
  }

  public void playMusic(int i) {
    music.setFile(i);
    music.play();
    music.loop();
  }

  public void stopMusic() {
    music.stop();
  }

  public void playSE(int i) {
    se.setFile(i);
    se.play();
  }
}
