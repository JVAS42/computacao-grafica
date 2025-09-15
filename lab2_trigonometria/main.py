import tkinter as tk
from tkinter import ttk, messagebox
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import math

# Importa apenas as funções de transformação
from translacao import translacao
from escala import escala
from rotacao import rotacao
from reflexao import reflexao
from cisalhamento import cisalhamento
from transformacoes_compostas import aplicar_sequencia, multiplicar_matrizes

# -----------------------------
# Classe principal da GUI
# -----------------------------
class TransformacaoGUI:
    def __init__(self, master):
        self.master = master
        self.master.title("Algoritmos de Computação Gráfica")
        self.master.geometry("1000x700")

        # Objeto original: quadrado 1x1
        self.objeto_original = [(0, 0), (1, 0), (1, 1), (0, 1)]
        self.sequencia_passos = []

        # Frames
        self.control_frame = ttk.Frame(self.master, padding="10")
        self.control_frame.pack(side=tk.LEFT, fill=tk.Y)
        self.plot_frame = ttk.Frame(self.master, padding="10")
        self.plot_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        # Widgets
        self.create_control_widgets()
        self.create_plot_widget()

    # -----------------------------
    # Funções de Transformação
    # -----------------------------
    def aplicar_transformacao(self, ponto, matriz, fator=50):
        x, y = ponto
        vetor = [x, y, 1]
        x_novo = matriz[0][0] * vetor[0] + matriz[0][1] * vetor[1] + matriz[0][2] * vetor[2]
        y_novo = matriz[1][0] * vetor[0] + matriz[1][1] * vetor[1] + matriz[1][2] * vetor[2]
        return (round(x_novo*fator, 4), round(y_novo*fator, 4))

    # -----------------------------
    # GUI: Widgets
    # -----------------------------
    def create_control_widgets(self):
        ttk.Label(self.control_frame, text="Algoritmo:").grid(row=0, column=0, columnspan=2, sticky="w")
        self.algoritmo_var = tk.StringVar()
        self.algoritmo_combo = ttk.Combobox(
            self.control_frame,
            textvariable=self.algoritmo_var,
            values=["Translação", "Escala", "Rotação", "Reflexão", "Cisalhamento", "Sequência"]
        )
        self.algoritmo_combo.grid(row=1, column=0, columnspan=2, sticky="ew")
        self.algoritmo_combo.bind("<<ComboboxSelected>>", self.update_inputs)

        self.input_frame = ttk.Frame(self.control_frame)
        self.input_frame.grid(row=2, column=0, columnspan=2, sticky="ew")
        self.input_widgets = {}

        ttk.Button(self.control_frame, text="Adicionar Passo à Sequência", command=self.adicionar_passo)\
            .grid(row=3, column=0, columnspan=2, sticky="ew")
        ttk.Label(self.control_frame, text="Sequência Definida:").grid(row=4, column=0, columnspan=2, sticky="w")
        self.sequence_listbox = tk.Listbox(self.control_frame, height=6)
        self.sequence_listbox.grid(row=5, column=0, columnspan=2, sticky="ew")
        ttk.Button(self.control_frame, text="Limpar Sequência", command=self.limpar_sequencia)\
            .grid(row=6, column=0, columnspan=2, sticky="ew", pady=5)

        self.draw_button = ttk.Button(self.control_frame, text="Aplicar e Desenhar", command=self.executar_transformacao)
        self.draw_button.grid(row=7, column=0, columnspan=2, pady=10, sticky="ew")

        ttk.Label(self.control_frame, text="Coordenadas Geradas:").grid(row=8, column=0, columnspan=2, sticky="w")
        self.coords_text = tk.Text(self.control_frame, height=10, width=30, state=tk.DISABLED)
        self.coords_text.grid(row=9, column=0, columnspan=2, sticky="nsew")

        self.algoritmo_combo.current(0)
        self.update_inputs()

    def update_inputs(self, event=None):
        for widget in self.input_frame.winfo_children(): widget.destroy()
        self.input_widgets = {}
        algo = self.algoritmo_var.get()

        if algo == "Translação":
            self.create_entry("tx:", 0); self.create_entry("ty:", 1)
        elif algo == "Escala":
            self.create_entry("sx:", 0, "1"); self.create_entry("sy:", 1, "1")
        elif algo == "Rotação":
            self.create_entry("Ângulo (°):", 0)
        elif algo == "Reflexão":
            ttk.Label(self.input_frame, text="Eixo:").grid(row=0, column=0, sticky="w")
            self.input_widgets["eixo"] = ttk.Combobox(self.input_frame, values=["x","y","origem"])
            self.input_widgets["eixo"].grid(row=0, column=1, sticky="ew")
            self.input_widgets["eixo"].current(0)
        elif algo == "Cisalhamento":
            self.create_entry("shx:", 0); self.create_entry("shy:", 1)
        elif algo == "Sequência":
            pass

    def create_entry(self, label, row, default_value="0"):
        ttk.Label(self.input_frame, text=label).grid(row=row, column=0, sticky="w", padx=5, pady=2)
        entry = ttk.Entry(self.input_frame)
        entry.grid(row=row, column=1, sticky="ew", padx=5, pady=2)
        entry.insert(0, default_value)
        self.input_widgets[label.replace(":", "")] = entry

    def get_matriz_from_ui(self, algo):
        try:
            if algo == "Translação":
                tx = float(self.input_widgets["tx"].get()); ty = float(self.input_widgets["ty"].get())
                return translacao(tx, ty), f"Translação(tx={tx}, ty={ty})"
            elif algo == "Escala":
                sx = float(self.input_widgets["sx"].get()); sy = float(self.input_widgets["sy"].get())
                return escala(sx, sy), f"Escala(sx={sx}, sy={sy})"
            elif algo == "Rotação":
                angulo = float(self.input_widgets["Ângulo (°)"].get())
                return rotacao(angulo), f"Rotação(Ângulo={angulo}°)"
            elif algo == "Reflexão":
                eixo = self.input_widgets["eixo"].get()
                return reflexao(eixo), f"Reflexão(eixo={eixo})"
            elif algo == "Cisalhamento":
                shx = float(self.input_widgets["shx"].get()); shy = float(self.input_widgets["shy"].get())
                return cisalhamento(shx, shy), f"Cisalhamento(shx={shx}, shy={shy})"
            return None, None
        except ValueError:
            messagebox.showerror("Erro de Entrada", "Insira valores numéricos válidos.")
            return None, None
        except KeyError:
            messagebox.showerror("Seleção Inválida", "Selecione uma transformação válida.")
            return None, None

    def adicionar_passo(self):
        algo = self.algoritmo_var.get()
        if algo == "Sequência":
            messagebox.showwarning("Aviso", "Selecione uma transformação individual para adicionar.")
            return

        matriz, label = self.get_matriz_from_ui(algo)
        if matriz:
            self.sequencia_passos.append({'matriz': matriz, 'label': label})
            self.atualizar_lista_sequencia()

    def limpar_sequencia(self):
        self.sequencia_passos.clear()
        self.atualizar_lista_sequencia()

    def atualizar_lista_sequencia(self):
        self.sequence_listbox.delete(0, tk.END)
        for i, passo in enumerate(self.sequencia_passos):
            self.sequence_listbox.insert(tk.END, f"{i+1}. {passo['label']}")

    def executar_transformacao(self):
        algo = self.algoritmo_var.get()
        matriz_final = None

        if algo == "Sequência":
            if not self.sequencia_passos:
                messagebox.showwarning("Aviso", "Sequência vazia.")
                return
            matriz_final = [[1,0,0],[0,1,0],[0,0,1]]
            for passo in self.sequencia_passos:
                matriz_final = multiplicar_matrizes(passo['matriz'], matriz_final)
        else:
            matriz_final, _ = self.get_matriz_from_ui(algo)

        if matriz_final:
            fator = 50  # fator de escala para melhor visualização
            objeto_transformado = [self.aplicar_transformacao(p, matriz_final, fator) for p in self.objeto_original]
            objeto_original_scaled = [(x*fator, y*fator) for x,y in self.objeto_original]
            self.update_coords_text(self.objeto_original, objeto_transformado)
            self.plot_objetos(objeto_original_scaled, objeto_transformado, f"Transformação: {algo}")

    def create_plot_widget(self):
        fig = Figure(figsize=(7,6), dpi=100)
        self.ax = fig.add_subplot(111)
        self.canvas = FigureCanvasTkAgg(fig, master=self.plot_frame)
        self.canvas.get_tk_widget().pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        self.plot_objetos(self.objeto_original, self.objeto_original, "Estado Inicial")

    def plot_objetos(self, original, transformado, titulo):
        self.ax.clear()
        orig_fechado = original + [original[0]]
        trans_fechado = transformado + [transformado[0]]
        orig_x, orig_y = zip(*orig_fechado)
        trans_x, trans_y = zip(*trans_fechado)
        self.ax.plot(orig_x, orig_y, 'b-o', label='Original')
        self.ax.plot(trans_x, trans_y, 'r-o', label='Transformado')
        self.ax.set_title(titulo)
        self.ax.legend()
        self.ax.grid(True)
        self.ax.set_aspect('equal', adjustable='box')
        all_x = list(orig_x) + list(trans_x)
        all_y = list(orig_y) + list(trans_y)
        if all_x and all_y:
            self.ax.set_xlim(min(all_x)-1, max(all_x)+1)
            self.ax.set_ylim(min(all_y)-1, max(all_y)+1)
        self.canvas.draw()

    def update_coords_text(self, original, transformado):
        self.coords_text.config(state=tk.NORMAL)
        self.coords_text.delete(1.0, tk.END)
        texto = "Coordenadas Originais:\n" + "\n".join(map(str, original))
        texto += "\n\nCoordenadas Transformadas:\n" + "\n".join(map(str, transformado))
        self.coords_text.insert(tk.END, texto)
        self.coords_text.config(state=tk.DISABLED)


# -----------------------------
# Função principal
# -----------------------------
if __name__ == "__main__":
    root = tk.Tk()
    app = TransformacaoGUI(root)
    root.mainloop()
