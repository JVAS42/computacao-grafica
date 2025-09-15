import tkinter as tk
from tkinter import ttk, messagebox
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# Importando algoritmos
from dda import dda_line
from bresenham import bresenham
from circunferencia_ponto_medio import circunferencia_ponto_medio
from circunferencia_polinomial import circunferencia_polinomial
from circunferencia_trigonometrico import circunferencia_trigonometrico


class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Algoritmos de Computação Gráfica")
        self.geometry("900x700")

        # --- Frame de controles
        control_frame = ttk.Frame(self, padding="10")
        control_frame.pack(side="left", fill="y", padx=10)

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

        draw_button = ttk.Button(control_frame, text="Desenhar", command=self.desenhar)
        draw_button.grid(row=6, column=0, columnspan=2, pady=20)

        ttk.Label(control_frame, text="Coordenadas Geradas:").grid(row=7, column=0, columnspan=2, pady=5, sticky="w")
        self.output_text = tk.Text(control_frame, height=20, width=30)
        self.output_text.grid(row=8, column=0, columnspan=2, padx=5, pady=5, sticky="nsew")
        
        plot_frame = ttk.Frame(self)
        plot_frame.pack(side="right", fill="both", expand=True, padx=10, pady=10)

        fig = Figure(figsize=(6, 6), dpi=100)
        self.plot_ax = fig.add_subplot(111)
        
        self.canvas = FigureCanvasTkAgg(fig, master=plot_frame)
        self.canvas.get_tk_widget().pack(side="top", fill="both", expand=True)
        
        self.toggle_inputs()

    def toggle_inputs(self, event=None):
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
        algo = self.algoritmo_var.get()
        try:
            params = {key: int(entry.get()) for key, entry in self.entries.items() if entry.cget('state') != 'disabled' and entry.get()}
            pontos = []
            
            if algo == "DDA":
                pontos = dda_line(params["x0"], params["y0"], params["x1"], params["y1"])
            elif algo == "Bresenham":
                pontos = bresenham(params["x0"], params["y0"], params["x1"], params["y1"])
            elif algo == "Ponto Médio Circunferência":
                pontos = circunferencia_ponto_medio(params["x0"], params["y0"], params["r"])
            elif algo == "Método Polinomial":
                pontos = circunferencia_polinomial(params["x0"], params["y0"], params["r"])
            elif algo == "Método Trigonométrico":
                pontos = circunferencia_trigonometrico(params["x0"], params["y0"], params["r"])

            self.mostrar_resultados(pontos)

        except ValueError:
            messagebox.showerror("Erro de Entrada", "Por favor, preencha todos os campos necessários com números inteiros.")
        except KeyError:
             messagebox.showerror("Erro de Entrada", "Por favor, preencha todos os campos do algoritmo selecionado.")
        except Exception as e:
            messagebox.showerror("Erro", f"Ocorreu um erro inesperado: {e}")
            
    def mostrar_resultados(self, pontos):
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
        self.plot_ax.scatter(xs, ys, s=10, color="red")
        self.plot_ax.set_title(self.algoritmo_var.get())
        self.plot_ax.grid(True)
        self.plot_ax.set_aspect('equal', adjustable='box')
        self.canvas.draw()


if __name__ == "__main__":
    app = App()
    app.mainloop()
