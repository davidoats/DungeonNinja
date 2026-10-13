
package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;

/**
 * A stationary target used for testing combat.
 */
public class TrainingDummy extends Entity {

    private final GamePanel gp;

    // Health
    private final int maxHealth = 100;
    private int health = maxHealth;

    /**
     * Creates a training dummy at a world position.
     *
     * @param gp     Game panel
     * @param worldX Horizontal world position
     * @param worldY Vertical world position
     */
    public TrainingDummy(GamePanel gp, double worldX, double worldY) {
        this.gp = gp;

        WorldX = worldX;
        WorldY = worldY;

        // Dummy collision area
        solidArea = new Rectangle(
                8, 8,
                GamePanel.tileSize - 16,
                GamePanel.tileSize - 16);

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    /**
     * Draws the dummy relative to the player's camera.
     */
    public void draw(Graphics2D g2) {

        int screenX = (int) (WorldX - gp.player.WorldX + gp.player.screenX);

        int screenY = (int) (WorldY - gp.player.WorldY + gp.player.screenY);

        // Dummy body
        g2.setColor(new Color(150, 100, 55));
        g2.fillRect(
                screenX + GamePanel.tileSize / 4,
                screenY + GamePanel.tileSize / 4,
                GamePanel.tileSize / 2,
                GamePanel.tileSize / 2);

        // Dummy target
        g2.setColor(Color.RED);
        g2.fillOval(
                screenX + GamePanel.tileSize / 3,
                screenY + GamePanel.tileSize / 3,
                GamePanel.tileSize / 3,
                GamePanel.tileSize / 3);

        // Health bar dimensions
        int barWidth = GamePanel.tileSize;
        int barHeight = 6;

        // Position above the dummy
        int barX = screenX;
        int barY = screenY - 12;

        // Calculate remaining health percentage
        double healthPercent = (double) health / maxHealth;

        int healthWidth = (int) (barWidth * healthPercent);

        // Background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        // Remaining health
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, healthWidth, barHeight);

        // Border
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);

    }

    /**
     * Reduces the dummy's health when it is hit.
     *
     * @param damage Amount of damage received
     */
    public void takeDamage(int damage) {

        if (damage <= 0 || health <= 0) {
            return;
        }

        health = Math.max(0, health - damage);

        System.out.println(
                "Training Dummy HP: " + health + "/" + maxHealth);
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

}
