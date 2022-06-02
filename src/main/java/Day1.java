import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day1 {
    public static void main(String[] args) throws IOException {
        int[] changes;
        try (BufferedReader in = new BufferedReader(new FileReader("day1.txt"))) {
            changes = in.lines().mapToInt(Integer::parseInt).toArray();
        }

        System.out.println(Arrays.stream(changes).sum());

        Set<Integer> seen = new HashSet<>();
        int freq = 0;
        for (int i = 0; ; ++i) {
            if (seen.contains(freq)) {
                System.out.println(freq);
                break;
            }
            seen.add(freq);
            freq += changes[i % changes.length];
        }
    }
}
