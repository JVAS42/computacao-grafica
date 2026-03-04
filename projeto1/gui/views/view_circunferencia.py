# gui/views/view_circunferencia.py
import math
import tkinter as tk
from tkinter import ttk, messagebox
from algoritmos.circunferencia import equacao_explicita, trigonometrico, ponto_medio


class ViewCircunferencia(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent)
        self.pack(fill=tk.BOTH, expand=True)

        self.click_count = 0
        self.cx = 0
        self.cy = 0

        self.setup_ui()
        self.desenhar_eixos()

    def setup_ui(self):
        # ==========================================
        # PAINEL ESQUERDO (Estilo Desktop Clássico)
        # ==========================================
        self.left_panel = tk.Frame(self, width=280, padx=5, pady=5)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)
        self.left_panel.pack_propagate(False)

        # --- Grupo 1: Informações em Tempo Real ---
        frame_live = tk.LabelFrame(self.left_panel, text=" Tempo Real ", padx=5, pady=5)
        frame_live.pack(fill=tk.X, pady=5)

        self.lbl_coord_live = tk.Label(frame_live, text="Coord: (0, 0)\nQuad: Origem", justify=tk.LEFT)
        self.lbl_coord_live.pack(anchor=tk.W)

        # --- Grupo 2: Entrada de Dados ---
        frame_entrada = tk.LabelFrame(self.left_panel, text=" Desenhar Circunferência ", padx=5, pady=5)
        frame_entrada.pack(fill=tk.X, pady=5)

        tk.Label(frame_entrada, text="Algoritmo:").pack(anchor=tk.W)
        self.combo_algoritmo = ttk.Combobox(frame_entrada,
                                            values=["Equação Explícita", "Trigonométrico", "Ponto Médio"],
                                            state="readonly")
        self.combo_algoritmo.current(2)  # Padrão: Ponto médio
        self.combo_algoritmo.pack(fill=tk.X, pady=(0, 10))
        self.combo_algoritmo.bind("<<ComboboxSelected>>", lambda e: self.limpar_tela())

        # Inputs de Centro (X, Y) e Raio
        self.entradas = {}
        for campo in ["X:", "Y:", "Raio:"]:
            row = tk.Frame(frame_entrada)
            row.pack(fill=tk.X, pady=2)
            tk.Label(row, text=campo, width=5, anchor=tk.W).pack(side=tk.LEFT)
            entry = tk.Entry(row, width=10)
            entry.pack(side=tk.LEFT, padx=5)
            self.entradas[campo] = entry

        tk.Button(frame_entrada, text="Desenhar", command=self.desenhar_via_input).pack(fill=tk.X, pady=(10, 0))

        # --- Grupo 3: Resultados ---
        frame_resultado = tk.LabelFrame(self.left_panel, text=" Dados da Circunferência ", padx=5, pady=5)
        frame_resultado.pack(fill=tk.BOTH, expand=True, pady=5)

        self.lbl_info_circ = tk.Label(frame_resultado, text="Nenhuma desenhada", font=("Arial", 8))
        self.lbl_info_circ.pack(anchor=tk.W, pady=(0, 5))

        # Tabela (Treeview) para os pontos calculados
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

        # Botão Limpar
        tk.Button(self.left_panel, text="Limpar Tela", command=self.limpar_tela).pack(fill=tk.X, pady=5)

        # ==========================================
        # ÁREA CENTRAL: Canvas
        # ==========================================
        self.center_panel = tk.Frame(self, relief=tk.SUNKEN, bd=2)
        self.center_panel.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.canvas = tk.Canvas(self.center_panel, bg="white")
        self.canvas.pack(fill=tk.BOTH, expand=True)

        self.canvas.bind("<Motion>", self.mouse_move)
        self.canvas.bind("<Button-1>", self.mouse_click)
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
        self.canvas.create_line(w // 2, 0, w // 2, h, fill="darkred", tags="eixos")
        self.canvas.create_line(0, h // 2, w, h // 2, fill="darkgray", tags="eixos")

    def desenhar_pixel(self, x, y):
        cx, cy = self.converter_para_tela(x, y)
        self.canvas.create_rectangle(cx, cy, cx + 1, cy + 1, fill="black", outline="black", tags="circulo")

    # ==========================================
    # Eventos de Interação
    # ==========================================
    def mouse_move(self, event):
        x, y = self.converter_para_logico(event.x, event.y)
        self.lbl_coord_live.config(text=f"Coord: ({x}, {y})")

    def mouse_click(self, event):
        x, y = self.converter_para_logico(event.x, event.y)

        if self.click_count == 0:
            # Primeiro clique define o centro
            self.cx, self.cy = x, y
            self.entradas["X:"].delete(0, tk.END)
            self.entradas["X:"].insert(0, str(x))
            self.entradas["Y:"].delete(0, tk.END)
            self.entradas["Y:"].insert(0, str(y))
            self.click_count = 1
        else:
            # Segundo clique define o raio pela distância Euclidiana
            raio = round(math.sqrt((x - self.cx) ** 2 + (y - self.cy) ** 2))
            self.entradas["Raio:"].delete(0, tk.END)
            self.entradas["Raio:"].insert(0, str(raio))
            self.click_count = 0
            self.executar_algoritmo()

    def desenhar_via_input(self):
        try:
            int(self.entradas["X:"].get())
            int(self.entradas["Y:"].get())
            int(self.entradas["Raio:"].get())
            self.executar_algoritmo()
        except ValueError:
            messagebox.showwarning("Aviso", "Preencha X, Y e Raio com números inteiros.")

    def executar_algoritmo(self):
        xc = int(self.entradas["X:"].get())
        yc = int(self.entradas["Y:"].get())
        raio = int(self.entradas["Raio:"].get())

        self.lbl_info_circ.config(text=f"Centro: ({xc}, {yc}) | Raio: {raio}")

        algoritmo = self.combo_algoritmo.get()
        if algoritmo == "Equação Explícita":
            pontos = equacao_explicita(xc, yc, raio)
        elif algoritmo == "Trigonométrico":
            pontos = trigonometrico(xc, yc, raio)
        else:
            pontos = ponto_medio(xc, yc, raio)

        # Limpa tabela lateral
        for item in self.tree_coords.get_children():
            self.tree_coords.delete(item)

        # Plota na tela e adiciona na tabela
        for i, (px, py) in enumerate(pontos):
            self.desenhar_pixel(px, py)
            self.tree_coords.insert("", "end", values=(f"P{i + 1}", px, py))

    def limpar_tela(self):
        self.canvas.delete("circulo")
        self.click_count = 0
        self.lbl_info_circ.config(text="Nenhuma desenhada")
        for entry in self.entradas.values():
            entry.delete(0, tk.END)
        for item in self.tree_coords.get_children():
            self.tree_coords.delete(item)
