package main.ui.panels;

import main.algorithms.ConversorCoordenadas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class CoordenadasPanel extends JPanel {

    private double xMax = 100.3, xMin = 10.5, yMax = 100.4, yMin = 15.2;

    private JLabel lblLimitesAtuais;
    private JLabel lblLiveMundo, lblLiveNDC, lblLiveNDCCentral, lblLiveDisp;
    private JTextField txtSetXMax, txtSetXMin, txtSetYMax, txtSetYMin;
    private JLabel lblClickMundo, lblClickNDC, lblClickNDCCentral, lblClickDisp;
    private JTextField txtAtivarX, txtAtivarY;

    private CanvasPanel canvas;
    private Point pixelAtivado = null;

    public CoordenadasPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F0F0F0"));

        setupPainelTopo();
        setupPainelEsquerdo();
        setupPainelDireito();
        setupCanvas();
    }

    private void setupPainelTopo() {
        lblLimitesAtuais = new JLabel(getLimitesText(), SwingConstants.CENTER);
        lblLimitesAtuais.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLimitesAtuais.setForeground(Color.DARK_GRAY);
        lblLimitesAtuais.setBorder(new EmptyBorder(15, 0, 5, 0));
        add(lblLimitesAtuais, BorderLayout.NORTH);
    }

    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel(new BorderLayout());
        painelEsq.setPreferredSize(new Dimension(320, 0));
        painelEsq.setBackground(Color.decode("#F0F0F0"));
        painelEsq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel pnlTextos = new JPanel();
        pnlTextos.setLayout(new BoxLayout(pnlTextos, BoxLayout.Y_AXIS));
        pnlTextos.setOpaque(false);

        JLabel tituloEsq = new JLabel("Coordenadas em Tempo Real");
        tituloEsq.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloEsq.setForeground(Color.decode("#213555"));

        pnlTextos.add(tituloEsq);
        pnlTextos.add(Box.createVerticalStrut(5));
        pnlTextos.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlTextos.add(Box.createVerticalStrut(15));

        lblLiveMundo = criarLabelCoordenada("Coordenadas de Mundo:");
        lblLiveNDC = criarLabelCoordenada("Coordenadas NDC:");
        lblLiveNDCCentral = criarLabelCoordenada("Coordenadas NDC Centralizada:");
        lblLiveDisp = criarLabelCoordenada("Coordenadas de Dispositivo:");

        pnlTextos.add(lblLiveMundo); pnlTextos.add(Box.createVerticalStrut(12));
        pnlTextos.add(lblLiveNDC); pnlTextos.add(Box.createVerticalStrut(12));
        pnlTextos.add(lblLiveNDCCentral); pnlTextos.add(Box.createVerticalStrut(12));
        pnlTextos.add(lblLiveDisp);

        painelEsq.add(pnlTextos, BorderLayout.NORTH);

        JPanel pnlControles = new JPanel(new GridBagLayout());
        pnlControles.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtSetXMax = estilizarTextField(String.valueOf(xMax));
        txtSetXMin = estilizarTextField(String.valueOf(xMin));
        txtSetYMax = estilizarTextField(String.valueOf(yMax));
        txtSetYMin = estilizarTextField(String.valueOf(yMin));

        adicionarCampoGrid(pnlControles, "Xmax:", txtSetXMax, gbc, 0);
        adicionarCampoGrid(pnlControles, "Xmin:", txtSetXMin, gbc, 1);
        adicionarCampoGrid(pnlControles, "Ymax:", txtSetYMax, gbc, 2);
        adicionarCampoGrid(pnlControles, "Ymin:", txtSetYMin, gbc, 3);

        JButton btnDefinir = estilizarBotao("Definir Valores");
        btnDefinir.addActionListener(e -> definirValoresMundo());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        pnlControles.add(btnDefinir, gbc);

        painelEsq.add(pnlControles, BorderLayout.SOUTH);
        add(painelEsq, BorderLayout.WEST);
    }

    private void setupPainelDireito() {
        JPanel painelDir = new JPanel(new BorderLayout());
        painelDir.setPreferredSize(new Dimension(320, 0));
        painelDir.setBackground(Color.decode("#F0F0F0"));
        painelDir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel pnlTextos = new JPanel();
        pnlTextos.setLayout(new BoxLayout(pnlTextos, BoxLayout.Y_AXIS));
        pnlTextos.setOpaque(false);

        JLabel tituloDir = new JLabel("Coordenadas do Pixel Ativado");
        tituloDir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloDir.setForeground(Color.decode("#213555"));

        pnlTextos.add(tituloDir);
        pnlTextos.add(Box.createVerticalStrut(5));
        pnlTextos.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlTextos.add(Box.createVerticalStrut(15));

        lblClickMundo = criarLabelCoordenada("Coordenadas de Mundo:");
        lblClickNDC = criarLabelCoordenada("Coordenadas NDC:");
        lblClickNDCCentral = criarLabelCoordenada("Coordenadas NDC Centralizada:");
        lblClickDisp = criarLabelCoordenada("Coordenadas de Dispositivo:");

        pnlTextos.add(lblClickMundo); pnlTextos.add(Box.createVerticalStrut(12));
        pnlTextos.add(lblClickNDC); pnlTextos.add(Box.createVerticalStrut(12));
        pnlTextos.add(lblClickNDCCentral); pnlTextos.add(Box.createVerticalStrut(12));
        pnlTextos.add(lblClickDisp);

        painelDir.add(pnlTextos, BorderLayout.NORTH);

        JPanel pnlControles = new JPanel(new GridBagLayout());
        pnlControles.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtAtivarX = estilizarTextField("");
        txtAtivarY = estilizarTextField("");

        adicionarCampoGrid(pnlControles, "X:", txtAtivarX, gbc, 0);
        adicionarCampoGrid(pnlControles, "Y:", txtAtivarY, gbc, 1);

        JButton btnAtivar = estilizarBotao("Ativar Pixel");
        btnAtivar.addActionListener(e -> ativarPixelViaInputs());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        pnlControles.add(btnAtivar, gbc);

        painelDir.add(pnlControles, BorderLayout.SOUTH);
        add(painelDir, BorderLayout.EAST);
    }

    private void setupCanvas() {
        JPanel wrapperCanvas = new JPanel(new GridBagLayout());
        wrapperCanvas.setOpaque(false);

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(601, 401));

        wrapperCanvas.add(canvas);
        add(wrapperCanvas, BorderLayout.CENTER);
    }

    // Gerencia o reajuste das dimensões de exibição baseadas na interface
    private void definirValoresMundo() {
        try {
            xMax = Double.parseDouble(txtSetXMax.getText());
            xMin = Double.parseDouble(txtSetXMin.getText());
            yMax = Double.parseDouble(txtSetYMax.getText());
            yMin = Double.parseDouble(txtSetYMin.getText());
            lblLimitesAtuais.setText(getLimitesText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores inválidos para limites do mundo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ativarPixelViaInputs() {
        try {
            double inputX = Double.parseDouble(txtAtivarX.getText());
            double inputY = Double.parseDouble(txtAtivarY.getText());

            if (inputX < xMin || inputX > xMax || inputY < yMin || inputY > yMax) {
                JOptionPane.showMessageDialog(this, "Coordenadas fora do intervalo permitido do Mundo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double ndcx = (inputX - xMin) / (xMax - xMin);
            double ndcy = (inputY - yMin) / (yMax - yMin);

            int pixelX = (int) Math.round(ndcx * (canvas.getWidth() - 1));
            int pixelY = (int) Math.round(ndcy * (canvas.getHeight() - 1));

            pixelAtivado = new Point(pixelX, pixelY);

            atualizarLabels(lblClickMundo, lblClickNDC, lblClickNDCCentral, lblClickDisp, pixelX, pixelY);
            canvas.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Insira coordenadas válidas para ativar o pixel.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Converte os pontos físicos de tela para as medidas relativas e matemáticas requisitadas
    private void atualizarLabels(JLabel lblMundo, JLabel lblNDC, JLabel lblNDCCent, JLabel lblDisp, int pixelX, int pixelY) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        double[] ndc = ConversorCoordenadas.inpToNdc(pixelX, pixelY, w, h);
        double[] world = ConversorCoordenadas.ndcToWd(ndc[0], ndc[1], xMax, xMin, yMax, yMin);
        double[] ndcCentral = ConversorCoordenadas.wdToNdcCentral(world[0], world[1], xMax, xMin, yMax, yMin);

        lblMundo.setText(formatLabel("Coordenadas de Mundo:", world[0], world[1], 3));
        lblNDC.setText(formatLabel("Coordenadas NDC:", ndc[0], ndc[1], 3));
        lblNDCCent.setText(formatLabel("Coordenadas NDC Centralizada:", ndcCentral[0], ndcCentral[1], 3));
        lblDisp.setText(formatLabel("Coordenadas de Dispositivo:", pixelX, pixelY, 0));
    }

    private String getLimitesText() {
        return String.format(Locale.US, "Xmax: %.1f   |   Xmin: %.1f   |   Ymax: %.1f   |   Ymin: %.1f", xMax, xMin, yMax, yMin);
    }

    private JLabel criarLabelCoordenada(String titulo) {
        JLabel lbl = new JLabel("<html><span style='color: #555555;'><strong>" + titulo + "</strong></span><br><span style='font-size: 11px;'>(-, -)</span></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private String formatLabel(String titulo, double v1, double v2, int casasDecimais) {
        String format = "<html><span style='color: #555555;'><strong>" + titulo + "</strong></span><br><span style='font-size: 11px;'>(%." + casasDecimais + "f, %." + casasDecimais + "f)</span></html>";
        return String.format(Locale.US, format, v1, v2);
    }

    private JTextField estilizarTextField(String textoInicial) {
        JTextField txt = new JTextField(textoInicial);
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
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }

    private void adicionarCampoGrid(JPanel pnl, String labelText, JTextField txt, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText, SwingConstants.RIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.DARK_GRAY);
        pnl.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        pnl.add(txt, gbc);
    }

    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int dispX = e.getX();
                    int dispY = getHeight() - 1 - e.getY();
                    atualizarLabels(lblLiveMundo, lblLiveNDC, lblLiveNDCCentral, lblLiveDisp, dispX, dispY);
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int dispX = e.getX();
                    int dispY = getHeight() - 1 - e.getY();
                    pixelAtivado = new Point(dispX, dispY);

                    atualizarLabels(lblClickMundo, lblClickNDC, lblClickNDCCentral, lblClickDisp, dispX, dispY);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (pixelAtivado != null) {
                g.setColor(Color.RED);
                int drawY = getHeight() - 1 - pixelAtivado.y;
                g.fillRect(pixelAtivado.x, drawY, 1, 1);
            }
        }
    }
}