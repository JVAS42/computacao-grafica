import matplotlib.pyplot as plt
import tkinter as tk
from tkinter import ttk, messagebox
import math


def dda_line(x0, y0, x1, y1):
    """Implementação do algoritmo DDA. (de Dda.java)"""
    dx = x1 - x0
    dy = y1 - y0

    step = max(abs(dx), abs(dy))

    x_incr = dx / step
    y_incr = dy / step
    x, y = float(x0), float(y0)

    pontos = []

    for _ in range(step + 1):
        pontos.append((round(x), round(y)))
        x += x_incr
        y += y_incr

    return pontos


# Exemplo de uso
x1, y1 = 6, 9
x2, y2 = 11, 12

pontos = dda_line(x1, y1, x2, y2)

# Mostrar resultado
for p in pontos:
    print(p)

# Plotar
xs, ys = zip(*pontos)
plt.scatter(xs, ys, color="blue")
plt.plot([x1, x2], [y1, y2], color="red", linestyle="--")  # reta "matemática"
plt.show()