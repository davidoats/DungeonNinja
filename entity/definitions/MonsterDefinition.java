
package entity.definitions;

import entity.ai.PathfindingStyle;

/**
 * Stores the configurable attributes of a monster type.
 */
public class MonsterDefinition {

    private final String name;
    private final int maxHealth;
    private final int attackDamage;
    private final double speed;
    private final double detectionRange;
    private final double attackRange;
    private final PathfindingStyle pathfindingStyle;
    private final String spritePath;

    /**
     * Creates a monster definition.
     *
     * @param name             Monster's display name
     * @param maxHealth        Maximum health
     * @param attackDamage     Damage dealt by an attack
     * @param speed            Movement speed in pixels per update
     * @param detectionRange   Detection range in tiles
     * @param attackRange      Attack range in tiles
     * @param pathfindingStyle Navigation behavior
     * @param spritePath       Path to the monster's sprite
     */
    public MonsterDefinition(
            String name,
            int maxHealth,
            int attackDamage,
            double speed,
            double detectionRange,
            double attackRange,
            PathfindingStyle pathfindingStyle,
            String spritePath) {

        this.name = name;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.speed = speed;
        this.detectionRange = detectionRange;
        this.attackRange = attackRange;
        this.pathfindingStyle = pathfindingStyle;
        this.spritePath = spritePath;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDetectionRange() {
        return detectionRange;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public PathfindingStyle getPathfindingStyle() {
        return pathfindingStyle;
    }

    public String getSpritePath() {
        return spritePath;
    }
}
