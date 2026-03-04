# algoritmos/recorte.py

INSIDE = 0  # 0000
LEFT = 1  # 0001
RIGHT = 2  # 0010
BOTTOM = 4  # 0100
TOP = 8  # 1000


def obter_codigo(x, y, xmin, xmax, ymin, ymax):
    code = INSIDE
    if x < xmin:
        code |= LEFT
    elif x > xmax:
        code |= RIGHT

    if y < ymin:
        code |= BOTTOM
    elif y > ymax:
        code |= TOP

    return code


def recortar_cohen_sutherland(x1, y1, x2, y2, xmin, xmax, ymin, ymax):
    code1 = obter_codigo(x1, y1, xmin, xmax, ymin, ymax)
    code2 = obter_codigo(x2, y2, xmin, xmax, ymin, ymax)
    accept = False
    steps = []

    steps.append({
        'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2,
        'code1': code1, 'code2': code2, 'action': 'Inicial'
    })

    while True:
        # Trivialmente aceito (totalmente dentro)
        if (code1 | code2) == 0:
            accept = True
            steps.append({
                'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2,
                'code1': code1, 'code2': code2, 'action': 'Aceita (dentro)'
            })
            break
        # Trivialmente rejeitado (totalmente fora da mesma região)
        elif (code1 & code2) != 0:
            steps.append({
                'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2,
                'code1': code1, 'code2': code2, 'action': 'Rejeitada (fora)'
            })
            break
        else:
            # Precisa recortar
            x = 0.0
            y = 0.0
            # Pega um ponto que está fora da janela
            code_out = code1 if code1 != 0 else code2
            action = ''

            # Encontra a interseção usando a fórmula y = y1 + slope * (x - x1)
            if code_out & TOP:
                x = x1 + (x2 - x1) * (ymax - y1) / (y2 - y1)
                y = ymax
                action = 'Recorte TOP'
            elif code_out & BOTTOM:
                x = x1 + (x2 - x1) * (ymin - y1) / (y2 - y1)
                y = ymin
                action = 'Recorte BOTTOM'
            elif code_out & RIGHT:
                y = y1 + (y2 - y1) * (xmax - x1) / (x2 - x1)
                x = xmax
                action = 'Recorte RIGHT'
            elif code_out & LEFT:
                y = y1 + (y2 - y1) * (xmin - x1) / (x2 - x1)
                x = xmin
                action = 'Recorte LEFT'

            # Atualiza o ponto recortado e seu novo código
            if code_out == code1:
                steps.append({'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2, 'code1': code1, 'code2': code2, 'action': action})
                x1 = x
                y1 = y
                code1 = obter_codigo(x1, y1, xmin, xmax, ymin, ymax)
            else:
                steps.append({'x1': x1, 'y1': y1, 'x2': x2, 'y2': y2, 'code1': code1, 'code2': code2, 'action': action})
                x2 = x
                y2 = y
                code2 = obter_codigo(x2, y2, xmin, xmax, ymin, ymax)

    return {'accept': accept, 'x1': round(x1), 'y1': round(y1), 'x2': round(x2), 'y2': round(y2), 'steps': steps}
