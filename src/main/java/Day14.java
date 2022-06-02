import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Day14 {
    public static void main(String[] args) throws IOException {
        int after;
        try (var in = new BufferedReader(new FileReader("day14.txt"))) {
            after = Integer.parseInt(in.readLine());
        }

        var scores = new ArrayList<Integer>();
        scores.add(3);
        scores.add(7);
        int a = 0;
        int b = 1;
        while (scores.size() < after + 10) {
            int sum = scores.get(a) + scores.get(b);
            if (sum >= 10) {
                scores.add(sum / 10);
            }
            scores.add(sum % 10);
            a = (a + scores.get(a) + 1) % scores.size();
            b = (b + scores.get(b) + 1) % scores.size();
        }
        var result = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            result.append(scores.get(i + after));
        }
        System.out.println(result);

        var goal = String.valueOf(after).chars().mapToObj(c -> c - '0').toList();
        scores = new ArrayList<>();
        scores.add(3);
        scores.add(7);
        a = 0;
        b = 1;
        while (true) {
            int sum = scores.get(a) + scores.get(b);
            if (sum >= 10) {
                scores.add(sum / 10);
            }
            scores.add(sum % 10);
            a = (a + scores.get(a) + 1) % scores.size();
            b = (b + scores.get(b) + 1) % scores.size();

            if (scores.size() >= goal.size()) {
                var matches = true;
                for (int i = 0; i < goal.size(); ++i) {
                    if (!scores.get(scores.size() - goal.size() + i).equals(goal.get(i))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    System.out.println(scores.size() - goal.size());
                    break;
                }
            }
            if (scores.size() - 1 >= goal.size()) {
                var matches = true;
                for (int i = 0; i < goal.size(); ++i) {
                    if (!scores.get(scores.size() - goal.size() - 1 + i).equals(goal.get(i))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    System.out.println(scores.size() - goal.size() - 1);
                    break;
                }
            }
        }
    }
}
