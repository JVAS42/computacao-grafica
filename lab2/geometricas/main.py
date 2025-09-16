import tkinter as tk
from tkinter import ttk, messagebox
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

from translacao import translacao
from escala import escala
from rotacao import rotacao
from reflexao import reflexao
from cisalhamento import cisalhamento
from transformacoes_compostas import multiplicar_matrizes


def aplicar_transformacao_ponto(ponto, matriz, fator=50):
    x, y = ponto
    vetor = [x, y, 1]
    x_novo = matriz[0][0] * vetor[0] + matriz[0][1] * vetor[1] + matriz[0][2] * vetor[2]
    y_novo = matriz[1][0] * vetor[0] + matriz[1][1] * vetor[1] + matriz[1][2] * vetor[2]
    return (round(x_novo * fator, 4), round(y_novo * fator, 4))


class TransformacaoGUI:
    def __init__(self, master):
        self.master = master
        self.master.title("Algoritmos de Computação Gráfica")
        self.master.geometry("1000x700")

        self.objeto_original = [(0, 0), (1, 0), (1, 1), (0, 1)]

        self.sequencia_passos = []

        self.frame_controle = ttk.Frame(self.master, padding="10")
        self.frame_controle.pack(side=tk.LEFT, fill=tk.Y)
        self.frame_grafico = ttk.Frame(self.master, padding="10")
        self.frame_grafico.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        self.criar_widgets_controle()
        self.criar_area_grafico()

    def criar_widgets_controle(self):
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

        ttk.Button(self.frame_controle, text="Adicionar Passo à Sequência", command=self.adicionar_passo) \
            .grid(row=3, column=0, columnspan=2, sticky="ew")
        ttk.Label(self.frame_controle, text="Sequência Definida:").grid(row=4, column=0, columnspan=2, sticky="w")
        self.lista_sequencia = tk.Listbox(self.frame_controle, height=6)
        self.lista_sequencia.grid(row=5, column=0, columnspan=2, sticky="ew")

        ttk.Button(self.frame_controle, text="Limpar Sequência", command=self.limpar_sequencia) \
            .grid(row=6, column=0, columnspan=2, sticky="ew", pady=5)

        ttk.Button(self.frame_controle, text="Aplicar e Desenhar", command=self.aplicar_e_desenhar) \
            .grid(row=7, column=0, columnspan=2, pady=10, sticky="ew")

        ttk.Label(self.frame_controle, text="Coordenadas Geradas:").grid(row=8, column=0, columnspan=2, sticky="w")
        self.texto_coords = tk.Text(self.frame_controle, height=10, width=30, state=tk.DISABLED)
        self.texto_coords.grid(row=9, column=0, columnspan=2, sticky="nsew")

        self.combo_algoritmo.current(0)
        self.atualizar_entradas()

    def atualizar_entradas(self, event=None):
        for widget in self.frame_entradas.winfo_children():
            widget.destroy()
        self.widgets_entrada = {}
        tipo = self.algoritmo_var.get()

        if tipo == "Translação":
            self.criar_campo_entrada("tx:", 0)
            self.criar_campo_entrada("ty:", 1)
        elif tipo == "Escala":
            self.criar_campo_entrada("sx:", 0, "1")
            self.criar_campo_entrada("sy:", 1, "1")
        elif tipo == "Rotação":
            self.criar_campo_entrada("Ângulo (°):", 0)
        elif tipo == "Reflexão":
            ttk.Label(self.frame_entradas, text="Eixo:").grid(row=0, column=0, sticky="w")
            self.widgets_entrada["eixo"] = ttk.Combobox(self.frame_entradas, values=["x", "y", "origem"])
            self.widgets_entrada["eixo"].grid(row=0, column=1, sticky="ew")
            self.widgets_entrada["eixo"].current(0)
        elif tipo == "Cisalhamento":
            self.criar_campo_entrada("shx:", 0)
            self.criar_campo_entrada("shy:", 1)

    def criar_campo_entrada(self, label, linha, valor_padrao="0"):
        ttk.Label(self.frame_entradas, text=label).grid(row=linha, column=0, sticky="w", padx=5, pady=2)
        entrada = ttk.Entry(self.frame_entradas)
        entrada.grid(row=linha, column=1, sticky="ew", padx=5, pady=2)
        entrada.insert(0, valor_padrao)
        self.widgets_entrada[label.replace(":", "")] = entrada

    def obter_matriz_da_interface(self, tipo):
        try:
            if tipo == "Translação":
                tx = float(self.widgets_entrada["tx"].get())
                ty = float(self.widgets_entrada["ty"].get())
                return translacao(tx, ty), f"Translação(tx={tx}, ty={ty})"
            elif tipo == "Escala":
                sx = float(self.widgets_entrada["sx"].get())
                sy = float(self.widgets_entrada["sy"].get())
                return escala(sx, sy), f"Escala(sx={sx}, sy={sy})"
            elif tipo == "Rotação":
                angulo = float(self.widgets_entrada["Ângulo (°)"].get())
                return rotacao(angulo), f"Rotação(Ângulo={angulo}°)"
            elif tipo == "Reflexão":
                eixo = self.widgets_entrada["eixo"].get()
                return reflexao(eixo), f"Reflexão(eixo={eixo})"
            elif tipo == "Cisalhamento":
                shx = float(self.widgets_entrada["shx"].get())
                shy = float(self.widgets_entrada["shy"].get())
                return cisalhamento(shx, shy), f"Cisalhamento(shx={shx}, shy={shy})"
        except ValueError:
            messagebox.showerror("Erro de Entrada", "Insira valores numéricos válidos.")
        except KeyError:
            messagebox.showerror("Seleção Inválida", "Selecione uma transformação válida.")
        return None, None

    def adicionar_passo(self):
        tipo = self.algoritmo_var.get()
        if tipo == "Sequência":
            messagebox.showwarning("Aviso", "Selecione uma transformação individual para adicionar.")
            return

        matriz, descricao = self.obter_matriz_da_interface(tipo)
        if matriz:
            self.sequencia_passos.append({'matriz': matriz, 'label': descricao})
            self.atualizar_lista_sequencia()

    def limpar_sequencia(self):
        self.sequencia_passos.clear()
        self.atualizar_lista_sequencia()

    def atualizar_lista_sequencia(self):
        self.lista_sequencia.delete(0, tk.END)
        for i, passo in enumerate(self.sequencia_passos):
            self.lista_sequencia.insert(tk.END, f"{i + 1}. {passo['label']}")

    def aplicar_e_desenhar(self):
        tipo = self.algoritmo_var.get()
        matriz_final = None

        if tipo == "Sequência":
            if not self.sequencia_passos:
                messagebox.showwarning("Aviso", "Sequência vazia.")
                return
            matriz_final = [[1, 0, 0], [0, 1, 0], [0, 0, 1]]  # identidade
            for passo in reversed(self.sequencia_passos):
                matriz_final = multiplicar_matrizes(matriz_final, passo['matriz'])
        else:
            matriz_final, _ = self.obter_matriz_da_interface(tipo)

        if matriz_final:
            fator = 1
            transformado = [aplicar_transformacao_ponto(p, matriz_final, fator) for p in self.objeto_original]
            original_escalado = [(x * fator, y * fator) for x, y in self.objeto_original]
            self.atualizar_texto_coordenadas(self.objeto_original, transformado)
            self.plotar_objetos(original_escalado, transformado, f"Transformação: {tipo}")

    def criar_area_grafico(self):
        fig = Figure(figsize=(7, 6), dpi=100)
        self.ax = fig.add_subplot(111)
        self.canvas = FigureCanvasTkAgg(fig, master=self.frame_grafico)
        self.canvas.get_tk_widget().pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        objeto_escalado = [(x * 50, y * 50) for x, y in self.objeto_original]
        self.plotar_objetos(objeto_escalado, objeto_escalado, "Estado Inicial")

    def plotar_objetos(self, original, transformado, titulo):
        """Desenha os objetos (original e transformado) no gráfico"""
        self.ax.clear()
        orig = original + [original[0]]
        trans = transformado + [transformado[0]]
        orig_x, orig_y = zip(*orig)
        trans_x, trans_y = zip(*trans)
        self.ax.plot(orig_x, orig_y, 'b-o', label='Original')
        self.ax.plot(trans_x, trans_y, 'r-o', label='Transformado')
        self.ax.set_title(titulo)
        self.ax.legend()
        self.ax.grid(True)
        self.ax.set_aspect('equal')
        todos_x = list(orig_x) + list(trans_x)
        todos_y = list(orig_y) + list(trans_y)
        margem_x = (max(todos_x) - min(todos_x)) * 0.1 + 10
        margem_y = (max(todos_y) - min(todos_y)) * 0.1 + 10
        self.ax.set_xlim(min(todos_x) - margem_x, max(todos_x) + margem_x)
        self.ax.set_ylim(min(todos_y) - margem_y, max(todos_y) + margem_y)
        self.canvas.draw()

    def atualizar_texto_coordenadas(self, original, transformado):
        self.texto_coords.config(state=tk.NORMAL)
        self.texto_coords.delete(1.0, tk.END)
        texto = "Coordenadas Originais:\n" + "\n".join(map(str, original))
        texto += "\n\nCoordenadas Transformadas:\n" + "\n".join(f"({x:.2f}, {y:.2f})" for x, y in transformado)
        self.texto_coords.insert(tk.END, texto)
        self.texto_coords.config(state=tk.DISABLED)


if __name__ == "__main__":
    root = tk.Tk()
    app = TransformacaoGUI(root)
    root.mainloop()
