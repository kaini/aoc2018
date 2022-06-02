import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class Day15 {
    public static void main(String... args) throws IOException {
        Set<XY> walls = new HashSet<>();
        List<Unit> units = new ArrayList<>();
        try (var in = new BufferedReader(new FileReader("day15.txt"))) {
            int y = 0;
            String line;
            while ((line = in.readLine()) != null) {
                for (int x = 0; x < line.length(); ++x) {
                    switch (line.charAt(x)) {
                        case '#' -> walls.add(new XY(x, y));
                        case 'G' -> units.add(new Unit(x, y, 'G'));
                        case 'E' -> units.add(new Unit(x, y, 'E'));
                    }
                }
                y += 1;
            }
        }
        walls = Collections.unmodifiableSet(walls);
        units = Collections.unmodifiableList(units);

        var unitsA = units.stream().map(Unit::clone).collect(Collectors.toList());
        int rounds = fight(unitsA, walls, 3);
        int remainingHp = unitsA.stream().filter(u -> u.hp > 0).mapToInt(u -> u.hp).sum();
        System.out.println(rounds * remainingHp);

        for (int atk = 4;; ++atk) {
            var unitsB = units.stream().map(Unit::clone).collect(Collectors.toList());
            rounds = fight(unitsB, walls, atk);
            if (unitsB.stream().noneMatch(u -> u.type == 'E' && u.hp <= 0)) {
                remainingHp = unitsB.stream().filter(u -> u.hp > 0).mapToInt(u -> u.hp).sum();
                System.out.println(rounds * remainingHp);
                break;
            }
        }
    }

    private static int fight(List<Unit> units, Set<XY> walls, int elfAttack) {
        int round;
        combat: for (round = 0;; ++round) {
            units.sort(comparing(u -> new XY(u.x, u.y), READING_ORDER));

            for (var unit : units) {
                if (unit.hp <= 0) {
                    continue;
                }

                var targets = units
                        .stream()
                        .filter(u -> u.hp > 0 && u.type != unit.type)
                        .sorted(comparing(u -> new XY(u.x, u.y), READING_ORDER))
                        .toList();
                if (targets.isEmpty()) {
                    break combat;
                }

                var unitAdjacentA = adjacent(unit.x, unit.y).toList();
                var targetInRange = targets.stream().filter(u -> unitAdjacentA.contains(new XY(u.x, u.y))).findFirst();
                if (targetInRange.isEmpty()) {
                    var occupied = new HashSet<>(walls);
                    for (var u : units) {
                        if (u.hp > 0) {
                            occupied.add(new XY(u.x, u.y));
                        }
                    }

                    var distances = distances(unit.x, unit.y, occupied);
                    var bestOpen = targets
                            .stream()
                            .flatMap(u -> adjacent(u.x, u.y))
                            .filter(p -> !occupied.contains(p) && distances.containsKey(p))
                            .min((a, b) -> {
                                var d = Integer.compare(distances.get(a), distances.get(b));
                                if (d != 0) return d;
                                else return READING_ORDER.compare(a, b);
                            });

                    if (bestOpen.isPresent()) {
                        var at = bestOpen.get();
                        while (distances.get(at) > 1) {
                            at = adjacent(at.x(), at.y())
                                    .filter(distances::containsKey)
                                    .min(comparingInt(distances::get))
                                    .orElseThrow();
                        }
                        unit.x = at.x();
                        unit.y = at.y();
                    }
                }

                var unitAdjacentB = adjacent(unit.x, unit.y).toList();
                targets.stream()
                        .filter(u -> unitAdjacentB.contains(new XY(u.x, u.y)))
                        .min((a, b) -> {
                            var hp = Integer.compare(a.hp, b.hp);
                            if (hp != 0) return hp;
                            else return READING_ORDER.compare(new XY(a.x, a.y), new XY(b.x, b.y));
                        })
                        .ifPresent((target) -> target.hp -= unit.type == 'E' ? elfAttack : 3);
            }
        }
        return round;
    }

    private static Map<XY, Integer> distances(int x, int y, Set<XY> occupied) {
        Queue<XY> todo = new ArrayDeque<>();
        Map<XY, Integer> distances = new HashMap<>();
        distances.put(new XY(x, y), 0);
        todo.add(new XY(x, y));
        while (!todo.isEmpty()) {
            XY p = todo.remove();
            adjacent(p.x(), p.y()).forEach((pp) -> {
                if (!occupied.contains(pp) && !distances.containsKey(pp)) {
                    distances.put(pp, distances.get(p) + 1);
                    todo.add(pp);
                }
            });
        }
        return distances;
    }

    private static Stream<XY> adjacent(int x, int y) {
        // This is in reading order
        return Stream.of(
                new XY(x, y - 1),
                new XY(x - 1, y),
                new XY(x + 1, y),
                new XY(x, y + 1));
    }

    private static final Comparator<XY> READING_ORDER = (a, b) -> {
        int result = Integer.compare(a.y(), b.y());
        if (result != 0) {
            return result;
        } else {
            return Integer.compare(a.x(), b.x());
        }
    };

    private record XY(int x, int y) {
    }

    private static class Unit implements Cloneable {
        int x;
        int y;
        char type;
        int hp = 200;

        public Unit(int x, int y, char type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Unit{" +
                    "x=" + x +
                    ", y=" + y +
                    ", type=" + type +
                    ", hp=" + hp +
                    '}';
        }

        @Override
        public Unit clone() {
            try {
                return (Unit) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
