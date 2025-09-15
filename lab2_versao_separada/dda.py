def dda_line(x0, y0, x1, y1):
    dx = x1 - x0
    dy = y1 - y0

    if dx == 0 and dy == 0:
        return [(x0, y0)]
    
    step = max(abs(dx), abs(dy))
    x_incr = dx / step
    y_incr = dy / step
    x, y = float(x0), float(y0)

    pontos = []
    for _ in range(int(step) + 1):
        pontos.append((round(x), round(y)))
        x += x_incr
        y += y_incr

    return pontos