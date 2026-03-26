import customtkinter as ctk
from PIL import Image as PILImage
import sys
import os
import numpy as np

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from algoritmos import filters
from algoritmos.filters import IMAGES_PATH, carregar_pgm, aplicar_filtro_media, aplicar_filtro_mediana


class FilterFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        # Configuração de grid principal (3 colunas para os cards)
        self.grid_columnconfigure((0, 1, 2), weight=1, pad=20)
        self.grid_rowconfigure(0, weight=0)
        self.grid_rowconfigure(1, weight=1)

        # --- COLUNA 1: IMAGEM ORIGINAL ---
        self.card_original = ctk.CTkFrame(self, width=300, height=450)
        self.card_original.grid(row=0, column=0, padx=10, pady=10, sticky="nsew")

        ctk.CTkLabel(self.card_original, text="Imagem Original", font=("Arial", 16, "bold")).pack(pady=10)

        self.canvas_original = ctk.CTkFrame(self.card_original, width=256, height=256, fg_color="gray20")
        self.canvas_original.pack(pady=5, padx=20)

        # Seletor de Imagem
        self.img_selector = ctk.CTkOptionMenu(
            self.card_original,
            values=["Lena.pgm", "Lenasalp.pgm", "Lenag.pgm", "Airplane.pgm"],
            command=self.on_image_selected
        )
        self.img_selector.pack(pady=20)

        # --- COLUNA 2: FILTROS (PAINEL CENTRAL) ---
        self.card_filters = ctk.CTkFrame(self, width=300, height=450)
        self.card_filters.grid(row=0, column=1, padx=10, pady=10, sticky="nsew")

        ctk.CTkLabel(self.card_filters, text="Filtros", font=("Arial", 16, "bold")).pack(pady=10)

        # Seletor de Filtro
        self.filter_selector = ctk.CTkOptionMenu(
            self.card_filters,
            values=["Original", "Media", "Mediana", "Passa Alto", "Roberts em X", "Roberts em Y", "Roberts Cruzado",
                    "Prewitt em X", "Prewitt em Y", "Prewitt Cruzado", "Sobel em X", "Sobel em Y", "Sobel Cruzado",
                    "Alto Reforço"],
            command=self.ao_selecionar_filtro
        )
        self.filter_selector.pack(pady=10)

        # Matrix de Input (3x3)
        self.matrix_frame = ctk.CTkFrame(self.card_filters, fg_color="transparent")
        self.matrix_frame.pack(pady=10)

        self.matrix_entries = []
        for r in range(3):
            for c in range(3):
                entry = ctk.CTkEntry(self.matrix_frame, width=50, justify="center")
                entry.grid(row=r, column=c, padx=2, pady=2)
                entry.insert(0, "0")
                self.matrix_entries.append(entry)

        # Criar o Label (sem dar pack direto)
        self.label_ahb = ctk.CTkLabel(self.card_filters, text="Valor A (Alto Reforço):")
        self.entry_ahb = ctk.CTkEntry(self.card_filters, width=60, justify="center")
        self.entry_ahb.insert(0, "1.5")

        # o botão "Aplicar Filtro"
        self.btn_aplicar = ctk.CTkButton(
            self.card_filters,
            text="Aplicar Filtro",
            command=lambda: self.executar_filtro(self.filter_selector.get())  # Ele pega o valor atual e executa
        )
        self.btn_aplicar.pack(pady=20)

        # --- COLUNA 3: IMAGEM PROCESSADA ---
        self.card_processed = ctk.CTkFrame(self, width=300, height=450)
        self.card_processed.grid(row=0, column=2, padx=10, pady=10, sticky="nsew")

        ctk.CTkLabel(self.card_processed, text="Imagem Processada", font=("Arial", 16, "bold")).pack(pady=10)

        self.canvas_processed = ctk.CTkFrame(self.card_processed, width=256, height=256, fg_color="gray20")
        self.canvas_processed.pack(pady=5, padx=20)

        # --- SEÇÃO INFERIOR: TABELAS DE PIXELS ---
        self.table_section = ctk.CTkFrame(self, fg_color="transparent")
        self.table_section.grid(row=1, column=0, columnspan=3, padx=10, pady=20, sticky="nsew")
        self.table_section.grid_columnconfigure((0, 1), weight=1)

        # Tabela Original
        self.label_tab_orig = ctk.CTkLabel(self.table_section, text="Tabela de Pixels (Original)", font=("Arial", 14))
        self.label_tab_orig.grid(row=0, column=0, sticky="w", padx=5)
        self.txt_pixels_orig = ctk.CTkTextbox(self.table_section, height=500, font=("Courier New", 12), wrap="none")
        self.txt_pixels_orig.grid(row=1, column=0, padx=5, sticky="nsew")
        self.txt_pixels_orig.insert("0.0", "Aguardando carregamento da imagem...")

        # Tabela Processada
        self.label_tab_proc = ctk.CTkLabel(self.table_section, text="Tabela de Pixels (Processada)", font=("Arial", 14))
        self.label_tab_proc.grid(row=0, column=1, sticky="w", padx=5)
        self.txt_pixels_proc = ctk.CTkTextbox(self.table_section, height=500, font=("Courier New", 12), wrap="none")
        self.txt_pixels_proc.grid(row=1, column=1, padx=5, sticky="nsew")
        self.txt_pixels_proc.insert("0.0", "Selecione um filtro para gerar os dados...")

        self.ultimo_pixel = (-1, -1)
        self.scheduled_update = None

    def executar_filtro(self):
        # Futura integração com src/algoritmos/filters.py
        print(f"Executando {self.filter_selector.get()}...")

    def ao_selecionar_filtro(self, filtro_escolhido):
        # 1. Atualiza os números da matriz (Kernel) imediatamente
        if filtro_escolhido == "Media":
            self.atualizar_visualizacao_kernel(filters.KERNELS["media"])
        elif filtro_escolhido == "Mediana":
            self.atualizar_visualizacao_kernel(np.zeros((3, 3)))
        elif filtro_escolhido == "Passa Alto":
            self.atualizar_visualizacao_kernel(filters.KERNELS["passaAlto"])
        elif filtro_escolhido == "Roberts em X":
            self.atualizar_visualizacao_kernel(filters.KERNELS["robertsX"])
        elif filtro_escolhido == "Roberts em Y":
            self.atualizar_visualizacao_kernel(filters.KERNELS["robertsY"])
        elif filtro_escolhido == "Roberts Cruzado":
            self.atualizar_visualizacao_kernel(np.zeros((3, 3)))
        elif filtro_escolhido == "Prewitt em X":
            self.atualizar_visualizacao_kernel(filters.KERNELS["prewittX"])
        elif filtro_escolhido == "Prewitt em Y":
            self.atualizar_visualizacao_kernel(filters.KERNELS["prewittY"])
        elif filtro_escolhido == "Prewitt Cruzado":
            self.atualizar_visualizacao_kernel(np.zeros((3, 3)))
        elif filtro_escolhido == "Sobel em X":
            self.atualizar_visualizacao_kernel(filters.KERNELS["sobelX"])
        elif filtro_escolhido == "Sobel em Y":
            self.atualizar_visualizacao_kernel(filters.KERNELS["sobelY"])
        elif filtro_escolhido == "Sobel Cruzado":
            self.atualizar_visualizacao_kernel(np.zeros((3, 3)))
        else:
            self.atualizar_visualizacao_kernel(filters.KERNELS["none"])

        # 2. Lógica do High Boost (Mostrar/Esconder campo A)
        if filtro_escolhido == "Alto Reforço":
            self.label_ahb.pack(pady=(10, 0))
            self.entry_ahb.pack(pady=5)
        else:
            if hasattr(self, 'label_ahb'):
                self.label_ahb.pack_forget()
                self.entry_ahb.pack_forget()

    # Lógica disparada ao selecionar uma imagem no menu suspenso
    def on_image_selected(self, escolha):
        index = self.img_selector._values.index(escolha)
        path = IMAGES_PATH.get(index)

        if path:
            try:
                # 1. Carrega os dados brutos da imagem
                self.matriz_original = filters.load_pgm(path)

                # 2. Converte a matriz Numpy para uma imagem PIL
                pil_img = PILImage.fromarray(self.matriz_original)

                # 3. Converte para CTkImage para exibir na interface
                # O tamanho 256x256 é o que você definiu no canvas
                self.img_tk = ctk.CTkImage(light_image=pil_img, size=(256, 256))

                # 4. Exibe a imagem em um Label dentro do canvas_original
                if not hasattr(self, "label_img_orig"):
                    self.label_img_orig = ctk.CTkLabel(self.canvas_original, text="")
                    self.label_img_orig.pack()

                self.label_img_orig.configure(image=self.img_tk)

                # 5. Atualiza a tabela de texto com os pixels (opcional)
                self.render_pixel_table(self.txt_pixels_orig, self.matriz_original)
                # No método on_image_selected, onde você configurou o Label:
                self.label_img_orig.bind("<Motion>", self.on_mouse_move)
                # ADICIONE ESTA LINHA:
                self.label_img_orig.bind("<Leave>", self.voltar_estado_inicial)

            except Exception as e:
                print(f"Erro ao carregar imagem: {e}")

    # Função que mostra os pixels na tabela original da matriz
    def render_pixel_table(self, textbox, matriz):
        textbox.configure(state="normal")
        textbox.delete("0.0", "end")

        largura_celula = 4
        linhas_tabela = []

        # Topo da tabela
        topo = "┌" + "┬".join("─" * (largura_celula + 2) for _ in range(matriz.shape[1])) + "┐"
        linhas_tabela.append(topo)

        for r in range(matriz.shape[0]):
            # Linha de dados: │ 255 │  0  │
            linha_dados = "│ " + " │ ".join(f"{str(val).center(largura_celula)}" for val in matriz[r]) + " │"
            linhas_tabela.append(linha_dados)

            # Linha separadora entre os pixels
            if r < matriz.shape[0] - 1:
                separador = "├" + "┼".join("─" * (largura_celula + 2) for _ in range(matriz.shape[1])) + "┤"
                linhas_tabela.append(separador)

        # Base da tabela
        base = "└" + "┴".join("─" * (largura_celula + 2) for _ in range(matriz.shape[1])) + "┘"
        linhas_tabela.append(base)

        textbox.insert("0.0", "\n".join(linhas_tabela))
        textbox.configure(state="disabled")

    # Lógica disparada ao selecionar um filtro ou interagir com o menu de filtros
    def executar_filtro(self, filtro_escolhido):
        print(f"Filtro selecionado: '{filtro_escolhido}'")
        if not hasattr(self, "matriz_original"): return

        if filtro_escolhido == "Media":
            self.matriz_processada = filters.aplicar_filtro_media(self.matriz_original)

        elif filtro_escolhido == "Mediana":
            self.matriz_processada = filters.aplicar_filtro_mediana(self.matriz_original)

        elif filtro_escolhido == "Passa Alto":
            kernel = filters.KERNELS["passaAlto"]
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Roberts em X":
            kernel = filters.KERNELS["robertsX"]
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Roberts em Y":
            kernel = filters.KERNELS["robertsY"]
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Prewitt em X":
            kernel = filters.KERNELS["prewittX"]
            # Gradientes não são normalizados na convolução (soma = 0)
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Prewitt em Y":
            kernel = filters.KERNELS["prewittY"]
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Prewitt Cruzado":
            self.matriz_processada = filters.aplicar_prewitt_completo(self.matriz_original)

        # Roberts Cruzado (Magnitude de X e Y combinados)
        elif filtro_escolhido == "Roberts Cruzado":
            self.matriz_processada = filters.aplicar_roberts_cruzado(self.matriz_original)

        elif filtro_escolhido == "Sobel em X":
            kernel = filters.KERNELS["sobelX"]
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Sobel em Y":
            kernel = filters.KERNELS["sobelY"]
            self.matriz_processada = filters.aplicar_convolucao(self.matriz_original, kernel, normalizar=False)

        elif filtro_escolhido == "Sobel Cruzado":
            self.matriz_processada = filters.aplicar_sobel_cruzado(self.matriz_original)

        elif filtro_escolhido == "Alto Reforço":
            try:
                valor_a = float(self.entry_ahb.get())
            except:
                valor_a = 1.5
            self.matriz_processada = filters.high_boost_filter(self.matriz_original, A=valor_a)

        elif filtro_escolhido == "Original":
            self.matriz_processada = self.matriz_original.copy()

        # Se o filtro foi reconhecido, a matriz existirá e o erro sumirá
        if hasattr(self, "matriz_processada"):
            self.atualizar_imagem_processada()

    def atualizar_visualizacao_kernel(self, matriz_kernel):
        """Lógica para preencher os CTkEntry 3x3 com os valores do Kernel."""
        valores = matriz_kernel.flatten()
        for i, valor in enumerate(valores):
            self.matrix_entries[i].delete(0, "end")
            # Mostra 2 casas decimais (ex: 0.11 para a média)
            self.matrix_entries[i].insert(0, f"{valor:.2f}")

    def atualizar_imagem_processada(self):
        """Converte a matriz calculada em imagem para o CustomTkinter."""
        pil_img = PILImage.fromarray(self.matriz_processada)
        self.img_tk_proc = ctk.CTkImage(light_image=pil_img, size=(256, 256))

        if not hasattr(self, "label_img_proc"):
            self.label_img_proc = ctk.CTkLabel(self.canvas_processed, text="")
            self.label_img_proc.pack()

        self.label_img_proc.configure(image=self.img_tk_proc)

        # Atualiza a tabela de pixels com os novos valores
        self.render_pixel_table_estatica(self.txt_pixels_proc, self.matriz_processada)

    # Detecta a posição do mouse sobre a imagem e calcula qual pixel da matriz corresponde
    def on_mouse_move(self, event):
        if not hasattr(self, "matriz_original"): return

        # Calcula as coordenadas do pixel com base no tamanho do canvas (256x256)
        x = int(event.x)
        y = int(event.y)

        if 0 <= x < 256 and 0 <= y < 256:
            # 1. Destaca na tabela original
            self.render_pixel_table_com_destaque(self.txt_pixels_orig, self.matriz_original, x, y)

            # 2. Destaca na tabela processada (se ela existir)
            if hasattr(self, "matriz_processada"):
                self.render_pixel_table_com_destaque(self.txt_pixels_proc, self.matriz_processada, x, y)

    # Cria uma tabela de texto que mostra apenas uma vizinhança ao redor do pixel focado
    def render_pixel_table_com_destaque(self, textbox, matriz, x_alvo, y_alvo):
        textbox.configure(state="normal")
        textbox.delete("1.0", "end")

        raio = 12  # Mostra uma vizinhança de 5x5 ou 7x7 ao redor do pixel

        # Define os limites para não sair da imagem
        y_min, y_max = max(0, y_alvo - raio), min(matriz.shape[0], y_alvo + raio + 1)
        x_min, x_max = max(0, x_alvo - raio), min(matriz.shape[1], x_alvo + raio + 1)

        for r in range(y_min, y_max):
            linha_texto = ""
            for c in range(x_min, x_max):
                valor = matriz[r, c]
                item = f"{str(valor).center(4)} "

                # Se for o pixel exato onde o mouse está, marcamos a posição
                if r == y_alvo and c == x_alvo:
                    # Inserimos o valor com uma tag de cor
                    pos_inicio = textbox.index("end-1c")
                    textbox.insert("end", item, "pixel_azul")
                else:
                    textbox.insert("end", item)

            textbox.insert("end", "\n")

        # Configura a cor do destaque
        textbox.tag_config("pixel_azul", background="#007ACC", foreground="white")
        textbox.configure(state="disabled")

    # Reseta a visualização da tabela para o canto superior (0,0) quando o mouse sai da imagem
    def voltar_estado_inicial(self, event):
        if not hasattr(self, "matriz_original"): return

        self.ultimo_pixel = (-1, -1)

        if self.scheduled_update:
            self.after_cancel(self.scheduled_update)
            self.scheduled_update = None

        # 1. Reseta a tabela Original para o canto (0,0)
        self.render_pixel_table_estatica(self.txt_pixels_orig, self.matriz_original)

        # 2. Reseta a tabela Processada para o canto (0,0) - ADICIONE ESTA LINHA:
        if hasattr(self, "matriz_processada"):
            self.render_pixel_table_estatica(self.txt_pixels_proc, self.matriz_processada)

    # Renderiza uma visão fixa (geralmente o topo da imagem) na tabela de pixels
    def render_pixel_table_estatica(self, textbox, matriz):
        textbox.configure(state="normal")
        textbox.delete("1.0", "end")
        textbox.tag_remove("pixel_alvo", "1.0", "end")

        raio = 18
        recorte = matriz[:raio, :raio]
        largura_val = 4

        res = []
        res.append("┌" + "┬".join("─" * (largura_val + 2) for _ in range(recorte.shape[1])) + "┐")
        for r in range(recorte.shape[0]):
            res.append("│ " + " │ ".join(f"{str(val).center(largura_val)}" for val in recorte[r]) + " │")
            if r < recorte.shape[0] - 1:
                res.append("├" + "┼".join("─" * (largura_val + 2) for _ in range(recorte.shape[1])) + "┤")
        res.append("└" + "┴".join("─" * (largura_val + 2) for _ in range(recorte.shape[1])) + "┘")

        textbox.insert("1.0", "\n".join(res))
        textbox.see("1.0")
        textbox.configure(state="disabled")