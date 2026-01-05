
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Canvas {
    private final int width;
    private final int height;
    private String[][] canvas;

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        canvas = new String[height][width];
        reset();
    }

    public Canvas() {
        this(80, 18);
    }

    public void drawRectangle(int x, int y, int width, int height, String symbol) {
        var toX = Math.min(x + width - 1, this.width - 1);
        var toY = Math.min(y + height - 1, this.height - 1);

        IntStream.rangeClosed(x, toX).forEach(colIndex -> {
            setPixel(colIndex, y, symbol);
            setPixel(colIndex, toY, symbol);
        });

        IntStream.rangeClosed(y, toY).forEach(rowIndex -> {
            setPixel(x, rowIndex, symbol);
            setPixel(toX, rowIndex, symbol);
        });
    }

    public void fill(String fillSymbol) {
        Arrays.stream(canvas).forEach(r -> Arrays.fill(r, fillSymbol));
    }

    public void fill() {
        fill(" ");
    }

    public void reset() {
        fill(" ");
    }

    public void setPixel(int x, int y, String symbol) {
        canvas[y][x] = symbol;
    }

    public void printTextLine(int x, int y, String text) {
        var maxLength = Math.min(text.length(), this.width - x - 1);
        var symbols = text.split("");
        IntStream.range(0, maxLength).forEach(
                e -> setPixel(x + e, y, symbols[e])
        );
    }

    @Override
    public String toString() {
        return Arrays.stream(canvas)
                .map(row -> Arrays.stream(row).collect(Collectors.joining("")))
                .collect(Collectors.joining("\n"));
    }

    public void drawBorder(String borderSymbol) {
        drawRectangle(0, 0, width, height, borderSymbol);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
