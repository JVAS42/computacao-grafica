
package main.ui.panels;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new GridBagLayout()); // Centraliza o conteúdo perfeitamente
        setBackground(new Color(45, 52, 54)); // Um cinza escuro moderno

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título Principal
        JLabel lblTitulo = new JLabel("Projeto de Computação Gráfica");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(lblTitulo, gbc);

        // Subtítulo / Professor
        JLabel lblProfessor = new JLabel("Professor: Robson Pequeno de Sousa");
        lblProfessor.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblProfessor.setForeground(new Color(178, 190, 195));
        gbc.gridy = 1;
        add(lblProfessor, gbc);

        // Divisor visual
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(400, 1));
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 30, 0);
        add(sep, gbc);

        // Equipe
        JLabel lblEquipe = new JLabel("Equipe: Denis William, Flávia Vitória , João Victor de Araujo, Raquel Melo");
        lblEquipe.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblEquipe.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(lblEquipe, gbc);

        // Instrução
        JLabel lblDica = new JLabel("Selecione um módulo no menu superior para começar");
        lblDica.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDica.setForeground(new Color(9, 132, 227)); // Um azul vibrante
        gbc.gridy = 4;
        gbc.insets = new Insets(50, 10, 10, 10);
        add(lblDica, gbc);
    }
}