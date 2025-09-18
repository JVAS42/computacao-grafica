import tkinter as tk
from tkinter import ttk, messagebox
import math

# ALTERAÇÃO: Importa as funções dos arquivos .py fornecidos
from dda import dda_line
from bresenham import bresenham
from circunferencia_ponto_medio import circunferencia_ponto_medio
from circunferencia_polinomial import circunferencia_polinomial
from circunferencia_trigonometrico import circunferencia_trigonometrico


# REMOVIDO: As definições locais das funções de algoritmos foram removidas
# pois agora são importadas dos seus respectivos arquivos.


# --- Classe Principal da Aplicação ---

class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Editor Gráfico 2D (HD 1280x720)")
        self.geometry("1550x760")

        # --- Constantes do Canvas ---
        self.CANVAS_WIDTH = 1280
        self.CANVAS_HEIGHT = 720
        self.AXIS_COLOR = "#808080"

        # --- Frame de Controles (Esquerda) ---
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
            "DDA", "Bresenham", "Ponto Médio Circunferência",
            "Método Polinomial", "Método Trigonométrico"
        )
        self.algoritmo_menu.grid(row=5, column=1, padx=5, pady=10, sticky="ew")
        self.algoritmo_menu.current(0)
        self.algoritmo_menu.bind("<<ComboboxSelected>>", self.toggle_inputs)

        draw_button = ttk.Button(control_frame, text="Desenhar", command=self.desenhar)
        draw_button.grid(row=6, column=0, columnspan=2, pady=20)

        ttk.Label(control_frame, text="Coordenadas Geradas:").grid(row=7, column=0, columnspan=2, pady=5, sticky="w")
        self.output_text = tk.Text(control_frame, height=25, width=30)
        self.output_text.grid(row=8, column=0, columnspan=2, padx=5, pady=5, sticky="nsew")

        # --- Canvas para o Plano Cartesiano (Direita) ---
        canvas_frame = ttk.Frame(self)
        canvas_frame.pack(side="right", fill="both", expand=True, padx=10, pady=10)

        self.canvas = tk.Canvas(canvas_frame, width=self.CANVAS_WIDTH, height=self.CANVAS_HEIGHT, bg='white')
        self.canvas.pack()

        self.origin_x = self.CANVAS_WIDTH / 2
        self.origin_y = self.CANVAS_HEIGHT / 2

        self.toggle_inputs()

    def draw_axes(self):
        """Desenha apenas os eixos no canvas."""
        self.canvas.create_line(0, self.origin_y, self.CANVAS_WIDTH, self.origin_y, fill=self.AXIS_COLOR, width=1)
        self.canvas.create_line(self.origin_x, 0, self.origin_x, self.CANVAS_HEIGHT, fill=self.AXIS_COLOR, width=1)

    def clear_canvas(self):
        """Limpa o canvas e redesenha os eixos."""
        self.canvas.delete("all")
        self.draw_axes()

    def toggle_inputs(self, event=None):
        for entry in self.entries.values():
            entry.delete(0, tk.END)
        self.output_text.delete(1.0, tk.END)

        self.clear_canvas()

        algo = self.algoritmo_var.get()
        is_circulo = "Circunferência" in algo or "Trigonométrico" in algo or "Polinomial" in algo

        self.labels["x0"].config(text="xc:" if is_circulo else "x0:")
        self.labels["y0"].config(text="yc:" if is_circulo else "y0:")

        state_reta = "normal" if not is_circulo else "disabled"
        for key in ["x1", "y1"]:
            self.labels[key].config(state=state_reta)
            self.entries[key].config(state=state_reta)

        state_circulo = "normal" if is_circulo else "disabled"
        self.labels["r"].config(state=state_circulo)
        self.entries["r"].config(state=state_circulo)

    def desenhar(self):
        # Nenhuma mudança é necessária aqui. As chamadas de função
        # agora usam as versões importadas dos algoritmos.
        algo = self.algoritmo_var.get()
        try:
            params = {key: int(entry.get()) for key, entry in self.entries.items() if
                      entry.cget('state') != 'disabled' and entry.get()}
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
            messagebox.showerror("Erro de Entrada", "Por favor, preencha todos os campos com números inteiros.")
        except KeyError:
            messagebox.showerror("Erro de Entrada", "Por favor, preencha todos os campos do algoritmo selecionado.")
        except Exception as e:
            messagebox.showerror("Erro", f"Ocorreu um erro inesperado: {e}")

    def mostrar_resultados(self, pontos):
        self.clear_canvas()
        self.output_text.delete(1.0, tk.END)

        if not pontos:
            self.output_text.insert(tk.END, "Nenhum ponto gerado.")
            return

        algo = self.algoritmo_var.get()

        if algo in ["DDA", "Bresenham"]:
            x0 = int(self.entries["x0"].get())
            y0 = int(self.entries["y0"].get())
            x1 = int(self.entries["x1"].get())
            y1 = int(self.entries["y1"].get())

            canvas_x0 = self.origin_x + x0
            canvas_y0 = self.origin_y - y0
            canvas_x1 = self.origin_x + x1
            canvas_y1 = self.origin_y - y1

            self.canvas.create_line(canvas_x0, canvas_y0, canvas_x1, canvas_y1, fill="red", width=1)

        elif "Circunferência" in algo or "Polinomial" in algo or "Trigonométrico" in algo:
            xc = int(self.entries["x0"].get())
            yc = int(self.entries["y0"].get())
            r = int(self.entries["r"].get())

            canvas_x0 = self.origin_x + (xc - r)
            canvas_y0 = self.origin_y - (yc + r)
            canvas_x1 = self.origin_x + (xc + r)
            canvas_y1 = self.origin_y - (yc - r)

            self.canvas.create_oval(canvas_x0, canvas_y0, canvas_x1, canvas_y1, outline="red", width=1)

        pontos_str = "\n".join(map(str, pontos[:100]))
        self.output_text.insert(tk.END, pontos_str)
        if len(pontos) > 100:
            self.output_text.insert(tk.END, f"\n... (e mais {len(pontos) - 100} pontos)")


if __name__ == "__main__":
    app = App()
    app.mainloop()