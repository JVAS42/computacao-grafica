package main.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MainWindow() {
        // Configurações básicas da janela
        setTitle("Aplicação de Computação Gráfica");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        // Configura o Menu Superior
        configurarMenuSuperior();

        // Configura o painel principal com CardLayout para alternar as telas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Adiciona painéis de placeholder (onde você colocará a lógica depois)
        mainPanel.add(new main.ui.panels.CoordenadasPanel(), "COORDENADAS");
        mainPanel.add(new main.ui.panels.RetasPanel(), "RETAS");
        mainPanel.add(new main.ui.panels.CircunferenciaPanel(), "CIRCUNFERENCIA");
        mainPanel.add(new main.ui.panels.Transformacoes2DPanel(), "2D");
        mainPanel.add(new main.ui.panels.Transformacoes3DPanel(), "3D");
        mainPanel.add(new main.ui.panels.CohenSutherlandPanel(), "RECORTE");

        add(mainPanel, BorderLayout.CENTER);
    }

    private void configurarMenuSuperior() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuModulos = new JMenu("Módulos CG");

        // Criação dos itens do menu
        JMenuItem itemCoordenadas = new JMenuItem("Sistemas de Coordenadas");
        JMenuItem itemRetas = new JMenuItem("Retas");
        JMenuItem itemCircunferencia = new JMenuItem("Circunferência");
        JMenuItem item2D = new JMenuItem("2D");
        JMenuItem item3D = new JMenuItem("3D");
        JMenuItem itemRecorte = new JMenuItem("Recorte de Cohen-Sutherland");

        // Adiciona as ações de clique para trocar os painéis
        itemCoordenadas.addActionListener(e -> cardLayout.show(mainPanel, "COORDENADAS"));
        itemRetas.addActionListener(e -> cardLayout.show(mainPanel, "RETAS"));
        itemCircunferencia.addActionListener(e -> cardLayout.show(mainPanel, "CIRCUNFERENCIA"));
        item2D.addActionListener(e -> cardLayout.show(mainPanel, "2D"));
        item3D.addActionListener(e -> cardLayout.show(mainPanel, "3D"));
        itemRecorte.addActionListener(e -> cardLayout.show(mainPanel, "RECORTE"));

        // Adiciona os itens ao menu
        menuModulos.add(itemCoordenadas);
        menuModulos.add(itemRetas);
        menuModulos.add(itemCircunferencia);
        menuModulos.addSeparator(); // Linha divisória para organização
        menuModulos.add(item2D);
        menuModulos.add(item3D);
        menuModulos.addSeparator();
        menuModulos.add(itemRecorte);

        menuBar.add(menuModulos);
        setJMenuBar(menuBar);
    }

    // Método auxiliar provisório para criar telas vazias com título
    private JPanel criarPainelPlaceholder(String titulo) {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        painel.add(label, BorderLayout.CENTER);

        // Fundo branco como na imagem de referência
        painel.setBackground(Color.WHITE);
        return painel;
    }
}