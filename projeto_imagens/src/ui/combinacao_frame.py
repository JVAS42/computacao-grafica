import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.combinacao import combinar_imagens


class CombinacaoFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")
        self.matriz_a = None
        self.matriz_b = None

        self.grid_columnconfigure((0, 1, 2), weight=1)
        self.grid_rowconfigure(0, weight=1)

        # === COLUNA 0 ===
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_esq, text="Imagem A", font=("Arial", 16, "bold"), text_color="#213555").pack(pady=5)
        self.lbl_img_a = ctk.CTkLabel(self.frame_esq, text="[ Preview Imagem A ]", width=256, height=256,
                                      bg_color="gray")
        self.lbl_img_a.pack(pady=10)

        opcoes_imagens = ["lena.pgm", "airplane.pgm"]
        self.cmb_imagem_a = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_imagem_a)
        self.cmb_imagem_a.pack(pady=5)

        # === COLUNA 1 ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_centro, text="Imagem B", font=("Arial", 16, "bold"), text_color="#213555").pack(pady=5)
        self.lbl_img_b = ctk.CTkLabel(self.frame_centro, text="[ Preview Imagem B ]", width=256, height=256,
                                      bg_color="gray")
        self.lbl_img_b.pack(pady=10)

        self.cmb_imagem_b = ctk.CTkComboBox(self.frame_centro, values=opcoes_imagens, command=self.carregar_imagem_b)
        self.cmb_imagem_b.pack(pady=5)

        # === COLUNA 2 ===
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_dir, text="Imagem Processada", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_processada = ctk.CTkLabel(self.frame_dir, text="[ Preview Resultado ]", width=256, height=256,
                                               bg_color="gray")
        self.lbl_img_processada.pack(pady=10)

        opcoes_operacao = ["Escolher", "+", "-", "*", "/", "OR", "AND", "XOR"]
        self.cmb_operacao = ctk.CTkComboBox(self.frame_dir, values=opcoes_operacao, command=self.aplicar_combinacao)
        self.cmb_operacao.pack(pady=5)

        # Adicionando o botão de Normalizar que faltava!
        self.check_normalizar = ctk.CTkCheckBox(self.frame_dir, text="Normalizar", text_color="#213555",
                                                command=self.aplicar_combinacao)
        self.check_normalizar.pack(pady=10)
        self.check_normalizar.select()  # Mantém igual ao JS (True por padrão)

        # Inicializar a interface
        self.carregar_imagem_a(opcoes_imagens[0])
        self.carregar_imagem_b(opcoes_imagens[1])

    def carregar_imagem_a(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_a = carregar_imagem_pgm(caminho)
            img_ctk = matriz_para_imagem(self.matriz_a)
            self.lbl_img_a.configure(image=img_ctk, text="")
            self.aplicar_combinacao()
        except Exception as e:
            print(f"Erro em A: {e}")

    def carregar_imagem_b(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_b = carregar_imagem_pgm(caminho)
            img_ctk = matriz_para_imagem(self.matriz_b)
            self.lbl_img_b.configure(image=img_ctk, text="")
            self.aplicar_combinacao()
        except Exception as e:
            print(f"Erro em B: {e}")

    def aplicar_combinacao(self, *args):
        if self.matriz_a is None or self.matriz_b is None: return

        operacao = self.cmb_operacao.get()
        if operacao == "Escolher": return

        # Pega o valor do Checkbox para passar pra função matemática
        normalizar_ativado = bool(self.check_normalizar.get())

        try:
            matriz_res = combinar_imagens(self.matriz_a, self.matriz_b, operacao, normalizar=normalizar_ativado)
            img_ctk = matriz_para_imagem(matriz_res)
            self.lbl_img_processada.configure(image=img_ctk, text="")
        except Exception as e:
            print(f"Erro ao combinar: {e}")