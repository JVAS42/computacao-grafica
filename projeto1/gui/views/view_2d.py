import tkinter as tk
from tkinter import ttk, messagebox
from algoritmos.transformacoes_2d import *
from algoritmos.retas import dda, ponto_medio


class View2D(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent)
        self.pack(fill=tk.BOTH, expand=True)

        self.vertices_objeto = []
        self.historico = []

        self.setup_ui()
        self.desenhar_eixos()

    def setup_ui(self):
        # ==========================================
        # PAINEL ESQUERDO ÚNICO (Estilo Imagem de Referência)
        # ==========================================
        self.left_panel = tk.Frame(self, width=260, padx=5, pady=5)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)
        self.left_panel.pack_propagate(False)

        # Canvas e Scrollbar para permitir rolar o painel esquerdo
        canvas_left = tk.Canvas(self.left_panel, highlightthickness=0)
        scrollbar_left = tk.Scrollbar(self.left_panel, orient="vertical", command=canvas_left.yview)
        scrollable_frame = tk.Frame(canvas_left)

        scrollable_frame.bind("<Configure>", lambda e: canvas_left.configure(scrollregion=canvas_left.bbox("all")))
        canvas_left.create_window((0, 0), window=scrollable_frame, anchor="nw", width=230)
        canvas_left.configure(yscrollcommand=scrollbar_left.set)

        canvas_left.pack(side="left", fill="both", expand=True)
        scrollbar_left.pack(side="right", fill="y")

        self.entradas = {}

        # --- GERAÇÃO DO OBJETO ---
        lf_gerar = tk.LabelFrame(scrollable_frame, text=" DESENHA QUADRADO ", font=("Arial", 8, "bold"),
                                 labelanchor="n", padx=5, pady=5)
        lf_gerar.pack(fill=tk.X, pady=5, padx=2)

        row_alg = tk.Frame(lf_gerar)
        row_alg.pack(pady=2)
        tk.Label(row_alg, text="Algoritmo:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.combo_algoritmo = ttk.Combobox(row_alg, values=["DDA", "Ponto Médio"], state="readonly", width=12)
        self.combo_algoritmo.current(0)
        self.combo_algoritmo.pack(side=tk.LEFT, padx=2)

        row_quad = tk.Frame(lf_gerar)
        row_quad.pack(pady=2)
        tk.Label(row_quad, text="Tam:", font=("Arial", 8)).pack(side=tk.LEFT)
        ent_tam = tk.Entry(row_quad, width=4)
        ent_tam.insert(0, "50")
        ent_tam.pack(side=tk.LEFT, padx=(0, 5))

        tk.Label(row_quad, text="X:", font=("Arial", 8)).pack(side=tk.LEFT)
        ent_x = tk.Entry(row_quad, width=4)
        ent_x.insert(0, "0")
        ent_x.pack(side=tk.LEFT, padx=(0, 5))

        tk.Label(row_quad, text="Y:", font=("Arial", 8)).pack(side=tk.LEFT)
        ent_y = tk.Entry(row_quad, width=4)
        ent_y.insert(0, "0")
        ent_y.pack(side=tk.LEFT)

        self.entradas["Quad_Tam"] = ent_tam
        self.entradas["Quad_X"] = ent_x
        self.entradas["Quad_Y"] = ent_y

        tk.Button(lf_gerar, text="Desenha Quadrado", command=self.gerar_quadrado).pack(fill=tk.X, pady=(5, 0))

        # --- TRANSLAÇÃO ---
        self.criar_bloco_transformacao(scrollable_frame, "TRANSLAÇÃO", ["X", "Y"], self.aplicar_translacao)

        # --- ESCALA ---
        self.criar_bloco_transformacao(scrollable_frame, "ESCALA", ["X", "Y"], self.aplicar_escala, ["1", "1"])

        # --- REFLEXÃO ---
        lf_ref = tk.LabelFrame(scrollable_frame, text=" REFLEXÃO ", font=("Arial", 8, "bold"), labelanchor="n", padx=5,
                               pady=5)
        lf_ref.pack(fill=tk.X, pady=5, padx=2)
        row_ref = tk.Frame(lf_ref)
        row_ref.pack(pady=2)
        self.var_ref_x = tk.BooleanVar()
        self.var_ref_y = tk.BooleanVar()
        tk.Checkbutton(row_ref, text="Em X", variable=self.var_ref_x, font=("Arial", 8)).pack(side=tk.LEFT)
        tk.Checkbutton(row_ref, text="Em Y", variable=self.var_ref_y, font=("Arial", 8)).pack(side=tk.LEFT)
        tk.Button(lf_ref, text="Reflexão", command=self.aplicar_reflexao).pack(fill=tk.X, pady=(5, 0))

        # --- ROTAÇÃO ---
        lf_rot = tk.LabelFrame(scrollable_frame, text=" ROTAÇÃO ", font=("Arial", 8, "bold"), labelanchor="n", padx=5,
                               pady=5)
        lf_rot.pack(fill=tk.X, pady=5, padx=2)
        row_rot = tk.Frame(lf_rot)
        row_rot.pack(pady=2)
        tk.Label(row_rot, text="Ângulo", font=("Arial", 8)).pack(side=tk.LEFT)
        ent_ang = tk.Entry(row_rot, width=6)
        ent_ang.insert(0, "0")
        ent_ang.pack(side=tk.LEFT, padx=5)
        self.entradas["ROTAÇÃO_Angulo"] = ent_ang
        tk.Button(lf_rot, text="Rotação", command=self.aplicar_rotacao).pack(fill=tk.X, pady=(5, 0))

        # --- CISALHAMENTO ---
        self.criar_bloco_transformacao(scrollable_frame, "CISALHAMENTO", ["X", "Y"], self.aplicar_cisalhamento)

        # --- BOTÃO LIMPAR TELA (Solto embaixo igual na foto) ---
        tk.Button(scrollable_frame, text="Limpa Tela", command=self.limpar_tudo).pack(fill=tk.X, pady=(10, 5), padx=2)

        ttk.Separator(scrollable_frame, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=5)

        # --- INFORMAÇÕES E HISTÓRICO (Mantendo os dados da interface atual) ---
        tk.Label(scrollable_frame, text="Vértices:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lbl_vertices = tk.Label(scrollable_frame, text="Nenhum.", bg="white", relief=tk.SUNKEN, justify=tk.LEFT,
                                     height=4, anchor="nw", font=("Arial", 8))
        self.lbl_vertices.pack(fill=tk.X, pady=(0, 5), padx=2)

        tk.Label(scrollable_frame, text="Centro:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lbl_centro = tk.Label(scrollable_frame, text="Nenhum.", bg="white", relief=tk.SUNKEN, anchor=tk.W,
                                   font=("Arial", 8))
        self.lbl_centro.pack(fill=tk.X, pady=(0, 10), padx=2)

        tk.Label(scrollable_frame, text="Histórico:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lst_historico = tk.Listbox(scrollable_frame, height=5, font=("Arial", 8))
        self.lst_historico.pack(fill=tk.X, pady=(0, 10), padx=2)

        # ==========================================
        # PAINEL CENTRAL (Canvas)
        # ==========================================
        self.center_panel = tk.Frame(self, relief=tk.SUNKEN, bd=2)
        self.center_panel.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.canvas = tk.Canvas(self.center_panel, bg="white")
        self.canvas.pack(fill=tk.BOTH, expand=True)
        self.canvas.bind("<Configure>", lambda e: self.redesenhar_tudo())

    # --- Método Auxiliar para criar as caixas com visual da foto ---
    def criar_bloco_transformacao(self, parent, titulo, campos, comando, def_vals=None):
        lf = tk.LabelFrame(parent, text=f" {titulo} ", font=("Arial", 8, "bold"), labelanchor="n", padx=5, pady=5)
        lf.pack(fill=tk.X, pady=5, padx=2)

        row = tk.Frame(lf)
        row.pack(pady=2)

        for i, campo in enumerate(campos):
            tk.Label(row, text=campo, font=("Arial", 8)).pack(side=tk.LEFT)
            val = def_vals[i] if def_vals else "0"
            ent = tk.Entry(row, width=5)
            ent.insert(0, val)
            ent.pack(side=tk.LEFT, padx=(0, 5))
            self.entradas[f"{titulo}_{campo}"] = ent

        # Para deixar igual a foto, o texto do botão é só "Translação", "Escala", etc (sem "Aplicar")
        texto_botao = titulo.capitalize()
        tk.Button(lf, text=texto_botao, command=comando).pack(fill=tk.X, pady=(5, 0))

    # ==========================================
    # Funções de Tela e Conversão (Pixel)
    # ==========================================
    def converter_para_tela(self, x, y):
        w, h = self.canvas.winfo_width(), self.canvas.winfo_height()
        return x + w // 2, h // 2 - y

    def desenhar_eixos(self):
        w, h = self.canvas.winfo_width(), self.canvas.winfo_height()
        if w < 10: return
        # Eixos em vermelho escuro e cinza como na foto
        self.canvas.create_line(w // 2, 0, w // 2, h, fill="darkred", tags="eixos")
        self.canvas.create_line(0, h // 2, w, h // 2, fill="darkgray", tags="eixos")

    def desenhar_pixel(self, x, y):
        cx, cy = self.converter_para_tela(x, y)
        self.canvas.create_rectangle(cx, cy, cx + 1, cy + 1, fill="blue", outline="blue", tags="objeto")

    def redesenhar_tudo(self):
        self.canvas.delete("all")
        self.desenhar_eixos()

        if not self.vertices_objeto: return

        algoritmo = self.combo_algoritmo.get()

        # Desenha as 4 arestas pixel a pixel
        for i in range(len(self.vertices_objeto)):
            p1 = self.vertices_objeto[i]
            p2 = self.vertices_objeto[(i + 1) % len(self.vertices_objeto)]

            if algoritmo == "DDA":
                pontos = dda(p1[0], p1[1], p2[0], p2[1])
            else:
                pontos = ponto_medio(p1[0], p1[1], p2[0], p2[1])

            for px, py in pontos:
                self.desenhar_pixel(px, py)

        self.atualizar_informacoes()

    def atualizar_informacoes(self):
        if not self.vertices_objeto:
            self.lbl_vertices.config(text="Nenhum.")
            self.lbl_centro.config(text="Nenhum.")
            return

        cx = sum(p[0] for p in self.vertices_objeto) / len(self.vertices_objeto)
        cy = sum(p[1] for p in self.vertices_objeto) / len(self.vertices_objeto)

        txt_vert = "\n".join([f"V{i + 1}: ({v[0]:.1f}, {v[1]:.1f})" for i, v in enumerate(self.vertices_objeto)])
        self.lbl_vertices.config(text=txt_vert)
        self.lbl_centro.config(text=f"({cx:.2f}, {cy:.2f})")

    def adicionar_historico(self, texto):
        self.historico.append(texto)
        self.lst_historico.insert(tk.END, f"{len(self.historico)}. {texto}")
        self.lst_historico.yview(tk.END)

    def limpar_tudo(self):
        self.vertices_objeto = []
        self.historico = []
        self.lst_historico.delete(0, tk.END)
        self.redesenhar_tudo()

    # ==========================================
    # Lógica Geométrica e Transformações
    # ==========================================
    def gerar_quadrado(self):
        try:
            tam = int(self.entradas["Quad_Tam"].get())
            x = int(self.entradas["Quad_X"].get())
            y = int(self.entradas["Quad_Y"].get())

            self.vertices_objeto = [
                [x, y],
                [x + tam, y],
                [x + tam, y + tam],
                [x, y + tam]
            ]
            self.adicionar_historico("Quadrado Gerado")
            self.redesenhar_tudo()
        except ValueError:
            messagebox.showerror("Erro", "Valores inválidos.")

    def aplicar_transformacao(self, matriz, nome_transformacao):
        if not self.vertices_objeto:
            messagebox.showwarning("Aviso", "Gere um quadrado primeiro!")
            return

        novos_vertices = []
        for v in self.vertices_objeto:
            novo_x, novo_y = multiplicar_matriz_vetor(matriz, v)
            novos_vertices.append([round(novo_x, 2), round(novo_y, 2)])

        self.vertices_objeto = novos_vertices
        self.adicionar_historico(nome_transformacao)
        self.redesenhar_tudo()

    def aplicar_translacao(self):
        dx = float(self.entradas["TRANSLAÇÃO_X"].get())
        dy = float(self.entradas["TRANSLAÇÃO_Y"].get())
        matriz = matriz_translacao(dx, dy)
        self.aplicar_transformacao(matriz, f"Translação: X={dx}, Y={dy}")

    def aplicar_escala(self):
        sx = float(self.entradas["ESCALA_X"].get())
        sy = float(self.entradas["ESCALA_Y"].get())

        # Escala baseada no primeiro vértice
        ox, oy = self.vertices_objeto[0]
        m_t1 = matriz_translacao(-ox, -oy)
        m_esc = matriz_escala(sx, sy)
        m_t2 = matriz_translacao(ox, oy)

        matriz_final = multiplicar_matrizes(m_t2, multiplicar_matrizes(m_esc, m_t1))
        self.aplicar_transformacao(matriz_final, f"Escala: Sx={sx}, Sy={sy}")

    def aplicar_rotacao(self):
        ang = float(self.entradas["ROTAÇÃO_Angulo"].get())
        # Rotação em torno do centro do objeto
        cx = sum(p[0] for p in self.vertices_objeto) / len(self.vertices_objeto)
        cy = sum(p[1] for p in self.vertices_objeto) / len(self.vertices_objeto)

        matriz = matriz_rotacao(ang, cx, cy)
        self.aplicar_transformacao(matriz, f"Rotação: {ang}°")

    def aplicar_reflexao(self):
        rx = self.var_ref_x.get()
        ry = self.var_ref_y.get()
        if not rx and not ry: return
        matriz = matriz_reflexao(rx, ry)
        self.aplicar_transformacao(matriz, f"Reflexão: {'X' if rx else ''} {'Y' if ry else ''}")

    def aplicar_cisalhamento(self):
        shx = float(self.entradas["CISALHAMENTO_X"].get())
        shy = float(self.entradas["CISALHAMENTO_Y"].get())
        matriz = matriz_cisalhamento(shx, shy)
        self.aplicar_transformacao(matriz, f"Cisalhamento: X={shx}, Y={shy}")
