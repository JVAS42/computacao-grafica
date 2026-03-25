import customtkinter as ctk

# Forçamos o modo claro como base para evitar conflitos com o tema do SO
ctk.set_appearance_mode("Light")


class MainWindow(ctk.CTk):
    def __init__(self):
        super().__init__()

        # --- DEFINIÇÃO DAS CORES ---
        self.COR_ESCURA = "#213555"  # Azul marinho
        self.COR_CLARA = "#F0F0F0"  # Cinza gelo
        self.COR_HOVER = "#324e7b"  # Um azul ligeiramente mais claro para o "hover" do mouse

        self.title("Projeto de Processamento de Imagem")
        self.geometry("1000x600")

        # Cor de fundo da janela inteira
        self.configure(fg_color=self.COR_CLARA)

        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(1, weight=1)

        # ==========================================
        # 1. MENU LATERAL (Sidebar) - Fundo Escuro
        # ==========================================
        self.sidebar_frame = ctk.CTkFrame(self, width=200, corner_radius=0, fg_color=self.COR_ESCURA)
        self.sidebar_frame.grid(row=0, column=0, sticky="nsew")
        self.sidebar_frame.grid_rowconfigure(8, weight=1)

        # Textos da sidebar recebem a cor clara
        self.logo_label = ctk.CTkLabel(self.sidebar_frame, text="Menu Principal",
                                       font=ctk.CTkFont(size=20, weight="bold"), text_color=self.COR_CLARA)
        self.logo_label.grid(row=0, column=0, padx=20, pady=(20, 20))

        # Função auxiliar para criar botões padronizados
        def criar_botao_menu(texto, comando, linha):
            btn = ctk.CTkButton(self.sidebar_frame, text=texto, command=comando,
                                fg_color="transparent",  # Fundo transparente para mesclar com a sidebar
                                text_color=self.COR_CLARA,
                                hover_color=self.COR_HOVER,  # Cor ao passar o mouse
                                anchor="w")  # Alinha o texto à esquerda
            btn.grid(row=linha, column=0, padx=20, pady=10, sticky="ew")
            return btn

        self.btn_home = criar_botao_menu("Home", self.show_home, 1)
        self.btn_filtros = criar_botao_menu("Filtros", lambda: self.show_frame("Filtros"), 2)
        self.btn_combinacao = criar_botao_menu("Combinação", lambda: self.show_frame("Combinação"), 3)
        self.btn_transformacoes = criar_botao_menu("Transformações", lambda: self.show_frame("Transformações"), 4)
        self.btn_histograma = criar_botao_menu("Histograma", lambda: self.show_frame("Histograma"), 5)
        self.btn_morfologia = criar_botao_menu("Morfologia", lambda: self.show_frame("Morfologia"), 6)
        self.btn_morfismo = criar_botao_menu("Morfismo Temporal", lambda: self.show_frame("Morfismo"), 7)

        # ==========================================
        # 2. ÁREA PRINCIPAL - Fundo Claro
        # ==========================================
        self.main_frame = ctk.CTkFrame(self, corner_radius=0, fg_color=self.COR_CLARA)
        self.main_frame.grid(row=0, column=1, sticky="nsew")
        self.main_frame.grid_rowconfigure(0, weight=1)
        self.main_frame.grid_columnconfigure(0, weight=1)

        self.frames = {}

        self.setup_home_frame()
        self.setup_placeholder_frames()
        self.show_home()

    def setup_home_frame(self):
        """Constrói a tela inicial reproduzindo os dados da equipe."""
        frame = ctk.CTkFrame(self.main_frame, fg_color="transparent")
        self.frames["Home"] = frame

        # Textos da área principal recebem a cor escura
        title = ctk.CTkLabel(frame, text="Projeto de Processamento de Imagem 2025.1",
                             font=ctk.CTkFont(size=28, weight="bold"), text_color=self.COR_ESCURA)
        title.pack(anchor="w", padx=40, pady=(40, 20))

        prof = ctk.CTkLabel(frame, text="Professor: Robson Pequeno de Sousa",
                            font=ctk.CTkFont(size=16), text_color=self.COR_ESCURA)
        prof.pack(anchor="w", padx=40, pady=5)

        lbl_integrantes = ctk.CTkLabel(frame, text="Integrantes:",
                                       font=ctk.CTkFont(size=16, weight="bold"), text_color=self.COR_ESCURA)
        lbl_integrantes.pack(anchor="w", padx=40, pady=(15, 5))

        for nome in ["➜ Denis", "➜ Flávia", "➜ João", "➜ Raquel"]:
            lbl = ctk.CTkLabel(frame, text=nome, font=ctk.CTkFont(size=14), text_color=self.COR_ESCURA)
            lbl.pack(anchor="w", padx=60, pady=2)

        lbl_ementa = ctk.CTkLabel(frame, text="Ementa:",
                                  font=ctk.CTkFont(size=16, weight="bold"), text_color=self.COR_ESCURA)
        lbl_ementa.pack(anchor="w", padx=40, pady=(20, 5))

        topicos = [
            "➜ Filtros de Imagem", "➜ Combinação de Imagens", "➜ Transformações Geométricas",
            "➜ Histograma de Imagem", "➜ Morfologia Matemática", "➜ Morfismo Temporal"
        ]

        for topico in topicos:
            lbl_topico = ctk.CTkLabel(frame, text=topico, font=ctk.CTkFont(size=14), text_color=self.COR_ESCURA)
            lbl_topico.pack(anchor="w", padx=60, pady=2)

    def setup_placeholder_frames(self):
        nomes_telas = ["Filtros", "Combinação", "Transformações", "Histograma", "Morfologia", "Morfismo"]

        for nome in nomes_telas:
            frame = ctk.CTkFrame(self.main_frame, fg_color="transparent")
            label = ctk.CTkLabel(frame, text=f"Área de Trabalho: {nome}",
                                 font=ctk.CTkFont(size=24, weight="bold"), text_color=self.COR_ESCURA)
            label.pack(expand=True)
            self.frames[nome] = frame

    def show_frame(self, frame_name):
        for frame in self.frames.values():
            frame.grid_forget()
        self.frames[frame_name].grid(row=0, column=0, sticky="nsew", padx=10, pady=10)

    def show_home(self):
        self.show_frame("Home")