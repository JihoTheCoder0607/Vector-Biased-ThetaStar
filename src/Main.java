import java.awt.Point;

public class Main {
    public static void main(String[] args) {
        char[][] grid = {{'S', ' ', '*', ' ', 'G'}, {' ', ' ', ' ', ' ', ' '}};
        Point start = new Point(0, 0);
        Point goal = new Point(4, 0);
        AStar astar = new AStar(grid, start, goal);
        Node result = astar.search();
        System.out.println(result.position);
    }
}
