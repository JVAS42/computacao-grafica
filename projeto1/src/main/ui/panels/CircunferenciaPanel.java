package main.ui.panels;

import main.algorithms.AlgoritmoCircunferencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CircunferenciaPanel extends JPanel {

    private JLabel lblCoordenadaLive, lblQuadrante;
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtX, txtY, txtRaio;
    private JButton btnDesenhar;

    private JLabel lblCurrentX, lblCurrentY, lblCurrentRaio;
    private JPanel scrollContainer;
    private JPanel painelClickCoords;
    private JLabel lblNoLineMessage;

    private CanvasPanel canvas;
    private int clickCount = 0;
    private int cx, cy;
    private List<CirculoDef> circulos = new ArrayList<>();

    public CircunferenciaPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F0F0F0"));

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
    }

    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setBackground(Color.decode("#F0F0F0"));
        painelEsquerdo.setPreferredSize(new Dimension(320, 0));
        painelEsquerdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel containerNorte = new JPanel();
        containerNorte.setLayout(new BoxLayout(containerNorte, BoxLayout.Y_AXIS));
        containerNorte.setOpaque(false);

        JLabel lblTituloEsq = new JLabel("INFORMAÇÕES DO PLANO");
        lblTituloEsq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloEsq.setForeground(Color.decode("#213555"));
        lblTituloEsq.setAlignmentX(Component.LEFT_ALIGNMENT);

        containerNorte.add(lblTituloEsq);
        containerNorte.add(Box.createVerticalStrut(5));

        JSeparator sepEsq = new JSeparator(SwingConstants.HORIZONTAL);
        sepEsq.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerNorte.add(sepEsq);
        containerNorte.add(Box.createVerticalStrut(10));

        lblCoordenadaLive = new JLabel("Coordenada: (0, 0)");
        lblCoordenadaLive.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCoordenadaLive.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblQuadrante = new JLabel("Quadrante: Origem");
        lblQuadrante.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblQuadrante.setAlignmentX(Component.LEFT_ALIGNMENT);

        containerNorte.add(lblCoordenadaLive);
        containerNorte.add(Box.createVerticalStrut(5));
        containerNorte.add(lblQuadrante);
        containerNorte.add(Box.createVerticalStrut(25));

        JPanel painelInputs = new JPanel(new GridBagLayout());
        painelInputs.setOpaque(false);
        painelInputs.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] algs = {"Equação Explícita", "Trigonométrico", "Ponto Médio"};
        comboAlgoritmo = new JComboBox<>(algs);
        comboAlgoritmo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtX = estilizarTextField(5);
        txtY = estilizarTextField(5);
        txtRaio = estilizarTextField(5);

        adicionarCampoGrid(painelInputs, "Algoritmo:", comboAlgoritmo, gbc, 0);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        painelInputs.add(Box.createVerticalStrut(10), gbc);

        adicionarCampoGrid(painelInputs, "X (Centro):", txtX, gbc, 2);
        adicionarCampoGrid(painelInputs, "Y (Centro):", txtY, gbc, 3);
        adicionarCampoGrid(painelInputs, "Raio:", txtRaio, gbc, 4);

        containerNorte.add(painelInputs);
        painelEsquerdo.add(containerNorte, BorderLayout.NORTH);

        btnDesenhar = estilizarBotao("DESENHAR");
        btnDesenhar.addActionListener(e -> acaoDesenharBtn());

        JPanel pnlBtn = new JPanel(new BorderLayout());
        pnlBtn.setOpaque(false);
        pnlBtn.setBorder(new EmptyBorder(15, 0, 0, 0));
        pnlBtn.add(btnDesenhar, BorderLayout.CENTER);

        painelEsquerdo.add(pnlBtn, BorderLayout.SOUTH);
        comboAlgoritmo.addActionListener(e -> limparTudo());

        add(painelEsquerdo, BorderLayout.WEST);
    }

    private void adicionarCampoGrid(JPanel pnl, String labelText, JComponent comp, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.DARK_GRAY);
        pnl.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        pnl.add(comp, gbc);
    }

    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBackground(Color.decode("#F0F0F0"));
        painelDireito.setPreferredSize(new Dimension(320, 0));
        painelDireito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlTopoDir = new JPanel();
        pnlTopoDir.setLayout(new BoxLayout(pnlTopoDir, BoxLayout.Y_AXIS));
        pnlTopoDir.setOpaque(false);

        JLabel lblTituloDir = new JLabel("INFORMAÇÕES DA CIRCUNFERÊNCIA");
        lblTituloDir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloDir.setForeground(Color.decode("#213555"));
        lblTituloDir.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTopoDir.add(lblTituloDir);
        pnlTopoDir.add(Box.createVerticalStrut(5));

        JSeparator sepDir = new JSeparator(SwingConstants.HORIZONTAL);
        sepDir.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTopoDir.add(sepDir);
        pnlTopoDir.add(Box.createVerticalStrut(10));

        lblNoLineMessage = new JLabel("Nenhuma circunferência desenhada");
        lblNoLineMessage.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNoLineMessage.setForeground(Color.GRAY);
        lblNoLineMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTopoDir.add(lblNoLineMessage);

        painelClickCoords = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelClickCoords.setOpaque(false);
        painelClickCoords.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelClickCoords.add(criarLabelInfo("X:", lblCurrentX = new JLabel("-")));
        painelClickCoords.add(criarLabelInfo("Y:", lblCurrentY = new JLabel("-")));
        painelClickCoords.add(criarLabelInfo("Raio:", lblCurrentRaio = new JLabel("-")));
        painelClickCoords.setVisible(false);

        pnlTopoDir.add(painelClickCoords);
        pnlTopoDir.add(Box.createVerticalStrut(15));
        painelDireito.add(pnlTopoDir, BorderLayout.NORTH);

        scrollContainer = new JPanel();
        scrollContainer.setLayout(new BoxLayout(scrollContainer, BoxLayout.Y_AXIS));
        scrollContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(scrollContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        painelDireito.add(scrollPane, BorderLayout.CENTER);

        JButton btnLimpar = estilizarBotao("LIMPAR TELA");
        btnLimpar.addActionListener(e -> limparTudo());

        JPanel pnlBtnDir = new JPanel(new BorderLayout());
        pnlBtnDir.setOpaque(false);
        pnlBtnDir.setBorder(new EmptyBorder(15, 0, 0, 0));
        pnlBtnDir.add(btnLimpar, BorderLayout.CENTER);

        painelDireito.add(pnlBtnDir, BorderLayout.SOUTH);
        add(painelDireito, BorderLayout.EAST);
    }

    private JPanel criarLabelInfo(String titulo, JLabel valor) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        pnl.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(Color.DARK_GRAY);
        valor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnl.add(lblTitulo);
        pnl.add(valor);
        return pnl;
    }

    private JTextField estilizarTextField(int colunas) {
        JTextField txt = new JTextField(colunas);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        return txt;
    }

    private JButton estilizarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.decode("#213555"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 40));
        return btn;
    }

    private void setupCanvas() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.decode("#F0F0F0"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500));
        centerPanel.add(canvas, BorderLayout.CENTER);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
    }

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
            JOptionPane.showMessageDialog(this, "Preencha X, Y e Raio com números inteiros válidos.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Atualiza o painel direito com os pontos gerados pela circunferência selecionada
    private void atualizarPainelDireito() {
        if (circulos.isEmpty()) return;

        CirculoDef ultimoCirculo = circulos.get(circulos.size() - 1);

        lblNoLineMessage.setVisible(false);
        painelClickCoords.setVisible(true);

        lblCurrentX.setText(String.valueOf(ultimoCirculo.x));
        lblCurrentY.setText(String.valueOf(ultimoCirculo.y));
        lblCurrentRaio.setText(String.valueOf(ultimoCirculo.r));

        String alg = (String) comboAlgoritmo.getSelectedItem();

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
                JPanel row = new JPanel(new GridLayout(1, 2));
                row.setBackground(Color.WHITE);
                row.setBorder(new EmptyBorder(5, 10, 5, 10));

                JLabel lblX = new JLabel("X" + count + ": " + p.x);
                JLabel lblY = new JLabel("Y" + count + ": " + p.y);

                lblX.setFont(new Font("Monospaced", Font.BOLD, 12));
                lblY.setFont(new Font("Monospaced", Font.BOLD, 12));
                lblX.setForeground(Color.DARK_GRAY);
                lblY.setForeground(Color.DARK_GRAY);

                row.add(lblX);
                row.add(lblY);

                scrollContainer.add(row);

                JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
                sep.setForeground(new Color(240, 240, 240));
                scrollContainer.add(sep);

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

    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1));

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

        // Renderiza as circunferências e eixos no Canvas
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();

            g.setColor(new Color(220, 220, 220));
            g.drawLine(w / 2, 0, w / 2, h);
            g.drawLine(0, h / 2, w, h / 2);

            g.setColor(Color.RED);
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