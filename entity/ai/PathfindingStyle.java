
package entity.ai;

/**
 * Defines how a monster navigates the world.
 */
public enum PathfindingStyle {

    // Moves directly toward its target
    DIRECT,

    // Navigates around obstacles using A* pathfinding
    ASTAR,

    // Follows a predefined patrol route
    PATROL,

    // Does not move
    STATIONARY
}
