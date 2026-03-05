package main.algorithms;

import java.util.ArrayList;
import java.util.List;

public class SutherlandHodgman {

    public static class Ponto {
        public double x, y;
        public Ponto(double x, double y) { this.x = x; this.y = y; }
    }

    // Função principal de recorte de polígono
    public static List<Ponto> clipPolygon(List<Ponto> poligono, double xMin, double xMax, double yMin, double yMax) {
        List<Ponto> resultado = poligono;

        // Recorta contra as 4 bordas (0: Esquerda, 1: Direita, 2: Fundo/Top, 3: Topo/Bottom)
        resultado = clipEdge(resultado, xMin, xMax, yMin, yMax, 0);
        resultado = clipEdge(resultado, xMin, xMax, yMin, yMax, 1);
        resultado = clipEdge(resultado, xMin, xMax, yMin, yMax, 2);
        resultado = clipEdge(resultado, xMin, xMax, yMin, yMax, 3);

        return resultado;
    }

    private static List<Ponto> clipEdge(List<Ponto> poligono, double xMin, double xMax, double yMin, double yMax, int edge) {
        List<Ponto> clipped = new ArrayList<>();
        if (poligono.isEmpty()) return clipped;

        Ponto p1 = poligono.get(poligono.size() - 1); // Começa com o último ponto

        for (Ponto p2 : poligono) {
            boolean p1Dentro = isInside(p1, xMin, xMax, yMin, yMax, edge);
            boolean p2Dentro = isInside(p2, xMin, xMax, yMin, yMax, edge);

            if (p1Dentro && p2Dentro) {
                // Ambos dentro: adiciona p2
                clipped.add(p2);
            } else if (p1Dentro && !p2Dentro) {
                // Saindo: adiciona interseção
                clipped.add(getIntersection(p1, p2, xMin, xMax, yMin, yMax, edge));
            } else if (!p1Dentro && p2Dentro) {
                // Entrando: adiciona interseção e depois p2
                clipped.add(getIntersection(p1, p2, xMin, xMax, yMin, yMax, edge));
                clipped.add(p2);
            }
            // Se ambos estiverem fora, não faz nada

            p1 = p2;
        }
        return clipped;
    }

    private static boolean isInside(Ponto p, double xMin, double xMax, double yMin, double yMax, int edge) {
        switch (edge) {
            case 0: return p.x >= xMin; // Esquerda
            case 1: return p.x <= xMax; // Direita
            case 2: return p.y >= yMin; // Topo (yMin visual)
            case 3: return p.y <= yMax; // Fundo (yMax visual)
        }
        return false;
    }

    private static Ponto getIntersection(Ponto p1, Ponto p2, double xMin, double xMax, double yMin, double yMax, int edge) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double m = (dx == 0) ? 0 : dy / dx;

        double x = 0, y = 0;
        switch (edge) {
            case 0: // Esquerda
                x = xMin;
                y = p1.y + m * (xMin - p1.x);
                break;
            case 1: // Direita
                x = xMax;
                y = p1.y + m * (xMax - p1.x);
                break;
            case 2: // Topo
                y = yMin;
                x = (dy == 0) ? p1.x : p1.x + (yMin - p1.y) / m;
                break;
            case 3: // Fundo
                y = yMax;
                x = (dy == 0) ? p1.x : p1.x + (yMax - p1.y) / m;
                break;
        }
        return new Ponto(x, y);
    }
}