import math

def circunferencia_polinomial(xc, yc, r):
    pontos = []
    x = 0

    while x <= r:
        y = round(math.sqrt(r*r - x*x))
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
