import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day13 {
    public static void main(String[] args) throws IOException {
        var map = new ArrayList<String>();
        var carts = new ArrayList<Cart>();
        try (BufferedReader in = new BufferedReader(new FileReader("day13.txt"))) {
            String line;
            int y = 0;
            while ((line = in.readLine()) != null) {
                for (int x = 0; x < line.length(); ++x) {
                    switch (line.charAt(x)) {
                        case '^', 'v' -> {
                            carts.add(new Cart(x, y, line.charAt(x)));
                            line = line.substring(0, x) + '|' + line.substring(x + 1);
                        }
                        case '<', '>' -> {
                            carts.add(new Cart(x, y, line.charAt(x)));
                            line = line.substring(0, x) + '-' + line.substring(x + 1);
                        }
                    }
                }
                map.add(line);
                y += 1;
            }
        }

        var firstCrash = true;
        while (carts.size() > 1) {
            carts.sort((a, b) -> {
                var yCmp = Integer.compare(a.y, b.y);
                if (yCmp == 0) {
                    return Integer.compare(a.x, b.x);
                } else {
                    return yCmp;
                }
            });

            var remove = new HashSet<Cart>();
            for (var cart : carts) {
                if (remove.contains(cart)) {
                    continue;
                }

                // move
                switch (cart.dir) {
                    case '^' -> cart.y -= 1;
                    case 'v' -> cart.y += 1;
                    case '<' -> cart.x -= 1;
                    case '>' -> cart.x += 1;
                    default -> throw new IllegalStateException();
                }

                // steer
                cart.dir = switch (map.get(cart.y).charAt(cart.x)) {
                    case '\\' -> switch (cart.dir) {
                        case '^' -> '<';
                        case 'v' -> '>';
                        case '<' -> '^';
                        case '>' -> 'v';
                        default -> throw new IllegalStateException();
                    };
                    case '/' -> switch (cart.dir) {
                        case '^' -> '>';
                        case 'v' -> '<';
                        case '<' -> 'v';
                        case '>' -> '^';
                        default -> throw new IllegalStateException();
                    };
                    case '+' -> switch (cart.count % 3) {
                        case 0 -> switch (cart.dir) {
                            case '^' -> '<';
                            case 'v' -> '>';
                            case '<' -> 'v';
                            case '>' -> '^';
                            default -> throw new IllegalStateException();
                        };
                        case 1 -> cart.dir;
                        case 2 -> switch (cart.dir) {
                            case '^' -> '>';
                            case 'v' -> '<';
                            case '<' -> '^';
                            case '>' -> 'v';
                            default -> throw new IllegalStateException();
                        };
                        default -> throw new IllegalStateException();
                    };
                    case '-', '|' -> cart.dir;
                    default -> throw new IllegalStateException(String.valueOf(map.get(cart.y).charAt(cart.x)));
                };
                if (map.get(cart.y).charAt(cart.x) == '+') {
                    cart.count += 1;
                }

                // crash
                for (var cartB : carts) {
                    if (cart != cartB && !remove.contains(cartB) && cart.x == cartB.x && cart.y == cartB.y) {
                        if (firstCrash) {
                            System.out.println(cart.x + "," + cart.y);
                            firstCrash = false;
                        }
                        remove.add(cart);
                        remove.add(cartB);
                        break;
                    }
                }
            }

            carts.removeAll(remove);
        }

        System.out.println(carts.get(0).x + "," + carts.get(0).y);
    }

    private static class Cart {
        private int x;
        private int y;
        private char dir;
        private int count;

        public Cart(int x, int y, char dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.count = 0;
        }
    }
}
