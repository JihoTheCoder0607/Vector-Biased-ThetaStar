import java.awt.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;
import static java.lang.Math.pow;

class Node implements Comparable<Node> {
    double f=0;
    double g=0;
    double h=0;
    Point position;
    Node parent;

    public Node(Point position, Node parent) {
        this.position = position;
        this.parent = parent;
    }
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
    int[][] grid;
    Point start;
    Point goal;

    public AStar(int[][] grid, Point start, Point goal) {
        this.grid = grid;
        this.start = start;
        this.goal = goal;
    }

    public void initialize() {
        Node startNode = new Node(start);
        startNode.g = 0;
        startNode.h = calculateDistance(start, goal);
        startNode.f = startNode.g + startNode.h;

        openList.add(startNode);
    }

    public double calculateDistance(Point a, Point b) {
        return pow(pow((b.x - a.x), 2) + pow((b.y-a.y), 2), 0.5);
    }

    ArrayList<Node> getNeighbors() {

    }

}