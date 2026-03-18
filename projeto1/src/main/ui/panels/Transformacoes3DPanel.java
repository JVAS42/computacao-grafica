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
    private List<String> historyLog = new ArrayList<>();

    // Inputs de Transformação
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
        leftScroll.setPreferredSize(new Dimension(500, 0)); // Espaço de sobra para evitar cortes
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Remove scroll horizontal
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
        JTextField txtTamanho = estilizarInput(new JTextField("40"));
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
        pnlTrans.add(criarLabel("X:")); pnlTrans.add(txtTx);
        pnlTrans.add(criarLabel("Y:")); pnlTrans.add(txtTy);
        pnlTrans.add(criarLabel("Z:")); pnlTrans.add(txtTz);
        JButton btnApplyTrans = criarBotaoPrimario("Aplicar Translação");
        btnApplyTrans.addActionListener(e -> aplicarTransformacaoDireta(
                Transformacoes3D.translation(Double.parseDouble(txtTx.getText()), Double.parseDouble(txtTy.getText()), Double.parseDouble(txtTz.getText())),
                "Translação ("+txtTx.getText()+", "+txtTy.getText()+", "+txtTz.getText()+")"
        ));
        pnlTrans.add(new JLabel("")); pnlTrans.add(btnApplyTrans);
        tabTransforms.addTab("Trans", pnlTrans);

        // Rotação
        JPanel pnlRot = criarAbaTransformacao(new GridLayout(3, 2, 8, 8));
        cbRotAxis = new JComboBox<>(new String[]{"X", "Y", "Z"});
        cbRotAxis.setFont(FONT_DEFAULT);
        txtRotAngle = estilizarInput(new JTextField("0"));
        pnlRot.add(criarLabel("Eixo:")); pnlRot.add(cbRotAxis);
        pnlRot.add(criarLabel("Ângulo:")); pnlRot.add(txtRotAngle);
        JButton btnApplyRot = criarBotaoPrimario("Aplicar Rotação");
        btnApplyRot.addActionListener(e -> {
            double ang = Double.parseDouble(txtRotAngle.getText());
            double[][] mat = cbRotAxis.getSelectedItem().equals("X") ? Transformacoes3D.rotationX(ang) :
                    (cbRotAxis.getSelectedItem().equals("Y") ? Transformacoes3D.rotationY(ang) : Transformacoes3D.rotationZ(ang));
            aplicarEmTornoDoVertice1(mat, "Rotação Eixo "+cbRotAxis.getSelectedItem()+" ("+ang+"°)");
        });
        pnlRot.add(new JLabel("")); pnlRot.add(btnApplyRot);
        tabTransforms.addTab("Rot", pnlRot);

        // Escala
        JPanel pnlScale = criarAbaTransformacao(new GridLayout(4, 2, 8, 8));
        txtSx = estilizarInput(new JTextField("1.5"));
        txtSy = estilizarInput(new JTextField("1.5"));
        txtSz = estilizarInput(new JTextField("1.5"));
        pnlScale.add(criarLabel("X:")); pnlScale.add(txtSx);
        pnlScale.add(criarLabel("Y:")); pnlScale.add(txtSy);
        pnlScale.add(criarLabel("Z:")); pnlScale.add(txtSz);
        JButton btnApplyScale = criarBotaoPrimario("Aplicar Escala");
        btnApplyScale.addActionListener(e -> aplicarEmTornoDoVertice1(
                Transformacoes3D.scaling(Double.parseDouble(txtSx.getText()), Double.parseDouble(txtSy.getText()), Double.parseDouble(txtSz.getText())),
                "Escala ("+txtSx.getText()+", "+txtSy.getText()+", "+txtSz.getText()+")"
        ));
        pnlScale.add(new JLabel("")); pnlScale.add(btnApplyScale);
        tabTransforms.addTab("Escala", pnlScale);

        // Cisalhamento
        JPanel pnlShear = criarAbaTransformacao(new GridLayout(4, 2, 8, 8));
        txtShXY = estilizarInput(new JTextField("0"));
        txtShXZ = estilizarInput(new JTextField("0"));
        txtShYZ = estilizarInput(new JTextField("0"));
        pnlShear.add(criarLabel("XY:")); pnlShear.add(txtShXY);
        pnlShear.add(criarLabel("XZ:")); pnlShear.add(txtShXZ);
        pnlShear.add(criarLabel("YZ:")); pnlShear.add(txtShYZ);
        JButton btnSh = criarBotaoPrimario("Aplicar Cisalh.");
        btnSh.addActionListener(e-> aplicarTransformacaoDireta(Transformacoes3D.shear(Double.parseDouble(txtShXY.getText()), Double.parseDouble(txtShXZ.getText()), Double.parseDouble(txtShYZ.getText())), "Cisalhamento"));
        pnlShear.add(new JLabel("")); pnlShear.add(btnSh);
        tabTransforms.addTab("Cisal", pnlShear);

        // Reflexão
        JPanel pnlRef = criarAbaTransformacao(new GridLayout(3, 2, 8, 8));
        cbRefAxis = new JComboBox<>(new String[]{"XY", "XZ", "YZ"});
        cbRefAxis.setFont(FONT_DEFAULT);
        pnlRef.add(criarLabel("Plano:")); pnlRef.add(cbRefAxis);
        JButton btnRef = criarBotaoPrimario("Aplicar Reflex.");
        btnRef.addActionListener(e-> aplicarTransformacaoDireta(Transformacoes3D.reflection((String)cbRefAxis.getSelectedItem()), "Reflexão plano "+cbRefAxis.getSelectedItem()));
        pnlRef.add(new JLabel("")); pnlRef.add(btnRef);
        tabTransforms.addTab("Reflex", pnlRef);

        panel.add(tabTransforms);
        panel.add(Box.createVerticalStrut(15));

        // --- Visualização ---
        JPanel pnlVis = criarSecao("Visualização", new GridLayout(5, 2, 8, 8));
        slRotX = new JSlider(-180, 180, 0); slRotY = new JSlider(-180, 180, 0);
        slRotZ = new JSlider(-180, 180, 0); slZoom = new JSlider(50, 200, 100);
        slRotX.setBackground(BG_PANEL); slRotY.setBackground(BG_PANEL);
        slRotZ.setBackground(BG_PANEL); slZoom.setBackground(BG_PANEL);

        slRotX.addChangeListener(e -> { viewRotX = slRotX.getValue(); atualizarTelas(); });
        slRotY.addChangeListener(e -> { viewRotY = slRotY.getValue(); atualizarTelas(); });
        slRotZ.addChangeListener(e -> { viewRotZ = slRotZ.getValue(); atualizarTelas(); });
        slZoom.addChangeListener(e -> { zoom = slZoom.getValue(); atualizarTelas(); });

        pnlVis.add(criarLabelCor("X (Red):", Color.RED)); pnlVis.add(slRotX);
        pnlVis.add(criarLabelCor("Y (Green):", new Color(0, 150, 0))); pnlVis.add(slRotY);
        pnlVis.add(criarLabelCor("Z (Blue):", Color.BLUE)); pnlVis.add(slRotZ);
        pnlVis.add(criarLabel("Zoom (%):")); pnlVis.add(slZoom);

        JButton btnResetVis = criarBotaoPrimario("Resetar Vis.");
        btnResetVis.addActionListener(e-> { slRotX.setValue(0); slRotY.setValue(0); slRotZ.setValue(0); slZoom.setValue(100); });
        pnlVis.add(new JLabel("")); pnlVis.add(btnResetVis);
        panel.add(pnlVis);
        panel.add(Box.createVerticalStrut(15));

        // --- Sequência ---
        JPanel pnlSeq = criarSecao("Sequência", new GridLayout(3, 1, 5, 8));
        cbSeqType = new JComboBox<>(new String[]{"Translação", "Rotação", "Escala", "Cisalhamento", "Reflexão"});
        cbSeqType.setFont(FONT_DEFAULT);
        JButton btnAddSeq = criarBotaoPrimario("Adicionar à Sequência");
        btnAddSeq.addActionListener(e -> adicionarASequencia());
        JButton btnApplySeq = criarBotaoPrimario("Aplicar Sequência");
        btnApplySeq.addActionListener(e -> aplicarSequencia());
        pnlSeq.add(cbSeqType); pnlSeq.add(btnAddSeq); pnlSeq.add(btnApplySeq);
        panel.add(pnlSeq);
        panel.add(Box.createVerticalStrut(15));

        // --- Viewport ---
        JPanel pnlVp = criarSecao("Viewport 3D", new GridLayout(5, 2, 8, 8));
        chkViewPort = new JCheckBox("Ativar Viewport", true);
        chkViewPort.setBackground(BG_PANEL); chkViewPort.setFont(FONT_BOLD);
        chkViewPort.addActionListener(e -> atualizarTelas());
        txtVpXMin = estilizarInput(new JTextField("-100"));
        txtVpYMin = estilizarInput(new JTextField("-100"));
        txtVpXMax = estilizarInput(new JTextField("100"));
        txtVpYMax = estilizarInput(new JTextField("100"));
        pnlVp.add(chkViewPort); pnlVp.add(new JLabel(""));
        pnlVp.add(criarLabel("Xmin:")); pnlVp.add(txtVpXMin);
        pnlVp.add(criarLabel("Ymin:")); pnlVp.add(txtVpYMin);
        pnlVp.add(criarLabel("Xmax:")); pnlVp.add(txtVpXMax);
        pnlVp.add(criarLabel("Ymax:")); pnlVp.add(txtVpYMax);
        panel.add(pnlVp);

        JButton btnLimpar = criarBotaoPrimario("Limpar Tudo");
        btnLimpar.setBackground(new Color(180, 50, 50));
        btnLimpar.addActionListener(e -> { gerarCubo(40); historyLog.clear(); btnResetVis.doClick(); });
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

        // Instancia as telas base (brancas)
        canvasMundo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); desenharMundo((Graphics2D) g);
            }
        };
        canvasMundo.setBackground(Color.WHITE);

        canvasViewport = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); desenharViewport((Graphics2D) g);
            }
        };
        canvasViewport.setBackground(Color.WHITE);

        // Adiciona as telas usando o gerador de cabeçalho moderno e quadrado
        container.add(criarJanelaQuadrada("Mundo (500x500)", canvasMundo, 500), gbc);
        container.add(criarJanelaQuadrada("Viewport (300x300)", canvasViewport, 300), gbc);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG_PANEL);

        return scroll;
    }

    // Método para criar o aspecto moderno de "Janela"
    private JPanel criarJanelaQuadrada(String titulo, JPanel canvas, int size) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Cabeçalho da janela
        JLabel lblHeader = new JLabel(titulo, SwingConstants.CENTER);
        lblHeader.setOpaque(true);
        lblHeader.setBackground(BG_BUTTON);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(FONT_BOLD);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        // O Canvas interno tem seu tamanho rígido
        canvas.setPreferredSize(new Dimension(size, size));

        wrapper.add(lblHeader, BorderLayout.NORTH);
        wrapper.add(canvas, BorderLayout.CENTER);

        return wrapper;
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

    private void gerarCubo(double size) {
        vertices = new double[][] {
                {0, 0, 0}, {size, 0, 0}, {size, size, 0}, {0, size, 0},
                {0, 0, size}, {size, 0, size}, {size, size, size}, {0, size, size}
        };
        sequence.clear();
        logHistory("Cubo gerado (Tamanho: " + size + ")");
        atualizarTelas();
    }

    private void aplicarTransformacaoDireta(double[][] matriz, String logMsg) {
        for (int i = 0; i < vertices.length; i++) {
            double[] vTrans = Transformacoes3D.multiplyVector(matriz, new double[]{vertices[i][0], vertices[i][1], vertices[i][2], 1});
            vertices[i][0] = vTrans[0]; vertices[i][1] = vTrans[1]; vertices[i][2] = vTrans[2];
        }
        logHistory(logMsg);
        atualizarTelas();
    }

    private void aplicarEmTornoDoVertice1(double[][] matriz, String logMsg) {
        double px = vertices[0][0], py = vertices[0][1], pz = vertices[0][2];
        double[][] toOrigin = Transformacoes3D.translation(-px, -py, -pz);
        double[][] toPos = Transformacoes3D.translation(px, py, pz);
        double[][] comp = Transformacoes3D.multiply(toPos, Transformacoes3D.multiply(matriz, toOrigin));
        aplicarTransformacaoDireta(comp, logMsg + " no V1");
    }

    private void adicionarASequencia() {
        String tipo = (String) cbSeqType.getSelectedItem();
        try {
            if (tipo.equals("Translação")) {
                sequence.add(Transformacoes3D.translation(Double.parseDouble(txtTx.getText()), Double.parseDouble(txtTy.getText()), Double.parseDouble(txtTz.getText())));
            } else if (tipo.equals("Escala")) {
                sequence.add(Transformacoes3D.scaling(Double.parseDouble(txtSx.getText()), Double.parseDouble(txtSy.getText()), Double.parseDouble(txtSz.getText())));
            } else if (tipo.equals("Rotação")) {
                double ang = Double.parseDouble(txtRotAngle.getText());
                String axis = (String) cbRotAxis.getSelectedItem();
                double[][] mat = axis.equals("X") ? Transformacoes3D.rotationX(ang) :
                        (axis.equals("Y") ? Transformacoes3D.rotationY(ang) : Transformacoes3D.rotationZ(ang));
                sequence.add(mat);
            } else if (tipo.equals("Cisalhamento")) {
                sequence.add(Transformacoes3D.shear(Double.parseDouble(txtShXY.getText()), Double.parseDouble(txtShXZ.getText()), Double.parseDouble(txtShYZ.getText())));
            } else if (tipo.equals("Reflexão")) {
                sequence.add(Transformacoes3D.reflection((String) cbRefAxis.getSelectedItem()));
            }
            logHistory("Adicionado à sequência: " + tipo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar. Verifique os valores na aba de " + tipo + ".");
        }
    }

    private void aplicarSequencia() {
        if(sequence.isEmpty()) return;
        double[][] matrizComposta = sequence.get(0);
        for (int i = 1; i < sequence.size(); i++) {
            matrizComposta = Transformacoes3D.multiply(matrizComposta, sequence.get(i));
        }
        aplicarTransformacaoDireta(matrizComposta, "Sequência Aplicada");
        sequence.clear();
    }

    private void logHistory(String msg) {
        historyLog.add(msg);
        txtHistory.setText(String.join("\n", historyLog));
    }

    private void atualizarInfos() {
        if(vertices == null) return;
        StringBuilder html = new StringBuilder("<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#333;'>");

        double cx=0, cy=0, cz=0, minX=Double.MAX_VALUE, maxX=-Double.MAX_VALUE, minY=Double.MAX_VALUE, maxY=-Double.MAX_VALUE, minZ=Double.MAX_VALUE, maxZ=-Double.MAX_VALUE;
        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Vértices:</h4><div style='background:#fff; padding:5px; border:1px solid #ddd;'>");
        for (int i = 0; i < vertices.length; i++) {
            html.append(String.format("<b>V%d:</b> (%.1f, %.1f, %.1f)<br>", i+1, vertices[i][0], vertices[i][1], vertices[i][2]));
            cx += vertices[i][0]; cy += vertices[i][1]; cz += vertices[i][2];
            minX = Math.min(minX, vertices[i][0]); maxX = Math.max(maxX, vertices[i][0]);
            minY = Math.min(minY, vertices[i][1]); maxY = Math.max(maxY, vertices[i][1]);
            minZ = Math.min(minZ, vertices[i][2]); maxZ = Math.max(maxZ, vertices[i][2]);
        }
        cx /= 8; cy /= 8; cz /= 8;
        html.append("</div>");

        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Centro Geométrico:</h4>");
        html.append(String.format("<div style='background:#fff; padding:5px; border:1px solid #ddd;'>(%.1f, %.1f, %.1f)</div>", cx, cy, cz));

        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Dimensões:</h4>");
        html.append(String.format("<div style='background:#fff; padding:5px; border:1px solid #ddd;'>Largura: %.1f<br>Altura: %.1f<br>Profundidade: %.1f</div>", (maxX-minX), (maxY-minY), (maxZ-minZ)));

        int oct = 1;
        if(cx>=0 && cy>=0 && cz>=0) oct=1; else if(cx<0 && cy>=0 && cz>=0) oct=2;
        else if(cx<0 && cy<0 && cz>=0) oct=3; else if(cx>=0 && cy<0 && cz>=0) oct=4;
        else if(cx>=0 && cy>=0 && cz<0) oct=5; else if(cx<0 && cy>=0 && cz<0) oct=6;
        else if(cx<0 && cy<0 && cz<0) oct=7; else if(cx>=0 && cy<0 && cz<0) oct=8;
        html.append("<h4 style='color:#213555; margin-bottom:5px;'>Octante Dominante:</h4>");
        html.append("<div style='background:#fff; padding:5px; border:1px solid #ddd;'>").append(oct).append("</div>");

        html.append("</body></html>");
        txtInfoObject.setText(html.toString());
    }

    private void atualizarTelas() {
        atualizarInfos();
        if(canvasMundo != null) canvasMundo.repaint();
        if(canvasViewport != null) canvasViewport.repaint();
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

        double[] endX = Transformacoes3D.projectIsometric(new double[]{len,0,0}, rX, rY, rZ, z);
        double[] endY = Transformacoes3D.projectIsometric(new double[]{0,len,0}, rX, rY, rZ, z);
        double[] endZ = Transformacoes3D.projectIsometric(new double[]{0,0,len}, rX, rY, rZ, z);

        g.setColor(Color.RED); g.drawLine(cX, cY, cX + (int)endX[0], cY - (int)endX[1]);
        g.setColor(new Color(0, 180, 0)); g.drawLine(cX, cY, cX + (int)endY[0], cY - (int)endY[1]);
        g.setColor(Color.BLUE); g.drawLine(cX, cY, cX + (int)endZ[0], cY - (int)endZ[1]);
    }

    private void desenharMundo(Graphics2D g) {
        if(vertices == null) return;
        configurarGraficos(g);
        int cX = canvasMundo.getWidth() / 2, cY = canvasMundo.getHeight() / 2;

        desenharEixos(g, cX, cY, true);

        g.setColor(Color.BLACK);
        for (int[] aresta : arestas) {
            double[] p1 = Transformacoes3D.projectIsometric(vertices[aresta[0]], viewRotX, viewRotY, viewRotZ, zoom);
            double[] p2 = Transformacoes3D.projectIsometric(vertices[aresta[1]], viewRotX, viewRotY, viewRotZ, zoom);
            g.drawLine(cX + (int)p1[0], cY - (int)p1[1], cX + (int)p2[0], cY - (int)p2[1]);
        }
    }

    private void desenharViewport(Graphics2D g) {
        if(vertices == null || !chkViewPort.isSelected()) return;
        try {
            configurarGraficos(g);
            int w = canvasViewport.getWidth(), h = canvasViewport.getHeight();
            int cX = w / 2, cY = h / 2;

            double vXMin = Double.parseDouble(txtVpXMin.getText()); double vYMin = Double.parseDouble(txtVpYMin.getText());
            double vXMax = Double.parseDouble(txtVpXMax.getText()); double vYMax = Double.parseDouble(txtVpYMax.getText());

            double wXMin = -200, wYMin = -200, wXMax = 200, wYMax = 200;

            double[] vpTopLeft = Transformacoes3D.mapToViewport(vXMin, vYMax, wXMin, wYMin, wXMax, wYMax, 0, 0, w, h);
            double[] vpBotRight = Transformacoes3D.mapToViewport(vXMax, vYMin, wXMin, wYMin, wXMax, wYMax, 0, 0, w, h);

            g.setColor(Color.LIGHT_GRAY);
            g.drawRect((int)vpTopLeft[0], h - (int)vpTopLeft[1], (int)(vpBotRight[0]-vpTopLeft[0]), (int)(vpTopLeft[1]-vpBotRight[1]));

            g.setColor(Color.BLACK);
            for (int[] aresta : arestas) {
                double[] p1Proj = Transformacoes3D.projectIsometric(vertices[aresta[0]], viewRotX, viewRotY, viewRotZ, zoom);
                double[] p2Proj = Transformacoes3D.projectIsometric(vertices[aresta[1]], viewRotX, viewRotY, viewRotZ, zoom);

                double[] p1Vp = Transformacoes3D.mapToViewport(p1Proj[0], p1Proj[1], wXMin, wYMin, wXMax, wYMax, 0, 0, w, h);
                double[] p2Vp = Transformacoes3D.mapToViewport(p2Proj[0], p2Proj[1], wXMin, wYMin, wXMax, wYMax, 0, 0, w, h);

                double[] clipped = Transformacoes3D.cohenSutherlandClip(p1Vp[0], p1Vp[1], p2Vp[0], p2Vp[1], vpTopLeft[0], vpBotRight[1], vpBotRight[0], vpTopLeft[1]);
                if(clipped != null) {
                    g.drawLine((int)clipped[0], h - (int)clipped[1], (int)clipped[2], h - (int)clipped[3]);
                }
            }
        } catch(Exception ignored) {}
    }
}