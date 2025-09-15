# 1. IMPORTAÇÕES
import tkinter as tk
from tkinter import ttk, messagebox
import math
import matplotlib.pyplot as plt
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# 2. FUNÇÕES DOS ALGORITMOS
# --- ALGORITMOS DE RETA ---
def dda_line(x0, y0, x1, y1):
    """Implementação do algoritmo DDA."""
    dx = x1 - x0
    dy = y1 - y0
    if dx == 0 and dy == 0:
        return [(x0, y0)]
    step = max(abs(dx), abs(dy))
    x_incr = dx / step
    y_incr = dy / step
    x, y = float(x0), float(y0)
    pontos = []
    for _ in range(int(step) + 1):
        pontos.append((round(x), round(y)))
        x += x_incr
        y += y_incr
    return pontos

def bresenham(x1, y1, x2, y2):
    """Implementação do algoritmo de Bresenham para retas."""
    pontos = []
    dx = abs(x2 - x1)
    dy = abs(y2 - y1)
    sx = 1 if x2 >= x1 else -1
    sy = 1 if y2 >= y1 else -1

    if dx > dy:
        p = 2 * dy - dx
        x, y = x1, y1
        for _ in range(dx + 1):
            pontos.append((x, y))
            if p >= 0:
                y += sy
                p += 2 * (dy - dx)
            else:
                p += 2 * dy
            x += sx
    else:
        p = 2 * dx - dy
        x, y = x1, y1
        for _ in range(dy + 1):
            pontos.append((x, y))
            if p >= 0:
                x += sx
                p += 2 * (dx - dy)
            else:
                p += 2 * dx
            y += sy
    return pontos

# --- ALGORITMOS DE CIRCUNFERÊNCIA ---
def circulo_ponto_medio(xc, yc, r):
    """Implementação do algoritmo do Ponto Médio para circunferências."""
    pontos = []
    x = 0
    y = r
    d = 1 - r
    while x <= y:
        pontos.extend([
            (xc + x, yc + y), (xc - x, yc + y), (xc + x, yc - y), (xc - x, yc - y),
            (xc + y, yc + x), (xc - y, yc + x), (xc + y, yc - x), (xc - y, yc - x)
        ])
        if d < 0:
            d += 2 * x + 3
        else:
            d += 2 * (x - y) + 5
            y -= 1
        x += 1
    return pontos

def circulo_polinomial(xc, yc, r):
    """Implementação da equação polinomial para circunferências."""
    pontos = []
    x = 0
    while x <= r / math.sqrt(2): # Otimizado para apenas um octante
        y = round(math.sqrt(r*r - x*x))
        pontos.extend([
            (xc + x, yc + y), (xc - x, yc + y), (xc + x, yc - y), (xc - x, yc - y),
            (xc + y, yc + x), (xc - y, yc + x), (xc + y, yc - x), (xc - y, yc - x)
        ])
        x += 1
    return pontos

def circulo_trigonometrico(xc, yc, r, n_pontos=360):
    """Implementação do método trigonométrico para circunferências."""
    pontos = []
    for i in range(n_pontos + 1):
        theta = 2 * math.pi * i / n_pontos
        x = round(xc + r * math.cos(theta))
        y = round(yc + r * math.sin(theta))
        if (x,y) not in pontos:
            pontos.append((x, y))
    return pontos

