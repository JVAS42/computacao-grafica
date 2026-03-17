package main.ui.panels;

import main.algorithms.AlgoritmoRetas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class RetasPanel extends JPanel {

    // Componentes da Interface (Painel Esquerdo)
    private JLabel lblCoordenadaLive, lblQuadrante;
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtX1, txtY1, txtX2, txtY2;
    private JButton btnDesenhar;

    // Componentes da Interface (Painel Direito)
    private JLabel lblInfoReta;
    private JTextArea txtAreaHistorico;
    private JButton btnLimpar;

    // Área de desenho customizada
    private CanvasPanel canvas;

    // Variáveis de controle de estado
    private int clickCount = 0;
    private int startX, startY;
    private List<LineDef> linhas = new ArrayList<>();

    private JPanel painelListaPontos;

    public RetasPanel() {
        setLayout(new BorderLayout());

        // Inicializa as áreas principais
        setupPainelEsquerdo();
        setupPainelDireito();

        // 1. Criamos um painel para ser o fundo cinza
        JPanel containerCentro = new JPanel(new GridBagLayout()); 
        containerCentro.setBackground(new Color(211, 211, 211)); // Cor cinza claro

        // 2. Configuramos o canvas para 500x500
        canvas = new CanvasPanel();
        canvas.setPreferredSize(new Dimension(500, 500));
        canvas.setMinimumSize(new Dimension(500, 500));
        canvas.setMaximumSize(new Dimension(500, 500));

        // 3. Colocamos o canvas dentro do fundo cinza (o GridBagLayout centraliza ele)
        containerCentro.add(canvas);

        // 4. Adicionamos o conjunto todo ao centro da tela
        add(containerCentro, BorderLayout.CENTER);
        comboAlgoritmo.addActionListener(e -> limparCanvas());
    }

    // ===============================
    // Configuração do Layout Esquerdo
    // ===============================
    private void setupPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setBorder(new EmptyBorder(15, 15, 15, 15));
        painelEsquerdo.setPreferredSize(new Dimension(240, 0));

        // --- TOPO: Informações do Plano ---
        JPanel topoInfo = new JPanel();
        topoInfo.setLayout(new BoxLayout(topoInfo, BoxLayout.Y_AXIS));
        
        JLabel titlePlano = new JLabel("INFORMAÇÕES DO PLANO");
        titlePlano.setFont(new Font("SansSerif", Font.BOLD, 12));
        titlePlano.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblCoordenadaLive = new JLabel("Coordenada: (0, 0)");
        lblQuadrante = new JLabel("Quadrante: Origem");
        
        topoInfo.add(titlePlano);
        topoInfo.add(new JSeparator());
        topoInfo.add(Box.createVerticalStrut(8));
        topoInfo.add(lblCoordenadaLive);
        topoInfo.add(lblQuadrante);
        topoInfo.add(Box.createVerticalStrut(20));

        // --- MEIO: Controles de Entrada (Grid) ---
        JPanel painelInputs = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 10); // Espaçamento entre elementos

        // Algoritmo Selector
        gbc.gridx = 0; gbc.gridy = 0;
        painelInputs.add(new JLabel("Algoritmo:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 1;
        comboAlgoritmo = new JComboBox<>(new String[]{"DDA", "Ponto Médio"});
        painelInputs.add(comboAlgoritmo, gbc);
        

        // X e Y Inicial
        addInputRow(painelInputs, "X Inicial:", txtX1 = new JTextField(4), 1);
        addInputRow(painelInputs, "Y Inicial:", txtY1 = new JTextField(4), 2);
        
        // Separador visual entre Início e Fim
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painelInputs.add(Box.createVerticalStrut(10), gbc);

        // X e Y Final
        addInputRow(painelInputs, "X Final:", txtX2 = new JTextField(4), 4);
        addInputRow(painelInputs, "Y Final:", txtY2 = new JTextField(4), 5);

        // --- BAIXO: Botão Desenhar ---
        JPanel painelBotao = new JPanel(new BorderLayout());
        btnDesenhar = new JButton("DESENHAR");
        btnDesenhar.setPreferredSize(new Dimension(0, 40));
        btnDesenhar.setBackground(new Color(40, 167, 69)); // Verde mais moderno
        btnDesenhar.setForeground(Color.WHITE);
        btnDesenhar.setFocusPainted(false);
        btnDesenhar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnDesenhar.addActionListener(e -> desenharViaInputs());
        painelBotao.add(btnDesenhar, BorderLayout.NORTH);

        // Montagem Final
        JPanel containerNorte = new JPanel(new BorderLayout());
        containerNorte.add(topoInfo, BorderLayout.NORTH);
        containerNorte.add(painelInputs, BorderLayout.CENTER);

        painelEsquerdo.add(containerNorte, BorderLayout.NORTH);
        painelEsquerdo.add(painelBotao, BorderLayout.SOUTH);

        add(painelEsquerdo, BorderLayout.WEST);
    }

    // Método auxiliar para criar as linhas de input de forma padronizada
    private void addInputRow(JPanel panel, String label, JTextField field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 0, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    // ===============================
    // Configuração do Layout Direito
    // ===============================
    private void setupPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBorder(new EmptyBorder(15, 15, 15, 15));
        painelDireito.setPreferredSize(new Dimension(280, 0));

        // --- CABEÇALHO ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("INFORMAÇÕES DA RETA");
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        lblInfoReta = new JLabel("Nenhuma reta desenhada");
        lblInfoReta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInfoReta.setForeground(Color.GRAY);
        lblInfoReta.setBorder(new EmptyBorder(5, 0, 5, 0));

        headerPanel.add(title);
        headerPanel.add(new JSeparator());
        headerPanel.add(lblInfoReta);
        headerPanel.add(Box.createVerticalStrut(10));

        painelDireito.add(headerPanel, BorderLayout.NORTH);

        // --- ÁREA DE PONTOS (HISTÓRICO) ---
        // Usamos um painel com BoxLayout para empilhar os pontos
        JPanel listaPontosContainer = new JPanel();
        listaPontosContainer.setLayout(new BoxLayout(listaPontosContainer, BoxLayout.Y_AXIS));
        listaPontosContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listaPontosContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll mais suave
        
        // Referência para podermos limpar depois
        this.txtAreaHistorico = new JTextArea(); // Mantemos a variável para não quebrar a lógica
        // Mas agora vamos usar um container para os "labels" bonitos
        this.painelListaPontos = listaPontosContainer; 

        painelDireito.add(scrollPane, BorderLayout.CENTER);

        // --- RODAPÉ (BOTÃO LIMPAR) ---
        btnLimpar = new JButton("LIMPAR TELA");
        btnLimpar.setPreferredSize(new Dimension(0, 35));
        btnLimpar.setBackground(new Color(40, 167, 69));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLimpar.addActionListener(e -> limparCanvas());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomPanel.add(btnLimpar, BorderLayout.CENTER);
        
        painelDireito.add(bottomPanel, BorderLayout.SOUTH);

        add(painelDireito, BorderLayout.EAST);
    }

    // ===============================
    // Lógica de Interação
    // ===============================
    private void limparCanvas() {
        linhas.clear();
        clickCount = 0;
        txtX1.setText(""); txtY1.setText("");
        txtX2.setText(""); txtY2.setText("");
        lblInfoReta.setText("Nenhuma reta desenhada");
        painelListaPontos.removeAll(); // Limpa a nova lista visual
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
            JOptionPane.showMessageDialog(this, "Preencha todas as coordenadas com números inteiros válidos.");
        }
    }

    private void atualizarHistorico(int x1, int y1, int x2, int y2) {
        // Atualiza o resumo no topo
        lblInfoReta.setText(String.format("Início: (%d, %d)  |  Fim: (%d, %d)", x1, y1, x2, y2));

        String alg = (String) comboAlgoritmo.getSelectedItem();
        List<Point> pontosGerados = "DDA".equals(alg) ? 
                AlgoritmoRetas.dda(x1, y1, x2, y2) : 
                AlgoritmoRetas.pontoMedio(x1, y1, x2, y2);

        painelListaPontos.removeAll(); // Limpa a lista visual antiga

        for (int i = 0; i < pontosGerados.size(); i++) {
            Point p = pontosGerados.get(i);
            
            // Cria um painel para cada linha de coordenada
            JPanel item = new JPanel(new GridLayout(1, 2));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            item.setBackground(Color.WHITE);
            item.setBorder(new EmptyBorder(5, 10, 5, 10));

            JLabel lblX = new JLabel("X" + (i + 1) + ": " + p.x);
            JLabel lblY = new JLabel("Y" + (i + 1) + ": " + p.y);
            lblX.setFont(new Font("Monospaced", Font.BOLD, 12));
            lblY.setFont(new Font("Monospaced", Font.BOLD, 12));

            item.add(lblX);
            item.add(lblY);

            painelListaPontos.add(item);
            painelListaPontos.add(new JSeparator(JSeparator.HORIZONTAL));
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

    // Classe auxiliar para armazenar as retas na memória
    private class LineDef {
        int x1, y1, x2, y2;
        public LineDef(int x1, int y1, int x2, int y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    // ===============================
    // Área de Desenho Customizada (Substitui o Canvas do HTML)
    // ===============================
    private class CanvasPanel extends JPanel {

        public CanvasPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // Rastreador de Mouse (Hover)
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int cartX = screenToCartesianX(e.getX());
                    int cartY = screenToCartesianY(e.getY());
                    lblCoordenadaLive.setText(String.format("Coordenada: (%d, %d)", cartX, cartY));
                    lblQuadrante.setText("Quadrante: " + getQuadrante(cartX, cartY));
                }
            });

            // Rastreador de Cliques
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
                        repaint(); // Aciona a renderização
                    }
                }
            });
        }

        // Conversores de Coordenadas (Tela <-> Plano Cartesiano)
        private int screenToCartesianX(int screenX) { return screenX - getWidth() / 2; }
        private int screenToCartesianY(int screenY) { return getHeight() / 2 - screenY; }
        private int cartesianToScreenX(int cartX) { return cartX + getWidth() / 2; }
        private int cartesianToScreenY(int cartY) { return getHeight() / 2 - cartY; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Limpa a tela

            int w = getWidth();
            int h = getHeight();

            // 1. Desenha os Eixos Cartesianos
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(w / 2, 0, w / 2, h); // Eixo Y
            g.drawLine(0, h / 2, w, h / 2); // Eixo X

            // 2. Desenha as retas calculando os pixels
            g.setColor(Color.BLACK);
            String alg = (String) comboAlgoritmo.getSelectedItem();

            for (LineDef linha : linhas) {
                List<Point> pontos = "DDA".equals(alg) ?
                        AlgoritmoRetas.dda(linha.x1, linha.y1, linha.x2, linha.y2) :
                        AlgoritmoRetas.pontoMedio(linha.x1, linha.y1, linha.x2, linha.y2);

                for (Point p : pontos) {
                    int screenX = cartesianToScreenX(p.x);
                    int screenY = cartesianToScreenY(p.y);
                    // Pinta um "pixel" (retângulo 1x1 ou 2x2 para melhor visibilidade)
                    g.fillRect(screenX, screenY, 2, 2);
                }
            }
        }
    }
}
