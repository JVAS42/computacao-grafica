package main.algorithms;

import java.util.ArrayList;
import java.util.List;

public class CohenSutherland {

    // Códigos de Região (em binário)
    public static final int INSIDE = 0; // 0000
    public static final int LEFT   = 1; // 0001
    public static final int RIGHT  = 2; // 0010
    public static final int BOTTOM = 4; // 0100
    public static final int TOP    = 8; // 1000

    public static class StepInfo {
        public double x1, y1, x2, y2;
        public String code1, code2, action;
        public StepInfo(double x1, double y1, double x2, double y2, int c1, int c2, String action) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.code1 = codeToBits(c1); this.code2 = codeToBits(c2);
            this.action = action;
        }
    }

    public static class ClipResult {
        public boolean accept;
        public double x1, y1, x2, y2;
        public List<StepInfo> steps = new ArrayList<>();
    }

    public static int computeRegionCode(double x, double y, double xMin, double xMax, double yMin, double yMax) {
        int code = INSIDE;
        if (x < xMin) code |= LEFT;
        else if (x > xMax) code |= RIGHT;
        // No Swing/Canvas padrão, Y cresce para baixo. Então y < yMin é o topo visual, mas seguiremos a lógica do seu JS:
        if (y < yMin) code |= BOTTOM;
        else if (y > yMax) code |= TOP;
        return code;
    }

    public static String codeToBits(int code) {
        return String.format("%4s", Integer.toBinaryString(code)).replace(' ', '0');
    }

    public static ClipResult clipLine(double x1, double y1, double x2, double y2, double xMin, double xMax, double yMin, double yMax) {
        ClipResult result = new ClipResult();
        int code1 = computeRegionCode(x1, y1, xMin, xMax, yMin, yMax);
        int code2 = computeRegionCode(x2, y2, xMin, xMax, yMin, yMax);
        boolean accept = false;

        result.steps.add(new StepInfo(x1, y1, x2, y2, code1, code2, "Inicial"));

        while (true) {
            if ((code1 | code2) == 0) {
                accept = true;
                result.steps.add(new StepInfo(x1, y1, x2, y2, code1, code2, "Aceita (dentro)"));
                break;
            } else if ((code1 & code2) != 0) {
                result.steps.add(new StepInfo(x1, y1, x2, y2, code1, code2, "Rejeitada (fora)"));
                break;
            }

            double x = 0, y = 0;
            int codeOut = (code1 != 0) ? code1 : code2;
            String action = "";

            if ((codeOut & TOP) != 0) {
                x = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
                y = yMax;
                action = "Recorte TOP";
            } else if ((codeOut & BOTTOM) != 0) {
                x = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
                y = yMin;
                action = "Recorte BOTTOM";
            } else if ((codeOut & RIGHT) != 0) {
                y = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
                x = xMax;
                action = "Recorte RIGHT";
            } else if ((codeOut & LEFT) != 0) {
                y = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
                x = xMin;
                action = "Recorte LEFT";
            }

            if (codeOut == code1) {
                result.steps.add(new StepInfo(x1, y1, x2, y2, code1, code2, action));
                x1 = x;
                y1 = y;
                code1 = computeRegionCode(x1, y1, xMin, xMax, yMin, yMax);
            } else {
                result.steps.add(new StepInfo(x1, y1, x2, y2, code1, code2, action));
                x2 = x;
                y2 = y;
                code2 = computeRegionCode(x2, y2, xMin, xMax, yMin, yMax);
            }
        }

        result.accept = accept;
        result.x1 = x1; result.y1 = y1;
        result.x2 = x2; result.y2 = y2;
        return result;
    }
}
