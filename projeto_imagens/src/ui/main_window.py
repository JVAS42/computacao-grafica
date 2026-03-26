import customtkinter as ctk
from src.ui.home_frame import HomeFrame
from src.ui.filter_frame import FilterFrame
from src.ui.combinacao_frame import CombinacaoFrame
from src.ui.transformacoes_frame import TransformacoesFrame  # <-- NOVO
from src.ui.histograma_frame import HistogramaFrame
from src.ui.morfologia_frame import MorfologiaFrame
from src.ui.morfismo_frame import MorfismoFrame


class MainWindow(ctk.CTk):
    def __init__(self):
        super().__init__()

        self.title("Projeto Processamento de Imagens 2026")
        self.geometry("1200x700")  # Aumentei um pouco para caber as 3 colunas e tabelas
        self.configure(fg_color="#F0F0F0")

        self.grid_columnconfigure(1, weight=1)
        self.grid_rowconfigure(0, weight=1)

        # --- MENU LATERAL ---
        self.navigation_frame = ctk.CTkFrame(self, corner_radius=0, fg_color="#213555")
        self.navigation_frame.grid(row=0, column=0, sticky="nsew")

        self.logo_label = ctk.CTkLabel(self.navigation_frame, text="Projeto Imagens",
                                       font=ctk.CTkFont(size=20, weight="bold"), text_color="#F0F0F0")
        self.logo_label.grid(row=0, column=0, padx=20, pady=20)

        # Botões do Menu
        btn_args = {"fg_color": "transparent", "text_color": "#F0F0F0", "hover_color": "#182740", "anchor": "w"}

        self.btn_home = ctk.CTkButton(self.navigation_frame, text="Home", command=self.show_home, **btn_args)
        self.btn_home.grid(row=1, column=0, padx=10, pady=5, sticky="ew")

        self.btn_filtros = ctk.CTkButton(self.navigation_frame, text="Filtros", command=self.show_filters, **btn_args)
        self.btn_filtros.grid(row=2, column=0, padx=10, pady=5, sticky="ew")

        self.btn_combinacao = ctk.CTkButton(self.navigation_frame, text="Combinação", command=self.show_combinacao,
                                            **btn_args)
        self.btn_combinacao.grid(row=3, column=0, padx=10, pady=5, sticky="ew")

        # --- NOVA ABA ADICIONADA ---
        self.btn_transformacoes = ctk.CTkButton(self.navigation_frame, text="Transformações",
                                                command=self.show_transformacoes, **btn_args)
        self.btn_transformacoes.grid(row=4, column=0, padx=10, pady=5, sticky="ew")

        self.btn_histograma = ctk.CTkButton(self.navigation_frame, text="Histogramas", command=self.show_histograma,
                                            **btn_args)
        self.btn_histograma.grid(row=5, column=0, padx=10, pady=5, sticky="ew")

        self.btn_morfologia = ctk.CTkButton(self.navigation_frame, text="Morfologia", command=self.show_morfologia,
                                            **btn_args)
        self.btn_morfologia.grid(row=6, column=0, padx=10, pady=5, sticky="ew")

        self.btn_morfismo = ctk.CTkButton(self.navigation_frame, text="Morfismo Temporal", command=self.show_morfismo,
                                          **btn_args)
        self.btn_morfismo.grid(row=7, column=0, padx=10, pady=5, sticky="ew")

        # --- ÁREA DE CONTEÚDO PRINCIPAL ---
        self.container = ctk.CTkFrame(self, fg_color="transparent")
        self.container.grid(row=0, column=1, sticky="nsew", padx=20, pady=20)

        self.current_frame = None
        self.show_home()

    # --- FUNÇÕES DE NAVEGAÇÃO ---
    def show_home(self): self._switch_frame(HomeFrame)

    def show_filters(self): self._switch_frame(FilterFrame)

    def show_combinacao(self): self._switch_frame(CombinacaoFrame)

    def show_transformacoes(self): self._switch_frame(TransformacoesFrame)

    def show_histograma(self): self._switch_frame(HistogramaFrame)

    def show_morfologia(self): self._switch_frame(MorfologiaFrame)

    def show_morfismo(self): self._switch_frame(MorfismoFrame)

    def _switch_frame(self, frame_class):
        if self.current_frame is not None:
            self.current_frame.destroy()
        self.current_frame = frame_class(self.container)
        self.current_frame.pack(fill="both", expand=True)