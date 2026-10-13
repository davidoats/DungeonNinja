
package combat;

import java.awt.Rectangle;
import java.awt.geom.Line2D;

/**
 * Handles collision detection between weapon blades
 * and rectangular enemy hitboxes.
 */
public final class WeaponCollision {

  private WeaponCollision() {
  }

  /**
   * Checks whether a weapon blade intersects a hitbox.
   *
   * @param handleX Blade's starting X coordinate
   * @param handleY Blade's starting Y coordinate
   * @param tipX    Blade's ending X coordinate
   * @param tipY    Blade's ending Y coordinate
   * @param hitbox  Enemy's world-space collision rectangle
   * @return true if the blade intersects the hitbox
   */

  public static boolean bladeIntersects(
      double handleX, double handleY,
      double tipX, double tipY,
      Rectangle hitbox) {

    if (hitbox == null) {
      return false;
    }

    // Check whether either end is inside the hitbox
    if (hitbox.contains(handleX, handleY) ||
        hitbox.contains(tipX, tipY)) {
      return true;
    }

    // Check whether the blade crosses the hitbox
    return hitbox.intersectsLine(
        new Line2D.Double(
            handleX, handleY,
            tipX, tipY));
  }
}
