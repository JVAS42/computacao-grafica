package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoRetas {

    // ===============================
    // Algoritmo DDA
    // ===============================
    public static List<Point> dda(int x1, int y1, int x2, int y2) {
        List<Point> pontos = new ArrayList<>();

        int length = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));

        // Evita divisão por zero se os pontos forem iguais
        if (length == 0) {
            pontos.add(new Point(x1, y1));
            return pontos;
        }

        float xinc = (float) (x2 - x1) / length;
        float yinc = (float) (y2 - y1) / length;

        float x = x1;
        float y = y1;

        pontos.add(new Point(Math.round(x), Math.round(y)));

        for (int i = 0; i < length; i++) {
            x += xinc;
            y += yinc;
            pontos.add(new Point(Math.round(x), Math.round(y)));
        }

        return pontos;
    }

    // ===============================
    // Algoritmo do Ponto Médio (Bresenham) - 8 oitantes
    // ===============================
    public static List<Point> pontoMedio(int x1, int y1, int x2, int y2) {
        List<Point> pontos = new ArrayList<>();

        int dx = x2 - x1;
        int dy = y2 - y1;

        int x = x1;
        int y = y1;

        int sx = dx >= 0 ? 1 : -1;
        int sy = dy >= 0 ? 1 : -1;

        dx = Math.abs(dx);
        dy = Math.abs(dy);

        pontos.add(new Point(x, y));

        if (dx > dy) {
            int d = 2 * dy - dx;
            int incE = 2 * dy;
            int incNE = 2 * (dy - dx);

            for (int i = 0; i < dx; i++) {
                if (d <= 0) {
                    d += incE;
                    x += sx;
                } else {
                    d += incNE;
                    x += sx;
                    y += sy;
                }
                pontos.add(new Point(x, y));
            }
        } else {
            int d = 2 * dx - dy;
            int incE = 2 * dx;
            int incNE = 2 * (dx - dy);

            for (int i = 0; i < dy; i++) {
                if (d <= 0) {
                    d += incE;
                    y += sy;
                } else {
                    d += incNE;
                    y += sy;
                    x += sx;
                }
                pontos.add(new Point(x, y));
            }
        }

        return pontos;
    }
}
