import java.awt.Point;
import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStar {
    PriorityQueue<Node> openList = new PriorityQueue<>();
    HashSet<Point> closedSet = new HashSet<>();
    HashMap<Point, Node> nodes = new HashMap<>();
    char[][] grid;
    Point start;
    Point goal;

    public AStar(char[][] grid, Point start, Point goal) {
        this.grid = grid;
        this.start = start;
        this.goal = goal;
    }

    public void initialize() {
        Node startNode = new Node(start);
        startNode.g = 0;
        startNode.h = calculateDistance(start, goal);
        startNode.f = startNode.g + startNode.h;
        startNode.position = start;

        openList.add(startNode);
        nodes.put(start, startNode);
    }

    public double calculateDistance(Point a, Point b) {
        return pow(pow((b.x - a.x), 2) + pow((b.y-a.y), 2), 0.5);
    }

    void getNeighbors(Node currentNode) {
        int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        for (int[] direction : directions) {
            int dy = direction[0];
            int dx = direction[1];

            int newX = currentNode.position.x + dx;
            int newY = currentNode.position.y + dy;

            Point neighbor = new Point(newX, newY);
            if (0 <= newX && newX < grid[0].length && 0 <= newY && newY < grid.length) {
                if (dx != 0 && dy != 0) {
                    if (grid[currentNode.position.y][newX] == '#' || grid[newY][currentNode.position.x] == '#') {
                        continue;
                    }
                }
                if (closedSet.contains(neighbor)){
                    continue;
                }
                if (grid[newY][newX] == ' ' || grid[newY][newX] == 'E') {
                    double tentativeG = currentNode.g + sqrt(dy * dy + dx * dx);

                    Node neighborNode;
                    if (!nodes.containsKey(neighbor)) {
                        neighborNode = new Node(neighbor);
                        nodes.put(neighbor, neighborNode);
                    }
                    else {
                        neighborNode = nodes.get(neighbor);
                    }

                    if (tentativeG < neighborNode.g) {
                        neighborNode.parent = currentNode;
                        neighborNode.g = tentativeG;
                        neighborNode.h = calculateDistance(neighbor, goal);
                        neighborNode.f = neighborNode.g + neighborNode.h;

                        openList.add(neighborNode);
                    }
                }
            }
        }
    }

    public Node search() {
        this.initialize();
        while (!openList.isEmpty()) {

            Node current = openList.poll();

            if (closedSet.contains(current.position)) {
                continue;
            }

            if (current.position.equals(goal)) {
                return current;
            }

            closedSet.add(current.position);

            this.getNeighbors(current);
        }
        return null;
    }
    public ArrayList<Node> reconstructPath(Node goalNode) {
        ArrayList<Node> path = new ArrayList<>();
        Node current = goalNode;

        while (current != null) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }
}