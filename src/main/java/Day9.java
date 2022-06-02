import java.util.*;

public class Day9 {
    public static void main(String[] args) {
        System.out.println(playGame(419, 71052));
        System.out.println(playGame(419, 7105200));
    }

    public static long playGame(int players, int lastMarble) {
        var circle = new LinkedList<Integer>();
        circle.add(0);

        var playerScores = new HashMap<Integer, Long>();
        var player = 0;
        var current = circle.listIterator();
        for (var marble = 1; marble <= lastMarble; ++marble) {
            if (marble % 23 == 0) {
                var result = advanceIterator(-8, circle, current);
                current = result.newIterator();
                current.remove();
                current = advanceIterator(+1, circle, current).newIterator();
                var currentMarble = marble;
                playerScores.compute(player, (key, value) -> Objects.requireNonNullElse(value, 0L) + currentMarble + result.value());
            } else {
                current = advanceIterator(+1, circle, current).newIterator();
                current.add(marble);
            }
            player = (player + 1) % players;
        }

        return playerScores.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getValue();
    }

    public static <T> AdvanceResult<T> advanceIterator(int i, LinkedList<T> list, ListIterator<T> iterator) {
        T value;

        if (i > 0) {
            do {
                i -= 1;
                if (!iterator.hasNext()) {
                    iterator = list.listIterator(0);
                }
                value = iterator.next();
            } while (i > 0);
        } else if (i < 0) {
            do {
                i += 1;
                if (!iterator.hasPrevious()) {
                    iterator = list.listIterator(list.size());
                }
                value = iterator.previous();
            } while (i < 0);
        } else {
            throw new IllegalArgumentException("i");
        }

        return new AdvanceResult<>(iterator, value);
    }
}

record AdvanceResult<T>(ListIterator<T> newIterator, T value) {
}
