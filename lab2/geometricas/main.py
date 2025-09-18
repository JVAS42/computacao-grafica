import tkinter as tk
from tkinter import ttk, messagebox
import math


# --- Funções dos Algoritmos de Transformação (permanecem as mesmas) ---

from translacao import translacao
from escala import escala
from rotacao import rotacao
from reflexao import reflexao
from cisalhamento import cisalhamento
from transformacoes_compostas import multiplicar_matrizes


def aplicar_transformacao_ponto(ponto, matriz):
    x, y = ponto
    vetor = [x, y, 1]
    x_novo = matriz[0][0] * vetor[0] + matriz[0][1] * vetor[1] + matriz[0][2] * vetor[2]
    y_novo = matriz[1][0] * vetor[0] + matriz[1][1] * vetor[1] + matriz[1][2] * vetor[2]
    return (x_novo, y_novo)


class TransformacaoGUI:
    def __init__(self, master):
        self.master = master
        self.master.title("Transformações Geométricas 2D")
        self.master.geometry("1600x800")

        self.objeto_original = [(0, 0), (1, 0), (1, 1), (0, 1)]
        self.objeto_atual = list(self.objeto_original)  # ALTERAÇÃO: Mantém o estado atual do objeto
        self.sequencia_passos = []

        self.CANVAS_WIDTH = 1280
        self.CANVAS_HEIGHT = 720
        self.SCALE = 50
        self.AXIS_COLOR = "#808080"
        self.origin_x = self.CANVAS_WIDTH / 2
        self.origin_y = self.CANVAS_HEIGHT / 2

        self.frame_controle = ttk.Frame(self.master, padding="10")
        self.frame_controle.pack(side=tk.LEFT, fill=tk.Y)
        self.frame_grafico = ttk.Frame(self.master, padding="10")
        self.frame_grafico.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        self.criar_widgets_controle()
        self.criar_area_grafico()

    def criar_widgets_controle(self):
        # (Interface de controle praticamente a mesma)
        ttk.Label(self.frame_controle, text="Transformação:").grid(row=0, column=0, columnspan=2, sticky="w")
        self.algoritmo_var = tk.StringVar()
        self.combo_algoritmo = ttk.Combobox(
            self.frame_controle,
            textvariable=self.algoritmo_var,
            values=["Translação", "Escala", "Rotação", "Reflexão", "Cisalhamento"]
        )
        self.combo_algoritmo.grid(row=1, column=0, columnspan=2, sticky="ew")
        self.combo_algoritmo.bind("<<ComboboxSelected>>", self.atualizar_entradas)

        self.frame_entradas = ttk.Frame(self.frame_controle)
        self.frame_entradas.grid(row=2, column=0, columnspan=2, sticky="ew")
        self.widgets_entrada = {}

        # O botão de sequência agora aplica ao objeto atual, não ao original
        ttk.Button(self.frame_controle, text="Adicionar Passo à Sequência", command=self.adicionar_passo) \
            .grid(row=3, column=0, columnspan=2, sticky="ew")
        ttk.Label(self.frame_controle, text="Sequência Definida:").grid(row=4, column=0, columnspan=2, sticky="w")
        self.lista_sequencia = tk.Listbox(self.frame_controle, height=6)
        self.lista_sequencia.grid(row=5, column=0, columnspan=2, sticky="ew")

        ttk.Button(self.frame_controle, text="Limpar Sequência", command=self.limpar_sequencia) \
            .grid(row=6, column=0, columnspan=2, sticky="ew", pady=5)

        ttk.Button(self.frame_controle, text="Aplicar e Desenhar", command=self.aplicar_e_desenhar) \
            .grid(row=7, column=0, columnspan=2, pady=10, sticky="ew")

        # ALTERAÇÃO: Adiciona um botão para resetar o objeto
        ttk.Button(self.frame_controle, text="Resetar Objeto", command=self.resetar_objeto) \
            .grid(row=8, column=0, columnspan=2, sticky="ew", pady=5)

        ttk.Label(self.frame_controle, text="Coordenadas Atuais:").grid(row=9, column=0, columnspan=2, sticky="w")
        self.texto_coords = tk.Text(self.frame_controle, height=15, width=40, state=tk.DISABLED)
        self.texto_coords.grid(row=10, column=0, columnspan=2, sticky="nsew")

        self.combo_algoritmo.current(0)
        self.atualizar_entradas()

    # ALTERAÇÃO: Adicionada função para resetar
    def resetar_objeto(self):
        """Restaura o objeto para sua forma e posição originais."""
        self.objeto_atual = list(self.objeto_original)
        self.limpar_sequencia()
        self.plotar_objetos(self.objeto_atual, "Objeto Resetado")
        self.atualizar_texto_coordenadas(self.objeto_atual)

    def aplicar_e_desenhar(self):
        # ALTERAÇÃO: A lógica agora opera sobre self.objeto_atual
        matriz_final = [[1, 0, 0], [0, 1, 0], [0, 0, 1]]

        if self.sequencia_passos:
            for passo in reversed(self.sequencia_passos):
                matriz_final = multiplicar_matrizes(passo['matriz'], matriz_final)
            titulo = "Sequência Aplicada"
        else:
            matriz_final, _ = self.obter_matriz_da_interface(self.algoritmo_var.get())
            titulo = self.algoritmo_var.get()

        if matriz_final:
            # Aplica a transformação ao objeto ATUAL, não ao original
            novos_pontos = [aplicar_transformacao_ponto(p, matriz_final) for p in self.objeto_atual]

            # Atualiza o estado do objeto
            self.objeto_atual = novos_pontos

            self.atualizar_texto_coordenadas(self.objeto_atual)
            self.plotar_objetos(self.objeto_atual, titulo)

    def plotar_objetos(self, objeto, titulo):
        # ALTERAÇÃO: Esta função agora desenha apenas UM objeto.
        self.canvas.delete("all")
        self.draw_axes()
        self.title_label.config(text=titulo)

        # Se o objeto é o original, desenha em azul. Se foi transformado, em vermelho.
        cor_desenho = 'blue' if objeto == self.objeto_original else 'red'

        # Converte coordenadas e desenha o polígono atual
        coords_obj = self._converter_coords_para_canvas(objeto)
        self.canvas.create_polygon(coords_obj, outline=cor_desenho, fill='', width=2, tags="objeto_atual")

    def atualizar_texto_coordenadas(self, objeto):
        # ALTERAÇÃO: Mostra apenas as coordenadas do objeto atual.
        self.texto_coords.config(state=tk.NORMAL)
        self.texto_coords.delete(1.0, tk.END)
        texto = "Coordenadas Atuais:\n" + "\n".join(f"({x:.2f}, {y:.2f})" for x, y in objeto)
        self.texto_coords.insert(tk.END, texto)
        self.texto_coords.config(state=tk.DISABLED)

    def criar_area_grafico(self):
        self.title_label = ttk.Label(self.frame_grafico, text="Estado Inicial", font=("Helvetica", 14))
        self.title_label.pack(side=tk.TOP, pady=5)

        self.canvas = tk.Canvas(self.frame_grafico, width=self.CANVAS_WIDTH, height=self.CANVAS_HEIGHT, bg='white')
        self.canvas.pack(side=tk.TOP, fill=tk.BOTH, expand=True)

        self.plotar_objetos(self.objeto_atual, "Estado Inicial")
        self.atualizar_texto_coordenadas(self.objeto_atual)

    # --- Funções não modificadas ---
    def _converter_coords_para_canvas(self, pontos):
        coords_canvas = []
        for x, y in pontos:
            canvas_x = self.origin_x + x * self.SCALE
            canvas_y = self.origin_y - y * self.SCALE
            coords_canvas.extend([canvas_x, canvas_y])
        return coords_canvas

    def draw_axes(self):
        self.canvas.create_line(0, self.origin_y, self.CANVAS_WIDTH, self.origin_y, fill=self.AXIS_COLOR, width=1)
        self.canvas.create_line(self.origin_x, 0, self.origin_x, self.CANVAS_HEIGHT, fill=self.AXIS_COLOR, width=1)

    def atualizar_entradas(self, event=None):
        for widget in self.frame_entradas.winfo_children():
            widget.destroy()
        self.widgets_entrada = {}
        tipo = self.algoritmo_var.get()

        if tipo == "Translação":
            self.criar_campo_entrada("tx:", 0); self.criar_campo_entrada("ty:", 1)
        elif tipo == "Escala":
            self.criar_campo_entrada("sx:", 0, "1"); self.criar_campo_entrada("sy:", 1, "1")
        elif tipo == "Rotação":
            self.criar_campo_entrada("Ângulo (°):", 0)
        elif tipo == "Reflexão":
            ttk.Label(self.frame_entradas, text="Eixo:").grid(row=0, column=0, sticky="w");
            self.widgets_entrada["eixo"] = ttk.Combobox(self.frame_entradas, values=["x", "y", "origem"]);
            self.widgets_entrada["eixo"].grid(row=0, column=1, sticky="ew");
            self.widgets_entrada["eixo"].current(0)
        elif tipo == "Cisalhamento":
            self.criar_campo_entrada("shx:", 0); self.criar_campo_entrada("shy:", 1)

    def criar_campo_entrada(self, label, linha, valor_padrao="0"):
        ttk.Label(self.frame_entradas, text=label).grid(row=linha, column=0, sticky="w", padx=5, pady=2)
        entrada = ttk.Entry(self.frame_entradas)
        entrada.grid(row=linha, column=1, sticky="ew", padx=5, pady=2)
        entrada.insert(0, valor_padrao)
        self.widgets_entrada[label.replace(":", "")] = entrada

    def obter_matriz_da_interface(self, tipo):
        try:
            if tipo == "Translação":
                tx = float(self.widgets_entrada["tx"].get()); ty = float(
                    self.widgets_entrada["ty"].get()); return translacao(tx, ty), f"Translação(tx={tx}, ty={ty})"
            elif tipo == "Escala":
                sx = float(self.widgets_entrada["sx"].get()); sy = float(
                    self.widgets_entrada["sy"].get()); return escala(sx, sy), f"Escala(sx={sx}, sy={sy})"
            elif tipo == "Rotação":
                angulo = float(self.widgets_entrada["Ângulo (°)"].get()); return rotacao(
                    angulo), f"Rotação(Ângulo={angulo}°)"
            elif tipo == "Reflexão":
                eixo = self.widgets_entrada["eixo"].get(); return reflexao(eixo), f"Reflexão(eixo={eixo})"
            elif tipo == "Cisalhamento":
                shx = float(self.widgets_entrada["shx"].get()); shy = float(
                    self.widgets_entrada["shy"].get()); return cisalhamento(shx,
                                                                            shy), f"Cisalhamento(shx={shx}, shy={shy})"
        except ValueError:
            messagebox.showerror("Erro de Entrada", "Insira valores numéricos válidos.")
        except KeyError:
            messagebox.showerror("Seleção Inválida", "Selecione uma transformação válida.")
        return None, None

    def adicionar_passo(self):
        tipo = self.algoritmo_var.get()
        matriz, descricao = self.obter_matriz_da_interface(tipo)
        if matriz: self.sequencia_passos.append(
            {'matriz': matriz, 'label': descricao}); self.atualizar_lista_sequencia()

    def limpar_sequencia(self):
        self.sequencia_passos.clear();
        self.atualizar_lista_sequencia()

    def atualizar_lista_sequencia(self):
        self.lista_sequencia.delete(0, tk.END)
        for i, passo in enumerate(self.sequencia_passos): self.lista_sequencia.insert(tk.END,
                                                                                      f"{i + 1}. {passo['label']}")


if __name__ == "__main__":
    root = tk.Tk()
    app = TransformacaoGUI(root)
    root.mainloop()