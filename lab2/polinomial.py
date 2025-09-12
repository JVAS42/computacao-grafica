import matplotlib.pyplot as plt
import math

def circulo_polinomial(xc, yc, r):
    pontos = []
    x = 0
    while x <= r:
        y = round(math.sqrt(r*r - x*x))

        # 8 simetrias
        pontos.extend([
            (xc + x, yc + y),
            (xc - x, yc + y),
            (xc + x, yc - y),
            (xc - x, yc - y),
            (xc + y, yc + x),
            (xc - y, yc + x),
            (xc + y, yc - x),
            (xc - y, yc - x)
        ])

        x += 1

    return pontos

# Exemplo de uso
xc, yc, r = 50, 50, 30
pontos = circulo_polinomial(xc, yc, r)

# Plotar
xs, ys = zip(*pontos)
plt.scatter(xs, ys, color="blue", s=10)
plt.gca().set_aspect("equal", adjustable="box")
plt.show()