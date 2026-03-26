import numpy as np
from src.algoritmos.utils import normalizar_matriz


def transformar_imagem(matriz, transformacao, parametros, normalizar=False):
    """
    Aplica transformações de intensidade na matriz.
    'parametros' é um dicionário contendo gamma, log_c, w, sigma, dynamic_target, a, b
    """
    res = np.copy(matriz)

    if transformacao == "Negativo":
        res = 255 - res

    elif transformacao == "Transformação Gamma":
        gamma = parametros.get('gamma', 1.0)
        if gamma > 0:
            res = 255.0 * (res / 255.0) ** (1.0 / gamma)

    elif transformacao == "Transformação Logarítmica":
        c_val = parametros.get('log_c', 1.0)
        c_const = 255.0 / np.log(256.0)
        res = c_val * c_const * np.log(res + 1.0)

    elif transformacao == "Transferência Linear":
        a = parametros.get('a', 1.0)
        b = parametros.get('b', 1.0)
        res = a * res + b

    elif transformacao == "Faixa Dinâmica":
        target = parametros.get('dynamic_target', 255.0)
        res = (res / 255.0) * target

    elif transformacao == "Transferência Sigmoide":
        w = parametros.get('w', 127.0)
        sigma = parametros.get('sigma', 25.0)
        if sigma == 0:
            # Comportamento de função degrau se o sigma for 0
            res = np.where(res < w, 0, 255)
        else:
            res = 255.0 / (1.0 + np.exp(-(res - w) / sigma))

    if normalizar:
        return normalizar_matriz(res)

    return np.clip(res, 0, 255)