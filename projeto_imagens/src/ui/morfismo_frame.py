import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.morfismo import morfismo_temporal


class MorfismoFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")
        self.matriz_a = None
        self.matriz_b = None

        self.tempo_animacao = 0.0  # Vai de 0.0 (Criança) até 1.0 (Adulto)

        self.grid_columnconfigure((0, 1, 2), weight=1)
        self.grid_rowconfigure(0, weight=1)
        opcoes_imagens = ["kid.pgm", "adult.pgm", "deniskid.pgm", "denisadult.pgm", "flaviakid.pgm", "flaviadulta.pgm",
                          "joaokid.pgm", "joaoadult.pgm", "raquelkid.pgm", "raqueladult.pgm"]

        # === COLUNA 0 ===
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_esq, text="Imagem Criança", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_crianca = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256, bg_color="gray")
        self.lbl_img_crianca.pack(pady=10)
        self.cmb_img_crianca = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_a)
        self.cmb_img_crianca.pack(pady=5)

        # === COLUNA 1 ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_centro, text="Imagem Adulto", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_adulto = ctk.CTkLabel(self.frame_centro, text="[ Preview ]", width=256, height=256,
                                           bg_color="gray")
        self.lbl_img_adulto.pack(pady=10)
        self.cmb_img_adulto = ctk.CTkComboBox(self.frame_centro, values=opcoes_imagens, command=self.carregar_b)
        self.cmb_img_adulto.pack(pady=5)

        # === COLUNA 2 ===
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")
        ctk.CTkLabel(self.frame_dir, text="Morfismo Temporal", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_morfismo = ctk.CTkLabel(self.frame_dir, text="[ Animação / Resultado ]", width=256, height=256,
                                             bg_color="lightgray")
        self.lbl_img_morfismo.pack(pady=10)

        self.btn_iniciar = ctk.CTkButton(self.frame_dir, text="INICIAR MORFISMO\nTEMPORAL", fg_color="#213555",
                                         hover_color="#45a049", command=self.iniciar_animacao)
        self.btn_iniciar.pack(pady=10)

        self.carregar_a(opcoes_imagens[0])
        self.carregar_b(opcoes_imagens[1])

    def carregar_a(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_a = carregar_imagem_pgm(caminho)
            self.lbl_img_crianca.configure(image=matriz_para_imagem(self.matriz_a), text="")
        except Exception as e:
            print(e)

    def carregar_b(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_b = carregar_imagem_pgm(caminho)
            self.lbl_img_adulto.configure(image=matriz_para_imagem(self.matriz_b), text="")
        except Exception as e:
            print(e)

    def iniciar_animacao(self):
        """Bloqueia o botão e inicia o loop de renderização do vídeo/morfismo."""
        if self.matriz_a is None or self.matriz_b is None: return
        self.btn_iniciar.configure(state="disabled", text="ANIMANDO...")
        self.tempo_animacao = 0.0
        self.contador_frame = 0

        self._loop_animacao()

    def _loop_animacao(self):
        """Função recursiva que roda a cada 50ms para renderizar o próximo frame."""
        if self.tempo_animacao <= 1.0:
            # Calcula a nova matriz interpolada
            matriz_morf = morfismo_temporal(self.matriz_a, self.matriz_b, self.tempo_animacao)
            self.lbl_img_morfismo.configure(image=matriz_para_imagem(matriz_morf), text="")

            # Incrementa o T (quanto menor o valor, mais demorada a transição)
            self.tempo_animacao += 0.01

            self.contador_frame += 1

            # Agenda o próprio método para rodar novamente daqui a 50 milissegundos
            self.after(50, self._loop_animacao)
        else:
            # Terminou! Libera o botão.
            print(f"Morfismo concluído\nTOTAL DE INTERAÇÕES: {self.contador_frame}")
            self.btn_iniciar.configure(state="normal", text="INICIAR MORFISMO\nTEMPORAL")