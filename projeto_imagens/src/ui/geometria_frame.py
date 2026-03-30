import os
import customtkinter as ctk
from PIL import Image
from src.algoritmos.utils import carregar_imagem_pgm
from src.algoritmos.geometria import (
    get_matriz_identidade, matriz_translacao, matriz_escala,
    matriz_rotacao, matriz_cisalhamento, matriz_reflexao, renderizar_no_plano
)


class GeometriaFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        # Variáveis Globais de Estado
        self.matriz_original = None
        self.matriz_global = get_matriz_identidade()

        # Agora guardamos uma tupla: (Texto_para_UI, Matriz_Matematica)
        self.fila_operacoes = []

        self.grid_columnconfigure(0, weight=0, minsize=320)
        self.grid_columnconfigure(1, weight=1)
        self.grid_rowconfigure(0, weight=1)

        # ==========================================
        # MENU LATERAL ESQUERDO (Controles)
        # ==========================================
        self.frame_controles = ctk.CTkFrame(self, fg_color="white", corner_radius=15)
        self.frame_controles.grid(row=0, column=0, padx=20, pady=20, sticky="nsew")

        ctk.CTkLabel(self.frame_controles, text="Transformações\nAcumuladas", font=("Arial", 22, "bold"),
                     text_color="#213555").pack(pady=20)

        # Seleção de Imagem
        opcoes_imagens = ["lena.pgm", "airplane.pgm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_controles, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5, padx=20, fill="x")

        # Caixa de Histórico
        ctk.CTkLabel(self.frame_controles, text="Fila de Operações:", font=("Arial", 12, "bold"),
                     text_color="gray").pack(anchor="w", padx=20, pady=(15, 0))
        self.txt_fila = ctk.CTkTextbox(self.frame_controles, height=90, fg_color="#F0F0F0", text_color="#213555")
        self.txt_fila.pack(padx=20, pady=5, fill="x")

        # Botão Limpar
        self.btn_limpar = ctk.CTkButton(self.frame_controles, text="Limpar Transformações", fg_color="#E74C3C",
                                        hover_color="#C0392B", command=self.resetar_transformacoes)
        self.btn_limpar.pack(pady=5, padx=20, fill="x")

        # Configurações de Nova Operação
        ctk.CTkLabel(self.frame_controles, text="Nova Transformação:", font=("Arial", 14, "bold"),
                     text_color="#213555").pack(anchor="w", padx=20, pady=(20, 5))
        opcoes_op = ["Translação", "Escala", "Reflexão", "Cisalhamento", "Rotação"]
        self.cmb_operacao = ctk.CTkComboBox(self.frame_controles, values=opcoes_op, command=self.atualizar_inputs)
        self.cmb_operacao.pack(padx=20, fill="x")

        self.frame_parametros = ctk.CTkFrame(self.frame_controles, fg_color="transparent")
        self.frame_parametros.pack(fill="x", padx=20)
        self.inputs_dinamicos = {}

        # Botão Adicionar à Fila (Azul)
        self.btn_adicionar = ctk.CTkButton(self.frame_controles, text="+ Adicionar à Fila", fg_color="#3498DB",
                                           hover_color="#2980B9", command=self.adicionar_na_fila)
        self.btn_adicionar.pack(pady=(20, 10), padx=20, fill="x")

        # Botão Aplicar Tudo (Verde)
        self.btn_aplicar = ctk.CTkButton(self.frame_controles, text="▶ Aplicar Sequência", fg_color="#4CAF50",
                                         hover_color="#45a049", command=self.executar_sequencia)
        self.btn_aplicar.pack(pady=(0, 20), padx=20, fill="x")

        # ==========================================
        # ÁREA DIREITA (Exibição)
        # ==========================================
        self.frame_exibicao = ctk.CTkFrame(self, fg_color="#D9D9D9", corner_radius=15)
        self.frame_exibicao.grid(row=0, column=1, padx=(0, 20), pady=20, sticky="nsew")

        self.lbl_imagem = ctk.CTkLabel(self.frame_exibicao, text="", bg_color="#D9D9D9")
        self.lbl_imagem.place(relx=0.5, rely=0.5, anchor="center")

        # Start da página
        self.carregar_imagem(opcoes_imagens[0])
        self.atualizar_inputs("Translação")

    def atualizar_inputs(self, escolha):
        for widget in self.frame_parametros.winfo_children(): widget.destroy()
        self.inputs_dinamicos.clear()

        if escolha == "Translação":
            self.inputs_dinamicos['tx'] = self._criar_input("Eixo X (Pixels):", "50")
            self.inputs_dinamicos['ty'] = self._criar_input("Eixo Y (Pixels):", "50")
        elif escolha == "Escala":
            self.inputs_dinamicos['sx'] = self._criar_input("Fator X (ex: 1.5):", "1.5")
            self.inputs_dinamicos['sy'] = self._criar_input("Fator Y (ex: 1.5):", "1.5")
        elif escolha == "Reflexão":
            cmb = ctk.CTkComboBox(self.frame_parametros, values=["Horizontal", "Vertical"])
            cmb.pack(fill="x", pady=10)
            self.inputs_dinamicos['eixo'] = cmb
        elif escolha == "Cisalhamento":
            self.inputs_dinamicos['hx'] = self._criar_input("Fator X (ex: 0.2):", "0.2")
            self.inputs_dinamicos['hy'] = self._criar_input("Fator Y (ex: 0.0):", "0.0")
        elif escolha == "Rotação":
            self.inputs_dinamicos['angulo'] = self._criar_input("Ângulo em Graus:", "30")

    def _criar_input(self, texto, valor_padrao):
        ctk.CTkLabel(self.frame_parametros, text=texto, text_color="#213555").pack(anchor="w", pady=(10, 0))
        entry = ctk.CTkEntry(self.frame_parametros)
        entry.insert(0, valor_padrao)
        entry.pack(fill="x", pady=2)
        return entry

    def carregar_imagem(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            self.resetar_transformacoes()
        except Exception as e:
            print(e)

    def resetar_transformacoes(self):
        """Limpa a fila, zera a matriz global e renderiza a imagem original"""
        self.matriz_global = get_matriz_identidade()
        self.fila_operacoes.clear()
        self._atualizar_texto_fila()
        self.renderizar_tela()

    def adicionar_na_fila(self):
        """Gera a matriz da operação atual e joga na lista de espera"""
        op = self.cmb_operacao.get()
        matriz_local = get_matriz_identidade()
        texto_hist = op

        try:
            if op == "Translação":
                tx, ty = int(self.inputs_dinamicos['tx'].get()), int(self.inputs_dinamicos['ty'].get())
                matriz_local = matriz_translacao(tx, ty)
                texto_hist = f"Translação (x:{tx}, y:{ty})"
            elif op == "Escala":
                sx, sy = float(self.inputs_dinamicos['sx'].get()), float(self.inputs_dinamicos['sy'].get())
                matriz_local = matriz_escala(sx, sy)
                texto_hist = f"Escala (x:{sx}, y:{sy})"
            elif op == "Reflexão":
                eixo = self.inputs_dinamicos['eixo'].get()
                matriz_local = matriz_reflexao(eixo)
                texto_hist = f"Reflexão {eixo}"
            elif op == "Cisalhamento":
                hx, hy = float(self.inputs_dinamicos['hx'].get()), float(self.inputs_dinamicos['hy'].get())
                matriz_local = matriz_cisalhamento(hx, hy)
                texto_hist = f"Cisalhamento (x:{hx}, y:{hy})"
            elif op == "Rotação":
                angulo = float(self.inputs_dinamicos['angulo'].get())
                matriz_local = matriz_rotacao(angulo)
                texto_hist = f"Rotação {angulo}°"

            # Salva na fila de espera (Texto para UI, Matriz Matemática)
            self.fila_operacoes.append((texto_hist, matriz_local))
            self._atualizar_texto_fila()

        except ValueError:
            pass  # Ignora caso digitem letras nos campos de números

    def executar_sequencia(self):
        """Pega todas as matrizes da fila de espera, multiplica em cadeia e renderiza"""
        self.matriz_global = get_matriz_identidade()

        # A matemática da Álgebra Linear diz que a ordem importa:
        # Multiplicamos as matrizes sequencialmente pela matriz Global
        for _, matriz_local in self.fila_operacoes:
            self.matriz_global = matriz_local @ self.matriz_global

        self.renderizar_tela()

    def _atualizar_texto_fila(self):
        self.txt_fila.delete("1.0", "end")
        if not self.fila_operacoes:
            self.txt_fila.insert("1.0", "Nenhuma (Original)")
        else:
            # Extrai apenas os textos da tupla
            linhas = [f"{i + 1}. {item[0]}" for i, item in enumerate(self.fila_operacoes)]
            self.txt_fila.insert("1.0", "\n".join(linhas))

    def renderizar_tela(self):
        if self.matriz_original is None: return
        img_pil = renderizar_no_plano(self.matriz_original, self.matriz_global, tamanho_canvas=600)
        img_ctk = ctk.CTkImage(light_image=img_pil, dark_image=img_pil, size=img_pil.size)
        self.lbl_imagem.configure(image=img_ctk, text="")