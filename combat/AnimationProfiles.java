
package combat;

//Contains reusable weapon animation profiles.
public final class AnimationProfiles {

  private AnimationProfiles() {
    // Prevent creating instances of this utility class
  }

  public static final AnimationProfile STANDARD_SWORD = new AnimationProfile(
      "Standard Sword",
      // First attack: clockwise slash
      new AttackAnimation(
          AttackType.SLASH,
          400,
          -60,
          60,
          1.0),

      // Second attack: reverse slash
      new AttackAnimation(
          AttackType.SLASH,
          400,
          60,
          -60,
          1.0),

      // Third attack: forward thrust
      new AttackAnimation(
          AttackType.THRUST,
          242,
          0,
          0,
          1.2));

  public static final AnimationProfile KATANA = new AnimationProfile(
      "Katana",
      // Quick diagonal slash
      new AttackAnimation(
          AttackType.SLASH,
          260,
          -45,
          45,
          1.1),

      // Quick reverse slash
      new AttackAnimation(
          AttackType.SLASH,
          260,
          45,
          -45,
          1.1),

      // Wide finishing slash
      new AttackAnimation(
          AttackType.SLASH,
          380,
          -80,
          80,
          1.2));
}
