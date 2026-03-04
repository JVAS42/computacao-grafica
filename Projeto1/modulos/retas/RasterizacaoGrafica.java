package Projeto1.modulos.retas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class RasterizacaoGrafica extends JFrame {

    // --- Componentes de Dados ---
    class Reta {
        int x1, y1, x2, y2;
        String algoritmo;
        Reta(int x1, int y1, int x2, int y2, String algoritmo) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.algoritmo = algoritmo;
        }
    }

    private List<Reta> listaRetas = new ArrayList<>();
    private String algoritmoAtual = "DDA";

    // --- Componentes de UI ---
    private CanvasPainel canvas;
    private JTextField txtX1, txtY1, txtX2, txtY2;
    private JLabel lblMouseCoords, lblQuadrante;
    private JLabel lblRetaX1, lblRetaY1, lblRetaX2, lblRetaY2;
    private JPanel painelLogs; // Para a lista de pontos calculados

    public RasterizacaoGrafica() {
        setTitle("Computação Gráfica - Rasterização");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. MENU SUPERIOR
        setJMenuBar(criarMenuBar());

        // 2. CANVAS (CENTRO)
        canvas = new CanvasPainel();
        add(canvas, BorderLayout.CENTER);

        // 3. BARRA LATERAL (DIREITA)
        add(criarPainelLateral(), BorderLayout.EAST);

        setLocationRelativeTo(null);
    }

    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("Sistemas de Coordenadas"));
        menuBar.add(new JMenu("Retas"));
        menuBar.add(new JMenu("Circunferência"));
        menuBar.add(new JMenu("2D"));
        menuBar.add(new JMenu("3D"));
        menuBar.add(new JMenu("Recorte Cohen-Sutherland"));
        return menuBar;
    }

    private JPanel criarPainelLateral() {
        JPanel painel = new JPanel();
        painel.setPreferredSize(new Dimension(300, 0));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Seção: Informações do Plano
        painel.add(new JLabel("<html><b>Informações Do Plano</b></html>"));
        painel.add(new JSeparator());
        lblMouseCoords = new JLabel("Coordenada: (0, 0)");
        lblQuadrante = new JLabel("Quadrante: -");
        painel.add(lblMouseCoords);
        painel.add(lblQuadrante);
        painel.add(Box.createVerticalStrut(20));

        // Seção: Inputs de Desenho
        painel.add(new JLabel("Algoritmo:"));
        JComboBox<String> combo = new JComboBox<>(new String[]{"DDA", "Ponto Médio"});
        combo.addActionListener(e -> algoritmoAtual = (String) combo.getSelectedItem());
        painel.add(combo);

        txtX1 = new JTextField(); txtY1 = new JTextField();
        txtX2 = new JTextField(); txtY2 = new JTextField();
        
        painel.add(new JLabel("X Inicial:")); painel.add(txtX1);
        painel.add(new JLabel("Y Inicial:")); painel.add(txtY1);
        painel.add(new JLabel("X Final:")); painel.add(txtX2);
        painel.add(new JLabel("Y Final:")); painel.add(txtY2);

        JButton btnDesenhar = new JButton("Desenhar");
        btnDesenhar.setBackground(new Color(76, 175, 80));
        btnDesenhar.setForeground(Color.WHITE);
        btnDesenhar.addActionListener(e -> acaoDesenhar());
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnDesenhar);

        // Seção: Informações da Reta Atual (Tabela de pontos)
        painel.add(Box.createVerticalStrut(20));
        painel.add(new JLabel("<html><b>Informações Da Reta Atual</b></html>"));
        painel.add(new JSeparator());
        
        lblRetaX1 = new JLabel("X Inicial: -"); lblRetaY1 = new JLabel("Y Inicial: -");
        lblRetaX2 = new JLabel("X Final: -"); lblRetaY2 = new JLabel("Y Final: -");
        painel.add(lblRetaX1); painel.add(lblRetaY1);
        painel.add(lblRetaX2); painel.add(lblRetaY2);

        // Área de scroll para os pontos calculados (X1, Y1, X2, Y2...)
        painelLogs = new JPanel();
        painelLogs.setLayout(new BoxLayout(painelLogs, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(painelLogs);
        scroll.setPreferredSize(new Dimension(280, 200));
        painel.add(scroll);

        return painel;
    }

    private void acaoDesenhar() {
        try {
            int x1 = Integer.parseInt(txtX1.getText());
            int y1 = Integer.parseInt(txtY1.getText());
            int x2 = Integer.parseInt(txtX2.getText());
            int y2 = Integer.parseInt(txtY2.getText());
            
            listaRetas.add(new Reta(x1, y1, x2, y2, algoritmoAtual));
            atualizarLabelsReta(x1, y1, x2, y2);
            canvas.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira valores numéricos válidos.");
        }
    }

    private void atualizarLabelsReta(int x1, int y1, int x2, int y2) {
        lblRetaX1.setText("X Inicial: " + x1); lblRetaY1.setText("Y Inicial: " + y1);
        lblRetaX2.setText("X Final: " + x2); lblRetaY2.setText("Y Final: " + y2);
        
        // Simulação de preenchimento da lista de pontos na lateral
        painelLogs.removeAll();
        painelLogs.add(new JLabel("Ponto calculado: (" + x1 + ", " + y1 + ")"));
        painelLogs.revalidate();
    }

    // --- Subclasse do Painel de Desenho ---
    class CanvasPainel extends JPanel {
        public CanvasPainel() {
            setBackground(Color.WHITE);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int x = e.getX() - getWidth() / 2;
                    int y = getHeight() / 2 - e.getY();
                    lblMouseCoords.setText("Coordenada: (" + x + ", " + y + ")");
                    lblQuadrante.setText("Quadrante: " + identificarQuadrante(x, y));
                }
            });
        }

        private String identificarQuadrante(int x, int y) {
            if (x > 0 && y > 0) return "1";
            if (x < 0 && y > 0) return "2";
            if (x < 0 && y < 0) return "3";
            if (x > 0 && y < 0) return "4";
            return "Eixo";
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            desenharEixos(g);
            for (Reta r : listaRetas) {
                if (r.algoritmo.equals("DDA")) dda(g, r.x1, r.y1, r.x2, r.y2);
                else bresenham(g, r.x1, r.y1, r.x2, r.y2);
            }
        }

        private void desenharEixos(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
            g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        }

        private void putPixel(Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            g.fillRect(x + getWidth()/2, getHeight()/2 - y, 2, 2);
        }

        private void dda(Graphics g, int x1, int y1, int x2, int y2) {
            float dx = x2 - x1, dy = y2 - y1;
            float steps = Math.max(Math.abs(dx), Math.abs(dy));
            float xInc = dx / steps, yInc = dy / steps;
            float x = x1, y = y1;
            for(int i=0; i<=steps; i++) {
                putPixel(g, Math.round(x), Math.round(y));
                x += xInc; y += yInc;
            }
        }

        private void bresenham(Graphics g, int x1, int y1, int x2, int y2) {
            int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
            int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1;
            int err = dx - dy;
            while (true) {
                putPixel(g, x1, y1);
                if (x1 == x2 && y1 == y2) break;
                int e2 = 2 * err;
                if (e2 > -dy) { err -= dy; x1 += sx; }
                if (e2 < dx) { err += dx; y1 += sy; }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RasterizacaoGrafica().setVisible(true);
        });
    }
}