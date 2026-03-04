package Projeto1.modulos.circuferencia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmosCirculo {

    // 1. Equação Explícita: y = sqrt(r² - x²)
    public static List<Point> calcularEquacaoExplicita(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        for (int x = -r; x <= r; x++) {
            int y = (int) Math.round(Math.sqrt(r * r - x * x));
            pontos.add(new Point(xc + x, yc + y));
            pontos.add(new Point(xc + x, yc - y));
        }
        return pontos;
    }

    // 2. Trigonométrico: x = r * cos(θ), y = r * sin(θ)
    public static List<Point> calcularTrigonometrico(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        double step = 0.1; // Passo do ângulo
        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            int x = (int) Math.round(xc + r * Math.cos(theta));
            int y = (int) Math.round(yc + r * Math.sin(theta));
            pontos.add(new Point(x, y));
        }
        return pontos;
    }

    // 3. Ponto Médio (Bresenham para Círculos)
    public static List<Point> calcularPontoMedio(int xc, int yc, int r) {
        List<Point> pontos = new ArrayList<>();
        int x = 0;
        int y = r;
        int p = 1 - r;

        adicionarOitoPontos(pontos, xc, yc, x, y);

        while (x < y) {
            x++;
            if (p < 0) {
                p += 2 * x + 1;
            } else {
                y--;
                p += 2 * (x - y) + 1;
            }
            adicionarOitoPontos(pontos, xc, yc, x, y);
        }
        return pontos;
    }

    private static void adicionarOitoPontos(List<Point> lista, int xc, int yc, int x, int y) {
        lista.add(new Point(xc + x, yc + y));
        lista.add(new Point(xc - x, yc + y));
        lista.add(new Point(xc + x, yc - y));
        lista.add(new Point(xc - x, yc - y));
        lista.add(new Point(xc + y, yc + x));
        lista.add(new Point(xc - y, yc + x));
        lista.add(new Point(xc + y, yc - x));
        lista.add(new Point(xc - y, yc - x));
    }
}