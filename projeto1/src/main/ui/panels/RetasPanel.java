package main.ui.panels;

import main.algorithms.AlgoritmoRetas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class RetasPanel extends JPanel {

    // Componentes da Interface (Painel Esquerdo)
    private JLabel lblCoordenadaLive, lblQuadrante;
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtX1, txtY1, txtX2, txtY2;
    private JButton btnDesenhar;

    // Componentes da Interface (Painel Direito)
    private JLabel lblInfoReta;
    private JTextArea txtAreaHistorico;
    private JButton btnLimpar;

    // Área de desenho customizada
    private CanvasPanel canvas;

    // Variáveis de controle de estado
    private int clickCount = 0;
    private int startX, startY;
    private List<LineDef> linhas = new ArrayList<>();

    public RetasPanel() {
        setLayout(new BorderLayout());

        // Inicializa as áreas principais
        setupPainelEsquerdo();
        setupPainelDireito();

        canvas = new CanvasPanel();
        add(canvas, BorderLayout.CENTER);
    }

    // ===============================
    // Configuração do Layout Esquerdo
    // ===============================
    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        painelEsquerdo.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelEsquerdo.setPreferredSize(new Dimension(200, 0));

        // Informações do Plano
        painelEsquerdo.add(new JLabel("Informações Do Plano"));
        painelEsquerdo.add(new JSeparator());
        painelEsquerdo.add(Box.createVerticalStrut(10));

        lblCoordenadaLive = new JLabel("Coordenada: (0, 0)");
        lblQuadrante = new JLabel("Quadrante: Origem");
        painelEsquerdo.add(lblCoordenadaLive);
        painelEsquerdo.add(lblQuadrante);

        painelEsquerdo.add(Box.createVerticalGlue()); // Empurra o resto para baixo

        // Controles de Entrada
        painelEsquerdo.add(new JLabel("Algoritmo:"));
        comboAlgoritmo = new JComboBox<>(new String[]{"DDA", "Ponto Médio"});
        comboAlgoritmo.addActionListener(e -> limparCanvas()); // Limpa ao trocar algoritmo
        painelEsquerdo.add(comboAlgoritmo);
        painelEsquerdo.add(Box.createVerticalStrut(10));

        txtX1 = new JTextField(5); txtY1 = new JTextField(5);
        txtX2 = new JTextField(5); txtY2 = new JTextField(5);

        painelEsquerdo.add(new JLabel("X Inicial:")); painelEsquerdo.add(txtX1);
        painelEsquerdo.add(new JLabel("Y Inicial:")); painelEsquerdo.add(txtY1);
        painelEsquerdo.add(new JLabel("X Final:")); painelEsquerdo.add(txtX2);
        painelEsquerdo.add(new JLabel("Y Final:")); painelEsquerdo.add(txtY2);

        painelEsquerdo.add(Box.createVerticalStrut(10));

        btnDesenhar = new JButton("Desenhar");
        btnDesenhar.setBackground(new Color(92, 184, 92)); // Verde similar à imagem
        btnDesenhar.setForeground(Color.WHITE);
        btnDesenhar.addActionListener(e -> desenharViaInputs());
        painelEsquerdo.add(btnDesenhar);

        add(painelEsquerdo, BorderLayout.WEST);
    }

    // ===============================
    // Configuração do Layout Direito
    // ===============================
    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelDireito.setPreferredSize(new Dimension(250, 0));

        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.add(new JLabel("Informações Da Reta Atual"));
        headerPanel.add(new JSeparator());
        lblInfoReta = new JLabel("Nenhuma reta desenhada");
        headerPanel.add(lblInfoReta);
        painelDireito.add(headerPanel, BorderLayout.NORTH);

        txtAreaHistorico = new JTextArea();
        txtAreaHistorico.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaHistorico);
        painelDireito.add(scrollPane, BorderLayout.CENTER);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBackground(new Color(92, 184, 92));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.addActionListener(e -> limparCanvas());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnLimpar);
        painelDireito.add(bottomPanel, BorderLayout.SOUTH);

        add(painelDireito, BorderLayout.EAST);
    }

    // ===============================
    // Lógica de Interação
    // ===============================
    private void limparCanvas() {
        linhas.clear();
        clickCount = 0;
        txtX1.setText(""); txtY1.setText("");
        txtX2.setText(""); txtY2.setText("");
        lblInfoReta.setText("Nenhuma reta desenhada");
        txtAreaHistorico.setText("");
        canvas.repaint();
    }

    private void desenharViaInputs() {
        try {
            int x1 = Integer.parseInt(txtX1.getText());
            int y1 = Integer.parseInt(txtY1.getText());
            int x2 = Integer.parseInt(txtX2.getText());
            int y2 = Integer.parseInt(txtY2.getText());

            linhas.add(new LineDef(x1, y1, x2, y2));
            atualizarHistorico(x1, y1, x2, y2);
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha todas as coordenadas com números inteiros válidos.");
        }
    }

    private void atualizarHistorico(int x1, int y1, int x2, int y2) {
        lblInfoReta.setText(String.format("X Ini: %d  Y Ini: %d | X Fin: %d  Y Fin: %d", x1, y1, x2, y2));

        String alg = (String) comboAlgoritmo.getSelectedItem();
        List<Point> pontosGerados;

        if ("DDA".equals(alg)) {
            pontosGerados = AlgoritmoRetas.dda(x1, y1, x2, y2);
        } else {
            pontosGerados = AlgoritmoRetas.pontoMedio(x1, y1, x2, y2);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pontosGerados.size(); i++) {
            Point p = pontosGerados.get(i);
            sb.append(String.format("X%d: %d \t Y%d: %d\n", i+1, p.x, i+1, p.y));
        }
        txtAreaHistorico.setText(sb.toString());
        txtAreaHistorico.setCaretPosition(0); // Volta o scroll para o topo
    }

    private String getQuadrante(int x, int y) {
        if (x > 0 && y > 0) return "1";
        if (x < 0 && y > 0) return "2";
        if (x < 0 && y < 0) return "3";
        if (x > 0 && y < 0) return "4";
        if (x == 0 && y != 0) return "Eixo Y";
        if (y == 0 && x != 0) return "Eixo X";
        return "Origem";
    }

    // Classe auxiliar para armazenar as retas na memória
    private class LineDef {
        int x1, y1, x2, y2;
        public LineDef(int x1, int y1, int x2, int y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    // ===============================
    // Área de Desenho Customizada (Substitui o Canvas do HTML)
    // ===============================
    private class CanvasPanel extends JPanel {

        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // Rastreador de Mouse (Hover)
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int cartX = screenToCartesianX(e.getX());
                    int cartY = screenToCartesianY(e.getY());
                    lblCoordenadaLive.setText(String.format("Coordenada: (%d, %d)", cartX, cartY));
                    lblQuadrante.setText("Quadrante: " + getQuadrante(cartX, cartY));
                }
            });

            // Rastreador de Cliques
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cartX = screenToCartesianX(e.getX());
                    int cartY = screenToCartesianY(e.getY());

                    if (clickCount == 0) {
                        startX = cartX;
                        startY = cartY;
                        txtX1.setText(String.valueOf(startX));
                        txtY1.setText(String.valueOf(startY));
                        clickCount = 1;
                    } else {
                        txtX2.setText(String.valueOf(cartX));
                        txtY2.setText(String.valueOf(cartY));
                        linhas.add(new LineDef(startX, startY, cartX, cartY));
                        atualizarHistorico(startX, startY, cartX, cartY);
                        clickCount = 0;
                        repaint(); // Aciona a renderização
                    }
                }
            });
        }

        // Conversores de Coordenadas (Tela <-> Plano Cartesiano)
        private int screenToCartesianX(int screenX) { return screenX - getWidth() / 2; }
        private int screenToCartesianY(int screenY) { return getHeight() / 2 - screenY; }
        private int cartesianToScreenX(int cartX) { return cartX + getWidth() / 2; }
        private int cartesianToScreenY(int cartY) { return getHeight() / 2 - cartY; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Limpa a tela

            int w = getWidth();
            int h = getHeight();

            // 1. Desenha os Eixos Cartesianos
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(w / 2, 0, w / 2, h); // Eixo Y
            g.drawLine(0, h / 2, w, h / 2); // Eixo X

            // 2. Desenha as retas calculando os pixels
            g.setColor(Color.BLACK);
            String alg = (String) comboAlgoritmo.getSelectedItem();

            for (LineDef linha : linhas) {
                List<Point> pontos = "DDA".equals(alg) ?
                        AlgoritmoRetas.dda(linha.x1, linha.y1, linha.x2, linha.y2) :
                        AlgoritmoRetas.pontoMedio(linha.x1, linha.y1, linha.x2, linha.y2);

                for (Point p : pontos) {
                    int screenX = cartesianToScreenX(p.x);
                    int screenY = cartesianToScreenY(p.y);
                    // Pinta um "pixel" (retângulo 1x1 ou 2x2 para melhor visibilidade)
                    g.fillRect(screenX, screenY, 2, 2);
                }
            }
        }
    }
}
