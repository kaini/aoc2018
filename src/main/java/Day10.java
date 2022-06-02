import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class Day10 {
    public static final Pattern PATTERN = Pattern.compile("position=<\\s*([0-9-]+)\\s*,\\s*([0-9-]+)\\s*> velocity=<\\s*([0-9-]+)\\s*,\\s*([0-9-]+)\\s*>");

    public static void main(String[] args) throws IOException {
        List<Light> lights;
        try (BufferedReader in = new BufferedReader(new FileReader("day10.txt"))) {
            lights = in.lines().map(line -> {
                var match = PATTERN.matcher(line);
                if (!match.matches()) {
                    throw new IllegalArgumentException(line);
                }
                return new Light(parseInt(match.group(1)), parseInt(match.group(2)), parseInt(match.group(3)), parseInt(match.group(4)));
            }).toList();
        }

        int second = 0;
        out: while (true) {
            second += 1;
            lights = lights.stream().map(Light::move).toList();
            var positions = lights.stream().map(Light::position).collect(Collectors.toSet());

            // Heuristic to find the message: test if it contains a 8 high vertical line
            for (var position : positions) {
                if (IntStream.range(position.y(), position.y() + 8).allMatch(y -> positions.contains(new LightPosition(position.x(), y)))) {
                    var xStats = positions.stream().mapToInt(LightPosition::x).summaryStatistics();
                    var yStats = positions.stream().mapToInt(LightPosition::y).summaryStatistics();
                    for (int y = yStats.getMin(); y <= yStats.getMax(); ++y) {
                        for (int x = xStats.getMin(); x <= xStats.getMax(); ++x) {
                            System.out.print(positions.contains(new LightPosition(x, y)) ? "#" : " ");
                        }
                        System.out.println();
                    }
                    break out;
                }
            }
        }
        System.out.println(second);
    }


    private record Light(int x, int y, int dx, int dy) {
        public Light move() {
            return new Light(x + dx, y + dy, dx, dy);
        }

        public LightPosition position() {
            return new LightPosition(x, y);
        }
    }

    private record LightPosition(int x, int y) {}
}
