
package combat;

/**
 * Stores a reusable sequence of weapon attacks.
 */
public class AnimationProfile {

  private final String name;
  private final AttackAnimation[] attacks;

  /**
   * Creates an animation profile containing one or more attacks.
   *
   * @param name    Name of the animation profile
   * @param attacks Attacks in combo order
   */
  public AnimationProfile(String name, AttackAnimation... attacks) {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Profile needs a name");
    }

    if (attacks == null || attacks.length == 0) {
      throw new IllegalArgumentException(
          "Profile needs at least one attack");
    }

    for (AttackAnimation attack : attacks) {
      if (attack == null) {
        throw new IllegalArgumentException(
            "Attacks cannot be null");
      }
    }

    this.name = name;
    this.attacks = attacks.clone();
  }

  public String getName() {
    return name;
  }

  public int getComboLength() {
    return attacks.length;
  }

  public AttackAnimation getAttack(int index) {
    return attacks[index];
  }
}
