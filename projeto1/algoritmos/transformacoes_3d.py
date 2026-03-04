# algoritmos/transformacoes_3d.py
import math

def multiplicar_matrizes_4x4(a, b):
    result = [[0]*4 for _ in range(4)]
    for i in range(4):
        for j in range(4):
            for k in range(4):
                result[i][j] += a[i][k] * b[k][j]
    return result

def multiplicar_matriz_vetor_4x4(matriz, vetor):
    result = [0, 0, 0, 0]
    for i in range(4):
        for j in range(4):
            result[i] += matriz[i][j] * vetor[j]
    return result

def matriz_translacao_3d(tx, ty, tz):
    return [
        [1, 0, 0, tx],
        [0, 1, 0, ty],
        [0, 0, 1, tz],
        [0, 0, 0, 1]
    ]

def matriz_escala_3d(sx, sy, sz):
    return [
        [sx, 0, 0, 0],
        [0, sy, 0, 0],
        [0, 0, sz, 0],
        [0, 0, 0, 1]
    ]

def matriz_rotacao_x(angulo_graus):
    rad = math.radians(angulo_graus)
    c, s = math.cos(rad), math.sin(rad)
    return [
        [1, 0,  0, 0],
        [0, c, -s, 0],
        [0, s,  c, 0],
        [0, 0,  0, 1]
    ]

def matriz_rotacao_y(angulo_graus):
    rad = math.radians(angulo_graus)
    c, s = math.cos(rad), math.sin(rad)
    return [
        [ c, 0, s, 0],
        [ 0, 1, 0, 0],
        [-s, 0, c, 0],
        [ 0, 0, 0, 1]
    ]

def matriz_rotacao_z(angulo_graus):
    rad = math.radians(angulo_graus)
    c, s = math.cos(rad), math.sin(rad)
    return [
        [c, -s, 0, 0],
        [s,  c, 0, 0],
        [0,  0, 1, 0],
        [0,  0, 0, 1]
    ]

def matriz_reflexao_3d(plano):
    if plano == 'xy':
        return [[1,0,0,0], [0,1,0,0], [0,0,-1,0], [0,0,0,1]]
    elif plano == 'xz':
        return [[1,0,0,0], [0,-1,0,0], [0,0,1,0], [0,0,0,1]]
    elif plano == 'yz':
        return [[-1,0,0,0], [0,1,0,0], [0,0,1,0], [0,0,0,1]]

def matriz_cisalhamento_3d(shXY, shXZ, shYZ):
    return [
        [1, shXY, shXZ, 0],
        [0, 1, shYZ, 0],
        [0, 0, 1, 0],
        [0, 0, 0, 1]
    ]

def projetar_3d_para_2d(x, y, z, width, height, zoom=100):
    """Projeção isométrica baseada no JS fornecido"""
    f = zoom / 100.0
    px = (x - z) * f * 0.7071 + width / 2
    py = (-y + (x + z) * 0.5) * f * 0.7071 + height / 2
    return int(round(px)), int(round(py))
