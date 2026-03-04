# gui/views/view_3d.py
import tkinter as tk
from tkinter import ttk, messagebox
from algoritmos.transformacoes_3d import *
from algoritmos.retas import dda, ponto_medio


class View3D(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent)
        self.pack(fill=tk.BOTH, expand=True)

        self.vertices_objeto = []
        self.arestas_objeto = []
        self.historico = []

        self.setup_ui()
        self.desenhar_eixos()

    def setup_ui(self):
        # ==========================================
        # PAINEL ESQUERDO ÚNICO (Estilo Imagem)
        # ==========================================
        self.left_panel = tk.Frame(self, width=280, padx=5, pady=5)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)
        self.left_panel.pack_propagate(False)

        canvas_left = tk.Canvas(self.left_panel, highlightthickness=0)
        scrollbar_left = tk.Scrollbar(self.left_panel, orient="vertical", command=canvas_left.yview)
        scrollable_frame = tk.Frame(canvas_left)

        scrollable_frame.bind("<Configure>", lambda e: canvas_left.configure(scrollregion=canvas_left.bbox("all")))
        canvas_left.create_window((0, 0), window=scrollable_frame, anchor="nw", width=250)
        canvas_left.configure(yscrollcommand=scrollbar_left.set)

        canvas_left.pack(side="left", fill="both", expand=True)
        scrollbar_left.pack(side="right", fill="y")

        self.entradas = {}

        # --- GERAÇÃO DO CUBO ---
        lf_gerar = tk.LabelFrame(scrollable_frame, text=" Desenha Cubo ", font=("Arial", 8, "bold"), labelanchor="n",
                                 padx=5, pady=5)
        lf_gerar.pack(fill=tk.X, pady=5, padx=2)

        row_alg = tk.Frame(lf_gerar)
        row_alg.pack(pady=2)
        tk.Label(row_alg, text="Algoritmo Reta:", font=("Arial", 8)).pack(side=tk.LEFT)
        self.combo_algoritmo = ttk.Combobox(row_alg, values=["DDA", "Ponto Médio"], state="readonly", width=10)
        self.combo_algoritmo.current(0)
        self.combo_algoritmo.pack(side=tk.LEFT, padx=5)

        row_tam = tk.Frame(lf_gerar)
        row_tam.pack(pady=2)
        tk.Label(row_tam, text="Tamanho:", font=("Arial", 8)).pack(side=tk.LEFT)
        ent_tam = tk.Entry(row_tam, width=5)
        ent_tam.insert(0, "50")
        ent_tam.pack(side=tk.LEFT, padx=5)
        self.entradas["Cubo_Tam"] = ent_tam

        tk.Button(lf_gerar, text="Gerar Cubo", command=self.gerar_cubo).pack(fill=tk.X, pady=(5, 0))

        # --- TRANSLAÇÃO ---
        self.criar_bloco_xyz(scrollable_frame, "TRANSLAÇÃO", ["X", "Y", "Z"], self.aplicar_translacao)

        # --- ESCALA ---
        self.criar_bloco_xyz(scrollable_frame, "ESCALA", ["X", "Y", "Z"], self.aplicar_escala, def_vals=["1", "1", "1"])

        # --- REFLEXÃO ---
        lf_ref = tk.LabelFrame(scrollable_frame, text=" REFLEXÃO ", font=("Arial", 8, "bold"), labelanchor="n", padx=5,
                               pady=5)
        lf_ref.pack(fill=tk.X, pady=5, padx=2)
        self.var_reflexao = tk.StringVar(value="xy")
        row_ref = tk.Frame(lf_ref)
        row_ref.pack()
        tk.Radiobutton(row_ref, text="em XY", variable=self.var_reflexao, value="xy", font=("Arial", 8)).pack(
            side=tk.LEFT)
        tk.Radiobutton(row_ref, text="em YZ", variable=self.var_reflexao, value="yz", font=("Arial", 8)).pack(
            side=tk.LEFT)
        tk.Radiobutton(row_ref, text="em XZ", variable=self.var_reflexao, value="xz", font=("Arial", 8)).pack(
            side=tk.LEFT)
        tk.Button(lf_ref, text="Reflexão", command=self.aplicar_reflexao).pack(fill=tk.X, pady=(5, 0))

        # --- ROTAÇÃO ---
        lf_rot = tk.LabelFrame(scrollable_frame, text=" ROTAÇÃO ", font=("Arial", 8, "bold"), labelanchor="n", padx=5,
                               pady=5)
        lf_rot.pack(fill=tk.X, pady=5, padx=2)
        self.var_rotacao_eixo = tk.StringVar(value="x")

        row_rot_1 = tk.Frame(lf_rot)
        row_rot_1.pack(fill=tk.X)
        col_radios = tk.Frame(row_rot_1)
        col_radios.pack(side=tk.LEFT)
        tk.Radiobutton(col_radios, text="eixo X", variable=self.var_rotacao_eixo, value="x", font=("Arial", 8)).pack(
            anchor=tk.W)
        tk.Radiobutton(col_radios, text="eixo Y", variable=self.var_rotacao_eixo, value="y", font=("Arial", 8)).pack(
            anchor=tk.W)
        tk.Radiobutton(col_radios, text="eixo Z", variable=self.var_rotacao_eixo, value="z", font=("Arial", 8)).pack(
            anchor=tk.W)

        col_ang = tk.Frame(row_rot_1)
        col_ang.pack(side=tk.RIGHT, padx=10)
        tk.Label(col_ang, text="Ângulo", font=("Arial", 8)).pack()
        ent_ang = tk.Entry(col_ang, width=6)
        ent_ang.insert(0, "0")
        ent_ang.pack()
        self.entradas["Rot_Angulo"] = ent_ang
        tk.Button(lf_rot, text="Rotação", command=self.aplicar_rotacao).pack(fill=tk.X, pady=(5, 0))

        # --- CISALHAMENTO ---
        self.criar_bloco_xyz(scrollable_frame, "CISALHAMENTO", ["XY", "XZ", "YZ"], self.aplicar_cisalhamento)

        # --- BOTÕES FINAIS ---
        tk.Button(scrollable_frame, text="Limpa Tela", command=self.limpar_tudo).pack(fill=tk.X, pady=(10, 2), padx=2)
        tk.Button(scrollable_frame, text="Animação",
                  command=lambda: messagebox.showinfo("Info", "Animação em breve.")).pack(fill=tk.X, pady=(0, 10),
                                                                                          padx=2)

        # --- INFORMAÇÕES DO OBJETO ---
        ttk.Separator(scrollable_frame, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=5)
        tk.Label(scrollable_frame, text="Vértices:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lbl_vertices = tk.Label(scrollable_frame, text="Nenhum.", bg="white", relief=tk.SUNKEN, justify=tk.LEFT,
                                     height=5, anchor="nw", font=("Arial", 8))
        self.lbl_vertices.pack(fill=tk.X, pady=(0, 5), padx=2)

        # ==========================================
        # PAINEL CENTRAL (Canvas)
        # ==========================================
        self.center_panel = tk.Frame(self, relief=tk.SUNKEN, bd=2)
        self.center_panel.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.canvas = tk.Canvas(self.center_panel, bg="white")
        self.canvas.pack(fill=tk.BOTH, expand=True)
        self.canvas.bind("<Configure>", lambda e: self.redesenhar_tudo())

    def criar_bloco_xyz(self, parent, titulo, campos, comando, def_vals=None):
        lf = tk.LabelFrame(parent, text=f" {titulo} ", font=("Arial", 8, "bold"), labelanchor="n", padx=5, pady=5)
        lf.pack(fill=tk.X, pady=5, padx=2)

        row = tk.Frame(lf)
        row.pack(pady=2)

        for i, campo in enumerate(campos):
            tk.Label(row, text=campo, font=("Arial", 8)).pack(side=tk.LEFT)
            val = def_vals[i] if def_vals else "0"
            ent = tk.Entry(row, width=4)
            ent.insert(0, val)
            ent.pack(side=tk.LEFT, padx=(0, 4))
            self.entradas[f"{titulo}_{campo}"] = ent

        tk.Button(lf, text=titulo.capitalize(), command=comando).pack(fill=tk.X, pady=(5, 0))

    # ==========================================
    # Funções de Desenho (Nível de Pixel)
    # ==========================================
    def desenhar_eixos(self):
        w, h = self.canvas.winfo_width(), self.canvas.winfo_height()
        if w < 10: return

        # Origem
        ox, oy = projetar_3d_para_2d(0, 0, 0, w, h)

        # Fim dos eixos
        tamanho = 200
        xx, xy = projetar_3d_para_2d(tamanho, 0, 0, w, h)
        yx, yy = projetar_3d_para_2d(0, tamanho, 0, w, h)
        zx, zy = projetar_3d_para_2d(0, 0, tamanho, w, h)

        self.canvas.create_line(ox, oy, xx, xy, fill="red", tags="eixos")
        self.canvas.create_line(ox, oy, yx, yy, fill="green", tags="eixos")
        self.canvas.create_line(ox, oy, zx, zy, fill="blue", tags="eixos")

        self.canvas.create_text(xx + 10, xy, text="X", fill="red", tags="eixos")
        self.canvas.create_text(yx, yy - 10, text="Y", fill="green", tags="eixos")
        self.canvas.create_text(zx - 10, zy, text="Z", fill="blue", tags="eixos")

    def desenhar_pixel(self, x, y, cor="black"):
        self.canvas.create_rectangle(x, y, x + 1, y + 1, fill=cor, outline=cor, tags="objeto")

    def redesenhar_tudo(self):
        self.canvas.delete("all")
        self.desenhar_eixos()

        if not self.vertices_objeto: return

        w, h = self.canvas.winfo_width(), self.canvas.winfo_height()
        algoritmo = self.combo_algoritmo.get()

        # Projeta todos os vértices 3D para 2D
        vertices_2d = []
        for v in self.vertices_objeto:
            px, py = projetar_3d_para_2d(v[0], v[1], v[2], w, h)
            vertices_2d.append((px, py))

        # Desenha as arestas unindo os pontos 2D
        for aresta in self.arestas_objeto:
            p1 = vertices_2d[aresta[0]]
            p2 = vertices_2d[aresta[1]]

            if algoritmo == "DDA":
                pontos = dda(p1[0], p1[1], p2[0], p2[1])
            else:
                pontos = ponto_medio(p1[0], p1[1], p2[0], p2[1])

            for px, py in pontos:
                self.desenhar_pixel(px, py, cor="#333")

        self.atualizar_informacoes()

    def atualizar_informacoes(self):
        if not self.vertices_objeto:
            self.lbl_vertices.config(text="Nenhum.")
            return
        txt = ""
        for i, v in enumerate(self.vertices_objeto[:4]):  # Mostra os 4 primeiros para caber
            txt += f"V{i}: ({v[0]:.1f}, {v[1]:.1f}, {v[2]:.1f})\n"
        txt += "... (8 vértices)"
        self.lbl_vertices.config(text=txt)

    def limpar_tudo(self):
        self.vertices_objeto = []
        self.arestas_objeto = []
        self.redesenhar_tudo()

    # ==========================================
    # Lógica Geométrica 3D
    # ==========================================
    def gerar_cubo(self):
        try:
            size = int(self.entradas["Cubo_Tam"].get())
            # Vértices idênticos à função createCube do JS
            self.vertices_objeto = [
                [0, 0, 0], [size, 0, 0], [size, size, 0], [0, size, 0],
                [0, 0, size], [size, 0, size], [size, size, size], [0, size, size]
            ]
            self.arestas_objeto = [
                [0, 1], [1, 2], [2, 3], [3, 0],
                [4, 5], [5, 6], [6, 7], [7, 4],
                [0, 4], [1, 5], [2, 6], [3, 7]
            ]
            self.redesenhar_tudo()
        except ValueError:
            messagebox.showerror("Erro", "Tamanho inválido.")

    def aplicar_transformacao(self, matriz):
        if not self.vertices_objeto:
            messagebox.showwarning("Aviso", "Gere o cubo primeiro!")
            return

        novos_vertices = []
        for v in self.vertices_objeto:
            vec4 = [v[0], v[1], v[2], 1]
            resultado = multiplicar_matriz_vetor_4x4(matriz, vec4)
            novos_vertices.append([resultado[0], resultado[1], resultado[2]])

        self.vertices_objeto = novos_vertices
        self.redesenhar_tudo()

    def aplicar_translacao(self):
        tx = float(self.entradas["TRANSLAÇÃO_X"].get())
        ty = float(self.entradas["TRANSLAÇÃO_Y"].get())
        tz = float(self.entradas["TRANSLAÇÃO_Z"].get())
        mat = matriz_translacao_3d(tx, ty, tz)
        self.aplicar_transformacao(mat)

    def aplicar_escala(self):
        sx = float(self.entradas["ESCALA_X"].get())
        sy = float(self.entradas["ESCALA_Y"].get())
        sz = float(self.entradas["ESCALA_Z"].get())

        # Ponto fixo: Vértice 0 (como no JS)
        px, py, pz = self.vertices_objeto[0]
        t1 = matriz_translacao_3d(-px, -py, -pz)
        sc = matriz_escala_3d(sx, sy, sz)
        t2 = matriz_translacao_3d(px, py, pz)

        mat_final = multiplicar_matrizes_4x4(t2, multiplicar_matrizes_4x4(sc, t1))
        self.aplicar_transformacao(mat_final)

    def aplicar_reflexao(self):
        plano = self.var_reflexao.get()
        mat = matriz_reflexao_3d(plano)
        self.aplicar_transformacao(mat)

    def aplicar_rotacao(self):
        eixo = self.var_rotacao_eixo.get()
        ang = float(self.entradas["Rot_Angulo"].get())

        # Ponto fixo: Vértice 0
        px, py, pz = self.vertices_objeto[0]
        t1 = matriz_translacao_3d(-px, -py, -pz)

        if eixo == 'x':
            rot = matriz_rotacao_x(ang)
        elif eixo == 'y':
            rot = matriz_rotacao_y(ang)
        else:
            rot = matriz_rotacao_z(ang)

        t2 = matriz_translacao_3d(px, py, pz)
        mat_final = multiplicar_matrizes_4x4(t2, multiplicar_matrizes_4x4(rot, t1))
        self.aplicar_transformacao(mat_final)

    def aplicar_cisalhamento(self):
        shXY = float(self.entradas["CISALHAMENTO_XY"].get())
        shXZ = float(self.entradas["CISALHAMENTO_XZ"].get())
        shYZ = float(self.entradas["CISALHAMENTO_YZ"].get())
        mat = matriz_cisalhamento_3d(shXY, shXZ, shYZ)
        self.aplicar_transformacao(mat)