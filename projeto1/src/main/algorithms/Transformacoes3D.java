package main.algorithms;

public class Transformacoes3D {

    // ===============================
    // Operações Básicas de Matrizes 4x4
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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }

    // ===============================
    // Matrizes de Transformação 3D
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

    public static double[][] cisalhamento(double shXY, double shXZ, double shYZ) {
        return new double[][]{
                {1, shXY, shXZ, 0},
                {0, 1,    shYZ, 0},
                {0, 0,    1,    0},
                {0, 0,    0,    1}
        };
    }

    public static double[][] reflexao(String eixo) {
        double[][] mat = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        switch (eixo.toLowerCase()) {
            case "xy": mat[2][2] = -1; break; // Inverte Z
            case "xz": mat[1][1] = -1; break; // Inverte Y
            case "yz": mat[0][0] = -1; break; // Inverte X
        }
        return mat;
    }
}
