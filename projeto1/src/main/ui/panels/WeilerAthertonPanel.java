package main.ui.panels;

import main.algorithms.WeilerAtherton;
import main.algorithms.WeilerAtherton.PontoWA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeilerAthertonPanel extends JPanel {

    private final Color COR_FUNDO = new Color(224, 224, 224);
    private final Color COR_BOTAO_VERDE = new Color(76, 175, 80);
    private final Color COR_BOTAO_VERMELHO = new Color(244, 67, 54);
    private final Color COR_BOTAO_ESCURO = new Color(46, 125, 50);
    private final Color COR_BOTAO_ROXO = new Color(156, 39, 176);

    // Estado Global
    private double xMin = 150, yMin = 100, xMax = 450, yMax = 300;
    private List<PontoWA> poligonoOriginal = new ArrayList<>();
    private List<List<PontoWA>> poligonosRecortados = new ArrayList<>();
    private boolean isFechado = false;
    private boolean mostrarRecortado = false;

    // Componentes Esquerdos
    private JLabel lblLiveCoords;
    private JTextField txtXMax, txtXMin, txtYMax, txtYMin;

    // Componentes Direitos
    private JLabel lblLastPoint;
    private JLabel lblStatus;

    // Canvas
    private CanvasPanel canvas;

    public WeilerAthertonPanel() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
    }

    // PAINEL ESQUERDO ***************************
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Valores inválidos nas coordenadas.");
            }
        });

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnDefinir);
        painelEsq.add(pnlBtn);

        add(painelEsq, BorderLayout.WEST);
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

    // PAINEL DIREITO ********************************
    private void setupPainelDireito() {
        JPanel painelDir = new JPanel();
        painelDir.setLayout(new BoxLayout(painelDir, BoxLayout.Y_AXIS));
        painelDir.setPreferredSize(new Dimension(300, 0));
        painelDir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        painelDir.setBackground(COR_FUNDO);

        JLabel tituloDir = new JLabel("Criar Polígono (WA)", SwingConstants.CENTER);
        tituloDir.setFont(new Font("Arial", Font.BOLD, 14));
        tituloDir.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelDir.add(tituloDir);
        painelDir.add(Box.createVerticalStrut(5));
        painelDir.add(new JSeparator());
        painelDir.add(Box.createVerticalStrut(10));

        lblLastPoint = new JLabel("Último ponto: (-, -)");
        lblLastPoint.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelDir.add(lblLastPoint);
        painelDir.add(Box.createVerticalStrut(10));

        lblStatus = new JLabel("Status: Desenhando...");
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelDir.add(lblStatus);

        painelDir.add(Box.createVerticalGlue());

        JPanel pnlBotoes = new JPanel(new GridLayout(3, 1, 0, 5));
        pnlBotoes.setOpaque(false);
        pnlBotoes.add(criarBotao("Fechar Polígono", COR_BOTAO_ROXO, e -> {
            if (poligonoOriginal.size() > 2) {
                isFechado = true;
                lblStatus.setText("Status: Polígono Fechado.");
                canvas.repaint();
            }
        }));
        pnlBotoes.add(criarBotao("Aplicar Recorte (WA)", COR_BOTAO_ESCURO, e -> {
            if (isFechado) {
                poligonosRecortados = WeilerAtherton.clipPolygon(poligonoOriginal, xMin, xMax, yMin, yMax);
                mostrarRecortado = true;
                lblStatus.setText("Status: Recorte Aplicado!");
                canvas.repaint();
            }
        }));
        pnlBotoes.add(criarBotao("Resetar", COR_BOTAO_VERMELHO, e -> resetValues()));

        JPanel wrapperBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperBotoes.setOpaque(false);
        wrapperBotoes.add(pnlBotoes);
        painelDir.add(wrapperBotoes);

        add(painelDir, BorderLayout.EAST);
    }

    private JButton criarBotao(String texto, Color cor, ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 30));
        btn.addActionListener(acao);
        return btn;
    }

    private void resetValues() {
        poligonoOriginal.clear();
        poligonosRecortados.clear();
        isFechado = false;
        mostrarRecortado = false;

        xMin = 150; xMax = 450; yMin = 100; yMax = 300;
        txtXMin.setText("150"); txtXMax.setText("450");
        txtYMin.setText("100"); txtYMax.setText("300");

        lblLastPoint.setText("Último ponto: (-, -)");
        lblStatus.setText("Status: Desenhando...");
        canvas.repaint();
    }

    // CANVAS ********************************
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
                    lblLastPoint.setText(String.format("Último ponto: (%d, %d)", e.getX(), e.getY()));
                    if (!isFechado) {
                        poligonoOriginal.add(new PontoWA(e.getX(), e.getY()));
                        repaint();
                    }
                }
            });
        }

        private void drawPolygon(Graphics g, List<PontoWA> poly, boolean fill) {
            if (poly.isEmpty()) return;
            int[] xPoints = new int[poly.size()];
            int[] yPoints = new int[poly.size()];
            for (int i = 0; i < poly.size(); i++) {
                xPoints[i] = (int) Math.round(poly.get(i).x);
                yPoints[i] = (int) Math.round(poly.get(i).y);
            }
            if (fill) {
                g.fillPolygon(xPoints, yPoints, poly.size());
            } else {
                g.drawPolygon(xPoints, yPoints, poly.size());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Desenha Título Fixo Simulado no topo do Canvas
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format(Locale.US, "Xmax: %.0f | Xmin: %.0f | Ymax: %.0f | Ymin: %.0f", xMax, xMin, yMax, yMin), getWidth()/2 - 140, 20);

            // Desenha a Janela de Recorte (Azul)
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect((int)xMin, (int)yMin, (int)(xMax - xMin), (int)(yMax - yMin));

            // Desenha o polígono original
            if (!mostrarRecortado && !poligonoOriginal.isEmpty()) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(1));

                for (int i = 0; i < poligonoOriginal.size() - 1; i++) {
                    PontoWA p1 = poligonoOriginal.get(i);
                    PontoWA p2 = poligonoOriginal.get(i + 1);
                    g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                }

                if (isFechado && poligonoOriginal.size() > 2) {
                    PontoWA pFirst = poligonoOriginal.get(0);
                    PontoWA pLast = poligonoOriginal.get(poligonoOriginal.size() - 1);
                    g2d.drawLine((int)pLast.x, (int)pLast.y, (int)pFirst.x, (int)pFirst.y);
                }

                for (PontoWA p : poligonoOriginal) {
                    g2d.fillOval((int)p.x - 3, (int)p.y - 3, 6, 6);
                }
            }

            // Desenha todos os polígonos recortados devolvidos pelo Weiler-Atherton
            if (mostrarRecortado && !poligonosRecortados.isEmpty()) {
                for (List<PontoWA> poly : poligonosRecortados) {
                    g2d.setColor(new Color(156, 39, 176, 150)); // Roxo semi-transparente
                    drawPolygon(g2d, poly, true);

                    g2d.setColor(new Color(106, 27, 154)); // Roxo escuro para a borda
                    g2d.setStroke(new BasicStroke(2));
                    drawPolygon(g2d, poly, false);
                }
            }
        }
    }
}