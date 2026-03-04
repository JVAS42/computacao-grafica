package main.ui.panels;

import main.algorithms.AlgoritmoCircunferencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CircunferenciaPanel extends JPanel {

    private final Color COR_FUNDO = new Color(224, 224, 224);
    private final Color COR_BOTAO = new Color(76, 175, 80);

    // Painel Esquerdo
    private JLabel lblCoordenadaLive, lblQuadrante;
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtX, txtY, txtRaio;
    private JButton btnDesenhar;

    // Painel Direito
    private JLabel lblCurrentX, lblCurrentY, lblCurrentRaio;
    private JPanel scrollContainer;
    private JPanel painelClickCoords;
    private JLabel lblNoLineMessage;

    // Canvas e Estado
    private CanvasPanel canvas;
    private int clickCount = 0;
    private int cx, cy; // Centro da circunferência
    private List<CirculoDef> circulos = new ArrayList<>();

    public CircunferenciaPanel() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
    }

    // ==========================================
    // PAINEL ESQUERDO
    // ==========================================
    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        painelEsquerdo.setBackground(COR_FUNDO);
        painelEsquerdo.setPreferredSize(new Dimension(220, 0));
        painelEsquerdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Topo: Info
        JLabel lblTituloEsq = new JLabel("Informações Do Plano");
        lblTituloEsq.setFont(new Font("Arial", Font.BOLD, 14));
        painelEsquerdo.add(lblTituloEsq);
        painelEsquerdo.add(Box.createVerticalStrut(5));
        painelEsquerdo.add(new JSeparator(SwingConstants.HORIZONTAL));
        painelEsquerdo.add(Box.createVerticalStrut(10));

        lblCoordenadaLive = new JLabel("Coordenada: (0, 0)");
        lblQuadrante = new JLabel("Quadrante: Origem");
        painelEsquerdo.add(lblCoordenadaLive);
        painelEsquerdo.add(lblQuadrante);

        painelEsquerdo.add(Box.createVerticalGlue());

        // Base: Controles
        String[] algs = {"Equação Explícita", "Trigonométrico", "Ponto Médio"};
        painelEsquerdo.add(criarLinhaFormulario("Algoritmo:", comboAlgoritmo = new JComboBox<>(algs)));
        painelEsquerdo.add(Box.createVerticalStrut(15));

        painelEsquerdo.add(criarLinhaFormulario("X:", txtX = new JTextField()));
        painelEsquerdo.add(Box.createVerticalStrut(5));
        painelEsquerdo.add(criarLinhaFormulario("Y:", txtY = new JTextField()));
        painelEsquerdo.add(Box.createVerticalStrut(15));
        painelEsquerdo.add(criarLinhaFormulario("Raio:", txtRaio = new JTextField()));
        painelEsquerdo.add(Box.createVerticalStrut(20));

        // Botão Desenhar
        btnDesenhar = new JButton("Desenhar");
        estilizarBotao(btnDesenhar);
        btnDesenhar.addActionListener(e -> acaoDesenharBtn());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnDesenhar);
        painelEsquerdo.add(pnlBtn);

        comboAlgoritmo.addActionListener(e -> limparTudo());

        add(painelEsquerdo, BorderLayout.WEST);
    }

    private JPanel criarLinhaFormulario(String label, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(5, 0));
        pnl.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(65, 20));
        pnl.add(l, BorderLayout.WEST);
        pnl.add(comp, BorderLayout.CENTER);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return pnl;
    }

    // ==========================================
    // PAINEL DIREITO
    // ==========================================
    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBackground(COR_FUNDO);
        painelDireito.setPreferredSize(new Dimension(280, 0));
        painelDireito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Topo
        JPanel pnlTopoDir = new JPanel();
        pnlTopoDir.setLayout(new BoxLayout(pnlTopoDir, BoxLayout.Y_AXIS));
        pnlTopoDir.setOpaque(false);

        JLabel lblTituloDir = new JLabel("<html>Informações Da<br>Circunferência Atual</html>");
        lblTituloDir.setFont(new Font("Arial", Font.BOLD, 14));
        pnlTopoDir.add(lblTituloDir);
        pnlTopoDir.add(Box.createVerticalStrut(5));
        pnlTopoDir.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlTopoDir.add(Box.createVerticalStrut(10));

        lblNoLineMessage = new JLabel("Nenhuma circunferência desenhada");
        pnlTopoDir.add(lblNoLineMessage);

        // Info do circulo atual
        painelClickCoords = new JPanel(new GridLayout(1, 6, 2, 2));
        painelClickCoords.setOpaque(false);
        painelClickCoords.add(new JLabel("X:")); painelClickCoords.add(lblCurrentX = new JLabel(""));
        painelClickCoords.add(new JLabel("Y:")); painelClickCoords.add(lblCurrentY = new JLabel(""));
        painelClickCoords.add(new JLabel("Raio:")); painelClickCoords.add(lblCurrentRaio = new JLabel(""));
        painelClickCoords.setVisible(false);
        pnlTopoDir.add(painelClickCoords);
        pnlTopoDir.add(Box.createVerticalStrut(10));

        painelDireito.add(pnlTopoDir, BorderLayout.NORTH);

        // Centro (Scroll)
        scrollContainer = new JPanel();
        scrollContainer.setLayout(new BoxLayout(scrollContainer, BoxLayout.Y_AXIS));
        scrollContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(scrollContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        painelDireito.add(scrollPane, BorderLayout.CENTER);

        // Base (Limpar)
        JButton btnLimpar = new JButton("Limpar");
        estilizarBotao(btnLimpar);
        btnLimpar.addActionListener(e -> limparTudo());

        JPanel pnlBtnDir = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBtnDir.setOpaque(false);
        pnlBtnDir.setBorder(new EmptyBorder(10, 0, 0, 0));
        pnlBtnDir.add(btnLimpar);
        painelDireito.add(pnlBtnDir, BorderLayout.SOUTH);

        add(painelDireito, BorderLayout.EAST);
    }

    private void estilizarBotao(JButton btn) {
        btn.setBackground(COR_BOTAO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 30));
    }

    // ==========================================
    // CANVAS
    // ==========================================
    private void setupCanvas() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(COR_FUNDO);

        // Título central
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        JLabel lblVoltar = new JLabel("Voltar ao Início", SwingConstants.CENTER);
        lblVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        lblVoltar.setForeground(Color.DARK_GRAY);
        lblVoltar.setBorder(new EmptyBorder(0, 0, 20, 0));
        centerPanel.add(lblVoltar, BorderLayout.NORTH);

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500));
        centerPanel.add(canvas, BorderLayout.CENTER);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
    }

    // ==========================================
    // LÓGICA E EVENTOS
    // ==========================================
    private void limparTudo() {
        circulos.clear();
        clickCount = 0;
        txtX.setText(""); txtY.setText(""); txtRaio.setText("");

        lblNoLineMessage.setVisible(true);
        painelClickCoords.setVisible(false);
        scrollContainer.removeAll();
        scrollContainer.revalidate();
        scrollContainer.repaint();

        canvas.repaint();
    }

    private void acaoDesenharBtn() {
        try {
            int x = Integer.parseInt(txtX.getText());
            int y = Integer.parseInt(txtY.getText());
            int r = Integer.parseInt(txtRaio.getText());

            circulos.add(new CirculoDef(x, y, r));
            atualizarPainelDireito();
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha X, Y e Raio com números inteiros válidos.");
        }
    }

    private void atualizarPainelDireito() {
        if (circulos.isEmpty()) return;

        CirculoDef ultimoCirculo = circulos.get(circulos.size() - 1);

        lblNoLineMessage.setVisible(false);
        painelClickCoords.setVisible(true);

        lblCurrentX.setText(String.valueOf(ultimoCirculo.x));
        lblCurrentY.setText(String.valueOf(ultimoCirculo.y));
        lblCurrentRaio.setText(String.valueOf(ultimoCirculo.r));

        String alg = (String) comboAlgoritmo.getSelectedItem();
        scrollContainer.removeAll();

        for (CirculoDef c : circulos) {
            List<Point> pontos;
            if ("Equação Explícita".equals(alg)) {
                pontos = AlgoritmoCircunferencia.equacaoExplicita(c.x, c.y, c.r);
            } else if ("Trigonométrico".equals(alg)) {
                pontos = AlgoritmoCircunferencia.trigonometrico(c.x, c.y, c.r);
            } else {
                pontos = AlgoritmoCircunferencia.pontoMedio(c.x, c.y, c.r);
            }

            scrollContainer.removeAll();

            int count = 1;
            for (Point p : pontos) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(new EmptyBorder(5, 10, 5, 10));

                row.add(new JLabel("<html><strong>X" + count + ":</strong> " + p.x + "</html>"), BorderLayout.WEST);
                row.add(new JLabel("<html><strong>Y" + count + ":</strong> " + p.y + "</html>"), BorderLayout.EAST);

                scrollContainer.add(row);
                scrollContainer.add(new JSeparator());
                count++;
            }
        }

        scrollContainer.revalidate();
        scrollContainer.repaint();
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

    private class CirculoDef {
        int x, y, r;
        public CirculoDef(int x, int y, int r) {
            this.x = x; this.y = y; this.r = r;
        }
    }

    // ==========================================
    // ÁREA DE DESENHO (Canvas)
    // ==========================================
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int x = e.getX() - getWidth() / 2;
                    int y = getHeight() / 2 - e.getY();

                    lblCoordenadaLive.setText(String.format("Coordenada: (%d, %d)", x, y));
                    lblQuadrante.setText("Quadrante: " + getQuadrante(x, y));
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() - getWidth() / 2;
                    int y = getHeight() / 2 - e.getY();

                    if (clickCount == 0) {
                        cx = x;
                        cy = y;
                        txtX.setText(String.valueOf(cx));
                        txtY.setText(String.valueOf(cy));
                        clickCount = 1;
                    } else if (clickCount == 1) {
                        // Calcula o raio usando a distância do centro (cx, cy) para o clique atual (x, y)
                        int r = (int) Math.round(Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2)));
                        txtRaio.setText(String.valueOf(r));

                        circulos.add(new CirculoDef(cx, cy, r));
                        clickCount = 0;
                        atualizarPainelDireito();
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();

            g.setColor(new Color(200, 200, 200));
            g.drawLine(w / 2, 0, w / 2, h);
            g.drawLine(0, h / 2, w, h / 2);

            g.setColor(Color.BLACK);
            String alg = (String) comboAlgoritmo.getSelectedItem();

            for (CirculoDef circulo : circulos) {
                List<Point> pontos;
                if ("Equação Explícita".equals(alg)) {
                    pontos = AlgoritmoCircunferencia.equacaoExplicita(circulo.x, circulo.y, circulo.r);
                } else if ("Trigonométrico".equals(alg)) {
                    pontos = AlgoritmoCircunferencia.trigonometrico(circulo.x, circulo.y, circulo.r);
                } else {
                    pontos = AlgoritmoCircunferencia.pontoMedio(circulo.x, circulo.y, circulo.r);
                }

                for (Point p : pontos) {
                    int canvasX = p.x + w / 2;
                    int canvasY = h / 2 - p.y;
                    g.fillRect(canvasX, canvasY, 1, 1);
                }
            }
        }
    }
}