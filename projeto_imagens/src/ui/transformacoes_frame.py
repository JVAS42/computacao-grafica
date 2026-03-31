import os
import customtkinter as ctk
import numpy as np
from src.algoritmos.utils import carregar_imagem_pgm, matriz_para_imagem
from src.algoritmos.transformacoes import transformar_imagem


class TransformacoesFrame(ctk.CTkFrame):
    """
    Esta classe cria a tela visual onde o usuário interage.
    Ela é dividida em 3 colunas: Imagem Original (Esquerda), Controles (Centro) e Imagem Processada (Direita).
    """

    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        self.matriz_original = None
        self.grid_columnconfigure((0, 1, 2), weight=1)

        # =========================================================
        # COLUNA 0: ORIGINAL (Visualização da imagem sem filtro)
        # =========================================================
        self.frame_esq = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_esq.grid(row=0, column=0, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_esq, text="Imagem Original", font=("Arial", 16, "bold")).pack(pady=5)
        self.lbl_img_original = ctk.CTkLabel(self.frame_esq, text="[ Preview ]", width=256, height=256)
        self.lbl_img_original.pack(pady=10)

        # Menu para escolher qual imagem base carregar
        opcoes_imagens = ["lena.pgm", "airplane.pgm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_esq, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5)

        # =========================================================
        # COLUNA 1: CONTROLES (Menus, botões e campos de texto)
        # =========================================================
        self.frame_centro = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_centro.grid(row=0, column=1, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_centro, text="Transformação", font=("Arial", 16, "bold")).pack(pady=5)

        # Lista de transformações que implementamos no outro arquivo
        opcoes_transf = [
            "Original", "Negativo", "Transformação Gamma",
            "Transformação Logarítmica", "Transferência Linear",
            "Faixa Dinâmica", "Transferência Sigmoide"
        ]

        # Ao trocar de transformação no menu, chama a função 'on_transformacao_change'
        self.cmb_transformacao = ctk.CTkComboBox(
            self.frame_centro,
            values=opcoes_transf,
            command=self.on_transformacao_change
        )
        self.cmb_transformacao.pack(pady=10)

        # Container para colocar as caixinhas de parâmetros dinamicamente
        self.frame_params = ctk.CTkFrame(self.frame_centro, fg_color="transparent")
        self.frame_params.pack(pady=10, fill="x")

        # Aqui criamos todos os campos de parâmetros (Gamma, Constantes, etc.)
        # Eles começam escondidos. Só mostramos o que importa para a transformação selecionada.
        self.frame_gamma, self.entry_gamma = self._add_param_input("Gamma (0.01 a 1.0):", "1.0")
        self.frame_log, self.entry_log_c = self._add_param_input("Constante (c):", "1.0")
        self.frame_sig, self.entry_w, self.entry_sigma = self._add_param_input("Sigmoide (w | σ):", "127", "25")
        self.frame_dyn, self.entry_dyn = self._add_param_input("Faixa Dinâmica:", "255")
        self.frame_lin, self.entry_a, self.entry_b = self._add_param_input("Linear (a | b):", "1", "0")

        # Botão principal que envia os parâmetros para o processamento
        self.btn_aplicar = ctk.CTkButton(
            self.frame_centro,
            text="Aplicar Transformação",
            command=self.aplicar_transformacao
        )
        self.btn_aplicar.pack(pady=5)

        # Botão para limpar a bagunça do usuário e voltar aos valores padrão
        self.btn_reset = ctk.CTkButton(
            self.frame_centro,
            text="Resetar Parâmetros",
            fg_color="#444444",
            hover_color="#333333",
            command=self.resetar_parametros
        )
        self.btn_reset.pack(pady=5)

        # =========================================================
        # COLUNA 2: PROCESSADA (Resultado final)
        # =========================================================
        self.frame_dir = ctk.CTkFrame(self, fg_color="transparent")
        self.frame_dir.grid(row=0, column=2, padx=10, pady=10, sticky="n")

        ctk.CTkLabel(self.frame_dir, text="Imagem Processada", font=("Arial", 16, "bold")).pack(pady=5)
        self.lbl_img_processada = ctk.CTkLabel(self.frame_dir, text="[ Preview ]", width=256, height=256)
        self.lbl_img_processada.pack(pady=10)

        # Carrega a primeira imagem e o estado inicial automaticamente ao abrir o app
        self.carregar_imagem(opcoes_imagens[0])
        self.on_transformacao_change("Original")

    def on_transformacao_change(self, escolha):
        """
        Esta função organiza a interface. Se o usuário escolheu "Gamma", ela esconde
        os campos do Sigmoide e mostra apenas o campo de digitar o valor do Gamma.
        """
        # Esconde todo mundo primeiro
        for frame in [self.frame_gamma, self.frame_log, self.frame_sig, self.frame_dyn, self.frame_lin]:
            frame.pack_forget()

        # Mostra apenas o formulário correto baseado na escolha do menu
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
        """ Função auxiliar para desenhar as caixinhas de texto e rótulos na tela mais facilmente. """
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
        """ Busca a imagem na pasta assets e converte para um formato que a tela entenda. """
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            img_ctk = matriz_para_imagem(self.matriz_original)

            # Atualiza a imagem no painel esquerdo
            self.lbl_img_original.configure(image=img_ctk, text="")
            self.lbl_img_original.image = img_ctk  # Evita que a imagem seja apagada da memória (Garbage Collection)

            # Já aplica a transformação vigente (mesmo que seja só "Original")
            self.aplicar_transformacao()
        except Exception as e:
            print(f"Erro ao carregar: {e}")

    def resetar_parametros(self):
        """ Devolve os campos da interface para o modo padrão. """
        self.cmb_transformacao.set("Original")
        self.on_transformacao_change("Original")

        # Limpa e reinsere os valores básicos
        for e, v in [(self.entry_gamma, "1.0"), (self.entry_log_c, "1.0"), (self.entry_w, "127")]:
            e.delete(0, "end");
            e.insert(0, v)
        self.aplicar_transformacao()

    def aplicar_transformacao(self, *args):
        """
        Ação principal do botão! Ele lê o que o usuário digitou,
        manda para o arquivo transformacoes.py e exibe o resultado final.
        """
        if self.matriz_original is None: return

        # IMPORTANTÍSSIMO: Copiar a matriz. Se alterarmos 'self.matriz_original',
        # não conseguiríamos voltar a imagem ao normal sem carregar o arquivo de novo.
        matriz_base = self.matriz_original.copy()
        transf = self.cmb_transformacao.get()

        try:
            # Pega o que foi digitado. Se estiver vazio, usa o padrão (or 1.0).
            g_val = float(self.entry_gamma.get() or 1.0)
            # Limita fisicamente o Gamma entre 0.01 e 1.0 para evitar crashes matemáticos ou estouros
            g_val = max(0.01, min(g_val, 1.0))

            # Agrupa tudo em um dicionário para enviar de uma vez para a função lógica
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
            # Se o usuário digitou letras onde deveria ser número, cai aqui e usa um valor de segurança
            params = {'gamma': 1.0, 'log_c': 1.0, 'w': 127, 'sigma': 25, 'dynamic_target': 255, 'a': 1, 'b': 0}

        # -------------------------------------------------------------
        # Chama a função que faz a matemática pesada (do outro arquivo)
        # -------------------------------------------------------------
        matriz_res = transformar_imagem(matriz_base, transf, params)

        # Converte os números da matriz processada de volta para pixels visuais
        img_ctk = matriz_para_imagem(matriz_res)

        # Atualiza a tela da direita (Imagem Processada)
        self.lbl_img_processada.configure(image=img_ctk, text="")
        self.lbl_img_processada.image = img_ctk  # Proteção contra o Garbage Collector do Python