import matplotlib.pyplot as plt
import math

def circulo_trigonometrico(xc, yc, r, n_pontos=360):
    pontos = []
    for i in range(n_pontos + 1):
        theta = 2 * math.pi * i / n_pontos  # ângulo em radianos
        x = xc + r * math.cos(theta)
        y = yc + r * math.sin(theta)
        pontos.append((x, y))
    return pontos

# Exemplo de uso
xc, yc, r = 50, 50, 30
pontos = circulo_trigonometrico(xc, yc, r)

# Plotar
xs, ys = zip(*pontos)
plt.plot(xs, ys, color="red")  # linha contínua
plt.scatter(xs, ys, color="blue", s=1)  # pontos discretos
plt.gca().set_aspect("equal", adjustable="box")
plt.show()