package kt12;

import java.util.Arrays;

/**
 * Задача 6. Связная часть дерева ровно из k пунктов с максимальной суммой ценностей.
 */
public final class ConnectedSubtree {

    private static final long NONE = Long.MIN_VALUE;

    private ConnectedSubtree() {
    }

    /**
     * Подвешиваем дерево за пункт 1. У любой связной части есть самая верхняя
     * вершина v, и часть целиком лежит в поддереве v. best[v][j] - лучшая
     * сумма связной части из j вершин, верхняя вершина которой v.
     *
     * Сначала best[v] = {-, value[v]}. Каждый ребёнок c добавляется как
     * рюкзак: из его поддерева берётся связная часть с верхней вершиной c
     * из b вершин (или ничего), и best[v][a + b] = max(best[v][a] + best[c][b]).
     * Размер таблиц ограничен размером поддерева, поэтому все объединения
     * вместе стоят O(n^2): каждая пара вершин встречается один раз,
     * в их наименьшем общем предке.
     *
     * Ответ - максимум best[v][k] по всем v. Обход в обратном порядке BFS,
     * дети готовы раньше родителя. O(n^2) по времени и памяти.
     */
    public static long maxValue(int[] values, int[] from, int[] to, int k) {
        int n = values.length;
        int[][] rooted = TreeIndependentSet.rootedOrder(n, from, to);
        int[] order = rooted[0];
        int[] parent = rooted[1];

        long[][] best = new long[n][];
        for (int v = 0; v < n; v++)
            best[v] = new long[] {NONE, values[v]};

        long answer = NONE;
        for (int i = n - 1; i >= 0; i--) {
            int v = order[i];
            if (best[v].length > k)
                answer = Math.max(answer, best[v][k]);
            if (parent[v] >= 0)
                best[parent[v]] = attach(best[parent[v]], best[v]);
            best[v] = null;
        }
        return answer;
    }

    // Объединение таблицы родителя с таблицей ребёнка, индекс таблицы - число вершин.
    private static long[] attach(long[] top, long[] child) {
        long[] merged = Arrays.copyOf(top, top.length + child.length - 1);
        Arrays.fill(merged, top.length, merged.length, NONE);
        for (int a = 1; a < top.length; a++) {
            if (top[a] == NONE)
                continue;
            for (int b = 1; b < child.length; b++)
                if (child[b] != NONE)
                    merged[a + b] = Math.max(merged[a + b], top[a] + child[b]);
        }
        return merged;
    }
}
