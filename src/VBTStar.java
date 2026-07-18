import java.awt.*;

import static java.lang.Math.*;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class VBTStar extends ThetaStar {
    public VBTStar(char[][] grid, Point start, Point goal) {
        super(grid, start, goal);
    }

    private double crossTrack(Node node) {
        double dx = goal.x - start.x;
        double dy = goal.y - start.y;

        double numerator = abs(dy * node.position.x - dx * node.position.y + goal.x * start.y - goal.y * start.x);
        double denominator = sqrt(pow(dx, 2) + pow(dy, 2));

        return numerator / denominator / calculateDistance(this.start, this.goal);
    }

    private double angle(Node parent, Node node) {
        double angle1 = atan2(goal.y - start.y, goal.x - start.x);
        double angle2 = atan2(node.position.y - parent.position.y, node.position.x - parent.position.x);

        double diff = Math.abs(angle1 - angle2);

        if (diff > Math.PI) {
            diff = (2 * Math.PI) - diff;
        }

        return diff / Math.PI;
    }

    @Override
    protected double getPenalty(Node parent, Node node) {
        return crossTrack(node) + angle(parent, node);
    }
}
