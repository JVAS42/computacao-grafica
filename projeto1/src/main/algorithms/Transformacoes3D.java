package main.algorithms;

public class Transformacoes3D {

    public static double[][] multiply(double[][] a, double[][] b) {
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

    public static double[] multiplyVector(double[][] matrix, double[] vector) {
        double[] result = new double[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }

    public static double[][] translation(double tx, double ty, double tz) {
        return new double[][] {
                {1, 0, 0, tx}, {0, 1, 0, ty}, {0, 0, 1, tz}, {0, 0, 0, 1}
        };
    }

    public static double[][] scaling(double sx, double sy, double sz) {
        return new double[][] {
                {sx, 0, 0, 0}, {0, sy, 0, 0}, {0, 0, sz, 0}, {0, 0, 0, 1}
        };
    }

    public static double[][] rotationX(double angleDegrees) {
        double rad = Math.toRadians(angleDegrees);
        double c = Math.cos(rad); double s = Math.sin(rad);
        return new double[][] {
                {1, 0, 0, 0}, {0, c, -s, 0}, {0, s, c, 0}, {0, 0, 0, 1}
        };
    }

    public static double[][] rotationY(double angleDegrees) {
        double rad = Math.toRadians(angleDegrees);
        double c = Math.cos(rad); double s = Math.sin(rad);
        return new double[][] {
                {c, 0, s, 0}, {0, 1, 0, 0}, {-s, 0, c, 0}, {0, 0, 0, 1}
        };
    }

    public static double[][] rotationZ(double angleDegrees) {
        double rad = Math.toRadians(angleDegrees);
        double c = Math.cos(rad); double s = Math.sin(rad);
        return new double[][] {
                {c, -s, 0, 0}, {s, c, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}
        };
    }

    public static double[][] shear(double shXY, double shXZ, double shYZ) {
        return new double[][] {
                {1, shXY, shXZ, 0},
                {0, 1, shYZ, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
    }

    public static double[][] reflection(String axis) {
        double[][] mat = {
                {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}
        };
        if(axis.equals("XY")) mat[2][2] = -1;
        if(axis.equals("XZ")) mat[1][1] = -1;
        if(axis.equals("YZ")) mat[0][0] = -1;
        return mat;
    }

    // =========================================================================
    // LÓGICA ATUALIZADA: PROJEÇÃO PARALELA ISOMÉTRICA (TEORIA CLÁSSICA)
    // =========================================================================
    public static double[] projectIsometric(double[] vertex3D, double viewRotX, double viewRotY, double viewRotZ, double zoom) {

        // 1. Aplica as transformações da câmera (sliders da visualização interativa) e o zoom
        double[][] camRotX = rotationX(viewRotX);
        double[][] camRotY = rotationY(viewRotY);
        double[][] camRotZ = rotationZ(viewRotZ);

        double f = zoom / 100.0;
        double[][] zoomMat = scaling(f, f, f);

        // Multiplica a matriz da câmera: Zoom * RotZ * RotY * RotX
        double[][] viewMat = multiply(camRotZ, multiply(camRotY, camRotX));
        viewMat = multiply(zoomMat, viewMat);

        // Vértice local com as rotações da câmera interativa aplicadas
        double[] v = multiplyVector(viewMat, new double[]{vertex3D[0], vertex3D[1], vertex3D[2], 1});

        // ---------------------------------------------------------------------
        // 2. MATRIZ DE PROJEÇÃO ISOMÉTRICA
        // Conforme a literatura, rotaciona 45° em Y e ~35.264° em X
        // ---------------------------------------------------------------------
        double[][] isoRotY = rotationY(45.0);
        double[][] isoRotX = rotationX(35.26438968); // Este valor é o arco-seno da tangente de 30°

        // Composição isométrica: Rotação X * Rotação Y
        double[][] matrizIsometrica = multiply(isoRotX, isoRotY);

        // Aplica a matriz de projeção isométrica ao vértice final
        double[] vProjetado = multiplyVector(matrizIsometrica, v);

        // Retorna as coordenadas X e Y projetadas ortogonalmente no plano (descartando Z)
        return new double[]{vProjetado[0], vProjetado[1]};
    }

    public static double[] mapToViewport(double xWorld, double yWorld,
                                         double wXMin, double wYMin, double wXMax, double wYMax,
                                         double vpXMin, double vpYMin, double vpXMax, double vpYMax) {

        double xVp = ((xWorld - wXMin) / (wXMax - wXMin)) * (vpXMax - vpXMin) + vpXMin;

        // Inversão do eixo Y: subtraímos de vpYMax para que o Y do mundo suba e o da tela desça
        double yVp = vpYMax - ((yWorld - wYMin) / (wYMax - wYMin)) * (vpYMax - vpYMin);

        return new double[]{xVp, yVp};
    }

    public static double[] cohenSutherlandClip(double x0, double y0, double x1, double y1,
                                               double xMin, double yMin, double xMax, double yMax) {
        int outcode0 = computeOutCode(x0, y0, xMin, yMin, xMax, yMax);
        int outcode1 = computeOutCode(x1, y1, xMin, yMin, xMax, yMax);
        boolean accept = false;

        while (true) {
            if ((outcode0 | outcode1) == 0) {
                accept = true;
                break;
            } else if ((outcode0 & outcode1) != 0) {
                break;
            } else {
                double x = 0, y = 0;
                int outcodeOut = (outcode0 != 0) ? outcode0 : outcode1;

                if ((outcodeOut & 8) != 0) { y = yMax; x = x0 + (x1 - x0) * (yMax - y0) / (y1 - y0); }
                else if ((outcodeOut & 4) != 0) { y = yMin; x = x0 + (x1 - x0) * (yMin - y0) / (y1 - y0); }
                else if ((outcodeOut & 2) != 0) { x = xMax; y = y0 + (y1 - y0) * (xMax - x0) / (x1 - x0); }
                else if ((outcodeOut & 1) != 0) { x = xMin; y = y0 + (y1 - y0) * (xMin - x0) / (x1 - x0); }

                if (outcodeOut == outcode0) {
                    x0 = x; y0 = y;
                    outcode0 = computeOutCode(x0, y0, xMin, yMin, xMax, yMax);
                } else {
                    x1 = x; y1 = y;
                    outcode1 = computeOutCode(x1, y1, xMin, yMin, xMax, yMax);
                }
            }
        }
        return accept ? new double[]{x0, y0, x1, y1} : null;
    }

    private static int computeOutCode(double x, double y, double xMin, double yMin, double xMax, double yMax) {
        int code = 0;
        if (x < xMin) code |= 1; else if (x > xMax) code |= 2;
        if (y < yMin) code |= 4; else if (y > yMax) code |= 8;
        return code;
    }
}