import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day6 {
    private static final int UNINITIALIZED = -1;
    private static final int MULTIPLE = -2;
    private static final int DISTANCE = 10000;

    public static void main(String[] args) throws IOException {
        List<Point> points;
        try (BufferedReader in = new BufferedReader(new FileReader("day6.txt"))) {
            points = in.lines().map(line -> {
                String[] parts = line.split(", ");
                return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }).toList();
        }

        var xStats = points.stream().mapToInt(Point::x).summaryStatistics();
        var yStats = points.stream().mapToInt(Point::y).summaryStatistics();
        var minX = xStats.getMin();
        var maxX = xStats.getMax();
        var minY = yStats.getMin();
        var maxY = yStats.getMax();

        int[][] grid = new int[maxX - minX + 1][maxY - minY + 1];
        Arrays.stream(grid).forEach(row -> Arrays.fill(row, -1));
        for (int i = 0; i < points.size(); ++i) {
            var point = points.get(i);
            grid[point.x() - minX][point.y() - minY] = i;
        }

        var didChange = true;
        var sizes = new double[points.size()];
        Arrays.fill(sizes, 1);
        while (didChange) {
            var newGrid = new int[maxX - minX + 1][maxY - minY + 1];
            didChange = false;

            for (int x = 0; x < grid.length; ++x) {
                for (int y = 0; y < grid[x].length; ++y) {
                    if (grid[x][y] == UNINITIALIZED) {
                        var neighbors = new int[]{UNINITIALIZED, UNINITIALIZED, UNINITIALIZED, UNINITIALIZED};
                        if (x > 0) neighbors[0] = grid[x - 1][y];
                        if (x < grid.length - 1) neighbors[1] = grid[x + 1][y];
                        if (y > 0) neighbors[2] = grid[x][y - 1];
                        if (y < grid[x].length - 1) neighbors[3] = grid[x][y + 1];

                        int result = UNINITIALIZED;
                        for (var n : neighbors) {
                            if (n != UNINITIALIZED) {
                                if (result == UNINITIALIZED) {
                                    result = n;
                                } else if (result != n) {
                                    result = MULTIPLE;
                                }
                            }
                        }

                        newGrid[x][y] = result;

                        if (result != UNINITIALIZED) {
                            didChange = true;
                            if (result != MULTIPLE) {
                                if (x == 0 || x == grid.length - 1 || y == 0 || y == grid[x].length - 1) {
                                    sizes[result] = Double.POSITIVE_INFINITY;
                                } else {
                                    sizes[result] += 1;
                                }
                            }
                        }
                    } else {
                        newGrid[x][y] = grid[x][y];
                    }
                }
            }

            grid = newGrid;
        }

        System.out.println((int) Arrays.stream(sizes).filter(Double::isFinite).max().orElse(0));

        var count = 0;
        // Strictly speaking this is not correct: The safe area might extend over the edges of the map ...
        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                int distance = 0;
                for (var p : points) {
                    distance += Math.abs(p.x() - x) + Math.abs(p.y() - y);
                }
                if (distance < DISTANCE) {
                    count += 1;
                }
            }
        }
        System.out.println(count);
    }

    private record Point(int x, int y) {
    }
}
