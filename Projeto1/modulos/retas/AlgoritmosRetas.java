package Projeto1.modulos.retas;// Tudo minúsculo para evitar confusão

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlgoritmosRetas {

    public static List<Point> calcularDDA(int x1, int y1, int x2, int y2) {
        List<Point> pontos = new ArrayList<>();
        float dx = x2 - x1;
        float dy = y2 - y1;
        float passos = Math.max(Math.abs(dx), Math.abs(dy));
        float xInc = dx / passos;
        float yInc = dy / passos;
        float x = x1, y = y1;

        for (int i = 0; i <= passos; i++) {
            pontos.add(new Point(Math.round(x), Math.round(y)));
            x += xInc;
            y += yInc;
        }
        return pontos;
    }

    public static List<Point> calcularPontoMedio(int x1, int y1, int x2, int y2) {
        List<Point> pontos = new ArrayList<>();
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            pontos.add(new Point(x1, y1));
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
        return pontos;
    }
}