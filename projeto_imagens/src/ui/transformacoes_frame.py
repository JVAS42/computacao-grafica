import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.transformacoes import transformar_imagem


class TransformacoesFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")
        self.matriz_original = None

        self.grid_columnconfigure((0, 1, 2), weight=1)

        # === COLUNA 0 ===
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_esq, text="Imagem Original", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_original = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256, bg_color="gray")
        self.lbl_img_original.pack(pady=10)

        opcoes_imagens = ["lena.pgm", "airplane.pgm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5)

        # === COLUNA 1 ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_centro, text="Transformação", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)

        opcoes_transf = ["Original", "Negativo", "Transformação Gamma", "Transformação Logarítmica",
                         "Transferência Linear", "Faixa Dinâmica", "Transferência Sigmoide"]
        self.cmb_transformacao = ctk.CTkComboBox(self.frame_centro, values=opcoes_transf,
                                                 command=self.aplicar_transformacao)
        self.cmb_transformacao.pack(pady=10)

        self.frame_params = ctk.CTkFrame(self.frame_centro, fg_color="transparent")
        self.frame_params.pack(pady=10, fill="x")

        self.entry_gamma = self._add_param_input("Gamma (γ):", "1")
        self.entry_log_c = self._add_param_input("Log (c):", "1")
        self.entry_w, self.entry_sigma = self._add_param_input("Intensidade (w | σ):", "127", "25")
        self.entry_dyn = self._add_param_input("Faixa Dinâmica:", "255")
        self.entry_a, self.entry_b = self._add_param_input("Linear (a | b):", "1", "1")

        # === COLUNA 2 ===
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_dir, text="Imagem Processada", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_processada = ctk.CTkLabel(self.frame_dir, text="[ Preview ]", width=256, height=256,
                                               bg_color="gray")
        self.lbl_img_processada.pack(pady=10)

        self.carregar_imagem(opcoes_imagens[0])

    def _add_param_input(self, label_text, val1, val2=None):
        frame = ctk.CTkFrame(self.frame_params, fg_color="transparent")
        frame.pack(fill="x", pady=2)
        lbl = ctk.CTkLabel(frame, text=label_text, width=120, anchor="e", text_color="#213555")
        lbl.pack(side="left", padx=(0, 10))

        entry1 = ctk.CTkEntry(frame, width=40)
        entry1.insert(0, val1)
        entry1.pack(side="left", padx=2)
        entry1.bind("<Return>", self.aplicar_transformacao)

        if val2 is not None:
            entry2 = ctk.CTkEntry(frame, width=40)
            entry2.insert(0, val2)
            entry2.pack(side="left", padx=2)
            entry2.bind("<Return>", self.aplicar_transformacao)
            return entry1, entry2
        return entry1

    def carregar_imagem(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            img_ctk = matriz_para_imagem(self.matriz_original)
            self.lbl_img_original.configure(image=img_ctk, text="")
            self.aplicar_transformacao()
        except Exception as e:
            print(f"Erro: {e}")

    def aplicar_transformacao(self, *args):
        if self.matriz_original is None: return
        transf = self.cmb_transformacao.get()

        try:
            params = {
                'gamma': float(self.entry_gamma.get()),
                'log_c': float(self.entry_log_c.get()),
                'w': float(self.entry_w.get()),
                'sigma': float(self.entry_sigma.get()),
                'dynamic_target': float(self.entry_dyn.get()),
                'a': float(self.entry_a.get()),
                'b': float(self.entry_b.get())
            }
        except ValueError:
            params = {}

        matriz_res = transformar_imagem(self.matriz_original, transf, params)
        img_ctk = matriz_para_imagem(matriz_res)
        self.lbl_img_processada.configure(image=img_ctk, text="")