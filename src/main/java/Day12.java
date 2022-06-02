import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Day12 {
    public static void main(String[] args) throws IOException {
        var initialState = new TreeSet<Integer>();
        var patterns = new HashSet<String>();
        try (BufferedReader in = new BufferedReader(new FileReader("day12.txt"))) {
            var firstLine = in.readLine();
            var stateString = firstLine.split(": ")[1];
            for (int i = 0; i < stateString.length(); ++i) {
                if (stateString.charAt(i) == '#') {
                    initialState.add(i);
                }
            }

            in.readLine();  // empty line

            String line;
            while ((line = in.readLine()) != null) {
                var parts = line.split(" => ");
                if (parts[1].charAt(0) == '#') {
                    patterns.add(parts[0]);
                }
            }
        }
        if (patterns.contains(".....")) throw new IllegalArgumentException("I assume that ..... leads to .");

        List<Set<Integer>> seenStates = new ArrayList<>();
        List<Integer> stateOffsets = new ArrayList<>();
        var currentState = initialState;
        int stateOffset = 0;
        while (!seenStates.contains(currentState)) {
            seenStates.add(currentState);
            stateOffsets.add(stateOffset);

            // calculate next state
            var nextState = new TreeSet<Integer>();
            var max = currentState.last() + 4;
            for (int i = -4; i <= max; ++i) {
                StringBuilder situation = new StringBuilder();
                for (int offset = -2; offset <= 2; ++offset) {
                    situation.append(currentState.contains(i + offset) ? '#' : '.');
                }
                if (patterns.contains(situation.toString())) {
                    nextState.add(i);
                }
            }

            // normalize coordinates
            int minCoordinate = nextState.first();
            stateOffset += minCoordinate;
            currentState = new TreeSet<>(nextState.stream().map(i -> i - minCoordinate).collect(new TreeSetCollector<>()));
        }

        // solution for part 1
        var stateOffsetCopy = stateOffsets.get(20);
        System.out.println(seenStates.get(20).stream().mapToInt(i -> i + stateOffsetCopy).sum());

        // solution for part 2
        var loopStart = seenStates.indexOf(currentState);
        var loopOffset = stateOffset - stateOffsets.get(loopStart);
        var endOffset = stateOffsets.get(loopStart) + loopOffset * (50000000000L - loopStart);
        if (loopStart != seenStates.size() - 1) throw new IllegalArgumentException("I assume that we reach a steady state");
        System.out.println(currentState.stream().mapToLong(i -> i + endOffset).sum());
    }

    private static final class TreeSetCollector<T> implements Collector<T, TreeSet<T>, TreeSet<T>> {
        @Override
        public Supplier<TreeSet<T>> supplier() {
            return TreeSet::new;
        }

        @Override
        public BiConsumer<TreeSet<T>, T> accumulator() {
            return TreeSet::add;
        }

        @Override
        public BinaryOperator<TreeSet<T>> combiner() {
            return (a, b) -> {
                a.addAll(b);
                return a;
            };
        }

        @Override
        public Function<TreeSet<T>, TreeSet<T>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
        }
    }
}
