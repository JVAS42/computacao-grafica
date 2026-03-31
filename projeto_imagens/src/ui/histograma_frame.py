import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.histograma import equalizar_imagem, gerar_grafico_histograma


class HistogramaFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        # Inicia a construção da interface assim que a classe é instanciada
        self.construir_interface()

    def construir_interface(self):
        """Constrói (ou reconstrói) todos os elementos da interface do zero."""

        # 1. Limpa todos os widgets existentes no frame (funciona como uma vassoura
        # para garantir que a tela esteja limpa ao reiniciar ou trocar de aba).
        for widget in self.winfo_children():
            widget.destroy()

        # 2. Reseta as variáveis de estado, esquecendo imagens e gráficos antigos.
        self.matriz_original = None
        self.matriz_equalizada = None
        self.img_orig_ctk = None
        self.img_proc_ctk = None
        self.hist_orig_ctk = None
        self.hist_proc_ctk = None

        # 3. Configura o grid principal dividindo a tela em 3 colunas iguais.
        self.grid_columnconfigure((0, 1, 2), weight=1)
        self.grid_rowconfigure(0, weight=1)

        # === COLUNA 0: ORIGINAL (Esquerda) ===
        # Área dedicada a mostrar a foto escolhida e seu gráfico original.
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_esq, text="Imagem Original", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_original = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256, bg_color="gray")
        self.lbl_img_original.pack(pady=10)

        self.lbl_hist_original = ctk.CTkLabel(self.frame_esq, text="[ Histograma Original ]", width=256, height=150,
                                              bg_color="lightgray")
        self.lbl_hist_original.pack(pady=10)

        # === COLUNA 1: CONTROLES (Centro) ===
        # Área dos botões e menus onde o usuário interage.
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")

        opcoes_imagens = ["lena.pgm", "airplane.pgm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_centro, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.set("Selecione...")
        self.cmb_imagem.pack(pady=20)

        self.btn_equalizar = ctk.CTkButton(self.frame_centro, text="Equalizar Imagem", command=self.aplicar_equalizacao)
        self.btn_equalizar.pack(pady=10)

        # O botão resetar reconstrói toda a interface, apagando tudo.
        self.btn_resetar = ctk.CTkButton(self.frame_centro, text="Limpar / Resetar", command=self.construir_interface,
                                         fg_color="#C0392B", hover_color="#922B21")
        self.btn_resetar.pack(pady=10)

        # === COLUNA 2: PROCESSADA (Direita) ===
        # Área para exibir o resultado final após a matemática agir.
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_dir, text="Imagem Equalizada", font=("Arial", 16, "bold"), text_color="#213555").pack(
            pady=5)
        self.lbl_img_processada = ctk.CTkLabel(self.frame_dir, text="[ Processada ]", width=256, height=256,
                                               bg_color="gray")
        self.lbl_img_processada.pack(pady=10)

        self.lbl_hist_equalizado = ctk.CTkLabel(self.frame_dir, text="[ Histograma Equalizado ]", width=256, height=150,
                                                bg_color="lightgray")
        self.lbl_hist_equalizado.pack(pady=10)

    def carregar_imagem(self, nome_arquivo):
        """Busca o arquivo escolhido, exibe na tela e limpa resultados antigos."""
        diretorio_base = r"C:\Projetos\computacao-grafica\projeto_imagens\assets"
        caminho = os.path.join(diretorio_base, nome_arquivo)

        try:
            # Carrega a matriz bruta da imagem da pasta do computador
            self.matriz_original = carregar_imagem_pgm(caminho)

            # Transforma a matriz em algo que a interface gráfica consiga desenhar na tela
            self.img_orig_ctk = matriz_para_imagem(self.matriz_original)
            self.lbl_img_original.configure(image=self.img_orig_ctk, text="")

            # Remove qualquer imagem ou gráfico equalizado que tenha sobrado de antes
            self.matriz_equalizada = None
            self.img_proc_ctk = None
            self.hist_proc_ctk = None
            self.lbl_img_processada.configure(image=None, text="[ Processada ]")
            self.lbl_hist_equalizado.configure(image=None, text="[ Histograma Equalizado ]")

            # Chama a função para desenhar o gráfico da imagem original recém-carregada
            self.mostrar_grafico("original")

        except Exception as e:
            print(f"Erro ao carregar imagem: {e}")

    def aplicar_equalizacao(self):
        """Envia a imagem para o cálculo de equalização e exibe o resultado."""
        if self.matriz_original is None: return

        # Chama a função matemática do outro arquivo (o "cérebro")
        self.matriz_equalizada = equalizar_imagem(self.matriz_original)

        # Converte a matriz melhorada de volta para uma imagem visível na interface
        self.img_proc_ctk = matriz_para_imagem(self.matriz_equalizada)
        self.lbl_img_processada.configure(image=self.img_proc_ctk, text="")

        # Desenha o novo gráfico da imagem já processada
        self.mostrar_grafico("equalizado")

    def mostrar_grafico(self, tipo):
        """Busca o gráfico gerado na lógica matemática e o posiciona no app."""
        if tipo == "original" and self.matriz_original is not None:
            # Pede o gráfico original gerado pelo arquivo matemático
            img_pil = gerar_grafico_histograma(self.matriz_original, desenhar_cdf=True)
            self.hist_orig_ctk = ctk.CTkImage(light_image=img_pil, size=(256, 150))
            self.lbl_hist_original.configure(image=self.hist_orig_ctk, text="")

        elif tipo == "equalizado" and self.matriz_equalizada is not None:
            # Pede o gráfico processado gerado pelo arquivo matemático
            img_pil = gerar_grafico_histograma(self.matriz_equalizada, desenhar_cdf=True)
            self.hist_proc_ctk = ctk.CTkImage(light_image=img_pil, size=(256, 150))
            self.lbl_hist_equalizado.configure(image=self.hist_proc_ctk, text="")