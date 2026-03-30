import os
import customtkinter as ctk
from src.algoritmos.utils import carregar_imagem_pgm
from src.algoritmos.geometria import (
    get_matriz_identidade, matriz_translacao, matriz_escala,
    matriz_rotacao, matriz_cisalhamento, matriz_reflexao, renderizar_imagem
)


class GeometriaFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        # Variáveis de Estado
        self.matriz_original = None
        self.matriz_global = get_matriz_identidade()
        self.fila_historico = []

        self.grid_columnconfigure(0, weight=0, minsize=320)
        self.grid_columnconfigure(1, weight=1)
        self.grid_rowconfigure(0, weight=1)

        # ==========================================
        # MENU LATERAL ESQUERDO
        # ==========================================
        self.frame_controles = ctk.CTkFrame(self, fg_color="white", corner_radius=15)
        self.frame_controles.grid(row=0, column=0, padx=20, pady=20, sticky="nsew")

        ctk.CTkLabel(self.frame_controles, text="Fotos e\nFiltros", font=("Arial", 22, "bold"),
                     text_color="#213555").pack(pady=20)

        # 1. Imagem e Fila
        opcoes_imagens = ["lena.pgm", "lena.pbm", "airplane.pgm", "airplane.pbm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_controles, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5, padx=20, fill="x")

        ctk.CTkLabel(self.frame_controles, text="Fila de Operações:", font=("Arial", 12, "bold"),
                     text_color="gray").pack(anchor="w", padx=20, pady=(15, 0))
        self.txt_fila = ctk.CTkTextbox(self.frame_controles, height=80, fg_color="#F0F0F0", text_color="#213555")
        self.txt_fila.pack(padx=20, pady=5, fill="x")

        # Botão Limpar
        self.btn_limpar = ctk.CTkButton(self.frame_controles, text="Limpar Transformações", fg_color="#E74C3C",
                                        hover_color="#C0392B", command=self.resetar_transformacoes)
        self.btn_limpar.pack(pady=5, padx=20, fill="x")

        # 2. Nova Operação
        ctk.CTkLabel(self.frame_controles, text="Nova Transformação:", font=("Arial", 14, "bold"),
                     text_color="#213555").pack(anchor="w", padx=20, pady=(20, 5))
        opcoes_op = ["Translação", "Escala", "Reflexão", "Cisalhamento", "Rotação"]
        self.cmb_operacao = ctk.CTkComboBox(self.frame_controles, values=opcoes_op, command=self.atualizar_inputs)
        self.cmb_operacao.pack(padx=20, fill="x")

        self.frame_parametros = ctk.CTkFrame(self.frame_controles, fg_color="transparent")
        self.frame_parametros.pack(fill="x", padx=20)
        self.inputs_dinamicos = {}

        self.btn_adicionar = ctk.CTkButton(self.frame_controles, text="Aplicar", fg_color="#213555",
                                           hover_color="#45a049", command=self.adicionar_operacao)
        self.btn_adicionar.pack(pady=20, padx=20, fill="x")

        # ==========================================
        # ÁREA DIREITA (DUAS VISUALIZAÇÕES)
        # ==========================================
        self.frame_exibicao = ctk.CTkFrame(self, fg_color="#D9D9D9", corner_radius=15)
        self.frame_exibicao.grid(row=0, column=1, padx=(0, 20), pady=20, sticky="nsew")

        # Configura as colunas para dividir o espaço igualmente
        self.frame_exibicao.grid_columnconfigure(0, weight=1)
        self.frame_exibicao.grid_columnconfigure(1, weight=1)
        self.frame_exibicao.grid_rowconfigure(1, weight=1)

        # Títulos das Janelas
        ctk.CTkLabel(self.frame_exibicao, text="Transformação", font=("Arial", 16, "bold"), text_color="#213555").grid(
            row=0, column=0, pady=(15, 0))
        ctk.CTkLabel(self.frame_exibicao, text="Enquadrada", font=("Arial", 16, "bold"), text_color="#213555").grid(
            row=0, column=1, pady=(15, 0))

        # Containers das Imagens
        self.lbl_imagem_transf = ctk.CTkLabel(self.frame_exibicao, text="", bg_color="#D9D9D9")
        self.lbl_imagem_transf.grid(row=1, column=0, padx=10, pady=10)

        self.lbl_imagem_final = ctk.CTkLabel(self.frame_exibicao, text="", bg_color="#D9D9D9")
        self.lbl_imagem_final.grid(row=1, column=1, padx=10, pady=10)

        # Inicializa a tela
        self.carregar_imagem(opcoes_imagens[0])
        self.atualizar_inputs("Translação")

    # ==================== LÓGICA DE UI ====================
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
            self.inputs_dinamicos['angulo'] = self._criar_input("Ângulo em Graus:", "90")

    def _criar_input(self, texto, valor_padrao):
        ctk.CTkLabel(self.frame_parametros, text=texto, text_color="#213555").pack(anchor="w", pady=(10, 0))
        entry = ctk.CTkEntry(self.frame_parametros)
        entry.insert(0, valor_padrao)
        entry.pack(fill="x", pady=2)
        return entry

    # ==================== LÓGICA DO MOTOR ====================
    def carregar_imagem(self, nome_arquivo):
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            self.resetar_transformacoes()
        except Exception as e:
            print(e)

    def resetar_transformacoes(self):
        """Limpa a fila e a matriz de operações."""
        self.matriz_global = get_matriz_identidade()
        self.fila_historico.clear()
        self._atualizar_texto_fila()
        self.renderizar_tela()

    def adicionar_operacao(self):
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

            # ACUMULA AS MATRIZES (Multiplicação M_local x M_global)
            self.matriz_global = matriz_local @ self.matriz_global
            self.fila_historico.append(texto_hist)

            self._atualizar_texto_fila()
            self.renderizar_tela()
        except ValueError:
            pass  # Ignora caso digitem algo errado

    def _atualizar_texto_fila(self):
        self.txt_fila.delete("1.0", "end")
        if not self.fila_historico:
            self.txt_fila.insert("1.0", "Nenhuma (Original)")
        else:
            linhas = [f"{i + 1}. {txt}" for i, txt in enumerate(self.fila_historico)]
            self.txt_fila.insert("1.0", "\n".join(linhas))

    def renderizar_tela(self):
        if self.matriz_original is None: return

        # 1. Renderiza a Transformação Dinâmica (Calcula o bounding box exato da forma geométrica)
        img_transf = renderizar_imagem(self.matriz_original, self.matriz_global, modo="auto")
        img_ctk_transf = ctk.CTkImage(light_image=img_transf, size=img_transf.size)
        self.lbl_imagem_transf.configure(image=img_ctk_transf)

        # 2. Renderiza a Imagem Finalizada (Mantendo-se fixa no tamanho inicial)
        img_final = renderizar_imagem(self.matriz_original, self.matriz_global, modo="original")
        img_ctk_final = ctk.CTkImage(light_image=img_final, size=img_final.size)
        self.lbl_imagem_final.configure(image=img_ctk_final)