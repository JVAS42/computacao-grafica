import tkinter as tk
from tkinter import messagebox


class ComputacaoGraficaApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Trabalho de Computação Gráfica")

        # Define o tamanho inicial da janela (largura x altura)
        self.root.geometry("900x600")

        # Configura o menu superior
        self.criar_menu()

        # Cria a área principal onde o conteúdo de cada opção será renderizado
        self.main_frame = tk.Frame(self.root, bg="#f0f0f0")
        self.main_frame.pack(fill=tk.BOTH, expand=True)

        # Tela inicial de boas-vindas
        self.mostrar_tela_inicial()

    def criar_menu(self):
        # Cria a barra de menus
        menu_bar = tk.Menu(self.root)

        # Adiciona as opções diretamente na barra superior
        # O 'command' usa lambda para passar o nome da tela para a função 'abrir_tela'
        menu_bar.add_command(label="Sistemas de Coordenadas",
                             command=lambda: self.abrir_tela("Sistemas de Coordenadas"))
        menu_bar.add_command(label="Retas", command=lambda: self.abrir_tela("Retas"))
        menu_bar.add_command(label="Circunferência", command=lambda: self.abrir_tela("Circunferência"))
        menu_bar.add_command(label="2D", command=lambda: self.abrir_tela("Transformações 2D"))
        menu_bar.add_command(label="3D", command=lambda: self.abrir_tela("Transformações 3D"))
        menu_bar.add_command(label="Recorte de Cohen-Sutherland",
                             command=lambda: self.abrir_tela("Recorte de Cohen-Sutherland"))

        # Atribui a barra de menus à janela principal
        self.root.config(menu=menu_bar)

    def mostrar_tela_inicial(self):
        self.limpar_frame()
        label = tk.Label(self.main_frame, text="Selecione uma opção no menu superior", font=("Arial", 16), bg="#f0f0f0")
        label.place(relx=0.5, rely=0.5, anchor=tk.CENTER)

    def limpar_frame(self):
        # Destrói todos os widgets (botões, canvas, textos) da tela atual antes de abrir a próxima
        for widget in self.main_frame.winfo_children():
            widget.destroy()

    def abrir_tela(self, nome_tela):
        self.limpar_frame()

        if nome_tela == "Sistemas de Coordenadas":
            from gui.views.view_coordenadas import ViewCoordenadas
            ViewCoordenadas(self.main_frame)
        elif nome_tela == "Retas":
            from gui.views.view_retas import ViewRetas
            ViewRetas(self.main_frame)
        elif nome_tela == "Circunferência":
            from gui.views.view_circunferencia import ViewCircunferencia
            ViewCircunferencia(self.main_frame)
        elif nome_tela == "Transformações 2D":
            from gui.views.view_2d import View2D
            View2D(self.main_frame)
        elif nome_tela == "Transformações 3D" or nome_tela == "3D":
            from gui.views.view_3d import View3D
            View3D(self.main_frame)
        elif nome_tela == "Recorte de Cohen-Sutherland":
            from gui.views.view_recorte import ViewRecorte
            ViewRecorte(self.main_frame)
        else:
            titulo = tk.Label(self.main_frame, text=f"Módulo em construção: {nome_tela}", font=("Arial", 18))
            titulo.pack(pady=20)


if __name__ == "__main__":
    # Inicializa a aplicação Tkinter
    root = tk.Tk()
    app = ComputacaoGraficaApp(root)

    # Inicia o loop de eventos da interface
    root.mainloop()