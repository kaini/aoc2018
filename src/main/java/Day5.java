import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Day5 {
    public static void main(String[] args) throws IOException {
        String polymer;
        try (BufferedReader in = new BufferedReader(new FileReader("day5.txt"))) {
            polymer = in.readLine();
        }

        System.out.println(react(polymer));

        int min = Integer.MAX_VALUE;
        for (char c = 'a'; c <= 'z'; ++c) {
            int len = react(polymer.replace("" + c, "").replace("" + Character.toUpperCase(c), ""));
            min = Math.min(min, len);
        }
        System.out.println(min);
    }

    private static int react(String polymer) {
        Stack<Character> stack = new Stack<>();
        for (char c : polymer.toCharArray()) {
            if (stack.isEmpty()) {
                stack.push(c);
            } else if (stack.peek() == toOtherCase(c)) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        return stack.size();
    }

    private static char toOtherCase(char c) {
        if (Character.isUpperCase(c)) {
            return Character.toLowerCase(c);
        } else {
            return Character.toUpperCase(c);
        }
    }
}
