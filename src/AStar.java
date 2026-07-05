import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import static java.lang.Math.pow;

class Node implements Comparable<Node> {
    double g = Double.POSITIVE_INFINITY;
    double h = 0;
    double f = Double.POSITIVE_INFINITY;
    Point position;
    Node parent;

    public Node(Point position) {
        this.position = position;
        this.parent = null;
    }

    @Override
    public int compareTo(Node other) {
        if (this.f == other.f) {
            return Double.compare(this.h, other.h);
        }
        return Double.compare(this.f, other.f);
    }
}

class AStar {
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
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
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
                    double tentativeG = currentNode.g + 1;

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

    Node search() {
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
}