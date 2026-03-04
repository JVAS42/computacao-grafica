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

    // ==========================================
    // PAINEL ESQUERDO
    // ==========================================
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

        // Blocos
        painelEsq.add(criarBlocoDuplo("Translação:", "X:", txtTransX = new JTextField("0"), "Y:", txtTransY = new JTextField("0"), "Aplicar Translação", this::aplicarTranslacao));
        painelEsq.add(criarBlocoDuplo("Escala:", "X:", txtEscalaX = new JTextField("1"), "Y:", txtEscalaY = new JTextField("1"), "Aplicar Escala", this::aplicarEscala));

        // Rotação (3 campos)
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

        // Reflexão
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

        // Configurar Quadrado
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

    // ==========================================
    // PAINEL DIREITO
    // ==========================================
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

        // Painel dinâmico para a Sequência usando CardLayout
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

    // Paineis auxiliares para o CardLayout da Sequencia
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

    // ==========================================
    // CANVAS
    // ==========================================
    private void setupCanvas() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);

        JLabel lblVoltar = new JLabel("Voltar ao Início", SwingConstants.CENTER);
        lblVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        lblVoltar.setBorder(new EmptyBorder(10, 0, 10, 0));
        wrapper.add(lblVoltar, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500));
        centerWrapper.add(canvas);
        wrapper.add(centerWrapper, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ==========================================
    // LÓGICA DE TRANSFORMAÇÕES E EVENTOS
    // ==========================================
    private void gerarQuadrado() {
        try {
            double size = Double.parseDouble(txtQuadTamanho.getText());
            double x = Double.parseDouble(txtQuadPosX.getText());
            double y = Double.parseDouble(txtQuadPosY.getText());

            quadradoAtual.clear();
            quadradoAtual.add(new Point2D.Double(x, y));
            quadradoAtual.add(new Point2D.Double(x + size, y));
            quadradoAtual.add(new Point2D.Double(x + size, y + size));
            quadradoAtual.add(new Point2D.Double(x, y + size));

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

            // Escala em torno do primeiro vértice
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

    // ==========================================
    // SEQUÊNCIA DE TRANSFORMAÇÕES
    // ==========================================
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
                    // Na sequencia, a escala precisa do centro do objeto na hora de aplicar.
                    // Para simplificar e seguir a risca o JS, guardaremos a matriz crua se for na origem, ou faremos o cálculo no "aplicar"
                    // Como o JS faz a escala da sequencia em torno do vértice 0, faremos o cálculo na hora de aplicar.
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

    // ==========================================
    // FUNÇÕES UTILITÁRIAS
    // ==========================================
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
        sequenciaAtual.clear();
        historicoStr.setLength(0);
        historicoCount = 1;
        txtHistorico.setText("");
        atualizarInfoObjeto();
        canvas.repaint();
    }

    // ==========================================
    // ÁREA DE DESENHO (Canvas)
    // ==========================================
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }

        // Simula o setPixel do JS
        private void setPixel(Graphics g, double cartX, double cartY) {
            int screenX = (int) Math.round(cartX + getWidth() / 2.0);
            int screenY = (int) Math.round(getHeight() / 2.0 - cartY);
            g.fillRect(screenX, screenY, 1, 1);
        }

        // Reimplementa o DDA internamente para simular o comportamento exato do JS na plotagem das arestas
        private void drawLineDDA(Graphics g, Point2D.Double p1, Point2D.Double p2) {
            double length = Math.max(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
            double xinc = (p2.x - p1.x) / length;
            double yinc = (p2.y - p1.y) / length;

            double x = p1.x;
            double y = p1.y;

            setPixel(g, x, y);
            for (int i = 0; i < length; i++) {
                x += xinc;
                y += yinc;
                setPixel(g, x, y);
            }
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
            if (!quadradoAtual.isEmpty()) {
                for (int i = 0; i < quadradoAtual.size(); i++) {
                    Point2D.Double p1 = quadradoAtual.get(i);
                    Point2D.Double p2 = quadradoAtual.get((i + 1) % quadradoAtual.size());
                    drawLineDDA(g, p1, p2);
                }
            }
        }
    }
}
