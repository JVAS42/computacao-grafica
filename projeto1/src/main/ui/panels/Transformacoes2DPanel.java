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

    private final Color COR_FUNDO = new Color(240, 240, 240);
    private final Color COR_BOTAO = new Color(33, 53, 85);
    private final int LARGURA_LATERAL_ESQ = 320;
    private final int LARGURA_LATERAL_DIR = 280;

    private JTextField txtQuadTamanho, txtQuadPosX, txtQuadPosY;
    private JTextField txtTransX, txtTransY, txtEscalaX, txtEscalaY;
    private JTextField txtRotX, txtRotY, txtRotAngulo;
    private JCheckBox chkRefX, chkRefY;
    private JTextField txtCisX, txtCisY;

    private JTextArea txtVertices, txtHistorico;
    private JTextField txtCentro, txtDimensoes;

    private JComboBox<String> comboSequencia;
    private JPanel panelSeqParams;
    private CardLayout cardSeqParams;
    private JTextField seqTransX, seqTransY, seqRotAng, seqRotCX, seqRotCY;
    private JTextField seqEscalaX, seqEscalaY, seqCisX, seqCisY;
    private JCheckBox seqRefX, seqRefY;

    private MundoCanvas canvasMundo;
    private ViewportCanvas canvasViewport;
    private List<Point2D.Double> quadradoAtual = new ArrayList<>();
    private List<TransformacaoConfig> sequenciaAtual = new ArrayList<>();
    private StringBuilder historicoStr = new StringBuilder();
    private int historicoCount = 1;

    private JTextField txtWinMinX, txtWinMaxX, txtWinMinY, txtWinMaxY;
    private JTextField txtViewMinX, txtViewMaxX, txtViewMinY, txtViewMaxY;
    private JCheckBox chkAtivarMapeamento;

    public Transformacoes2DPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COR_FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane painelEsquerdo = setupPainelEsquerdo();
        JPanel painelCentral = setupPainelCentral();
        JScrollPane painelDireito = setupPainelDireito();
        JPanel painelHistorico = setupPainelHistorico();

        JPanel painelSuperior = new JPanel(new BorderLayout(10, 10));
        painelSuperior.setBackground(COR_FUNDO);
        painelSuperior.add(painelCentral, BorderLayout.CENTER);
        painelSuperior.add(painelDireito, BorderLayout.EAST);

        JPanel painelConteudo = new JPanel(new BorderLayout(10, 10));
        painelConteudo.setBackground(COR_FUNDO);
        painelConteudo.add(painelSuperior, BorderLayout.CENTER);
        painelConteudo.add(painelHistorico, BorderLayout.SOUTH);

        add(painelEsquerdo, BorderLayout.WEST);
        add(painelConteudo, BorderLayout.CENTER);
    }

    private JScrollPane setupPainelEsquerdo() {
        JPanel containerEsq = new JPanel();
        containerEsq.setLayout(new BoxLayout(containerEsq, BoxLayout.Y_AXIS));
        containerEsq.setBackground(COR_FUNDO);

        JPanel pnlQuad = new JPanel(new GridBagLayout());
        pnlQuad.setBorder(BorderFactory.createTitledBorder("Objeto 2D"));
        pnlQuad.setBackground(COR_FUNDO);
        adicionarComponenteGrid(pnlQuad, 0, "Tamanho:", txtQuadTamanho = new JTextField("50"));
        adicionarComponenteGrid(pnlQuad, 1, "Posição X:", txtQuadPosX = new JTextField("0"));
        adicionarComponenteGrid(pnlQuad, 2, "Posição Y:", txtQuadPosY = new JTextField("0"));
        adicionarBotaoGrid(pnlQuad, 3, "Gerar Objeto", e -> gerarQuadrado());
        containerEsq.add(pnlQuad);
        containerEsq.add(Box.createVerticalStrut(10));

        JTabbedPane tabTransformacoes = new JTabbedPane();
        tabTransformacoes.addTab("Trans", criarPainelTranslacao());
        tabTransformacoes.addTab("Rot", criarPainelRotacao());
        tabTransformacoes.addTab("Escala", criarPainelEscala());
        tabTransformacoes.addTab("Cisal", criarPainelCisalhamento());
        tabTransformacoes.addTab("Reflex", criarPainelReflexao());
        containerEsq.add(tabTransformacoes);
        containerEsq.add(Box.createVerticalStrut(10));

        containerEsq.add(criarPainelSequencia());
        containerEsq.add(Box.createVerticalStrut(10));
        containerEsq.add(criarPainelMapeamento());

        JScrollPane scroll = new JScrollPane(containerEsq);
        scroll.setPreferredSize(new Dimension(LARGURA_LATERAL_ESQ, 0));
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel setupPainelCentral() {
        JPanel containerCentro = new JPanel(new GridBagLayout());
        containerCentro.setBackground(COR_FUNDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.insets = new Insets(0, 20, 0, 20);

        JPanel wrapperMundo = new JPanel(new BorderLayout(0, 5));
        wrapperMundo.setBackground(COR_FUNDO);
        JLabel lblMundo = new JLabel("Mundo (500 x 500)", SwingConstants.CENTER);
        lblMundo.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel boxMundo = new JPanel(new BorderLayout());
        boxMundo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        canvasMundo = new MundoCanvas();
        canvasMundo.setPreferredSize(new Dimension(500, 500));
        boxMundo.add(canvasMundo, BorderLayout.CENTER);

        wrapperMundo.add(lblMundo, BorderLayout.NORTH);
        wrapperMundo.add(boxMundo, BorderLayout.CENTER);

        JPanel wrapperViewport = new JPanel(new BorderLayout(0, 5));
        wrapperViewport.setBackground(COR_FUNDO);
        JLabel lblVp = new JLabel("Viewport (300 x 300)", SwingConstants.CENTER);
        lblVp.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel boxViewport = new JPanel(new BorderLayout());
        boxViewport.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        canvasViewport = new ViewportCanvas();
        canvasViewport.setPreferredSize(new Dimension(300, 300));
        boxViewport.add(canvasViewport, BorderLayout.CENTER);

        wrapperViewport.add(lblVp, BorderLayout.NORTH);
        wrapperViewport.add(boxViewport, BorderLayout.CENTER);

        gbc.gridx = 0; containerCentro.add(wrapperMundo, gbc);
        gbc.gridx = 1; containerCentro.add(wrapperViewport, gbc);

        return containerCentro;
    }

    private JScrollPane setupPainelDireito() {
        JPanel containerDir = new JPanel();
        containerDir.setLayout(new BoxLayout(containerDir, BoxLayout.Y_AXIS));
        containerDir.setBackground(COR_FUNDO);

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

        JButton btnLimpar = criarBotaoBase("Limpar Tudo", e -> limparTudo());
        btnLimpar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        containerDir.add(btnLimpar);

        JScrollPane scroll = new JScrollPane(containerDir);
        scroll.setPreferredSize(new Dimension(LARGURA_LATERAL_DIR, 0));
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel setupPainelHistorico() {
        JPanel pnlHist = new JPanel(new BorderLayout());
        pnlHist.setBackground(COR_FUNDO);

        txtHistorico = new JTextArea(10, 20);
        txtHistorico.setEditable(false);
        txtHistorico.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollHist = new JScrollPane(txtHistorico);
        scrollHist.setBorder(BorderFactory.createTitledBorder("Histórico de Operações e Matrizes de Transformação"));

        pnlHist.add(scrollHist, BorderLayout.CENTER);
        pnlHist.setPreferredSize(new Dimension(0, 220));

        return pnlHist;
    }

    private JPanel criarPainelTranslacao() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtTransX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", txtTransY = new JTextField("0"));
        adicionarBotaoGrid(p, 2, "Aplicar Translação", e -> aplicarTranslacao());
        return p;
    }

    private JPanel criarPainelEscala() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtEscalaX = new JTextField("1"));
        adicionarComponenteGrid(p, 1, "Y:", txtEscalaY = new JTextField("1"));
        adicionarBotaoGrid(p, 2, "Aplicar Escala", e -> aplicarEscala());
        return p;
    }

    private JPanel criarPainelRotacao() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtRotX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", txtRotY = new JTextField("0"));
        adicionarComponenteGrid(p, 2, "Ângulo:", txtRotAngulo = new JTextField("0"));
        adicionarBotaoGrid(p, 3, "Aplicar Rotação", e -> aplicarRotacao());
        return p;
    }

    private JPanel criarPainelReflexao() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        JPanel pnlChecks = new JPanel(new FlowLayout()); pnlChecks.setBackground(COR_FUNDO);
        pnlChecks.add(chkRefX = new JCheckBox("Em X")); pnlChecks.add(chkRefY = new JCheckBox("Em Y"));
        chkRefX.setBackground(COR_FUNDO); chkRefY.setBackground(COR_FUNDO);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; p.add(pnlChecks, gbc);
        adicionarBotaoGrid(p, 1, "Aplicar Reflexão", e -> aplicarReflexao());
        return p;
    }

    private JPanel criarPainelCisalhamento() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", txtCisX = new JTextField("0"));
        adicionarComponenteGrid(p, 1, "Y:", txtCisY = new JTextField("0"));
        adicionarBotaoGrid(p, 2, "Aplicar Cisalhamento", e -> aplicarCisalhamento());
        return p;
    }

    private JPanel criarPainelSequencia() {
        JPanel pnlSeq = new JPanel(new GridBagLayout());
        pnlSeq.setBorder(BorderFactory.createTitledBorder("Sequência")); pnlSeq.setBackground(COR_FUNDO);
        comboSequencia = new JComboBox<>(new String[]{"Translação", "Rotação", "Escala", "Cisalhamento", "Reflexão"});

        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(2, 5, 5, 5); pnlSeq.add(comboSequencia, gbc);

        cardSeqParams = new CardLayout(); panelSeqParams = new JPanel(cardSeqParams); panelSeqParams.setBackground(COR_FUNDO);
        panelSeqParams.add(criarPanelSeqTrans(), "Translação"); panelSeqParams.add(criarPanelSeqRot(), "Rotação"); panelSeqParams.add(criarPanelSeqEscala(), "Escala"); panelSeqParams.add(criarPanelSeqCis(), "Cisalhamento"); panelSeqParams.add(criarPanelSeqRef(), "Reflexão");

        gbc.gridy = 1; pnlSeq.add(panelSeqParams, gbc);
        adicionarBotaoGrid(pnlSeq, 2, "Adicionar à Sequência", e -> adicionarSequencia());
        adicionarBotaoGrid(pnlSeq, 3, "Aplicar Sequência", e -> aplicarSequencia());
        comboSequencia.addActionListener(e -> cardSeqParams.show(panelSeqParams, (String) comboSequencia.getSelectedItem()));
        return pnlSeq;
    }

    private JPanel criarPanelSeqTrans() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", seqTransX = new JTextField("0")); adicionarComponenteGrid(p, 1, "Y:", seqTransY = new JTextField("0")); return p;
    }

    private JPanel criarPanelSeqRot() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "Ang:", seqRotAng = new JTextField("0")); adicionarComponenteGrid(p, 1, "CX:", seqRotCX = new JTextField("0")); adicionarComponenteGrid(p, 2, "CY:", seqRotCY = new JTextField("0")); return p;
    }

    private JPanel criarPanelSeqEscala() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", seqEscalaX = new JTextField("1")); adicionarComponenteGrid(p, 1, "Y:", seqEscalaY = new JTextField("1")); return p;
    }

    private JPanel criarPanelSeqCis() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(COR_FUNDO);
        adicionarComponenteGrid(p, 0, "X:", seqCisX = new JTextField("0")); adicionarComponenteGrid(p, 1, "Y:", seqCisY = new JTextField("0")); return p;
    }

    private JPanel criarPanelSeqRef() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); p.setBackground(COR_FUNDO);
        p.add(seqRefX = new JCheckBox("X")); p.add(seqRefY = new JCheckBox("Y")); seqRefX.setBackground(COR_FUNDO); seqRefY.setBackground(COR_FUNDO); return p;
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

    private JPanel criarPainelMapeamento() {
        JPanel pnlMapeamento = new JPanel(new GridBagLayout());
        pnlMapeamento.setBorder(BorderFactory.createTitledBorder("Mapeamento 2D"));
        pnlMapeamento.setBackground(COR_FUNDO);

        chkAtivarMapeamento = new JCheckBox("Ativar Window ➜ Viewport");
        chkAtivarMapeamento.setBackground(COR_FUNDO);

        txtWinMinX = new JTextField(""); txtWinMaxX = new JTextField("");
        txtWinMinY = new JTextField(""); txtWinMaxY = new JTextField("");
        txtViewMinX = new JTextField(""); txtViewMaxX = new JTextField("");
        txtViewMinY = new JTextField(""); txtViewMaxY = new JTextField("");

        setCamposMapeamentoEnabled(false);

        chkAtivarMapeamento.addActionListener(e -> {
            setCamposMapeamentoEnabled(chkAtivarMapeamento.isSelected());
            canvasMundo.repaint();
            canvasViewport.repaint();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(0, 5, 5, 5);
        pnlMapeamento.add(chkAtivarMapeamento, gbc);

        gbc.gridy++; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridwidth = 4;
        pnlMapeamento.add(new JLabel("Coordenadas da Window (Mundo)"), gbc);
        gbc.gridy++; gbc.gridwidth = 1; gbc.weightx = 0.2; pnlMapeamento.add(new JLabel("Xmin:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4; pnlMapeamento.add(txtWinMinX, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0; pnlMapeamento.add(new JLabel("Xmax"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4; pnlMapeamento.add(txtWinMaxX, gbc);
        gbc.gridy++; gbc.gridx = 0; pnlMapeamento.add(new JLabel("Ymin:"), gbc);
        gbc.gridx = 1; pnlMapeamento.add(txtWinMinY, gbc);
        gbc.gridx = 2; pnlMapeamento.add(new JLabel("Ymax"), gbc);
        gbc.gridx = 3; pnlMapeamento.add(txtWinMaxY, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 4; gbc.insets = new Insets(10, 5, 5, 5);
        pnlMapeamento.add(new JLabel("Coordenadas da Viewport"), gbc);
        gbc.gridy++; gbc.gridwidth = 1; gbc.insets = new Insets(0, 5, 5, 5); pnlMapeamento.add(new JLabel("Xmin:"), gbc);
        gbc.gridx = 1; pnlMapeamento.add(txtViewMinX, gbc);
        gbc.gridx = 2; pnlMapeamento.add(new JLabel("Xmax"), gbc);
        gbc.gridx = 3; pnlMapeamento.add(txtViewMaxX, gbc);
        gbc.gridy++; gbc.gridx = 0; pnlMapeamento.add(new JLabel("Ymin:"), gbc);
        gbc.gridx = 1; pnlMapeamento.add(txtViewMinY, gbc);
        gbc.gridx = 2; pnlMapeamento.add(new JLabel("Ymax"), gbc);
        gbc.gridx = 3; pnlMapeamento.add(txtViewMaxY, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 4;
        JButton btnAplicarMapeamento = criarBotaoBase("Aplicar Limites", e -> validarEAplicarMapeamento());
        pnlMapeamento.add(btnAplicarMapeamento, gbc);

        return pnlMapeamento;
    }

    private void setCamposMapeamentoEnabled(boolean b) {
        txtWinMinX.setEnabled(b); txtWinMaxX.setEnabled(b); txtWinMinY.setEnabled(b); txtWinMaxY.setEnabled(b);
        txtViewMinX.setEnabled(b); txtViewMaxX.setEnabled(b); txtViewMinY.setEnabled(b); txtViewMaxY.setEnabled(b);
    }

    private void validarEAplicarMapeamento() {
        try {
            Retangulo2D win = getRetanguloWindow();
            Retangulo2D vp = getRetanguloViewport();

            if (win.minX >= win.maxX || win.minY >= win.maxY) {
                JOptionPane.showMessageDialog(this, "Valores inválidos para a Window.\nMin deve ser menor que Max.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (vp.minX < 0 || vp.minY < 0 || vp.minX >= vp.maxX || vp.minY >= vp.maxY || vp.maxX > getWidth() || vp.maxY > getHeight()) {
                JOptionPane.showMessageDialog(this, "Valores inválidos para a Viewport.\nEles devem estar dentro do canvas (0 a " + getWidth() + ") e Min menor que Max.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            addLog("Mapeamento Window/Viewport atualizado.", null);
            canvasMundo.repaint(); canvasViewport.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos nos campos de mapeamento.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Processamento Lógico
    private void gerarQuadrado() {
        try {
            double size = Double.parseDouble(txtQuadTamanho.getText());
            double x = Double.parseDouble(txtQuadPosX.getText());
            double y = Double.parseDouble(txtQuadPosY.getText());
            quadradoAtual.clear();
            Point2D.Double p1 = new Point2D.Double(x, y);
            Point2D.Double p2 = new Point2D.Double(x + size, y);
            Point2D.Double p3 = new Point2D.Double(x + size, y + size);
            Point2D.Double p4 = new Point2D.Double(x, y + size);
            quadradoAtual.add(p1); quadradoAtual.add(p2); quadradoAtual.add(p3); quadradoAtual.add(p4);
            atualizarInfoObjeto();
            addLog("Objeto gerado (Tamanho: " + size + ")", null);
            canvasMundo.repaint(); canvasViewport.repaint();
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Valores numéricos inválidos para geração do objeto.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void aplicarTranslacao() {
        if (quadradoAtual.isEmpty()) return;
        try {
            double dx = Double.parseDouble(txtTransX.getText());
            double dy = Double.parseDouble(txtTransY.getText());
            double[][] matriz = Transformacoes2D.criarMatrizTranslacao(dx, dy);
            addLogComposto(
                    "Translação (dx=" + dx + ", dy=" + dy + ")",
                    new String[]{"Matriz de Translação"},
                    new double[][][]{matriz}
            );
            aplicarMatrizEmTodos(matriz, "Resultado da Translação");
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Erro", JOptionPane.ERROR_MESSAGE); }
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

            addLogComposto(
                    "Escala em relação ao ponto (" + origem.x + "," + origem.y + ")",
                    new String[]{"1) Translação para origem", "2) Matriz de Escala", "3) Translação de volta", "4) Matriz Final (T2 × S × T1)"},
                    new double[][][]{t1, s, t2, matrizFinal}
            );

            aplicarMatrizEmTodos(matrizFinal, "Resultado da Escala");
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void aplicarRotacao() {
        if (quadradoAtual.isEmpty()) return;
        try {
            double ang = Double.parseDouble(txtRotAngulo.getText());
            double cx = Double.parseDouble(txtRotX.getText());
            double cy = Double.parseDouble(txtRotY.getText());
            double[][] t1 = Transformacoes2D.criarMatrizTranslacao(-cx, -cy);
            double[][] r = Transformacoes2D.criarMatrizRotacao(ang, 0, 0);
            double[][] t2 = Transformacoes2D.criarMatrizTranslacao(cx, cy);

            double[][] matrizFinal = Transformacoes2D.multiplicarMatrizes(t2, Transformacoes2D.multiplicarMatrizes(r, t1));

            addLogComposto(
                    "Rotação de " + ang + "° em (" + cx + "," + cy + ")",
                    new String[]{"1) Translação para origem", "2) Matriz de Rotação", "3) Translação de volta", "4) Matriz Final (T2 × R × T1)"},
                    new double[][][]{t1, r, t2, matrizFinal}
            );

            aplicarMatrizEmTodos(matrizFinal, "Resultado da Rotação");
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void aplicarReflexao() {
        if (quadradoAtual.isEmpty()) return;

        boolean rx = chkRefX.isSelected();
        boolean ry = chkRefY.isSelected();

        if (!rx && !ry) return;

        Point2D.Double origem = quadradoAtual.get(0);
        double[][] t1 = Transformacoes2D.criarMatrizTranslacao(-origem.x, -origem.y);
        double[][] r  = Transformacoes2D.criarMatrizReflexao(rx, ry);
        double[][] t2 = Transformacoes2D.criarMatrizTranslacao(origem.x, origem.y);

        double[][] matrizFinal = Transformacoes2D.multiplicarMatrizes(t2, Transformacoes2D.multiplicarMatrizes(r, t1));

        addLogComposto(
                "Reflexão em relação ao ponto (" + origem.x + "," + origem.y + ")",
                new String[]{"1) Translação para origem", "2) Matriz de Reflexão", "3) Translação de volta", "4) Matriz Final (T2 × R × T1)"},
                new double[][][]{t1, r, t2, matrizFinal}
        );

        aplicarMatrizEmTodos(matrizFinal, "Resultado da Reflexão: " + (rx ? "X " : "") + (ry ? "Y" : ""));
    }

    private void aplicarCisalhamento() {
        if (quadradoAtual.isEmpty()) return;

        try {
            double shx = Double.parseDouble(txtCisX.getText());
            double shy = Double.parseDouble(txtCisY.getText());

            Point2D.Double origem = quadradoAtual.get(0);
            double[][] t1 = Transformacoes2D.criarMatrizTranslacao(-origem.x, -origem.y);
            double[][] sh = Transformacoes2D.criarMatrizCisalhamento(shx, shy);
            double[][] t2 = Transformacoes2D.criarMatrizTranslacao(origem.x, origem.y);

            double[][] matrizFinal = Transformacoes2D.multiplicarMatrizes(t2, Transformacoes2D.multiplicarMatrizes(sh, t1));

            addLogComposto(
                    "Cisalhamento em relação ao ponto (" + origem.x + "," + origem.y + ")",
                    new String[]{"1) Translação para origem", "2) Matriz de Cisalhamento", "3) Translação de volta", "4) Matriz Final (T2 × Sh × T1)"},
                    new double[][][]{t1, sh, t2, matrizFinal}
            );

            aplicarMatrizEmTodos(matrizFinal, "Resultado do Cisalhamento (Shx=" + shx + ", Shy=" + shy + ")");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarMatrizEmTodos(double[][] matriz, String logText) {
        for (int i = 0; i < quadradoAtual.size(); i++) {
            quadradoAtual.set(i, Transformacoes2D.aplicarTransformacao(quadradoAtual.get(i), matriz));
        }
        addLog(logText, matriz);
        atualizarInfoObjeto();
        canvasMundo.repaint(); canvasViewport.repaint();
    }

    private void adicionarSequencia() {
        String tipo = (String) comboSequencia.getSelectedItem();
        try {
            double[][] m = null; String log = "";
            switch (tipo) {
                case "Translação":
                    m = Transformacoes2D.criarMatrizTranslacao(Double.parseDouble(seqTransX.getText()), Double.parseDouble(seqTransY.getText()));
                    log = "Seq: Translação";
                    break;
                case "Rotação":
                    m = Transformacoes2D.criarMatrizRotacao(Double.parseDouble(seqRotAng.getText()), 0, 0);
                    log = "Seq: Rotação";
                    break;
                case "Escala":
                    m = Transformacoes2D.criarMatrizEscala(Double.parseDouble(seqEscalaX.getText()), Double.parseDouble(seqEscalaY.getText()));
                    log = "Seq: Escala";
                    break;
                case "Cisalhamento":
                    m = Transformacoes2D.criarMatrizCisalhamento(Double.parseDouble(seqCisX.getText()), Double.parseDouble(seqCisY.getText()));
                    log = "Seq: Cisalhamento";
                    break;
                case "Reflexão":
                    m = Transformacoes2D.criarMatrizReflexao(seqRefX.isSelected(), seqRefY.isSelected());
                    log = "Seq: Reflexão";
                    break;
            }
            if (m != null) {
                sequenciaAtual.add(new TransformacaoConfig(tipo, m, log));
                historicoStr.append("============================================================\n");
                historicoStr.append(String.format("Fila Atualizada: Adicionado %s\n", tipo));
                historicoStr.append(formatarMatriz(m));
                historicoStr.append("============================================================\n\n");
                txtHistorico.setText(historicoStr.toString());
                txtHistorico.setCaretPosition(txtHistorico.getDocument().getLength());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro numérico nos parâmetros da sequência.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarSequencia() {
        if (quadradoAtual.isEmpty() || sequenciaAtual.isEmpty()) return;

        Point2D.Double o = quadradoAtual.get(0);
        double[][] tIda = Transformacoes2D.criarMatrizTranslacao(-o.x, -o.y);
        double[][] tVolta = Transformacoes2D.criarMatrizTranslacao(o.x, o.y);

        StringBuilder logSeq = new StringBuilder();
        logSeq.append("============================================================\n");
        logSeq.append(String.format("Operação %d: === SEQUÊNCIA DE %d TRANSFORMAÇÕES ===\n\n", historicoCount++, sequenciaAtual.size()));

        int passoAtual = 1;
        logSeq.append("Passo ").append(passoAtual++).append(": Translação para a Origem (T -p):\n").append(formatarMatriz(tIda)).append("\n");

        double[][] matrizFinal = tIda;

        for (TransformacaoConfig config : sequenciaAtual) {
            logSeq.append("Passo ").append(passoAtual++).append(": Matriz Base - ").append(config.tipo).append(":\n").append(formatarMatriz(config.matriz)).append("\n");
            matrizFinal = Transformacoes2D.multiplicarMatrizes(config.matriz, matrizFinal);
        }

        logSeq.append("Passo ").append(passoAtual).append(": Translação de Volta (T p):\n").append(formatarMatriz(tVolta)).append("\n");
        matrizFinal = Transformacoes2D.multiplicarMatrizes(tVolta, matrizFinal);

        logSeq.append("RESULTADO GERAL (Matriz Composta Final):\n").append(formatarMatriz(matrizFinal)).append("\n");

        for (int i = 0; i < quadradoAtual.size(); i++) {
            quadradoAtual.set(i, Transformacoes2D.aplicarTransformacao(quadradoAtual.get(i), matrizFinal));
        }

        logSeq.append("Coordenadas Resultantes do Objeto:\n");
        for (int i = 0; i < quadradoAtual.size(); i++) {
            Point2D.Double p = quadradoAtual.get(i);
            logSeq.append(String.format(Locale.US, "   V%d: (%.2f, %.2f)\n", i + 1, p.x, p.y));
        }
        logSeq.append("============================================================\n\n");

        historicoStr.append(logSeq.toString());
        txtHistorico.setText(historicoStr.toString());
        txtHistorico.setCaretPosition(txtHistorico.getDocument().getLength());

        sequenciaAtual.clear();
        atualizarInfoObjeto();
        canvasMundo.repaint();
        canvasViewport.repaint();
    }

    private void atualizarInfoObjeto() {
        if (quadradoAtual.isEmpty()) { txtVertices.setText(""); txtCentro.setText(""); txtDimensoes.setText(""); return; }
        StringBuilder sb = new StringBuilder(); double cx = 0, cy = 0;
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

    private void addLog(String text, double[][] matriz) {
        historicoStr.append("============================================================\n");
        historicoStr.append(String.format("Operação %d: %s\n", historicoCount++, text));

        if (matriz != null) {
            historicoStr.append("\nMatriz de Transformação:\n");
            for (double[] linha : matriz) {
                historicoStr.append("   | ");
                for (double valor : linha) {
                    historicoStr.append(String.format(Locale.US, "%8.2f ", valor));
                }
                historicoStr.append("|\n");
            }
        }

        if (quadradoAtual != null && !quadradoAtual.isEmpty()) {
            historicoStr.append("\nCoordenadas Resultantes do Objeto:\n");
            for (int i = 0; i < quadradoAtual.size(); i++) {
                Point2D.Double p = quadradoAtual.get(i);
                historicoStr.append(String.format(Locale.US, "   V%d: (%.2f, %.2f)\n", i + 1, p.x, p.y));
            }
        }

        historicoStr.append("============================================================\n\n");
        txtHistorico.setText(historicoStr.toString());
        txtHistorico.setCaretPosition(txtHistorico.getDocument().getLength());
    }

    private void addLogComposto(String titulo, String[] descricoes, double[][][] matrizes) {
        historicoStr.append("============================================================\n");
        historicoStr.append(String.format("Operação %d: %s\n\n", historicoCount++, titulo));

        for (int i = 0; i < matrizes.length; i++) {
            historicoStr.append(descricoes[i]).append("\n");

            for (double[] linha : matrizes[i]) {
                historicoStr.append("   | ");
                for (double valor : linha) {
                    historicoStr.append(String.format(Locale.US, "%8.2f ", valor));
                }
                historicoStr.append("|\n");
            }
            historicoStr.append("\n");
        }

        historicoStr.append("============================================================\n\n");

        txtHistorico.setText(historicoStr.toString());
        txtHistorico.setCaretPosition(txtHistorico.getDocument().getLength());
    }

    private void limparTudo() {
        quadradoAtual.clear();
        sequenciaAtual.clear();
        historicoStr.setLength(0);
        historicoCount = 1;

        txtHistorico.setText("");
        txtQuadTamanho.setText("50");
        txtQuadPosX.setText("0");
        txtQuadPosY.setText("0");

        txtTransX.setText("0"); txtTransY.setText("0");
        txtEscalaX.setText("1"); txtEscalaY.setText("1");
        txtRotX.setText("0"); txtRotY.setText("0"); txtRotAngulo.setText("0");
        chkRefX.setSelected(false); chkRefY.setSelected(false);
        txtCisX.setText("0"); txtCisY.setText("0");

        seqTransX.setText("0"); seqTransY.setText("0");
        seqRotAng.setText("0"); seqRotCX.setText("0"); seqRotCY.setText("0");
        seqEscalaX.setText("1"); seqEscalaY.setText("1");
        seqCisX.setText("0"); seqCisY.setText("0");
        seqRefX.setSelected(false); seqRefY.setSelected(false);

        txtWinMinX.setText(""); txtWinMaxX.setText(""); txtWinMinY.setText(""); txtWinMaxY.setText("");
        txtViewMinX.setText(""); txtViewMaxX.setText(""); txtViewMinY.setText(""); txtViewMaxY.setText("");
        chkAtivarMapeamento.setSelected(false);
        setCamposMapeamentoEnabled(false);

        atualizarInfoObjeto();
        canvasMundo.repaint();
        canvasViewport.repaint();
    }

    private String formatarMatriz(double[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matriz.length; i++) {
            sb.append("  | ");
            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(String.format(Locale.US, "%8.2f ", matriz[i][j]));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    private class TransformacaoConfig {
        String tipo; double[][] matriz; String log;
        public TransformacaoConfig(String t, double[][] m, String l) { tipo = t; matriz = m; log = l; }
    }

    private static class Retangulo2D {
        double minX, maxX, minY, maxY;
        public Retangulo2D(double minX, double maxX, double minY, double maxY) {
            this.minX = minX; this.maxX = maxX; this.minY = minY; this.maxY = maxY;
        }
    }

    private Retangulo2D getRetanguloWindow() throws NumberFormatException {
        return new Retangulo2D(
                Double.parseDouble(txtWinMinX.getText()), Double.parseDouble(txtWinMaxX.getText()),
                Double.parseDouble(txtWinMinY.getText()), Double.parseDouble(txtWinMaxY.getText())
        );
    }

    private Retangulo2D getRetanguloViewport() throws NumberFormatException {
        return new Retangulo2D(
                Double.parseDouble(txtViewMinX.getText()), Double.parseDouble(txtViewMaxX.getText()),
                Double.parseDouble(txtViewMinY.getText()), Double.parseDouble(txtViewMaxY.getText())
        );
    }

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

            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(w / 2, 0, w / 2, h);
            g2.drawLine(0, h / 2, w, h / 2);

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
        }
    }

    private class ViewportCanvas extends JPanel {
        public ViewportCanvas() { setBackground(Color.WHITE); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (!chkAtivarMapeamento.isSelected()) return;

            Graphics2D g2 = (Graphics2D) g;
            int canvasHeight = getHeight();

            try {
                Retangulo2D win = getRetanguloWindow();
                Retangulo2D vp = getRetanguloViewport();

                if (win.minX >= win.maxX || win.minY >= win.maxY || vp.minX >= vp.maxX || vp.minY >= vp.maxY) return;
                if (quadradoAtual == null || quadradoAtual.isEmpty()) return;

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));

                double[][] matrizWV = Transformacoes2D.criarMatrizMundoParaViewport(
                        win.minX, win.maxX, win.minY, win.maxY,
                        vp.minX, vp.maxX, vp.minY, vp.maxY
                );

                for (int i = 0; i < quadradoAtual.size(); i++) {
                    Point2D.Double p1 = quadradoAtual.get(i);
                    Point2D.Double p2 = quadradoAtual.get((i + 1) % quadradoAtual.size());

                    Point2D.Double[] pontosClipados = Transformacoes2D.cohenSutherlandClip(p1, p2, win.minX, win.maxX, win.minY, win.maxY);

                    if (pontosClipados != null) {
                        Point2D.Double c1 = pontosClipados[0];
                        Point2D.Double c2 = pontosClipados[1];

                        Point2D.Double vp1 = Transformacoes2D.aplicarMatriz(matrizWV, c1.x, c1.y);
                        Point2D.Double vp2 = Transformacoes2D.aplicarMatriz(matrizWV, c2.x, c2.y);

                        double telaY1 = canvasHeight - vp1.y;
                        double telaY2 = canvasHeight - vp2.y;

                        g2.drawLine((int)vp1.x, (int)telaY1, (int)vp2.x, (int)telaY2);
                    }
                }
            } catch (Exception ignored) {}
        }
    }
}