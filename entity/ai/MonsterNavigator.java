package entity.ai;

import entity.Monster;
import entity.ai.pathfinding.AStarPathfinder;
import entity.ai.pathfinding.NavigationGrid;
import main.GamePanel;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

/**
 * Selects a monster's desired movement toward a target.
 * The Monster class remains responsible for actual movement and collision.
 * Supports direct movement, A* navigation, and stuck recovery.
 */
public class MonsterNavigator {

    // A* path state
    private final AStarPathfinder pathfinder = new AStarPathfinder();
    private List<Point> currentPath = Collections.emptyList();
    private int pathIndex = 0;
    private int lastGoalCol = -1;
    private int lastGoalRow = -1;
    private boolean pathBlocked = false;
    private long lastPathTime = 0;
    private static final long PATH_UPDATE_INTERVAL = 400;

    // Stuck detection (measured in navigation updates and world pixels)
    private static final int STUCK_CHECK_INTERVAL = 30;
    private static final double MINIMUM_MOVEMENT = 4.0;
    private double lastCheckX;
    private double lastCheckY;
    private int stuckCheckFrames = 0;
    private boolean stuckCheckInitialized = false;
    private boolean stuck = false;

    // Temporary recovery navigation
    private static final int RECOVERY_TIMEOUT = 180;
    private static final int RECOVERY_SEARCH_RADIUS = 4;
    private boolean recovering = false;
    private Point recoveryTarget = null;
    private int recoveryFrames = 0;

    /** Reports whether a monster's attempted movement was blocked. */
    public void reportMovementResult(boolean moved) {
        if (!moved) {
            pathBlocked = true;
        }
    }

    /** Returns four-direction movement toward the target, without pathfinding. */
    public double[] getDirectMovement(Monster monster, double targetX, double targetY) {
        double dx = targetX - monster.WorldX;
        double dy = targetY - monster.WorldY;
        double speed = monster.getDefinition().getSpeed();

        double moveX = 0;
        double moveY = 0;

        if (Math.abs(dx) > Math.abs(dy)) {
            moveX = Math.signum(dx) * Math.min(speed, Math.abs(dx));
        } else {
            moveY = Math.signum(dy) * Math.min(speed, Math.abs(dy));
        }

        return new double[] { moveX, moveY };
    }

    /**
     * Follows an A* route, completing each waypoint before changing routes
     * unless movement has been blocked long enough to trigger recalculation.
     */
    public double[] getAStarMovement(Monster monster, double targetX, double targetY) {
        int tileSize = GamePanel.tileSize;
        double speed = monster.getDefinition().getSpeed();

        int startCol = (int) Math.floor(monster.WorldX / tileSize);
        int startRow = (int) Math.floor(monster.WorldY / tileSize);
        int goalCol = (int) Math.floor(targetX / tileSize);
        int goalRow = (int) Math.floor(targetY / tileSize);

        long now = System.currentTimeMillis();
        boolean goalChanged = goalCol != lastGoalCol || goalRow != lastGoalRow;
        boolean pathFinished = pathIndex >= currentPath.size();

        // Advance past reached waypoints.
        while (!pathFinished) {
            Point waypoint = currentPath.get(pathIndex);
            double waypointX = waypoint.x * tileSize;
            double waypointY = waypoint.y * tileSize;

            if (Math.abs(monster.WorldX - waypointX) > 0.5 ||
                    Math.abs(monster.WorldY - waypointY) > 0.5) {
                break;
            }

            pathIndex++;
            pathFinished = pathIndex >= currentPath.size();
        }

        boolean canRecalculate = pathFinished ||
                currentPath.isEmpty() ||
                (pathBlocked && now - lastPathTime >= PATH_UPDATE_INTERVAL);

        if (canRecalculate && (goalChanged || pathFinished || pathBlocked)) {
            NavigationGrid grid = new NavigationGrid(monster.getGamePanel(), monster);
            currentPath = pathfinder.findPath(
                    startCol, startRow, goalCol, goalRow,
                    monster.getGamePanel().maxWorldCol,
                    monster.getGamePanel().maxWorldRow,
                    grid, grid::canTravel);

            pathIndex = 0;
            pathBlocked = false;
            lastPathTime = now;
            lastGoalCol = goalCol;
            lastGoalRow = goalRow;
        }

        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            return new double[] { 0, 0 };
        }

        Point waypoint = currentPath.get(pathIndex);
        double dx = waypoint.x * tileSize - monster.WorldX;
        double dy = waypoint.y * tileSize - monster.WorldY;

        double moveX = 0;
        double moveY = 0;

        // Preserve the existing horizontal-first alignment behavior.
        if (Math.abs(dx) > 0.5) {
            moveX = Math.signum(dx) * Math.min(speed, Math.abs(dx));
        } else if (Math.abs(dy) > 0.5) {
            moveY = Math.signum(dy) * Math.min(speed, Math.abs(dy));
        }

