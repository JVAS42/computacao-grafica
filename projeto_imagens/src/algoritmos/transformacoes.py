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
            # Aplica a fórmula clássica: s = r^gamma, c = 1
            res = r_norm ** gamma
            # reescalar para 0-255 só para visualização:
            res = res * 255.0
            # Usando valores válidos para exibição
            res = np.clip(res, 0, 255)
            # convertendo para inteiro
            res = res.astype(np.uint8)

    elif transformacao == "Transformação Logarítmica":
        ''' Baseado na nota (2): Logaritmo
        Fórmula: f(x) = c * log(x + 1)
        Onde 'c' pertence aos inteiros positivos e a entrada é qualquer valor exceto 0 negativo. '''
        c = 255.0 / np.log(256.0)
        res = c * np.log(res + 1.0)

        res = np.clip(res, 0, 255)
        res = res.astype(np.uint8)

    elif transformacao == "Transferência Linear":
        ''' Baseado na nota (1): Linear
        Representa a função do primeiro grau: f(x) = a*x + b
        As anotações trazem o exemplo a = 1/2 e b = 10. '''
        a = parametros.get('a', 1.0)
        b = parametros.get('b', 1.0)
        res = a * res + b

    elif transformacao == "Faixa Dinâmica":
        ''' Baseado na nota (3): Função de transformação de intensidade / Faixa dinâmica
        Fórmula anotada: f = [ (f - f_min) / (f_max - f_min) ] * w
        No código abaixo, 'target' desempenha o papel da variável 'w' da fórmula (ex: w = 10 nas notas). '''
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
        '''Baseado na nota (4): Transferência de intensidade geral
        Fórmula principal destacada: f(r) = 255 / (1 + e^(-(1/sigma) * (r - w)))
        Onde:
        r = entrada (pixel atual, equivalente ao 'res' na iteração matricial)
        w = parâmetro de deslocamento (exemplo das notas: w = 10)
        1/sigma = ganho 'g' (exemplo das notas: sigma = 100)
        A operação -(res - w) / sigma no código equivale a multiplicar por -(1/sigma). '''
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