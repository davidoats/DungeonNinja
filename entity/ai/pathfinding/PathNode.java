
package entity.ai.pathfinding;

/**
 * Represents one tile in an A* pathfinding search.
 */
public class PathNode implements Comparable<PathNode> {

  public final int col;
  public final int row;

  // Cost from starting tile
  public double gCost = Double.POSITIVE_INFINITY;

  // Estimated cost to destination
  public double hCost;

  // Previous node in the path
  public PathNode parent;

  public PathNode(int col, int row) {
    this.col = col;
    this.row = row;
  }

  /**
   * Total estimated cost of a path through this node.
   */
  public double getFCost() {
    return gCost + hCost;
  }

  /**
   * Allows nodes to be ordered in a priority queue.
   */
  @Override
  public int compareTo(PathNode other) {

    int result = Double.compare(
        getFCost(),
        other.getFCost());

    if (result == 0) {
      result = Double.compare(hCost, other.hCost);
    }

    return result;
  }
}
