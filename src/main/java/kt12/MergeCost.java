package kt12;

/**
 * Задача 4. Минимальная стоимость объединения соседних групп в одну.
 */
public final class MergeCost {

    private MergeCost() {
    }

    /**
     * cost[l][r] - минимальная стоимость собрать фрагменты l..r в одну группу.
     * Последнее объединение склеивает группы l..k и k + 1..r и стоит сумму
     * всех фрагментов l..r, сколько бы ни стоили шаги до него. Поэтому
     * cost[l][r] = min по k (cost[l][k] + cost[k + 1][r]) + sum(l..r),
     * а сумма отрезка берётся из префиксных сумм.
     *
     * Отрезки считаются по возрастанию длины. O(n^3) по времени, при n = 500
     * это около 2 * 10^7 шагов. O(n^2) по памяти.
     */
    public static long minCost(int[] sizes) {
        int n = sizes.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++)
            prefix[i + 1] = prefix[i] + sizes[i];

        long[][] cost = new long[n][n];
        for (int length = 2; length <= n; length++)
            for (int left = 0; left + length <= n; left++) {
                int right = left + length - 1;
                long best = Long.MAX_VALUE;
                for (int split = left; split < right; split++)
                    best = Math.min(best, cost[left][split] + cost[split + 1][right]);
                cost[left][right] = best + prefix[right + 1] - prefix[left];
            }
        return cost[0][n - 1];
    }
}
