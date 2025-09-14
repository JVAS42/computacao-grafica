import matplotlib.pyplot as plt

def circulo_ponto_medio(xc, yc, r):
    pontos = []

    x = 0
    y = r
    d = 1 - r  # decisão inicial

    while x <= y:
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

        # Atualiza decisão
        if d < 0:  # escolhe pixel acima
            d += 2 * x + 3
        else:      # escolhe pixel diagonal
            d += 2 * (x - y) + 5
            y -= 1

        x += 1

    return pontos


# Exemplo de uso
xc, yc, r = 50, 50, 10
pontos = circulo_ponto_medio(xc, yc, r)

# Plotar
xs, ys = zip(*pontos)
plt.scatter(xs, ys, color="green", s=10)
plt.gca().set_aspect("equal", adjustable="box")
plt.show()
