package main.algorithms;

public class ConversorCoordenadas {

    // Converte de Dispositivo (Pixels) para NDC [0, 1]
    public static double[] inpToNdc(int x, int y, int width, int height) {
        return new double[]{
                (double) x / (width - 1),
                (double) y / (height - 1)
        };
    }

    // Converte de NDC para Coordenadas de Mundo
    public static double[] ndcToWd(double ndcx, double ndcy, double xMax, double xMin, double yMax, double yMin) {
        return new double[]{
                ndcx * (xMax - xMin) + xMin,
                ndcy * (yMax - yMin) + yMin
        };
    }

    // Converte de Coordenadas de Mundo para NDC Centralizada [-1, 1]
    public static double[] wdToNdcCentral(double x, double y, double xMax, double xMin, double yMax, double yMin) {
        return new double[]{
                2 * ((x - xMin) / (xMax - xMin)) - 1,
                2 * ((y - yMin) / (yMax - yMin)) - 1
        };
    }
}