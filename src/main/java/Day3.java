import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 {
    private static final Pattern CLAIM_RE = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");

    public static void main(String[] args) throws IOException {
        List<Claim> claims;
        try (BufferedReader in = new BufferedReader(new FileReader("day3.txt"))) {
            claims = in.lines().map(line -> {
                Matcher matcher = CLAIM_RE.matcher(line);
                if (matcher.matches()) {
                    return new Claim(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4)),
                            Integer.parseInt(matcher.group(5)));
                } else {
                    throw new RuntimeException("Regex does not match!");
                }
            }).toList();
        }

        int[] fabric = new int[1000 * 1000];
        for (Claim claim : claims) {
            for (int x = claim.left(); x < claim.left() + claim.width(); ++x) {
                for (int y = claim.top(); y < claim.top() + claim.height(); ++y) {
                    fabric[index(x, y)] += 1;
                }
            }
        }
        System.out.println(Arrays.stream(fabric).filter(count -> count > 1).count());

        for (Claim claim : claims) {
            int sum = 0;
            for (int x = claim.left(); x < claim.left() + claim.width(); ++x) {
                for (int y = claim.top(); y < claim.top() + claim.height(); ++y) {
                    sum += fabric[index(x, y)] - 1;
                }
            }
            if (sum == 0) {
                System.out.println(claim.id());
                break;
            }
        }
    }

    public static int index(int left, int top) {
        return left * 1000 + top;
    }
}

record Claim(
        int id,
        int left,
        int top,
        int width,
        int height
) {
}
