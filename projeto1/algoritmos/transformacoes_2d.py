# algoritmos/transformacoes_2d.py
import math


def multiplicar_matrizes(a, b):
    result = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
    for i in range(3):
        for j in range(3):
            for k in range(3):
                result[i][j] += a[i][k] * b[k][j]
    return result


def multiplicar_matriz_vetor(matriz, vetor):
    x = matriz[0][0] * vetor[0] + matriz[0][1] * vetor[1] + matriz[0][2] * 1
    y = matriz[1][0] * vetor[0] + matriz[1][1] * vetor[1] + matriz[1][2] * 1
    return [x, y]


def matriz_translacao(dx, dy):
    return [
        [1, 0, dx],
        [0, 1, dy],
        [0, 0, 1]
    ]


def matriz_escala(sx, sy):
    return [
        [sx, 0, 0],
        [0, sy, 0],
        [0, 0, 1]
    ]


def matriz_rotacao(angulo_graus, cx=0, cy=0):
    rad = math.radians(angulo_graus)
    cos = math.cos(rad)
    sin = math.sin(rad)

    m_rot = [
        [cos, -sin, 0],
        [sin, cos, 0],
        [0, 0, 1]
    ]

    if cx != 0 or cy != 0:
        t_pos = matriz_translacao(cx, cy)
        t_neg = matriz_translacao(-cx, -cy)
        return multiplicar_matrizes(t_pos, multiplicar_matrizes(m_rot, t_neg))

    return m_rot


def matriz_reflexao(ref_x, ref_y):
    return [
        [-1 if ref_y else 1, 0, 0],
        [0, -1 if ref_x else 1, 0],
        [0, 0, 1]
    ]


def matriz_cisalhamento(shx, shy):
    return [
        [1, shx, 0],
        [shy, 1, 0],
        [0, 0, 1]
    ]
