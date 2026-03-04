import tkinter as tk
from tkinter import ttk
from algoritmos.retas import dda, ponto_medio


class ViewRetas(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent)
        self.pack(fill=tk.BOTH, expand=True)

        self.click_count = 0
        self.ponto_inicial = (None, None)

        self.setup_ui()
        self.desenhar_eixos()

    def setup_ui(self):
        # ==========================================
        # PAINEL ESQUERDO ÚNICO (Estilo Desktop Clássico)
        # ==========================================
        self.left_panel = tk.Frame(self, width=280, padx=5, pady=5)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)
        # Impede que o frame encolha para caber apenas no conteúdo
        self.left_panel.pack_propagate(False)

        # --- Grupo 1: Informações em Tempo Real ---
        frame_live = tk.LabelFrame(self.left_panel, text=" Tempo Real ", padx=5, pady=5)
        frame_live.pack(fill=tk.X, pady=5)

        self.lbl_coord_live = tk.Label(frame_live, text="Coord: (0, 0)\nQuad: Origem", justify=tk.LEFT)
        self.lbl_coord_live.pack(anchor=tk.W)

        # --- Grupo 2: Entrada de Dados ---
        frame_entrada = tk.LabelFrame(self.left_panel, text=" Desenhar Reta ", padx=5, pady=5)
        frame_entrada.pack(fill=tk.X, pady=5)

        tk.Label(frame_entrada, text="Algoritmo:").pack(anchor=tk.W)
        self.combo_algoritmo = ttk.Combobox(frame_entrada, values=["DDA", "Ponto Médio"], state="readonly")
        self.combo_algoritmo.current(0)
        self.combo_algoritmo.pack(fill=tk.X, pady=(0, 10))
        self.combo_algoritmo.bind("<<ComboboxSelected>>", lambda e: self.limpar_tela())

        # Container para colocar X e Y lado a lado
        self.entradas = {}
        for titulo in ["Inicial", "Final"]:
            row = tk.Frame(frame_entrada)
            row.pack(fill=tk.X, pady=2)

            tk.Label(row, text=f"X {titulo}:", width=8).pack(side=tk.LEFT)
            ent_x = tk.Entry(row, width=6)
            ent_x.pack(side=tk.LEFT, padx=(0, 5))

            tk.Label(row, text=f"Y:", width=2).pack(side=tk.LEFT)
            ent_y = tk.Entry(row, width=6)
            ent_y.pack(side=tk.LEFT)

            self.entradas[f"X {titulo}:"] = ent_x
            self.entradas[f"Y {titulo}:"] = ent_y

        tk.Button(frame_entrada, text="Desenhar", command=self.desenhar_via_input).pack(fill=tk.X, pady=(10, 0))

        # --- Grupo 3: Resultados ---
        frame_resultado = tk.LabelFrame(self.left_panel, text=" Resultados da Reta ", padx=5, pady=5)
        frame_resultado.pack(fill=tk.BOTH, expand=True, pady=5)

        self.lbl_info_reta = tk.Label(frame_resultado, text="Nenhuma reta", font=("Arial", 8))
        self.lbl_info_reta.pack(anchor=tk.W)

        # Tabela (Treeview) mais compacta
        scroll = tk.Scrollbar(frame_resultado)
        scroll.pack(side=tk.RIGHT, fill=tk.Y)

        self.tree_coords = ttk.Treeview(frame_resultado, columns=("P", "X", "Y"), show="headings",
                                        yscrollcommand=scroll.set, height=8)
        self.tree_coords.heading("P", text="P")
        self.tree_coords.heading("X", text="X")
        self.tree_coords.heading("Y", text="Y")
        self.tree_coords.column("P", width=40, anchor=tk.CENTER)
        self.tree_coords.column("X", width=50, anchor=tk.CENTER)
        self.tree_coords.column("Y", width=50, anchor=tk.CENTER)
        self.tree_coords.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scroll.config(command=self.tree_coords.yview)

        # Botão Limpar solto embaixo
        tk.Button(self.left_panel, text="Limpa Tela", command=self.limpar_tela).pack(fill=tk.X, pady=5)

        # ==========================================
        # ÁREA CENTRAL: Canvas
        # ==========================================
        self.center_panel = tk.Frame(self, relief=tk.SUNKEN, bd=2)
        self.center_panel.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.canvas = tk.Canvas(self.center_panel, bg="white")
        self.canvas.pack(fill=tk.BOTH, expand=True)

        self.canvas.bind("<Motion>", self.mouse_move)
        self.canvas.bind("<Button-1>", self.mouse_click)
        # Redesenha os eixos se a janela mudar de tamanho
        self.canvas.bind("<Configure>", lambda e: self.desenhar_eixos())

    # ==========================================
    # Funções de Tela e Conversão
    # ==========================================
    def converter_para_logico(self, cx, cy):
        w, h = self.canvas.winfo_width(), self.canvas.winfo_height()
        w = w if w > 1 else 600
        h = h if h > 1 else 500
        return cx - w // 2, h // 2 - cy

    def converter_para_tela(self, x, y):
        w, h = self.canvas.winfo_width(), self.canvas.winfo_height()
        return x + w // 2, h // 2 - y

    def desenhar_eixos(self):
        self.canvas.delete("eixos")
        w = self.canvas.winfo_width()
        h = self.canvas.winfo_height()
        if w < 10 or h < 10: return

        # Eixo Y e Eixo X (Cores inspiradas na foto)
        self.canvas.create_line(w // 2, 0, w // 2, h, fill="darkred", tags="eixos")
        self.canvas.create_line(0, h // 2, w, h // 2, fill="darkgray", tags="eixos")

    def desenhar_pixel(self, x, y):
        cx, cy = self.converter_para_tela(x, y)
        self.canvas.create_rectangle(cx, cy, cx + 1, cy + 1, fill="black", outline="black", tags="reta")

    # ==========================================
    # Eventos (mesma lógica do anterior)
    # ==========================================
    def mouse_move(self, event):
        x, y = self.converter_para_logico(event.x, event.y)
        self.lbl_coord_live.config(text=f"Coord: ({x}, {y})")

    def mouse_click(self, event):
        x, y = self.converter_para_logico(event.x, event.y)
        if self.click_count == 0:
            self.ponto_inicial = (x, y)
            self.entradas["X Inicial:"].delete(0, tk.END)
            self.entradas["X Inicial:"].insert(0, str(x))
            self.entradas["Y Inicial:"].delete(0, tk.END)
            self.entradas["Y Inicial:"].insert(0, str(y))
            self.click_count = 1
        else:
            self.entradas["X Final:"].delete(0, tk.END)
            self.entradas["X Final:"].insert(0, str(x))
            self.entradas["Y Final:"].delete(0, tk.END)
            self.entradas["Y Final:"].insert(0, str(y))
            self.click_count = 0
            self.executar_algoritmo()

    def desenhar_via_input(self):
        try:
            int(self.entradas["X Inicial:"].get())
            int(self.entradas["Y Inicial:"].get())
            int(self.entradas["X Final:"].get())
            int(self.entradas["Y Final:"].get())
            self.executar_algoritmo()
        except ValueError:
            pass

    def executar_algoritmo(self):
        x1 = int(self.entradas["X Inicial:"].get())
        y1 = int(self.entradas["Y Inicial:"].get())
        x2 = int(self.entradas["X Final:"].get())
        y2 = int(self.entradas["Y Final:"].get())

        self.lbl_info_reta.config(text=f"X1: {x1} Y1: {y1} | X2: {x2} Y2: {y2}")

        if self.combo_algoritmo.get() == "DDA":
            pontos = dda(x1, y1, x2, y2)
        else:
            pontos = ponto_medio(x1, y1, x2, y2)

        for item in self.tree_coords.get_children():
            self.tree_coords.delete(item)

        for i, (px, py) in enumerate(pontos):
            self.desenhar_pixel(px, py)
            self.tree_coords.insert("", "end", values=(f"P{i}", px, py))

    def limpar_tela(self):
        self.canvas.delete("reta")
        self.click_count = 0
        self.lbl_info_reta.config(text="Nenhuma reta")
        for entry in self.entradas.values():
            entry.delete(0, tk.END)
        for item in self.tree_coords.get_children():
            self.tree_coords.delete(item)
