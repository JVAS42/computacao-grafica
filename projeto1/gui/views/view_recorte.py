import tkinter as tk
from tkinter import ttk, messagebox
from algoritmos.recorte import recortar_cohen_sutherland
from algoritmos.retas import dda, ponto_medio


class ViewRecorte(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent)
        self.pack(fill=tk.BOTH, expand=True)

        # Estado inicial da janela de recorte
        self.xmin_val = 150
        self.ymin_val = 100
        self.xmax_val = 450
        self.ymax_val = 300

        self.linhas = []
        self.click_buffer = []
        self.show_only_clipped = False

        self.setup_ui()
        self.redesenhar_tudo()

    def setup_ui(self):
        # ==========================================
        # PAINEL ESQUERDO ÚNICO (Estilo Desktop)
        # ==========================================
        self.left_panel = tk.Frame(self, width=280, padx=5, pady=5)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)
        self.left_panel.pack_propagate(False)

        # Canvas e Scrollbar para permitir rolar o painel esquerdo
        canvas_left = tk.Canvas(self.left_panel, highlightthickness=0)
        scrollbar_left = tk.Scrollbar(self.left_panel, orient="vertical", command=canvas_left.yview)
        scrollable_frame = tk.Frame(canvas_left)

        scrollable_frame.bind("<Configure>", lambda e: canvas_left.configure(scrollregion=canvas_left.bbox("all")))
        canvas_left.create_window((0, 0), window=scrollable_frame, anchor="nw", width=250)
        canvas_left.configure(yscrollcommand=scrollbar_left.set)

        canvas_left.pack(side="left", fill="both", expand=True)
        scrollbar_left.pack(side="right", fill="y")

        self.entradas = {}

        # --- JANELA DE RECORTE ---
        lf_janela = tk.LabelFrame(scrollable_frame, text=" Janela de Recorte ", font=("Arial", 8, "bold"),
                                  labelanchor="n", padx=5, pady=5)
        lf_janela.pack(fill=tk.X, pady=5, padx=2)

        row_vp1 = tk.Frame(lf_janela)
        row_vp1.pack(pady=2)
        tk.Label(row_vp1, text="Xmin:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["Xmin"] = tk.Entry(row_vp1, width=5)
        self.entradas["Xmin"].insert(0, str(self.xmin_val))
        self.entradas["Xmin"].pack(side=tk.LEFT, padx=(0, 5))

        tk.Label(row_vp1, text="Ymin:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["Ymin"] = tk.Entry(row_vp1, width=5)
        self.entradas["Ymin"].insert(0, str(self.ymin_val))
        self.entradas["Ymin"].pack(side=tk.LEFT)

        row_vp2 = tk.Frame(lf_janela)
        row_vp2.pack(pady=2)
        tk.Label(row_vp2, text="Xmax:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["Xmax"] = tk.Entry(row_vp2, width=5)
        self.entradas["Xmax"].insert(0, str(self.xmax_val))
        self.entradas["Xmax"].pack(side=tk.LEFT, padx=(0, 5))

        tk.Label(row_vp2, text="Ymax:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["Ymax"] = tk.Entry(row_vp2, width=5)
        self.entradas["Ymax"].insert(0, str(self.ymax_val))
        self.entradas["Ymax"].pack(side=tk.LEFT)

        tk.Button(lf_janela, text="Atualizar Janela", command=self.atualizar_limites).pack(fill=tk.X, pady=(5, 0))

        # --- DESENHAR LINHA ---
        lf_linha = tk.LabelFrame(scrollable_frame, text=" Adicionar Linha ", font=("Arial", 8, "bold"), labelanchor="n",
                                 padx=5, pady=5)
        lf_linha.pack(fill=tk.X, pady=5, padx=2)

        row_alg = tk.Frame(lf_linha)
        row_alg.pack(pady=2)
        tk.Label(row_alg, text="Algoritmo:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.combo_algoritmo = ttk.Combobox(row_alg, values=["DDA", "Ponto Médio"], state="readonly", width=12)
        self.combo_algoritmo.current(0)
        self.combo_algoritmo.pack(side=tk.LEFT, padx=5)
        self.combo_algoritmo.bind("<<ComboboxSelected>>", lambda e: self.redesenhar_tudo())

        row_l1 = tk.Frame(lf_linha)
        row_l1.pack(pady=2)
        tk.Label(row_l1, text="X1:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["X1"] = tk.Entry(row_l1, width=5)
        self.entradas["X1"].pack(side=tk.LEFT, padx=(0, 5))
        tk.Label(row_l1, text="Y1:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["Y1"] = tk.Entry(row_l1, width=5)
        self.entradas["Y1"].pack(side=tk.LEFT)

        row_l2 = tk.Frame(lf_linha)
        row_l2.pack(pady=2)
        tk.Label(row_l2, text="X2:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["X2"] = tk.Entry(row_l2, width=5)
        self.entradas["X2"].pack(side=tk.LEFT, padx=(0, 5))
        tk.Label(row_l2, text="Y2:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.entradas["Y2"] = tk.Entry(row_l2, width=5)
        self.entradas["Y2"].pack(side=tk.LEFT)

        tk.Button(lf_linha, text="Desenhar Linha", command=self.adicionar_linha_manual).pack(fill=tk.X, pady=(5, 0))

        # --- AÇÕES ---
        lf_acoes = tk.LabelFrame(scrollable_frame, text=" Ações ", font=("Arial", 8, "bold"), labelanchor="n", padx=5,
                                 pady=5)
        lf_acoes.pack(fill=tk.X, pady=5, padx=2)

        tk.Button(lf_acoes, text="Aplicar Recorte", bg="#4CAF50", fg="white", command=self.aplicar_recorte).pack(
            fill=tk.X, pady=2)
        tk.Button(lf_acoes, text="Limpar e Resetar", bg="#F44336", fg="white", command=self.resetar_valores).pack(
            fill=tk.X, pady=2)

        # --- INFORMAÇÕES E HISTÓRICO ---
        ttk.Separator(scrollable_frame, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=10)

        tk.Label(scrollable_frame, text="Leituras do Mouse:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lbl_coord_live = tk.Label(scrollable_frame, text="Posição: (0, 0)", font=("Arial", 8))
        self.lbl_coord_live.pack(anchor=tk.W)
        self.lbl_clicked_coords = tk.Label(scrollable_frame, text="Último Clique: Nenhum", font=("Arial", 8))
        self.lbl_clicked_coords.pack(anchor=tk.W, pady=(0, 10))

        tk.Label(scrollable_frame, text="Histórico de Recorte:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.txt_historico = tk.Text(scrollable_frame, height=8, font=("Arial", 8), bg="#f9f9f9", wrap=tk.WORD)
        self.txt_historico.insert(tk.END, "Nenhum histórico.")
        self.txt_historico.config(state=tk.DISABLED)
        self.txt_historico.pack(fill=tk.X, pady=(0, 5), padx=2)

        # ==========================================
        # PAINEL CENTRAL (Canvas)
        # ==========================================
        self.center_panel = tk.Frame(self, relief=tk.SUNKEN, bd=2)
        self.center_panel.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.canvas = tk.Canvas(self.center_panel, bg="white")
        self.canvas.pack(fill=tk.BOTH, expand=True)

        self.canvas.bind("<Motion>", self.mouse_move)
        self.canvas.bind("<Button-1>", self.mouse_click)

    # ==========================================
    # Lógica de Atualização e Interação
    # ==========================================
    def atualizar_limites(self):
        try:
            self.xmin_val = int(self.entradas["Xmin"].get())
            self.ymin_val = int(self.entradas["Ymin"].get())
            self.xmax_val = int(self.entradas["Xmax"].get())
            self.ymax_val = int(self.entradas["Ymax"].get())
            self.redesenhar_tudo()
        except ValueError:
            messagebox.showerror("Erro", "Valores numéricos inválidos para a janela.")

    def resetar_valores(self):
        self.xmin_val, self.ymin_val = 150, 100
        self.xmax_val, self.ymax_val = 450, 300

        self.entradas["Xmin"].delete(0, tk.END);
        self.entradas["Xmin"].insert(0, str(self.xmin_val))
        self.entradas["Ymin"].delete(0, tk.END);
        self.entradas["Ymin"].insert(0, str(self.ymin_val))
        self.entradas["Xmax"].delete(0, tk.END);
        self.entradas["Xmax"].insert(0, str(self.xmax_val))
        self.entradas["Ymax"].delete(0, tk.END);
        self.entradas["Ymax"].insert(0, str(self.ymax_val))

        self.linhas = []
        self.click_buffer = []
        self.show_only_clipped = False

        self.txt_historico.config(state=tk.NORMAL)
        self.txt_historico.delete(1.0, tk.END)
        self.txt_historico.insert(tk.END, "Nenhum histórico.")
        self.txt_historico.config(state=tk.DISABLED)

        self.redesenhar_tudo()

    def aplicar_recorte(self):
        self.show_only_clipped = True
        self.redesenhar_tudo()

    def mouse_move(self, event):
        self.lbl_coord_live.config(text=f"Posição: ({event.x}, {event.y})")

    def mouse_click(self, event):
        self.lbl_clicked_coords.config(text=f"Último Clique: ({event.x}, {event.y})")
        self.click_buffer.append((event.x, event.y))

        if len(self.click_buffer) == 2:
            p1, p2 = self.click_buffer[0], self.click_buffer[1]
            self.linhas.append((p1[0], p1[1], p2[0], p2[1]))
            self.click_buffer = []
            self.redesenhar_tudo()

    def adicionar_linha_manual(self):
        try:
            x1 = int(self.entradas["X1"].get())
            y1 = int(self.entradas["Y1"].get())
            x2 = int(self.entradas["X2"].get())
            y2 = int(self.entradas["Y2"].get())

            self.linhas.append((x1, y1, x2, y2))

            for key in ["X1", "Y1", "X2", "Y2"]:
                self.entradas[key].delete(0, tk.END)

            self.redesenhar_tudo()
        except ValueError:
            messagebox.showwarning("Aviso", "Preencha as coordenadas da linha.")

    # ==========================================
    # Desenho Nível de Pixel e Recorte
    # ==========================================
    def desenhar_pixel(self, x, y, cor="black"):
        # Garante o desenho exato de 1 pixel de tamanho
        self.canvas.create_rectangle(x, y, x + 1, y + 1, fill=cor, outline=cor)

    def traçar_linha(self, x1, y1, x2, y2, cor):
        algoritmo = self.combo_algoritmo.get()
        if algoritmo == "DDA":
            pontos = dda(x1, y1, x2, y2)
        else:
            pontos = ponto_medio(x1, y1, x2, y2)

        # Pinta cada pixel calculado individualmente na tela
        for px, py in pontos:
            self.desenhar_pixel(px, py, cor=cor)

    def atualizar_historico(self, idx, result):
        texto = f"Linha {idx + 1}:\n"
        if result['accept']:
            texto += f"  Aceito: ({result['x1']}, {result['y1']}) -> ({result['x2']}, {result['y2']})\n"
        else:
            texto += "  Totalmente Rejeitada\n"

        for step in result['steps'][1:]:
            texto += f"  -> {step['action']}\n"
        texto += "-" * 20 + "\n"

        self.txt_historico.config(state=tk.NORMAL)
        if idx == 0: self.txt_historico.delete(1.0, tk.END)
        self.txt_historico.insert(tk.END, texto)
        self.txt_historico.see(tk.END)
        self.txt_historico.config(state=tk.DISABLED)

    def redesenhar_tudo(self):
        self.canvas.delete("all")

        # Desenha a borda da janela de recorte (área azul)
        self.canvas.create_rectangle(self.xmin_val, self.ymin_val, self.xmax_val, self.ymax_val, outline="blue",
                                     width=2)

        if not self.linhas: return

        for idx, (x1, y1, x2, y2) in enumerate(self.linhas):
            result = recortar_cohen_sutherland(x1, y1, x2, y2, self.xmin_val, self.xmax_val, self.ymin_val,
                                               self.ymax_val)
            self.atualizar_historico(idx, result)

            # Se NÃO estiver no modo "apenas recortado", desenha a linha rejeitada (fora da janela) em vermelho
            if not self.show_only_clipped:
                self.traçar_linha(x1, y1, x2, y2, "red")

            # Se a linha cruzar ou estiver na janela, desenha a porção válida em verde por cima
            if result['accept']:
                self.traçar_linha(result['x1'], result['y1'], result['x2'], result['y2'], "green")