package main.ui.panels;

import main.algorithms.Transformacoes2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.awt.geom.Area;


public class Transformacoes2DPanel extends JPanel {

    private final Color COR_FUNDO = new Color(245, 245, 245);
    private final Color COR_BOTAO = new Color(60, 140, 60);
    private final int LARGURA_LATERAL = 280;

    // Controles Esq - Translação
    private JTextField txtTransX, txtTransY;
    private JTextField txtEscalaX, txtEscalaY;
    private JTextField txtRotX, txtRotY, txtRotAngulo;
    private JCheckBox chkRefX, chkRefY;
    private JTextField txtCisX, txtCisY;
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

    // Estado
    private CanvasPanel canvas;
    private List<Point2D.Double> quadradoAtual = new ArrayList<>();
    private List<Point2D.Double> quadradoOriginal = new ArrayList<>();
    private List<TransformacaoConfig> sequenciaAtual = new ArrayList<>();
    private StringBuilder historicoStr = new StringBuilder();
    private int historicoCount = 1;

    // Controles de Viewport (Adicionar aos campos de classe)
    private JTextField txtVpMinX, txtVpMaxX, txtVpMinY, txtVpMaxY;
    private double vpMinX = -250, vpMaxX = 250, vpMinY = -250, vpMaxY = 250;
    private JCheckBox chkAtivarVp; // Nova caixa de seleção


