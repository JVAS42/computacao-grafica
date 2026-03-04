package Projeto1.modulos.circuferencia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class TelaCirculo extends JPanel {
    private JTextField txtXC, txtYC, txtR;
    private JComboBox<String> comboAlgo;
    private DefaultListModel<String> modelLista;
    private List<Point> pontosAtuais;
    private JLabel lblCoords, lblQuadrante, lblResXC, lblResYC, lblResR;

    public TelaCirculo() {
        setLayout(new BorderLayout());

        // --- Área de Desenho ---
        JPanel areaDesenho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharGrade(g);
                if (pontosAtuais != null) {
                    g.setColor(Color.BLUE); // Diferente das retas para identificar
                    for (Point p : pontosAtuais) {
                        g.fillRect(p.x + getWidth()/2, getHeight()/2 - p.y, 2, 2);
                    }
                }
            }
        };
        areaDesenho.setBackground(Color.WHITE);

        // Mouse Move para Coordenadas
        areaDesenho.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int x = e.getX() - areaDesenho.getWidth() / 2;
                int y = areaDesenho.getHeight() / 2 - e.getY();
                lblCoords.setText("Coordenada: (" + x + ", " + y + ")");
                lblQuadrante.setText("Quadrante: " + (x > 0 ? (y > 0 ? "1" : "4") : (y > 0 ? "2" : "3")));
            }
        });

        add(areaDesenho, BorderLayout.CENTER);
        add(criarPainelControle(), BorderLayout.EAST);
    }

    private JPanel criarPainelControle() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setPreferredSize(new Dimension(280, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));

        // Info do Plano
        topo.add(new JLabel("<html><b>Informações Do Plano</b></html>"));
        lblCoords = new JLabel("Coordenada: (0, 0)");
        lblQuadrante = new JLabel("Quadrante: -");
        topo.add(lblCoords); topo.add(lblQuadrante);
        topo.add(Box.createVerticalStrut(10));

        // Inputs
        topo.add(new JLabel("Algoritmo:"));
        comboAlgo = new JComboBox<>(new String[]{"Eq. Explícita", "Trigonométrico", "Ponto Médio"});
        topo.add(comboAlgo);

        JPanel gridInputs = new JPanel(new GridLayout(2, 2, 5, 5));
        txtXC = new JTextField(); txtYC = new JTextField(); txtR = new JTextField();
        gridInputs.add(new JLabel("Centro X:")); gridInputs.add(txtXC);
        gridInputs.add(new JLabel("Centro Y:")); gridInputs.add(txtYC);
        topo.add(gridInputs);
        topo.add(new JLabel("Raio (R):"));
        topo.add(txtR);

        // Botões
        JButton btnDesenhar = new JButton("Desenhar Círculo");
        btnDesenhar.setBackground(new Color(76, 175, 80));
        btnDesenhar.setForeground(Color.WHITE);
        btnDesenhar.addActionListener(e -> desenhar());

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> {
            pontosAtuais = null;
            modelLista.clear();
            repaint();
        });

        topo.add(Box.createVerticalStrut(10));
        topo.add(btnDesenhar);
        topo.add(btnLimpar);
        topo.add(Box.createVerticalStrut(15));

        // Info da Circunferência
        topo.add(new JLabel("<html><b>Circunferência Atual</b></html>"));
        lblResXC = new JLabel("Centro X: -"); lblResYC = new JLabel("Centro Y: -");
        lblResR = new JLabel("Raio: -");
        topo.add(lblResXC); topo.add(lblResYC); topo.add(lblResR);

        // Lista de Pontos
        modelLista = new DefaultListModel<>();
        JScrollPane scroll = new JScrollPane(new JList<>(modelLista));

        painel.add(topo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    private void desenhar() {
        try {
            int xc = Integer.parseInt(txtXC.getText());
            int yc = Integer.parseInt(txtYC.getText());
            int r = Integer.parseInt(txtR.getText());

            String sel = (String) comboAlgo.getSelectedItem();
            if (sel.equals("Eq. Explícita")) pontosAtuais = AlgoritmosCirculo.calcularEquacaoExplicita(xc, yc, r);
            else if (sel.equals("Trigonométrico")) pontosAtuais = AlgoritmosCirculo.calcularTrigonometrico(xc, yc, r);
            else pontosAtuais = AlgoritmosCirculo.calcularPontoMedio(xc, yc, r);

            lblResXC.setText("Centro X: " + xc); lblResYC.setText("Centro Y: " + yc);
            lblResR.setText("Raio: " + r);
            
            modelLista.clear();
            for (Point p : pontosAtuais) modelLista.addElement("X: " + p.x + " Y: " + p.y);
            
            repaint();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Valores inválidos!"); }
    }

    private void desenharGrade(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
    }
}