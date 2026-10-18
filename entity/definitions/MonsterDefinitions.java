
package entity.definitions;

import entity.ai.PathfindingStyle;

/**
 * Central registry of monster types.
 *
 * Each definition is shared by all monsters of that type.
 */
public final class MonsterDefinitions {

    private MonsterDefinitions() {
        // Prevent creating instances of this utility class
    }

    public static final MonsterDefinition ZOMBIE = new MonsterDefinition(
            "Zombie",
            50, // Maximum health
            10, // Attack damage
            0.20, // 20% knockback resistance
            1.2, // Movement speed
            5.0, // Detection range (tiles)
            0.8, // Attack range (tiles)
            PathfindingStyle.ASTAR, // Navigation style
            "zombie" // Sprite path
    );

    public static final MonsterDefinition SKELETON = new MonsterDefinition(
            "Skeleton",
            30,
            8,
            0.10,
            2.1,
            7.0,
            0.8,
            PathfindingStyle.ASTAR,
            "skeleton");

    public static final MonsterDefinition SPIDER = new MonsterDefinition(
            "Spider",
            25,
            6,
            0.05,
            4.0,
            6.0,
            0.8,
            PathfindingStyle.DIRECT,
            "spider");
}
