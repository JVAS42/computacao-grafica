import tkinter as tk
from tkinter import ttk, messagebox
import math

CANVAS_WIDTH = 700
CANVAS_HEIGHT = 500


class AplicativoDesenho:
    """
    Classe principal que cria e gerencia a interface gráfica.
    Agora inclui funcionalidades de zoom e eixos de coordenadas.
    """

    def __init__(self, root):
        self.root = root
        self.root.title("Desenho de Primitivas (Python com Zoom)")
        self.root.geometry("1024x820")  # Aumentei a altura para os novos botões

        # Variáveis de estado para zoom e redesenho
        self.escala = 1.0
        self.ultima_acao = None  # Armazena a última operação de desenho

        # --- Painel de Entrada (Norte) ---
        self.painel_entrada = tk.Frame(root)
        self.painel_entrada.grid(row=0, column=0, columnspan=3, padx=10, pady=10, sticky='ew')

        self.campos = {}
        labels = {"x0": "x0:", "y0": "y0:", "x1": "x1:", "y1": "y1:", "r": "R:", "a": "a:", "b": "b:"}
        row = 0
        for key, text in labels.items():
            label = tk.Label(self.painel_entrada, text=text)
            label.grid(row=row, column=0, sticky='w', padx=5, pady=2)
            entry = tk.Entry(self.painel_entrada)
            entry.grid(row=row, column=1, sticky='ew', padx=5, pady=2)
            self.campos[key] = {'label': label, 'entry': entry}
            row += 1

        tk.Label(self.painel_entrada, text="Algoritmo:").grid(row=row, column=0, sticky='w', padx=5, pady=2)
        self.algoritmos = [
            "DDA", "Bresenham", "Ponto Médio Circunferência",
            "Equação Circunferência", "Método Trigonométrico Circunferência",
            "Ponto Médio Elipse"
        ]
        self.combo_algoritmo = ttk.Combobox(self.painel_entrada, values=self.algoritmos, state="readonly")
        self.combo_algoritmo.grid(row=row, column=1, sticky='ew', padx=5, pady=2)
        self.combo_algoritmo.bind("<<ComboboxSelected>>", self.atualizar_campos_visiveis)

        # --- Painel Central (Canvas) ---
        self.painel_central = tk.Frame(root)
        self.painel_central.grid(row=1, column=0, columnspan=3, pady=10)

        self.canvas = tk.Canvas(self.painel_central, width=CANVAS_WIDTH, height=CANVAS_HEIGHT, bg="white")
        self.canvas.pack()

        # --- Painel de Botões (Sul) ---
        self.painel_botoes = tk.Frame(root)
        self.painel_botoes.grid(row=2, column=0, columnspan=3, pady=10)

        self.btn_desenhar = tk.Button(self.painel_botoes, text="Desenhar", command=self.desenhar)
        self.btn_desenhar.grid(row=0, column=0, padx=10)

        self.btn_limpar = tk.Button(self.painel_botoes, text="Limpar", command=self.limpar_canvas)
        self.btn_limpar.grid(row=0, column=1, padx=10)

        self.btn_zoom_in = tk.Button(self.painel_botoes, text="Zoom In (+)", command=self.zoom_in)
        self.btn_zoom_in.grid(row=0, column=2, padx=10)

        self.btn_zoom_out = tk.Button(self.painel_botoes, text="Zoom Out (-)", command=self.zoom_out)
        self.btn_zoom_out.grid(row=0, column=3, padx=10)

        self.combo_algoritmo.current(0)
        self.atualizar_campos_visiveis(None)
        self.limpar_canvas()  # Limpa e desenha os eixos iniciais

        self.root.grid_columnconfigure(0, weight=1)

    # --- Métodos de Desenho e Utilitários ---

    def _desenhar_eixos(self):
        """Desenha os eixos X e Y no centro do canvas."""
        centro_x = CANVAS_WIDTH / 2
        centro_y = CANVAS_HEIGHT / 2
        # Eixo X (horizontal)
        self.canvas.create_line(0, centro_y, CANVAS_WIDTH, centro_y, fill="#ccc", dash=(1, 3))
        # Eixo Y (vertical)
        self.canvas.create_line(centro_x, 0, centro_x, CANVAS_HEIGHT, fill="#ccc", dash=(1, 3))

    def _desenhar_pixel(self, x, y):
        """Desenha um pixel no canvas, aplicando a escala de zoom."""
        centro_x = CANVAS_WIDTH / 2
        centro_y = CANVAS_HEIGHT / 2

        # Aplica a escala
        x_scaled = x * self.escala
        y_scaled = y * self.escala

        # Ajusta para a origem central
        x_ajustado = centro_x + x_scaled
        y_ajustado = centro_y - y_scaled

        # O tamanho do pixel pode aumentar um pouco com o zoom para melhor visibilidade
        pixel_size = 1 if self.escala < 2 else 2
        self.canvas.create_rectangle(
            x_ajustado - pixel_size / 2, y_ajustado - pixel_size / 2,
            x_ajustado + pixel_size / 2, y_ajustado + pixel_size / 2,
            fill="red", outline="red"
        )

    def _redesenhar(self):
        """Limpa o canvas e redesenha a última forma com a escala atual."""
        self.limpar_canvas()
        if self.ultima_acao:
            algoritmo, params = self.ultima_acao
            # Usa **params para desempacotar o dicionário de argumentos na chamada da função
            getattr(self, algoritmo)(**params)

    # --- Métodos de Controle ---

    def zoom_in(self):
        """Aumenta o zoom e redesenha."""
        self.escala *= 1.2
        self._redesenhar()

    def zoom_out(self):
        """Diminui o zoom e redesenha."""
        self.escala /= 1.2
        self._redesenhar()

    def limpar_canvas(self):
        """Limpa o canvas e redesenha os eixos."""
        self.canvas.delete("all")
        self._desenhar_eixos()

    def atualizar_campos_visiveis(self, event):
        """Mostra/esconde os campos de entrada com base no algoritmo selecionado."""
        # ... (código original sem alterações)
        selecionado = self.combo_algoritmo.get()
        campos_linha = ['x0', 'y0', 'x1', 'y1']
        campos_circulo = ['r']
        campos_elipse = ['a', 'b']
        visibilidade = {'linha': False, 'circulo': False, 'elipse': False}
        if selecionado in ["DDA", "Bresenham"]:
            visibilidade['linha'] = True
        elif "Circunferência" in selecionado:
            visibilidade['circulo'] = True
        elif "Elipse" in selecionado:
            visibilidade['elipse'] = True
        for campo in campos_linha:
            if visibilidade['linha']:
                self.campos[campo]['label'].grid(); self.campos[campo]['entry'].grid()
            else:
                self.campos[campo]['label'].grid_remove(); self.campos[campo]['entry'].grid_remove()
        for campo in campos_circulo:
            if visibilidade['circulo']:
                self.campos[campo]['label'].grid(); self.campos[campo]['entry'].grid()
            else:
                self.campos[campo]['label'].grid_remove(); self.campos[campo]['entry'].grid_remove()
        for campo in campos_elipse:
            if visibilidade['elipse']:
                self.campos[campo]['label'].grid(); self.campos[campo]['entry'].grid()
            else:
                self.campos[campo]['label'].grid_remove(); self.campos[campo]['entry'].grid_remove()

    def desenhar(self):
        """Lê os valores, armazena a ação e executa o desenho."""
        selecionado = self.combo_algoritmo.get()
        self.escala = 1.0  # Reseta o zoom ao desenhar uma nova forma
        try:
            if selecionado == "DDA":
                params = {'x0': int(self.campos['x0']['entry'].get()), 'y0': int(self.campos['y0']['entry'].get()),
                          'x1': int(self.campos['x1']['entry'].get()), 'y1': int(self.campos['y1']['entry'].get())}
                self.ultima_acao = ('dda_line', params)
            elif selecionado == "Bresenham":
                params = {'x0': int(self.campos['x0']['entry'].get()), 'y0': int(self.campos['y0']['entry'].get()),
                          'x1': int(self.campos['x1']['entry'].get()), 'y1': int(self.campos['y1']['entry'].get())}
                self.ultima_acao = ('bresenham_line', params)
            elif "Circunferência" in selecionado:
                r = int(self.campos['r']['entry'].get())
                if r <= 0: raise ValueError("O raio deve ser positivo.")
                params = {'r': r}
                if selecionado == "Ponto Médio Circunferência":
                    self.ultima_acao = ('mid_point_circle', params)
                elif selecionado == "Equação Circunferência":
                    self.ultima_acao = ('equation_circle', params)
                elif selecionado == "Método Trigonométrico Circunferência":
                    self.ultima_acao = ('trigonometric_circle', params)
            elif "Elipse" in selecionado:
                a = int(self.campos['a']['entry'].get())
                b = int(self.campos['b']['entry'].get())
                if a <= 0 or b <= 0: raise ValueError("Os raios devem ser positivos.")
                params = {'a': a, 'b': b}
                self.ultima_acao = ('midpoint_ellipse', params)

            self._redesenhar()  # Chama o método que limpa e desenha

        except ValueError as e:
            messagebox.showerror("Erro de Entrada", f"Valor inválido: {e}")
        except Exception as e:
            messagebox.showerror("Erro Inesperado", f"Ocorreu um erro: {e}")

    # --- Algoritmos de Desenho (agora como métodos da classe) ---

    def dda_line(self, x0, y0, x1, y1):
        dx = x1 - x0;
        dy = y1 - y0
        step = abs(dx) if abs(dx) > abs(dy) else abs(dy)
        if step == 0: self._desenhar_pixel(x0, y0); return
        x_incr = dx / step;
        y_incr = dy / step
        x, y = float(x0), float(y0)
        for _ in range(step + 1):
            self._desenhar_pixel(round(x), round(y))
            x += x_incr;
            y += y_incr

    def bresenham_line(self, x0, y0, x1, y1):
        if x0 > x1: x0, x1, y0, y1 = x1, x0, y1, y0
        if x1 - x0 == 0:
            for y in range(min(y0, y1), max(y0, y1) + 1): self._desenhar_pixel(x0, y)
            return
        a = (y1 - y0) / (x1 - x0)
        for x in range(x0, x1 + 1):
            y = round(y0 + a * (x - x0))
            self._desenhar_pixel(x, y)

    def _circle_points(self, x, y):
        self._desenhar_pixel(x, y);
        self._desenhar_pixel(y, x);
        self._desenhar_pixel(y, -x)
        self._desenhar_pixel(x, -y);
        self._desenhar_pixel(-x, -y);
        self._desenhar_pixel(-y, -x)
        self._desenhar_pixel(-y, x);
        self._desenhar_pixel(-x, y)

    def mid_point_circle(self, r):
        x, y, d = 0, r, 1 - r
        self._circle_points(x, y)
        while y > x:
            if d < 0:
                d += 2 * x + 3; x += 1
            else:
                d += 2 * (x - y) + 5; x += 1; y -= 1
            self._circle_points(x, y)

    def trigonometric_circle(self, r):
        for theta in range(360):
            radianos = math.radians(theta)
            x = round(r * math.cos(radianos))
            y = round(r * math.sin(radianos))
            self._desenhar_pixel(x, y)

    def equation_circle(self, r):
        x, x_end = 0, round(r / math.sqrt(2))
        while x <= x_end:
            y = round(math.sqrt(r * r - x * x))
            self._circle_points(x, y)
            x += 1

    def _ellipse_points(self, x, y):
        self._desenhar_pixel(x, y);
        self._desenhar_pixel(-x, y)
        self._desenhar_pixel(x, -y);
        self._desenhar_pixel(-x, -y)

    def midpoint_ellipse(self, a, b):
        x, y = 0, b
        d1 = (b * b) - (a * a * b) + (0.25 * a * a)
        self._ellipse_points(x, y)
        while (a * a * (y - 0.5)) > (b * b * (x + 1)):
            if d1 < 0:
                d1 += b * b * (2 * x + 3); x += 1
            else:
                d1 += b * b * (2 * x + 3) + a * a * (-2 * y + 2); x += 1; y -= 1
            self._ellipse_points(x, y)
        d2 = (b * b * (x + 0.5) ** 2) + (a * a * (y - 1) ** 2) - (a * a * b * b)
        while y > 0:
            if d2 < 0:
                d2 += b * b * (2 * x + 2) + a * a * (-2 * y + 3); x += 1; y -= 1
            else:
                d2 += a * a * (-2 * y + 3); y -= 1
            self._ellipse_points(x, y)


if __name__ == "__main__":
    root = tk.Tk()
    app = AplicativoDesenho(root)
    root.mainloop()