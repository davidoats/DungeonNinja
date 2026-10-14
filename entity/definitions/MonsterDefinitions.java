
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
            2.0, // Movement speed
            5.0, // Detection range (tiles)
            0.8, // Attack range (tiles)
            PathfindingStyle.ASTAR, // Navigation style
            "/res/monsters/zombie.png" // Sprite path
    );

    public static final MonsterDefinition SKELETON = new MonsterDefinition(
            "Skeleton",
            30,
            8,
            2.5,
            7.0,
            5.0,
            PathfindingStyle.ASTAR,
            "/res/monsters/skeleton.png");

    public static final MonsterDefinition SPIDER = new MonsterDefinition(
            "Spider",
            25,
            6,
            4.0,
            6.0,
            0.8,
            PathfindingStyle.DIRECT,
            "/res/monsters/spider.png");
}
