import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day16 {
    private static final List<Op> OPS;

    static {
        OPS = new ArrayList<>();
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] + reg[b]);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] + b);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] * reg[b]);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] * b);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] & reg[b]);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] & b);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] | reg[b]);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] | b);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a]);
        OPS.add((reg, a, b, c) -> reg[c] = a);
        OPS.add((reg, a, b, c) -> reg[c] = a > reg[b] ? 1 : 0);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] > b ? 1 : 0);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] > reg[b] ? 1 : 0);
        OPS.add((reg, a, b, c) -> reg[c] = a == reg[b] ? 1 : 0);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] == b ? 1 : 0);
        OPS.add((reg, a, b, c) -> reg[c] = reg[a] == reg[b] ? 1 : 0);
    }

    public static void main(String... args) throws IOException {
        var samples = new ArrayList<Sample>();
        var program = new ArrayList<int[]>();
        try (var in = new BufferedReader(new FileReader("day16.txt"))) {
            for (;;) {
                String beforeLine = in.readLine();
                if (beforeLine.isEmpty()) {
                    break;
                }
                int[] before = Arrays.stream(beforeLine.substring(beforeLine.indexOf('[') + 1, beforeLine.indexOf(']'))
                        .split(", "))
                        .mapToInt(Integer::parseInt)
                        .toArray();

                String instrLine = in.readLine();
                int[] instr = Arrays.stream(instrLine.split(" ")).mapToInt(Integer::parseInt).toArray();

                String afterLine = in.readLine();
                int[] after = Arrays.stream(afterLine.substring(afterLine.indexOf('[') + 1, afterLine.indexOf(']'))
                                .split(", "))
                        .mapToInt(Integer::parseInt)
                        .toArray();

                in.readLine();  // empty line

                samples.add(new Sample(before, after, instr));
            }

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                program.add(Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray());
            }
        }

        var opMapping = new HashMap<Integer, Set<Op>>();
        for (int i = 0; i < OPS.size(); ++i) {
            opMapping.put(i, new HashSet<>(OPS));
        }
        int count = 0;
        for (var sample : samples) {
            var possibleOps = OPS.stream().filter(op -> {
                var state = Arrays.copyOf(sample.before(), sample.before().length);
                op.run(state, sample.instr()[1], sample.instr()[2], sample.instr()[3]);
                return Arrays.equals(state, sample.after());
            }).collect(Collectors.toUnmodifiableSet());

            var mapping = opMapping.get(sample.instr()[0]);
            if (mapping.size() > 1) {
                var didRemove = mapping.removeIf(op -> !possibleOps.contains(op));
                if (didRemove && mapping.size() == 1) {
                    removeFromOthers(sample.instr()[0], opMapping);
                }
            }

            if (possibleOps.size() >= 3) {
                count += 1;
            }
        }
        System.out.println(count);

        int[] state = new int[] { 0, 0, 0, 0 };
        for (var instr : program) {
            opMapping.get(instr[0]).stream().findAny().orElseThrow().run(state, instr[1], instr[2], instr[3]);
        }
        System.out.println(state[0]);
    }

    private static void removeFromOthers(int i, Map<Integer, Set<Op>> opMapping) {
        for (var entry : opMapping.entrySet()) {
            if (entry.getKey() != i) {
                if (entry.getValue().removeAll(opMapping.get(i)) && entry.getValue().size() == 1) {
                    removeFromOthers(entry.getKey(), opMapping);
                }
            }
        }
    }

    @FunctionalInterface
    private interface Op {
        void run(int[] reg, int a, int b, int c);
    }

    private record Sample(int[] before, int[] after, int[] instr) {
    }
}
