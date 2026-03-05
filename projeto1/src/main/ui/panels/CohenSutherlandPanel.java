package main.ui.panels;

import main.algorithms.CohenSutherland;
import main.algorithms.CohenSutherland.ClipResult;
import main.algorithms.CohenSutherland.StepInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CohenSutherlandPanel extends JPanel {

    private final Color COR_FUNDO = new Color(224, 224, 224);
    private final Color COR_BOTAO_VERDE = new Color(76, 175, 80);
    private final Color COR_BOTAO_VERMELHO = new Color(244, 67, 54);
    private final Color COR_BOTAO_ESCURO = new Color(46, 125, 50);
    private final Color COR_BOTAO_AZUL = new Color(33, 150, 243);

    // Estado Global
    private double xMin = 150, yMin = 100, xMax = 450, yMax = 300;
    private List<LineDef> lines = new ArrayList<>();
    private List<String> historyHtmlBlocks = new ArrayList<>();
    private boolean showOnlyClipped = false;
    private int clickCount = 0;
    private double startX, startY;

    // Variáveis para Animação
    private Timer timerAnimacao;
    private double anguloAnimacao = 0;
    private boolean isAnimando = false;

    // Componentes Esquerdos
    private JLabel lblLiveCoords;
    private JTextField txtXMax, txtXMin, txtYMax, txtYMin;

    // Componentes Direitos
    private JLabel lblLastPoint;
    private JEditorPane htmlHistoryPane;
    private JTextField txtX1, txtY1, txtX2, txtY2;

    // Canvas
    private CanvasPanel canvas;

    public CohenSutherlandPanel() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
        atualizarHistoricoUI(); // Inicia o histórico vazio
    }

    // ==========================================
    // PAINEL ESQUERDO
    // ==========================================
    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        painelEsq.setPreferredSize(new Dimension(220, 0));
        painelEsq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        painelEsq.setBackground(COR_FUNDO);

        JLabel tituloEsq = new JLabel("Coordenadas da Janela");
        tituloEsq.setFont(new Font("Arial", Font.BOLD, 14));
        painelEsq.add(tituloEsq);
        painelEsq.add(Box.createVerticalStrut(5));
        painelEsq.add(new JSeparator());
        painelEsq.add(Box.createVerticalStrut(10));

        lblLiveCoords = new JLabel("X: 0, Y: 0");
        painelEsq.add(lblLiveCoords);

        painelEsq.add(Box.createVerticalGlue());

        painelEsq.add(criarLinhaForm("Xmax:", txtXMax = new JTextField(String.valueOf((int)xMax))));
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(criarLinhaForm("Xmin:", txtXMin = new JTextField(String.valueOf((int)xMin))));
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(criarLinhaForm("Ymax:", txtYMax = new JTextField(String.valueOf((int)yMax))));
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(criarLinhaForm("Ymin:", txtYMin = new JTextField(String.valueOf((int)yMin))));
        painelEsq.add(Box.createVerticalStrut(20));

        JButton btnDefinir = criarBotao("Definir Valores", COR_BOTAO_VERDE, e -> {
            try {
                xMax = Double.parseDouble(txtXMax.getText());
                xMin = Double.parseDouble(txtXMin.getText());
                yMax = Double.parseDouble(txtYMax.getText());
                yMin = Double.parseDouble(txtYMin.getText());
                canvas.repaint();
            } catch (Exception ex) {}
        });
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER)); pnlBtn.setOpaque(false);
        pnlBtn.add(btnDefinir);
        painelEsq.add(pnlBtn);

        add(painelEsq, BorderLayout.WEST);
    }

    // ==========================================
    // PAINEL DIREITO
    // ==========================================
    private void setupPainelDireito() {
        JPanel painelDir = new JPanel();
        painelDir.setLayout(new BoxLayout(painelDir, BoxLayout.Y_AXIS));
        painelDir.setPreferredSize(new Dimension(300, 0));
        painelDir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        painelDir.setBackground(COR_FUNDO);

        JLabel tituloDir = new JLabel("Adicionar Linha");
        tituloDir.setFont(new Font("Arial", Font.BOLD, 14));
        painelDir.add(tituloDir);
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(new JSeparator());
        painelDir.add(Box.createVerticalStrut(10));

        lblLastPoint = new JLabel("Último ponto: (-, -)");
        painelDir.add(lblLastPoint);
        painelDir.add(Box.createVerticalStrut(10));

        // Área HTML para histórico
        htmlHistoryPane = new JEditorPane();
        htmlHistoryPane.setContentType("text/html");
        htmlHistoryPane.setEditable(false);
        htmlHistoryPane.setBackground(COR_FUNDO);
        JScrollPane scrollHistory = new JScrollPane(htmlHistoryPane);
        scrollHistory.setBorder(null);
        painelDir.add(scrollHistory);
        painelDir.add(Box.createVerticalStrut(10));

        // Controles Adicionar Linha
        painelDir.add(criarLinhaForm("X1:", txtX1 = new JTextField()));
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(criarLinhaForm("Y1:", txtY1 = new JTextField()));
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(criarLinhaForm("X2:", txtX2 = new JTextField()));
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(criarLinhaForm("Y2:", txtY2 = new JTextField()));
        painelDir.add(Box.createVerticalStrut(15));

        JPanel pnlBotoes = new JPanel(new GridLayout(4, 1, 0, 5));
        pnlBotoes.setOpaque(false);
        pnlBotoes.add(criarBotao("Desenhar Linha", COR_BOTAO_VERDE, e -> adicionarLinhaManual()));
        pnlBotoes.add(criarBotao("Animar Rotação", COR_BOTAO_AZUL, e -> alternarAnimacao()));
        pnlBotoes.add(criarBotao("Resetar", COR_BOTAO_VERMELHO, e -> resetValues()));
        pnlBotoes.add(criarBotao("Aplicar Recorte", COR_BOTAO_ESCURO, e -> {
            showOnlyClipped = true;
            canvas.repaint();
        }));

        JPanel wrapperBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperBotoes.setOpaque(false);
        wrapperBotoes.add(pnlBotoes);
        painelDir.add(wrapperBotoes);

        add(painelDir, BorderLayout.EAST);
    }

    private JPanel criarLinhaForm(String label, JTextField tf) {
        JPanel pnl = new JPanel(new BorderLayout(5, 0));
        pnl.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(40, 20));
        pnl.add(l, BorderLayout.WEST);
        pnl.add(tf, BorderLayout.CENTER);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return pnl;
    }

    private JButton criarBotao(String texto, Color cor, ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(acao);
        return btn;
    }

    // ==========================================
    // CANVAS
    // ==========================================
    private void setupCanvas() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);

        JPanel pnlTopo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlTopo.setOpaque(false);
        pnlTopo.add(new JLabel("Voltar ao Início", SwingConstants.CENTER));
        wrapper.add(pnlTopo, BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(600, 450));
        centerWrapper.add(canvas);

        wrapper.add(centerWrapper, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    // ==========================================
    // LÓGICA E ESTADO
    // ==========================================
    private class LineDef {
        double x1, y1, x2, y2;
        public LineDef(double x1, double y1, double x2, double y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    private void resetValues() {
        if (timerAnimacao != null) timerAnimacao.stop();
        isAnimando = false;

        xMin = 150; xMax = 450; yMin = 100; yMax = 300;
        txtXMin.setText("150"); txtXMax.setText("450");
        txtYMin.setText("100"); txtYMax.setText("300");
        lines.clear();
        historyHtmlBlocks.clear();
        showOnlyClipped = false;
        clickCount = 0;
        lblLastPoint.setText("Último ponto: (-, -)");
        atualizarHistoricoUI();
        canvas.repaint();
    }

    private void adicionarLinhaManual() {
        if (isAnimando) alternarAnimacao(); // Para a animação se for adicionar linha manual
        try {
            double x1 = Double.parseDouble(txtX1.getText());
            double y1 = Double.parseDouble(txtY1.getText());
            double x2 = Double.parseDouble(txtX2.getText());
            double y2 = Double.parseDouble(txtY2.getText());
            txtX1.setText(""); txtY1.setText(""); txtX2.setText(""); txtY2.setText("");
            processarNovaLinha(x1, y1, x2, y2);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.");
        }
    }

    private void processarNovaLinha(double x1, double y1, double x2, double y2) {
        lines.add(new LineDef(x1, y1, x2, y2));
        ClipResult res = CohenSutherland.clipLine(x1, y1, x2, y2, xMin, xMax, yMin, yMax);
        gerarBlocoHistoricoHTML(res, lines.size());
        atualizarHistoricoUI();
        canvas.repaint();
    }

    private void alternarAnimacao() {
        if (isAnimando) {
            timerAnimacao.stop();
            isAnimando = false;
            canvas.repaint();
        } else {
            lines.clear();  // apenas limpa linhas
            historyHtmlBlocks.clear();
            atualizarHistoricoUI();

            isAnimando = true;
            showOnlyClipped = false;

            timerAnimacao = new Timer(50, e -> {
                anguloAnimacao += Math.toRadians(2);
                canvas.repaint();
            });
            timerAnimacao.start();
        }
    }

    private void gerarBlocoHistoricoHTML(ClipResult res, int lineIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial; font-size: 10px; margin-bottom: 10px;'>");
        sb.append("<strong>Linha ").append(lineIndex).append(":</strong><ul style='margin: 0; padding-left: 15px;'>");

        List<StepInfo> steps = res.steps;
        for (int i = 1; i < steps.size(); i++) {
            StepInfo prev = steps.get(i - 1);
            StepInfo curr = steps.get(i);

            if (curr.action.startsWith("Recorte")) {
                String math = "";
                if (curr.action.contains("TOP")) math = "y = yMax = " + yMax;
                else if (curr.action.contains("BOTTOM")) math = "y = yMin = " + yMin;
                else if (curr.action.contains("RIGHT")) math = "x = xMax = " + xMax;
                else if (curr.action.contains("LEFT")) math = "x = xMin = " + xMin;

                if (curr.x1 != prev.x1 || curr.y1 != prev.y1) {
                    sb.append(String.format(Locale.US, "<li style='color:#b00;'><b>Descartado</b> (%s):<br>P1=(%.1f, %.1f) [%s] &rarr; P2=(%.1f, %.1f) [%s]<br><i>%s</i></li>",
                            curr.action, prev.x1, prev.y1, prev.code1, curr.x1, curr.y1, curr.code1, math));
                }
                if (curr.x2 != prev.x2 || curr.y2 != prev.y2) {
                    sb.append(String.format(Locale.US, "<li style='color:#b00;'><b>Descartado</b> (%s):<br>P1=(%.1f, %.1f) [%s] &rarr; P2=(%.1f, %.1f) [%s]<br><i>%s</i></li>",
                            curr.action, prev.x2, prev.y2, prev.code2, curr.x2, curr.y2, curr.code2, math));
                }
            }
        }

        StepInfo finalStep = steps.get(steps.size() - 1);
        if (finalStep.action.startsWith("Aceita")) {
            sb.append(String.format(Locale.US, "<li style='color:green;'><b>Aceito:</b><br>P1=(%.1f, %.1f) [%s] &rarr; P2=(%.1f, %.1f) [%s]</li>",
                    finalStep.x1, finalStep.y1, finalStep.code1, finalStep.x2, finalStep.y2, finalStep.code2));
        } else {
            sb.append("<li style='color:#b00;'><b>Rejeitada:</b> Nenhum segmento aceito</li>");
        }
        sb.append("</ul></div>");
        historyHtmlBlocks.add(sb.toString());
    }

    private void atualizarHistoricoUI() {
        if (historyHtmlBlocks.isEmpty()) {
            htmlHistoryPane.setText("<p style='font-family: Arial; font-size: 11px;'>Nenhum histórico de recorte.</p>");
        } else {
            StringBuilder allHtml = new StringBuilder("<html><body style='background-color: #E0E0E0;'>");
            for (String block : historyHtmlBlocks) allHtml.append(block);
            allHtml.append("</body></html>");
            htmlHistoryPane.setText(allHtml.toString());
        }
    }

    // ==========================================
    // ÁREA DE DESENHO
    // ==========================================
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    lblLiveCoords.setText(String.format("X: %d, Y: %d", e.getX(), e.getY()));
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isAnimando) return; // Bloqueia cliques manuais durante a animação
                    lblLastPoint.setText(String.format("Último ponto: (%d, %d)", e.getX(), e.getY()));
                    if (clickCount == 0) {
                        startX = e.getX(); startY = e.getY();
                        clickCount = 1;
                    } else {
                        processarNovaLinha(startX, startY, e.getX(), e.getY());
                        clickCount = 0;
                    }
                }
            });
        }

        private void drawLineDDA(Graphics g, double x1, double y1, double x2, double y2) {
            double dx = x2 - x1, dy = y2 - y1;
            double steps = Math.max(Math.abs(dx), Math.abs(dy));
            if (steps == 0) return;
            double xInc = dx / steps, yInc = dy / steps;
            double x = x1, y = y1;
            for (int i = 0; i <= steps; i++) {
                g.fillRect((int) Math.round(x), (int) Math.round(y), 1, 1);
                x += xInc; y += yInc;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Desenha Título Fixo Simulado
            g.setColor(Color.BLACK);
            g.drawString(String.format(Locale.US, "Xmax: %.0f | Xmin: %.0f | Ymax: %.0f | Ymin: %.0f", xMax, xMin, yMax, yMin), getWidth()/2 - 120, 20);

            // Janela de Recorte (Azul)
            g.setColor(Color.BLUE);
            g.drawRect((int)xMin, (int)yMin, (int)(xMax - xMin), (int)(yMax - yMin));

            // Lógica de Animação
            if (isAnimando) {
                double centroX = xMin + (xMax - xMin) / 2.0;
                double centroY = yMin + (yMax - yMin) / 2.0;

                // Comprimento maior que a diagonal da janela
                double diagonal = Math.hypot(xMax - xMin, yMax - yMin);
                double comprimento = diagonal * 1.3;

                double lx1 = centroX + (comprimento / 2.0) * Math.cos(anguloAnimacao);
                double ly1 = centroY + (comprimento / 2.0) * Math.sin(anguloAnimacao);
                double lx2 = centroX - (comprimento / 2.0) * Math.cos(anguloAnimacao);
                double ly2 = centroY - (comprimento / 2.0) * Math.sin(anguloAnimacao);

                ClipResult res = CohenSutherland.clipLine(lx1, ly1, lx2, ly2, xMin, xMax, yMin, yMax);

                // Desenha a parte aceita da linha animada (Verde)
                if (res.accept) {
                    g.setColor(new Color(0, 128, 0));
                    drawLineDDA(g, res.x1, res.y1, res.x2, res.y2);
                }

                // Desenha as partes rejeitadas (Vermelho) para visualizar o recorte acontecendo
                if (!showOnlyClipped) {
                    g.setColor(new Color(187, 0, 0));
                    drawLineDDA(g, lx1, ly1, res.x1, res.y1);
                    drawLineDDA(g, lx2, ly2, res.x2, res.y2);
                }
                return; // Interrompe o desenho das outras linhas enquanto a animação ocorre
            }

            // Lógica de desenho de linhas estáticas (quando não está animando)
            for (LineDef l : lines) {
                ClipResult res = CohenSutherland.clipLine(l.x1, l.y1, l.x2, l.y2, xMin, xMax, yMin, yMax);

                if (!showOnlyClipped) {
                    // Partes descartadas (Vermelho)
                    g.setColor(new Color(187, 0, 0)); // #b00
                    List<StepInfo> steps = res.steps;
                    for (int j = 1; j < steps.size(); j++) {
                        StepInfo prev = steps.get(j - 1);
                        StepInfo curr = steps.get(j);
                        if (curr.action.startsWith("Recorte")) {
                            if (curr.x1 != prev.x1 || curr.y1 != prev.y1) drawLineDDA(g, prev.x1, prev.y1, curr.x1, curr.y1);
                            if (curr.x2 != prev.x2 || curr.y2 != prev.y2) drawLineDDA(g, prev.x2, prev.y2, curr.x2, curr.y2);
                        }
                    }
                    drawLineDDA(g, l.x1, l.y1, l.x2, l.y2);
                }

                if (res.accept) {
                    g.setColor(new Color(0, 128, 0)); // Verde
                    drawLineDDA(g, res.x1, res.y1, res.x2, res.y2);
                }
            }
        }
    }
}