package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoCircunferencia {

    public static List<Point> equacaoExplicita(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        for (int x = -r; x <= r; x++) {
            int y = (int) Math.round(Math.sqrt(r * r - x * x));
            pontos.add(new Point(xc + x, yc + y));
            pontos.add(new Point(xc + x, yc - y));
        }
        return pontos;
    }

    public static List<Point> trigonometrico(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        if (r == 0) {
            pontos.add(new Point(xc, yc));
            return pontos;
        }

        // Passo dinâmico: garante precisão sem cálculos excessivos
        double step = 1.0 / r;

        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            int x = (int) Math.round(xc + r * Math.cos(theta));
            int y = (int) Math.round(yc + r * Math.sin(theta));
            pontos.add(new Point(x, y));
        }
        return pontos;
    }

    public static List<Point> pontoMedio(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        int x = 0;
        int y = r;
        int p = 1 - r; // Valor inicial conforme imagem image_f753be.png

        adicionarPontosCirculo(xc, yc, x, y, pontos);

        while (x < y) {
            x++;
            if (p < 0) {
                p += 2 * x + 1;
            } else {
                y--;
                p += 2 * (x - y) + 1;
            }
            adicionarPontosCirculo(xc, yc, x, y, pontos);
        }
        return pontos;
    }

    // Simetria de 8 octantes conforme imagem image_f753b6.png
    private static void adicionarPontosCirculo(int xc, int yc, int x, int y, List<Point> pontos) {
        pontos.add(new Point(xc + x, yc + y));
        pontos.add(new Point(xc - x, yc + y));
        pontos.add(new Point(xc + x, yc - y));
        pontos.add(new Point(xc - x, yc - y));
        pontos.add(new Point(xc + y, yc + x));
        pontos.add(new Point(xc - y, yc + x));
        pontos.add(new Point(xc + y, yc - x));
        pontos.add(new Point(xc - y, yc - x));
    }
}
