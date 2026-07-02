import java.awt.Point;
import java.util.PriorityQueue;

class Node implements Comparable<Node> {
    int f=0;
    int g=0;
    int h=0;
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
            return Integer.compare(this.h, other.h);
        }
        return Integer.compare(this.f, other.f);
    }
}

class AStar {
    PriorityQueue<Node> openList = new PriorityQueue<>();
    int[][] grid;

    public AStar(int[][] grid) {
        this.grid = grid;
    }


}