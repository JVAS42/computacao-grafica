package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoElipse {

    public static List<Point> pontoMedio(int xc, int yc, int rx, int ry) {
        List<Point> pontos = new ArrayList<>();

        int rx2 = rx * rx;
        int ry2 = ry * ry;
        int doisRx2 = 2 * rx2;
        int doisRy2 = 2 * ry2;

        int x = 0;
        int y = ry;
        int px = 0;
        int py = doisRx2 * y;

        adicionarSimetria(pontos, xc, yc, x, y);

        // Região 1 (Inclinação < 1)
        double p1 = ry2 - (rx2 * ry) + (0.25 * rx2);
        while (px < py) {
            x++;
            px += doisRy2;
            if (p1 < 0) {
                p1 += ry2 + px;
            } else {
                y--;
                py -= doisRx2;
                p1 += ry2 + px - py;
            }
            adicionarSimetria(pontos, xc, yc, x, y);
        }

        // Região 2 (Inclinação >= 1)
        double p2 = (double)ry2 * (x + 0.5) * (x + 0.5) + (double)rx2 * (y - 1) * (y - 1) - (double)rx2 * ry2;
        while (y > 0) {
            y--;
            py -= doisRx2;
            if (p2 > 0) {
                p2 += rx2 - py;
            } else {
                x++;
                px += doisRy2;
                p2 += rx2 - py + px;
            }
            adicionarSimetria(pontos, xc, yc, x, y);
        }

        return pontos;
    }

    private static void adicionarSimetria(List<Point> pontos, int xc, int yc, int x, int y) {
        pontos.add(new Point(xc + x, yc + y));
        pontos.add(new Point(xc - x, yc + y));
        pontos.add(new Point(xc + x, yc - y));
        pontos.add(new Point(xc - x, yc - y));
    }
}