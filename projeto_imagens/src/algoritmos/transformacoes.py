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

            # Normaliza para [0,1]
            r_norm = res / 255.0
            # Aplica a fórmula clássica: s = constante . r^gamma, c = 1
            res = r_norm ** gamma
            #######
            # reescalar para 0-255 só para visualização:
            res = res * 255.0
            # Usando valores válidos para exibição
            res = np.clip(res, 0, 255)
            # convertendo para inteiro
            res = res.astype(np.uint8)

    # S = alog(r + 1),
    # em que a é uma constante e r o nível de cinza da imagem;
    elif transformacao == "Transformação Logarítmica":
        c_input = parametros.get('log_c', 1.0)

        c_base = 255.0 / np.log(256.0)

        res = (c_input * c_base) * np.log(res + 1.0)

        res = np.clip(res, 0, 255)
        res = res.astype(np.uint8)

    elif transformacao == "Transferência Linear":
        a = parametros.get('a', 1.0)
        b = parametros.get('b', 1.0)
        res = a * res + b


    elif transformacao == "Faixa Dinâmica":
        target = parametros.get('dynamic_target', 255.0)

        f_min = np.min(res)
        f_max = np.max(res)

        if f_max > f_min:
            res = ((res - f_min) / (f_max - f_min)) * target
        else:
            res = np.zeros_like(res)

        res = np.clip(res, 0, target)
        res = res.astype(np.uint8)

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