package main.ui.panels;

import main.algorithms.ConversorCoordenadas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class CoordenadasPanel extends JPanel {

    // Limites do Mundo (Padrão inicial baseado no seu JS)
    private double xMax = 100.3, xMin = 10.5, yMax = 100.4, yMin = 15.2;

    // Elementos da Interface Topo
    private JLabel lblLimitesAtuais;

    // Elementos da Interface Esquerda (Live)
    private JLabel lblLiveMundo, lblLiveNDC, lblLiveNDCCentral, lblLiveDisp;
    private JTextField txtSetXMax, txtSetXMin, txtSetYMax, txtSetYMin;

    // Elementos da Interface Direita (Click)
    private JLabel lblClickMundo, lblClickNDC, lblClickNDCCentral, lblClickDisp;
    private JTextField txtAtivarX, txtAtivarY;

    // Canvas e estado
    private CanvasPanel canvas;
    private Point pixelAtivado = null; // Armazena a coordenada do pixel clicado (no sistema do dispositivo)

    public CoordenadasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(225, 225, 225)); // Fundo cinza claro igual à imagem

        setupPainelTopo();
        setupPainelEsquerdo();
        setupPainelDireito();
        setupCanvas();
    }

    private void setupPainelTopo() {
        lblLimitesAtuais = new JLabel(getLimitesText(), SwingConstants.CENTER);
        lblLimitesAtuais.setFont(new Font("Arial", Font.BOLD, 12));
        lblLimitesAtuais.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(lblLimitesAtuais, BorderLayout.NORTH);
    }

    private void setupPainelEsquerdo() {
        JPanel painelEsq = new JPanel(new BorderLayout());
        painelEsq.setPreferredSize(new Dimension(250, 0));
        painelEsq.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

        // Título e Textos (Cima)
        JPanel pnlTextos = new JPanel();
        pnlTextos.setLayout(new BoxLayout(pnlTextos, BoxLayout.Y_AXIS));
        pnlTextos.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel tituloEsq = new JLabel("Coordenadas em Tempo Real");
        tituloEsq.setFont(new Font("Arial", Font.BOLD, 14));
        pnlTextos.add(tituloEsq);
        pnlTextos.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlTextos.add(Box.createVerticalStrut(10));

        lblLiveMundo = criarLabelCoordenada("Coordenadas de Mundo:");
        lblLiveNDC = criarLabelCoordenada("Coordenadas NDC:");
        lblLiveNDCCentral = criarLabelCoordenada("Coordenadas NDC Centralizada:");
        lblLiveDisp = criarLabelCoordenada("Coordenadas de Dispositivo:");

        pnlTextos.add(lblLiveMundo); pnlTextos.add(Box.createVerticalStrut(10));
        pnlTextos.add(lblLiveNDC); pnlTextos.add(Box.createVerticalStrut(10));
        pnlTextos.add(lblLiveNDCCentral); pnlTextos.add(Box.createVerticalStrut(10));
        pnlTextos.add(lblLiveDisp);

        painelEsq.add(pnlTextos, BorderLayout.NORTH);

        // Inputs e Botão (Baixo)
        JPanel pnlControles = new JPanel(new GridBagLayout());
        pnlControles.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5);

        txtSetXMax = new JTextField(String.valueOf(xMax));
        txtSetXMin = new JTextField(String.valueOf(xMin));
        txtSetYMax = new JTextField(String.valueOf(yMax));
        txtSetYMin = new JTextField(String.valueOf(yMin));

        adicionarCampoGrid(pnlControles, "Xmax:", txtSetXMax, gbc, 0);
        adicionarCampoGrid(pnlControles, "Xmin:", txtSetXMin, gbc, 1);
        adicionarCampoGrid(pnlControles, "Ymax:", txtSetYMax, gbc, 2);
        adicionarCampoGrid(pnlControles, "Ymin:", txtSetYMin, gbc, 3);

        JButton btnDefinir = new JButton("Definir Valores");
        btnDefinir.setBackground(new Color(92, 184, 92)); // Verde
        btnDefinir.setForeground(Color.WHITE);
        btnDefinir.addActionListener(e -> definirValoresMundo());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        pnlControles.add(btnDefinir, gbc);

        painelEsq.add(pnlControles, BorderLayout.SOUTH);
        add(painelEsq, BorderLayout.WEST);
    }

    private void setupPainelDireito() {
        JPanel painelDir = new JPanel(new BorderLayout());
        painelDir.setPreferredSize(new Dimension(250, 0));
        painelDir.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));

        // Título e Textos (Cima)
        JPanel pnlTextos = new JPanel();
        pnlTextos.setLayout(new BoxLayout(pnlTextos, BoxLayout.Y_AXIS));
        pnlTextos.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel tituloDir = new JLabel("Coordenadas do Pixel Ativado");
        tituloDir.setFont(new Font("Arial", Font.BOLD, 14));
        pnlTextos.add(tituloDir);
        pnlTextos.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlTextos.add(Box.createVerticalStrut(10));

        lblClickMundo = criarLabelCoordenada("Coordenadas de Mundo:");
        lblClickNDC = criarLabelCoordenada("Coordenadas NDC:");
        lblClickNDCCentral = criarLabelCoordenada("Coordenadas NDC Centralizada:");
        lblClickDisp = criarLabelCoordenada("Coordenadas de Dispositivo:");

        pnlTextos.add(lblClickMundo); pnlTextos.add(Box.createVerticalStrut(10));
        pnlTextos.add(lblClickNDC); pnlTextos.add(Box.createVerticalStrut(10));
        pnlTextos.add(lblClickNDCCentral); pnlTextos.add(Box.createVerticalStrut(10));
        pnlTextos.add(lblClickDisp);

        painelDir.add(pnlTextos, BorderLayout.NORTH);

        // Inputs e Botão (Baixo)
        JPanel pnlControles = new JPanel(new GridBagLayout());
        pnlControles.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5);

        txtAtivarX = new JTextField();
        txtAtivarY = new JTextField();

        adicionarCampoGrid(pnlControles, "X:", txtAtivarX, gbc, 0);
        adicionarCampoGrid(pnlControles, "Y:", txtAtivarY, gbc, 1);

        JButton btnAtivar = new JButton("Ativar Pixel");
        btnAtivar.setBackground(new Color(92, 184, 92)); // Verde
        btnAtivar.setForeground(Color.WHITE);
        btnAtivar.addActionListener(e -> ativarPixelViaInputs());

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        pnlControles.add(btnAtivar, gbc);

        painelDir.add(pnlControles, BorderLayout.SOUTH);
        add(painelDir, BorderLayout.EAST);
    }

    private void setupCanvas() {
        // Envolve o canvas com um painel para dar padding e centralizar
        JPanel wrapperCanvas = new JPanel(new GridBagLayout());
        wrapperCanvas.setOpaque(false);

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(601, 401)); // Tamanho aproximado da imagem

        wrapperCanvas.add(canvas);
        add(wrapperCanvas, BorderLayout.CENTER);
    }

    // ==========================================
    // Lógica de Atualização e Botões
    // ==========================================
    private void definirValoresMundo() {
        try {
            xMax = Double.parseDouble(txtSetXMax.getText());
            xMin = Double.parseDouble(txtSetXMin.getText());
            yMax = Double.parseDouble(txtSetYMax.getText());
            yMin = Double.parseDouble(txtSetYMin.getText());
            lblLimitesAtuais.setText(getLimitesText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores inválidos para limites do mundo.");
        }
    }

    private void ativarPixelViaInputs() {
        try {
            double inputX = Double.parseDouble(txtAtivarX.getText());
            double inputY = Double.parseDouble(txtAtivarY.getText());

            if (inputX < xMin || inputX > xMax || inputY < yMin || inputY > yMax) {
                JOptionPane.showMessageDialog(this, "Coordenadas fora do intervalo permitido do Mundo.");
                return;
            }

            // Conversão inversa (Mundo -> NDC -> Dispositivo) igual ao seu JS
            double ndcx = (inputX - xMin) / (xMax - xMin);
            double ndcy = (inputY - yMin) / (yMax - yMin);

            int pixelX = (int) Math.round(ndcx * (canvas.getWidth() - 1));
            int pixelY = (int) Math.round(ndcy * (canvas.getHeight() - 1));

            pixelAtivado = new Point(pixelX, pixelY);

            // Atualiza o painel de clique simulando um clique real
            atualizarLabels(lblClickMundo, lblClickNDC, lblClickNDCCentral, lblClickDisp, pixelX, pixelY);
            canvas.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Insira coordenadas válidas para ativar o pixel.");
        }
    }

    private void atualizarLabels(JLabel lblMundo, JLabel lblNDC, JLabel lblNDCCent, JLabel lblDisp, int pixelX, int pixelY) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        double[] ndc = ConversorCoordenadas.inpToNdc(pixelX, pixelY, w, h);
        double[] world = ConversorCoordenadas.ndcToWd(ndc[0], ndc[1], xMax, xMin, yMax, yMin);
        double[] ndcCentral = ConversorCoordenadas.wdToNdcCentral(world[0], world[1], xMax, xMin, yMax, yMin);

        // Formatação Locale.US para usar Ponto ao invés de Vírgula, como no JS
        lblMundo.setText(formatLabel("Coordenadas de Mundo:", world[0], world[1], 3));
        lblNDC.setText(formatLabel("Coordenadas NDC:", ndc[0], ndc[1], 3));
        lblNDCCent.setText(formatLabel("Coordenadas NDC Centralizada:", ndcCentral[0], ndcCentral[1], 3));
        lblDisp.setText(formatLabel("Coordenadas de Dispositivo:", pixelX, pixelY, 0));
    }

    // ==========================================
    // Funções Utilitárias de UI
    // ==========================================
    private String getLimitesText() {
        return String.format(Locale.US, "Xmax: %.1f | Xmin: %.1f | Ymax: %.1f | Ymin: %.1f", xMax, xMin, yMax, yMin);
    }

    private JLabel criarLabelCoordenada(String titulo) {
        JLabel lbl = new JLabel("<html><strong>" + titulo + "</strong><br>(-, -)</html>");
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        return lbl;
    }

    private String formatLabel(String titulo, double v1, double v2, int casasDecimais) {
        String format = "<html><strong>" + titulo + "</strong><br>(%." + casasDecimais + "f, %." + casasDecimais + "f)</html>";
        return String.format(Locale.US, format, v1, v2);
    }

    private void adicionarCampoGrid(JPanel pnl, String labelText, JTextField txt, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        pnl.add(new JLabel(labelText, SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        pnl.add(txt, gbc);
    }

    // ==========================================
    // ÁREA DE DESENHO
    // ==========================================
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Borda preta fina

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    // No JS: y = Math.round(canvas.height - event.clientY)
                    // No Java Swing, o Y=0 é no topo, então invertemos.
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
                    repaint(); // Força o redesenho do canvas
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (pixelAtivado != null) {
                g.setColor(Color.BLACK);
                // Inverte o Y novamente na hora de desenhar no Swing
                int drawY = getHeight() - 1 - pixelAtivado.y;
                // Desenha o "pixel" (retângulo 2x2 para ser visível)
                g.fillRect(pixelAtivado.x, drawY, 2, 2);
            }
        }
    }
}
