import java.awt.Point;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        char[][] grid = {{'S', ' ', '*', ' ', 'G'}, {' ', ' ', ' ', ' ', ' '}};
        Point start = new Point(0, 0);
        Point goal = new Point(4, 0);
        AStar astar = new AStar(grid, start, goal);
        Node result = astar.search();
        ArrayList<Node> path = astar.reconstructPath(result);
        for (Node node : path) {
            grid[node.position.y][node.position.x] = 'P';
        }

        for (char[] row : grid) {
            for (char element : row) {
                System.out.print(element);
            }
            System.out.println();
        }
    }
}
