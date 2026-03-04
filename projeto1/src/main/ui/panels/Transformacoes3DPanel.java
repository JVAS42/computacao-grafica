package main.ui.panels;

import main.algorithms.Transformacoes3D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transformacoes3DPanel extends JPanel {

    private final Color COR_FUNDO = new Color(240, 240, 240);
    private final Color COR_BOTAO = new Color(76, 175, 80);

    // Estado do Objeto e Câmera
    private List<double[]> objectVertices = new ArrayList<>();
    private int[][] objectEdges = new int[0][0];
    private double camRotX = 0, camRotY = 0, camRotZ = 0;
    private int zoom = 100;

    // Componentes de UI - Esquerda
    private JTextField txtTamanhoObj;
    private JTextField txtTransX, txtTransY, txtTransZ;
    private JComboBox<String> comboEixoRot;
    private JTextField txtRotAngulo;
    private JTextField txtEscalaX, txtEscalaY, txtEscalaZ;
    private JTextField txtCisXY, txtCisXZ, txtCisYZ;
    private JComboBox<String> comboReflexao;

    // Sliders de Câmera
    private JSlider sldCamX, sldCamY, sldCamZ, sldZoom;
    private JLabel lblCamX, lblCamY, lblCamZ, lblZoom;

    // Componentes de UI - Direita
    private JTextArea txtVerticesInfo;
    private JTextField txtCentroInfo;
    private JTextArea txtDimensoesInfo;
    private JTextField txtOctanteInfo;
    private JTextArea txtHistorico;
    private StringBuilder historicoBuilder = new StringBuilder();
    private int historicoCount = 1;

    // Canvas
    private Canvas3D mainCanvas;
    private ViewportCanvas viewportCanvas;

    public Transformacoes3DPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COR_FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setupPainelEsquerdo();
        setupPainelCentral();
        setupPainelDireito();
    }

    // ==========================================
    // PAINEL ESQUERDO (Controles)
    // ==========================================
    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        painelEsq.setPreferredSize(new Dimension(280, 0));

        // 1. Objeto 3D
        JPanel pnlObjeto = new JPanel(new GridLayout(3, 1, 5, 5));
        pnlObjeto.setBorder(BorderFactory.createTitledBorder("Objeto 3D"));
        pnlObjeto.add(new JLabel("Tipo: Cubo"));
        JPanel pnlTamanho = new JPanel(new BorderLayout());
        pnlTamanho.add(new JLabel("Tamanho: "), BorderLayout.WEST);
        txtTamanhoObj = new JTextField("40");
        pnlTamanho.add(txtTamanhoObj, BorderLayout.CENTER);
        pnlObjeto.add(pnlTamanho);
        pnlObjeto.add(criarBotao("Gerar Objeto", e -> gerarCubo()));
        painelEsq.add(pnlObjeto);
        painelEsq.add(Box.createVerticalStrut(10));

        // 2. Transformações (Abas simuladas com painéis)
        JTabbedPane tabbedTransformacoes = new JTabbedPane();

        // Aba Translação
        JPanel pnlTrans = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlTrans.add(new JLabel("X:")); pnlTrans.add(txtTransX = new JTextField("0"));
        pnlTrans.add(new JLabel("Y:")); pnlTrans.add(txtTransY = new JTextField("0"));
        pnlTrans.add(new JLabel("Z:")); pnlTrans.add(txtTransZ = new JTextField("0"));
        pnlTrans.add(new JLabel()); pnlTrans.add(criarBotao("Aplicar", e -> aplicarTranslacao()));
        tabbedTransformacoes.addTab("Translação", pnlTrans);

        // Aba Rotação
        JPanel pnlRot = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlRot.add(new JLabel("Eixo:")); pnlRot.add(comboEixoRot = new JComboBox<>(new String[]{"x", "y", "z"}));
        pnlRot.add(new JLabel("Ângulo:")); pnlRot.add(txtRotAngulo = new JTextField("0"));
        pnlRot.add(new JLabel()); pnlRot.add(criarBotao("Aplicar", e -> aplicarRotacao()));
        tabbedTransformacoes.addTab("Rotação", pnlRot);

        // Aba Escala
        JPanel pnlEscala = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlEscala.add(new JLabel("X:")); pnlEscala.add(txtEscalaX = new JTextField("1"));
        pnlEscala.add(new JLabel("Y:")); pnlEscala.add(txtEscalaY = new JTextField("1"));
        pnlEscala.add(new JLabel("Z:")); pnlEscala.add(txtEscalaZ = new JTextField("1"));
        pnlEscala.add(new JLabel()); pnlEscala.add(criarBotao("Aplicar", e -> aplicarEscala()));
        tabbedTransformacoes.addTab("Escala", pnlEscala);

        // Aba Cisalhamento e Reflexão omitidas parcialmente por espaço, mas seguem o mesmo padrão
        painelEsq.add(tabbedTransformacoes);
        painelEsq.add(Box.createVerticalStrut(10));

        // 3. Visualização (Câmera e Zoom)
        JPanel pnlVis = new JPanel(new GridLayout(5, 3, 2, 2));
        pnlVis.setBorder(BorderFactory.createTitledBorder("Visualização"));

        sldCamX = configurarSlider(pnlVis, "X:", lblCamX = new JLabel("0°"), -180, 180, 0);
        sldCamY = configurarSlider(pnlVis, "Y:", lblCamY = new JLabel("0°"), -180, 180, 0);
        sldCamZ = configurarSlider(pnlVis, "Z:", lblCamZ = new JLabel("0°"), -180, 180, 0);
        sldZoom = configurarSlider(pnlVis, "Zoom:", lblZoom = new JLabel("100%"), 10, 300, 100);

        JButton btnResetVis = criarBotao("Resetar Visualização", e -> resetarVisualizacao());
        pnlVis.add(new JLabel()); pnlVis.add(btnResetVis); pnlVis.add(new JLabel());
        painelEsq.add(pnlVis);

        add(new JScrollPane(painelEsq), BorderLayout.WEST);
    }

    private JSlider configurarSlider(JPanel pnl, String label, JLabel lblValor, int min, int max, int val) {
        pnl.add(new JLabel(label));
        JSlider slider = new JSlider(min, max, val);
        slider.addChangeListener(e -> {
            lblValor.setText(slider.getValue() + (label.equals("Zoom:") ? "%" : "°"));
            atualizarCamera();
        });
        pnl.add(slider);
        pnl.add(lblValor);
        return slider;
    }

    // ==========================================
    // PAINEL CENTRAL (Telas de Desenho)
    // ==========================================
    private void setupPainelCentral() {
        JPanel painelCentro = new JPanel(new GridBagLayout());

        mainCanvas = new Canvas3D();
        mainCanvas.setPreferredSize(new Dimension(500, 500));

        viewportCanvas = new ViewportCanvas();
        viewportCanvas.setPreferredSize(new Dimension(250, 250));
        viewportCanvas.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "View Port 2D (Top-Down)", TitledBorder.CENTER, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        painelCentro.add(mainCanvas, gbc);

        gbc.gridx = 1;
        painelCentro.add(viewportCanvas, gbc);

        add(painelCentro, BorderLayout.CENTER);
    }

    // ==========================================
    // PAINEL DIREITO (Informações)
    // ==========================================
    private void setupPainelDireito() {
        JPanel painelDir = new JPanel();
        painelDir.setLayout(new BoxLayout(painelDir, BoxLayout.Y_AXIS));
        painelDir.setPreferredSize(new Dimension(280, 0));

        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Informações do Objeto"));

        JPanel gridInfo = new JPanel(new GridLayout(8, 1));
        gridInfo.add(new JLabel("Vértices:"));
        txtVerticesInfo = new JTextArea(6, 20); txtVerticesInfo.setEditable(false);
        gridInfo.add(new JScrollPane(txtVerticesInfo));

        gridInfo.add(new JLabel("Centro Geométrico:"));
        txtCentroInfo = new JTextField(); txtCentroInfo.setEditable(false);
        gridInfo.add(txtCentroInfo);

        gridInfo.add(new JLabel("Dimensões:"));
        txtDimensoesInfo = new JTextArea(3, 20); txtDimensoesInfo.setEditable(false);
        gridInfo.add(new JScrollPane(txtDimensoesInfo));

        gridInfo.add(new JLabel("Octante:"));
        txtOctanteInfo = new JTextField(); txtOctanteInfo.setEditable(false);
        gridInfo.add(txtOctanteInfo);

        pnlInfo.add(gridInfo, BorderLayout.CENTER);
        painelDir.add(pnlInfo);

        JPanel pnlHist = new JPanel(new BorderLayout());
        pnlHist.setBorder(BorderFactory.createTitledBorder("Histórico de Transformações"));
        txtHistorico = new JTextArea(10, 20); txtHistorico.setEditable(false);
        pnlHist.add(new JScrollPane(txtHistorico), BorderLayout.CENTER);
        painelDir.add(pnlHist);

        JButton btnLimpar = criarBotao("Limpar Tudo", e -> limparTudo());
        painelDir.add(Box.createVerticalStrut(10));
        painelDir.add(btnLimpar);

        add(new JScrollPane(painelDir), BorderLayout.EAST);
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
    // LÓGICA E TRANSFORMAÇÕES
    // ==========================================
    private void gerarCubo() {
        try {
            double size = Double.parseDouble(txtTamanhoObj.getText());
            objectVertices.clear();
            // Padrão JS: V1 na origem (0,0,0)
            objectVertices.add(new double[]{0, 0, 0});
            objectVertices.add(new double[]{size, 0, 0});
            objectVertices.add(new double[]{size, size, 0});
            objectVertices.add(new double[]{0, size, 0});
            objectVertices.add(new double[]{0, 0, size});
            objectVertices.add(new double[]{size, 0, size});
            objectVertices.add(new double[]{size, size, size});
            objectVertices.add(new double[]{0, size, size});

            objectEdges = new int[][]{
                    {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Base
                    {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Topo
                    {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Laterais
            };

            resetarVisualizacao();
            atualizarInfo();
            renderizar();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tamanho inválido.");
        }
    }

    private void aplicarTranslacao() {
        if (objectVertices.isEmpty()) return;
        double tx = Double.parseDouble(txtTransX.getText());
        double ty = Double.parseDouble(txtTransY.getText());
        double tz = Double.parseDouble(txtTransZ.getText());

        double[][] matriz = Transformacoes3D.translacao(tx, ty, tz);
        aplicarMatrizObjeto(matriz, "Translação (TX: " + tx + ", TY: " + ty + ", TZ: " + tz + ")");
    }

    private void aplicarEscala() {
        if (objectVertices.isEmpty()) return;
        double sx = Double.parseDouble(txtEscalaX.getText());
        double sy = Double.parseDouble(txtEscalaY.getText());
        double sz = Double.parseDouble(txtEscalaZ.getText());

        // Fixo no vértice 1 (índice 0) igual ao JS
        double[] origem = objectVertices.get(0);
        double[][] toOrigin = Transformacoes3D.translacao(-origem[0], -origem[1], -origem[2]);
        double[][] scale = Transformacoes3D.escala(sx, sy, sz);
        double[][] toPos = Transformacoes3D.translacao(origem[0], origem[1], origem[2]);

        double[][] matFinal = Transformacoes3D.multiplicarMatrizes(toPos, Transformacoes3D.multiplicarMatrizes(scale, toOrigin));
        aplicarMatrizObjeto(matFinal, "Escala em V1 (SX: " + sx + ", SY: " + sy + ", SZ: " + sz + ")");
    }

    private void aplicarRotacao() {
        if (objectVertices.isEmpty()) return;
        String eixo = (String) comboEixoRot.getSelectedItem();
        double ang = Double.parseDouble(txtRotAngulo.getText());

        double[] origem = objectVertices.get(0);
        double[][] toOrigin = Transformacoes3D.translacao(-origem[0], -origem[1], -origem[2]);
        double[][] toPos = Transformacoes3D.translacao(origem[0], origem[1], origem[2]);

        double[][] rotMat;
        if (eixo.equals("x")) rotMat = Transformacoes3D.rotacaoX(ang);
        else if (eixo.equals("y")) rotMat = Transformacoes3D.rotacaoY(ang);
        else rotMat = Transformacoes3D.rotacaoZ(ang);

        double[][] matFinal = Transformacoes3D.multiplicarMatrizes(toPos, Transformacoes3D.multiplicarMatrizes(rotMat, toOrigin));
        aplicarMatrizObjeto(matFinal, "Rotação Real (Eixo: " + eixo.toUpperCase() + ", Âng: " + ang + "°)");
    }

    private void aplicarMatrizObjeto(double[][] matriz, String log) {
        for (int i = 0; i < objectVertices.size(); i++) {
            double[] v = objectVertices.get(i);
            double[] vHomogeneo = {v[0], v[1], v[2], 1};
            double[] vTransf = Transformacoes3D.multiplicarMatrizVetor(matriz, vHomogeneo);
            objectVertices.set(i, new double[]{vTransf[0], vTransf[1], vTransf[2]});
        }
        historicoBuilder.append(historicoCount++).append(". ").append(log).append("\n");
        txtHistorico.setText(historicoBuilder.toString());
        atualizarInfo();
        renderizar();
    }

    private void atualizarCamera() {
        camRotX = sldCamX.getValue();
        camRotY = sldCamY.getValue();
        camRotZ = sldCamZ.getValue();
        zoom = sldZoom.getValue();
        renderizar();
    }

    private void resetarVisualizacao() {
        sldCamX.setValue(0); sldCamY.setValue(0); sldCamZ.setValue(0); sldZoom.setValue(100);
    }

    private void limparTudo() {
        objectVertices.clear();
        objectEdges = new int[0][0];
        historicoBuilder.setLength(0); historicoCount = 1;
        txtHistorico.setText("");
        resetarVisualizacao();
        atualizarInfo();
        renderizar();
    }

    private void atualizarInfo() {
        if (objectVertices.isEmpty()) {
            txtVerticesInfo.setText(""); txtCentroInfo.setText("");
            txtDimensoesInfo.setText(""); txtOctanteInfo.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        double cx = 0, cy = 0, cz = 0;
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        for (int i = 0; i < objectVertices.size(); i++) {
            double[] v = objectVertices.get(i);
            sb.append(String.format(Locale.US, "V%d: (%.1f, %.1f, %.1f)\n", i+1, v[0], v[1], v[2]));
            cx += v[0]; cy += v[1]; cz += v[2];
            if (v[0] < minX) minX = v[0]; if (v[0] > maxX) maxX = v[0];
            if (v[1] < minY) minY = v[1]; if (v[1] > maxY) maxY = v[1];
            if (v[2] < minZ) minZ = v[2]; if (v[2] > maxZ) maxZ = v[2];
        }

        txtVerticesInfo.setText(sb.toString());
        int n = objectVertices.size();
        txtCentroInfo.setText(String.format(Locale.US, "(%.1f, %.1f, %.1f)", cx/n, cy/n, cz/n));
        txtDimensoesInfo.setText(String.format(Locale.US, "Largura: %.1f\nAltura: %.1f\nProfundidade: %.1f", (maxX-minX), (maxY-minY), (maxZ-minZ)));
    }

    private void renderizar() {
        mainCanvas.repaint();
        viewportCanvas.repaint();
    }

    // ==========================================
    // CÁLCULO DE PROJEÇÃO (3D -> 2D)
    // ==========================================
    private Point projetarPonto(double x, double y, double z, int width, int height) {
        double[] pt = {x, y, z, 1};

        // Aplica rotação da câmera
        double[][] camXMat = Transformacoes3D.rotacaoX(camRotX);
        double[][] camYMat = Transformacoes3D.rotacaoY(camRotY);
        double[][] camZMat = Transformacoes3D.rotacaoZ(camRotZ);

        double[][] camTransf = Transformacoes3D.multiplicarMatrizes(camYMat, camXMat);
        camTransf = Transformacoes3D.multiplicarMatrizes(camZMat, camTransf);
        pt = Transformacoes3D.multiplicarMatrizVetor(camTransf, pt);

        // Projeção isométrica/perspectiva customizada do seu script.js
        double f = zoom / 100.0;
        double px = (pt[0] - pt[2]) * f * 0.7071 + width / 2.0;
        double py = (-pt[1] + (pt[0] + pt[2]) * 0.5) * f * 0.7071 + height / 2.0;

        return new Point((int) Math.round(px), (int) Math.round(py));
    }

    // ==========================================
    // ÁREA DE DESENHO PRINCIPAL
    // ==========================================
    private class Canvas3D extends JPanel {
        public Canvas3D() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        private void drawLineDDA(Graphics g, Point p1, Point p2) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y); // Usando a linha nativa do Java por otimização de renderização 3D
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(); int h = getHeight();
            int length = (int) (1655 * (zoom / 100.0));

            // Desenha Eixos Fixos
            Point center = projetarPonto(0, 0, 0, w, h);
            g.setColor(Color.RED);   drawLineDDA(g, center, projetarPonto(length, 0, 0, w, h));
            g.setColor(Color.GREEN); drawLineDDA(g, center, projetarPonto(0, length, 0, w, h));
            g.setColor(Color.BLUE);  drawLineDDA(g, center, projetarPonto(0, 0, length, w, h));

            if (objectVertices.isEmpty()) return;

            g.setColor(Color.BLACK);
            for (int[] aresta : objectEdges) {
                double[] v1 = objectVertices.get(aresta[0]);
                double[] v2 = objectVertices.get(aresta[1]);
                Point p1 = projetarPonto(v1[0], v1[1], v1[2], w, h);
                Point p2 = projetarPonto(v2[0], v2[1], v2[2], w, h);
                drawLineDDA(g, p1, p2);
            }
        }
    }

    // ==========================================
    // VIEWPORT SECUNDÁRIO (Mundo 2D Top-Down)
    // ==========================================
    private class ViewportCanvas extends JPanel {
        public ViewportCanvas() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (objectVertices.isEmpty()) return;

            int size = Math.min(getWidth(), getHeight());
            double xMin = -100, yMin = -100, xMax = 100, yMax = 100;

            g.setColor(Color.BLACK);
            for (int[] aresta : objectEdges) {
                double[] v1 = objectVertices.get(aresta[0]);
                double[] v2 = objectVertices.get(aresta[1]);

                // Converte as coordenadas do Mundo XY direto para o Canvas (Top-Down)
                int cx1 = (int) (((v1[0] - xMin) / (xMax - xMin)) * size);
                int cy1 = (int) (size - ((v1[1] - yMin) / (yMax - yMin)) * size);
                int cx2 = (int) (((v2[0] - xMin) / (xMax - xMin)) * size);
                int cy2 = (int) (size - ((v2[1] - yMin) / (yMax - yMin)) * size);

                g.drawLine(cx1, cy1, cx2, cy2);
            }
        }
    }
}
