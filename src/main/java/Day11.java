public class Day11 {
    public static final int SERIAL_NUMBER = 1308;

    public static void main(String[] args) {
        var grid = new int[300][300];
        for (int x = 0; x < grid.length; ++x) {
            for (int y = 0; y < grid[x].length; ++y) {
                var result = powerLevel(x, y);
                if (x > 0) {
                    result += grid[x - 1][y];
                }
                if (y > 0) {
                    result += grid[x][y - 1];
                }
                if (x > 0 && y > 0) {
                    result -= grid[x - 1][y - 1];
                }
                grid[x][y] = result;
            }
        }

        var result = findMax(grid, 3);
        System.out.printf("%d,%d\n", result.x() + 1, result.y() + 1);

        XYPower maxResult = new XYPower(-1, -1, Integer.MIN_VALUE);
        int maxSize = Integer.MIN_VALUE;
        for (int size = 1; size <= 300; ++size) {
            var thisResult = findMax(grid, size);
            if (thisResult.power() > maxResult.power()) {
                maxResult = thisResult;
                maxSize = size;
            }
        }
        System.out.printf("%d,%d,%d\n", maxResult.x() + 1, maxResult.y() + 1, maxSize);
    }

    private static XYPower findMax(int[][] grid, int size) {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxSum = Integer.MIN_VALUE;
        for (int x = 0; x < grid.length - size + 1; ++x) {
            for (int y = 0; y < grid[x].length - size + 1; ++y) {
                var sum = grid[x + size - 1][y + size - 1];
                if (x > 0) {
                    sum -= grid[x - 1][y + size - 1];
                }
                if (y > 0) {
                    sum -= grid[x + size - 1][y - 1];
                }
                if (x > 0 && y > 0) {
                    sum += grid[x - 1][y - 1];
                }
                if (sum > maxSum) {
                    maxX = x;
                    maxY = y;
                    maxSum = sum;
                }
            }
        }
        return new XYPower(maxX, maxY, maxSum);
    }

    private static int powerLevel(int x, int y) {
        x += 1;
        y += 1;

        var rackId = x + 10;
        var powerLevel = rackId * y;
        powerLevel += SERIAL_NUMBER;
        powerLevel *= rackId;
        powerLevel = (powerLevel / 100) % 10;
        powerLevel -= 5;
        return powerLevel;
    }
}

record XYPower(int x, int y, int power) {
}
