package kt12;

/**
 * Задача 1. Разбиение строки на минимальное число палиндромов.
 */
public final class PalindromePartition {

    private PalindromePartition() {
    }

    /**
     * parts[i] - минимальное число палиндромов, на которое делится префикс
     * длины i, parts[0] = 0. Каждый палиндром s[l..r] даёт вариант
     * parts[r + 1] = parts[l] + 1.
     *
     * Палиндромы перебираются расширением от центров, без таблицы n * n.
     * Центр задаётся суммой l + r, суммы идут по возрастанию. У палиндрома,
     * который заканчивается в l - 1, сумма меньше 2l, а у любого палиндрома,
     * начинающегося в l, не меньше 2l. Поэтому к моменту, когда палиндром
     * s[l..r] использует parts[l], это значение уже окончательное.
     *
     * O(n^2) по времени, O(n) по памяти.
     */
    public static int minParts(String s) {
        int n = s.length();
        int[] parts = new int[n + 1];
        for (int i = 1; i <= n; i++)
            parts[i] = i;

        for (int sum = 0; sum <= 2 * n - 2; sum++) {
            int left = sum / 2;
            int right = sum - left;
            while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
                parts[right + 1] = Math.min(parts[right + 1], parts[left] + 1);
                left--;
                right++;
            }
        }
        return parts[n];
    }
}
