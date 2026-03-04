package Projeto1.inicio;

import Projeto1.modulos.retas.TelaRetas;
import Projeto1.modulos.circuferencia.TelaCirculo;
// 1. MUDANÇA AQUI: Importando do novo nome do pacote
import Projeto1.modulos.duasD.Tela2D; 

import javax.swing.*;
import java.awt.*;

public class TelaInicio extends JFrame {
    private JPanel container;

    public TelaInicio() {
        setTitle("Computação Gráfica - Início");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        container = new JPanel(new CardLayout());
        JPanel painelBoasVindas = new JPanel(new GridBagLayout());
        painelBoasVindas.add(new JLabel("Selecione um algoritmo no menu superior para começar."));
        container.add(painelBoasVindas);
        
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Retas
        JMenu menuRetas = new JMenu("Retas");
        JMenuItem itemRetas = new JMenuItem("Rasterização de Retas");
        itemRetas.addActionListener(e -> trocarTela(new TelaRetas()));
        menuRetas.add(itemRetas);

        // Menu Circunferência
        JMenu menuCirc = new JMenu("Circunferência");
        JMenuItem itemCirc = new JMenuItem("Algoritmos de Círculo");
        itemCirc.addActionListener(e -> trocarTela(new TelaCirculo()));
        menuCirc.add(itemCirc);

        // 2. MUDANÇA AQUI: Menu de Transformações 2D
        JMenu menu2D = new JMenu("2D");
        JMenuItem item2D = new JMenuItem("Transformações Geométricas");
        item2D.addActionListener(e -> trocarTela(new Tela2D())); // Chama a tela da pasta duasD
        menu2D.add(item2D);

        // Adicionando à barra de menu
        menuBar.add(new JMenu("Sistemas de Coordenadas"));
        menuBar.add(menuRetas);
        menuBar.add(menuCirc);
        menuBar.add(menu2D); // Agora o menu 2D está funcional
        menuBar.add(new JMenu("3D"));
        
        setJMenuBar(menuBar);
        add(container);
        setLocationRelativeTo(null);
    }

    private void trocarTela(JPanel novaTela) {
        container.removeAll();
        container.add(novaTela);
        container.revalidate();
        container.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicio().setVisible(true));
    }
}