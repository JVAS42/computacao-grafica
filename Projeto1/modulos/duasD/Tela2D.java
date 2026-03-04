package Projeto1.modulos.duasD;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Tela2D extends JPanel {
    private List<Point2D.Double> pontosObjeto;
    private DefaultListModel<String> modelHistorico;

    public Tela2D() {
        // BorderLayout é excelente para separar "Área Principal" de "Menu Lateral"
        setLayout(new BorderLayout());
        pontosObjeto = new ArrayList<>();
        reiniciarObjeto();

        // 1. Área de Desenho (Centro)
        JPanel areaDesenho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharEixos(g);
                desenharObjeto(g);
            }
        };
        areaDesenho.setBackground(Color.WHITE);
        add(areaDesenho, BorderLayout.CENTER);

        // 2. Painel Lateral com Scroll (Direita)
        JPanel painelControles = criarPainelLateral();
        JScrollPane scrollPane = new JScrollPane(painelControles);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.EAST);
    }

    private JPanel criarPainelLateral() {
        JPanel painel = new JPanel();
        // BoxLayout para empilhar os componentes verticalmente
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título Geral
        JLabel titulo = new JLabel("TRANSFORMAÇÕES 2D");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        // --- SEÇÃO: TRANSLAÇÃO ---
        adicionarSecao(painel, "Translação (ΔX, ΔY)");
        JTextField tx = criarCampoTexto("10");
        JTextField ty = criarCampoTexto("10");
        painel.add(new JLabel("X:")); painel.add(tx);
        painel.add(new JLabel("Y:")); painel.add(ty);
        
        JButton btnTrans = new JButton("Aplicar Translação");
        btnTrans.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnTrans.addActionListener(e -> aplicarTransformacao(
            Transformacoes2D.criarMatrizTranslacao(Double.parseDouble(tx.getText()), Double.parseDouble(ty.getText())),
            "Translação (" + tx.getText() + ", " + ty.getText() + ")"
        ));
        painel.add(Box.createVerticalStrut(5));
        painel.add(btnTrans);
        painel.add(Box.createVerticalStrut(20));

        // --- SEÇÃO: ROTAÇÃO ---
        adicionarSecao(painel, "Rotação (Graus)");
        JTextField tAngulo = criarCampoTexto("45");
        painel.add(new JLabel("Ângulo:"));
        painel.add(tAngulo);
        
        JButton btnRot = new JButton("Rotacionar na Origem");
        btnRot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnRot.addActionListener(e -> aplicarTransformacao(
            Transformacoes2D.criarMatrizRotacao(Double.parseDouble(tAngulo.getText()), 0, 0),
            "Rotação " + tAngulo.getText() + "°"
        ));
        painel.add(Box.createVerticalStrut(5));
        painel.add(btnRot);
        painel.add(Box.createVerticalStrut(20));

        // --- SEÇÃO: HISTÓRICO ---
        adicionarSecao(painel, "Histórico");
        modelHistorico = new DefaultListModel<>();
        JList<String> listaH = new JList<>(modelHistorico);
        JScrollPane scrollH = new JScrollPane(listaH);
        scrollH.setPreferredSize(new Dimension(0, 150));
        painel.add(scrollH);

        // Botão Limpar
        JButton btnLimpar = new JButton("Resetar Quadrado");
        btnLimpar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnLimpar.addActionListener(e -> {
            reiniciarObjeto();
            modelHistorico.clear();
            repaint();
        });
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnLimpar);

        return painel;
    }

    // Funções Auxiliares de Estética
    private void adicionarSecao(JPanel p, String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(label);
        p.add(Box.createVerticalStrut(5));
    }

    private JTextField criarCampoTexto(String padrao) {
        JTextField tf = new JTextField(padrao);
        // Isso impede que o campo de texto estique verticalmente e ocupe a tela toda
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return tf;
    }

    // Logica de Transformação (Igual ao anterior)
    private void reiniciarObjeto() {
        pontosObjeto.clear();
        pontosObjeto.add(new Point2D.Double(0, 0));
        pontosObjeto.add(new Point2D.Double(50, 0));
        pontosObjeto.add(new Point2D.Double(50, 50));
        pontosObjeto.add(new Point2D.Double(0, 50));
    }

    private void aplicarTransformacao(double[][] matriz, String log) {
        try {
            for (int i = 0; i < pontosObjeto.size(); i++) {
                pontosObjeto.set(i, Transformacoes2D.multiplicarMatrizPonto(matriz, pontosObjeto.get(i)));
            }
            modelHistorico.addElement(log);
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro nos valores inseridos!");
        }
    }

    private void desenharEixos(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(220, 220, 220));
        g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
    }

    private void desenharObjeto(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2)); // Linha mais grossa para ver melhor
        int midX = getWidth() / 2;
        int midY = getHeight() / 2;

        for (int i = 0; i < pontosObjeto.size(); i++) {
            Point2D.Double p1 = pontosObjeto.get(i);
            Point2D.Double p2 = pontosObjeto.get((i + 1) % pontosObjeto.size());
            // Invertemos o Y (midY - p.y) para que o positivo seja para cima
            g2.drawLine(midX + (int) p1.x, midY - (int) p1.y, midX + (int) p2.x, midY - (int) p2.y);
        }
    }
}