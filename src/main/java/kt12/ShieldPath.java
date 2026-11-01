package kt12;

/**
 * Задача 5. Путь вправо и вниз с максимальной суммой, до k отрицательных клеток можно обнулить.
 */
public final class ShieldPath {

    private ShieldPath() {
    }

    /**
     * best[c][u] - максимальная сумма пути до клетки (r, c) текущей строки,
     * если экран применён не больше u раз. В клетку приходят сверху или слева,
     * из пути с лучшим значением для того же u. Значение клетки a либо
     * прибавляется, либо, если a < 0, клетка закрывается экраном: тогда берётся
     * путь, потративший не больше u - 1 экранов, и к нему прибавляется 0.
     *
     * Хранится одна строка таблицы: до обновления best[c] относится к клетке
     * сверху, best[c - 1] уже пересчитан для клетки слева.
     * O(n * m * k) по времени, O(m * k) по памяти.
     */
    public static long maxSum(int[][] grid, int shields) {
        int m = grid[0].length;
        long[][] best = new long[m][shields + 1];
        long[] incoming = new long[shields + 1];

        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < m; c++) {
                for (int u = 0; u <= shields; u++) {
                    if (r == 0 && c == 0)
                        incoming[u] = 0;
                    else if (r == 0)
                        incoming[u] = best[c - 1][u];
                    else if (c == 0)
                        incoming[u] = best[c][u];
                    else
                        incoming[u] = Math.max(best[c][u], best[c - 1][u]);
                }

                int value = grid[r][c];
                for (int u = 0; u <= shields; u++) {
                    long result = incoming[u] + value;
                    if (value < 0 && u > 0)
                        result = Math.max(result, incoming[u - 1]);
                    best[c][u] = result;
                }
            }
        return best[m - 1][shields];
    }
}
