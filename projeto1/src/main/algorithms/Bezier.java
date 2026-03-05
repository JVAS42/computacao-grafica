package main.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Bezier {
    public List<Point> generateBezier(List<Point> controlPoints, int segments) {
        List<Point> curvePoints = new ArrayList<>();
        if (controlPoints.size() < 4) return curvePoints;

        Point p0 = controlPoints.get(0);
        Point p1 = controlPoints.get(1);
        Point p2 = controlPoints.get(2);
        Point p3 = controlPoints.get(3);

        for (int i = 0; i <= segments; i++) {
            double u = (double) i / segments;

            // Fórmula da Bézier Cúbica: (1-u)^3*P0 + 3u(1-u)^2*P1 + 3u^2(1-u)*P2 + u^3*P3
            double b0 = Math.pow(1 - u, 3);
            double b1 = 3 * u * Math.pow(1 - u, 2);
            double b2 = 3 * Math.pow(u, 2) * (1 - u);
            double b3 = Math.pow(u, 3);

            int x = (int) (b0 * p0.x + b1 * p1.x + b2 * p2.x + b3 * p3.x);
            int y = (int) (b0 * p0.y + b1 * p1.y + b2 * p2.y + b3 * p3.y);

            curvePoints.add(new Point(x, y));
        }
        return curvePoints;
    }
}