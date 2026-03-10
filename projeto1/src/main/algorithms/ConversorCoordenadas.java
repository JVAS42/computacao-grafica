package main.algorithms;

public class ConversorCoordenadas {

    // Retorna [ndcX, ndcY]
    public static double[] inpToNdc(int x, int y, int width, int height) {
        return new double[]{
                (double) x / (width - 1),
                (double) y / (height - 1)
        };
    }

    // Retorna [worldX, worldY]
    public static double[] ndcToWd(double ndcx, double ndcy, double xMax, double xMin, double yMax, double yMin) {
        return new double[]{
                ndcx * (xMax - xMin) + xMin,
                ndcy * (yMax - yMin) + yMin
        };
    }

    // Retorna [ndccX, ndccY]
    public static double[] wdToNdcCentral(double x, double y, double xMax, double xMin, double yMax, double yMin) {
        return new double[]{
                2 * ((x - xMin) / (xMax - xMin)) - 1,
                2 * ((y - yMin) / (yMax - yMin)) - 1
        };
    }
}
