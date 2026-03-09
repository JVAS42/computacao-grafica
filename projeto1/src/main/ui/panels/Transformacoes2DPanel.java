package main.ui.panels;

import main.algorithms.Transformacoes2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transformacoes2DPanel extends JPanel {

    private final Color COR_FUNDO = new Color(224, 224, 224);
    private final Color COR_BOTAO = new Color(76, 175, 80);

    // Controles Esq - Translação
    private JTextField txtTransX, txtTransY;
    // Controles Esq - Escala
    private JTextField txtEscalaX, txtEscalaY;
    // Controles Esq - Rotação
    private JTextField txtRotX, txtRotY, txtRotAngulo;
    // Controles Esq - Reflexão
    private JCheckBox chkRefX, chkRefY;
    // Controles Esq - Cisalhamento
    private JTextField txtCisX, txtCisY;
    // Controles Esq - Quadrado
    private JTextField txtQuadTamanho, txtQuadPosX, txtQuadPosY;

    // Controles Dir
    private JTextArea txtVertices, txtHistorico;
    private JTextField txtCentro;
    private JComboBox<String> comboSequencia;
    private JPanel panelSeqParams;
    private CardLayout cardSeqParams;

    // Campos Dinâmicos Sequência
    private JTextField seqTransX, seqTransY;
    private JTextField seqRotAng, seqRotCX, seqRotCY;
    private JTextField seqEscalaX, seqEscalaY;
    private JTextField seqCisX, seqCisY;
    private JCheckBox seqRefX, seqRefY;

    // Estado do Objeto e Sequencia
    private CanvasPanel canvas;
    private List<Point2D.Double> quadradoAtual = new ArrayList<>();
    private List<Point2D.Double> quadradoOriginal = new ArrayList<>(); // Guarda o objeto original (Mundo)
    private List<TransformacaoConfig> sequenciaAtual = new ArrayList<>();
    private StringBuilder historicoStr = new StringBuilder();
    private int historicoCount = 1;

    public Transformacoes2DPanel() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
    }

    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        painelEsq.setBackground(COR_FUNDO);
        painelEsq.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Transformações Geométricas");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        painelEsq.add(titulo);
        painelEsq.add(Box.createVerticalStrut(5));
        painelEsq.add(new JSeparator());

        painelEsq.add(criarBlocoDuplo("Translação:", "X:", txtTransX = new JTextField("0"), "Y:", txtTransY = new JTextField("0"), "Aplicar Translação", this::aplicarTranslacao));
        painelEsq.add(criarBlocoDuplo("Escala:", "X:", txtEscalaX = new JTextField("1"), "Y:", txtEscalaY = new JTextField("1"), "Aplicar Escala", this::aplicarEscala));

        JPanel pnlRot = new JPanel(new GridLayout(4, 1, 2, 2));
        pnlRot.setOpaque(false);
        pnlRot.add(new JLabel("Rotação:"));
        JPanel pnlRotXY = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pnlRotXY.setOpaque(false);
        pnlRotXY.add(new JLabel("X: ")); pnlRotXY.add(txtRotX = new JTextField("0", 4));
        pnlRotXY.add(Box.createHorizontalStrut(10));
        pnlRotXY.add(new JLabel("Y: ")); pnlRotXY.add(txtRotY = new JTextField("0", 4));
        pnlRot.add(pnlRotXY);
        JPanel pnlRotAng = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pnlRotAng.setOpaque(false);
        pnlRotAng.add(new JLabel("Ângulo: ")); pnlRotAng.add(txtRotAngulo = new JTextField("0", 4));
        pnlRot.add(pnlRotAng);
        pnlRot.add(criarBotao("Aplicar Rotação", e -> aplicarRotacao()));
        painelEsq.add(pnlRot);
        painelEsq.add(new JSeparator());

        JPanel pnlRef = new JPanel(new GridLayout(3, 1, 2, 2));
        pnlRef.setOpaque(false);
        pnlRef.add(new JLabel("Reflexão:"));
        JPanel pnlRefChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pnlRefChecks.setOpaque(false);
        pnlRefChecks.add(chkRefX = new JCheckBox("Em X")); chkRefX.setOpaque(false);
        pnlRefChecks.add(chkRefY = new JCheckBox("Em Y")); chkRefY.setOpaque(false);
        pnlRef.add(pnlRefChecks);
        pnlRef.add(criarBotao("Aplicar Reflexão", e -> aplicarReflexao()));
        painelEsq.add(pnlRef);
        painelEsq.add(new JSeparator());

        painelEsq.add(criarBlocoDuplo("Cisalhamento:", "X:", txtCisX = new JTextField("0"), "Y:", txtCisY = new JTextField("0"), "Aplicar Cisalhamento", this::aplicarCisalhamento));

        JPanel pnlQuad = new JPanel(new GridLayout(5, 1, 2, 2));
        pnlQuad.setOpaque(false);
        pnlQuad.add(new JLabel("Configurar Quadrado:"));
        pnlQuad.add(criarLinhaForm("Tamanho:", txtQuadTamanho = new JTextField("50")));
        pnlQuad.add(criarLinhaForm("Posição X:", txtQuadPosX = new JTextField("0")));
        pnlQuad.add(criarLinhaForm("Posição Y:", txtQuadPosY = new JTextField("0")));
        pnlQuad.add(criarBotao("Gerar Quadrado", e -> gerarQuadrado()));
        painelEsq.add(pnlQuad);

        JScrollPane scroll = new JScrollPane(painelEsq);
        scroll.setPreferredSize(new Dimension(240, 0));
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        add(scroll, BorderLayout.WEST);
    }

    private JPanel criarBlocoDuplo(String titulo, String lbl1, JTextField f1, String lbl2, JTextField f2, String btnText, Runnable acao) {
        JPanel pnl = new JPanel(new GridLayout(3, 1, 2, 2));
        pnl.setOpaque(false);
        pnl.add(new JLabel(titulo));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.add(new JLabel(lbl1 + " ")); row.add(f1); f1.setColumns(4);
        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel(lbl2 + " ")); row.add(f2); f2.setColumns(4);
        pnl.add(row);
        pnl.add(criarBotao(btnText, e -> acao.run()));

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(pnl, BorderLayout.CENTER);
        container.add(new JSeparator(), BorderLayout.SOUTH);
        return container;
    }

    private JPanel criarLinhaForm(String lbl, JTextField tf) {
        JPanel pnl = new JPanel(new BorderLayout()); pnl.setOpaque(false);
        pnl.add(new JLabel(lbl), BorderLayout.WEST);
        pnl.add(tf, BorderLayout.EAST); tf.setColumns(5);
        return pnl;
    }

    private JButton criarBotao(String texto, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setBackground(COR_BOTAO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(acao);
        return btn;
    }

    private void setupPainelDireito() {
        JPanel painelDir = new JPanel();
        painelDir.setLayout(new BoxLayout(painelDir, BoxLayout.Y_AXIS));
        painelDir.setBackground(COR_FUNDO);
        painelDir.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Informações do Objeto");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        painelDir.add(titulo);
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(new JSeparator());

        painelDir.add(new JLabel("Vértices:"));
        txtVertices = new JTextArea(5, 20); txtVertices.setEditable(false);
        painelDir.add(new JScrollPane(txtVertices));
        painelDir.add(Box.createVerticalStrut(10));

        painelDir.add(new JLabel("Centro:"));
        txtCentro = new JTextField(); txtCentro.setEditable(false);
        painelDir.add(txtCentro);
        painelDir.add(Box.createVerticalStrut(10));

        painelDir.add(criarBotao("Limpar", e -> limparTudo()));
        painelDir.add(Box.createVerticalStrut(15));

        painelDir.add(new JLabel("Histórico de Transformações:"));
        txtHistorico = new JTextArea(4, 20); txtHistorico.setEditable(false);
        painelDir.add(new JScrollPane(txtHistorico));
        painelDir.add(Box.createVerticalStrut(15));

        painelDir.add(new JLabel("Sequência de Transformações"));
        comboSequencia = new JComboBox<>(new String[]{"Translação", "Rotação", "Escala", "Cisalhamento", "Reflexão"});
        painelDir.add(comboSequencia);

        cardSeqParams = new CardLayout();
        panelSeqParams = new JPanel(cardSeqParams);
        panelSeqParams.setOpaque(false);

        panelSeqParams.add(criarPanelSeqTrans(), "Translação");
        panelSeqParams.add(criarPanelSeqRot(), "Rotação");
        panelSeqParams.add(criarPanelSeqEscala(), "Escala");
        panelSeqParams.add(criarPanelSeqCis(), "Cisalhamento");
        panelSeqParams.add(criarPanelSeqRef(), "Reflexão");

        comboSequencia.addActionListener(e -> cardSeqParams.show(panelSeqParams, (String) comboSequencia.getSelectedItem()));

        painelDir.add(panelSeqParams);
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(criarBotao("Adicionar à Sequência", e -> adicionarSequencia()));
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(criarBotao("Aplicar Sequência", e -> aplicarSequencia()));

        JScrollPane scroll = new JScrollPane(painelDir);
        scroll.setPreferredSize(new Dimension(260, 0));
        scroll.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));
        add(scroll, BorderLayout.EAST);
    }

    private JPanel criarPanelSeqTrans() {
        JPanel p = new JPanel(new GridLayout(2, 2)); p.setOpaque(false);
        p.add(new JLabel("X:")); p.add(seqTransX = new JTextField("0"));
        p.add(new JLabel("Y:")); p.add(seqTransY = new JTextField("0"));
        return p;
    }
    private JPanel criarPanelSeqRot() {
        JPanel p = new JPanel(new GridLayout(3, 2)); p.setOpaque(false);
        p.add(new JLabel("Ângulo:")); p.add(seqRotAng = new JTextField("0"));
        p.add(new JLabel("Centro X:")); p.add(seqRotCX = new JTextField("0"));
        p.add(new JLabel("Centro Y:")); p.add(seqRotCY = new JTextField("0"));
        return p;
    }
    private JPanel criarPanelSeqEscala() {
        JPanel p = new JPanel(new GridLayout(2, 2)); p.setOpaque(false);
        p.add(new JLabel("X:")); p.add(seqEscalaX = new JTextField("1"));
        p.add(new JLabel("Y:")); p.add(seqEscalaY = new JTextField("1"));
        return p;
    }
    private JPanel criarPanelSeqCis() {
        JPanel p = new JPanel(new GridLayout(2, 2)); p.setOpaque(false);
        p.add(new JLabel("X:")); p.add(seqCisX = new JTextField("0"));
        p.add(new JLabel("Y:")); p.add(seqCisY = new JTextField("0"));
        return p;
    }
    private JPanel criarPanelSeqRef() {
        JPanel p = new JPanel(new GridLayout(2, 1)); p.setOpaque(false);
        p.add(seqRefX = new JCheckBox("Em X")); seqRefX.setOpaque(false);
        p.add(seqRefY = new JCheckBox("Em Y")); seqRefY.setOpaque(false);
        return p;
    }

    private void setupCanvas() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);

        JLabel lblVoltar = new JLabel("Voltar ao Início", SwingConstants.CENTER);
        lblVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        lblVoltar.setBorder(new EmptyBorder(10, 0, 10, 0));
        wrapper.add(lblVoltar, BorderLayout.NORTH);

        canvas = new CanvasPanel();
        wrapper.add(canvas, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    private void gerarQuadrado() {
        try {
            double size = Double.parseDouble(txtQuadTamanho.getText());
            double x = Double.parseDouble(txtQuadPosX.getText());
            double y = Double.parseDouble(txtQuadPosY.getText());

            quadradoAtual.clear();
            quadradoOriginal.clear();

            Point2D.Double p1 = new Point2D.Double(x, y);
            Point2D.Double p2 = new Point2D.Double(x + size, y);
            Point2D.Double p3 = new Point2D.Double(x + size, y + size);
            Point2D.Double p4 = new Point2D.Double(x, y + size);

            quadradoAtual.add(p1); quadradoAtual.add(p2); quadradoAtual.add(p3); quadradoAtual.add(p4);
            quadradoOriginal.add(p1); quadradoOriginal.add(p2); quadradoOriginal.add(p3); quadradoOriginal.add(p4);

            atualizarInfoObjeto();
            canvas.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Valores inválidos para gerar quadrado.");
        }
    }

    private void aplicarTranslacao() {
        if (quadradoAtual.isEmpty()) return;
        try {
            double dx = Double.parseDouble(txtTransX.getText());
            double dy = Double.parseDouble(txtTransY.getText());
            double[][] matriz = Transformacoes2D.criarMatrizTranslacao(dx, dy);
            aplicarMatrizEmTodos(matriz, "Translação: Δx=" + dx + ", Δy=" + dy);
        } catch (Exception e) {}
    }

    private void aplicarEscala() {
        if (quadradoAtual.isEmpty()) return;
        try {
            double sx = Double.parseDouble(txtEscalaX.getText());
            double sy = Double.parseDouble(txtEscalaY.getText());
            Point2D.Double origem = quadradoAtual.get(0);
            double[][] t1 = Transformacoes2D.criarMatrizTranslacao(-origem.x, -origem.y);
            double[][] s = Transformacoes2D.criarMatrizEscala(sx, sy);
            double[][] t2 = Transformacoes2D.criarMatrizTranslacao(origem.x, origem.y);
            double[][] matrizFinal = Transformacoes2D.multiplicarMatrizes(t2, Transformacoes2D.multiplicarMatrizes(s, t1));
            aplicarMatrizEmTodos(matrizFinal, "Escala: Sx=" + sx + ", Sy=" + sy);
        } catch (Exception e) {}
    }

    private void aplicarRotacao() {
        if (quadradoAtual.isEmpty()) return;
        try {
            double ang = Double.parseDouble(txtRotAngulo.getText());
            double cx = Double.parseDouble(txtRotX.getText());
            double cy = Double.parseDouble(txtRotY.getText());
            double[][] matriz = Transformacoes2D.criarMatrizRotacao(ang, cx, cy);
            aplicarMatrizEmTodos(matriz, "Rotação: " + ang + "° em (" + cx + "," + cy + ")");
        } catch (Exception e) {}
    }

    private void aplicarReflexao() {
        if (quadradoAtual.isEmpty()) return;
        boolean rx = chkRefX.isSelected();
        boolean ry = chkRefY.isSelected();
        if (!rx && !ry) return;
        double[][] matriz = Transformacoes2D.criarMatrizReflexao(rx, ry);
        aplicarMatrizEmTodos(matriz, "Reflexão: " + (rx ? "X " : "") + (ry ? "Y" : ""));
    }

    private void aplicarCisalhamento() {
        if (quadradoAtual.isEmpty()) return;
        try {
            double shx = Double.parseDouble(txtCisX.getText());
            double shy = Double.parseDouble(txtCisY.getText());
            double[][] matriz = Transformacoes2D.criarMatrizCisalhamento(shx, shy);
            aplicarMatrizEmTodos(matriz, "Cisalhamento: Shx=" + shx + ", Shy=" + shy);
        } catch (Exception e) {}
    }

    private void aplicarMatrizEmTodos(double[][] matriz, String logText) {
        for (int i = 0; i < quadradoAtual.size(); i++) {
            quadradoAtual.set(i, Transformacoes2D.aplicarTransformacao(quadradoAtual.get(i), matriz));
        }
        addLog(logText);
        atualizarInfoObjeto();
        canvas.repaint();
    }

    private class TransformacaoConfig {
        String tipo; double[][] matriz; String log;
        public TransformacaoConfig(String t, double[][] m, String l) { tipo = t; matriz = m; log = l; }
    }

    private void adicionarSequencia() {
        String tipo = (String) comboSequencia.getSelectedItem();
        try {
            double[][] m = null;
            String log = "";
            switch (tipo) {
                case "Translação":
                    m = Transformacoes2D.criarMatrizTranslacao(Double.parseDouble(seqTransX.getText()), Double.parseDouble(seqTransY.getText()));
                    log = "Seq: Translação"; break;
                case "Rotação":
                    m = Transformacoes2D.criarMatrizRotacao(Double.parseDouble(seqRotAng.getText()), Double.parseDouble(seqRotCX.getText()), Double.parseDouble(seqRotCY.getText()));
                    log = "Seq: Rotação"; break;
                case "Escala":
                    m = Transformacoes2D.criarMatrizEscala(Double.parseDouble(seqEscalaX.getText()), Double.parseDouble(seqEscalaY.getText()));
                    log = "Seq: Escala"; break;
                case "Cisalhamento":
                    m = Transformacoes2D.criarMatrizCisalhamento(Double.parseDouble(seqCisX.getText()), Double.parseDouble(seqCisY.getText()));
                    log = "Seq: Cisalhamento"; break;
                case "Reflexão":
                    m = Transformacoes2D.criarMatrizReflexao(seqRefX.isSelected(), seqRefY.isSelected());
                    log = "Seq: Reflexão"; break;
            }
            if (m != null) {
                sequenciaAtual.add(new TransformacaoConfig(tipo, m, log));
                addLog("Adicionado à sequência: " + tipo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Preencha corretamente os parâmetros da sequência.");
        }
    }

    private void aplicarSequencia() {
        if (quadradoAtual.isEmpty()) return;
        for (TransformacaoConfig config : sequenciaAtual) {
            if (config.tipo.equals("Escala")) {
                Point2D.Double o = quadradoAtual.get(0);
                double[][] t1 = Transformacoes2D.criarMatrizTranslacao(-o.x, -o.y);
                double[][] t2 = Transformacoes2D.criarMatrizTranslacao(o.x, o.y);
                double[][] mFinal = Transformacoes2D.multiplicarMatrizes(t2, Transformacoes2D.multiplicarMatrizes(config.matriz, t1));
                aplicarMatrizEmTodos(mFinal, config.log);
            } else {
                aplicarMatrizEmTodos(config.matriz, config.log);
            }
        }
        sequenciaAtual.clear();
    }

    private void atualizarInfoObjeto() {
        if (quadradoAtual.isEmpty()) {
            txtVertices.setText("Nenhum objeto gerado.");
            txtCentro.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        double cx = 0, cy = 0;
        for (int i = 0; i < quadradoAtual.size(); i++) {
            Point2D.Double p = quadradoAtual.get(i);
            sb.append(String.format(Locale.US, "Vértice %d: (%.2f, %.2f)\n", i + 1, p.x, p.y));
            cx += p.x; cy += p.y;
        }
        txtVertices.setText(sb.toString());
        txtCentro.setText(String.format(Locale.US, "(%.2f, %.2f)", cx / 4, cy / 4));
    }

    private void addLog(String text) {
        historicoStr.append(historicoCount++).append(". ").append(text).append("\n");
        txtHistorico.setText(historicoStr.toString());
    }

    private void limparTudo() {
        quadradoAtual.clear();
        quadradoOriginal.clear();
        sequenciaAtual.clear();
        historicoStr.setLength(0);
        historicoCount = 1;
        txtHistorico.setText("");
        atualizarInfoObjeto();
        canvas.repaint();
    }

    // ==========================================
    // ÁREA DE DESENHO E MAPEAMENTO WINDOW->VIEWPORT
    // ==========================================
    private class CanvasPanel extends JPanel {

        // Limites da Janela do Mundo
        private final double W_XMIN = -150, W_XMAX = 150;
        private final double W_YMIN = -150, W_YMAX = 150;

        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }

        // Mapeamento matemático do Mundo para uma região específica (Viewport) da tela
        private Point2D.Double mapWorldToScreen(Point2D.Double pt, int vXmin, int vXmax, int vYmin, int vYmax) {
            double sx = vXmin + ((pt.x - W_XMIN) / (W_XMAX - W_XMIN)) * (vXmax - vXmin);
            double sy = vYmin + ((W_YMAX - pt.y) / (W_YMAX - W_YMIN)) * (vYmax - vYmin); // Y inverte na tela
            return new Point2D.Double(sx, sy);
        }

        // ==========================================
        // ALGORITMO DE RECORTE (COHEN-SUTHERLAND)
        // Aplicado após o mapeamento (nas coordenadas da viewport)
        // ==========================================
        private int computeOutCodeScreen(double x, double y, double xmin, double ymin, double xmax, double ymax) {
            int code = 0;
            if (x < xmin) code |= 1; // ESQUERDA
            else if (x > xmax) code |= 2; // DIREITA
            if (y < ymin) code |= 8; // TOPO (Y cresce pra baixo na tela)
            else if (y > ymax) code |= 4; // FUNDO
            return code;
        }

        private double[] cohenSutherlandClip(double x0, double y0, double x1, double y1, double xmin, double ymin, double xmax, double ymax) {
            int outcode0 = computeOutCodeScreen(x0, y0, xmin, ymin, xmax, ymax);
            int outcode1 = computeOutCodeScreen(x1, y1, xmin, ymin, xmax, ymax);
            boolean accept = false;

            while (true) {
                if ((outcode0 | outcode1) == 0) { // Totalmente dentro
                    accept = true;
                    break;
                } else if ((outcode0 & outcode1) != 0) { // Totalmente fora
                    break;
                } else {
                    double x = 0, y = 0;
                    int outcodeOut = (outcode0 != 0) ? outcode0 : outcode1;

                    if ((outcodeOut & 8) != 0) { // TOPO
                        x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
                        y = ymin;
                    } else if ((outcodeOut & 4) != 0) { // FUNDO
                        x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
                        y = ymax;
                    } else if ((outcodeOut & 2) != 0) { // DIREITA
                        y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
                        x = xmax;
                    } else if ((outcodeOut & 1) != 0) { // ESQUERDA
                        y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
                        x = xmin;
                    }

                    if (outcodeOut == outcode0) {
                        x0 = x; y0 = y;
                        outcode0 = computeOutCodeScreen(x0, y0, xmin, ymin, xmax, ymax);
                    } else {
                        x1 = x; y1 = y;
                        outcode1 = computeOutCodeScreen(x1, y1, xmin, ymin, xmax, ymax);
                    }
                }
            }
            if (accept) return new double[]{x0, y0, x1, y1};
            return null; // Linha rejeitada
        }

        private void drawLineDDA(Graphics g, double x1, double y1, double x2, double y2) {
            double length = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
            double xinc = (x2 - x1) / length;
            double yinc = (y2 - y1) / length;

            double x = x1;
            double y = y1;

            g.fillRect((int)Math.round(x), (int)Math.round(y), 1, 1);
            for (int i = 0; i < length; i++) {
                x += xinc;
                y += yinc;
                g.fillRect((int)Math.round(x), (int)Math.round(y), 1, 1);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(); int h = getHeight();

            // Dividindo a tela em Duas Áreas: Esquerda (Janela/Mundo) e Direita (Viewport/Processado)
            int winX = 10, winY = 30, winW = w/2 - 20, winH = h - 40;
            int vpX = w/2 + 10, vpY = 30, vpW = w/2 - 20, vpH = h - 40;

            // Fundo Janela
            g.setColor(new Color(245, 245, 245)); g.fillRect(winX, winY, winW, winH);
            g.setColor(Color.LIGHT_GRAY); g.drawRect(winX, winY, winW, winH);
            g.setColor(Color.BLACK); g.drawString("Janela do Mundo (Original)", winX, winY - 5);
            // Eixos da Janela
            g.drawLine(winX + winW/2, winY, winX + winW/2, winY + winH);
            g.drawLine(winX, winY + winH/2, winX + winW, winY + winH/2);

            // Fundo Viewport
            g.setColor(Color.WHITE); g.fillRect(vpX, vpY, vpW, vpH);
            g.setColor(Color.BLUE); g.drawRect(vpX, vpY, vpW, vpH);
            g.setColor(Color.BLACK); g.drawString("Viewport (Transformado + Recorte)", vpX, vpY - 5);
            // Eixos do Viewport


            if (quadradoOriginal.isEmpty()) return;

            // 1. Desenhar Objeto Original Mapeado na Janela Esquerda
            g.setColor(Color.GRAY);
            for (int i = 0; i < quadradoOriginal.size(); i++) {
                Point2D.Double p1 = quadradoOriginal.get(i);
                Point2D.Double p2 = quadradoOriginal.get((i + 1) % quadradoOriginal.size());
                Point2D.Double sp1 = mapWorldToScreen(p1, winX, winX+winW, winY, winY+winH);
                Point2D.Double sp2 = mapWorldToScreen(p2, winX, winX+winW, winY, winY+winH);
                drawLineDDA(g, sp1.x, sp1.y, sp2.x, sp2.y);
            }

            // 2. Mapear e Aplicar Recorte no Objeto Transformado na Viewport Direita
            g.setColor(Color.BLACK);
            for (int i = 0; i < quadradoAtual.size(); i++) {
                Point2D.Double p1 = quadradoAtual.get(i);
                Point2D.Double p2 = quadradoAtual.get((i + 1) % quadradoAtual.size());

                // Passo 1: Mapeia pra tela do Viewport
                Point2D.Double sp1 = mapWorldToScreen(p1, vpX, vpX+vpW, vpY, vpY+vpH);
                Point2D.Double sp2 = mapWorldToScreen(p2, vpX, vpX+vpW, vpY, vpY+vpH);

                // Passo 2: Aplica o Algoritmo de Recorte (Cohen-Sutherland) na Viewport
                double[] clipped = cohenSutherlandClip(sp1.x, sp1.y, sp2.x, sp2.y, vpX, vpY, vpX+vpW, vpY+vpH);

                // Passo 3: Se houver intersecção com a Viewport, desenha a reta recortada
                if (clipped != null) {
                    drawLineDDA(g, clipped[0], clipped[1], clipped[2], clipped[3]);
                }
            }
        }
    }
}