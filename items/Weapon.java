
package items;

import combat.AnimationProfile;
import java.awt.image.BufferedImage;

/**
 * Represents an equippable weapon.
 *
 * Each weapon has its own appearance and can use
 * a reusable attack animation profile.
 */

public class Weapon {

  private final String name;
  private final double tipX;
  private final double tipY;
  private final double bladeBaseX;
  private final double bladeBaseY;
  private final int damage;
  private final double knockback;
  private final AnimationProfile animationProfile;
  private final BufferedImage sprite;

  /**
   * Creates a weapon.
   *
   * @param name             Name of the weapon
   * @param animationProfile Attack animations used by the weapon
   * @param sprite           Image used to draw the weapon
   */

  public Weapon(String name, AnimationProfile animationProfile,
      BufferedImage sprite,
      double tipX, double tipY,
      double bladeBaseX, double bladeBaseY, int damage, double knockback) {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Weapon needs a name");
    }

    if (animationProfile == null) {
      throw new IllegalArgumentException(
          "Weapon needs an animation profile");
    }

    if (sprite == null) {
      throw new IllegalArgumentException(
          "Weapon needs a sprite");
    }

    if (!Double.isFinite(tipX) || !Double.isFinite(tipY)
        || tipX < 0 || tipX > 1 || tipY < 0 || tipY > 1) {
      throw new IllegalArgumentException(
          "Weapon tip coordinates must be between 0 and 1");
    }

    if (!Double.isFinite(bladeBaseX) ||
        !Double.isFinite(bladeBaseY) ||
        bladeBaseX < 0 || bladeBaseX > 1 ||
        bladeBaseY < 0 || bladeBaseY > 1) {
      throw new IllegalArgumentException(
          "Blade base coordinates must be between 0 and 1");
    }

    if (damage < 0 || !Double.isFinite(knockback) || knockback < 0) {
    throw new IllegalArgumentException(
            "Damage and knockback must be nonnegative."
    );
}

    
    this.bladeBaseX = bladeBaseX;
    this.bladeBaseY = bladeBaseY;
    this.name = name;
    this.animationProfile = animationProfile;
    this.sprite = sprite;
    this.tipX = tipX;
    this.tipY = tipY;
    this.damage = damage;
    this.knockback = knockback;
  }

  public int getDamage() {
    return damage;
}

  public double getKnockback() {
    return knockback;
  }

  public double getTipX() {
    return tipX;
  }

  public double getTipY() {
    return tipY;
  }

  public double getBladeBaseX() {
    return bladeBaseX;
  }

  public double getBladeBaseY() {
    return bladeBaseY;
  }

  public String getName() {
    return name;
  }

  public AnimationProfile getAnimationProfile() {
    return animationProfile;
  }

  public BufferedImage getSprite() {
    return sprite;
  }
}
