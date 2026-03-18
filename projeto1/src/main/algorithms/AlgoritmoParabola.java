package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoParabola {

    // Desenha uma parábola do tipo y^2 = 4ax (eixo de simetria horizontal)
    public static List<Point> pontoMedio(int xc, int yc, int a, int limiteY) {
        List<Point> pontos = new ArrayList<>();

        int x = 0;
        int y = 0;
        double p;

        adicionarSimetria(pontos, xc, yc, x, y);

        // ============================================================
        // Região 1: Inclinação dy/dx > 1 (Variação em Y é mais rápida)
        // ============================================================
        // f(x,y) = y^2 - 4ax. Ponto médio inicial (x+1/2, y+1)
        p = 1.0 - 2.0 * a;

        while (y < 2 * a && y <= limiteY) {
            y++;
            if (p < 0) {
                p += 2 * y + 1;
            } else {
                x++;
                p += 2 * y + 1 - 4 * a;
            }
            adicionarSimetria(pontos, xc, yc, x, y);
        }

        // ============================================================
        // Região 2: Inclinação dy/dx < 1 (Variação em X é mais rápida)
        // ============================================================
        // Substituindo Math.pow por cálculo direto para maior performance
        // p = (y + 0.5)^2 - 4 * a * (x + 1)
        p = (y + 0.5) * (y + 0.5) - 4.0 * a * (x + 1);

        while (y <= limiteY) {
            x++;
            if (p > 0) {
                p -= 4 * a;
            } else {
                y++;
                p += 2 * y - 4 * a;
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