    public Transformacoes2DPanel() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
    }

    private void setupPainelEsquerdo() {
        JPanel containerEsq = new JPanel();
        containerEsq.setLayout(new BoxLayout(containerEsq, BoxLayout.Y_AXIS));
        containerEsq.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bloco Translação
        containerEsq.add(criarSecaoSimples("Translação", "X:", txtTransX = new JTextField("0"), "Y:", txtTransY = new JTextField("0"), "Aplicar Translação", this::aplicarTranslacao));

        // Bloco Escala
        containerEsq.add(criarSecaoSimples("Escala", "X:", txtEscalaX = new JTextField("1"), "Y:", txtEscalaY = new JTextField("1"), "Aplicar Escala", this::aplicarEscala));

        // Bloco Rotação - CORREÇÃO: Restaurando estilo visual original
        JPanel pnlRot = new JPanel(new GridBagLayout());
        pnlRot.setBorder(BorderFactory.createTitledBorder("Rotação"));

        // Criando os labels de texto simples como na imagem original
        JLabel lblRotX = new JLabel("X:");
        JLabel lblRotY = new JLabel("Y:");
        JLabel lblRotAng = new JLabel("Ângulo:");

        txtRotX = new JTextField("0");
        txtRotY = new JTextField("0");
        txtRotAngulo = new JTextField("0");

        // Usando GridBagLayout para alinhar os componentes, mas mantendo a aparência de texto simples
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: X
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.0; // Label não cresce
        pnlRot.add(lblRotX, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; // Campo de texto cresce
        pnlRot.add(txtRotX, gbc);

        // Linha 1: Y
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.0;
        pnlRot.add(lblRotY, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        pnlRot.add(txtRotY, gbc);

        // Linha 2: Ângulo
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.0;
        pnlRot.add(lblRotAng, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        pnlRot.add(txtRotAngulo, gbc);

        // Linha 3: Botão
        adicionarBotaoGrid(pnlRot, 3, "Aplicar Rotação", e -> aplicarRotacao());
        containerEsq.add(pnlRot);

        // Bloco Reflexão (MANTIDO)
        JPanel pnlRef = new JPanel(new GridBagLayout());
        pnlRef.setBorder(BorderFactory.createTitledBorder("Reflexão"));
        JPanel pnlChecks = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlChecks.add(chkRefX = new JCheckBox("Em X"));
        pnlChecks.add(chkRefY = new JCheckBox("Em Y"));
        gbc = new GridBagConstraints();
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlRef.add(pnlChecks, gbc);
        adicionarBotaoGrid(pnlRef, 1, "Aplicar Reflexão", e -> aplicarReflexao());
        adicionarBotaoGrid(pnlRef, 2, "Remover Reflexão", e -> removerReflexao());
        containerEsq.add(pnlRef);

        // Bloco Cisalhamento (MANTIDO)
        containerEsq.add(criarSecaoSimples("Cisalhamento", "X:", txtCisX = new JTextField("0"), "Y:", txtCisY = new JTextField("0"), "Aplicar Cisalhamento", this::aplicarCisalhamento));

        // Bloco Gerar Quadrado (MANTIDO)
        JPanel pnlQuad = new JPanel(new GridBagLayout());
        pnlQuad.setBorder(BorderFactory.createTitledBorder("Configurar Quadrado"));
        adicionarComponenteGrid(pnlQuad, 0, "Tamanho:", txtQuadTamanho = new JTextField("50"));
        adicionarComponenteGrid(pnlQuad, 1, "Posição X:", txtQuadPosX = new JTextField("0"));
        adicionarComponenteGrid(pnlQuad, 2, "Posição Y:", txtQuadPosY = new JTextField("0"));
        adicionarBotaoGrid(pnlQuad, 3, "Gerar Quadrado", e -> gerarQuadrado());
        containerEsq.add(pnlQuad);

        JScrollPane scroll = new JScrollPane(containerEsq);
        scroll.setPreferredSize(new Dimension(LARGURA_LATERAL, 0));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.WEST);
    }

    private void setupPainelDireito() {
        JPanel containerDir = new JPanel();
        containerDir.setLayout(new BoxLayout(containerDir, BoxLayout.Y_AXIS));
        containerDir.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Vértices
        JPanel pnlVert = new JPanel(new BorderLayout());
        pnlVert.setBorder(BorderFactory.createTitledBorder("Vértices do Objeto"));
        txtVertices = new JTextArea(6, 15);
        txtVertices.setEditable(false);
        txtVertices.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pnlVert.add(new JScrollPane(txtVertices), BorderLayout.CENTER);
        containerDir.add(pnlVert);

        // Centro
        txtCentro = new JTextField();
        txtCentro.setEditable(false);
        txtCentro.setBorder(BorderFactory.createTitledBorder("Centro da Massa"));
        containerDir.add(txtCentro);

        containerDir.add(Box.createVerticalStrut(10));
        JButton btnLimpar = criarBotaoBase("Limpar Tudo", e -> limparTudo());
        btnLimpar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        containerDir.add(btnLimpar);

        // Histórico
        txtHistorico = new JTextArea(6, 15);
        txtHistorico.setEditable(false);
        JScrollPane scrollHist = new JScrollPane(txtHistorico);
        scrollHist.setBorder(BorderFactory.createTitledBorder("Histórico de Ações"));
        containerDir.add(scrollHist);

        //ViewPort
        containerDir.add(criarPainelViewport());

        // Sequência
        JPanel pnlSeq = new JPanel(new GridBagLayout());
        pnlSeq.setBorder(BorderFactory.createTitledBorder("Sequência de Comandos"));
        comboSequencia = new JComboBox<>(new String[]{"Translação", "Rotação", "Escala", "Cisalhamento", "Reflexão"});

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 5, 2);
        pnlSeq.add(comboSequencia, gbc);

        cardSeqParams = new CardLayout();
        panelSeqParams = new JPanel(cardSeqParams);
        panelSeqParams.add(criarPanelSeqTrans(), "Translação");
        panelSeqParams.add(criarPanelSeqRot(), "Rotação");
        panelSeqParams.add(criarPanelSeqEscala(), "Escala");
        panelSeqParams.add(criarPanelSeqCis(), "Cisalhamento");
        panelSeqParams.add(criarPanelSeqRef(), "Reflexão");

        gbc.gridy = 1;
        pnlSeq.add(panelSeqParams, gbc);

        comboSequencia.addActionListener(e -> cardSeqParams.show(panelSeqParams, (String) comboSequencia.getSelectedItem()));

        adicionarBotaoGrid(pnlSeq, 2, "Adicionar à Lista", e -> adicionarSequencia());
        adicionarBotaoGrid(pnlSeq, 3, "Aplicar Lista", e -> aplicarSequencia());

        containerDir.add(pnlSeq);

        JScrollPane scroll = new JScrollPane(containerDir);
        scroll.setPreferredSize(new Dimension(LARGURA_LATERAL, 0));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.EAST);
    }

    // --- Helpers de UI ---

    private JPanel criarSecaoSimples(String titulo, String l1, JTextField f1, String l2, JTextField f2, String btnT, Runnable acao) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        adicionarComponenteGrid(p, 0, l1, f1);
        adicionarComponenteGrid(p, 1, l2, f2);
        adicionarBotaoGrid(p, 2, btnT, e -> acao.run());
        return p;
    }

    private void adicionarComponenteGrid(JPanel p, int row, String label, JTextField field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.3;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(field, gbc);
    }

    private void adicionarBotaoGrid(JPanel p, int row, String text, java.awt.event.ActionListener acao) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);
        p.add(criarBotaoBase(text, acao), gbc);
    }

    private JButton criarBotaoBase(String t, java.awt.event.ActionListener a) {
        JButton b = new JButton(t);
        b.setBackground(COR_BOTAO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.addActionListener(a);
        return b;
    }

    // --- Panels de Sequência (Refatorados para Grid) ---

    private JPanel criarPanelSeqTrans() {
        JPanel p = new JPanel(new GridBagLayout());
        adicionarComponenteGrid(p, 0, "X:", seqTransX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", seqTransY = new JTextField("0"));
        return p;
    }
    private JPanel criarPanelSeqRot() {
        JPanel p = new JPanel(new GridBagLayout());
        adicionarComponenteGrid(p, 0, "Ang:", seqRotAng = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "CX:", seqRotCX = new JTextField("0"));
        adicionarComponenteGrid(p, 2, "CY:", seqRotCY = new JTextField("0"));
        return p;
    }
    private JPanel criarPanelSeqEscala() {
        JPanel p = new JPanel(new GridBagLayout());
        adicionarComponenteGrid(p, 0, "X:", seqEscalaX = new JTextField("1"));
        adicionarComponenteGrid(p, 1, "Y:", seqEscalaY = new JTextField("1"));
        return p;
    }
    private JPanel criarPanelSeqCis() {
        JPanel p = new JPanel(new GridBagLayout());
        adicionarComponenteGrid(p, 0, "X:", seqCisX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", seqCisY = new JTextField("0"));
        return p;
    }
    private JPanel criarPanelSeqRef() {
        JPanel p = new JPanel(new FlowLayout());
        p.add(seqRefX = new JCheckBox("X"));
        p.add(seqRefY = new JCheckBox("Y"));
        return p;
    }

    private void setupCanvas() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.GRAY);
        JLabel lbl = new JLabel("Visualização Computacional", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
        wrapper.add(lbl, BorderLayout.NORTH);
        canvas = new CanvasPanel();
        wrapper.add(canvas, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    // ==========================================
    // LÓGICA DE TRANSFORMAÇÕES (MANTIDA ORIGINAL)
    // ==========================================

    private void gerarQuadrado() {
        try {
            double size = Double.parseDouble(txtQuadTamanho.getText());
            double x = Double.parseDouble(txtQuadPosX.getText());
            double y = Double.parseDouble(txtQuadPosY.getText());
            quadradoAtual.clear(); quadradoOriginal.clear();
            Point2D.Double p1 = new Point2D.Double(x, y);
            Point2D.Double p2 = new Point2D.Double(x + size, y);
            Point2D.Double p3 = new Point2D.Double(x + size, y + size);
            Point2D.Double p4 = new Point2D.Double(x, y + size);
            quadradoAtual.add(p1); quadradoAtual.add(p2); quadradoAtual.add(p3); quadradoAtual.add(p4);
            quadradoOriginal.add(p1); quadradoOriginal.add(p2); quadradoOriginal.add(p3); quadradoOriginal.add(p4);
            atualizarInfoObjeto(); canvas.repaint();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Valores inválidos."); }
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
        boolean rx = chkRefX.isSelected(); boolean ry = chkRefY.isSelected();
        if (!rx && !ry) return;
        double[][] matriz = Transformacoes2D.criarMatrizReflexao(rx, ry);
        aplicarMatrizEmTodos(matriz, "Reflexão: " + (rx ? "X " : "") + (ry ? "Y" : ""));
    }

    private void removerReflexao() {
        if (quadradoOriginal.isEmpty()) return;
        quadradoAtual.clear();
        for (Point2D.Double p : quadradoOriginal) quadradoAtual.add(new Point2D.Double(p.x, p.y));
        chkRefX.setSelected(false); chkRefY.setSelected(false);
        addLog("Objeto resetado ao original");
        atualizarInfoObjeto(); canvas.repaint();
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
        atualizarInfoObjeto(); canvas.repaint();
    }

    private void adicionarSequencia() {
        String tipo = (String) comboSequencia.getSelectedItem();
        try {
            double[][] m = null; String log = "";
            switch (tipo) {
                case "Translação": m = Transformacoes2D.criarMatrizTranslacao(Double.parseDouble(seqTransX.getText()), Double.parseDouble(seqTransY.getText())); log = "Seq: Translação"; break;
                case "Rotação": m = Transformacoes2D.criarMatrizRotacao(Double.parseDouble(seqRotAng.getText()), Double.parseDouble(seqRotCX.getText()), Double.parseDouble(seqRotCY.getText())); log = "Seq: Rotação"; break;
                case "Escala": m = Transformacoes2D.criarMatrizEscala(Double.parseDouble(seqEscalaX.getText()), Double.parseDouble(seqEscalaY.getText())); log = "Seq: Escala"; break;
                case "Cisalhamento": m = Transformacoes2D.criarMatrizCisalhamento(Double.parseDouble(seqCisX.getText()), Double.parseDouble(seqCisY.getText())); log = "Seq: Cisalhamento"; break;
                case "Reflexão": m = Transformacoes2D.criarMatrizReflexao(seqRefX.isSelected(), seqRefY.isSelected()); log = "Seq: Reflexão"; break;
            }
            if (m != null) {
                sequenciaAtual.add(new TransformacaoConfig(tipo, m, log));
                addLog("Adicionado à sequência: " + tipo);
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro nos parâmetros da sequência."); }
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
            } else { aplicarMatrizEmTodos(config.matriz, config.log); }
        }
        sequenciaAtual.clear();
    }

    private void atualizarInfoObjeto() {
        if (quadradoAtual.isEmpty()) {
            txtVertices.setText("Nenhum objeto."); txtCentro.setText(""); return;
        }
        StringBuilder sb = new StringBuilder();
        double cx = 0, cy = 0;
        for (int i = 0; i < quadradoAtual.size(); i++) {
            Point2D.Double p = quadradoAtual.get(i);
            sb.append(String.format(Locale.US, "V%d: (%.1f, %.1f)\n", i + 1, p.x, p.y));
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
        quadradoAtual.clear(); quadradoOriginal.clear(); sequenciaAtual.clear();
        historicoStr.setLength(0); historicoCount = 1; txtHistorico.setText("");
        atualizarInfoObjeto(); canvas.repaint();
    }

    private class TransformacaoConfig {
        String tipo; double[][] matriz; String log;
        public TransformacaoConfig(String t, double[][] m, String l) { tipo = t; matriz = m; log = l; }
    }

    private void setCamposVpEnabled(boolean b) {
        txtVpMinX.setEnabled(b);
        txtVpMaxX.setEnabled(b);
        txtVpMinY.setEnabled(b);
        txtVpMaxY.setEnabled(b);
    }

    private JPanel criarPainelViewport() {
        JPanel pnlVp = new JPanel(new GridBagLayout());
        pnlVp.setBorder(BorderFactory.createTitledBorder("Configurar ViewPort"));

        chkAtivarVp = new JCheckBox("Ativar Viewport");

        // Inicializa campos (desabilitados por padrão)
        txtVpMinX = new JTextField("-100", 5);
        txtVpMaxX = new JTextField("100", 5);
        txtVpMinY = new JTextField("-100", 5);
        txtVpMaxY = new JTextField("100", 5);

        setCamposVpEnabled(false); // Começa desativado

        // Listener para o Checkbox
        chkAtivarVp.addActionListener(e -> {
            boolean ativo = chkAtivarVp.isSelected();
            setCamposVpEnabled(ativo);
            canvas.repaint(); // Redesenha para mostrar/esconder o pontilhado
        });

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pnlVp.add(chkAtivarVp, gbc);

        adicionarComponenteGrid(pnlVp, 1, "Min X:", txtVpMinX);
        adicionarComponenteGrid(pnlVp, 2, "Max X:", txtVpMaxX);
        adicionarComponenteGrid(pnlVp, 3, "Min Y:", txtVpMinY);
        adicionarComponenteGrid(pnlVp, 4, "Max Y:", txtVpMaxY);

        adicionarBotaoGrid(pnlVp, 5, "Aplicar Limites", e -> aplicarViewport());

        return pnlVp;
    }

    private void aplicarViewport() {
        try {
            vpMinX = Double.parseDouble(txtVpMinX.getText());
            vpMaxX = Double.parseDouble(txtVpMaxX.getText());
            vpMinY = Double.parseDouble(txtVpMinY.getText());
            vpMaxY = Double.parseDouble(txtVpMaxY.getText());

            historicoStr.append(historicoCount++).append(". Viewport atualizada\n");
            txtHistorico.setText(historicoStr.toString());
            canvas.repaint(); // Redesenha com a nova escala
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores de Viewport inválidos.");
        }
    }

    private Point2D.Double mapWorldToScreen(Point2D.Double p, int screenW, int screenH) {
        // Calcula a posição relativa (0.0 a 1.0) dentro dos limites da Viewport
        double xRelativo = (p.x - vpMinX) / (vpMaxX - vpMinX);
        double yRelativo = (p.y - vpMinY) / (vpMaxY - vpMinY);

        // Multiplica pelo tamanho real da tela (pixels)
        double sx = xRelativo * screenW;
        // Inverte o Y: no Java o 0 é no topo, no plano cartesiano o 0 é embaixo
        double sy = (1.0 - yRelativo) * screenH;

        return new Point2D.Double(sx, sy);
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
        private Point2D.Double mapWorldToScreen(Point2D.Double p, double minX, double maxX, double minY, double maxY, int width, int height) {
            // Regra de três para mapear o intervalo [min, max] para [0, largura/altura]
            double sx = (p.x - minX) / (maxX - minX) * width;
            // Inversão do eixo Y (no Java Y cresce para baixo)
            double sy = (1 - (p.y - minY) / (maxY - minY)) * height;
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

        private Point2D.Double worldToCanvas(double x, double y, int width, int height) {
            // Mapeia as coordenadas para que o centro (0,0) fique no meio do painel
            double canvasX = x + (width / 2.0);
            double canvasY = (height / 2.0) - y;
            return new Point2D.Double(canvasX, canvasY);
        }

        // Função utilitária para calcular os limites da Viewport em pixels
        private Rectangle getLimitesViewportPixels(int w, int h) {
            if (chkAtivarVp == null || !chkAtivarVp.isSelected()) {
                return null;
            }
            try {
                double vMinX = Double.parseDouble(txtVpMinX.getText());
                double vMaxX = Double.parseDouble(txtVpMaxX.getText());
                double vMinY = Double.parseDouble(txtVpMinY.getText());
                double vMaxY = Double.parseDouble(txtVpMaxY.getText());

                Point2D.Double pMin = worldToCanvas(vMinX, vMinY, w, h);
                Point2D.Double pMax = worldToCanvas(vMaxX, vMaxY, w, h);

                int x = (int) Math.min(pMin.x, pMax.x);
                int y = (int) Math.min(pMin.y, pMax.y);
                int largura = (int) Math.abs(pMax.x - pMin.x);
                int altura = (int) Math.abs(pMax.y - pMin.y);

                return new Rectangle(x, y, largura, altura);
            } catch (Exception e) {
                return null;
            }
        }


        private void desenharObjeto(Graphics2D g2, int w, int h) {
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < quadradoAtual.size(); i++) {
                Point2D.Double p1 = quadradoAtual.get(i);
                Point2D.Double p2 = quadradoAtual.get((i + 1) % quadradoAtual.size());

                // Usa a tua função de conversão para pixels
                Point2D.Double s1 = worldToCanvas(p1.x, p1.y, w, h);
                Point2D.Double s2 = worldToCanvas(p2.x, p2.y, w, h);

                g2.drawLine((int)s1.x, (int)s1.y, (int)s2.x, (int)s2.y);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();

            // 1. Desenha os eixos cinzentos de fundo
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(w / 2, 0, w / 2, h);
            g2.drawLine(0, h / 2, w, h / 2);

            if (quadradoAtual != null && !quadradoAtual.isEmpty()) {

                // Se a Viewport estiver ativa, usamos a lógica de duas cores
                if (chkAtivarVp != null && chkAtivarVp.isSelected()) {
                    Rectangle limitesVp = getLimitesViewportPixels(w, h);

                    if (limitesVp != null) {
                        Shape clipOriginal = g2.getClip();

                        // --- CAMADA PRETA (DENTRO) ---
                        g2.setClip(limitesVp);
                        g2.setColor(Color.BLACK);
                        desenharObjeto(g2, w, h);

                        // --- CAMADA CINZENTA (FORA) ---
                        Area foraDaVp = new Area(new Rectangle(0, 0, w, h));
                        foraDaVp.subtract(new Area(limitesVp));
                        g2.setClip(foraDaVp);
                        g2.setColor(new Color(180, 180, 180));
                        desenharObjeto(g2, w, h);

                        // Restaura o clip e desenha a borda vermelha por cima
                        g2.setClip(clipOriginal);

                        // Desenha o pontilhado vermelho
                        float[] dash = {5.0f};
                        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                        g2.setColor(Color.RED);
                        g2.draw(limitesVp);
                    }
                } else {
                    // Se a viewport estiver desligada, desenha tudo normal em preto
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                    desenharObjeto(g2, w, h);
                }
            }
        }
    }
}