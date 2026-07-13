import java.awt.Point;
import java.util.*;

public class Node implements Comparable<Node> {
    double g = Double.POSITIVE_INFINITY;
    double h = 0;
    double f = Double.POSITIVE_INFINITY;
    public Point position;
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