package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmoCircunferencia {

    // ===============================
    // Algoritmo da Equação Explícita
    // ===============================
    public static List<Point> equacaoExplicita(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();

        for (int x = -r; x <= r; x++) {
            int y = (int) Math.round(Math.sqrt(r * r - x * x));
            pontos.add(new Point(xc + x, yc + y));
            pontos.add(new Point(xc + x, yc - y));
        }

        return pontos;
    }

    // ===============================
    // Algoritmo Trigonométrico
    // ===============================
    public static List<Point> trigonometrico(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        double step = 0.1;

        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            int x = (int) Math.round(xc + r * Math.cos(theta));
            int y = (int) Math.round(yc + r * Math.sin(theta));
            pontos.add(new Point(x, y));
        }

        return pontos;
    }

    // ===============================
    // Algoritmo do Ponto Médio
    // ===============================
    public static List<Point> pontoMedio(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        int x = 0;
        int y = r;
        int p = 1 - r;

        while (x <= y) {
            // Adiciona os 8 pontos simétricos
            pontos.add(new Point(xc + x, yc + y));
            pontos.add(new Point(xc - x, yc + y));
            pontos.add(new Point(xc + x, yc - y));
            pontos.add(new Point(xc - x, yc - y));
            pontos.add(new Point(xc + y, yc + x));
            pontos.add(new Point(xc - y, yc + x));
            pontos.add(new Point(xc + y, yc - x));
            pontos.add(new Point(xc - y, yc - x));

            if (p < 0) {
                p = p + 2 * x + 3;
            } else {
                p = p + 2 * (x - y) + 5;
                y--;
            }
            x++;
        }

        return pontos;
    }
}
