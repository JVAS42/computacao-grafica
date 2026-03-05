package main.ui.panels;

import main.algorithms.Transformacoes3D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transformacoes3DPanel extends JPanel {

    private final Color COR_FUNDO = new Color(240, 240, 240);
    private final Color COR_BOTAO = new Color(76, 175, 80);

    // Estado do Objeto e Câmera
    private List<double[]> objectVertices = new ArrayList<>();
    private List<double[]> objectVerticesOriginal = new ArrayList<>(); // Guarda o objeto original para a Janela
    private int[][] objectEdges = new int[0][0];
    private double camRotX = 0, camRotY = 0, camRotZ = 0;
    private int zoom = 100;

    // Componentes de UI - Esquerda
    private JTextField txtTamanhoObj;
    private JTextField txtTransX, txtTransY, txtTransZ;
    private JComboBox<String> comboEixoRot;
    private JTextField txtRotAngulo;
    private JTextField txtEscalaX, txtEscalaY, txtEscalaZ;

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

    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        painelEsq.setPreferredSize(new Dimension(280, 0));

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

        JTabbedPane tabbedTransformacoes = new JTabbedPane();

        JPanel pnlTrans = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlTrans.add(new JLabel("X:")); pnlTrans.add(txtTransX = new JTextField("0"));
        pnlTrans.add(new JLabel("Y:")); pnlTrans.add(txtTransY = new JTextField("0"));
        pnlTrans.add(new JLabel("Z:")); pnlTrans.add(txtTransZ = new JTextField("0"));
        pnlTrans.add(new JLabel()); pnlTrans.add(criarBotao("Aplicar", e -> aplicarTranslacao()));
        tabbedTransformacoes.addTab("Translação", pnlTrans);

        JPanel pnlRot = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlRot.add(new JLabel("Eixo:")); pnlRot.add(comboEixoRot = new JComboBox<>(new String[]{"x", "y", "z"}));
        pnlRot.add(new JLabel("Ângulo:")); pnlRot.add(txtRotAngulo = new JTextField("0"));
        pnlRot.add(new JLabel()); pnlRot.add(criarBotao("Aplicar", e -> aplicarRotacao()));
        tabbedTransformacoes.addTab("Rotação", pnlRot);

        JPanel pnlEscala = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlEscala.add(new JLabel("X:")); pnlEscala.add(txtEscalaX = new JTextField("1"));
        pnlEscala.add(new JLabel("Y:")); pnlEscala.add(txtEscalaY = new JTextField("1"));
        pnlEscala.add(new JLabel("Z:")); pnlEscala.add(txtEscalaZ = new JTextField("1"));
        pnlEscala.add(new JLabel()); pnlEscala.add(criarBotao("Aplicar", e -> aplicarEscala()));
        tabbedTransformacoes.addTab("Escala", pnlEscala);

        painelEsq.add(tabbedTransformacoes);
        painelEsq.add(Box.createVerticalStrut(10));

        JPanel pnlVis = new JPanel(new GridLayout(5, 3, 2, 2));
        pnlVis.setBorder(BorderFactory.createTitledBorder("Câmera / Visualização"));

        sldCamX = configurarSlider(pnlVis, "X:", lblCamX = new JLabel("0°"), -180, 180, 0);
        sldCamY = configurarSlider(pnlVis, "Y:", lblCamY = new JLabel("0°"), -180, 180, 0);
        sldCamZ = configurarSlider(pnlVis, "Z:", lblCamZ = new JLabel("0°"), -180, 180, 0);
        sldZoom = configurarSlider(pnlVis, "Zoom:", lblZoom = new JLabel("100%"), 10, 300, 100);

        JButton btnResetVis = criarBotao("Resetar Câmera", e -> resetarVisualizacao());
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

    private void setupPainelCentral() {
        JPanel painelCentro = new JPanel(new GridBagLayout());

        mainCanvas = new Canvas3D();
        mainCanvas.setPreferredSize(new Dimension(650, 450)); // Área ampliada para caber Janela e Viewport

        viewportCanvas = new ViewportCanvas();
        viewportCanvas.setPreferredSize(new Dimension(250, 250));
        viewportCanvas.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Top-Down (2D)", TitledBorder.CENTER, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        painelCentro.add(mainCanvas, gbc);

        gbc.gridx = 1;
        painelCentro.add(viewportCanvas, gbc);

        add(painelCentro, BorderLayout.CENTER);
    }

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

    private void gerarCubo() {
        try {
            double size = Double.parseDouble(txtTamanhoObj.getText());
            objectVertices.clear();
            objectVerticesOriginal.clear();

            double[][] verticesBase = {
                    {0, 0, 0}, {size, 0, 0}, {size, size, 0}, {0, size, 0},
                    {0, 0, size}, {size, 0, size}, {size, size, size}, {0, size, size}
            };

            for(double[] v : verticesBase) {
                objectVertices.add(new double[]{v[0], v[1], v[2]});
                objectVerticesOriginal.add(new double[]{v[0], v[1], v[2]});
            }

            objectEdges = new int[][]{
                    {0, 1}, {1, 2}, {2, 3}, {3, 0},
                    {4, 5}, {5, 6}, {6, 7}, {7, 4},
                    {0, 4}, {1, 5}, {2, 6}, {3, 7}
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
        objectVerticesOriginal.clear();
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
    // ÁREA DE DESENHO PRINCIPAL (Janela vs Viewport)
    // ==========================================
    private class Canvas3D extends JPanel {

        // Limites da Janela do Mundo
        private final double W_XMIN = -250, W_XMAX = 250;
        private final double W_YMIN = -250, W_YMAX = 250;

        public Canvas3D() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Função para realizar a Projeção Isométrica em Coordenadas de Mundo 2D
        private double[] projetarMundo(double[] ptOriginal, double cx, double cy, double cz, int zLevel) {
            double[] pt = {ptOriginal[0], ptOriginal[1], ptOriginal[2], 1};

            double[][] rotX = Transformacoes3D.rotacaoX(cx);
            double[][] rotY = Transformacoes3D.rotacaoY(cy);
            double[][] rotZ = Transformacoes3D.rotacaoZ(cz);

            double[][] camTransf = Transformacoes3D.multiplicarMatrizes(rotZ, Transformacoes3D.multiplicarMatrizes(rotY, rotX));
            pt = Transformacoes3D.multiplicarMatrizVetor(camTransf, pt);

            double f = zLevel / 100.0;
            // Projeção isométrica sem depender das dimensões da tela (mantendo na coord. Mundo)
            double px = (pt[0] - pt[2]) * f * 0.7071;
            double py = (pt[1] + (pt[0] + pt[2]) * 0.5) * f * 0.7071;
            return new double[]{px, py};
        }

        // Mapeamento matemático do Mundo para uma região específica (Viewport) da tela
        private Point2D.Double mapWorldToScreen(Point2D.Double pt, int vXmin, int vXmax, int vYmin, int vYmax) {
            double sx = vXmin + ((pt.x - W_XMIN) / (W_XMAX - W_XMIN)) * (vXmax - vXmin);
            double sy = vYmin + ((W_YMAX - pt.y) / (W_YMAX - W_YMIN)) * (vYmax - vYmin);
            return new Point2D.Double(sx, sy);
        }

        // ==========================================
        // ALGORITMO DE RECORTE (COHEN-SUTHERLAND)
        // ==========================================
        private int computeOutCodeScreen(double x, double y, double xmin, double ymin, double xmax, double ymax) {
            int code = 0;
            if (x < xmin) code |= 1;
            else if (x > xmax) code |= 2;
            if (y < ymin) code |= 8;
            else if (y > ymax) code |= 4;
            return code;
        }

        private double[] cohenSutherlandClip(double x0, double y0, double x1, double y1, double xmin, double ymin, double xmax, double ymax) {
            int outcode0 = computeOutCodeScreen(x0, y0, xmin, ymin, xmax, ymax);
            int outcode1 = computeOutCodeScreen(x1, y1, xmin, ymin, xmax, ymax);
            boolean accept = false;

            while (true) {
                if ((outcode0 | outcode1) == 0) {
                    accept = true; break;
                } else if ((outcode0 & outcode1) != 0) {
                    break;
                } else {
                    double x = 0, y = 0;
                    int outcodeOut = (outcode0 != 0) ? outcode0 : outcode1;

                    if ((outcodeOut & 8) != 0) {
                        x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0); y = ymin;
                    } else if ((outcodeOut & 4) != 0) {
                        x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0); y = ymax;
                    } else if ((outcodeOut & 2) != 0) {
                        y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0); x = xmax;
                    } else if ((outcodeOut & 1) != 0) {
                        y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0); x = xmin;
                    }

                    if (outcodeOut == outcode0) {
                        x0 = x; y0 = y; outcode0 = computeOutCodeScreen(x0, y0, xmin, ymin, xmax, ymax);
                    } else {
                        x1 = x; y1 = y; outcode1 = computeOutCodeScreen(x1, y1, xmin, ymin, xmax, ymax);
                    }
                }
            }
            if (accept) return new double[]{x0, y0, x1, y1};
            return null;
        }

        // ==========================================
        // NOVO: DESENHO DOS EIXOS CARTESIANOS
        // ==========================================
        private void desenharEixos(Graphics g, int bX, int bY, int bW, int bH, boolean usarCamera) {
            double rx = usarCamera ? camRotX : 0;
            double ry = usarCamera ? camRotY : 0;
            double rz = usarCamera ? camRotZ : 0;
            int z = usarCamera ? zoom : 100;

            double L = 250; // Limites de desenho baseados no W_MAX

            // X e Y (Verde acinzentado) e Z (Vermelho) conforme a imagem
            Color corEixoXY = new Color(130, 180, 140);
            Color corEixoZ = new Color(200, 60, 60);

            desenharEixoUnico(g, new double[]{-L, 0, 0}, new double[]{L, 0, 0}, rx, ry, rz, z, bX, bY, bW, bH, corEixoXY, "X", "-X");
            desenharEixoUnico(g, new double[]{0, -L, 0}, new double[]{0, L, 0}, rx, ry, rz, z, bX, bY, bW, bH, corEixoXY, "Y", "-Y");
            desenharEixoUnico(g, new double[]{0, 0, -L}, new double[]{0, 0, L}, rx, ry, rz, z, bX, bY, bW, bH, corEixoZ, "Z", "-Z");

            // Rótulo da origem central (0,0,0)
            double[] pOrigem = projetarMundo(new double[]{0, 0, 0}, rx, ry, rz, z);
            Point2D.Double spOrigem = mapWorldToScreen(new Point2D.Double(pOrigem[0], pOrigem[1]), bX, bX+bW, bY, bY+bH);
            if (spOrigem.x >= bX && spOrigem.x <= bX+bW && spOrigem.y >= bY && spOrigem.y <= bY+bH) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("0,0,0", (int)spOrigem.x - 12, (int)spOrigem.y + 15);
            }
        }

        private void desenharEixoUnico(Graphics g, double[] v1, double[] v2, double rx, double ry, double rz, int zLevel, int bX, int bY, int bW, int bH, Color cor, String lblPos, String lblNeg) {
            double[] p1w = projetarMundo(v1, rx, ry, rz, zLevel);
            double[] p2w = projetarMundo(v2, rx, ry, rz, zLevel);

            Point2D.Double sp1 = mapWorldToScreen(new Point2D.Double(p1w[0], p1w[1]), bX, bX+bW, bY, bY+bH);
            Point2D.Double sp2 = mapWorldToScreen(new Point2D.Double(p2w[0], p2w[1]), bX, bX+bW, bY, bY+bH);

            double[] clipped = cohenSutherlandClip(sp1.x, sp1.y, sp2.x, sp2.y, bX, bY, bX+bW, bY+bH);
            if (clipped != null) {
                g.setColor(cor);
                g.drawLine((int)clipped[0], (int)clipped[1], (int)clipped[2], (int)clipped[3]);

                g.setColor(Color.GRAY);
                // Define qual ponta recebe qual label dependendo da distância na tela
                double d1 = Math.hypot(sp1.x - clipped[0], sp1.y - clipped[1]);
                double d2 = Math.hypot(sp1.x - clipped[2], sp1.y - clipped[3]);

                if (d1 < d2) {
                    g.drawString(lblNeg, (int)clipped[0] + 4, (int)clipped[1] - 4);
                    g.drawString(lblPos, (int)clipped[2] + 4, (int)clipped[3] - 4);
                } else {
                    g.drawString(lblPos, (int)clipped[0] + 4, (int)clipped[1] - 4);
                    g.drawString(lblNeg, (int)clipped[2] + 4, (int)clipped[3] - 4);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(); int h = getHeight();

            // Setup de Áreas
            int winX = 10, winY = 30, winW = w/2 - 20, winH = h - 40;
            int vpX = w/2 + 10, vpY = 30, vpW = w/2 - 20, vpH = h - 40;

            g.setColor(new Color(245, 245, 245)); g.fillRect(winX, winY, winW, winH);
            g.setColor(Color.LIGHT_GRAY); g.drawRect(winX, winY, winW, winH);
            g.setColor(Color.BLACK); g.drawString("Janela do Mundo (Objeto Original)", winX, winY - 5);

            g.setColor(Color.WHITE); g.fillRect(vpX, vpY, vpW, vpH);
            g.setColor(Color.BLUE); g.drawRect(vpX, vpY, vpW, vpH);
            g.setColor(Color.BLACK); g.drawString("Viewport (Projeção Isométrica + Recorte)", vpX, vpY - 5);

            // ---> DESENHA OS EIXOS EM AMBAS AS VIZUALIZAÇÕES <---
            desenharEixos(g, winX, winY, winW, winH, false);
            desenharEixos(g, vpX, vpY, vpW, vpH, true);

            if (objectVertices.isEmpty()) return;

            // 1. Janela do Mundo: Desenhar o Objeto Original
            g.setColor(Color.GRAY);
            for (int[] aresta : objectEdges) {
                double[] v1 = objectVerticesOriginal.get(aresta[0]);
                double[] v2 = objectVerticesOriginal.get(aresta[1]);

                double[] p1w = projetarMundo(v1, 0, 0, 0, 100);
                double[] p2w = projetarMundo(v2, 0, 0, 0, 100);

                Point2D.Double sp1 = mapWorldToScreen(new Point2D.Double(p1w[0], p1w[1]), winX, winX+winW, winY, winY+winH);
                Point2D.Double sp2 = mapWorldToScreen(new Point2D.Double(p2w[0], p2w[1]), winX, winX+winW, winY, winY+winH);
                g.drawLine((int)sp1.x, (int)sp1.y, (int)sp2.x, (int)sp2.y);
            }

            // 2. Viewport: Desenhar o Objeto Transformado e Recortado
            g.setColor(Color.BLUE); // Cubo agora desenhado em azul
            for (int[] aresta : objectEdges) {
                double[] v1 = objectVertices.get(aresta[0]);
                double[] v2 = objectVertices.get(aresta[1]);

                // Mapeia e Rotaciona pra projeção Isométrica do Mundo
                double[] p1w = projetarMundo(v1, camRotX, camRotY, camRotZ, zoom);
                double[] p2w = projetarMundo(v2, camRotX, camRotY, camRotZ, zoom);

                // Mapeia do Mundo para a Viewport
                Point2D.Double sp1 = mapWorldToScreen(new Point2D.Double(p1w[0], p1w[1]), vpX, vpX+vpW, vpY, vpY+vpH);
                Point2D.Double sp2 = mapWorldToScreen(new Point2D.Double(p2w[0], p2w[1]), vpX, vpX+vpW, vpY, vpY+vpH);

                // Aplica Clipping na Viewport
                double[] clipped = cohenSutherlandClip(sp1.x, sp1.y, sp2.x, sp2.y, vpX, vpY, vpX+vpW, vpY+vpH);
                if (clipped != null) {
                    g.drawLine((int)clipped[0], (int)clipped[1], (int)clipped[2], (int)clipped[3]);
                }
            }
        }
    }

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
                int cx1 = (int) (((v1[0] - xMin) / (xMax - xMin)) * size);
                int cy1 = (int) (size - ((v1[1] - yMin) / (yMax - yMin)) * size);
                int cx2 = (int) (((v2[0] - xMin) / (xMax - xMin)) * size);
                int cy2 = (int) (size - ((v2[1] - yMin) / (yMax - yMin)) * size);
                g.drawLine(cx1, cy1, cx2, cy2);
            }
        }
    }
}