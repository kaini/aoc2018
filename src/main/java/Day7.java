import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    public static final Pattern PATTERN = Pattern.compile("Step (.) must be finished before step (.) can begin\\.");
    public static final int WORKERS = 5;
    public static final int EXTRA_TIME = 60;

    public static void main(String[] args) throws IOException {
        List<Dependency> deps;
        try (BufferedReader in = new BufferedReader(new FileReader("day7.txt"))) {
            deps = in.lines().map(line -> {
                var match = PATTERN.matcher(line);
                if (match.matches())
                    return new Dependency(match.group(2), match.group(1));
                else {
                    throw new IllegalArgumentException(line);
                }
            }).collect(Collectors.toList());
        }
        var allSteps = deps.stream().flatMap(dep -> Stream.of(dep.dependant(), dep.dependency())).collect(Collectors.toSet());

        var done = new HashSet<String>();
        StringBuilder result = new StringBuilder();
        while (done.size() < allSteps.size()) {
            String candidate = getNextStep(deps, allSteps, done, new String[0]);
            done.add(candidate);
            result.append(candidate);
        }
        System.out.println(result);

        done.clear();
        String[] inProgress = new String[WORKERS];
        int[] readyAt = new int[WORKERS];
        Arrays.fill(readyAt, Integer.MAX_VALUE);
        int second = 0;
        while (true) {
            for (int i = 0; i < WORKERS; ++i) {
                if (readyAt[i] == second) {
                    done.add(inProgress[i]);
                    inProgress[i] = null;
                    readyAt[i] = Integer.MAX_VALUE;
                }
            }

            if (done.size() == allSteps.size()) {
                break;
            }

            for (int i = 0; i < WORKERS; ++i) {
                if (inProgress[i] == null) {
                    var nextStep = getNextStep(deps, allSteps, done, inProgress);
                    if (nextStep != null) {
                        inProgress[i] = nextStep;
                        readyAt[i] = second + EXTRA_TIME + (int)nextStep.charAt(0) - (int)'A' + 1;
                    }
                }
            }

            second = Arrays.stream(readyAt).min().orElseThrow();
        }
        System.out.println(second);
    }

    private static String getNextStep(List<Dependency> deps, Set<String> allSteps, Set<String> done, String[] inProgress) {
        String candidate = null;

        for (var step : allSteps) {
            if (!done.contains(step) && !Arrays.asList(inProgress).contains(step)) {
                var allDeps = true;
                for (var dep : deps) {
                    if (dep.dependant().equals(step) && !done.contains(dep.dependency())) {
                        allDeps = false;
                        break;
                    }
                }
                if (allDeps && (candidate == null || candidate.compareTo(step) > 0)) {
                    candidate = step;
                }
            }
        }
        return candidate;
    }

    private record Dependency(String dependant, String dependency) {
    }
}