# 3. CLASSE DA APLICAÇÃO (GUI)
class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Algoritmos de Computação Gráfica")
        self.geometry("900x700")

        # --- Frame para os controles ---
        control_frame = ttk.Frame(self, padding="10")
        control_frame.pack(side="left", fill="y", padx=10)

        # --- Labels e Entradas de Coordenadas ---
        self.labels = {}
        self.entries = {}
        coords = [("x0:", "x0"), ("y0:", "y0"), ("x1:", "x1"), ("y1:", "y1"), ("Raio:", "r")]
        
        for i, (text, key) in enumerate(coords):
            label = ttk.Label(control_frame, text=text)
            label.grid(row=i, column=0, padx=5, pady=5, sticky="w")
            self.labels[key] = label
            
            entry = ttk.Entry(control_frame, width=10)
            entry.grid(row=i, column=1, padx=5, pady=5)
            self.entries[key] = entry

        # --- Menu de Seleção de Algoritmo ---
        ttk.Label(control_frame, text="Algoritmo:").grid(row=5, column=0, padx=5, pady=10, sticky="w")
        self.algoritmo_var = tk.StringVar()
        self.algoritmo_menu = ttk.Combobox(control_frame, textvariable=self.algoritmo_var, state="readonly")
        self.algoritmo_menu['values'] = (
            "DDA",
            "Bresenham",
            "Ponto Médio Circunferência",
            "Equação Circunferência",
            "Método Trigonométrico"
        )
        self.algoritmo_menu.grid(row=5, column=1, padx=5, pady=10, sticky="ew")
        self.algoritmo_menu.current(0)
        self.algoritmo_menu.bind("<<ComboboxSelected>>", self.toggle_inputs)

        # --- Botão para Desenhar ---
        draw_button = ttk.Button(control_frame, text="Desenhar", command=self.desenhar)
        draw_button.grid(row=6, column=0, columnspan=2, pady=20)

        # --- Área de Exibição de Coordenadas ---
        ttk.Label(control_frame, text="Coordenadas Geradas:").grid(row=7, column=0, columnspan=2, pady=5, sticky="w")
        self.output_text = tk.Text(control_frame, height=20, width=30)
        self.output_text.grid(row=8, column=0, columnspan=2, padx=5, pady=5, sticky="nsew")
        
        # --- Frame para o Gráfico ---
        plot_frame = ttk.Frame(self)
        plot_frame.pack(side="right", fill="both", expand=True, padx=10, pady=10)

        fig = Figure(figsize=(6, 6), dpi=100)
        self.plot_ax = fig.add_subplot(111)
        
        self.canvas = FigureCanvasTkAgg(fig, master=plot_frame)
        self.canvas.get_tk_widget().pack(side="top", fill="both", expand=True)
        
        self.toggle_inputs()

    def toggle_inputs(self, event=None):
        """Ativa/Desativa campos de entrada com base no algoritmo selecionado."""
        algo = self.algoritmo_var.get()
        is_circulo = "Circunferência" in algo or "Trigonométrico" in algo

        self.labels["x0"].config(text="x0:" if not is_circulo else "xc:")
        self.labels["y0"].config(text="y0:" if not is_circulo else "yc:")
        
        state_reta = "normal" if not is_circulo else "disabled"
        self.labels["x1"].config(state=state_reta)
        self.entries["x1"].config(state=state_reta)
        self.labels["y1"].config(state=state_reta)
        self.entries["y1"].config(state=state_reta)
        
        state_circulo = "normal" if is_circulo else "disabled"
        self.labels["r"].config(state=state_circulo)
        self.entries["r"].config(state=state_circulo)
        
    def desenhar(self):
        """Função principal que lê os dados, chama o algoritmo e exibe os resultados."""
        algo = self.algoritmo_var.get()
        try:
            params = {key: int(entry.get()) for key, entry in self.entries.items() if entry.cget('state') != 'disabled' and entry.get()}
            pontos = []
            
            if algo == "DDA":
                pontos = dda_line(params["x0"], params["y0"], params["x1"], params["y1"])
            elif algo == "Bresenham":
                pontos = bresenham(params["x0"], params["y0"], params["x1"], params["y1"])
            elif algo == "Ponto Médio Circunferência":
                pontos = circulo_ponto_medio(params["x0"], params["y0"], params["r"])
            elif algo == "Equação Circunferência":
                pontos = circulo_polinomial(params["x0"], params["y0"], params["r"])
            elif algo == "Método Trigonométrico":
                pontos = circulo_trigonometrico(params["x0"], params["y0"], params["r"])

            self.mostrar_resultados(pontos)

        except ValueError:
            messagebox.showerror("Erro de Entrada", "Por favor, preencha todos os campos necessários com números inteiros.")
        except KeyError:
             messagebox.showerror("Erro de Entrada", "Por favor, preencha todos os campos do algoritmo selecionado.")
        except Exception as e:
            messagebox.showerror("Erro", f"Ocorreu um erro inesperado: {e}")
            
    def mostrar_resultados(self, pontos):
        """Limpa a tela e exibe as novas coordenadas e o gráfico."""
        self.output_text.delete(1.0, tk.END)
        if not pontos:
            self.output_text.insert(tk.END, "Nenhum ponto gerado.")
            self.plot_ax.clear()
            self.canvas.draw()
            return
            
        pontos_str = "\n".join(map(str, pontos[:100]))
        self.output_text.insert(tk.END, pontos_str)
        if len(pontos) > 100:
            self.output_text.insert(tk.END, f"\n... (e mais {len(pontos)-100} pontos)")

        self.plot_ax.clear()
        xs, ys = zip(*pontos)
        # <<<----------- MODIFICAÇÃO AQUI -----------<<<
        self.plot_ax.scatter(xs, ys, s=10, color="red")
        # <<<----------------------------------------<<<
        self.plot_ax.set_title(self.algoritmo_var.get())
        self.plot_ax.grid(True)
        self.plot_ax.set_aspect('equal', adjustable='box')
        self.canvas.draw()

# 4. FUNÇÃO PRINCIPAL
if __name__ == "__main__":
    app = App()
    app.mainloop()