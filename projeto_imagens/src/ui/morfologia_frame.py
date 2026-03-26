import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.morfologia import processar_morfologia


class MorfologiaFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")
        self.matriz_original = None

        self.grid_columnconfigure((0, 1, 2), weight=1)
        self.grid_rowconfigure(0, weight=1)

        # === COLUNA 0 ===
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_esq, text="Imagem Original", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_original = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256, bg_color="gray")
        self.lbl_img_original.pack(pady=10)

        opcoes_imagens = ["fingerprint.pbm", "holes.pbm", "text.pbm", "map.pbm", "lena.pgm", "airplane.pgm",
                          "cameraman.pgm", "supernova.pgm", "sea.pgm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5)

        # === COLUNA 1 ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_centro, text="Transformação", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)

        opcoes_operacao = [
            "Original", "Complemento", "Erosão", "Dilatação", "Abertura", "Fechamento",
            "Contorno Externo", "Contorno Interno", "Gradiente", "Afinamento",
            "Erosão (Cinza)", "Dilatação (Cinza)", "Abertura (Cinza)", "Fechamento (Cinza)",
            "Top Hat", "Bottom Hat"
        ]
        self.cmb_operacao = ctk.CTkComboBox(self.frame_centro, values=opcoes_operacao, command=self.aplicar_morfologia)
        self.cmb_operacao.pack(pady=10)

        self.frame_matriz = ctk.CTkFrame(self.frame_centro, fg_color="transparent")
        self.frame_matriz.pack(pady=10)
        self.entradas_matriz = []
        padrao = [0, 1, 0, 1, 1, 1, 0, 1, 0]  # Elemento estruturante em cruz
        k = 0
        for i in range(3):
            linha = []
            for j in range(3):
                entry = ctk.CTkEntry(self.frame_matriz, width=40, justify="center")
                entry.grid(row=i, column=j, padx=2, pady=2)
                entry.insert(0, str(padrao[k]))
                entry.bind("<Return>", self.aplicar_morfologia)  # Processa ao dar Enter
                linha.append(entry)
                k += 1
            self.entradas_matriz.append(linha)

        # === COLUNA 2 ===
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_dir, text="Imagem Processada", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_processada = ctk.CTkLabel(self.frame_dir, text="[ Preview ]", width=256, height=256,
                                               bg_color="gray")
        self.lbl_img_processada.pack(pady=10)

        self.carregar_imagem(opcoes_imagens[0])

    def carregar_imagem(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            img_ctk = matriz_para_imagem(self.matriz_original)
            self.lbl_img_original.configure(image=img_ctk, text="")
            self.aplicar_morfologia()
        except Exception as e:
            print(e)

    def aplicar_morfologia(self, *args):
        if self.matriz_original is None: return

        # Puxa os valores dos 9 inputs do Kernel e converte pra lista
        kernel_flat = []
        for i in range(3):
            for j in range(3):
                try:
                    val = int(self.entradas_matriz[i][j].get())
                except ValueError:
                    val = 0
                kernel_flat.append(val)

        operacao = self.cmb_operacao.get()
        matriz_res = processar_morfologia(self.matriz_original, operacao, kernel_flat)

        img_ctk = matriz_para_imagem(matriz_res)
        self.lbl_img_processada.configure(image=img_ctk, text="")