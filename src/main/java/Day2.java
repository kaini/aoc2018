import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day2 {
    public static void main(String[] args) throws IOException {
        List<String> ids;
        try (BufferedReader in = new BufferedReader(new FileReader("day2.txt"))) {
            ids = in.lines().toList();
        }

        int twos = 0;
        int threes = 0;
        for (String id : ids) {
            Map<Integer, Integer> counts = new HashMap<>();
            id.chars().forEach(chr -> counts.merge(chr, 1, Integer::sum));
            if (counts.containsValue(2)) twos += 1;
            if (counts.containsValue(3)) threes += 1;
        }
        System.out.println(twos * threes);

        out:
        for (String idA : ids) {
            for (String idB : ids) {
                int differenceIndex = findSingleDifference(idA, idB);
                if (differenceIndex != -1) {
                    System.out.println(idA.substring(0, differenceIndex) + idA.substring(differenceIndex + 1));
                    break out;
                }
            }
        }
    }

    private static int findSingleDifference(String a, String b) {
        int oneDifferent = -1;
        for (int i = 0; i < a.length() && i < b.length(); ++i) {
            if (a.charAt(i) != b.charAt(i)) {
                if (oneDifferent == -1) {
                    oneDifferent = i;
                } else {
                    return -1;
                }
            }
        }
        return oneDifferent;
    }
}
