import tkinter as tk
from tkinter import ttk, messagebox
from algoritmos.coordenadas import inp_to_ndc, ndc_to_wd, wd_to_ndc_central


class ViewCoordenadas(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent)
        self.pack(fill=tk.BOTH, expand=True)

        self.xmax = 100.3
        self.xmin = 10.5
        self.ymax = 100.4
        self.ymin = 15.2

        self.setup_ui()

    def setup_ui(self):
        # ==========================================
        # PAINEL ESQUERDO
        # ==========================================
        self.left_panel = tk.Frame(self, width=280, padx=5, pady=5)
        self.left_panel.pack(side=tk.LEFT, fill=tk.Y)
        self.left_panel.pack_propagate(False)

        # --- Grupo 1: Limites ---
        frame_limites = tk.LabelFrame(self.left_panel, text=" Limites do Mundo ", padx=5, pady=5)
        frame_limites.pack(fill=tk.X, pady=5)

        self.entradas_limites = {}
        for limite in ["Xmax:", "Xmin:", "Ymax:", "Ymin:"]:
            row = tk.Frame(frame_limites)
            row.pack(fill=tk.X, pady=2)
            tk.Label(row, text=limite, width=6, anchor=tk.W).pack(side=tk.LEFT)
            entry = tk.Entry(row, width=15)
            entry.pack(side=tk.LEFT, padx=5)
            self.entradas_limites[limite] = entry

        self.entradas_limites["Xmax:"].insert(0, str(self.xmax))
        self.entradas_limites["Xmin:"].insert(0, str(self.xmin))
        self.entradas_limites["Ymax:"].insert(0, str(self.ymax))
        self.entradas_limites["Ymin:"].insert(0, str(self.ymin))

        tk.Button(frame_limites, text="Definir Limites", command=self.atualizar_limites).pack(fill=tk.X, pady=(5, 0))

        # --- Grupo 2: Entrada Manual ---
        frame_pixel = tk.LabelFrame(self.left_panel, text=" Ativar Pixel ", padx=5, pady=5)
        frame_pixel.pack(fill=tk.X, pady=5)

        self.entradas_pixel = {}
        for eixo in ["X:", "Y:"]:
            row = tk.Frame(frame_pixel)
            row.pack(fill=tk.X, pady=2)
            tk.Label(row, text=eixo, width=4, anchor=tk.W).pack(side=tk.LEFT)
            entry = tk.Entry(row, width=10)
            entry.pack(side=tk.LEFT, padx=5)
            self.entradas_pixel[eixo] = entry

        tk.Button(frame_pixel, text="Desenhar", command=self.ativar_pixel_manual).pack(fill=tk.X, pady=(5, 0))
        tk.Button(frame_pixel, text="Limpar Tela", command=lambda: self.canvas.delete("ponto")).pack(fill=tk.X,
                                                                                                     pady=(5, 0))

        # --- Grupo 3: Leituras de Mouse (Substituindo texto longo) ---
        frame_leituras = tk.LabelFrame(self.left_panel, text=" Leituras ", padx=5, pady=5)
        frame_leituras.pack(fill=tk.BOTH, expand=True, pady=5)

        tk.Label(frame_leituras, text="Tempo Real (Mouse):", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lbl_live_coords = tk.Label(frame_leituras, text="Aguardando...", font=("Arial", 8), justify=tk.LEFT)
        self.lbl_live_coords.pack(anchor=tk.W, pady=(0, 10))

        tk.Label(frame_leituras, text="Último Clique:", font=("Arial", 8, "bold")).pack(anchor=tk.W)
        self.lbl_clicked_coords = tk.Label(frame_leituras, text="Nenhum clique...", font=("Arial", 8), justify=tk.LEFT)
        self.lbl_clicked_coords.pack(anchor=tk.W)

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
    # Funções Lógicas (Mesma do anterior)
    # ==========================================
    def atualizar_limites(self):
        try:
            self.xmax = float(self.entradas_limites["Xmax:"].get())
            self.xmin = float(self.entradas_limites["Xmin:"].get())
            self.ymax = float(self.entradas_limites["Ymax:"].get())
            self.ymin = float(self.entradas_limites["Ymin:"].get())
            messagebox.showinfo("Sucesso", "Limites atualizados!")
        except ValueError:
            messagebox.showerror("Erro", "Valores inválidos.")

    def calcular_textos(self, x_disp, y_disp):
        w = self.canvas.winfo_width()
        h = self.canvas.winfo_height()

        ndc = inp_to_ndc(x_disp, y_disp, w, h)
        wd = ndc_to_wd(ndc["ndcx"], ndc["ndcy"], self.xmax, self.xmin, self.ymax, self.ymin)
        ndcc = wd_to_ndc_central(wd["worldX"], wd["worldY"], self.xmax, self.xmin, self.ymax, self.ymin)

        return (f"Mundo: ({wd['worldX']:.2f}, {wd['worldY']:.2f})\n"
                f"NDC: ({ndc['ndcx']:.2f}, {ndc['ndcy']:.2f})\n"
                f"NDCc: ({ndcc['ndccx']:.2f}, {ndcc['ndccy']:.2f})\n"
                f"Tela: ({x_disp}, {y_disp})")

    def desenhar_pixel(self, x, y):
        self.canvas.delete("ponto")
        h = self.canvas.winfo_height()
        y_invertido = h - y
        self.canvas.create_rectangle(x - 2, y_invertido - 2, x + 2, y_invertido + 2, fill="red", outline="black",
                                     tags="ponto")

    def mouse_move(self, event):
        x = event.x
        y = self.canvas.winfo_height() - event.y
        self.lbl_live_coords.config(text=self.calcular_textos(x, y))

    def mouse_click(self, event):
        x = event.x
        y = self.canvas.winfo_height() - event.y
        self.desenhar_pixel(x, y)
        self.lbl_clicked_coords.config(text=self.calcular_textos(x, y))

    def ativar_pixel_manual(self):
        try:
            x_mundo = float(self.entradas_pixel["X:"].get())
            y_mundo = float(self.entradas_pixel["Y:"].get())

            ndcx = (x_mundo - self.xmin) / (self.xmax - self.xmin)
            ndcy = (y_mundo - self.ymin) / (self.ymax - self.ymin)

            pixel_x = round(ndcx * (self.canvas.winfo_width() - 1))
            pixel_y = round(ndcy * (self.canvas.winfo_height() - 1))

            self.desenhar_pixel(pixel_x, pixel_y)
            self.lbl_clicked_coords.config(text=self.calcular_textos(pixel_x, pixel_y))
        except ValueError:
            pass
