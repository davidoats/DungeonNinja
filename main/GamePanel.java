package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import javax.swing.JPanel;
import entity.*;
import entity.monsters.*;

import object.SuperObject;
import tile.TileManager;
import combat.DamageNumberManager;

public class GamePanel extends JPanel implements Runnable {

  // SREEN SETTINGS
  static final int originalTileSize = 16; // 16x16 Tile
  static int scale = 4;

  public static int tileSize = originalTileSize * scale; // 48x48 Tiles
  public static int maxScreenCol = 16;
  public static int maxScreenRow = 12;
  public static int screenWidth = maxScreenCol * tileSize;
  public static int screenHeight = maxScreenRow * tileSize;

  // WORLD SETTINGS
  public final int maxWorldCol = 15;
  public final int maxWorldRow = 15;

  // FPS
  int FPS = 120;

  static Dimension screenDimension = new Dimension(screenWidth, screenHeight);

  public CollisionChecker cChecker = new CollisionChecker(this);
  public AssetSetter aSetter = new AssetSetter(this);
  public UI ui = new UI(this);
  public TileManager tileM = new TileManager(this);
  KeyHandler keyH = new KeyHandler(this);
  public MouseHandler mouseH = new MouseHandler();
  Thread gameThread;
  public Player player = new Player(this, keyH);
  public ArrayList<SuperObject> obj = new ArrayList<>();
  public final Object objLock = new Object();
  Sound music = new Sound();
  Sound se = new Sound();



  //Replacing
  public TrainingDummy trainingDummy;

// With
  public final List<Monster> monsters = new ArrayList<>();

  public final DamageNumberManager damageNumbers = new DamageNumberManager();

  public GamePanel() {
    this.setPreferredSize(screenDimension);
    this.setBackground(Color.BLACK);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyH);
    this.addMouseListener(mouseH);
    this.addMouseMotionListener(mouseH);
    this.setFocusable(true);
  }

  public void setupGame(int level) {
    aSetter.setObject(level);
    player.setValues(level);
    tileM.loadMap(level + ".txt");
    trainingDummy = new TrainingDummy(this, GamePanel.tileSize * 9, GamePanel.tileSize * 11);
    
    // Clear monsters from the previous level
    monsters.clear();

    // Spawn our first zombie
    monsters.add(new Zombie( this, GamePanel.tileSize * 5, GamePanel.tileSize * 11));

    // Second zombie for testing
    monsters.add(new Zombie( this, GamePanel.tileSize * 7, GamePanel.tileSize * 7));

    // playMusic(0);
  }

  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {

    while (gameThread != null) {
      // Set FPS

      // Using Sleep Method
      double drawInterval = 1000000000 / FPS; // 1 second = 1 bil nanosec 0.016666 seconds per frame
      double nextDrawTime = System.nanoTime() + drawInterval;

      // UPDATE: update information like charactor pos.
      update();

      // DRAW: draw screen with updated info
      repaint();

      try {
        double remainingTime = nextDrawTime - System.nanoTime();
        remainingTime = remainingTime / 1000000;

        if (remainingTime < 0) {
          remainingTime = 0;
        } // incase takes longer to draw than out sleeptime **FPS TOO high??

        Thread.sleep((long) remainingTime); // SLeep takes millisecs MUST Convert

        nextDrawTime += drawInterval;
      } catch (InterruptedException e) {

      }
    }
  }

  public void update() {
    player.update();
    damageNumbers.update();

    // Update all living monsters
    for (Monster monster : monsters) {
      if (monster.isAlive()) {
        monster.update();
      }
    }

  }

  // Safely create a copy of the object list
  public List<SuperObject> getObjectSnapshot() {
    synchronized (objLock) {
      return new ArrayList<>(obj);
    }
  }

  @Override
  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    // Prevent Blurring
    g2.setRenderingHint(
        java.awt.RenderingHints.KEY_INTERPOLATION,
        java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

    // Draw tiles
    tileM.draw(g2);

    // Draw objects from a safe snapshot
    for (SuperObject object : getObjectSnapshot()) {
      object.draw(g2, this);
    }

    // Draw Dummy
    if (trainingDummy != null) {
      trainingDummy.draw(g2);
    }


      double cameraX = player.WorldX - player.screenX;
      double cameraY = player.WorldY - player.screenY;

      // Draw floating damage numbers above the game world
      damageNumbers.draw(g2, cameraX, cameraY);

    
    // Draw enemies
    // Draw all living monsters
    for (Monster monster : monsters) {
        if (monster.isAlive()) {
            monster.draw(g2);
        }
    }

// Draw player and weapon
player.draw(g2);
player.drawWeaponPose(g2);

    // Draw player & Weapon
    player.draw(g2);
    player.drawWeaponPose(g2);

    // Draw UI
    ui.draw(g2);
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
