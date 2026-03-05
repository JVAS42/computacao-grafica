package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoParabola {

    // Desenha uma parábola do tipo y^2 = 4ax (eixo de simetria horizontal)
    // LimiteY define até onde a curva deve ser desenhada
    public static List<Point> pontoMedio(int xc, int yc, int a, int limiteY) {
        List<Point> pontos = new ArrayList<>();

        int x = 0;
        int y = 0;
        double p;

        adicionarSimetria(pontos, xc, yc, x, y);

        // Região 1
        p = 1 - 2 * a;
        while (y < 2 * a && y <= limiteY) {
            y++;
            if (p < 0) {
                p += 2 * y + 3;
            } else {
                p += 2 * y + 3 - 4 * a;
                x++;
            }
            adicionarSimetria(pontos, xc, yc, x, y);
        }

        // Região 2
        p = Math.pow(y + 0.5, 2) - 4 * a * (x + 1);
        while (y <= limiteY) {
            x++;
            if (p > 0) {
                p -= 4 * a;
            } else {
                p += 2 * y + 2 - 4 * a;
                y++;
            }
            adicionarSimetria(pontos, xc, yc, x, y);
        }

        return pontos;
    }

    private static void adicionarSimetria(List<Point> pontos, int xc, int yc, int x, int y) {
        pontos.add(new Point(xc + x, yc + y)); // Parte superior
        pontos.add(new Point(xc + x, yc - y)); // Reflexo inferior
    }
}