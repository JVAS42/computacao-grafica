import matplotlib.pyplot as plt

# "framebuffer" de pontos
pontos = []


# writepixel recebe x, y, valor (como no C)
def writepixel(x, y, valor):
    pontos.append((x, y, valor))  # guarda tripla (x,y,valor)


# ponto_circulo gera os 8 pontos simétricos
def ponto_circulo(x, y, valor):
    writepixel(x, y, valor)
    writepixel(y, x, valor)
    writepixel(y, -x, valor)
    writepixel(x, -y, valor)
    writepixel(-x, -y, valor)
    writepixel(-y, -x, valor)
    writepixel(-y, x, valor)
    writepixel(-x, y, valor)


# círculo por ponto médio
def cpontomedio(raio, valor=1):
    x = 0
    y = raio
    d = 1 - raio

    ponto_circulo(x, y, valor)

    while y > x:
        if d < 0:  # escolhe pixel E
            d += 2 * x + 3
        else:  # escolhe pixel SE
            d += 2 * (x - y) + 5
            y -= 1
        x += 1
        ponto_circulo(x, y, valor)

    print(pontos)
    return pontos


# Exemplo
pontos = []
cpontomedio(10, valor=1)

# Plot (desenha só x,y, ignorando valor)
xs, ys, vs = zip(*pontos)
plt.scatter(xs, ys, color="green", s=10)
plt.gca().set_aspect("equal", adjustable="box")
plt.show()