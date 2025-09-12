import matplotlib.pyplot as plt

def bresenham(x1, y1, x2, y2):
    pontos = []

    dx = abs(x2 - x1)
    dy = abs(y2 - y1)

    # Direção do incremento
    sx = 1 if x2 >= x1 else -1
    sy = 1 if y2 >= y1 else -1

    # Caso inclinação <= 1
    if dx > dy:
        p = 2 * dy - dx
        x, y = x1, y1
        for _ in range(dx + 1):
            pontos.append((x, y))
            x += sx
            if p >= 0:
                y += sy
                p += 2 * (dy - dx)
            else:
                p += 2 * dy
    # Caso inclinação > 1
    else:
        p = 2 * dx - dy
        x, y = x1, y1
        for _ in range(dy + 1):
            pontos.append((x, y))
            y += sy
            if p >= 0:
                x += sx
                p += 2 * (dx - dy)
            else:
                p += 2 * dx

    return pontos


# Exemplo de uso
x1, y1 = 20, 10
x2, y2 = 30, 18

pontos = bresenham(x1, y1, x2, y2)

# Mostrar resultado
for p in pontos:
    print(p)

# Plotar
xs, ys = zip(*pontos)
plt.scatter(xs, ys, color="blue")
plt.plot([x1, x2], [y1, y2], color="red", linestyle="--")  # reta real
plt.show()