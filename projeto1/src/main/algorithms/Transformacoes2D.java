package main.algorithms;

import java.awt.geom.Point2D;

public class Transformacoes2D {

    public static void imprimirMatriz(double[][] matriz, String nome) {
        System.out.println(nome + ":");
        for (double[] linha : matriz) {
            for (double valor : linha) {
                System.out.printf("%8.3f ", valor);
            }
            System.out.println();
        }
        System.out.println();
    }

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

    // Calcula a matriz de mapeamento Window-to-Viewport combinando translação e escala
    public static double[][] criarMatrizMundoParaViewport(
            double xwMin, double xwMax, double ywMin, double ywMax,
            double xvMin, double xvMax, double yvMin, double yvMax) {

        double Sx = (xvMax - xvMin) / (xwMax - xwMin);
        double Sy = (yvMax - yvMin) / (ywMax - ywMin);

        double Tx = xvMin - Sx * xwMin;
        double Ty = yvMin - Sy * ywMin;

        double[][] M = {
                {Sx, 0,  Tx},
                {0,  Sy, Ty},
                {0,  0,  1}
        };

        System.out.println("\n===== MATRIZ MUNDO - VIEWPORT =====");
        imprimirMatriz(M);

        return M;
    }

    public static Point2D.Double aplicarMatriz(double[][] M, double x, double y) {
        double Sx = M[0][0];
        double Tx = M[0][2];
        double Sy = M[1][1];
        double Ty = M[1][2];

        double xv = Sx * x + Tx;
        double yv = Sy * y + Ty;

        System.out.printf("PONTO MUNDO: (%.2f, %.2f)  =>  RESULTADO VIEWPORT: (%.2f, %.2f)\n", x, y, xv, yv);
        System.out.println("-".repeat(60));

        System.out.printf("MATRIZ USADA:                EQUAÇÕES:\n");
        System.out.printf("| %5.2f  0.00  %6.2f |    Xv = %.2f * Xw + %.2f\n", Sx, Tx, Sx, Tx);
        System.out.printf("|  0.00  %5.2f  %6.2f |    Yv = %.2f * Yw + %.2f\n", Sy, Ty, Sy, Ty);
        System.out.printf("|  0.00   0.00    1.00 |\n\n");

        System.out.println("CÁLCULO DE X:                        CÁLCULO DE Y:");
        System.out.printf("Xv = %.2f * (%.2f) + %.2f          Yv = %.2f * (%.2f) + %.2f\n", Sx, x, Tx, Sy, y, Ty);
        System.out.printf("Xv = %.2f + %.2f                 Yv = %.2f + %.2f\n", (Sx * x), Tx, (Sy * y), Ty);
        System.out.printf("Xv = %.2f                           Yv = %.2f\n", xv, yv);
        System.out.println("=".repeat(60));

        return new Point2D.Double(xv, yv);
    }

    public static void imprimirMatriz(double[][] M) {
        for (int i = 0; i < 3; i++) {
            System.out.printf("| %8.3f %8.3f %8.3f |\n", M[i][0], M[i][1], M[i][2]);
        }
        System.out.println("====================================\n");
    }

    private static final int INSIDE = 0;
    private static final int LEFT   = 1;
    private static final int RIGHT  = 2;
    private static final int BOTTOM = 4;
    private static final int TOP    = 8;

    private static int computarOutCode(double x, double y, double xMin, double xMax, double yMin, double yMax) {
        int code = INSIDE;
        if (x < xMin) code |= LEFT;
        else if (x > xMax) code |= RIGHT;
        if (y < yMin) code |= BOTTOM;
        else if (y > yMax) code |= TOP;
        return code;
    }

    public static Point2D.Double[] cohenSutherlandClip(Point2D.Double p1, Point2D.Double p2, double xMin, double xMax, double yMin, double yMax) {
        double x0 = p1.x, y0 = p1.y;
        double x1 = p2.x, y1 = p2.y;

        int outcode0 = computarOutCode(x0, y0, xMin, xMax, yMin, yMax);
        int outcode1 = computarOutCode(x1, y1, xMin, xMax, yMin, yMax);
        boolean aceito = false;

        while (true) {
            if ((outcode0 | outcode1) == 0) {
                aceito = true;
                break;
            } else if ((outcode0 & outcode1) != 0) {
                break;
            } else {
                double x = 0, y = 0;
                int outcodeFora = (outcode0 != 0) ? outcode0 : outcode1;

                if ((outcodeFora & TOP) != 0) {
                    if (y1 != y0) x = x0 + (x1 - x0) * (yMax - y0) / (y1 - y0);
                    else x = x0;
                    y = yMax;
                } else if ((outcodeFora & BOTTOM) != 0) {
                    if (y1 != y0) x = x0 + (x1 - x0) * (yMin - y0) / (y1 - y0);
                    else x = x0;
                    y = yMin;
                } else if ((outcodeFora & RIGHT) != 0) {
                    if (x1 != x0) y = y0 + (y1 - y0) * (xMax - x0) / (x1 - x0);
                    else y = y0;
                    x = xMax;
                } else if ((outcodeFora & LEFT) != 0) {
                    if (x1 != x0) y = y0 + (y1 - y0) * (xMin - x0) / (x1 - x0);
                    else y = y0;
                    x = xMin;
                }

                if (outcodeFora == outcode0) {
                    x0 = x; y0 = y;
                    outcode0 = computarOutCode(x0, y0, xMin, xMax, yMin, yMax);
                } else {
                    x1 = x; y1 = y;
                    outcode1 = computarOutCode(x1, y1, xMin, xMax, yMin, yMax);
                }
            }
        }

        if (aceito) {
            return new Point2D.Double[]{new Point2D.Double(x0, y0), new Point2D.Double(x1, y1)};
        }

        return null;
    }

    public static Point2D.Double aplicarTransformacao(Point2D.Double ponto, double[][] matriz) {
        double[] vetor = {ponto.x, ponto.y, 1};
        double[] resultado = multiplicarMatrizVetor(matriz, vetor);
        return new Point2D.Double(resultado[0], resultado[1]);
    }
}