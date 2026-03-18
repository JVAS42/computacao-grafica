package main.algorithms;

public class Transformacoes3D {

    // ===============================
    // Operações Básicas (Idênticas ao JS Matrix3D.multiply)
    // ===============================
    public static double[][] multiplicarMatrizes(double[][] a, double[][] b) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public static double[] multiplicarMatrizVetor(double[][] matrix, double[] vector) {
        double[] result = new double[4];
        // Garante que o vetor de entrada tenha 4 elementos (x, y, z, w)
        double[] v4 = {vector[0], vector[1], vector[2], (vector.length > 3 ? vector[3] : 1.0)};

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i][j] * v4[j];
            }
        }
        return result;
    }

    // ===============================
    // Matrizes de Transformação (Paridade Total com script.js)
    // ===============================

    public static double[][] translacao(double tx, double ty, double tz) {
        return new double[][]{
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}
        };
    }

    public static double[][] escala(double sx, double sy, double sz) {
        return new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        };
    }

    public static double[][] rotacaoX(double anguloGraus) {
        double rad = Math.toRadians(anguloGraus);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        return new double[][]{
                {1, 0,  0, 0},
                {0, c, -s, 0},
                {0, s,  c, 0},
                {0, 0,  0, 1}
        };
    }

    public static double[][] rotacaoY(double anguloGraus) {
        double rad = Math.toRadians(anguloGraus);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        return new double[][]{
                { c, 0, s, 0},
                { 0, 1, 0, 0},
                {-s, 0, c, 0},
                { 0, 0, 0, 1}
        };
    }

    public static double[][] rotacaoZ(double anguloGraus) {
        double rad = Math.toRadians(anguloGraus);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        return new double[][]{
                {c, -s, 0, 0},
                {s,  c, 0, 0},
                {0,  0, 1, 0},
                {0,  0, 0, 1}
        };
    }

    // Ajustado para bater com Matrix3D.shear(shXY, shXZ, shYZ) do JS
    public static double[][] cisalhamento(double shXY, double shXZ, double shYZ) {
        return new double[][]{
                {1, shXY, shXZ, 0},
                {0, 1,    shYZ, 0},
                {0, 0,    1,    0},
                {0, 0,    0,    1}
        };
    }

    // Ajustado para bater com Matrix3D.reflection(axis) do JS
    public static double[][] reflexao(String eixo) {
        switch (eixo.toLowerCase()) {
            case "xy":
                return new double[][]{
                        {1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, -1, 0},
                        {0, 0, 0, 1}
                };
            case "xz":
                return new double[][]{
                        {1, 0, 0, 0},
                        {0, -1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                };
            case "yz":
                return new double[][]{
                        {-1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                };
            default:
                return Identidade();
        }
    }

    public static double[][] Identidade() {
        return new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
    }
}