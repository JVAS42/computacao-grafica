import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.morfismo import morfismo_temporal


class MorfismoFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        # Variáveis que vão guardar as imagens carregadas
        self.matriz_a = None
        self.matriz_b = None

        # O 'relógio' da nossa animação. Começa em 0.0 (totalmente Imagem A)
        self.tempo_animacao = 0.0

        # --- ESTRUTURA DA TELA ---
        # Divide a tela em 3 colunas iguais: Criança (Esq), Adulto (Centro), Resultado (Dir)
        self.grid_columnconfigure((0, 1, 2), weight=1)
        self.grid_rowconfigure(0, weight=1)

        # Lista de arquivos disponíveis na pasta
        opcoes_imagens = ["kid.pgm", "adult.pgm", "deniskid.pgm", "denisadult.pgm", "flaviakid.pgm", "flaviadulta.pgm",
                          "joaokid.pgm", "joaoadult.pgm", "raquelkid.pgm", "raqueladult.pgm"]

        # === COLUNA 0: ÁREA DA IMAGEM INICIAL (CRIANÇA) ===
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_esq, text="Imagem Criança", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_crianca = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256, bg_color="gray")
        self.lbl_img_crianca.pack(pady=10)

        # Menu suspenso para escolher a imagem inicial
        self.cmb_img_crianca = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_a)
        self.cmb_img_crianca.pack(pady=5)

        # === COLUNA 1: ÁREA DA IMAGEM FINAL (ADULTO) ===
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_centro, text="Imagem Adulto", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_adulto = ctk.CTkLabel(self.frame_centro, text="[ Preview ]", width=256, height=256,
                                           bg_color="gray")
        self.lbl_img_adulto.pack(pady=10)

        # Menu suspenso para escolher a imagem final
        self.cmb_img_adulto = ctk.CTkComboBox(self.frame_centro, values=opcoes_imagens, command=self.carregar_b)
        self.cmb_img_adulto.pack(pady=5)

        # === COLUNA 2: ÁREA DA ANIMAÇÃO (RESULTADO) ===
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_dir, text="Morfismo Temporal", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_morfismo = ctk.CTkLabel(self.frame_dir, text="[ Animação / Resultado ]", width=256, height=256,
                                             bg_color="lightgray")
        self.lbl_img_morfismo.pack(pady=10)

        # Botão que dá o "Play" na mágica
        self.btn_iniciar = ctk.CTkButton(self.frame_dir, text="INICIAR MORFISMO\nTEMPORAL", fg_color="#213555",
                                         hover_color="#45a049", command=self.iniciar_animacao)
        self.btn_iniciar.pack(pady=10)

        # Carrega as duas primeiras imagens da lista logo de cara para não ficar vazio
        self.carregar_a(opcoes_imagens[0])
        self.carregar_b(opcoes_imagens[1])

    # Funções auxiliares para ler a imagem do computador e colocar na tela
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
        """
        Gatilho inicial! Impede que o usuário aperte o botão várias vezes e bagunce
        a animação atual, e zera o nosso "relógio" (tempo_animacao) de volta para o início.
        """
        if self.matriz_a is None or self.matriz_b is None: return
        self.btn_iniciar.configure(state="disabled", text="ANIMANDO...")

        self.tempo_animacao = 0.0
        self.contador_frame = 0  # Conta quantos "quadros" de vídeo geramos

        # Dá a largada no loop que vai rodar o vídeo
        self._loop_animacao()

    def _loop_animacao(self):
        """
        O coração do reprodutor de vídeo. Esta função cria uma imagem, mostra na tela,
        espera um piscar de olhos, e chama ela mesma de novo para criar a próxima.
        """
        # Enquanto não chegarmos a 1.0 (o final da transformação)...
        if self.tempo_animacao <= 1.0:

            # Pede para o arquivo morfismo.py gerar o quadro do momento atual
            matriz_morf = morfismo_temporal(self.matriz_a, self.matriz_b, self.tempo_animacao)

            # Mostra essa nova imagem gerada na tela
            self.lbl_img_morfismo.configure(image=matriz_para_imagem(matriz_morf), text="")

            # Avança o relógio um pouquinho (1% de cada vez).
            # Se você mudar isso para 0.05, a animação será muito mais rápida (mas com menos quadros).
            self.tempo_animacao += 0.01

            self.contador_frame += 1

            # A mágica do Loop: Agenda para o próprio programa rodar esta exata função
            # de novo daqui a 50 milissegundos (criando o efeito de vídeo).
            self.after(50, self._loop_animacao)

        else:
            # O relógio passou de 1.0! A transformação acabou.
            print(f"Morfismo concluído\nTOTAL DE INTERAÇÕES: {self.contador_frame}")
            self.btn_iniciar.configure(state="normal", text="INICIAR MORFISMO\nTEMPORAL")