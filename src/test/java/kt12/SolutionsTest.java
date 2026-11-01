package kt12;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SolutionsTest {

    private static final Random RANDOM = new Random(12);

    // Случайное дерево на n вершинах с перемешанными номерами: from[i], to[i] с нумерацией с 1.
    private static int[][] randomTree(int n) {
        int[] label = new int[n];
        for (int i = 0; i < n; i++)
            label[i] = i + 1;
        for (int i = n - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int swap = label[i];
            label[i] = label[j];
            label[j] = swap;
        }
        int[][] edges = new int[2][n - 1];
        for (int v = 1; v < n; v++) {
            int u = RANDOM.nextInt(v);
            boolean flip = RANDOM.nextBoolean();
            edges[0][v - 1] = label[flip ? u : v];
            edges[1][v - 1] = label[flip ? v : u];
        }
        return edges;
    }

    // Дерево-цепочка 1 - 2 - ... - n.
    private static int[][] pathTree(int n) {
        int[][] edges = new int[2][n - 1];
        for (int i = 0; i < n - 1; i++) {
            edges[0][i] = i + 1;
            edges[1][i] = i + 2;
        }
        return edges;
    }

    private static boolean[][] adjacency(int n, int[][] edges) {
        boolean[][] linked = new boolean[n][n];
        for (int i = 0; i < edges[0].length; i++) {
            linked[edges[0][i] - 1][edges[1][i] - 1] = true;
            linked[edges[1][i] - 1][edges[0][i] - 1] = true;
        }
        return linked;
    }

    @Nested
    class PalindromePartitionTest {

        private int bruteForce(String s, int from) {
            if (from == s.length())
                return 0;
            int best = Integer.MAX_VALUE;
            for (int to = from + 1; to <= s.length(); to++) {
                String part = s.substring(from, to);
                if (new StringBuilder(part).reverse().toString().equals(part))
                    best = Math.min(best, 1 + bruteForce(s, to));
            }
            return best;
        }

        @Test
        void examples() {
            assertEquals(2, PalindromePartition.minParts("aab"));
            assertEquals(1, PalindromePartition.minParts("a"));
            assertEquals(1, PalindromePartition.minParts("abba"));
            assertEquals(3, PalindromePartition.minParts("abc"));
            assertEquals(2, PalindromePartition.minParts("abaaba" + "c"));
        }

        @Test
        void randomMatchesBruteForce() {
            for (int test = 0; test < 500; test++) {
                StringBuilder s = new StringBuilder();
                for (int i = 1 + RANDOM.nextInt(12); i > 0; i--)
                    s.append((char) ('a' + RANDOM.nextInt(2)));
                assertEquals(bruteForce(s.toString(), 0), PalindromePartition.minParts(s.toString()), s.toString());
            }
        }

        @Test
        @Timeout(5)
        void largeStrings() {
            assertEquals(1, PalindromePartition.minParts("a".repeat(4000)));
            // В "abcabc..." нет палиндромов длиннее одного символа.
            assertEquals(4000, PalindromePartition.minParts("abc".repeat(1333) + "a"));
        }
    }

    @Nested
    class GridRoutesTest {

        private long bruteForce(String[] grid, int r, int c) {
            if (r == grid.length || c == grid[0].length() || grid[r].charAt(c) == '#')
                return 0;
            if (r == grid.length - 1 && c == grid[0].length() - 1)
                return 1;
            return bruteForce(grid, r + 1, c) + bruteForce(grid, r, c + 1);
        }

        @Test
        void examples() {
            assertEquals(2, GridRoutes.count(new String[] {"...", ".#.", "..."}));
            assertEquals(6, GridRoutes.count(new String[] {"...", "...", "..."}));
            assertEquals(1, GridRoutes.count(new String[] {"."}));
        }

        @Test
        void blockedStartOrFinish() {
            assertEquals(0, GridRoutes.count(new String[] {"#.", ".."}));
            assertEquals(0, GridRoutes.count(new String[] {"..", ".#"}));
            assertEquals(0, GridRoutes.count(new String[] {"#"}));
        }

        @Test
        void randomMatchesBruteForce() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(7);
                int m = 1 + RANDOM.nextInt(7);
                String[] grid = new String[n];
                for (int r = 0; r < n; r++) {
                    StringBuilder row = new StringBuilder();
                    for (int c = 0; c < m; c++)
                        row.append(RANDOM.nextInt(5) == 0 ? '#' : '.');
                    grid[r] = row.toString();
                }
                assertEquals(bruteForce(grid, 0, 0), GridRoutes.count(grid), String.join("/", grid));
            }
        }

        @Test
        @Timeout(5)
        void largeEmptyMapTakenModulo() {
            int n = 1000;
            int m = 2000;
            String[] grid = new String[n];
            Arrays.fill(grid, ".".repeat(m));
            // Маршрутов C(n + m - 2, n - 1).
            BigInteger routes = BigInteger.ONE;
            for (int i = 1; i <= n - 1; i++)
                routes = routes.multiply(BigInteger.valueOf(m - 1 + i)).divide(BigInteger.valueOf(i));
            assertEquals(routes.mod(BigInteger.valueOf(GridRoutes.MOD)).intValue(), GridRoutes.count(grid));
        }
    }

    @Nested
    class TreeIndependentSetTest {

        private long bruteForce(int[] values, int[][] edges) {
            int n = values.length;
            boolean[][] linked = adjacency(n, edges);
            long best = 0;
            for (int mask = 0; mask < 1 << n; mask++) {
                long sum = 0;
                boolean independent = true;
                for (int u = 0; u < n && independent; u++) {
                    if ((mask >> u & 1) == 0)
                        continue;
                    sum += values[u];
                    for (int v = u + 1; v < n; v++)
                        if ((mask >> v & 1) == 1 && linked[u][v])
                            independent = false;
                }
                if (independent)
                    best = Math.max(best, sum);
            }
            return best;
        }

        @Test
        void examples() {
            // Цепочка 1 - 2 - 3 - 4: лучше всего 1 и 4.
            assertEquals(9, TreeIndependentSet.maxValue(new int[] {5, 1, 1, 4}, new int[] {1, 2, 3}, new int[] {2, 3, 4}));
            assertEquals(7, TreeIndependentSet.maxValue(new int[] {7}, new int[0], new int[0]));
        }

        @Test
        void allNegativeChoosesNothing() {
            assertEquals(0, TreeIndependentSet.maxValue(new int[] {-3, -1, -2}, new int[] {1, 1}, new int[] {2, 3}));
        }

        @Test
        void randomMatchesBruteForce() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(12);
                int[] values = RANDOM.ints(n, -10, 11).toArray();
                int[][] edges = randomTree(n);
                assertEquals(bruteForce(values, edges), TreeIndependentSet.maxValue(values, edges[0], edges[1]));
            }
        }

        @Test
        @Timeout(5)
        void longPathWithoutRecursion() {
            int n = 200_000;
            int[] values = new int[n];
            Arrays.fill(values, 1_000_000_000);
            int[][] edges = pathTree(n);
            assertEquals(100_000L * 1_000_000_000, TreeIndependentSet.maxValue(values, edges[0], edges[1]));
        }

        @Test
        @Timeout(5)
        void largeStar() {
            int n = 200_000;
            int[] values = new int[n];
            Arrays.fill(values, 1);
            values[0] = 1_000_000_000;
            int[] center = new int[n - 1];
            int[] leaves = new int[n - 1];
            for (int i = 0; i < n - 1; i++) {
                center[i] = 1;
                leaves[i] = i + 2;
            }
            assertEquals(1_000_000_000, TreeIndependentSet.maxValue(values, center, leaves));
            values[0] = 5;
            assertEquals(n - 1, TreeIndependentSet.maxValue(values, leaves, center));
        }
    }

    @Nested
    class MergeCostTest {

        private long bruteForce(List<Long> groups) {
            if (groups.size() == 1)
                return 0;
            long best = Long.MAX_VALUE;
            for (int i = 0; i + 1 < groups.size(); i++) {
                List<Long> next = new ArrayList<>(groups);
                long merged = next.get(i) + next.remove(i + 1);
                next.set(i, merged);
                best = Math.min(best, merged + bruteForce(next));
            }
            return best;
        }

        @Test
        void examples() {
            assertEquals(0, MergeCost.minCost(new int[] {5}));
            assertEquals(7, MergeCost.minCost(new int[] {3, 4}));
            // (1 + 2) + (3 + 3) = 9, а через (2 + 3) вышло бы 5 + 6 = 11.
            assertEquals(9, MergeCost.minCost(new int[] {1, 2, 3}));
            // 1 + 1 = 2, затем 4 + 2 = 6, затем 6 + 4 = 10.
            assertEquals(18, MergeCost.minCost(new int[] {4, 1, 1, 4}));
        }

        @Test
        void randomMatchesBruteForce() {
            for (int test = 0; test < 300; test++) {
                List<Long> groups = new ArrayList<>();
                int[] sizes = RANDOM.ints(1 + RANDOM.nextInt(7), 1, 20).toArray();
                for (int size : sizes)
                    groups.add((long) size);
                assertEquals(bruteForce(groups), MergeCost.minCost(sizes));
            }
        }

        @Test
        @Timeout(5)
        void largeEqualFragments() {
            // При равных размерах лучше всего почти сбалансированное дерево:
            // 488 фрагментов на глубине 9 и 12 на глубине 8, итого 4488 размеров.
            int[] sizes = new int[500];
            Arrays.fill(sizes, 1_000_000_000);
            assertEquals(4488L * 1_000_000_000, MergeCost.minCost(sizes));
        }
    }

    @Nested
    class ShieldPathTest {

        private long best;

        private void bruteForce(int[][] grid, int r, int c, List<Integer> visited, int shields) {
            visited.add(grid[r][c]);
            if (r == grid.length - 1 && c == grid[0].length - 1) {
                long sum = 0;
                List<Integer> negatives = new ArrayList<>();
                for (int value : visited) {
                    sum += value;
                    if (value < 0)
                        negatives.add(value);
                }
                negatives.sort(null);
                for (int i = 0; i < Math.min(shields, negatives.size()); i++)
                    sum -= negatives.get(i);
                best = Math.max(best, sum);
            }
            if (r + 1 < grid.length)
                bruteForce(grid, r + 1, c, visited, shields);
            if (c + 1 < grid[0].length)
                bruteForce(grid, r, c + 1, visited, shields);
            visited.remove(visited.size() - 1);
        }

        @Test
        void examples() {
            int[][] grid = {
                    {1, -5, 2},
                    {-1, -100, 3},
                    {4, -2, 1},
            };
            assertEquals(1 - 1 + 4 - 2 + 1, ShieldPath.maxSum(grid, 0));
            assertEquals(1 + 0 + 2 + 3 + 1, ShieldPath.maxSum(grid, 1));
        }

        @Test
        void shieldOnStartAndFinish() {
            assertEquals(0, ShieldPath.maxSum(new int[][] {{-7}}, 1));
            assertEquals(-7, ShieldPath.maxSum(new int[][] {{-7}}, 0));
            assertEquals(5, ShieldPath.maxSum(new int[][] {{-3, 5, -4}}, 2));
        }

        @Test
        void randomMatchesBruteForce() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(5);
                int m = 1 + RANDOM.nextInt(5);
                int shields = RANDOM.nextInt(4);
                int[][] grid = new int[n][];
                for (int r = 0; r < n; r++)
                    grid[r] = RANDOM.ints(m, -20, 21).toArray();
                best = Long.MIN_VALUE;
                bruteForce(grid, 0, 0, new ArrayList<>(), shields);
                assertEquals(best, ShieldPath.maxSum(grid, shields));
            }
        }

        @Test
        @Timeout(5)
        void largeNegativeGrid() {
            int[][] grid = new int[500][500];
            for (int[] row : grid)
                Arrays.fill(row, -1_000_000_000);
            // Путь проходит 999 клеток, 10 из них закрыты экраном.
            assertEquals(-989L * 1_000_000_000, ShieldPath.maxSum(grid, 10));
        }
    }

    @Nested
    class ConnectedSubtreeTest {

        private long bruteForce(int[] values, int[][] edges, int k) {
            int n = values.length;
            boolean[][] linked = adjacency(n, edges);
            long best = Long.MIN_VALUE;
            for (int mask = 0; mask < 1 << n; mask++) {
                if (Integer.bitCount(mask) != k)
                    continue;
                // Связность: обход от любой выбранной вершины только по выбранным.
                int first = Integer.numberOfTrailingZeros(mask);
                int reached = 1 << first;
                List<Integer> todo = new ArrayList<>(List.of(first));
                while (!todo.isEmpty()) {
                    int u = todo.remove(todo.size() - 1);
                    for (int v = 0; v < n; v++)
                        if (linked[u][v] && (mask >> v & 1) == 1 && (reached >> v & 1) == 0) {
                            reached |= 1 << v;
                            todo.add(v);
                        }
                }
                if (reached != mask)
                    continue;
                long sum = 0;
                for (int v = 0; v < n; v++)
                    if ((mask >> v & 1) == 1)
                        sum += values[v];
                best = Math.max(best, sum);
            }
            return best;
        }

        @Test
        void examples() {
            // Звезда с центром 1: две лучшие вершины 2 и 3 не связаны, нужен центр.
            int[] values = {-1, 10, 9, 1};
            assertEquals(18, ConnectedSubtree.maxValue(values, new int[] {1, 1, 1}, new int[] {2, 3, 4}, 3));
            assertEquals(10, ConnectedSubtree.maxValue(values, new int[] {1, 1, 1}, new int[] {2, 3, 4}, 1));
            assertEquals(19, ConnectedSubtree.maxValue(values, new int[] {1, 1, 1}, new int[] {2, 3, 4}, 4));
        }

        @Test
        void negativeValuesStillTakeExactlyK() {
            assertEquals(-3, ConnectedSubtree.maxValue(new int[] {-1, -2, -5}, new int[] {1, 2}, new int[] {2, 3}, 2));
        }

        @Test
        void randomMatchesBruteForce() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(10);
                int k = 1 + RANDOM.nextInt(n);
                int[] values = RANDOM.ints(n, -10, 11).toArray();
                int[][] edges = randomTree(n);
                assertEquals(bruteForce(values, edges, k), ConnectedSubtree.maxValue(values, edges[0], edges[1], k));
            }
        }

        @Test
        @Timeout(5)
        void largePathMatchesWindowSum() {
            // На цепочке связная часть из k вершин - это отрезок из k подряд.
            int n = 300;
            int k = 150;
            int[] values = RANDOM.ints(n, -1_000_000_000, 1_000_000_001).toArray();
            long window = 0;
            for (int i = 0; i < k; i++)
                window += values[i];
            long expected = window;
            for (int i = k; i < n; i++) {
                window += values[i] - values[i - k];
                expected = Math.max(expected, window);
            }
            int[][] edges = pathTree(n);
            assertEquals(expected, ConnectedSubtree.maxValue(values, edges[0], edges[1], k));
        }
    }
}
