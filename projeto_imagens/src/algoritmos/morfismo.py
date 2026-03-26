import numpy as np


def morfismo_temporal(matriz_a, matriz_b, t):
    """
    Mistura as duas imagens usando o parâmetro t (de 0.0 a 1.0).
    t = 0.0 -> Mostra 100% da Imagem Criança
    t = 1.0 -> Mostra 100% da Imagem Adulto
    """
    # Garante que tenham o mesmo tamanho cortando as sobras
    h = min(matriz_a.shape[0], matriz_b.shape[0])
    w = min(matriz_a.shape[1], matriz_b.shape[1])

    a = matriz_a[:h, :w]
    b = matriz_b[:h, :w]

    # Fórmula: (1 - t) * A + t * B
    res = (1.0 - t) * a + (t * b)
    return np.clip(res, 0, 255).astype(np.float32)