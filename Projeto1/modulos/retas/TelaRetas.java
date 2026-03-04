package Projeto1.modulos.retas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class TelaRetas extends JPanel {
    private JTextField txtX1, txtY1, txtX2, txtY2;
    private JComboBox<String> comboAlgo;
    private DefaultListModel<String> modelLista;
    private List<Point> pontosAtuais;
    
    // Novos Labels de Informação
    private JLabel lblCoords, lblQuadrante;
    private JLabel lblResX1, lblResY1, lblResX2, lblResY2;

    public TelaRetas() {
        setLayout(new BorderLayout());
        
        // --- Painel de Desenho (Centro) ---
        JPanel areaDesenho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharGrade(g);
                if (pontosAtuais != null) {
                    g.setColor(Color.BLACK);
                    for (Point p : pontosAtuais) {
                        g.fillRect(p.x + getWidth()/2, getHeight()/2 - p.y, 2, 2);
                    }
                }
            }
        };
        areaDesenho.setBackground(Color.WHITE);

        // Lógica para capturar coordenada e quadrante em tempo real
        areaDesenho.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX() - areaDesenho.getWidth() / 2;
                int y = areaDesenho.getHeight() / 2 - e.getY();
                lblCoords.setText("Coordenada: (" + x + ", " + y + ")");
                lblQuadrante.setText("Quadrante: " + descobrirQuadrante(x, y));
            }
        });

        add(areaDesenho, BorderLayout.CENTER);
        add(criarPainelControle(), BorderLayout.EAST);
    }

    private String descobrirQuadrante(int x, int y) {
        if (x > 0 && y > 0) return "1";
        if (x < 0 && y > 0) return "2";
        if (x < 0 && y < 0) return "3";
        if (x > 0 && y < 0) return "4";
        return "Eixo";
    }

    private JPanel criarPainelControle() {
        JPanel painel = new JPanel();
        painel.setPreferredSize(new Dimension(280, 0));
        painel.setLayout(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topoControle = new JPanel();
        topoControle.setLayout(new BoxLayout(topoControle, BoxLayout.Y_AXIS));

        // --- SEÇÃO: INFORMAÇÕES DO PLANO ---
        topoControle.add(new JLabel("<html><b>Informações Do Plano</b></html>"));
        topoControle.add(new JSeparator());
        lblCoords = new JLabel("Coordenada: (0, 0)");
        lblQuadrante = new JLabel("Quadrante: -");
        topoControle.add(lblCoords);
        topoControle.add(lblQuadrante);
        topoControle.add(Box.createVerticalStrut(15));

        // --- SEÇÃO: INPUTS ---
        topoControle.add(new JLabel("Algoritmo:"));
        comboAlgo = new JComboBox<>(new String[]{"DDA", "Ponto Médio"});
        topoControle.add(comboAlgo);
        topoControle.add(Box.createVerticalStrut(10));

        JPanel gridInputs = new JPanel(new GridLayout(2, 4, 5, 5));
        txtX1 = new JTextField(); txtY1 = new JTextField();
        txtX2 = new JTextField(); txtY2 = new JTextField();
        gridInputs.add(new JLabel("X1:")); gridInputs.add(txtX1);
        gridInputs.add(new JLabel("Y1:")); gridInputs.add(txtY1);
        gridInputs.add(new JLabel("X2:")); gridInputs.add(txtX2);
        gridInputs.add(new JLabel("Y2:")); gridInputs.add(txtY2);
        topoControle.add(gridInputs);
        topoControle.add(Box.createVerticalStrut(10));

        // Botões
        JButton btnDesenhar = new JButton("Desenhar");
        btnDesenhar.setBackground(new Color(76, 175, 80));
        btnDesenhar.setForeground(Color.WHITE);
        btnDesenhar.addActionListener(e -> acaoDesenhar());

        JButton btnLimpar = new JButton("Limpar Tela");
        btnLimpar.setBackground(new Color(220, 53, 69));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.addActionListener(e -> acaoLimpar());

        topoControle.add(btnDesenhar);
        topoControle.add(Box.createVerticalStrut(5));
        topoControle.add(btnLimpar);
        topoControle.add(Box.createVerticalStrut(15));

        // --- SEÇÃO: INFORMAÇÕES DA RETA ATUAL ---
        topoControle.add(new JLabel("<html><b>Informações Da Reta Atual</b></html>"));
        topoControle.add(new JSeparator());
        JPanel gridResultados = new JPanel(new GridLayout(2, 2));
        lblResX1 = new JLabel("X Inicial: -"); lblResY1 = new JLabel("Y Inicial: -");
        lblResX2 = new JLabel("X Final: -"); lblResY2 = new JLabel("Y Final: -");
        gridResultados.add(lblResX1); gridResultados.add(lblResY1);
        gridResultados.add(lblResX2); gridResultados.add(lblResY2);
        topoControle.add(gridResultados);
        topoControle.add(Box.createVerticalStrut(10));

        // --- LISTA DE PONTOS (LOG) ---
        modelLista = new DefaultListModel<>();
        JScrollPane scroll = new JScrollPane(new JList<>(modelLista));
        
        painel.add(topoControle, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    private void acaoDesenhar() {
        try {
            int x1 = Integer.parseInt(txtX1.getText());
            int y1 = Integer.parseInt(txtY1.getText());
            int x2 = Integer.parseInt(txtX2.getText());
            int y2 = Integer.parseInt(txtY2.getText());

            lblResX1.setText("X Inicial: " + x1); lblResY1.setText("Y Inicial: " + y1);
            lblResX2.setText("X Final: " + x2); lblResY2.setText("Y Final: " + y2);

            if (comboAlgo.getSelectedItem().equals("DDA")) {
                pontosAtuais = AlgoritmosRetas.calcularDDA(x1, y1, x2, y2);
            } else {
                pontosAtuais = AlgoritmosRetas.calcularPontoMedio(x1, y1, x2, y2);
            }
            atualizarListaLogs();
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro nos dados!");
        }
    }

    private void acaoLimpar() {
        pontosAtuais = null;
        modelLista.clear();
        lblResX1.setText("X Inicial: -"); lblResY1.setText("Y Inicial: -");
        lblResX2.setText("X Final: -"); lblResY2.setText("Y Final: -");
        repaint();
    }

    private void atualizarListaLogs() {
        modelLista.clear();
        for (int i = 0; i < pontosAtuais.size(); i++) {
            Point p = pontosAtuais.get(i);
            modelLista.addElement("X" + i + ": " + p.x + "  Y" + i + ": " + p.y);
        }
    }

    private void desenharGrade(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
    }
}