package kt12;

/**
 * Задача 2. Число маршрутов по карте вправо и вниз в обход заблокированных клеток.
 */
public final class GridRoutes {

    static final int MOD = 1_000_000_007;

    private GridRoutes() {
    }

    /**
     * В свободную клетку попадают сверху или слева, поэтому
     * ways[r][c] = ways[r - 1][c] + ways[r][c - 1], а в заблокированную
     * клетку путей нет. Строки обходятся сверху вниз, и хватает одной строки
     * таблицы: до обновления ways[c] хранит значение клетки сверху, ways[c - 1]
     * уже посчитано для клетки слева.
     *
     * Если старт или финиш заблокирован, ответ получается 0 сам собой.
     * O(n * m) по времени, O(m) по памяти.
     */
    public static int count(String[] grid) {
        int m = grid[0].length();
        int[] ways = new int[m];
        ways[0] = 1;
        for (String row : grid)
            for (int c = 0; c < m; c++) {
                if (row.charAt(c) == '#')
                    ways[c] = 0;
                else if (c > 0)
                    ways[c] = (ways[c] + ways[c - 1]) % MOD;
            }
        return ways[m - 1];
    }
}
