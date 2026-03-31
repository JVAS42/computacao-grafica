import numpy as np
from src.algoritmos.utils import normalizar_matriz


def transformar_imagem(matriz, transformacao, parametros, normalizar=False):
    """
    Esta função é o "cérebro" das operações de intensidade. Ela pega a matriz de pixels
    da imagem original e aplica uma fórmula matemática a cada pixel individualmente
    para alterar seu brilho ou contraste.
    """
    # Trabalhamos sempre com uma cópia para não alterar a imagem original acidentalmente
    res = np.copy(matriz)

    # 1. NEGATIVO
    # Efeito: Inverte as cores. O que é preto vira branco e vice-versa.
    # Matemática: Subtrai o valor atual do pixel do valor máximo possível (255).
    if transformacao == "Negativo":
        res = 255 - res

    # 2. TRANSFORMAÇÃO GAMMA (Potência)
    # Efeito: Altera o contraste e brilho de forma não linear.
    # - Gamma < 1: Clareia as áreas escuras (expande os tons escuros).
    # - Gamma > 1: Escurece a imagem (comprime tons escuros, expande tons claros).
    elif transformacao == "Transformação Gamma":
        gamma = parametros.get('gamma', 1.0)
        if gamma > 0:
            # Para aplicar a potência de forma segura, convertemos os pixels
            # de 0-255 para uma escala de 0.0 a 1.0.
            r_norm = res / 255.0

            # Aplica a fórmula: novo_pixel = pixel_original ^ gamma
            res = r_norm ** gamma

            # Retorna a escala para 0-255 para podermos visualizar como imagem novamente
            res = res * 255.0
            # Garante que nenhum valor passe de 255 ou fique abaixo de 0 (clipping)
            res = np.clip(res, 0, 255)
            # Converte de volta para números inteiros (formato padrão de imagens 8-bits)
            res = res.astype(np.uint8)

    # 3. TRANSFORMAÇÃO LOGARÍTMICA
    # Efeito: Clareia muito os pixels escuros e comprime os pixels claros.
    # É excelente para revelar detalhes escondidos nas sombras.
    # Fórmula: S = c * log(r + 1)
    elif transformacao == "Transformação Logarítmica":
        c_input = parametros.get('log_c', 1.0)

        # Calculamos uma constante base para garantir que o brilho máximo
        # após o logaritmo ainda se encaixe na escala de 0 a 255.
        c_base = 255.0 / np.log(256.0)

        # Soma 1 ao pixel (res + 1.0) porque o log de 0 não existe (dá erro na matemática).
        res = (c_input * c_base) * np.log(res + 1.0)

        res = np.clip(res, 0, 255)
        res = res.astype(np.uint8)

    # 4. TRANSFERÊNCIA LINEAR
    # Efeito: Altera o brilho e contraste de forma direta.
    # 'a' controla o contraste (inclinação da reta) e 'b' controla o brilho (deslocamento).
    elif transformacao == "Transferência Linear":
        a = parametros.get('a', 1.0)
        b = parametros.get('b', 1.0)
        # Fórmula clássica da reta: y = ax + b
        res = a * res + b

    # 5. FAIXA DINÂMICA (Alargamento de Contraste)
    # Efeito: "Estica" as cores da imagem. Se a imagem está desbotada (pixels variando
    # só entre cinza claro e escuro), isso força o pixel mais escuro a virar 0 (preto)
    # e o mais claro a virar o alvo (ex: 255, branco).
    elif transformacao == "Faixa Dinâmica":
        target = parametros.get('dynamic_target', 255.0)

        f_min = np.min(res)  # Acha o pixel mais escuro da imagem atual
        f_max = np.max(res)  # Acha o pixel mais claro

        # Se houver alguma variação de cor na imagem, aplicamos a regra de três (normalização)
        if f_max > f_min:
            res = ((res - f_min) / (f_max - f_min)) * target
        else:
            # Se a imagem for toda de uma cor só (f_max == f_min), zera tudo
            res = np.zeros_like(res)

        res = np.clip(res, 0, target)
        res = res.astype(np.uint8)

    # 6. TRANSFERÊNCIA SIGMOIDE (Curva em S)
    # Efeito: Aumenta bastante o contraste no meio-tom e achata os extremos.
    # 'w' é o limiar central. 'sigma' define quão suave é a transição entre preto e branco.
    elif transformacao == "Transferência Sigmoide":
        w = parametros.get('w', 127.0)
        sigma = parametros.get('sigma', 25.0)

        if sigma == 0:
            # Se sigma for 0, vira uma função "degrau" (Limiarização/Binarização).
            # Tudo menor que 'w' vira preto (0), o resto vira branco (255).
            res = np.where(res < w, 0, 255)
        else:
            # Fórmula da curva sigmoide (S)
            res = 255.0 / (1.0 + np.exp(-(res - w) / sigma))

    # Opcional: aplica uma normalização customizada se foi pedido nos parâmetros
    if normalizar:
        return normalizar_matriz(res)

    # Limpa valores residuais menores que 0 ou maiores que 255 antes de retornar a imagem final
    return np.clip(res, 0, 255)