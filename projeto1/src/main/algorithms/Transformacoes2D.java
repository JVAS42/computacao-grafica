package main.algorithms;

import java.awt.geom.Point2D;

public class Transformacoes2D {

    // ===============================
    // Operações Básicas de Matrizes
    // ===============================
    public static double[] multiplicarMatrizVetor(double[][] matriz, double[] vetor) {
        return new double[]{
                matriz[0][0] * vetor[0] + matriz[0][1] * vetor[1] + matriz[0][2] * vetor[2],
                matriz[1][0] * vetor[0] + matriz[1][1] * vetor[1] + matriz[1][2] * vetor[2],
                matriz[2][0] * vetor[0] + matriz[2][1] * vetor[1] + matriz[2][2] * vetor[2]
        };
    }

    public static double[][] multiplicarMatrizes(double[][] a, double[][] b) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    // ===============================
    // Matrizes de Transformação
    // ===============================
    public static double[][] criarMatrizTranslacao(double dx, double dy) {
        return new double[][]{
                {1, 0, dx},
                {0, 1, dy},
                {0, 0, 1}
        };
    }

    public static double[][] criarMatrizEscala(double sx, double sy) {
        return new double[][]{
                {sx, 0, 0},
                {0, sy, 0},
                {0, 0, 1}
        };
    }

    public static double[][] criarMatrizRotacao(double anguloGraus, double centroX, double centroY) {
        double anguloRad = Math.toRadians(anguloGraus);
        double cos = Math.cos(anguloRad);
        double sin = Math.sin(anguloRad);

        double[][] matrizRotacao = {
                {cos, -sin, 0},
                {sin,  cos, 0},
                {0,    0,   1}
        };

        if (centroX != 0 || centroY != 0) {
            double[][] tPos = criarMatrizTranslacao(centroX, centroY);
            double[][] tNeg = criarMatrizTranslacao(-centroX, -centroY);
            return multiplicarMatrizes(tPos, multiplicarMatrizes(matrizRotacao, tNeg));
        }

        return matrizRotacao;
    }

    public static double[][] criarMatrizCisalhamento(double shx, double shy) {
        return new double[][]{
                {1, shx, 0},
                {shy, 1, 0},
                {0, 0, 1}
        };
    }

    public static double[][] criarMatrizReflexao(boolean refletirX, boolean refletirY) {
        return new double[][]{
                {refletirY ? -1 : 1, 0, 0},
                {0, refletirX ? -1 : 1, 0},
                {0, 0, 1}
        };
    }

    // ===============================
    // Aplicação em um Ponto
    // ===============================
    public static Point2D.Double aplicarTransformacao(Point2D.Double ponto, double[][] matriz) {
        double[] vetor = {ponto.x, ponto.y, 1}; // Coordenada homogênea
        double[] resultado = multiplicarMatrizVetor(matriz, vetor);
        return new Point2D.Double(resultado[0], resultado[1]);
    }
}
