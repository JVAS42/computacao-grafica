package main.ui.panels;

import main.algorithms.Bezier;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BezierPanel extends JPanel {
    private List<Point> controlPoints;
    private Bezier bezierAlgo;
    private JTextField[] fieldsX = new JTextField[4];
    private JTextField[] fieldsY = new JTextField[4];
    private JTextArea logArea;
    private JPanel canvas;

    public BezierPanel() {
        this.bezierAlgo = new Bezier();
        this.controlPoints = new ArrayList<>();
        setLayout(new BorderLayout());

        // Inicializa pontos padrão
        int[][] defaults = {{-200, -100}, {-100, 200}, {100, -200}, {200, 100}};
        for (int[] p : defaults) controlPoints.add(new Point(p[0], p[1]));

        setupUI();
    }

    private void setupUI() {
        // Barra Lateral Esquerda (Inputs)
        JPanel sidebarLeft = new JPanel();
        sidebarLeft.setPreferredSize(new Dimension(180, 0));
        sidebarLeft.setLayout(new BoxLayout(sidebarLeft, BoxLayout.Y_AXIS));
        sidebarLeft.setBorder(BorderFactory.createTitledBorder("Entradas"));

        for (int i = 0; i < 4; i++) {
            sidebarLeft.add(new JLabel("Ponto P" + i));
            fieldsX[i] = new JTextField(String.valueOf(controlPoints.get(i).x));
            fieldsY[i] = new JTextField(String.valueOf(controlPoints.get(i).y));
            sidebarLeft.add(new JLabel(" X:")); sidebarLeft.add(fieldsX[i]);
            sidebarLeft.add(new JLabel(" Y:")); sidebarLeft.add(fieldsY[i]);
            sidebarLeft.add(Box.createVerticalStrut(10));
        }

        JButton btnDraw = new JButton("Desenhar");
        btnDraw.setBackground(new Color(92, 184, 92));
        btnDraw.setForeground(Color.WHITE);
        btnDraw.addActionListener(e -> atualizarCurva());
        sidebarLeft.add(btnDraw);

        // Canvas Central
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharTudo(g);
            }
        };
        canvas.setBackground(Color.WHITE);

        // Barra Lateral Direita (Log)
        logArea = new JTextArea();
        logArea.setEditable(false);
        JPanel sidebarRight = new JPanel(new BorderLayout());
        sidebarRight.setPreferredSize(new Dimension(180, 0));
        sidebarRight.add(new JLabel(" Pontos da Curva:"), BorderLayout.NORTH);
        sidebarRight.add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(sidebarLeft, BorderLayout.WEST);
        add(canvas, BorderLayout.CENTER);
        add(sidebarRight, BorderLayout.EAST);
    }

    private void atualizarCurva() {
        try {
            controlPoints.clear();
            logArea.setText("Log de Coordenadas:\n");
            for (int i = 0; i < 4; i++) {
                int x = Integer.parseInt(fieldsX[i].getText());
                int y = Integer.parseInt(fieldsY[i].getText());
                controlPoints.add(new Point(x, y));
                logArea.append("P" + i + ": (" + x + ", " + y + ")\n");
            }
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Insira apenas números inteiros!");
        }
    }

    private void desenharTudo(Graphics g) {
        int cx = canvas.getWidth() / 2;
        int cy = canvas.getHeight() / 2;

        // Eixos
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(cx, 0, cx, canvas.getHeight());
        g.drawLine(0, cy, canvas.getWidth(), cy);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Curva
        g2d.setColor(Color.RED);
        List<Point> curve = bezierAlgo.generateBezier(controlPoints, 500);
        for (int i = 0; i < curve.size() - 1; i++) {
            g2d.drawLine(cx + curve.get(i).x, cy - curve.get(i).y,
                    cx + curve.get(i+1).x, cy - curve.get(i+1).y);
        }

        // Pontos de Controle
        g2d.setColor(Color.BLUE);
        for (Point p : controlPoints) {
            g2d.fillOval(cx + p.x - 4, cy - p.y - 4, 8, 8);
        }
    }
}