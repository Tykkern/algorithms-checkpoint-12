package kt12;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Запуск решения по номеру задачи: входные данные читаются из stdin
 * в формате из задания, ответ печатается в stdout.
 *
 * Пример: gradlew run -q --args="2" < input.txt
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Укажите номер задачи от 1 до 6, входные данные подаются в stdin.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        solve(Integer.parseInt(args[0]), new FastReader(System.in), out);
        out.flush();
    }

    static void solve(int task, FastReader in, PrintWriter out) throws IOException {
        switch (task) {
            case 1 -> out.println(PalindromePartition.minParts(in.next()));
            case 2 -> out.println(GridRoutes.count(readGrid(in)));
            case 3 -> {
                int[] values = in.nextInts(in.nextInt());
                int[][] edges = readEdges(in, values.length);
                out.println(TreeIndependentSet.maxValue(values, edges[0], edges[1]));
            }
            case 4 -> out.println(MergeCost.minCost(in.nextInts(in.nextInt())));
            case 5 -> {
                int n = in.nextInt();
                int m = in.nextInt();
                int shields = in.nextInt();
                int[][] grid = new int[n][];
                for (int r = 0; r < n; r++)
                    grid[r] = in.nextInts(m);
                out.println(ShieldPath.maxSum(grid, shields));
            }
            case 6 -> {
                int n = in.nextInt();
                int k = in.nextInt();
                int[] values = in.nextInts(n);
                int[][] edges = readEdges(in, n);
                out.println(ConnectedSubtree.maxValue(values, edges[0], edges[1], k));
            }
            default -> throw new IllegalArgumentException("Нет задачи с номером " + task + ", есть 1-6.");
        }
    }

    // Формат: n и m, затем n строк по m символов без пробелов.
    private static String[] readGrid(FastReader in) throws IOException {
        int n = in.nextInt();
        int m = in.nextInt();
        String[] rows = new String[n];
        for (int i = 0; i < n; i++) {
            rows[i] = in.next();
            if (rows[i].length() != m)
                throw new IOException("В строке " + (i + 1) + " ожидалось " + m + " символов, а их " + rows[i].length() + ".");
        }
        return rows;
    }

    // n - 1 ребро дерева парами u v, концы рёбер в [0] и [1].
    private static int[][] readEdges(FastReader in, int n) throws IOException {
        int[][] edges = new int[2][n - 1];
        for (int i = 0; i < n - 1; i++) {
            edges[0][i] = readVertex(in, n);
            edges[1][i] = readVertex(in, n);
        }
        return edges;
    }

    private static int readVertex(FastReader in, int n) throws IOException {
        int v = in.nextInt();
        if (v < 1 || v > n)
            throw new IOException("Номер вершины " + v + " вне диапазона 1.." + n + ".");
        return v;
    }
}
