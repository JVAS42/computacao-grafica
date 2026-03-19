package main.ui.panels;

import main.algorithms.AlgoritmoRetas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class RetasPanel extends JPanel {

    private JLabel lblCoordenadaLive, lblQuadrante;
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtX1, txtY1, txtX2, txtY2;
    private JButton btnDesenhar;

    private JLabel lblInfoReta;
    private JTextArea txtAreaHistorico;
    private JButton btnLimpar;
    private CanvasPanel canvas;
    private JPanel painelListaPontos;

    private int clickCount = 0;
    private int startX, startY;
    private List<LineDef> linhas = new ArrayList<>();

    public RetasPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F0F0F0"));

        setupPainelEsquerdo();
        setupPainelDireito();

        JPanel containerCentro = new JPanel(new GridBagLayout());
        containerCentro.setBackground(Color.decode("#F0F0F0"));

        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500));
        canvas.setMinimumSize(new Dimension(500, 500));
        canvas.setMaximumSize(new Dimension(500, 500));

        containerCentro.add(canvas);
        add(containerCentro, BorderLayout.CENTER);

        comboAlgoritmo.addActionListener(e -> limparCanvas());
    }

    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setPreferredSize(new Dimension(320, 0));
        painelEsquerdo.setBackground(Color.decode("#F0F0F0"));
        painelEsquerdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topoInfo = new JPanel();
        topoInfo.setLayout(new BoxLayout(topoInfo, BoxLayout.Y_AXIS));
        topoInfo.setOpaque(false);

        JLabel titlePlano = new JLabel("INFORMAÇÕES DO PLANO");
        titlePlano.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titlePlano.setForeground(Color.decode("#213555"));
        titlePlano.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblCoordenadaLive = new JLabel("Coordenada: (0, 0)");
        lblCoordenadaLive.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblQuadrante = new JLabel("Quadrante: Origem");
        lblQuadrante.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        topoInfo.add(titlePlano);
        topoInfo.add(Box.createVerticalStrut(5));
        topoInfo.add(new JSeparator());
        topoInfo.add(Box.createVerticalStrut(10));
        topoInfo.add(lblCoordenadaLive);
        topoInfo.add(Box.createVerticalStrut(5));
        topoInfo.add(lblQuadrante);
        topoInfo.add(Box.createVerticalStrut(25));

        JPanel painelInputs = new JPanel(new GridBagLayout());
        painelInputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblAlg = new JLabel("Algoritmo:");
        lblAlg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        painelInputs.add(lblAlg, gbc);

        gbc.gridx = 1; gbc.gridwidth = 1;
        comboAlgoritmo = new JComboBox<>(new String[]{"DDA", "Ponto Médio"});
        comboAlgoritmo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelInputs.add(comboAlgoritmo, gbc);

        txtX1 = estilizarTextField(4);
        txtY1 = estilizarTextField(4);
        txtX2 = estilizarTextField(4);
        txtY2 = estilizarTextField(4);

        addInputRow(painelInputs, "X Inicial:", txtX1, 1);
        addInputRow(painelInputs, "Y Inicial:", txtY1, 2);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painelInputs.add(Box.createVerticalStrut(15), gbc);

        addInputRow(painelInputs, "X Final:", txtX2, 4);
        addInputRow(painelInputs, "Y Final:", txtY2, 5);

        JPanel painelBotao = new JPanel(new BorderLayout());
        painelBotao.setOpaque(false);
        painelBotao.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnDesenhar = estilizarBotao("DESENHAR");
        btnDesenhar.addActionListener(e -> desenharViaInputs());
        painelBotao.add(btnDesenhar, BorderLayout.NORTH);

        JPanel containerNorte = new JPanel(new BorderLayout());
        containerNorte.setOpaque(false);
        containerNorte.add(topoInfo, BorderLayout.NORTH);
        containerNorte.add(painelInputs, BorderLayout.CENTER);

        painelEsquerdo.add(containerNorte, BorderLayout.NORTH);
        painelEsquerdo.add(painelBotao, BorderLayout.SOUTH);

        add(painelEsquerdo, BorderLayout.WEST);
    }

    private void addInputRow(JPanel panel, String labelStr, JTextField field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.DARK_GRAY);
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setPreferredSize(new Dimension(320, 0));
        painelDireito.setBackground(Color.decode("#F0F0F0"));
        painelDireito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("INFORMAÇÕES DA RETA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.decode("#213555"));

        lblInfoReta = new JLabel("Nenhuma reta desenhada");
        lblInfoReta.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfoReta.setForeground(Color.GRAY);
        lblInfoReta.setBorder(new EmptyBorder(8, 0, 8, 0));

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(new JSeparator());
        headerPanel.add(lblInfoReta);
        headerPanel.add(Box.createVerticalStrut(10));

        painelDireito.add(headerPanel, BorderLayout.NORTH);

        JPanel listaPontosContainer = new JPanel();
        listaPontosContainer.setLayout(new BoxLayout(listaPontosContainer, BoxLayout.Y_AXIS));
        listaPontosContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listaPontosContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.txtAreaHistorico = new JTextArea();
        this.painelListaPontos = listaPontosContainer;

        painelDireito.add(scrollPane, BorderLayout.CENTER);

        btnLimpar = estilizarBotao("LIMPAR TELA");
        btnLimpar.addActionListener(e -> limparCanvas());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        bottomPanel.add(btnLimpar, BorderLayout.CENTER);

        painelDireito.add(bottomPanel, BorderLayout.SOUTH);
        add(painelDireito, BorderLayout.EAST);
    }

    private JTextField estilizarTextField(int columns) {
        JTextField txt = new JTextField(columns);
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

    private void limparCanvas() {
        linhas.clear();
        clickCount = 0;
        txtX1.setText(""); txtY1.setText("");
        txtX2.setText(""); txtY2.setText("");
        lblInfoReta.setText("Nenhuma reta desenhada");
        painelListaPontos.removeAll();
        painelListaPontos.revalidate();
        painelListaPontos.repaint();
        canvas.repaint();
    }

    private void desenharViaInputs() {
        try {
            int x1 = Integer.parseInt(txtX1.getText());
            int y1 = Integer.parseInt(txtY1.getText());
            int x2 = Integer.parseInt(txtX2.getText());
            int y2 = Integer.parseInt(txtY2.getText());

            linhas.add(new LineDef(x1, y1, x2, y2));
            atualizarHistorico(x1, y1, x2, y2);
            canvas.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha todas as coordenadas com números inteiros válidos.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Calcula os pontos da reta usando DDA ou Ponto Médio e atualiza a UI
    private void atualizarHistorico(int x1, int y1, int x2, int y2) {
        lblInfoReta.setText(String.format("Início: (%d, %d)  |  Fim: (%d, %d)", x1, y1, x2, y2));

        String alg = (String) comboAlgoritmo.getSelectedItem();
        List<Point> pontosGerados = "DDA".equals(alg) ?
                AlgoritmoRetas.dda(x1, y1, x2, y2) :
                AlgoritmoRetas.pontoMedio(x1, y1, x2, y2);

        painelListaPontos.removeAll();

        for (int i = 0; i < pontosGerados.size(); i++) {
            Point p = pontosGerados.get(i);

            JPanel item = new JPanel(new GridLayout(1, 2));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            item.setBackground(Color.WHITE);
            item.setBorder(new EmptyBorder(5, 10, 5, 10));

            JLabel lblX = new JLabel("X" + (i + 1) + ": " + p.x);
            JLabel lblY = new JLabel("Y" + (i + 1) + ": " + p.y);
            lblX.setFont(new Font("Monospaced", Font.BOLD, 12));
            lblY.setFont(new Font("Monospaced", Font.BOLD, 12));
            lblX.setForeground(Color.DARK_GRAY);
            lblY.setForeground(Color.DARK_GRAY);

            item.add(lblX);
            item.add(lblY);

            painelListaPontos.add(item);

            JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
            sep.setForeground(new Color(240, 240, 240));
            painelListaPontos.add(sep);
        }

        painelListaPontos.revalidate();
        painelListaPontos.repaint();
    }

    private String getQuadrante(int x, int y) {
        if (x > 0 && y > 0) return "1";
        if (x < 0 && y > 0) return "2";
        if (x < 0 && y < 0) return "3";
        if (x > 0 && y < 0) return "4";
        if (x == 0 && y != 0) return "Eixo Y";
        if (y == 0 && x != 0) return "Eixo X";
        return "Origem";
    }

    private class LineDef {
        int x1, y1, x2, y2;
        public LineDef(int x1, int y1, int x2, int y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    private class CanvasPanel extends JPanel {

        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int cartX = screenToCartesianX(e.getX());
                    int cartY = screenToCartesianY(e.getY());
                    lblCoordenadaLive.setText(String.format("Coordenada: (%d, %d)", cartX, cartY));
                    lblQuadrante.setText("Quadrante: " + getQuadrante(cartX, cartY));
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cartX = screenToCartesianX(e.getX());
                    int cartY = screenToCartesianY(e.getY());

                    if (clickCount == 0) {
                        startX = cartX;
                        startY = cartY;
                        txtX1.setText(String.valueOf(startX));
                        txtY1.setText(String.valueOf(startY));
                        clickCount = 1;
                    } else {
                        txtX2.setText(String.valueOf(cartX));
                        txtY2.setText(String.valueOf(cartY));
                        linhas.add(new LineDef(startX, startY, cartX, cartY));
                        atualizarHistorico(startX, startY, cartX, cartY);
                        clickCount = 0;
                        repaint();
                    }
                }
            });
        }

        private int screenToCartesianX(int screenX) { return screenX - getWidth() / 2; }
        private int screenToCartesianY(int screenY) { return getHeight() / 2 - screenY; }
        private int cartesianToScreenX(int cartX) { return cartX + getWidth() / 2; }
        private int cartesianToScreenY(int cartY) { return getHeight() / 2 - cartY; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int w = getWidth();
            int h = getHeight();

            g.setColor(new Color(220, 220, 220));
            g.drawLine(w / 2, 0, w / 2, h);
            g.drawLine(0, h / 2, w, h / 2);

            g.setColor(Color.RED);
            String alg = (String) comboAlgoritmo.getSelectedItem();

            for (LineDef linha : linhas) {
                List<Point> pontos = "DDA".equals(alg) ?
                        AlgoritmoRetas.dda(linha.x1, linha.y1, linha.x2, linha.y2) :
                        AlgoritmoRetas.pontoMedio(linha.x1, linha.y1, linha.x2, linha.y2);

                for (Point p : pontos) {
                    int screenX = cartesianToScreenX(p.x);
                    int screenY = cartesianToScreenY(p.y);
                    g.fillRect(screenX, screenY, 1, 1);
                }
            }
        }
    }
}