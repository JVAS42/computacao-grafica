import os
import customtkinter as ctk
import numpy as np
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.transformacoes import transformar_imagem


class TransformacoesFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        self.matriz_original = None
        self.grid_columnconfigure((0, 1, 2), weight=1)

        # === COLUNA 0: ORIGINAL ===
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_esq, text="Imagem Original", font=("Arial", 16, "bold")).pack(pady=5)
        self.lbl_img_original = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256)
        self.lbl_img_original.pack(pady=10)

        opcoes_imagens = ["lena.pgm", "airplane.pgm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5)

        # === COLUNA 1: CONTROLES ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_centro, text="Transformação", font=("Arial", 16, "bold")).pack(pady=5)

        opcoes_transf = [
            "Original", "Negativo", "Transformação Gamma",
            "Transformação Logarítmica", "Transferência Linear",
            "Faixa Dinâmica", "Transferência Sigmoide"
        ]

        self.cmb_transformacao = ctk.CTkComboBox(
            self.frame_centro,
            values=opcoes_transf,
            command=self.on_transformacao_change
        )
        self.cmb_transformacao.pack(pady=10)

        self.frame_params = ctk.CTkFrame(self.frame_centro, fg_color="transparent")
        self.frame_params.pack(pady=10, fill="x")

        # ===== INICIALIZAÇÃO DOS INPUTS (Escondidos por padrão) =====
        self.frame_gamma, self.entry_gamma = self._add_param_input("Gamma (0.01 a 1.0):", "1.0")
        self.frame_log, self.entry_log_c = self._add_param_input("Constante (c):", "1.0")
        self.frame_sig, self.entry_w, self.entry_sigma = self._add_param_input("Sigmoide (w | σ):", "127", "25")
        self.frame_dyn, self.entry_dyn = self._add_param_input("Faixa Dinâmica:", "255")
        self.frame_lin, self.entry_a, self.entry_b = self._add_param_input("Linear (a | b):", "1", "0")

        # Botões de Ação
        self.btn_aplicar = ctk.CTkButton(
            self.frame_centro,
            text="Aplicar Transformação",
            command=self.aplicar_transformacao
        )
        self.btn_aplicar.pack(pady=5)

        self.btn_reset = ctk.CTkButton(
            self.frame_centro,
            text="Resetar Parâmetros",
            fg_color="#444444",
            hover_color="#333333",
            command=self.resetar_parametros
        )
        self.btn_reset.pack(pady=5)

        # === COLUNA 2: PROCESSADA ===
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_dir, text="Imagem Processada", font=("Arial", 16, "bold")).pack(pady=5)
        self.lbl_img_processada = ctk.CTkLabel(self.frame_dir, text="[ Preview ]", width=256, height=256)
        self.lbl_img_processada.pack(pady=10)

        # Estado inicial
        self.carregar_imagem(opcoes_imagens[0])
        self.on_transformacao_change("Original")

    def on_transformacao_change(self, escolha):
        for frame in [self.frame_gamma, self.frame_log, self.frame_sig, self.frame_dyn, self.frame_lin]:
            frame.pack_forget()

        if escolha == "Transformação Gamma":
            self.frame_gamma.pack(fill="x", pady=2)
        elif escolha == "Transformação Logarítmica":
            self.frame_log.pack(fill="x", pady=2)
        elif escolha == "Transferência Linear":
            self.frame_lin.pack(fill="x", pady=2)
        elif escolha == "Faixa Dinâmica":
            self.frame_dyn.pack(fill="x", pady=2)
        elif escolha == "Transferência Sigmoide":
            self.frame_sig.pack(fill="x", pady=2)

    def _add_param_input(self, label_text, val1, val2=None):
        frame = ctk.CTkFrame(self.frame_params, fg_color="transparent")
        ctk.CTkLabel(frame, text=label_text, width=140, anchor="e").pack(side="left", padx=(0, 10))

        e1 = ctk.CTkEntry(frame, width=50)
        e1.insert(0, val1)
        e1.pack(side="left", padx=2)

        if val2 is not None:
            e2 = ctk.CTkEntry(frame, width=50)
            e2.insert(0, val2)
            e2.pack(side="left", padx=2)
            return frame, e1, e2
        return frame, e1

    def carregar_imagem(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            img_ctk = matriz_para_imagem(self.matriz_original)
            self.lbl_img_original.configure(image=img_ctk, text="")
            self.lbl_img_original.image = img_ctk  # Mantém referência
            self.aplicar_transformacao()
        except Exception as e:
            print(f"Erro ao carregar: {e}")

    def resetar_parametros(self):
        self.cmb_transformacao.set("Original")
        self.on_transformacao_change("Original")
        # Reset de valores padrão
        for e, v in [(self.entry_gamma, "1.0"), (self.entry_log_c, "1.0"), (self.entry_w, "127")]:
            e.delete(0, "end");
            e.insert(0, v)
        self.aplicar_transformacao()

    def aplicar_transformacao(self, *args):
        if self.matriz_original is None: return

        # CRUCIAL: Trabalhar sempre com uma CÓPIA para não travar a original
        matriz_base = self.matriz_original.copy()
        transf = self.cmb_transformacao.get()

        try:
            # Captura e validação
            g_val = float(self.entry_gamma.get() or 1.0)
            g_val = max(0.01, min(g_val, 1.0))  # Trava entre 0 e 1 conforme sua regra

            params = {
                'gamma': g_val,
                'log_c': float(self.entry_log_c.get() or 1.0),
                'w': float(self.entry_w.get() or 127.0),
                'sigma': float(self.entry_sigma.get() or 25.0),
                'dynamic_target': float(self.entry_dyn.get() or 255.0),
                'a': float(self.entry_a.get() or 1.0),
                'b': float(self.entry_b.get() or 0.0)
            }
        except ValueError:
            params = {'gamma': 1.0, 'log_c': 1.0, 'w': 127, 'sigma': 25, 'dynamic_target': 255, 'a': 1, 'b': 0}

        # Processamento e atualização da interface
        matriz_res = transformar_imagem(matriz_base, transf, params)
        img_ctk = matriz_para_imagem(matriz_res)

        self.lbl_img_processada.configure(image=img_ctk, text="")
        self.lbl_img_processada.image = img_ctk  # Garante que a imagem não suma da memória