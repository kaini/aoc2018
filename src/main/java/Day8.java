import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

public class Day8 {
    public static void main(String[] args) throws IOException {
        List<Integer> numbers;
        try (BufferedReader in = new BufferedReader(new FileReader("day8.txt"))) {
            numbers = Arrays.stream(in.readLine().split("\\s+")).map(Integer::parseInt).toList();
        }

        int result = walkTree(numbers, (metadatas, children) ->
                metadatas.stream().mapToInt(i -> i).sum() + children.stream().mapToInt(i -> i).sum());
        System.out.println(result);

        result = walkTree(numbers, (metadatas, children) -> {
            if (children.isEmpty()) {
                return metadatas.stream().mapToInt(i -> i).sum();
            } else {
                int sum = 0;
                for (var m : metadatas) {
                    if (m != 0 && m <= children.size()) {
                        sum += children.get(m - 1);
                    }
                }
                return sum;
            }
        });
        System.out.println(result);
    }

    public static <T> T walkTree(List<Integer> tree, BiFunction<List<Integer>, List<T>, T> consumer) {
        return walkTreeRec(tree, consumer).result();
    }

    public static <T> WalkTree<T> walkTreeRec(List<Integer> tree, BiFunction<List<Integer>, List<T>, T> consumer) {
        int children = tree.get(0);
        int metadatas = tree.get(1);
        int index = 2;
        List<T> childResults = new ArrayList<>();
        for (int i = 0; i < children; ++i) {
            var result = walkTreeRec(tree.subList(index, tree.size()), consumer);
            index += result.index();
            childResults.add(result.result());
        }
        return new WalkTree<>(index + metadatas, consumer.apply(tree.subList(index, index + metadatas), childResults));
    }
}

record WalkTree<T>(int index, T result) {
}
