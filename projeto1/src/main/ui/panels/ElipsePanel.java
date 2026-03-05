package main.ui.panels;

import main.algorithms.AlgoritmoElipse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ElipsePanel extends JPanel {

    private final Color COR_FUNDO = new Color(224, 224, 224);
    private final Color COR_BOTAO = new Color(76, 175, 80);

    // Painel Esquerdo
    private JLabel lblCoordenadaLive, lblQuadrante;
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtX, txtY, txtRaioX, txtRaioY;
    private JButton btnDesenhar;

    // Painel Direito
    private JLabel lblCurrentX, lblCurrentY, lblCurrentRaioX, lblCurrentRaioY;
    private JPanel scrollContainer;
    private JPanel painelClickCoords;
    private JLabel lblNoLineMessage;

    // Canvas e Estado
    private CanvasPanel canvas;
    private int clickCount = 0;
    private int cx, cy; // Centro da elipse
    private List<ElipseDef> elipses = new ArrayList<>();

    public ElipsePanel() {
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
        String[] algs = {"Ponto Médio"};
        painelEsquerdo.add(criarLinhaFormulario("Algoritmo:", comboAlgoritmo = new JComboBox<>(algs)));
        painelEsquerdo.add(Box.createVerticalStrut(15));

        painelEsquerdo.add(criarLinhaFormulario("Centro X:", txtX = new JTextField()));
        painelEsquerdo.add(Box.createVerticalStrut(5));
        painelEsquerdo.add(criarLinhaFormulario("Centro Y:", txtY = new JTextField()));
        painelEsquerdo.add(Box.createVerticalStrut(15));
        painelEsquerdo.add(criarLinhaFormulario("Raio X:", txtRaioX = new JTextField()));
        painelEsquerdo.add(Box.createVerticalStrut(5));
        painelEsquerdo.add(criarLinhaFormulario("Raio Y:", txtRaioY = new JTextField()));
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

        JLabel lblTituloDir = new JLabel("<html>Informações Da<br>Elipse Atual</html>");
        lblTituloDir.setFont(new Font("Arial", Font.BOLD, 14));
        pnlTopoDir.add(lblTituloDir);
        pnlTopoDir.add(Box.createVerticalStrut(5));
        pnlTopoDir.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlTopoDir.add(Box.createVerticalStrut(10));

        lblNoLineMessage = new JLabel("Nenhuma elipse desenhada");
        pnlTopoDir.add(lblNoLineMessage);

        // Info da elipse atual
        painelClickCoords = new JPanel(new GridLayout(2, 4, 2, 2));
        painelClickCoords.setOpaque(false);
        painelClickCoords.add(new JLabel("CX:")); painelClickCoords.add(lblCurrentX = new JLabel(""));
        painelClickCoords.add(new JLabel("CY:")); painelClickCoords.add(lblCurrentY = new JLabel(""));
        painelClickCoords.add(new JLabel("RaioX:")); painelClickCoords.add(lblCurrentRaioX = new JLabel(""));
        painelClickCoords.add(new JLabel("RaioY:")); painelClickCoords.add(lblCurrentRaioY = new JLabel(""));
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
        elipses.clear();
        clickCount = 0;
        txtX.setText(""); txtY.setText(""); txtRaioX.setText(""); txtRaioY.setText("");

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
            int rx = Integer.parseInt(txtRaioX.getText());
            int ry = Integer.parseInt(txtRaioY.getText());

            elipses.add(new ElipseDef(x, y, Math.abs(rx), Math.abs(ry)));
            atualizarPainelDireito();
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha Centro X/Y e Raios X/Y com inteiros válidos.");
        }
    }

    private void atualizarPainelDireito() {
        if (elipses.isEmpty()) return;

        ElipseDef ultimaElipse = elipses.get(elipses.size() - 1);

        lblNoLineMessage.setVisible(false);
        painelClickCoords.setVisible(true);

        lblCurrentX.setText(String.valueOf(ultimaElipse.x));
        lblCurrentY.setText(String.valueOf(ultimaElipse.y));
        lblCurrentRaioX.setText(String.valueOf(ultimaElipse.rx));
        lblCurrentRaioY.setText(String.valueOf(ultimaElipse.ry));

        scrollContainer.removeAll();

        for (ElipseDef elipse : elipses) {
            List<Point> pontos = AlgoritmoElipse.pontoMedio(elipse.x, elipse.y, elipse.rx, elipse.ry);

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

    private class ElipseDef {
        int x, y, rx, ry;
        public ElipseDef(int x, int y, int rx, int ry) {
            this.x = x; this.y = y; this.rx = rx; this.ry = ry;
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
                        // Calcula o RaioX e RaioY baseado na distância do centro até o clique atual
                        int rx = Math.abs(x - cx);
                        int ry = Math.abs(y - cy);

                        txtRaioX.setText(String.valueOf(rx));
                        txtRaioY.setText(String.valueOf(ry));

                        elipses.add(new ElipseDef(cx, cy, rx, ry));
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

            // Eixos
            g.setColor(new Color(200, 200, 200));
            g.drawLine(w / 2, 0, w / 2, h);
            g.drawLine(0, h / 2, w, h / 2);

            // Elipses
            g.setColor(Color.BLACK);

            for (ElipseDef elipse : elipses) {
                List<Point> pontos = AlgoritmoElipse.pontoMedio(elipse.x, elipse.y, elipse.rx, elipse.ry);

                for (Point p : pontos) {
                    int canvasX = p.x + w / 2;
                    int canvasY = h / 2 - p.y;
                    g.fillRect(canvasX, canvasY, 1, 1);
                }
            }
        }
    }
}
