import os
import customtkinter as ctk  # Biblioteca para criar a interface gráfica (janelas, botões)
# Importamos as funções de carregar imagem e a nossa matemática do outro arquivo
from src.algoritmos.utils import carregar_imagem_pgm
from src.algoritmos.geometria import (
    get_matriz_identidade, matriz_translacao, matriz_escala,
    matriz_rotacao, matriz_cisalhamento, matriz_reflexao, renderizar_imagem
)


class GeometriaFrame(ctk.CTkFrame):
    """
    Esta classe cria a 'tela' principal onde o usuário interage.
    Ela herda do CustomTkinter para já vir com funcionalidades de interface gráfica.
    """

    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        # ==================== VARIÁVEIS DE ESTADO (A MEMÓRIA DO APP) ====================
        self.matriz_original = None  # Guarda os pixels originais da imagem intocados

        # O segredo da Computação Gráfica: nós não alteramos a imagem a cada passo.
        # Nós acumulamos a matemática em UMA única matriz chamada "matriz_global".
        # Ela começa como "Identidade" (neutra, sem efeito).
        self.matriz_global = get_matriz_identidade()

        self.fila_historico = []  # Lista apenas para mostrar na tela o que o usuário já clicou

        # Configuração da grade (Grid) da tela: Coluna 0 (Menu lateral), Coluna 1 (Imagens)
        self.grid_columnconfigure(0, weight=0, minsize=320)
        self.grid_columnconfigure(1, weight=1)
        self.grid_rowconfigure(0, weight=1)

        # ==========================================
        # MENU LATERAL ESQUERDO (Controles)
        # ==========================================
        self.frame_controles = ctk.CTkFrame(self, fg_color="white", corner_radius=15)
        self.frame_controles.grid(row=0, column=0, padx=20, pady=20, sticky="nsew")

        ctk.CTkLabel(self.frame_controles, text="Fotos e\nFiltros", font=("Arial", 22, "bold"),
                     text_color="#213555").pack(pady=20)

        # 1. Seleção de Imagem e Histórico
        opcoes_imagens = ["lena.pgm", "lena.pbm", "airplane.pgm", "airplane.pbm"]
        self.cmb_imagem = ctk.CTkComboBox(self.frame_controles, values=opcoes_imagens, command=self.carregar_imagem)
        self.cmb_imagem.pack(pady=5, padx=20, fill="x")

        # Caixa de texto que mostra o histórico de operações aplicadas
        ctk.CTkLabel(self.frame_controles, text="Fila de Operações:", font=("Arial", 12, "bold"),
                     text_color="gray").pack(anchor="w", padx=20, pady=(15, 0))
        self.txt_fila = ctk.CTkTextbox(self.frame_controles, height=80, fg_color="#F0F0F0", text_color="#213555")
        self.txt_fila.pack(padx=20, pady=5, fill="x")

        # Botão para zerar a matemática e voltar à imagem original
        self.btn_limpar = ctk.CTkButton(self.frame_controles, text="Limpar Transformações", fg_color="#E74C3C",
                                        hover_color="#C0392B", command=self.resetar_transformacoes)
        self.btn_limpar.pack(pady=5, padx=20, fill="x")

        # 2. Escolha de Nova Operação
        ctk.CTkLabel(self.frame_controles, text="Nova Transformação:", font=("Arial", 14, "bold"),
                     text_color="#213555").pack(anchor="w", padx=20, pady=(20, 5))

        # Quando o usuário escolhe uma operação, a função "atualizar_inputs" é chamada
        # para trocar as caixinhas de digitação (ex: de Ângulo para Eixo X e Y).
        opcoes_op = ["Translação", "Escala", "Reflexão", "Cisalhamento", "Rotação"]
        self.cmb_operacao = ctk.CTkComboBox(self.frame_controles, values=opcoes_op, command=self.atualizar_inputs)
        self.cmb_operacao.pack(padx=20, fill="x")

        # Container vazio onde as caixinhas de digitação numéricas vão aparecer
        self.frame_parametros = ctk.CTkFrame(self.frame_controles, fg_color="transparent")
        self.frame_parametros.pack(fill="x", padx=20)
        self.inputs_dinamicos = {}  # Dicionário para guardar as caixinhas que criarmos

        self.btn_adicionar = ctk.CTkButton(self.frame_controles, text="Aplicar", fg_color="#213555",
                                           hover_color="#45a049", command=self.adicionar_operacao)
        self.btn_adicionar.pack(pady=20, padx=20, fill="x")

        # ==========================================
        # ÁREA DIREITA (Telas de Exibição das Imagens)
        # ==========================================
        self.frame_exibicao = ctk.CTkFrame(self, fg_color="#D9D9D9", corner_radius=15)
        self.frame_exibicao.grid(row=0, column=1, padx=(0, 20), pady=20, sticky="nsew")

        self.frame_exibicao.grid_columnconfigure(0, weight=1)
        self.frame_exibicao.grid_columnconfigure(1, weight=1)
        self.frame_exibicao.grid_rowconfigure(1, weight=1)

        # Labels (Textos e Molduras das imagens)
        ctk.CTkLabel(self.frame_exibicao, text="Transformação", font=("Arial", 16, "bold"), text_color="#213555").grid(
            row=0, column=0, pady=(15, 0))
        ctk.CTkLabel(self.frame_exibicao, text="Enquadrada", font=("Arial", 16, "bold"), text_color="#213555").grid(
            row=0, column=1, pady=(15, 0))

        # Onde a imagem final será desenhada
        self.lbl_imagem_transf = ctk.CTkLabel(self.frame_exibicao, text="", bg_color="#D9D9D9")
        self.lbl_imagem_transf.grid(row=1, column=0, padx=10, pady=10)

        self.lbl_imagem_final = ctk.CTkLabel(self.frame_exibicao, text="", bg_color="#D9D9D9")
        self.lbl_imagem_final.grid(row=1, column=1, padx=10, pady=10)

        # Inicializa o app carregando a primeira imagem e os inputs de translação
        self.carregar_imagem(opcoes_imagens[0])
        self.atualizar_inputs("Translação")

    # ==================== LÓGICA DE UI (INTERFACE) ====================
    def atualizar_inputs(self, escolha):
        """
        Muda os campos de digitação na tela. Cada matemática precisa de parâmetros
        diferentes. A Rotação só precisa de 1 número (ângulo), a Translação precisa de 2 (X e Y).
        """
        # Limpa os campos anteriores da tela
        for widget in self.frame_parametros.winfo_children(): widget.destroy()
        self.inputs_dinamicos.clear()

        # Cria novos campos baseados na escolha do ComboBox
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
            self.inputs_dinamicos['hx'] = self._criar_input("Fator X (ex: 0.2):", "1.0")
            self.inputs_dinamicos['hy'] = self._criar_input("Fator Y (ex: 0.0):", "0.0")
        elif escolha == "Rotação":
            self.inputs_dinamicos['angulo'] = self._criar_input("Ângulo em Graus:", "90")

    def _criar_input(self, texto, valor_padrao):
        """Função auxiliar para criar os textos e caixinhas de digitação padronizadas."""
        ctk.CTkLabel(self.frame_parametros, text=texto, text_color="#213555").pack(anchor="w", pady=(10, 0))
        entry = ctk.CTkEntry(self.frame_parametros)
        entry.insert(0, valor_padrao)
        entry.pack(fill="x", pady=2)
        return entry

    # ==================== LÓGICA DO MOTOR MATEMÁTICO ====================
    def carregar_imagem(self, nome_arquivo):
        """Lê o arquivo físico do computador, salva a matriz de pixels original e zera as contas."""
        caminho = os.path.join("assets", nome_arquivo)
        try:
            self.matriz_original = carregar_imagem_pgm(caminho)
            self.resetar_transformacoes()
        except Exception as e:
            print(e)

    def resetar_transformacoes(self):
        """
        Botão de pânico! Limpa a lista de operações e transforma a 'matriz_global'
        de volta em uma Matriz Identidade (que anula qualquer transformação).
        """
        self.matriz_global = get_matriz_identidade()
        self.fila_historico.clear()
        self._atualizar_texto_fila()
        self.renderizar_tela()

    def adicionar_operacao(self):
        """
        O CORAÇÃO DO PROGRAMA!
        Aqui nós criamos a matriz da operação atual e MULTIPLICAMOS ela pelas operações passadas.
        """
        op = self.cmb_operacao.get()
        matriz_local = get_matriz_identidade()
        texto_hist = op

        try:
            # 1. Pega o número que o usuário digitou e gera a matriz geométrica 3x3 correspondente
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

            # 2. COMPOSIÇÃO DE MATRIZES
            # O operador '@' no Python faz a multiplicação de matrizes.
            # Estamos dizendo: "Pegue tudo que já fizemos (matriz_global) e aplique a nova regra (matriz_local)".
            # A ordem (local @ global) é crucial: significa que a última operação será aplicada sobre o resultado da anterior.
            self.matriz_global = matriz_local @ self.matriz_global

            # Adiciona o texto bonitinho na caixinha para o usuário ver
            self.fila_historico.append(texto_hist)
            self._atualizar_texto_fila()

            # Manda redesenhar a imagem na tela com a nova matemática acumulada
            self.renderizar_tela()
        except ValueError:
            pass  # Se o usuário digitar um texto em vez de número, ignora o erro para o app não fechar

    def _atualizar_texto_fila(self):
        """Atualiza a caixa de texto cinza na interface com as operações enumeradas."""
        self.txt_fila.delete("1.0", "end")
        if not self.fila_historico:
            self.txt_fila.insert("1.0", "Nenhuma (Original)")
        else:
            linhas = [f"{i + 1}. {txt}" for i, txt in enumerate(self.fila_historico)]
            self.txt_fila.insert("1.0", "\n".join(linhas))

    def renderizar_tela(self):
        """
        Pega a matriz de pixels original, pega a matemática acumulada (matriz_global) e
        pede para a função `renderizar_imagem` calcular a imagem final.
        """
        if self.matriz_original is None: return

        # Imagem 1: Janela 'Transformação' (Modo "auto")
        # A tela de desenho vai crescer ou encolher dinamicamente para garantir que
        # a imagem transformada não seja cortada nas bordas.
        img_transf = renderizar_imagem(self.matriz_original, self.matriz_global, modo="auto")
        img_ctk_transf = ctk.CTkImage(light_image=img_transf, size=img_transf.size)
        self.lbl_imagem_transf.configure(image=img_ctk_transf)  # Joga na tela do app

        # Imagem 2: Janela 'Enquadrada' (Modo "original")
        # Mantém a janela fixa no tamanho e proporção originais da foto.
        # O que girar ou escalar para fora desse quadro, desaparece (é cortado/clipado).
        img_final = renderizar_imagem(self.matriz_original, self.matriz_global, modo="original")
        img_ctk_final = ctk.CTkImage(light_image=img_final, size=img_final.size)
        self.lbl_imagem_final.configure(image=img_ctk_final)  # Joga na tela do app