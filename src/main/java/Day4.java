import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4 {
    private static final Pattern EVENT_RE = Pattern.compile("\\[(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d)] (Guard #(\\d+) begins shift|falls asleep|wakes up)");
    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm").toFormatter();

    public static void main(String[] args) throws IOException {
        List<Event> events;
        try (BufferedReader in = new BufferedReader(new FileReader("day4.txt"))) {
            events = in.lines().map(line -> {
                Matcher matcher = EVENT_RE.matcher(line);
                if (matcher.matches()) {
                    return new Event(
                            LocalDateTime.parse(matcher.group(1), DATE_FORMAT),
                            EventType.fromString(matcher.group(2)),
                            matcher.group(3) == null ? -1 : Integer.parseInt(matcher.group(3)));
                } else {
                    throw new RuntimeException("Regex does not match!");
                }
            }).collect(Collectors.toList());
        }
        events.sort(Comparator.comparing(Event::time));

        int currentGuard = -1;
        LocalDateTime startSleepTime = null;
        // guard id -> minute -> numbers of times slept
        Map<Integer, Map<Integer, Integer>> sleepMinutesByGuard = new HashMap<>();
        // minute -> guard id -> number of times slept
        Map<Integer, Map<Integer, Integer>> sleepGuardsByMinute = new HashMap<>();
        for (Event event : events) {
            if (event.type() == EventType.BEGIN) {
                currentGuard = event.guard();
            } else if (event.type() == EventType.FALL_ASLEEP) {
                startSleepTime = event.time();
            } else if (event.type() == EventType.WAKE_UP) {
                assert startSleepTime != null;
                for (LocalDateTime at = startSleepTime; at.isBefore(event.time()); at = at.plus(1, ChronoUnit.MINUTES)) {
                    if (!sleepMinutesByGuard.containsKey(currentGuard)) {
                        sleepMinutesByGuard.put(currentGuard, new HashMap<>());
                    }
                    sleepMinutesByGuard.get(currentGuard).merge(at.getMinute(), 1, Integer::sum);

                    if (!sleepGuardsByMinute.containsKey(at.getMinute())) {
                        sleepGuardsByMinute.put(at.getMinute(), new HashMap<>());
                    }
                    sleepGuardsByMinute.get(at.getMinute()).merge(currentGuard, 1, Integer::sum);
                }
                startSleepTime = null;
            }
        }
        var mostSleepingGuard = sleepMinutesByGuard
                .entrySet()
                .stream()
                .max(Comparator.comparing(entry -> entry.getValue().values().stream().mapToInt(i -> i).sum()))
                .orElseThrow();
        int mostSleepingMinute = mostSleepingGuard.getValue()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
        System.out.println(mostSleepingMinute * mostSleepingGuard.getKey());

        var minute = sleepGuardsByMinute
                .entrySet()
                .stream()
                .max(Comparator.comparing(entry -> entry.getValue().values().stream().max(Integer::compareTo).orElseThrow()))
                .orElseThrow();
        int guard = minute.getValue().entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
        System.out.println(minute.getKey() * guard);
    }
}

record Event(
        LocalDateTime time,
        EventType type,
        int guard
) {
}

enum EventType {
    BEGIN, FALL_ASLEEP, WAKE_UP;

    public static EventType fromString(String s) {
        if (s.startsWith("Guard")) {
            return BEGIN;
        } else if (s.equals("falls asleep")) {
            return FALL_ASLEEP;
        } else if (s.equals("wakes up")) {
            return WAKE_UP;
        } else {
            throw new IllegalArgumentException(s);
        }
    }
}
