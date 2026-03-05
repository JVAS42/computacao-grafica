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

        // Configura o painel principal com CardLayout para alternar as telas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Adiciona painéis ao CardLayout
        mainPanel.add(new main.ui.panels.CoordenadasPanel(), "COORDENADAS");
        mainPanel.add(new main.ui.panels.RetasPanel(), "RETAS");
        mainPanel.add(new main.ui.panels.CircunferenciaPanel(), "CIRCUNFERENCIA");
        mainPanel.add(new main.ui.panels.ElipsePanel(), "ELIPSE");
        mainPanel.add(new main.ui.panels.ParabolaPanel(), "PARABOLA");
        mainPanel.add(new main.ui.panels.Transformacoes2DPanel(), "2D");
        mainPanel.add(new main.ui.panels.Transformacoes3DPanel(), "3D");

        // --- Novos painéis de recorte adicionados aqui ---
        mainPanel.add(new main.ui.panels.CohenSutherlandPanel(), "RECORTE_LINHAS");
        mainPanel.add(new main.ui.panels.SutherlandHodgmanPanel(), "RECORTE_POLIGONOS_SH");
        mainPanel.add(new main.ui.panels.WeilerAthertonPanel(), "RECORTE_POLIGONOS_WA");

        mainPanel.add(new main.ui.panels.BezierPanel(), "BEZIER");

        // Configura o Menu Superior (agora que o cardLayout e mainPanel existem)
        configurarMenuSuperior();

        add(mainPanel, BorderLayout.CENTER);
    }

    private void configurarMenuSuperior() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuModulos = new JMenu("Módulos CG");

        // Criação dos itens do menu principal
        JMenuItem itemCoordenadas = new JMenuItem("Sistemas de Coordenadas");
        JMenuItem itemRetas = new JMenuItem("Retas");
        JMenuItem itemCircunferencia = new JMenuItem("Circunferência");
        JMenuItem itemElipse = new JMenuItem("Elipse");
        JMenuItem itemParabola = new JMenuItem("Parábola");
        JMenuItem item2D = new JMenuItem("2D");
        JMenuItem item3D = new JMenuItem("3D");
        JMenuItem itemBezier = new JMenuItem("Curvas de Bézier");

        // Adiciona as ações de clique para trocar os painéis
        itemCoordenadas.addActionListener(e -> cardLayout.show(mainPanel, "COORDENADAS"));
        itemRetas.addActionListener(e -> cardLayout.show(mainPanel, "RETAS"));
        itemCircunferencia.addActionListener(e -> cardLayout.show(mainPanel, "CIRCUNFERENCIA"));
        itemElipse.addActionListener(e -> cardLayout.show(mainPanel, "ELIPSE"));
        itemParabola.addActionListener(e -> cardLayout.show(mainPanel, "PARABOLA"));
        item2D.addActionListener(e -> cardLayout.show(mainPanel, "2D"));
        item3D.addActionListener(e -> cardLayout.show(mainPanel, "3D"));
        itemBezier.addActionListener(e -> cardLayout.show(mainPanel, "BEZIER"));

        // --- Submenu para os Algoritmos de Recorte ---
        JMenu menuRecortes = new JMenu("Algoritmos de Recorte");

        JMenuItem itemCohenSutherland = new JMenuItem("Recorte de Linhas (Cohen-Sutherland)");
        itemCohenSutherland.addActionListener(e -> cardLayout.show(mainPanel, "RECORTE_LINHAS"));

        JMenuItem itemSutherlandHodgman = new JMenuItem("Recorte de Polígonos (Sutherland-Hodgman)");
        itemSutherlandHodgman.addActionListener(e -> cardLayout.show(mainPanel, "RECORTE_POLIGONOS_SH"));

        JMenuItem itemWeilerAtherton = new JMenuItem("Recorte de Polígonos (Weiler-Atherton)");
        itemWeilerAtherton.addActionListener(e -> cardLayout.show(mainPanel, "RECORTE_POLIGONOS_WA"));

        // Adiciona os itens ao submenu
        menuRecortes.add(itemCohenSutherland);
        menuRecortes.add(itemSutherlandHodgman);
        menuRecortes.add(itemWeilerAtherton);

        // --- Adiciona todos os itens ao menu principal ---
        menuModulos.add(itemCoordenadas);
        menuModulos.addSeparator(); // Linha divisória para primitivas
        menuModulos.add(itemRetas);
        menuModulos.add(itemCircunferencia);
        menuModulos.add(itemElipse);
        menuModulos.add(itemParabola);
        menuModulos.addSeparator(); // Linha divisória para transformações
        menuModulos.add(item2D);
        menuModulos.add(item3D);
        menuModulos.addSeparator(); // Linha divisória para algoritmos de recorte

        // Adiciona o submenu agrupado de recortes
        menuModulos.add(menuRecortes);

        menuModulos.add(itemBezier);

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