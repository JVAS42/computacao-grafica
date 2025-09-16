def bresenham(x1, y1, x2, y2):
    pontos = []

    dx = abs(x2 - x1)
    dy = abs(y2 - y1)
    sx = 1 if x2 >= x1 else -1
    sy = 1 if y2 >= y1 else -1

    if dx > dy:
        p = 2 * dy - dx
        x, y = x1, y1
        for _ in range(dx + 1):
            pontos.append((x, y))
            if p >= 0:
                y += sy
                p += 2 * (dy - dx)
            else:
                p += 2 * dy
            x += sx
    else:
        p = 2 * dx - dy
        x, y = x1, y1
        for _ in range(dy + 1):
            pontos.append((x, y))
            if p >= 0:
                x += sx
                p += 2 * (dx - dy)
            else:
                p += 2 * dx
            y += sy

    return pontos
