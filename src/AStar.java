import java.awt.Point;
import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.atan2;
import static java.lang.Math.abs;

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
                        openList.remove(neighborNode);
                        
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
    public void metrics(ArrayList<Node> path) {
        // 1. Distance
        double distance=0;
        for (Node p : path) {
            if (p.parent != null) {
                distance += calculateDistance(p.position, p.parent.position);
            }
        }
        System.out.println(distance);

        // 2. Angle
        Node p1, p2, p3;
        double angle1;
        double angle2;
        double angleSum = 0;
        for (int i = 0; i <= path.size()-3; i++) {
            p1 = path.get(i);
            p2 = path.get(i+1);
            p3 = path.get(i+2);

            angle1 = atan2(p2.position.y-p1.position.y, p2.position.x-p1.position.x) * 180 / Math.PI;
            angle2 = atan2(p3.position.y-p2.position.y, p3.position.x-p2.position.x) * 180 / Math.PI;

            double diff = angle2 - angle1;
            while (diff <= -180) diff += 360;
            while (diff > 180)  diff -= 360;
            angleSum += abs(diff);
        }
        System.out.println(angleSum/(path.size()-2));
    }
}