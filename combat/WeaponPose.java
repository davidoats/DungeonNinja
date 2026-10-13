
package combat;

import java.awt.geom.Point2D;

/**
 * Represents the position and orientation of a weapon
 * at a specific moment during an attack.
 *
 * All coordinates are in world space.
 */
public class WeaponPose {

  // Position of the weapon's handle
  public final double handleX;
  public final double handleY;

  // Position where the actual blade begins
  public final double bladeBaseX;
  public final double bladeBaseY;

  // Position of the weapon's blade tip
  public final double tipX;
  public final double tipY;

  // Weapon rotation in radians
  public final double rotation;

  // Animation progress (0.0 to 1.0)
  public final double progress;

  // Whether the weapon can currently deal damage
  public final boolean activeStrike;

  /**
   * Creates a new weapon pose.
   */

  public WeaponPose(
      double handleX,
      double handleY,
      double bladeBaseX,
      double bladeBaseY,
      double tipX,
      double tipY,
      double rotation,
      double progress,
      boolean activeStrike) {

    this.handleX = handleX;
    this.handleY = handleY;

    this.bladeBaseX = bladeBaseX;
    this.bladeBaseY = bladeBaseY;

    this.tipX = tipX;
    this.tipY = tipY;

    this.rotation = rotation;
    this.progress = progress;
    this.activeStrike = activeStrike;
  }

  /**
   * Returns the handle position.
   */
  public Point2D.Double getHandle() {
    return new Point2D.Double(handleX, handleY);
  }

  public Point2D.Double getBladeBase() {
    return new Point2D.Double(bladeBaseX, bladeBaseY);
  }

  /**
   * Returns the blade tip position.
   */
  public Point2D.Double getTip() {
    return new Point2D.Double(tipX, tipY);
  }
}
