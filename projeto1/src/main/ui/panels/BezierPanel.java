package main.ui.panels;

import main.algorithms.Bezier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BezierPanel extends JPanel {

    // Lógica e Estado
    private List<Point> controlPoints;
    private Bezier bezierAlgo;
    private int clickIndex = 0; // Controla qual ponto (0 a 3) será atualizado ao clicar

    // Painel Esquerdo
    private JLabel lblCoordenadaLive, lblQuadrante;
    private JTextField[] fieldsX = new JTextField[4];
    private JTextField[] fieldsY = new JTextField[4];
    private JButton btnDesenhar;

    // Painel Direito
    private JPanel painelListaPontos;
    private JButton btnLimpar;

    // Canvas
    private CanvasPanel canvas;

    public BezierPanel() {
        this.bezierAlgo = new Bezier();
        this.controlPoints = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F0F0F0")); // Fundo padronizado

        // Inicializa pontos padrão
        int[][] defaults = {{-200, -100}, {-100, 200}, {100, -200}, {200, 100}};
        for (int[] p : defaults) {
            controlPoints.add(new Point(p[0], p[1]));
        }

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();

        // Renderiza o estado inicial na direita
        atualizarPainelDireito();
    }

    // ==========================================
    // PAINEL ESQUERDO
    // ==========================================
    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setBackground(Color.decode("#F0F0F0"));
        painelEsquerdo.setPreferredSize(new Dimension(320, 0)); // Largura padronizada
        painelEsquerdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Container superior (Textos e Inputs)
        JPanel containerNorte = new JPanel();
        containerNorte.setLayout(new BoxLayout(containerNorte, BoxLayout.Y_AXIS));
        containerNorte.setOpaque(false);

        // Topo: Info
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

        // Controles de Entrada (GridBagLayout)
        JPanel painelInputs = new JPanel(new GridBagLayout());
        painelInputs.setOpaque(false);
        painelInputs.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int gridY = 0;
        for (int i = 0; i < 4; i++) {
            JLabel lblTituloPonto = new JLabel("Ponto P" + i);
            lblTituloPonto.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTituloPonto.setForeground(Color.decode("#213555"));

            gbc.gridx = 0; gbc.gridy = gridY++; gbc.gridwidth = 4;
            painelInputs.add(lblTituloPonto, gbc);

            gbc.gridwidth = 1;

            fieldsX[i] = estilizarTextField(String.valueOf(controlPoints.get(i).x), 4);
            fieldsY[i] = estilizarTextField(String.valueOf(controlPoints.get(i).y), 4);

            adicionarCampoGrid(painelInputs, "X:", fieldsX[i], gbc, gridY, 0);
            adicionarCampoGrid(painelInputs, "Y:", fieldsY[i], gbc, gridY, 2);
            gridY++;

            gbc.gridx = 0; gbc.gridy = gridY++; gbc.gridwidth = 4;
            painelInputs.add(Box.createVerticalStrut(5), gbc); // Espaço entre pontos
        }

        containerNorte.add(painelInputs);
        painelEsquerdo.add(containerNorte, BorderLayout.NORTH);

        // Botão Desenhar (Rodapé)
        btnDesenhar = estilizarBotao("DESENHAR CURVA");
        btnDesenhar.addActionListener(e -> atualizarCurvaViaInputs());

        JPanel pnlBtn = new JPanel(new BorderLayout());
        pnlBtn.setOpaque(false);
        pnlBtn.setBorder(new EmptyBorder(15, 0, 0, 0));
        pnlBtn.add(btnDesenhar, BorderLayout.CENTER);

        painelEsquerdo.add(pnlBtn, BorderLayout.SOUTH);

        add(painelEsquerdo, BorderLayout.WEST);
    }

    private void adicionarCampoGrid(JPanel pnl, String labelText, JComponent comp, GridBagConstraints gbc, int y, int startX) {
        gbc.gridx = startX; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.DARK_GRAY);
        pnl.add(label, gbc);

        gbc.gridx = startX + 1; gbc.weightx = 1.0;
        pnl.add(comp, gbc);
    }

    // ==========================================
    // PAINEL DIREITO
    // ==========================================
    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBackground(Color.decode("#F0F0F0"));
        painelDireito.setPreferredSize(new Dimension(320, 0)); // Largura padronizada
        painelDireito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Topo
        JPanel pnlTopoDir = new JPanel();
        pnlTopoDir.setLayout(new BoxLayout(pnlTopoDir, BoxLayout.Y_AXIS));
        pnlTopoDir.setOpaque(false);

        JLabel lblTituloDir = new JLabel("PONTOS DE CONTROLE (P0 - P3)");
        lblTituloDir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloDir.setForeground(Color.decode("#213555"));
        lblTituloDir.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTopoDir.add(lblTituloDir);
        pnlTopoDir.add(Box.createVerticalStrut(5));

        JSeparator sepDir = new JSeparator(SwingConstants.HORIZONTAL);
        sepDir.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTopoDir.add(sepDir);

        pnlTopoDir.add(Box.createVerticalStrut(15));
        painelDireito.add(pnlTopoDir, BorderLayout.NORTH);

        // Centro (Scroll com os 4 pontos)
        painelListaPontos = new JPanel();
        painelListaPontos.setLayout(new BoxLayout(painelListaPontos, BoxLayout.Y_AXIS));
        painelListaPontos.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(painelListaPontos);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        painelDireito.add(scrollPane, BorderLayout.CENTER);

        // Base (Limpar)
        btnLimpar = estilizarBotao("RESTAURAR PADRÕES");
        btnLimpar.addActionListener(e -> restaurarPadroes());

        JPanel pnlBtnDir = new JPanel(new BorderLayout());
        pnlBtnDir.setOpaque(false);
        pnlBtnDir.setBorder(new EmptyBorder(15, 0, 0, 0));
        pnlBtnDir.add(btnLimpar, BorderLayout.CENTER);

        painelDireito.add(pnlBtnDir, BorderLayout.SOUTH);

        add(painelDireito, BorderLayout.EAST);
    }

    // ==========================================
    // UTILITÁRIOS DE ESTILO
    // ==========================================
    private JTextField estilizarTextField(String textoInicial, int colunas) {
        JTextField txt = new JTextField(textoInicial, colunas);
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

    // ==========================================
    // CANVAS
    // ==========================================
    private void setupCanvas() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.decode("#F0F0F0"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JLabel lblVoltar = new JLabel("Voltar ao Início", SwingConstants.CENTER);
        lblVoltar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVoltar.setForeground(Color.GRAY);
        lblVoltar.setBorder(new EmptyBorder(0, 0, 15, 0));
        centerPanel.add(lblVoltar, BorderLayout.NORTH);

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500)); // Tamanho padronizado 500x500
        centerPanel.add(canvas, BorderLayout.CENTER);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
    }

    // ==========================================
    // LÓGICA E EVENTOS
    // ==========================================
    private void restaurarPadroes() {
        int[][] defaults = {{-200, -100}, {-100, 200}, {100, -200}, {200, 100}};
        controlPoints.clear();
        for (int i = 0; i < 4; i++) {
            controlPoints.add(new Point(defaults[i][0], defaults[i][1]));
            fieldsX[i].setText(String.valueOf(defaults[i][0]));
            fieldsY[i].setText(String.valueOf(defaults[i][1]));
        }
        clickIndex = 0;
        atualizarPainelDireito();
        canvas.repaint();
    }

    private void atualizarCurvaViaInputs() {
        try {
            controlPoints.clear();
            for (int i = 0; i < 4; i++) {
                int x = Integer.parseInt(fieldsX[i].getText());
                int y = Integer.parseInt(fieldsY[i].getText());
                controlPoints.add(new Point(x, y));
            }
            atualizarPainelDireito();
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Insira apenas números inteiros nos campos.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarPainelDireito() {
        painelListaPontos.removeAll();

        for (int i = 0; i < controlPoints.size(); i++) {
            Point p = controlPoints.get(i);

            JPanel item = new JPanel(new GridLayout(1, 2));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            item.setBackground(Color.WHITE);
            item.setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel lblX = new JLabel("P" + i + " X: " + p.x);
            JLabel lblY = new JLabel("Y: " + p.y);

            lblX.setFont(new Font("Monospaced", Font.BOLD, 13));
            lblY.setFont(new Font("Monospaced", Font.BOLD, 13));
            lblX.setForeground(Color.DARK_GRAY);
            lblY.setForeground(Color.DARK_GRAY);

            item.add(lblX);
            item.add(lblY);

            painelListaPontos.add(item);

            JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
            sep.setForeground(new Color(240, 240, 240));
            painelListaPontos.add(sep);
        }

        // Adiciona um aviso sobre os segmentos
        JLabel info = new JLabel("<html><div style='text-align: center; padding: 15px; color: #888888;'>"
                + "A curva de Bézier é renderizada interpolando <strong>500 segmentos</strong> "
                + "entre os pontos de controle acima.</div></html>");
        painelListaPontos.add(info);

        painelListaPontos.revalidate();
        painelListaPontos.repaint();
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

    // ==========================================
    // ÁREA DE DESENHO (Canvas)
    // ==========================================
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1));

            // Hover (MouseMotion)
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int x = e.getX() - getWidth() / 2;
                    int y = getHeight() / 2 - e.getY();
                    lblCoordenadaLive.setText(String.format("Coordenada: (%d, %d)", x, y));
                    lblQuadrante.setText("Quadrante: " + getQuadrante(x, y));
                }
            });

            // Clique na Tela (Adiciona/Altera pontos em sequência)
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() - getWidth() / 2;
                    int y = getHeight() / 2 - e.getY();

                    // Atualiza os inputs correspondentes
                    fieldsX[clickIndex].setText(String.valueOf(x));
                    fieldsY[clickIndex].setText(String.valueOf(y));

                    // Avança para o próximo ponto (loop de 0 a 3)
                    clickIndex = (clickIndex + 1) % 4;

                    // Atualiza a curva automaticamente
                    atualizarCurvaViaInputs();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            // Eixos (Cinza claro)
            g.setColor(new Color(220, 220, 220));
            g.drawLine(cx, 0, cx, getHeight());
            g.drawLine(0, cy, getWidth(), cy);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Desenha as linhas guia pontilhadas (Polígono de Controle) para melhor visualização (Opcional, mas padrão na CG)
            g2d.setColor(new Color(200, 200, 200));
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            g2d.setStroke(dashed);
            for (int i = 0; i < controlPoints.size() - 1; i++) {
                g2d.drawLine(cx + controlPoints.get(i).x, cy - controlPoints.get(i).y,
                        cx + controlPoints.get(i+1).x, cy - controlPoints.get(i+1).y);
            }
            g2d.setStroke(new BasicStroke()); // Reseta o stroke

            // Desenha a Curva (RED)
            g2d.setColor(Color.RED);
            List<Point> curve = bezierAlgo.generateBezier(controlPoints, 500);
            for (int i = 0; i < curve.size() - 1; i++) {
                g2d.drawLine(cx + curve.get(i).x, cy - curve.get(i).y,
                        cx + curve.get(i+1).x, cy - curve.get(i+1).y);
            }

            // Desenha os Pontos de Controle (Azul Destacado)
            g2d.setColor(Color.decode("#213555"));
            for (int i = 0; i < controlPoints.size(); i++) {
                Point p = controlPoints.get(i);
                int drawX = cx + p.x;
                int drawY = cy - p.y;

                g2d.fillOval(drawX - 4, drawY - 4, 8, 8);

                // Desenha a numeração do ponto (P0, P1...)
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2d.drawString("P" + i, drawX + 8, drawY - 8);
                g2d.setColor(Color.decode("#213555")); // Retorna para a cor do ponto
            }
        }
    }
}