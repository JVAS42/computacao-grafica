package main.ui.panels;

import main.algorithms.Transformacoes3D;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Transformacoes3DPanel extends JPanel {

    private double[][] vertices;
    private final int[][] arestas = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };

    // Parâmetros de Visualização
    private double viewRotX = 0, viewRotY = 0, viewRotZ = 0, zoom = 100;
    private JSlider slRotX, slRotY, slRotZ, slZoom;

    // Parâmetros da Viewport
    private JCheckBox chkViewPort;
    private JTextField txtVpXMin, txtVpYMin, txtVpXMax, txtVpYMax;

    // Painéis de Desenho e Textos
    private JPanel canvasMundo, canvasViewport;
    private JTextPane txtInfoObject;
    private JTextArea txtHistory;

    // Sequência e Histórico
    private List<double[][]> sequence = new ArrayList<>();
    private DefaultListModel<String> listModelSeq = new DefaultListModel<>(); // <--- NOVO
    private JList<String> listSequencia; // <--- NOVO
    private List<String> historyLog = new ArrayList<>();

    // Inputs de Transformação
    private JTextField txtTamanho; // Promovido a global para poder ser resetado
    private JTextField txtTx, txtTy, txtTz, txtRotAngle, txtSx, txtSy, txtSz;
    private JTextField txtShXY, txtShXZ, txtShYZ;
    private JComboBox<String> cbRotAxis, cbRefAxis, cbSeqType;

    // --- Cores e Estilos Modernos ---
    private final Color BG_PANEL = new Color(240, 240, 240); // #F0F0F0
    private final Color BG_BUTTON = new Color(33, 53, 85);   // #213555
    private final Color FG_BUTTON = Color.WHITE;
    private final Font FONT_DEFAULT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    public Transformacoes3DPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Painel Esquerdo (Controles)
        JScrollPane leftScroll = new JScrollPane(criarPainelEsquerdo());
        leftScroll.setPreferredSize(new Dimension(500, 0));
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        add(leftScroll, BorderLayout.WEST);

        // 2. Painel Central (Telas Quadradas Modernas)
        add(criarPainelCentral(), BorderLayout.CENTER);

        // 3. Painel Direito (Informações)
        JScrollPane rightScroll = new JScrollPane(criarPainelDireito());
        rightScroll.setPreferredSize(new Dimension(280, 0));
        rightScroll.setBorder(BorderFactory.createEmptyBorder());
        add(rightScroll, BorderLayout.EAST);

        gerarCubo(40);
    }

    private JPanel criarPainelEsquerdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // --- Gerar Objeto ---
        JPanel pnlObj = criarSecao("Objeto 3D", new GridLayout(3, 1, 5, 8));
        txtTamanho = estilizarInput(new JTextField("40")); // Agora é global
        JButton btnGerar = criarBotaoPrimario("Gerar Objeto");
        btnGerar.addActionListener(e -> gerarCubo(Double.parseDouble(txtTamanho.getText())));

        pnlObj.add(criarLabel("Tamanho:"));
        pnlObj.add(txtTamanho);
        pnlObj.add(btnGerar);
        panel.add(pnlObj);
        panel.add(Box.createVerticalStrut(15));

        // --- Transformações (Abas) ---
        JTabbedPane tabTransforms = new JTabbedPane();
        tabTransforms.setFont(FONT_DEFAULT);

        // Translação
        JPanel pnlTrans = criarAbaTransformacao(new GridLayout(4, 2, 8, 8));
        txtTx = estilizarInput(new JTextField("0"));
        txtTy = estilizarInput(new JTextField("0"));
        txtTz = estilizarInput(new JTextField("0"));
        pnlTrans.add(criarLabel("X:"));
        pnlTrans.add(txtTx);
        pnlTrans.add(criarLabel("Y:"));
        pnlTrans.add(txtTy);
        pnlTrans.add(criarLabel("Z:"));
        pnlTrans.add(txtTz);
        JButton btnApplyTrans = criarBotaoPrimario("Aplicar Translação");

        btnApplyTrans.addActionListener(e -> {
            double tx = Double.parseDouble(txtTx.getText());
            double ty = Double.parseDouble(txtTy.getText());
            double tz = Double.parseDouble(txtTz.getText());

            double[][] matriz = Transformacoes3D.translation(tx, ty, tz);
            String descricao = "Translação (" + tx + ", " + ty + ", " + tz + ")\n"
                    + "Matriz Aplicada:\n" + formatarMatriz(matriz)
                    + "------------------------------------";

            aplicarTransformacaoDireta(matriz, descricao);
        });

        pnlTrans.add(new JLabel(""));
        pnlTrans.add(btnApplyTrans);
        tabTransforms.addTab("Trans", pnlTrans);

        // Rotação
        JPanel pnlRot = criarAbaTransformacao(new GridLayout(3, 2, 8, 8));
        cbRotAxis = new JComboBox<>(new String[]{"X", "Y", "Z"});
        cbRotAxis.setFont(FONT_DEFAULT);
        txtRotAngle = estilizarInput(new JTextField("0"));
        pnlRot.add(criarLabel("Eixo:"));
        pnlRot.add(cbRotAxis);
        pnlRot.add(criarLabel("Ângulo:"));
        pnlRot.add(txtRotAngle);
        JButton btnApplyRot = criarBotaoPrimario("Aplicar Rotação");

        btnApplyRot.addActionListener(e -> {
            double ang = Double.parseDouble(txtRotAngle.getText());
            String eixo = (String) cbRotAxis.getSelectedItem();

            double[][] mat = eixo.equals("X") ? Transformacoes3D.rotationX(ang) :
                    (eixo.equals("Y") ? Transformacoes3D.rotationY(ang) : Transformacoes3D.rotationZ(ang));

            String descricao = "Rotação Eixo " + eixo + " (" + ang + "°)\n"
                    + "Matriz Aplicada:\n" + formatarMatriz(mat)
                    + "------------------------------------";

            aplicarEmTornoDoVertice1(mat, descricao);
        });

        pnlRot.add(new JLabel(""));
        pnlRot.add(btnApplyRot);
        tabTransforms.addTab("Rot", pnlRot);

        // Escala
        JPanel pnlScale = criarAbaTransformacao(new GridLayout(4, 2, 8, 8));
        txtSx = estilizarInput(new JTextField("1.5"));
        txtSy = estilizarInput(new JTextField("1.5"));
        txtSz = estilizarInput(new JTextField("1.5"));
        pnlScale.add(criarLabel("X:"));
        pnlScale.add(txtSx);
        pnlScale.add(criarLabel("Y:"));
        pnlScale.add(txtSy);
        pnlScale.add(criarLabel("Z:"));
        pnlScale.add(txtSz);
        JButton btnApplyScale = criarBotaoPrimario("Aplicar Escala");

        btnApplyScale.addActionListener(e -> {
            double sx = Double.parseDouble(txtSx.getText());
            double sy = Double.parseDouble(txtSy.getText());
            double sz = Double.parseDouble(txtSz.getText());

            double[][] matriz = Transformacoes3D.scaling(sx, sy, sz);
            String descricao = "Escala (" + sx + ", " + sy + ", " + sz + ")\n"
                    + "Matriz Aplicada:\n" + formatarMatriz(matriz)
                    + "------------------------------------";

            aplicarEmTornoDoVertice1(matriz, descricao);
        });

        pnlScale.add(new JLabel(""));
        pnlScale.add(btnApplyScale);
        tabTransforms.addTab("Escala", pnlScale);

        // Cisalhamento
        JPanel pnlShear = criarAbaTransformacao(new GridLayout(4, 2, 8, 8));
        txtShXY = estilizarInput(new JTextField("0"));
        txtShXZ = estilizarInput(new JTextField("0"));
        txtShYZ = estilizarInput(new JTextField("0"));
        pnlShear.add(criarLabel("XY:"));
        pnlShear.add(txtShXY);
        pnlShear.add(criarLabel("XZ:"));
        pnlShear.add(txtShXZ);
        pnlShear.add(criarLabel("YZ:"));
        pnlShear.add(txtShYZ);
        JButton btnSh = criarBotaoPrimario("Aplicar Cisalh.");

        btnSh.addActionListener(e -> {
            double shXY = Double.parseDouble(txtShXY.getText());
            double shXZ = Double.parseDouble(txtShXZ.getText());
            double shYZ = Double.parseDouble(txtShYZ.getText());

            double[][] matriz = Transformacoes3D.shear(shXY, shXZ, shYZ);
            String descricao = "Cisalhamento\n"
                    + "Matriz Aplicada:\n" + formatarMatriz(matriz)
                    + "------------------------------------";

            aplicarTransformacaoDireta(matriz, descricao);
        });

        pnlShear.add(new JLabel(""));
        pnlShear.add(btnSh);
        tabTransforms.addTab("Cisal", pnlShear);

        // Reflexão
        JPanel pnlRef = criarAbaTransformacao(new GridLayout(3, 2, 8, 8));
        cbRefAxis = new JComboBox<>(new String[]{"XY", "XZ", "YZ"});
        cbRefAxis.setFont(FONT_DEFAULT);
        pnlRef.add(criarLabel("Plano:"));
        pnlRef.add(cbRefAxis);
        JButton btnRef = criarBotaoPrimario("Aplicar Reflex.");

        btnRef.addActionListener(e -> {
            String plano = (String) cbRefAxis.getSelectedItem();

            double[][] matriz = Transformacoes3D.reflection(plano);
            String descricao = "Reflexão plano " + plano + "\n"
                    + "Matriz Aplicada:\n" + formatarMatriz(matriz)
                    + "------------------------------------";

            aplicarTransformacaoDireta(matriz, descricao);
        });

        pnlRef.add(new JLabel(""));
        pnlRef.add(btnRef);
        tabTransforms.addTab("Reflex", pnlRef);

        panel.add(tabTransforms);
        panel.add(Box.createVerticalStrut(15));

        // --- Visualização ---
        JPanel pnlVis = criarSecao("Visualização", new GridLayout(5, 2, 8, 8));
        slRotX = new JSlider(-180, 180, 0);
        slRotY = new JSlider(-180, 180, 0);
        slRotZ = new JSlider(-180, 180, 0);
        slZoom = new JSlider(50, 200, 100);
        slRotX.setBackground(BG_PANEL);
        slRotY.setBackground(BG_PANEL);
        slRotZ.setBackground(BG_PANEL);
        slZoom.setBackground(BG_PANEL);

        slRotX.addChangeListener(e -> {
            viewRotX = slRotX.getValue();
            atualizarTelas();
        });
        slRotY.addChangeListener(e -> {
            viewRotY = slRotY.getValue();
            atualizarTelas();
        });
        slRotZ.addChangeListener(e -> {
            viewRotZ = slRotZ.getValue();
            atualizarTelas();
        });
        slZoom.addChangeListener(e -> {
            zoom = slZoom.getValue();
            atualizarTelas();
        });

        pnlVis.add(criarLabelCor("X (Red):", Color.RED));
        pnlVis.add(slRotX);
        pnlVis.add(criarLabelCor("Y (Green):", new Color(0, 150, 0)));
        pnlVis.add(slRotY);
        pnlVis.add(criarLabelCor("Z (Blue):", Color.BLUE));
        pnlVis.add(slRotZ);
        pnlVis.add(criarLabel("Zoom (%):"));
        pnlVis.add(slZoom);

        JButton btnResetVis = criarBotaoPrimario("Resetar Vis.");
        btnResetVis.addActionListener(e -> {
            slRotX.setValue(0);
            slRotY.setValue(0);
            slRotZ.setValue(0);
            slZoom.setValue(100);
        });
        pnlVis.add(new JLabel(""));
        pnlVis.add(btnResetVis);
        panel.add(pnlVis);
        panel.add(Box.createVerticalStrut(15));

        // --- Sequência ---
        // Usando BorderLayout para deixar a lista no centro e os botões organizados
        JPanel pnlSeq = criarSecao("Sequência (Fila)", new BorderLayout(5, 5));

        // Parte Superior: Seleção e Botão Adicionar
        JPanel pnlSeqTop = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlSeqTop.setBackground(BG_PANEL);
        cbSeqType = new JComboBox<>(new String[]{"Translação", "Rotação", "Escala", "Cisalhamento", "Reflexão"});
        cbSeqType.setFont(FONT_DEFAULT);
        JButton btnAddSeq = criarBotaoPrimario("Adicionar à Fila");
        btnAddSeq.addActionListener(e -> adicionarASequencia());
        pnlSeqTop.add(cbSeqType);
        pnlSeqTop.add(btnAddSeq);

        // Parte Central: A Fila Visual
        listSequencia = new JList<>(listModelSeq);
        listSequencia.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollSeq = new JScrollPane(listSequencia);
        scrollSeq.setPreferredSize(new Dimension(0, 80)); // Altura fixa para caber na tela

        // Parte Inferior: Botões de Aplicar e Limpar
        JPanel pnlSeqBot = new JPanel(new GridLayout(1, 2, 5, 5));
        pnlSeqBot.setBackground(BG_PANEL);
        JButton btnApplySeq = criarBotaoPrimario("Aplicar Tudo");
        btnApplySeq.addActionListener(e -> aplicarSequencia());
        JButton btnClearSeq = criarBotaoPrimario("Limpar Fila");
        btnClearSeq.setBackground(new Color(180, 50, 50)); // Vermelhinho
        btnClearSeq.addActionListener(e -> limparSequencia());
        pnlSeqBot.add(btnApplySeq);
        pnlSeqBot.add(btnClearSeq);

        pnlSeq.add(pnlSeqTop, BorderLayout.NORTH);
        pnlSeq.add(scrollSeq, BorderLayout.CENTER);
        pnlSeq.add(pnlSeqBot, BorderLayout.SOUTH);

        panel.add(pnlSeq);
        panel.add(Box.createVerticalStrut(15));

        // --- Viewport ---
        JPanel pnlVp = criarSecao("Viewport 3D", new GridLayout(5, 2, 8, 8));
        chkViewPort = new JCheckBox("Ativar Viewport", true);
        chkViewPort.setBackground(BG_PANEL);
        chkViewPort.setFont(FONT_BOLD);
        chkViewPort.addActionListener(e -> atualizarTelas());
        txtVpXMin = estilizarInput(new JTextField("0"));
        txtVpYMin = estilizarInput(new JTextField("0"));
        txtVpXMax = estilizarInput(new JTextField("0"));
        txtVpYMax = estilizarInput(new JTextField("0"));
        pnlVp.add(chkViewPort);
        pnlVp.add(new JLabel(""));
        pnlVp.add(criarLabel("Xmin:"));
        pnlVp.add(txtVpXMin);
        pnlVp.add(criarLabel("Ymin:"));
        pnlVp.add(txtVpYMin);
        pnlVp.add(criarLabel("Xmax:"));
        pnlVp.add(txtVpXMax);
        pnlVp.add(criarLabel("Ymax:"));
        pnlVp.add(txtVpYMax);
        panel.add(pnlVp);

        JButton btnLimpar = criarBotaoPrimario("Limpar Tudo");
        btnLimpar.setBackground(new Color(180, 50, 50));
        btnLimpar.addActionListener(e -> limparTudo()); // <--- CHAMADA DO NOVO MÉTODO
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnLimpar);

        return panel;
    }

    private Component criarPainelCentral() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(BG_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridy = 0;

        canvasMundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharMundo((Graphics2D) g);
            }
        };
        canvasMundo.setBackground(Color.WHITE);

        canvasViewport = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharViewport((Graphics2D) g);
            }
        };
        canvasViewport.setBackground(Color.WHITE);

        container.add(criarJanelaQuadrada("Mundo (500x500)", canvasMundo, 500), gbc);
        container.add(criarJanelaQuadrada("Viewport (300x300)", canvasViewport, 300), gbc);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG_PANEL);

        return scroll;
    }

    private JPanel criarJanelaQuadrada(String titulo, JPanel canvas, int size) {
        // Wrapper principal que agrupa o texto (em cima) e o quadrado (embaixo)
        JPanel wrapperPrincipal = new JPanel(new BorderLayout(0, 5)); // O '5' é o espaçamento entre o texto e a caixa
        wrapperPrincipal.setBackground(BG_PANEL); // Fundo igual ao do resto da tela

        // Texto simples, sem fundo colorido
        JLabel lblHeader = new JLabel(titulo, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.BLACK);

        // O box que vai realmente guardar o canvas e ter a borda cinza
        JPanel boxCanvas = new JPanel(new BorderLayout());
        boxCanvas.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Borda apenas no quadrado
        boxCanvas.setBackground(Color.WHITE);

        canvas.setPreferredSize(new Dimension(size, size));
        boxCanvas.add(canvas, BorderLayout.CENTER);

        // Adiciona o texto em cima e a caixa embaixo
        wrapperPrincipal.add(lblHeader, BorderLayout.NORTH);
        wrapperPrincipal.add(boxCanvas, BorderLayout.CENTER);

        return wrapperPrincipal;
    }

    private JPanel criarPainelDireito() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        txtInfoObject = new JTextPane();
        txtInfoObject.setContentType("text/html");
        txtInfoObject.setEditable(false);
        txtInfoObject.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlInfo = criarSecao("Informações do Objeto", new BorderLayout());
        pnlInfo.add(new JScrollPane(txtInfoObject), BorderLayout.CENTER);
        panel.add(pnlInfo);
        panel.add(Box.createVerticalStrut(15));

        txtHistory = new JTextArea(10, 20);
        txtHistory.setEditable(false);
        txtHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtHistory.setMargin(new Insets(10, 10, 10, 10));

        JPanel pnlHist = criarSecao("Histórico de Transformações", new BorderLayout());
        pnlHist.add(new JScrollPane(txtHistory), BorderLayout.CENTER);
        panel.add(pnlHist);

        return panel;
    }

    // --- Métodos de UI / Componentes Padronizados ---

    private JPanel criarSecao(String titulo, LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_PANEL);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), titulo
        );
        border.setTitleFont(FONT_BOLD);
        border.setTitleColor(BG_BUTTON);
        p.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return p;
    }

    private JPanel criarAbaTransformacao(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        return p;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(BG_BUTTON);
        btn.setForeground(FG_BUTTON);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return btn;
    }

    private JTextField estilizarInput(JTextField txt) {
        txt.setFont(FONT_DEFAULT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return txt;
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_BOLD);
        return lbl;
    }

    private JLabel criarLabelCor(String texto, Color cor) {
        JLabel lbl = criarLabel(texto);
        lbl.setForeground(cor);
        return lbl;
    }

    // --- Lógica Principal ---

    private void gerarCubo(double tamanho) {
        // Ponto de origem alterado de (0,0,0) para (15, 15, 15)
        double startX = 15.0;
        double startY = 15.0;
        double startZ = 15.0;

        // O '1' no final de cada linha é a coordenada homogênea (w) necessária para multiplicar pelas matrizes 4x4
        vertices = new double[][]{
                {startX, startY, startZ, 1},                                     // Vértice 0
                {startX + tamanho, startY, startZ, 1},                           // Vértice 1
                {startX + tamanho, startY + tamanho, startZ, 1},                 // Vértice 2
                {startX, startY + tamanho, startZ, 1},                           // Vértice 3
                {startX, startY, startZ + tamanho, 1},                           // Vértice 4
                {startX + tamanho, startY, startZ + tamanho, 1},                 // Vértice 5
                {startX + tamanho, startY + tamanho, startZ + tamanho, 1},       // Vértice 6
                {startX, startY + tamanho, startZ + tamanho, 1}                  // Vértice 7
        };

        // Limpa as listas de histórico e sequências (caso existam no seu método original)
        if (sequence != null) sequence.clear();
        if (historyLog != null) historyLog.clear();
        if (txtHistory != null) txtHistory.setText("");

        // Atualiza a tela com o novo cubo
        repaint();
    }

    // NOVO MÉTODO: Retorna absolutamente todos os campos visuais e lógicos para o estado padrão
    private void limparTudo() {
        // Reseta os campos de input de texto
        if (txtTamanho != null) txtTamanho.setText("40");

        txtTx.setText("0");
        txtTy.setText("0");
        txtTz.setText("0");
        cbRotAxis.setSelectedIndex(0);
        txtRotAngle.setText("0");
        txtSx.setText("1.5");
        txtSy.setText("1.5");
        txtSz.setText("1.5");
        txtShXY.setText("0");
        txtShXZ.setText("0");
        txtShYZ.setText("0");
        cbRefAxis.setSelectedIndex(0);
        cbSeqType.setSelectedIndex(0);

        // Reseta Viewport
        chkViewPort.setSelected(true);
        txtVpXMin.setText("0");
        txtVpYMin.setText("0");
        txtVpXMax.setText("0");
        txtVpYMax.setText("0");

        // Reseta os sliders e os valores lógicos de rotação visual
        slRotX.setValue(0);
        slRotY.setValue(0);
        slRotZ.setValue(0);
        slZoom.setValue(100);
        viewRotX = 0;
        viewRotY = 0;
        viewRotZ = 0;
        zoom = 100;

        // Limpa o histórico completamente ANTES de gerar o cubo
        historyLog.clear();
        sequence.clear();
        txtHistory.setText("");

        // Gera o cubo inicial (a mensagem de cubo gerado será a primeira no histórico zerado)
        gerarCubo(40);
    }

    private void aplicarTransformacaoDireta(double[][] matriz, String logMsg) {
        for (int i = 0; i < vertices.length; i++) {
            double[] vTrans = Transformacoes3D.multiplyVector(matriz, new double[]{vertices[i][0], vertices[i][1], vertices[i][2], 1});
            vertices[i][0] = vTrans[0];
            vertices[i][1] = vTrans[1];
            vertices[i][2] = vTrans[2];
        }
        logHistory(logMsg);
        atualizarTelas();
    }

    private void aplicarEmTornoDoVertice1(double[][] matrizBase, String descricaoBase) {
        if (vertices == null || vertices.length == 0) return;

        // Pega as coordenadas atuais do Vértice 1 (que está no índice 0 do array)
        double cx = vertices[0][0];
        double cy = vertices[0][1];
        double cz = vertices[0][2];

        // 1. Gera as matrizes que levam e trazem o Vértice 1 da origem
        double[][] tIda = Transformacoes3D.translation(-cx, -cy, -cz);
        double[][] tVolta = Transformacoes3D.translation(cx, cy, cz);

        // 2. Multiplica as matrizes na ordem correta: Final = tVolta * matrizBase * tIda
        double[][] passo1 = Transformacoes3D.multiply(matrizBase, tIda);
        double[][] matrizFinal = Transformacoes3D.multiply(tVolta, passo1);

        // 3. Monta o histórico detalhado mostrando todos os passos
        StringBuilder sb = new StringBuilder();
        sb.append(descricaoBase).append("\n");
        sb.append("Passo 1: Translação para a Origem (T -p):\n").append(formatarMatriz(tIda));
        sb.append("Passo 2: Matriz Base da Transformação:\n").append(formatarMatriz(matrizBase));
        sb.append("Passo 3: Translação de Volta (T p):\n").append(formatarMatriz(tVolta));
        sb.append("RESULTADO (Matriz Composta Final):\n").append(formatarMatriz(matrizFinal));
        sb.append("------------------------------------"); // Sem \n porque o seu painel já formata

        // 4. Delega TODO o trabalho de atualizar a tela, arrays e listas para o
        // método que já funciona perfeitamente para as outras transformações!
        aplicarTransformacaoDireta(matrizFinal, sb.toString());
    }

    private void adicionarASequencia() {
        String tipo = (String) cbSeqType.getSelectedItem();
        try {
            String detalheFila = "";
            if (tipo.equals("Translação")) {
                double tx = Double.parseDouble(txtTx.getText());
                double ty = Double.parseDouble(txtTy.getText());
                double tz = Double.parseDouble(txtTz.getText());
                sequence.add(Transformacoes3D.translation(tx, ty, tz));
                detalheFila = String.format("Trans(X:%.1f, Y:%.1f, Z:%.1f)", tx, ty, tz);

            } else if (tipo.equals("Escala")) {
                double sx = Double.parseDouble(txtSx.getText());
                double sy = Double.parseDouble(txtSy.getText());
                double sz = Double.parseDouble(txtSz.getText());
                sequence.add(Transformacoes3D.scaling(sx, sy, sz));
                detalheFila = String.format("Escala(X:%.1f, Y:%.1f, Z:%.1f)", sx, sy, sz);

            } else if (tipo.equals("Rotação")) {
                double ang = Double.parseDouble(txtRotAngle.getText());
                String axis = (String) cbRotAxis.getSelectedItem();
                double[][] mat = axis.equals("X") ? Transformacoes3D.rotationX(ang) :
                        (axis.equals("Y") ? Transformacoes3D.rotationY(ang) : Transformacoes3D.rotationZ(ang));
                sequence.add(mat);
                detalheFila = String.format("Rot(Eixo:%s, Ang:%.1f°)", axis, ang);

            } else if (tipo.equals("Cisalhamento")) {
                double shXY = Double.parseDouble(txtShXY.getText());
                double shXZ = Double.parseDouble(txtShXZ.getText());
                double shYZ = Double.parseDouble(txtShYZ.getText());
                sequence.add(Transformacoes3D.shear(shXY, shXZ, shYZ));
                detalheFila = String.format("Cisal(XY:%.1f, XZ:%.1f, YZ:%.1f)", shXY, shXZ, shYZ);

            } else if (tipo.equals("Reflexão")) {
                String plano = (String) cbRefAxis.getSelectedItem();
                sequence.add(Transformacoes3D.reflection(plano));
                detalheFila = String.format("Reflexão(Plano:%s)", plano);
            }

            // Adiciona o texto bonitinho na fila visual
            listModelSeq.addElement((listModelSeq.getSize() + 1) + ". " + detalheFila);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar. Verifique os valores na aba de " + tipo + ".");
        }
    }

    private void aplicarSequencia() {
        if (sequence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A fila de sequência está vazia!");
            return;
        }

        // 1. Multiplica todas as matrizes da fila
        double[][] matrizComposta = sequence.get(0);
        for (int i = 1; i < sequence.size(); i++) {
            matrizComposta = Transformacoes3D.multiply(matrizComposta, sequence.get(i));
        }

        // 2. Aplica no cubo
        aplicarTransformacaoDireta(matrizComposta, "Sequência Aplicada (" + sequence.size() + " operações)");

        // 3. LIMPA A FILA MATEMÁTICA E A FILA VISUAL (O segredo está aqui!)
        sequence.clear();
        if (listModelSeq != null) {
            listModelSeq.clear(); // Isso aqui é o que apaga os textos da telinha!
        }
    }

    private void limparSequencia() {
        if (sequence != null) sequence.clear();
        if (listModelSeq != null) listModelSeq.clear();
    }

    private void logHistory(String msg) {
        historyLog.add(msg);
        txtHistory.setText(String.join("\n", historyLog));
    }

    private void atualizarInfos() {
        if (vertices == null) return;
        StringBuilder html = new StringBuilder("<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#333;'>");

        double cx = 0, cy = 0, cz = 0, minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE, minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Vértices:</h4><div style='background:#fff; padding:5px; border:1px solid #ddd;'>");
        for (int i = 0; i < vertices.length; i++) {
            html.append(String.format("<b>V%d:</b> (%.1f, %.1f, %.1f)<br>", i + 1, vertices[i][0], vertices[i][1], vertices[i][2]));
            cx += vertices[i][0];
            cy += vertices[i][1];
            cz += vertices[i][2];
            minX = Math.min(minX, vertices[i][0]);
            maxX = Math.max(maxX, vertices[i][0]);
            minY = Math.min(minY, vertices[i][1]);
            maxY = Math.max(maxY, vertices[i][1]);
            minZ = Math.min(minZ, vertices[i][2]);
            maxZ = Math.max(maxZ, vertices[i][2]);
        }
        cx /= 8;
        cy /= 8;
        cz /= 8;
        html.append("</div>");

        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Centro Geométrico:</h4>");
        html.append(String.format("<div style='background:#fff; padding:5px; border:1px solid #ddd;'>(%.1f, %.1f, %.1f)</div>", cx, cy, cz));

        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Dimensões:</h4>");
        html.append(String.format("<div style='background:#fff; padding:5px; border:1px solid #ddd;'>Largura: %.1f<br>Altura: %.1f<br>Profundidade: %.1f</div>", (maxX - minX), (maxY - minY), (maxZ - minZ)));

        int oct = 1;
        if (cx >= 0 && cy >= 0 && cz >= 0) oct = 1;
        else if (cx < 0 && cy >= 0 && cz >= 0) oct = 2;
        else if (cx < 0 && cy < 0 && cz >= 0) oct = 3;
        else if (cx >= 0 && cy < 0 && cz >= 0) oct = 4;
        else if (cx >= 0 && cy >= 0 && cz < 0) oct = 5;
        else if (cx < 0 && cy >= 0 && cz < 0) oct = 6;
        else if (cx < 0 && cy < 0 && cz < 0) oct = 7;
        else if (cx >= 0 && cy < 0 && cz < 0) oct = 8;
        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Octante Dominante:</h4>");
        html.append("<div style='background:#fff; padding:5px; border:1px solid #ddd;'>").append(oct).append("</div>");

        html.append("</body></html>");
        txtInfoObject.setText(html.toString());
    }

    private void atualizarTelas() {
        atualizarInfos();
        if (canvasMundo != null) canvasMundo.repaint();
        if (canvasViewport != null) canvasViewport.repaint();
    }

    // --- Rendering em "Tamanho de Pixel" ---

    private void configurarGraficos(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setStroke(new BasicStroke(1));
    }

    private void desenharEixos(Graphics2D g, int cX, int cY, boolean apllyRot) {
        int len = 150;
        double rX = apllyRot ? viewRotX : 0;
        double rY = apllyRot ? viewRotY : 0;
        double rZ = apllyRot ? viewRotZ : 0;
        double z = apllyRot ? zoom : 100;

        double[] endX = Transformacoes3D.projectIsometric(new double[]{len, 0, 0}, rX, rY, rZ, z);
        double[] endY = Transformacoes3D.projectIsometric(new double[]{0, len, 0}, rX, rY, rZ, z);
        double[] endZ = Transformacoes3D.projectIsometric(new double[]{0, 0, len}, rX, rY, rZ, z);

        g.setColor(Color.RED);
        g.drawLine(cX, cY, cX + (int) endX[0], cY - (int) endX[1]);
        g.setColor(new Color(0, 180, 0));
        g.drawLine(cX, cY, cX + (int) endY[0], cY - (int) endY[1]);
        g.setColor(Color.BLUE);
        g.drawLine(cX, cY, cX + (int) endZ[0], cY - (int) endZ[1]);
    }

    private void desenharMundo(Graphics2D g) {
        if (vertices == null) return;
        configurarGraficos(g);
        int cX = canvasMundo.getWidth() / 2, cY = canvasMundo.getHeight() / 2;

        desenharEixos(g, cX, cY, true);

        //g.setColor(Color.BLACK);
        for (int[] aresta : arestas) {
            double[] p1 = Transformacoes3D.projectIsometric(vertices[aresta[0]], viewRotX, viewRotY, viewRotZ, zoom);
            double[] p2 = Transformacoes3D.projectIsometric(vertices[aresta[1]], viewRotX, viewRotY, viewRotZ, zoom);
            g.drawLine(cX + (int) p1[0], cY - (int) p1[1], cX + (int) p2[0], cY - (int) p2[1]);
        }
    }

    private void desenharViewport(Graphics2D g) {
        if(vertices == null || !chkViewPort.isSelected()) return;
        try {
            configurarGraficos(g);
            int w = canvasViewport.getWidth();   // 300
            int h = canvasViewport.getHeight();  // 300

            // 1. A JANELA DO MUNDO (Window) fica fixa na matemática para garantir o centro perfeito.
            // Como a sua tela do Mundo tem 500x500, criamos um espaço simétrico de -250 a +250.
            // Isso garante que a origem (0,0) fique cravada no centro geométrico.
            double wXMin = -250;
            double wYMin = -250;
            double wXMax = 250;
            double wYMax = 250;

            // 2. A VIEWPORT (Tela) agora lê os valores exatos que o usuário digita na interface!
            // E como Viewport representa a tela física (pixels), o usuário SÓ PODE DIGITAR POSITIVOS (ex: 0 a 300)
            double vpXMin = Double.parseDouble(txtVpXMin.getText());
            double vpYMin = Double.parseDouble(txtVpYMin.getText());
            double vpXMax = Double.parseDouble(txtVpXMax.getText());
            double vpYMax = Double.parseDouble(txtVpYMax.getText());

            // 3. BARREIRA CORRIGIDA: Se o usuário digitar negativo na Viewport, recusa!
            if (vpXMin < 0 || vpYMin < 0 || vpXMax < 0 || vpYMax < 0) {
                chkViewPort.setSelected(false);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "A Viewport representa a tela física e não aceita valores negativos. Insira valores positivos (ex: 0 a 300).",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                });
                return;
            }

            // Proteção contra inversão de tela na Viewport
            if (vpXMax <= vpXMin) vpXMax = vpXMin + 1;
            if (vpYMax <= vpYMin) vpYMax = vpYMin + 1;

            g.setColor(Color.BLACK);
            for (int[] aresta : arestas) {
                // Projeta 3D para 2D usando a matriz isométrica
                double[] p1Proj = Transformacoes3D.projectIsometric(vertices[aresta[0]], viewRotX, viewRotY, viewRotZ, zoom);
                double[] p2Proj = Transformacoes3D.projectIsometric(vertices[aresta[1]], viewRotX, viewRotY, viewRotZ, zoom);

                // PASSO 1: Mapeia do Mundo (que está centralizado) para a Viewport (que o usuário definiu)
                double[] p1Vp = Transformacoes3D.mapToViewport(
                        p1Proj[0], p1Proj[1],
                        wXMin, wYMin, wXMax, wYMax,
                        vpXMin, vpYMin, vpXMax, vpYMax
                );
                double[] p2Vp = Transformacoes3D.mapToViewport(
                        p2Proj[0], p2Proj[1],
                        wXMin, wYMin, wXMax, wYMax,
                        vpXMin, vpYMin, vpXMax, vpYMax
                );

                // PASSO 2: Recorta (Clipping) usando os limites da Viewport que o usuário digitou
                double[] clipped = Transformacoes3D.cohenSutherlandClip(
                        p1Vp[0], p1Vp[1], p2Vp[0], p2Vp[1],
                        vpXMin, vpYMin, vpXMax, vpYMax
                );

                // PASSO 3: Desenha a linha se ela sobreviveu ao recorte
                if (clipped != null) {
                    g.drawLine((int) clipped[0], (int) clipped[1], (int) clipped[2], (int) clipped[3]);
                }
            }
        } catch(NumberFormatException e) {
            // Se o campo estiver vazio ou com letras, ele também desmarca a viewport por segurança
            chkViewPort.setSelected(false);
        } catch(Exception ignored) {}
    }

    // Método auxiliar para formatar a matriz em texto alinhado
    private String formatarMatriz(double[][] matriz) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matriz.length; i++) {
            sb.append("  | ");
            for (int j = 0; j < matriz[i].length; j++) {
                // Formata com 2 casas decimais e espaçamento fixo para alinhar as colunas
                sb.append(String.format("%6.2f ", matriz[i][j]));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

            /*
            // Desenha a borda ao redor da Viewport para acabamento visual
            g.setColor(new Color(33, 53, 85)); // Cor azul escura baseada no seu tema
            g.setStroke(new BasicStroke(2));
            g.drawRect(0, 0, w - 1, h - 1);
            */

}