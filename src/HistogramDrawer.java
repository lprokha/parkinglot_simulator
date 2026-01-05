public class HistogramDrawer {

    public static void drawHistogram(
            Canvas canvas,
            double[] values,
            String title,
            String barSymbol
    ) {
        canvas.reset();
        canvas.drawBorder("#");
        canvas.printTextLine(2, 1, title);

        int chartHeight = canvas.getHeight() - 5;
        int chartWidth = canvas.getWidth() - 4;

        double max = 0;
        for (double v : values) {
            if (v > max) max = v;
        }
        if (max == 0) max = 1;

        int barWidth = Math.max(1, chartWidth / values.length);
        int baseY = canvas.getHeight() - 4;

        for (int i = 0; i < values.length; i++) {
            int barHeight = (int) ((values[i] / max) * chartHeight);

            for (int h = 0; h < barHeight; h++) {
                for (int w = 0; w < barWidth; w++) {
                    canvas.setPixel(
                            2 + i,
                            baseY - h,
                            barSymbol
                    );
                }
            }
        }

        System.out.println(canvas);
    }
}
