package kt12;

/**
 * Задача 3. Набор станций максимальной ценности, никакие две из которых не соединены напрямую.
 */
public final class TreeIndependentSet {

    private TreeIndependentSet() {
    }

    /**
     * Подвешиваем дерево за станцию 1. Для каждой вершины v считаем два числа:
     * take[v] - лучший набор в поддереве v, в который v входит, и skip[v] -
     * лучший набор, в который v не входит. Если v взята, детей брать нельзя:
     * take[v] = value[v] + сумма skip[child]. Если не взята, каждый ребёнок
     * выбирает лучшее: skip[v] = сумма max(take[child], skip[child]).
     *
     * Набор может быть пустым, поэтому skip у листа 0, и отрицательные
     * ценности просто не берутся. Дети обрабатываются раньше родителя за счёт
     * обхода в обратном порядке BFS, без рекурсии: на дереве-цепочке из
     * 200 000 вершин рекурсия переполнила бы стек.
     *
     * Вершины во входе нумеруются с 1. O(n) по времени и памяти.
     */
    public static long maxValue(int[] values, int[] from, int[] to) {
        int n = values.length;
        int[][] rooted = rootedOrder(n, from, to);
        int[] order = rooted[0];
        int[] parent = rooted[1];

        long[] take = new long[n];
        long[] skip = new long[n];
        for (int i = n - 1; i >= 0; i--) {
            int v = order[i];
            take[v] += values[v];
            if (parent[v] >= 0) {
                take[parent[v]] += skip[v];
                skip[parent[v]] += Math.max(take[v], skip[v]);
            }
        }
        return Math.max(take[0], skip[0]);
    }

    /**
     * Порядок обхода в ширину от вершины 0 и родитель каждой вершины (у корня -1).
     * Рёбра заданы парами from[i], to[i] с нумерацией с 1. Списки смежности
     * хранятся в массивах: start[v] - начало соседей v в общем массиве adjacent.
     */
    static int[][] rootedOrder(int n, int[] from, int[] to) {
        int[] start = new int[n + 1];
        for (int i = 0; i < from.length; i++) {
            start[from[i]]++;
            start[to[i]]++;
        }
        for (int v = 0; v < n; v++)
            start[v + 1] += start[v];

        int[] adjacent = new int[2 * from.length];
        int[] filled = new int[n];
        for (int i = 0; i < from.length; i++) {
            int u = from[i] - 1;
            int v = to[i] - 1;
            adjacent[start[u] + filled[u]++] = v;
            adjacent[start[v] + filled[v]++] = u;
        }

        int[] order = new int[n];
        int[] parent = new int[n];
        parent[0] = -1;
        int size = 1;
        for (int head = 0; head < size; head++) {
            int v = order[head];
            for (int k = start[v]; k < start[v + 1]; k++) {
                int next = adjacent[k];
                if (next != parent[v]) {
                    parent[next] = v;
                    order[size++] = next;
                }
            }
        }
        return new int[][] {order, parent};
    }
}
