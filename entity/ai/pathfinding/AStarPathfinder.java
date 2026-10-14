
package entity.ai.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Reusable A* pathfinding algorithm for a four-direction grid.
 */
public class AStarPathfinder {

    /**
     * Describes which tiles can be entered.
     */
    @FunctionalInterface
    public interface Walkable {
        boolean isWalkable(int col, int row);
    }


/**
 * Checks whether movement between two adjacent tiles
 * is physically possible.
 */
@FunctionalInterface
public interface Traversable {
    boolean canTravel(
        int fromCol,
        int fromRow,
        int toCol,
        int toRow
    );
}


    /**
     * Finds a path from start to goal.
     *
     * @return Ordered tile coordinates, excluding the starting
     *         tile and including the destination.
     *         Returns an empty list if no path exists.
     */
    public List<Point> findPath(
        int startCol,
        int startRow,
        int goalCol,
        int goalRow,
        int mapCols,
        int mapRows,
        Walkable walkable,
        Traversable traversable) {

        List<Point> emptyPath = Collections.emptyList();

        // Reject invalid coordinates
        if (!inBounds(startCol, startRow, mapCols, mapRows)
                || !inBounds(goalCol, goalRow, mapCols, mapRows)) {
            return emptyPath;
        }

        if (startCol == goalCol && startRow == goalRow) {
            return emptyPath;
        }

        if (!walkable.isWalkable(goalCol, goalRow)) {
            return emptyPath;
        }

        

        PathNode[][] nodes = new PathNode[mapCols][mapRows];

        for (int col = 0; col < mapCols; col++) {
            for (int row = 0; row < mapRows; row++) {
                nodes[col][row] = new PathNode(col, row);
            }
        }

        PriorityQueue<PathNode> open = new PriorityQueue<>();
        Set<PathNode> closed = new HashSet<>();

        PathNode start = nodes[startCol][startRow];
        start.gCost = 0;
        start.hCost = heuristic(
            startCol, startRow, goalCol, goalRow
        );

        open.add(start);

        // Only cardinal directions: right, left, down, up
        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };

        while (!open.isEmpty()) {

            PathNode current = open.poll();

            if (closed.contains(current)) {
                continue;
            }

            if (current.col == goalCol
                    && current.row == goalRow) {
                return reconstructPath(current);
            }

            closed.add(current);

            for (int[] direction : directions) {

                int nextCol = current.col + direction[0];
                int nextRow = current.row + direction[1];

                if (!inBounds(
                        nextCol, nextRow, mapCols, mapRows)) {
                    continue;
                }

                if (!walkable.isWalkable(nextCol, nextRow)) {
                    continue;
                }

                // Reject routes that the monster cannot physically traverse
if (!traversable.canTravel(
        current.col,
        current.row,
        nextCol,
        nextRow)) {
    continue;
}

                PathNode next = nodes[nextCol][nextRow];

                if (closed.contains(next)) {
                    continue;
                }

                double newGCost = current.gCost + 1;

                if (newGCost < next.gCost) {

                    next.gCost = newGCost;
                    next.hCost = heuristic(
                        nextCol, nextRow, goalCol, goalRow
                    );
                    next.parent = current;

                    // Reinsert after cost changes so the
                    // priority queue remains correctly ordered.
                    open.remove(next);
                    open.add(next);
                }
            }
        }

        return emptyPath;
    }

    /**
     * Manhattan distance for four-direction movement.
     */
    private int heuristic(
            int col, int row, int goalCol, int goalRow) {

        return Math.abs(goalCol - col)
             + Math.abs(goalRow - row);
    }

    private boolean inBounds(
            int col, int row, int mapCols, int mapRows) {

        return col >= 0 && col < mapCols
            && row >= 0 && row < mapRows;
    }

    /**
     * Reconstructs the path from destination to start,
     * then reverses it into movement order.
     */
    private List<Point> reconstructPath(PathNode goal) {

        List<Point> path = new ArrayList<>();

        PathNode current = goal;

        while (current.parent != null) {
            path.add(new Point(current.col, current.row));
            current = current.parent;
        }

        Collections.reverse(path);

        return path;
    }
}
