package kt12;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка чтения входных данных и формата вывода для каждой задачи.
 */
class MainTest {

    private static String run(int task, String input) throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter out = new PrintWriter(output);
        FastReader in = new FastReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        Main.solve(task, in, out);
        out.flush();
        return output.toString().replace("\r\n", "\n");
    }

    @Test
    void task1() throws IOException {
        assertEquals("2\n", run(1, "aab\n"));
    }

    @Test
    void task2() throws IOException {
        assertEquals("2\n", run(2, "3 3\n...\n.#.\n...\n"));
        assertEquals("0\n", run(2, "1 2\n.#\n"));
    }

    @Test
    void task3() throws IOException {
        assertEquals("9\n", run(3, "4\n5 1 1 4\n1 2\n2 3\n3 4\n"));
        assertEquals("0\n", run(3, "1\n-5\n"));
    }

    @Test
    void task4() throws IOException {
        assertEquals("9\n", run(4, "3\n1 2 3\n"));
        assertEquals("0\n", run(4, "1\n1000000000\n"));
    }

    @Test
    void task5() throws IOException {
        assertEquals("7\n", run(5, "3 3 1\n1 -5 2\n-1 -100 3\n4 -2 1\n"));
    }

    @Test
    void task6() throws IOException {
        assertEquals("18\n", run(6, "4 3\n-1 10 9 1\n1 2\n1 3\n1 4\n"));
    }

    @Test
    void windowsLineEndings() throws IOException {
        assertEquals("2\n", run(2, "3 3\r\n...\r\n.#.\r\n...\r\n"));
    }

    @Test
    void wrongRowLengthThrows() {
        assertThrows(IOException.class, () -> run(2, "2 3\n...\n..\n"));
    }

    @Test
    void vertexOutOfRangeThrows() {
        assertThrows(IOException.class, () -> run(3, "2\n1 1\n1 3\n"));
    }

    @Test
    void unknownTaskThrows() {
        assertThrows(IllegalArgumentException.class, () -> run(7, ""));
    }
}
