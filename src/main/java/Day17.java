import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Day17 {
    private static final Pattern LINE = Pattern.compile("^(.)=(\\d+), .=(\\d+)\\.\\.(\\d+)$");

    public static void main(String... args) throws IOException {
        var field = new HashMap<XY, Character>();
        try (var in = new BufferedReader(new FileReader("day17.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                var match = LINE.matcher(line);
                if (match.matches()) {
                    if (match.group(1).equals("x")) {
                        int x = Integer.parseInt(match.group(2));
                        int yStart = Integer.parseInt(match.group(3));
                        int yEnd = Integer.parseInt(match.group(4));
                        for (int y = yStart; y <= yEnd; ++y) {
                            field.put(new XY(x, y), '#');
                        }
                    } else {
                        int y = Integer.parseInt(match.group(2));
                        int xStart = Integer.parseInt(match.group(3));
                        int xEnd = Integer.parseInt(match.group(4));
                        for (int x = xStart; x <= xEnd; ++x) {
                            field.put(new XY(x, y), '#');
                        }
                    }
                } else {
                    throw new IOException("Invalid input");
                }
            }
        }

        int minY = field.keySet().stream().mapToInt(XY::y).min().orElseThrow();
        int maxY = field.keySet().stream().mapToInt(XY::y).max().orElseThrow();

        field.put(new XY(500, 0), '+');
        dropWater(field, 500, 0, maxY);
        System.out.println(field.entrySet().stream().filter(c -> c.getKey().y() >= minY && (c.getValue() == '~' || c.getValue() == '|')).count());
        System.out.println(field.values().stream().filter(c -> c == '~').count());
    }

    private static void dropWater(Map<XY, Character> field, int x, int y, int maxY) {
        while (y + 1 <= maxY && field.get(new XY(x, y + 1)) == null) {
            y += 1;
            field.put(new XY(x, y), '|');
        }

        if (Objects.equals(field.get(new XY(x, y + 1)), '#')) {
            fillWater(field, x, y, maxY);
        } else if (Objects.equals(field.get(new XY(x, y + 1)), '~')) {
            fillWater(field, x, y, maxY);
        }
    }

    private static void fillWater(Map<XY, Character> field, int x, int y, int maxY) {
        int startX = x;
        boolean didDrop = false;
        field.put(new XY(x, y), '~');

        while (!(Objects.equals(field.get(new XY(x - 1, y)), '#'))
                && (Objects.equals(field.get(new XY(x - 1, y + 1)), '#') ||
                    Objects.equals(field.get(new XY(x - 1, y + 1)), '~'))) {
            x -= 1;
            field.put(new XY(x, y), '~');
        }
        if (!Objects.equals(field.get(new XY(x - 1, y)), '#')) {
            didDrop = true;
            field.put(new XY(x - 1, y), '|');
            dropWater(field, x - 1, y, maxY);
        }
        int minX = x;

        x = startX;
        while (!Objects.equals(field.get(new XY(x + 1, y)), '#')
                && (Objects.equals(field.get(new XY(x + 1, y + 1)), '#') ||
                    Objects.equals(field.get(new XY(x + 1, y + 1)), '~'))) {
            x += 1;
            field.put(new XY(x, y), '~');
        }
        if (!Objects.equals(field.get(new XY(x + 1, y)), '#')) {
            didDrop = true;
            field.put(new XY(x + 1, y), '|');
            dropWater(field, x + 1, y, maxY);
        }
        int maxX = x;

        if (didDrop) {
            for (int xx = minX; xx <= maxX; ++xx) {
                if (!Objects.equals(field.get(new XY(xx, y - 1)), '~')) {
                    field.put(new XY(xx, y), '|');
                }
            }
        } else {
            fillWater(field, startX, y - 1, maxY);
        }
    }

    private record XY(int x, int y) {}
}


