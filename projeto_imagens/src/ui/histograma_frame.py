import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.histograma import equalizar_imagem, gerar_grafico_histograma


class HistogramaFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")
        self.matriz_original = None
        self.matriz_equalizada = None

        self.grid_columnconfigure((0, 1, 2), weight=1)
        self.grid_rowconfigure(0, weight=1)

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

        self.btn_equalizar = ctk.CTkButton(self.frame_esq, text="EQUALIZAR", fg_color="#4CAF50", hover_color="#45a049",
                                           command=self.aplicar_equalizacao)
        self.btn_equalizar.pack(pady=10)

        # === COLUNA 1 ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_centro, text="Histogramas", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)

        self.btn_mostrar_original = ctk.CTkButton(self.frame_centro, text="MOSTRAR ORIGINAL", fg_color="transparent",
                                                  border_width=1, text_color="#213555",
                                                  command=lambda: self.mostrar_grafico("original"))
        self.btn_mostrar_original.pack(pady=(10, 5))
        self.lbl_hist_original = ctk.CTkLabel(self.frame_centro, text="[ Gráfico Histograma Original ]", width=256,
                                              height=150, bg_color="lightgray")
        self.lbl_hist_original.pack(pady=5)

        self.btn_mostrar_equalizado = ctk.CTkButton(self.frame_centro, text="MOSTRAR EQUALIZADO",
                                                    fg_color="transparent", border_width=1, text_color="#213555",
                                                    command=lambda: self.mostrar_grafico("equalizado"))
        self.btn_mostrar_equalizado.pack(pady=(20, 5))
        self.lbl_hist_equalizado = ctk.CTkLabel(self.frame_centro, text="[ Gráfico Histograma Equalizado ]", width=256,
                                                height=150, bg_color="lightgray")
        self.lbl_hist_equalizado.pack(pady=5)

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

            # Limpa o estado quando uma nova imagem é selecionada
            self.matriz_equalizada = None
            self.lbl_img_processada.configure(image=None, text="[ Clique em Equalizar ]")
            self.lbl_hist_original.configure(image=None, text="[ Clique Mostrar Original ]")
            self.lbl_hist_equalizado.configure(image=None, text="[ Clique Mostrar Equalizado ]")
        except Exception as e:
            print(e)

    def aplicar_equalizacao(self):
        if self.matriz_original is None: return
        self.matriz_equalizada = equalizar_imagem(self.matriz_original)
        img_ctk = matriz_para_imagem(self.matriz_equalizada)
        self.lbl_img_processada.configure(image=img_ctk, text="")

    def mostrar_grafico(self, tipo):
        if tipo == "original" and self.matriz_original is not None:
            img_pil = gerar_grafico_histograma(self.matriz_original, desenhar_cdf=True)
            img_ctk = ctk.CTkImage(light_image=img_pil, size=(256, 150))
            self.lbl_hist_original.configure(image=img_ctk, text="")
        elif tipo == "equalizado" and self.matriz_equalizada is not None:
            img_pil = gerar_grafico_histograma(self.matriz_equalizada, desenhar_cdf=True)
            img_ctk = ctk.CTkImage(light_image=img_pil, size=(256, 150))
            self.lbl_hist_equalizado.configure(image=img_ctk, text="")