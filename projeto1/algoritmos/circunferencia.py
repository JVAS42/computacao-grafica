# algoritmos/circunferencia.py
import math


def equacao_explicita(xc, yc, r):
    pontos = []
    # Itera de -r até r
    for x in range(-r, r + 1):
        # y = sqrt(r^2 - x^2)
        y = round(math.sqrt(r * r - x * x))

        # Adiciona o ponto positivo e negativo (simetria no eixo X)
        if (xc + x, yc + y) not in pontos:
            pontos.append((xc + x, yc + y))
        if (xc + x, yc - y) not in pontos:
            pontos.append((xc + x, yc - y))

    return pontos


def trigonometrico(xc, yc, r):
    pontos = []
    passo = 0.1  # Mesmo passo utilizado no seu código JS
    theta = 0

    while theta < 2 * math.pi:
        x = round(xc + r * math.cos(theta))
        y = round(yc + r * math.sin(theta))

        if (x, y) not in pontos:
            pontos.append((x, y))

        theta += passo

    return pontos


def ponto_medio(xc, yc, r):
    pontos = []
    x = 0
    y = r
    p = 1 - r

    def plotar_simetria(xc, yc, x, y):
        # 8 octantes
        simetricos = [
            (xc + x, yc + y), (xc - x, yc + y),
            (xc + x, yc - y), (xc - x, yc - y),
            (xc + y, yc + x), (xc - y, yc + x),
            (xc + y, yc - x), (xc - y, yc - x)
        ]
        for pt in simetricos:
            if pt not in pontos:
                pontos.append(pt)

    while x <= y:
        plotar_simetria(xc, yc, x, y)
        if p < 0:
            p = p + 2 * x + 3
        else:
            p = p + 2 * (x - y) + 5
            y -= 1
        x += 1

    return pontos
