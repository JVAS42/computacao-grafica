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

    private final Color COR_FUNDO = new Color(240, 240, 240); // #F0F0F0
    private final Color COR_BOTAO = new Color(33, 53, 85);    // #213555
    private final int LARGURA_LATERAL_ESQ = 320;
    private final int LARGURA_LATERAL_DIR = 280;

    // Controles Esq - Objeto
    private JTextField txtQuadTamanho, txtQuadPosX, txtQuadPosY;

    // Controles Esq - Translação
    private JTextField txtTransX, txtTransY;
    private JTextField txtEscalaX, txtEscalaY;
    private JTextField txtRotX, txtRotY, txtRotAngulo;
    private JCheckBox chkRefX, chkRefY;
    private JTextField txtCisX, txtCisY;

    // Controles Dir
    private JTextArea txtVertices, txtHistorico;
    private JTextField txtCentro, txtDimensoes;

    // Campos Sequência
    private JComboBox<String> comboSequencia;
    private JPanel panelSeqParams;
    private CardLayout cardSeqParams;
    private JTextField seqTransX, seqTransY;
    private JTextField seqRotAng, seqRotCX, seqRotCY;
    private JTextField seqEscalaX, seqEscalaY;
    private JTextField seqCisX, seqCisY;
    private JCheckBox seqRefX, seqRefY;

    // Estado
    private MundoCanvas canvasMundo;
    private ViewportCanvas canvasViewport;
    private List<Point2D.Double> quadradoAtual = new ArrayList<>();
    private List<Point2D.Double> quadradoOriginal = new ArrayList<>();
    private List<TransformacaoConfig> sequenciaAtual = new ArrayList<>();
    private StringBuilder historicoStr = new StringBuilder();
    private int historicoCount = 1;

    // Controles de Viewport
    private JTextField txtVpMinX, txtVpMaxX, txtVpMinY, txtVpMaxY;
    private JCheckBox chkAtivarVp;

    public Transformacoes2DPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COR_FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setupPainelEsquerdo();
        setupPainelCentral();
        setupPainelDireito();
    }

    private void setupPainelEsquerdo() {
        JPanel containerEsq = new JPanel();
        containerEsq.setLayout(new BoxLayout(containerEsq, BoxLayout.Y_AXIS));
        containerEsq.setBackground(COR_FUNDO);

        // 1. Objeto 2D
        JPanel pnlQuad = new JPanel(new GridBagLayout());
        pnlQuad.setBorder(BorderFactory.createTitledBorder("Objeto 2D"));
        pnlQuad.setBackground(COR_FUNDO);
        adicionarComponenteGrid(pnlQuad, 0, "Tamanho:", txtQuadTamanho = new JTextField("50"));
        adicionarComponenteGrid(pnlQuad, 1, "Posição X:", txtQuadPosX = new JTextField("0"));
        adicionarComponenteGrid(pnlQuad, 2, "Posição Y:", txtQuadPosY = new JTextField("0"));
        adicionarBotaoGrid(pnlQuad, 3, "Gerar Objeto", e -> gerarQuadrado());
        containerEsq.add(pnlQuad);
        containerEsq.add(Box.createVerticalStrut(10));

        // 2. Transformações (Abas)
        JTabbedPane tabTransformacoes = new JTabbedPane();
        tabTransformacoes.addTab("Trans", criarPainelTranslacao());
        tabTransformacoes.addTab("Rot", criarPainelRotacao());
        tabTransformacoes.addTab("Escala", criarPainelEscala());
        tabTransformacoes.addTab("Cisal", criarPainelCisalhamento());
        tabTransformacoes.addTab("Reflex", criarPainelReflexao());
        containerEsq.add(tabTransformacoes);
        containerEsq.add(Box.createVerticalStrut(10));

        // 3. Sequência
        containerEsq.add(criarPainelSequencia());
        containerEsq.add(Box.createVerticalStrut(10));

        // 4. Viewport Config
        containerEsq.add(criarPainelViewportConfig());

        JScrollPane scroll = new JScrollPane(containerEsq);
        scroll.setPreferredSize(new Dimension(LARGURA_LATERAL_ESQ, 0));
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.WEST);
    }

    private void setupPainelCentral() {
        // Usando GridBagLayout no container principal para impedir que as janelas estiquem
        JPanel containerCentro = new JPanel(new GridBagLayout());
        containerCentro.setBackground(COR_FUNDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 0, 20); // Espaçamento horizontal entre as duas janelas

        // --- Janela do Mundo ---
        JPanel boxMundo = new JPanel(new BorderLayout());
        boxMundo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblMundo = new JLabel("Mundo (500x500)", SwingConstants.CENTER);
        estilizarCabecalho(lblMundo);

        canvasMundo = new MundoCanvas();
        canvasMundo.setPreferredSize(new Dimension(500, 500)); // Trava o tamanho exato do canvas

        boxMundo.add(lblMundo, BorderLayout.NORTH);
        boxMundo.add(canvasMundo, BorderLayout.CENTER);

        // --- Janela da Viewport ---
        JPanel boxViewport = new JPanel(new BorderLayout());
        boxViewport.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblVp = new JLabel("Viewport (300x300)", SwingConstants.CENTER);
        estilizarCabecalho(lblVp);

        canvasViewport = new ViewportCanvas();
        canvasViewport.setPreferredSize(new Dimension(300, 300)); // Trava o tamanho exato do canvas

        boxViewport.add(lblVp, BorderLayout.NORTH);
        boxViewport.add(canvasViewport, BorderLayout.CENTER);

        // Adicionando as duas caixas ao centro
        gbc.gridx = 0;
        containerCentro.add(boxMundo, gbc);

        gbc.gridx = 1;
        containerCentro.add(boxViewport, gbc);

        add(containerCentro, BorderLayout.CENTER);
    }

    private void setupPainelDireito() {
        JPanel containerDir = new JPanel();
        containerDir.setLayout(new BoxLayout(containerDir, BoxLayout.Y_AXIS));
        containerDir.setBackground(COR_FUNDO);

        // Informações do Objeto
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Informações do Objeto"));
        pnlInfo.setBackground(COR_FUNDO);

        txtVertices = new JTextArea(8, 20);
        txtVertices.setEditable(false);
        txtVertices.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtVertices.setBorder(BorderFactory.createTitledBorder("Vértices:"));
        pnlInfo.add(txtVertices);
        pnlInfo.add(Box.createVerticalStrut(5));

        txtCentro = new JTextField();
        txtCentro.setEditable(false);
        txtCentro.setBorder(BorderFactory.createTitledBorder("Centro Geométrico:"));
        pnlInfo.add(txtCentro);
        pnlInfo.add(Box.createVerticalStrut(5));

        txtDimensoes = new JTextField();
        txtDimensoes.setEditable(false);
        txtDimensoes.setBorder(BorderFactory.createTitledBorder("Dimensões (LxA):"));
        pnlInfo.add(txtDimensoes);

        containerDir.add(pnlInfo);
        containerDir.add(Box.createVerticalStrut(10));

        // Histórico
        txtHistorico = new JTextArea(10, 20);
        txtHistorico.setEditable(false);
        JScrollPane scrollHist = new JScrollPane(txtHistorico);
        scrollHist.setBorder(BorderFactory.createTitledBorder("Histórico de Transformações"));
        containerDir.add(scrollHist);

        containerDir.add(Box.createVerticalStrut(10));
        JButton btnLimpar = criarBotaoBase("Limpar Tudo", e -> limparTudo());
        btnLimpar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        containerDir.add(btnLimpar);

        JScrollPane scroll = new JScrollPane(containerDir);
        scroll.setPreferredSize(new Dimension(LARGURA_LATERAL_DIR, 0));
        scroll.setBorder(null);
        add(scroll, BorderLayout.EAST);
    }

    // --- Helpers de Layout ---
    private void estilizarCabecalho(JLabel lbl) {
        lbl.setOpaque(true);
        lbl.setBackground(COR_BOTAO);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setBorder(new EmptyBorder(8, 0, 8, 0));
    }

    private JPanel criarPainelTranslacao() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtTransX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", txtTransY = new JTextField("0"));
        adicionarBotaoGrid(p, 2, "Aplicar Translação", e -> aplicarTranslacao());
        return p;
    }

    private JPanel criarPainelEscala() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtEscalaX = new JTextField("1"));
        adicionarComponenteGrid(p, 1, "Y:", txtEscalaY = new JTextField("1"));
        adicionarBotaoGrid(p, 2, "Aplicar Escala", e -> aplicarEscala());
        return p;
    }

    private JPanel criarPainelRotacao() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtRotX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", txtRotY = new JTextField("0"));
        adicionarComponenteGrid(p, 2, "Ângulo:", txtRotAngulo = new JTextField("0"));
        adicionarBotaoGrid(p, 3, "Aplicar Rotação", e -> aplicarRotacao());
        return p;
    }

    private JPanel criarPainelReflexao() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_FUNDO);
        JPanel pnlChecks = new JPanel(new FlowLayout());
        pnlChecks.setBackground(COR_FUNDO);
        pnlChecks.add(chkRefX = new JCheckBox("Em X"));
        pnlChecks.add(chkRefY = new JCheckBox("Em Y"));
        chkRefX.setBackground(COR_FUNDO);
        chkRefY.setBackground(COR_FUNDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(pnlChecks, gbc);
        adicionarBotaoGrid(p, 1, "Aplicar Reflexão", e -> aplicarReflexao());
        return p;
    }

    private JPanel criarPainelCisalhamento() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtCisX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", txtCisY = new JTextField("0"));
        adicionarBotaoGrid(p, 2, "Aplicar Cisalhamento", e -> aplicarCisalhamento());
        return p;
    }

    private JPanel criarPainelSequencia() {
        JPanel pnlSeq = new JPanel(new GridBagLayout());
        pnlSeq.setBorder(BorderFactory.createTitledBorder("Sequência"));
        pnlSeq.setBackground(COR_FUNDO);
        comboSequencia = new JComboBox<>(new String[]{"Translação", "Rotação", "Escala", "Cisalhamento", "Reflexão"});

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(2, 5, 5, 5);
        pnlSeq.add(comboSequencia, gbc);

        cardSeqParams = new CardLayout();
        panelSeqParams = new JPanel(cardSeqParams);
        panelSeqParams.setBackground(COR_FUNDO);
        panelSeqParams.add(criarPanelSeqTrans(), "Translação");
        panelSeqParams.add(criarPanelSeqRot(), "Rotação");
        panelSeqParams.add(criarPanelSeqEscala(), "Escala");
        panelSeqParams.add(criarPanelSeqCis(), "Cisalhamento");
        panelSeqParams.add(criarPanelSeqRef(), "Reflexão");

        gbc.gridy = 1;
        pnlSeq.add(panelSeqParams, gbc);

        adicionarBotaoGrid(pnlSeq, 2, "Adicionar à Sequência", e -> adicionarSequencia());
        adicionarBotaoGrid(pnlSeq, 3, "Aplicar Sequência", e -> aplicarSequencia());

        comboSequencia.addActionListener(e -> cardSeqParams.show(panelSeqParams, (String) comboSequencia.getSelectedItem()));
        return pnlSeq;
    }

    private JPanel criarPainelViewportConfig() {
        JPanel pnlVp = new JPanel(new GridBagLayout());
        pnlVp.setBorder(BorderFactory.createTitledBorder("Viewport 2D"));
        pnlVp.setBackground(COR_FUNDO);

        chkAtivarVp = new JCheckBox("Ativar Viewport");
        chkAtivarVp.setBackground(COR_FUNDO);

        txtVpMinX = new JTextField("-100");
        txtVpMaxX = new JTextField("100");
        txtVpMinY = new JTextField("-100");
        txtVpMaxY = new JTextField("100");

        setCamposVpEnabled(false);

        chkAtivarVp.addActionListener(e -> {
            boolean ativo = chkAtivarVp.isSelected();
            setCamposVpEnabled(ativo);
            canvasMundo.repaint();
            canvasViewport.repaint();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        pnlVp.add(chkAtivarVp, gbc);

        adicionarComponenteGrid(pnlVp, 1, "X Min:", txtVpMinX);
        adicionarComponenteGrid(pnlVp, 2, "X Max:", txtVpMaxX);
        adicionarComponenteGrid(pnlVp, 3, "Y Min:", txtVpMinY);
        adicionarComponenteGrid(pnlVp, 4, "Y Max:", txtVpMaxY);

        adicionarBotaoGrid(pnlVp, 5, "Aplicar Limites", e -> aplicarViewport());
        return pnlVp;
    }

    // --- Inputs Sequencia Internos ---
    private JPanel criarPanelSeqTrans() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", seqTransX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", seqTransY = new JTextField("0")); return p;
    }
    private JPanel criarPanelSeqRot() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "Ang:", seqRotAng = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "CX:", seqRotCX = new JTextField("0"));
        adicionarComponenteGrid(p, 2, "CY:", seqRotCY = new JTextField("0")); return p;
    }
    private JPanel criarPanelSeqEscala() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", seqEscalaX = new JTextField("1"));
        adicionarComponenteGrid(p, 1, "Y:", seqEscalaY = new JTextField("1")); return p;
    }
    private JPanel criarPanelSeqCis() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", seqCisX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", seqCisY = new JTextField("0")); return p;
    }
    private JPanel criarPanelSeqRef() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); p.setBackground(COR_FUNDO);
        p.add(seqRefX = new JCheckBox("X")); p.add(seqRefY = new JCheckBox("Y"));
        seqRefX.setBackground(COR_FUNDO); seqRefY.setBackground(COR_FUNDO); return p;
    }

    private void adicionarComponenteGrid(JPanel p, int row, String label, JTextField field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; p.add(field, gbc);
    }

    private void adicionarBotaoGrid(JPanel p, int row, String text, java.awt.event.ActionListener acao) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); p.add(criarBotaoBase(text, acao), gbc);
    }

    private JButton criarBotaoBase(String t, java.awt.event.ActionListener a) {
        JButton b = new JButton(t);
        b.setBackground(COR_BOTAO); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 12));
        b.addActionListener(a);
        return b;
    }

    // --- Lógica de Transformações ---
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
            atualizarInfoObjeto();
            addLog("Objeto gerado (Tamanho: " + size + ")");
            canvasMundo.repaint(); canvasViewport.repaint();
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
        canvasMundo.repaint(); canvasViewport.repaint();
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
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro nos parâmetros."); }
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
            txtVertices.setText(""); txtCentro.setText(""); txtDimensoes.setText(""); return;
        }
        StringBuilder sb = new StringBuilder();
        double cx = 0, cy = 0;
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (int i = 0; i < quadradoAtual.size(); i++) {
            Point2D.Double p = quadradoAtual.get(i);
            sb.append(String.format(Locale.US, "V%d: (%.1f, %.1f)\n", i + 1, p.x, p.y));
            cx += p.x; cy += p.y;
            if(p.x < minX) minX = p.x; if(p.x > maxX) maxX = p.x;
            if(p.y < minY) minY = p.y; if(p.y > maxY) maxY = p.y;
        }
        txtVertices.setText(sb.toString());
        txtCentro.setText(String.format(Locale.US, "(%.2f, %.2f)", cx / 4, cy / 4));
        txtDimensoes.setText(String.format(Locale.US, "%.1f x %.1f", (maxX - minX), (maxY - minY)));
    }

    private void addLog(String text) {
        historicoStr.append(historicoCount++).append(". ").append(text).append("\n");
        txtHistorico.setText(historicoStr.toString());
    }

    private void limparTudo() {
        quadradoAtual.clear(); quadradoOriginal.clear(); sequenciaAtual.clear();
        historicoStr.setLength(0); historicoCount = 1;
        txtHistorico.setText("");
        atualizarInfoObjeto();
        canvasMundo.repaint(); canvasViewport.repaint();
    }

    private class TransformacaoConfig {
        String tipo; double[][] matriz; String log;
        public TransformacaoConfig(String t, double[][] m, String l) { tipo = t; matriz = m; log = l; }
    }

    private void setCamposVpEnabled(boolean b) {
        txtVpMinX.setEnabled(b); txtVpMaxX.setEnabled(b); txtVpMinY.setEnabled(b); txtVpMaxY.setEnabled(b);
    }

    private void aplicarViewport() {
        try {
            Double.parseDouble(txtVpMinX.getText()); Double.parseDouble(txtVpMaxX.getText());
            Double.parseDouble(txtVpMinY.getText()); Double.parseDouble(txtVpMaxY.getText());
            addLog("Viewport atualizada");
            canvasMundo.repaint(); canvasViewport.repaint();
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Valores inválidos."); }
    }

    // --- Classes de Desenho ---

    // Canvas do Mundo - Exibe tudo a partir do centro
    private class MundoCanvas extends JPanel {
        public MundoCanvas() { setBackground(Color.WHITE); }

        private Point2D.Double worldToCanvas(double x, double y, int width, int height) {
            return new Point2D.Double(x + (width / 2.0), (height / 2.0) - y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(); int h = getHeight();

            // Eixos
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(w / 2, 0, w / 2, h);
            g2.drawLine(0, h / 2, w, h / 2);

            // Desenhar Objeto
            if (quadradoAtual != null && !quadradoAtual.isEmpty()) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                for (int i = 0; i < quadradoAtual.size(); i++) {
                    Point2D.Double p1 = quadradoAtual.get(i);
                    Point2D.Double p2 = quadradoAtual.get((i + 1) % quadradoAtual.size());
                    Point2D.Double s1 = worldToCanvas(p1.x, p1.y, w, h);
                    Point2D.Double s2 = worldToCanvas(p2.x, p2.y, w, h);
                    g2.drawLine((int)s1.x, (int)s1.y, (int)s2.x, (int)s2.y);
                }
            }

            // Desenhar Retângulo da Viewport (se ativo)
            if (chkAtivarVp != null && chkAtivarVp.isSelected()) {
                try {
                    double vMinX = Double.parseDouble(txtVpMinX.getText());
                    double vMaxX = Double.parseDouble(txtVpMaxX.getText());
                    double vMinY = Double.parseDouble(txtVpMinY.getText());
                    double vMaxY = Double.parseDouble(txtVpMaxY.getText());

                    Point2D.Double pMin = worldToCanvas(vMinX, vMinY, w, h);
                    Point2D.Double pMax = worldToCanvas(vMaxX, vMaxY, w, h);

                    int vx = (int) Math.min(pMin.x, pMax.x);
                    int vy = (int) Math.min(pMin.y, pMax.y);
                    int vLargura = (int) Math.abs(pMax.x - pMin.x);
                    int vAltura = (int) Math.abs(pMax.y - pMin.y);

                    float[] dash = {5.0f};
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                    g2.setColor(Color.RED);
                    g2.drawRect(vx, vy, vLargura, vAltura);
                } catch (Exception ignored) {}
            }
        }
    }

    // Canvas da Viewport - Implementa o Mapeamento (Window-to-Viewport)
    private class ViewportCanvas extends JPanel {
        public ViewportCanvas() { setBackground(Color.WHITE); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!chkAtivarVp.isSelected() || quadradoAtual == null || quadradoAtual.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            int vpWidth = getWidth(); int vpHeight = getHeight();

            try {
                // Coordenadas da Janela (Window)
                double wMinX = Double.parseDouble(txtVpMinX.getText());
                double wMaxX = Double.parseDouble(txtVpMaxX.getText());
                double wMinY = Double.parseDouble(txtVpMinY.getText());
                double wMaxY = Double.parseDouble(txtVpMaxY.getText());

                double rangeX = wMaxX - wMinX;
                double rangeY = wMaxY - wMinY;

                if (rangeX <= 0 || rangeY <= 0) return;

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));

                for (int i = 0; i < quadradoAtual.size(); i++) {
                    Point2D.Double p1 = quadradoAtual.get(i);
                    Point2D.Double p2 = quadradoAtual.get((i + 1) % quadradoAtual.size());

                    // Mapeamento Window -> Viewport
                    double x1 = (p1.x - wMinX) * (vpWidth / rangeX);
                    double y1 = vpHeight - ((p1.y - wMinY) * (vpHeight / rangeY)); // Inverte Y

                    double x2 = (p2.x - wMinX) * (vpWidth / rangeX);
                    double y2 = vpHeight - ((p2.y - wMinY) * (vpHeight / rangeY));

                    g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
                }
            } catch (Exception ignored) {}
        }
    }
}