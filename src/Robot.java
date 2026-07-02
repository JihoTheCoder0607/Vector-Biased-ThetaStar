import java.awt.Point;


class Node {
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
}

public class Robot {

}

