
package combat;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages floating damage numbers throughout the world.
 */
public class DamageNumberManager {

  private final ArrayList<DamageNumber> numbers = new ArrayList<>();

  /**
   * Creates a floating damage number at a world position.
   */
  public void add(int damage, double worldX, double worldY) {
    numbers.add(new DamageNumber(damage, worldX, worldY));
  }

  /**
   * Removes expired damage numbers.
   */
  public void update() {
    long currentTime = System.currentTimeMillis();

    Iterator<DamageNumber> iterator = numbers.iterator();

    while (iterator.hasNext()) {
      if (iterator.next().isExpired(currentTime)) {
        iterator.remove();
      }
    }
  }

  /**
   * Draws all active damage numbers.
   */
  public void draw(Graphics2D g2,
      double cameraX, double cameraY) {

    for (DamageNumber number : numbers) {
      number.draw(g2, cameraX, cameraY);
    }
  }

  /**
   * Clears all damage numbers.
   */
  public void clear() {
    numbers.clear();
  }
}
