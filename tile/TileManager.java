package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {
  GamePanel gp;
  public Tile[] tile;
  public int mapTileNum[] [];

  public TileManager(GamePanel gp) {

    this.gp = gp;

    tile = new Tile[10];
    mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

    getTileImage();
    loadMap("1.txt");
  }

  public void getTileImage() {
    try {

      tile[0] = new Tile();
      tile[0].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/earth.png"));

      tile[1] = new Tile();
      tile[1].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/wall.png"));
      tile[1].collision = true;



    } catch (Exception e) {e.printStackTrace();}
  }
  public void loadMap(String mapFile) {
    try {
      InputStream is = getClass().getResourceAsStream("/res/maps/"+mapFile);
      BufferedReader br = new BufferedReader(new InputStreamReader(is));

      int col = 0;
      int row =0;

      while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
        String line = br.readLine();

        while (col < gp.maxWorldCol) {
          String numbers[] = line.split(" ");

          int num = Integer.parseInt(numbers[col]);

          mapTileNum[col] [row] = num;
          col++;
        }
        if(col == gp.maxWorldCol) { col = 0; row++; }
      }
      br.close();
    } catch (Exception e) {}
  }
  public void draw(Graphics2D g2) {
   // g2.drawImage(tile[0].image, 0, 0, GamePanel.tileSize, GamePanel.tileSize, null);
    
   int worldCol = 0;
   int worldRow =0;


   while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

      int tileNum = mapTileNum[worldCol][worldRow];

      int worldX = worldCol * GamePanel.tileSize;
      int worldY = worldRow * GamePanel.tileSize;
      double screenX = worldX - gp.player.WorldX +gp.player.screenX;
      double screenY = worldY - gp.player.WorldY +gp.player.screenY;

      //basicly Render Distance 
      if (worldX + 1* (GamePanel.tileSize) > gp.player.WorldX - gp.player.screenX && 
          worldX - 2* GamePanel.tileSize < gp.player.WorldX + gp.player.screenX && 
          worldY + 1*GamePanel.tileSize > gp.player.WorldY - gp.player.screenY && 
          worldY - 2*GamePanel.tileSize < gp.player.WorldY + gp.player.screenY)  {
        g2.drawImage(tile[tileNum].image, (int)screenX, (int)screenY, GamePanel.tileSize, GamePanel.tileSize, null);
      }

      worldCol++;
      if(worldCol == gp.maxWorldCol) {
        worldCol = 0;
        worldRow++;

      }
   }
  }
}
