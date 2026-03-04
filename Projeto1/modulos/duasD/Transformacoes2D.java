package Projeto1.modulos.duasD;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Transformacoes2D {

    // Multiplica uma matriz 3x3 por um ponto (vetor homogêneo [x, y, 1])
    public static Point2D.Double multiplicarMatrizPonto(double[][] matriz, Point2D.Double ponto) {
        double x = matriz[0][0] * ponto.x + matriz[0][1] * ponto.y + matriz[0][2] * 1;
        double y = matriz[1][0] * ponto.x + matriz[1][1] * ponto.y + matriz[1][2] * 1;
        return new Point2D.Double(x, y);
    }

    public static double[][] criarMatrizTranslacao(double dx, double dy) {
        return new double[][]{
            {1, 0, dx},
            {0, 1, dy},
            {0, 0, 1}
        };
    }

    public static double[][] criarMatrizEscala(double sx, double sy, double px, double py) {
        // Escala em relação a um ponto fixo (px, py)
        double[][] t1 = criarMatrizTranslacao(-px, -py);
        double[][] e = {{sx, 0, 0}, {0, sy, 0}, {0, 0, 1}};
        double[][] t2 = criarMatrizTranslacao(px, py);
        return multiplicarMatrizes(t2, multiplicarMatrizes(e, t1));
    }

    public static double[][] criarMatrizRotacao(double anguloGraus, double px, double py) {
        double rad = Math.toRadians(anguloGraus);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        
        double[][] t1 = criarMatrizTranslacao(-px, -py);
        double[][] r = {{cos, -sin, 0}, {sin, cos, 0}, {0, 0, 1}};
        double[][] t2 = criarMatrizTranslacao(px, py);
        return multiplicarMatrizes(t2, multiplicarMatrizes(r, t1));
    }

    public static double[][] criarMatrizReflexao(boolean emX, boolean emY) {
        double mX = emY ? -1 : 1; // Refletir em Y inverte o X
        double mY = emX ? -1 : 1; // Refletir em X inverte o Y
        return new double[][]{
            {mX, 0, 0},
            {0, mY, 0},
            {0, 0, 1}
        };
    }

    public static double[][] multiplicarMatrizes(double[][] A, double[][] B) {
        double[][] C = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }
}