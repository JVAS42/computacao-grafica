# algoritmos/retas.py

def dda(x1, y1, x2, y2):
    pontos = []
    dx = x2 - x1
    dy = y2 - y1

    passos = max(abs(dx), abs(dy))

    if passos == 0:
        return [(x1, y1)]

    x_inc = dx / passos
    y_inc = dy / passos

    x = x1
    y = y1

    for _ in range(int(passos) + 1):
        pontos.append((round(x), round(y)))
        x += x_inc
        y += y_inc

    return pontos


def ponto_medio(x1, y1, x2, y2):
    pontos = []
    dx = x2 - x1
    dy = y2 - y1

    # Determina o sentido de incremento
    sx = 1 if dx >= 0 else -1
    sy = 1 if dy >= 0 else -1

    dx = abs(dx)
    dy = abs(dy)

    x = x1
    y = y1
    pontos.append((x, y))

    if dx > dy:
        d = 2 * dy - dx
        incE = 2 * dy
        incNE = 2 * (dy - dx)

        for _ in range(dx):
            if d <= 0:
                d += incE
                x += sx
            else:
                d += incNE
                x += sx
                y += sy
            pontos.append((x, y))
    else:
        d = 2 * dx - dy
        incE = 2 * dx
        incNE = 2 * (dx - dy)

        for _ in range(dy):
            if d <= 0:
                d += incE
                y += sy
            else:
                d += incNE
                y += sy
                x += sx
            pontos.append((x, y))

    return pontos
