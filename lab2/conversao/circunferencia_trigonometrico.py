import math

def circunferencia_trigonometrico(xc, yc, r, n_pontos=360):
    pontos = []

    for i in range(n_pontos + 1):
        theta = 2 * math.pi * i / n_pontos
        x = round(xc + r * math.cos(theta))
        y = round(yc + r * math.sin(theta))
        pontos.append((x, y))
        
    return pontos