        return new double[] { moveX, moveY };
    }

    /**
     * Selects the configured navigation style and temporarily redirects
     * stuck monsters to a recovery destination when necessary.
     */
    public double[] getMovement(Monster monster, double targetX, double targetY) {
        PathfindingStyle style = monster.getDefinition().getPathfindingStyle();

        if (style != PathfindingStyle.ASTAR && style != PathfindingStyle.DIRECT) {
            resetStuckDetection();
            if (recovering) {
                endRecovery();
            }
            return new double[] { 0, 0 };
        }

        if (recovering) {
            updateRecovery(monster);
        }

        double navigationX = targetX;
        double navigationY = targetY;
        if (recovering && recoveryTarget != null) {
            navigationX = recoveryTarget.x * GamePanel.tileSize;
            navigationY = recoveryTarget.y * GamePanel.tileSize;
        }

        double[] movement = style == PathfindingStyle.ASTAR
                ? getAStarMovement(monster, navigationX, navigationY)
                : getDirectMovement(monster, navigationX, navigationY);

        // Avoid treating expected stationary behavior during recovery as a new stall.
        if (!recovering) {
            double distanceToTarget = Math.hypot(
                    targetX - monster.WorldX, targetY - monster.WorldY);
            boolean attemptingMovement = distanceToTarget > GamePanel.tileSize;
            boolean wasStuck = stuck;
            boolean isStuck = updateStuckDetection(
                    monster.WorldX, monster.WorldY, attemptingMovement);

            if (isStuck && !wasStuck) {
                Point escapeTarget = findRecoveryTarget(monster, targetX, targetY);
                if (escapeTarget != null) {
                    beginRecovery(escapeTarget);

                    double escapeX = escapeTarget.x * GamePanel.tileSize;
                    double escapeY = escapeTarget.y * GamePanel.tileSize;
                    return style == PathfindingStyle.ASTAR
                            ? getAStarMovement(monster, escapeX, escapeY)
                            : getDirectMovement(monster, escapeX, escapeY);
                }
            }
        }

        return movement;
    }

    /** Checks actual displacement over a fixed number of navigation updates. */
    private boolean updateStuckDetection(double x, double y, boolean attemptingMovement) {
        if (!attemptingMovement) {
            resetStuckDetection();
            return false;
        }

        if (!stuckCheckInitialized) {
            lastCheckX = x;
            lastCheckY = y;
            stuckCheckInitialized = true;
            return false;
        }

        stuckCheckFrames++;
        if (stuckCheckFrames >= STUCK_CHECK_INTERVAL) {
            double distance = Math.hypot(x - lastCheckX, y - lastCheckY);
            stuck = distance < MINIMUM_MOVEMENT;
            lastCheckX = x;
            lastCheckY = y;
            stuckCheckFrames = 0;
        }

        return stuck;
    }

    private void resetStuckDetection() {
        stuckCheckFrames = 0;
        stuckCheckInitialized = false;
        stuck = false;
    }

    /** Clears cached A* state so the next request computes a fresh route. */
    private void resetPath() {
        currentPath = Collections.emptyList();
        pathIndex = 0;
        pathBlocked = false;
        lastGoalCol = -1;
        lastGoalRow = -1;
    }

    private void beginRecovery(Point target) {
        recoveryTarget = target;
        recovering = true;
        recoveryFrames = 0;
        resetStuckDetection();
        resetPath();
    }

    private void endRecovery() {
        recovering = false;
        recoveryTarget = null;
        recoveryFrames = 0;
        resetStuckDetection();
        resetPath();
    }

    /** Resumes normal pursuit after reaching the escape tile or timing out. */
    private void updateRecovery(Monster monster) {
        if (!recovering || recoveryTarget == null) {
            return;
        }

        recoveryFrames++;
        double targetX = recoveryTarget.x * GamePanel.tileSize;
        double targetY = recoveryTarget.y * GamePanel.tileSize;
        boolean reachedTarget = Math.abs(monster.WorldX - targetX) <= 0.5 &&
                Math.abs(monster.WorldY - targetY) <= 0.5;

        if (reachedTarget || recoveryFrames >= RECOVERY_TIMEOUT) {
            endRecovery();
        }
    }

    /**
     * Finds a nearby, A*-reachable escape tile, favoring progress toward the
     * player.
     */
    private Point findRecoveryTarget(Monster monster, double targetX, double targetY) {
        int tileSize = GamePanel.tileSize;
        int startCol = (int) Math.floor(monster.WorldX / tileSize);
        int startRow = (int) Math.floor(monster.WorldY / tileSize);
        int playerCol = (int) Math.floor(targetX / tileSize);
        int playerRow = (int) Math.floor(targetY / tileSize);

        NavigationGrid grid = new NavigationGrid(monster.getGamePanel(), monster);
        Point bestTarget = null;
        double bestScore = Double.POSITIVE_INFINITY;

        for (int col = startCol - RECOVERY_SEARCH_RADIUS; col <= startCol + RECOVERY_SEARCH_RADIUS; col++) {
            for (int row = startRow - RECOVERY_SEARCH_RADIUS; row <= startRow + RECOVERY_SEARCH_RADIUS; row++) {

                if (col < 0 || row < 0 ||
                        col >= monster.getGamePanel().maxWorldCol ||
                        row >= monster.getGamePanel().maxWorldRow) {
                    continue;
                }
                if (col == startCol && row == startRow) {
                    continue;
                }
                if (!grid.isWalkable(col, row)) {
                    continue;
                }

                List<Point> path = pathfinder.findPath(
                        startCol, startRow, col, row,
                        monster.getGamePanel().maxWorldCol,
                        monster.getGamePanel().maxWorldRow,
                        grid, grid::canTravel);

                if (path == null || path.isEmpty()) {
                    continue;
                }

                double playerDistance = Math.abs(col - playerCol) + Math.abs(row - playerRow);
                double score = playerDistance + path.size() * 0.25;
                if (score < bestScore) {
                    bestScore = score;
                    bestTarget = new Point(col, row);
                }
            }
        }

        return bestTarget;
    }
}
