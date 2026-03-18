package main.ui.panels;

import main.algorithms.SutherlandHodgman;
import main.algorithms.SutherlandHodgman.Ponto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SutherlandHodgmanPanel extends JPanel {

    // Cores da interface
    private final Color COR_FUNDO = Color.decode("#F0F0F0");
    private final Color COR_BOTAO = Color.decode("#213555");

    // Cores do desenho (Novas)
    private final Color RED_SOLIDO = Color.RED;
    // Vermelho com 100 de Alpha (transparência vai de 0 a 255)
    private final Color RED_PREENCHIMENTO = new Color(255, 0, 0, 100);

    // Estado Global
    private double xMin = 150, yMin = 100, xMax = 450, yMax = 300;
    private List<Ponto> poligonoOriginal = new ArrayList<>();
    private List<Ponto> poligonoRecortado = new ArrayList<>();
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

    public SutherlandHodgmanPanel() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        setupPainelEsquerdo();
        setupCanvas();
        setupPainelDireito();
    }

    // ==========================================
    // PAINEL ESQUERDO
    // ==========================================
    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        painelEsq.setPreferredSize(new Dimension(250, 0));
        painelEsq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                new EmptyBorder(20, 20, 20, 20)));
        painelEsq.setBackground(COR_FUNDO);

        JLabel tituloEsq = new JLabel("Coordenadas da Janela");
        tituloEsq.setFont(new Font("SansSerif", Font.BOLD, 15));
        painelEsq.add(tituloEsq);
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(new JSeparator());
        painelEsq.add(Box.createVerticalStrut(15));

        lblLiveCoords = new JLabel("X: 0, Y: 0");
        lblLiveCoords.setFont(new Font("SansSerif", Font.PLAIN, 14));
        painelEsq.add(lblLiveCoords);

        painelEsq.add(Box.createVerticalGlue());

        painelEsq.add(criarLinhaForm("Xmax:", txtXMax = new JTextField(String.valueOf((int)xMax))));
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(criarLinhaForm("Xmin:", txtXMin = new JTextField(String.valueOf((int)xMin))));
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(criarLinhaForm("Ymax:", txtYMax = new JTextField(String.valueOf((int)yMax))));
        painelEsq.add(Box.createVerticalStrut(10));
        painelEsq.add(criarLinhaForm("Ymin:", txtYMin = new JTextField(String.valueOf((int)yMin))));
        painelEsq.add(Box.createVerticalStrut(25));

        JButton btnDefinir = criarBotao("Definir Valores", e -> {
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
        JPanel pnl = new JPanel(new BorderLayout(10, 0));
        pnl.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setPreferredSize(new Dimension(50, 25));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pnl.add(l, BorderLayout.WEST);
        pnl.add(tf, BorderLayout.CENTER);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return pnl;
    }

    // ==========================================
    // PAINEL DIREITO
    // ==========================================
    private void setupPainelDireito() {
        JPanel painelDir = new JPanel();
        painelDir.setLayout(new BoxLayout(painelDir, BoxLayout.Y_AXIS));
        painelDir.setPreferredSize(new Dimension(320, 0));
        painelDir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                new EmptyBorder(20, 20, 20, 20)));
        painelDir.setBackground(COR_FUNDO);

        JLabel tituloDir = new JLabel("Criar Polígono", SwingConstants.CENTER);
        tituloDir.setFont(new Font("SansSerif", Font.BOLD, 15));
        tituloDir.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelDir.add(tituloDir);
        painelDir.add(Box.createVerticalStrut(10));
        painelDir.add(new JSeparator());
        painelDir.add(Box.createVerticalStrut(15));

        lblLastPoint = new JLabel("Último ponto: (-, -)");
        lblLastPoint.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblLastPoint.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelDir.add(lblLastPoint);
        painelDir.add(Box.createVerticalStrut(10));

        lblStatus = new JLabel("Status: Desenhando...");
        lblStatus.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelDir.add(lblStatus);

        painelDir.add(Box.createVerticalGlue());

        JPanel pnlBotoes = new JPanel(new GridLayout(3, 1, 0, 15));
        pnlBotoes.setOpaque(false);

        pnlBotoes.add(criarBotao("Fechar Polígono", e -> {
            if (poligonoOriginal.size() > 2) {
                isFechado = true;
                lblStatus.setText("Status: Polígono Fechado.");
                canvas.repaint();
            }
        }));
        pnlBotoes.add(criarBotao("Aplicar Recorte", e -> {
            if (isFechado) {
                poligonoRecortado = SutherlandHodgman.clipPolygon(poligonoOriginal, xMin, xMax, yMin, yMax);
                mostrarRecortado = true;
                lblStatus.setText("Status: Recorte Aplicado!");
                canvas.repaint();
            }
        }));
        pnlBotoes.add(criarBotao("Resetar", e -> resetValues()));

        JPanel wrapperBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperBotoes.setOpaque(false);
        wrapperBotoes.add(pnlBotoes);
        painelDir.add(wrapperBotoes);

        add(painelDir, BorderLayout.EAST);
    }

    private JButton criarBotao(String texto, ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setBackground(COR_BOTAO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setMargin(new Insets(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(acao);
        return btn;
    }

    private void resetValues() {
        poligonoOriginal.clear();
        poligonoRecortado.clear();
        isFechado = false;
        mostrarRecortado = false;

        xMin = 150; xMax = 450; yMin = 100; yMax = 300;
        txtXMin.setText("150"); txtXMax.setText("450");
        txtYMin.setText("100"); txtYMax.setText("300");

        lblLastPoint.setText("Último ponto: (-, -)");
        lblStatus.setText("Status: Desenhando...");
        canvas.repaint();
    }

    // ==========================================
    // CANVAS
    // ==========================================
    private void setupCanvas() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COR_FUNDO);

        JPanel pnlTopo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlTopo.setOpaque(false);
        JLabel lblTopo = new JLabel("Área de Visualização", SwingConstants.CENTER);
        lblTopo.setFont(new Font("SansSerif", Font.BOLD, 14));
        pnlTopo.add(lblTopo);
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
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

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
                        poligonoOriginal.add(new Ponto(e.getX(), e.getY()));
                        repaint();
                    }
                }
            });
        }

        private void drawPolygon(Graphics g, List<Ponto> poly, boolean fill) {
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

            // Título Fixo Simulado
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2d.drawString(String.format(Locale.US, "Xmax: %.0f | Xmin: %.0f | Ymax: %.0f | Ymin: %.0f", xMax, xMin, yMax, yMin), getWidth()/2 - 140, 20);

            // Janela de Recorte (Preta, 1px)
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect((int)xMin, (int)yMin, (int)(xMax - xMin), (int)(yMax - yMin));

            // Configuração padrão de desenho do polígono (RED, 1px)
            g2d.setStroke(new BasicStroke(1));

            // --- DESENHO DO POLÍGONO ORIGINAL (Antes do recorte) ---
            if (!mostrarRecortado && !poligonoOriginal.isEmpty()) {
                g2d.setColor(RED_SOLIDO);
                for (int i = 0; i < poligonoOriginal.size() - 1; i++) {
                    Ponto p1 = poligonoOriginal.get(i);
                    Ponto p2 = poligonoOriginal.get(i + 1);
                    g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                }

                if (isFechado && poligonoOriginal.size() > 2) {
                    Ponto pFirst = poligonoOriginal.get(0);
                    Ponto pLast = poligonoOriginal.get(poligonoOriginal.size() - 1);
                    g2d.drawLine((int)pLast.x, (int)pLast.y, (int)pFirst.x, (int)pFirst.y);
                }

                // Vértices originais
                for (Ponto p : poligonoOriginal) {
                    g2d.fillOval((int)p.x - 3, (int)p.y - 3, 6, 6);
                }
            }

            // --- DESENHO DO POLÍGONO RECORTADO (AQUI ESTÁ A MUDANÇA) ---
            if (mostrarRecortado && !poligonoRecortado.isEmpty()) {

                // 1. Pinta o interior (o que está dentro do recorte)
                g2d.setColor(RED_PREENCHIMENTO); // Vermelho transparente
                drawPolygon(g2d, poligonoRecortado, true); // true = PREENCHER

                // 2. Desenha a borda sólida por cima
                g2d.setColor(RED_SOLIDO);
                drawPolygon(g2d, poligonoRecortado, false); // false = SÓ BORDA

                // 3. Desenha bolinhas nos novos vértices recortados
                for (Ponto p : poligonoRecortado) {
                    g2d.fillOval((int)p.x - 3, (int)p.y - 3, 6, 6);
                }
            }
        }
    }
}