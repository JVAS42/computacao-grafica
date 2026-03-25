import customtkinter as ctk
from src.ui.home_frame import HomeFrame
from src.ui.filter_frame import FilterFrame


class MainWindow(ctk.CTk):
    def __init__(self):
        super().__init__()

        self.title("Projeto Processamento de Imagens 2026")
        self.geometry("1100x600")

        # Configuração de Grid (Layout)
        self.grid_columnconfigure(1, weight=1)
        self.grid_rowconfigure(0, weight=1)

        # --- MENU LATERAL (Equivalente ao seu <nav>) ---
        self.navigation_frame = ctk.CTkFrame(self, corner_radius=0)
        self.navigation_frame.grid(row=0, column=0, sticky="nsew")

        self.logo_label = ctk.CTkLabel(self.navigation_frame, text="Projeto Imagens",
                                       font=ctk.CTkFont(size=20, weight="bold"))
        self.logo_label.grid(row=0, column=0, padx=20, pady=20)

        # Botões do Menu
        self.btn_home = ctk.CTkButton(self.navigation_frame, text="Home", command=self.show_home)
        self.btn_home.grid(row=1, column=0, padx=10, pady=10)

        self.btn_filtros = ctk.CTkButton(self.navigation_frame, text="Filtros", command=self.show_filters)
        self.btn_filtros.grid(row=2, column=0, padx=10, pady=10)

        # --- ÁREA DE CONTEÚDO (Onde as "páginas" aparecem) ---
        self.container = ctk.CTkFrame(self, fg_color="transparent")
        self.container.grid(row=0, column=1, sticky="nsew", padx=20, pady=20)

        # Inicializa a tela inicial
        self.current_frame = None
        self.show_home()

    def show_home(self):
        self._switch_frame(HomeFrame)

    def show_filters(self):
        self._switch_frame(FilterFrame)

    def _switch_frame(self, frame_class):
        """Limpa o conteúdo atual e carrega a nova 'página'"""
        if self.current_frame is not None:
            self.current_frame.destroy()
        self.current_frame = frame_class(self.container)
        self.current_frame.pack(fill="both", expand=True)