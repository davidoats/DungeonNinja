
package combat;

//Defines the animation properties of a single weapon attack.

public class AttackAnimation {

  public final AttackType type;

  public final int duration;
  public final double startAngle;
  public final double endAngle;
  public final double reach;

  /**
   * Creates an attack animation.
   *
   * @param type       Type of attack
   * @param duration   Attack duration in milliseconds
   * @param startAngle Starting angle relative to aim, in degrees
   * @param endAngle   Ending angle relative to aim, in degrees
   * @param reach      Weapon reach multiplier
   */

  public AttackAnimation(AttackType type, int duration, double startAngle, double endAngle, double reach) {

    if (type == null || duration <= 0 || reach <= 0) {
      throw new IllegalArgumentException("Invalid attack animation");
    }

    this.type = type;
    this.duration = duration;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    this.reach = reach;
  }
}
