import java.awt.*;

import static java.lang.Math.*;

public class ThetaStar extends AStar {
    public ThetaStar(char[][] grid, Point start, Point goal) {
        super(grid, start, goal);
    }
    private boolean lineOfSight(Node node1, Node node2) {
        int x1 = node1.position.x;
        int y1 = node1.position.y;
        int x2 = node2.position.x;
        int y2 = node2.position.y;

        int dx = abs(x2 - x1);
        int dy = abs(y2 - y1);

        int sx = (x1 < x2) ? (1) : (-1);
        int sy = (y1 < y2) ? (1) : (-1);

        int err = dx - dy;

        while (true) {
            if (grid[y1][x1] == '#') {
                return false;
            }
            if (x1 == x2 && y1 == y2) {
                return true;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }

    }

    private double crossTrack(Node node) {
        double dx = goal.x - start.x;
        double dy = goal.y - start.y;

        double numerator = abs(dy * node.position.x - dx * node.position.y + goal.x * start.y - goal.y * start.x);
        double denominator = sqrt(pow(dx, 2) + pow(dy, 2));

        return numerator / denominator;
    }

    @Override
    void getNeighbors(Node currentNode) {
        int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        for (int[] direction : directions) {
            int dy = direction[0];
            int dx = direction[1];

            int newX = currentNode.position.x + dx;
            int newY = currentNode.position.y + dy;

            Point neighbor = new Point(newX, newY);
            if (0 <= newX && newX < grid[0].length && 0 <= newY && newY < grid.length) {
                if (closedSet.contains(neighbor)){
                    continue;
                }
                if (grid[newY][newX] == ' ' || grid[newY][newX] == 'G') {
                    double tentativeG = currentNode.g + sqrt(dy * dy + dx * dx);

                    Node neighborNode;
                    if (!nodes.containsKey(neighbor)) {
                        neighborNode = new Node(neighbor);
                        nodes.put(neighbor, neighborNode);
                    }
                    else {
                        neighborNode = nodes.get(neighbor);
                    }

                    Node bestParent = currentNode;

                    if (currentNode.parent != null && lineOfSight(currentNode.parent, neighborNode)) {
                        bestParent = currentNode.parent;
                        tentativeG = currentNode.parent.g + calculateDistance(currentNode.parent.position, neighbor);
                    }

                    if (tentativeG < neighborNode.g) {
                        neighborNode.parent = bestParent;
                        neighborNode.g = tentativeG;
                        neighborNode.h = calculateDistance(neighbor, goal);
                        neighborNode.f = neighborNode.g + neighborNode.h;

                        openList.add(neighborNode);
                    }
                }
            }
        }
    }

}
