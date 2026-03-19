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

    // Viewport centralizada em um Canvas de 500x500 (Tamanho 200x200)
    private double xMin = 150, yMin = 150, xMax = 350, yMax = 350;

    // Estado Global
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
        setBackground(Color.decode("#F0F0F0")); // Fundo padronizado

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
        atualizarHistoricoUI(); // Inicia o histórico vazio
    }

    // PAINEL ESQUERDO ***************************
    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setBackground(Color.decode("#F0F0F0"));
        painelEsquerdo.setPreferredSize(new Dimension(320, 0)); // Largura padronizada
        painelEsquerdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Container superior (Textos e Inputs)
        JPanel containerNorte = new JPanel();
        containerNorte.setLayout(new BoxLayout(containerNorte, BoxLayout.Y_AXIS));
        containerNorte.setOpaque(false);

        // Topo: Info
        JLabel lblTituloEsq = new JLabel("INFORMAÇÕES DA JANELA");
        lblTituloEsq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloEsq.setForeground(Color.decode("#213555"));
        lblTituloEsq.setAlignmentX(Component.LEFT_ALIGNMENT);

        containerNorte.add(lblTituloEsq);
        containerNorte.add(Box.createVerticalStrut(5));

        JSeparator sepEsq = new JSeparator(SwingConstants.HORIZONTAL);
        sepEsq.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerNorte.add(sepEsq);

        containerNorte.add(Box.createVerticalStrut(10));

        lblLiveCoords = new JLabel("X: 0, Y: 0");
        lblLiveCoords.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLiveCoords.setAlignmentX(Component.LEFT_ALIGNMENT);

        containerNorte.add(lblLiveCoords);
        containerNorte.add(Box.createVerticalStrut(25));

        // Controles com GridBagLayout
        JPanel painelInputs = new JPanel(new GridBagLayout());
        painelInputs.setOpaque(false);
        painelInputs.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtXMax = estilizarTextField(String.valueOf((int)xMax), 5);
        txtXMin = estilizarTextField(String.valueOf((int)xMin), 5);
        txtYMax = estilizarTextField(String.valueOf((int)yMax), 5);
        txtYMin = estilizarTextField(String.valueOf((int)yMin), 5);

        adicionarCampoGrid(painelInputs, "X Max:", txtXMax, gbc, 0);
        adicionarCampoGrid(painelInputs, "X Min:", txtXMin, gbc, 1);
        adicionarCampoGrid(painelInputs, "Y Max:", txtYMax, gbc, 2);
        adicionarCampoGrid(painelInputs, "Y Min:", txtYMin, gbc, 3);

        containerNorte.add(painelInputs);
        painelEsquerdo.add(containerNorte, BorderLayout.NORTH);

        // Botão (Rodapé)
        JButton btnDefinir = estilizarBotao("DEFINIR VALORES");
        btnDefinir.addActionListener(e -> {
            try {
                xMax = Double.parseDouble(txtXMax.getText());
                xMin = Double.parseDouble(txtXMin.getText());
                yMax = Double.parseDouble(txtYMax.getText());
                yMin = Double.parseDouble(txtYMin.getText());
                canvas.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Valores inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel pnlBtn = new JPanel(new BorderLayout());
        pnlBtn.setOpaque(false);
        pnlBtn.setBorder(new EmptyBorder(15, 0, 0, 0));
        pnlBtn.add(btnDefinir, BorderLayout.CENTER);

        painelEsquerdo.add(pnlBtn, BorderLayout.SOUTH);

        add(painelEsquerdo, BorderLayout.WEST);
    }

    private void adicionarCampoGrid(JPanel pnl, String labelText, JComponent comp, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.DARK_GRAY);
        pnl.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        pnl.add(comp, gbc);
    }

    // PAINEL DIREITO *********************************
    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBackground(Color.decode("#F0F0F0"));
        painelDireito.setPreferredSize(new Dimension(320, 0)); // Largura padronizada
        painelDireito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Topo
        JPanel pnlTopoDir = new JPanel();
        pnlTopoDir.setLayout(new BoxLayout(pnlTopoDir, BoxLayout.Y_AXIS));
        pnlTopoDir.setOpaque(false);

        JLabel lblTituloDir = new JLabel("ADICIONAR LINHA E HISTÓRICO");
        lblTituloDir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloDir.setForeground(Color.decode("#213555"));
        lblTituloDir.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTopoDir.add(lblTituloDir);
        pnlTopoDir.add(Box.createVerticalStrut(5));

        JSeparator sepDir = new JSeparator(SwingConstants.HORIZONTAL);
        sepDir.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTopoDir.add(sepDir);

        pnlTopoDir.add(Box.createVerticalStrut(10));

        lblLastPoint = new JLabel("Último ponto: (-, -)");
        lblLastPoint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLastPoint.setForeground(Color.GRAY);
        lblLastPoint.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTopoDir.add(lblLastPoint);
        pnlTopoDir.add(Box.createVerticalStrut(15));

        // Inputs
        JPanel painelInputsDir = new JPanel(new GridBagLayout());
        painelInputsDir.setOpaque(false);
        painelInputsDir.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtX1 = estilizarTextField("", 4);
        txtY1 = estilizarTextField("", 4);
        txtX2 = estilizarTextField("", 4);
        txtY2 = estilizarTextField("", 4);

        adicionarCampoGrid(painelInputsDir, "X1:", txtX1, gbc, 0);
        adicionarCampoGrid(painelInputsDir, "Y1:", txtY1, gbc, 1);
        adicionarCampoGrid(painelInputsDir, "X2:", txtX2, gbc, 2);
        adicionarCampoGrid(painelInputsDir, "Y2:", txtY2, gbc, 3);

        pnlTopoDir.add(painelInputsDir);
        pnlTopoDir.add(Box.createVerticalStrut(15));

        painelDireito.add(pnlTopoDir, BorderLayout.NORTH);

        // Área HTML para histórico
        htmlHistoryPane = new JEditorPane();
        htmlHistoryPane.setContentType("text/html");
        htmlHistoryPane.setEditable(false);
        htmlHistoryPane.setBackground(Color.WHITE);
        JScrollPane scrollHistory = new JScrollPane(htmlHistoryPane);
        scrollHistory.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        painelDireito.add(scrollHistory, BorderLayout.CENTER);

        // Botões Padronizados e Empilhados
        JPanel pnlBotoes = new JPanel(new GridLayout(4, 1, 0, 8));
        pnlBotoes.setOpaque(false);
        pnlBotoes.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnDesenhar = estilizarBotao("DESENHAR LINHA");
        btnDesenhar.addActionListener(e -> adicionarLinhaManual());

        JButton btnAnimar = estilizarBotao("ANIMAR ROTAÇÃO");
        btnAnimar.addActionListener(e -> alternarAnimacao());

        JButton btnRecorte = estilizarBotao("APLICAR RECORTE");
        btnRecorte.addActionListener(e -> {
            showOnlyClipped = true;
            canvas.repaint();
        });

        JButton btnLimpar = estilizarBotao("RESETAR TELA");
        btnLimpar.addActionListener(e -> resetValues());

        pnlBotoes.add(btnDesenhar);
        pnlBotoes.add(btnAnimar);
        pnlBotoes.add(btnRecorte);
        pnlBotoes.add(btnLimpar);

        painelDireito.add(pnlBotoes, BorderLayout.SOUTH);

        add(painelDireito, BorderLayout.EAST);
    }

    // UTILITÁRIOS DE ESTILO *****************************
    private JTextField estilizarTextField(String textoInicial, int colunas) {
        JTextField txt = new JTextField(textoInicial, colunas);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        return txt;
    }

    private JButton estilizarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.decode("#213555"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 40));
        return btn;
    }

    // CANVAS **************************
    private void setupCanvas() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.decode("#F0F0F0"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        /*JLabel lblVoltar = new JLabel("Voltar ao Início", SwingConstants.CENTER);
        lblVoltar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVoltar.setForeground(Color.GRAY);
        lblVoltar.setBorder(new EmptyBorder(0, 0, 15, 0));
        centerPanel.add(lblVoltar, BorderLayout.NORTH);*/

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500)); // Tamanho padronizado igual aos outros painéis
        centerPanel.add(canvas, BorderLayout.CENTER);

        wrapper.add(centerPanel);
        add(wrapper, BorderLayout.CENTER);
    }

    // LÓGICA E ESTADO *******************************
    private class LineDef {
        double x1, y1, x2, y2;
        public LineDef(double x1, double y1, double x2, double y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    private void resetValues() {
        if (timerAnimacao != null) timerAnimacao.stop();
        isAnimando = false;

        // Reseta centralizado para 500x500
        xMin = 150; xMax = 350; yMin = 150; yMax = 350;
        txtXMin.setText("150"); txtXMax.setText("350");
        txtYMin.setText("150"); txtYMax.setText("350");

        lines.clear();
        historyHtmlBlocks.clear();
        showOnlyClipped = false;
        clickCount = 0;
        lblLastPoint.setText("Último ponto: (-, -)");
        txtX1.setText(""); txtY1.setText(""); txtX2.setText(""); txtY2.setText("");
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
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.", "Erro", JOptionPane.ERROR_MESSAGE);
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
            lines.clear();
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
        sb.append("<div style='font-family: \"Segoe UI\", Arial; font-size: 11px; margin-bottom: 10px;'>");
        sb.append("<strong style='color:#333;'>Linha ").append(lineIndex).append(":</strong><ul style='margin: 0; padding-left: 15px;'>");

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
                    // Cor vermelha para descartado no histórico
                    sb.append(String.format(Locale.US, "<li style='color:red;'><b>Descartado</b> (%s):<br>P1=(%.1f, %.1f) [%s] &rarr; P2=(%.1f, %.1f) [%s]<br><i>%s</i></li>",
                            curr.action, prev.x1, prev.y1, prev.code1, curr.x1, curr.y1, curr.code1, math));
                }
                if (curr.x2 != prev.x2 || curr.y2 != prev.y2) {
                    // Cor vermelha para descartado no histórico
                    sb.append(String.format(Locale.US, "<li style='color:red;'><b>Descartado</b> (%s):<br>P1=(%.1f, %.1f) [%s] &rarr; P2=(%.1f, %.1f) [%s]<br><i>%s</i></li>",
                            curr.action, prev.x2, prev.y2, prev.code2, curr.x2, curr.y2, curr.code2, math));
                }
            }
        }

        StepInfo finalStep = steps.get(steps.size() - 1);
        if (finalStep.action.startsWith("Aceita")) {
            // Cor verde para aceito no histórico
            sb.append(String.format(Locale.US, "<li style='color:#008000;'><b>Aceito:</b><br>P1=(%.1f, %.1f) [%s] &rarr; P2=(%.1f, %.1f) [%s]</li>",
                    finalStep.x1, finalStep.y1, finalStep.code1, finalStep.x2, finalStep.y2, finalStep.code2));
        } else {
            // Cor vermelha para rejeitado no histórico
            sb.append("<li style='color:red;'><b>Rejeitada:</b> Nenhum segmento aceito</li>");
        }
        sb.append("</ul></div><hr style='border:0; border-top:1px solid #EEE;'>");
        historyHtmlBlocks.add(sb.toString());
    }

    private void atualizarHistoricoUI() {
        if (historyHtmlBlocks.isEmpty()) {
            htmlHistoryPane.setText("<div style='font-family: \"Segoe UI\", Arial; font-size: 12px; color: #888; padding: 10px;'>Nenhum histórico de recorte.</div>");
        } else {
            StringBuilder allHtml = new StringBuilder("<html><body style='background-color: #FFFFFF; padding: 5px;'>");
            for (String block : historyHtmlBlocks) allHtml.append(block);
            allHtml.append("</body></html>");
            htmlHistoryPane.setText(allHtml.toString());
        }
    }

    // ÁREA DE DESENHO *************************************
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1));

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

            // Desenha Título Fixo Simulado no topo do Canvas
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.format(Locale.US, "Viewport atual: Xmin(%.0f) Xmax(%.0f) Ymin(%.0f) Ymax(%.0f)", xMin, xMax, yMin, yMax), 10, 20);

            // Janela de Recorte combinando com os botões
            g.setColor(Color.decode("#213555"));
            g.drawRect((int)xMin, (int)yMin, (int)(xMax - xMin), (int)(yMax - yMin));

            // Lógica de Animação
            if (isAnimando) {
                double centroX = xMin + (xMax - xMin) / 2.0;
                double centroY = yMin + (yMax - yMin) / 2.0;

                // Comprimento maior que a diagonal da janela
                double diagonal = Math.hypot(xMax - xMin, yMax - yMin);
                double comprimento = diagonal * 1.5; // Aumentado para vazar bem da tela

                double lx1 = centroX + (comprimento / 2.0) * Math.cos(anguloAnimacao);
                double ly1 = centroY + (comprimento / 2.0) * Math.sin(anguloAnimacao);
                double lx2 = centroX - (comprimento / 2.0) * Math.cos(anguloAnimacao);
                double ly2 = centroY - (comprimento / 2.0) * Math.sin(anguloAnimacao);

                ClipResult res = CohenSutherland.clipLine(lx1, ly1, lx2, ly2, xMin, xMax, yMin, yMax);

                // Desenha a parte aceita da linha animada (VERDE)
                if (res.accept) {
                    g.setColor(new Color(0, 128, 0)); // Tom de verde
                    drawLineDDA(g, res.x1, res.y1, res.x2, res.y2);
                }

                // Desenha as partes rejeitadas (VERMELHO)
                if (!showOnlyClipped) {
                    g.setColor(Color.RED);
                    drawLineDDA(g, lx1, ly1, res.x1, res.y1);
                    drawLineDDA(g, lx2, ly2, res.x2, res.y2);
                }
                return;
            }

            // Lógica de desenho de linhas estáticas
            for (LineDef l : lines) {
                ClipResult res = CohenSutherland.clipLine(l.x1, l.y1, l.x2, l.y2, xMin, xMax, yMin, yMax);

                if (!showOnlyClipped) {
                    // Partes descartadas/recortadas (VERMELHO)
                    g.setColor(Color.RED);
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

                // Parte Aceita (VERDE)
                if (res.accept) {
                    g.setColor(new Color(0, 128, 0)); // Tom de verde
                    drawLineDDA(g, res.x1, res.y1, res.x2, res.y2);
                }
            }
        }
    }
}