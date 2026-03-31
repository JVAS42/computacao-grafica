import numpy as np

"""
PARA IMAGENS EM NIVEIS DE CINZA :
 - EROSÃO = menor valor da vizinhaça 
 - DiLATAÇÃO - maior valor da vizinhança 
 - ABERTURA - Erosão e depois diltação da imagem erodida
 - FECHAMENTO - Dilataçaõ e depois erosão da imagem dilatada
 - GRADIENTE - dilatação - erosão 
 - CONTORNO INTERNO = f(pixel) - erosão
 - CONTORNO EXTERNO = dilatacao - f(pixel)
 - TOP-HAT = f(pixel) - abertura
 - BOTTOM-HAT = fechamento - f(pixel)

"""

"""
PARA IMAGENS BINÁRIAS (Lógica de Conjuntos):
 - EROSÃO (A ⊖ B)   = Onde o elemento estruturante B cabe totalmente em A (Continência).
 - DILATAÇÃO (A ⊕ B) = Onde há intersecção entre o elemento estruturante B e A (União).
 - ABERTURA (A ∘ B)  = Erosão seguida de Dilatação. Suaviza contornos e remove ruídos finos.
 - FECHAMENTO (A • B) = Dilatação seguida de Erosão. Preenche fendas e pequenos buracos.
 - GRADIENTE          = Diferença entre Dilatação e Erosão (Extração da borda completa).
 - CONTORNO INTERNO   = Imagem Original - Erosão (Pixels da borda dentro do objeto).
 - CONTORNO EXTERNO   = Dilatação - Imagem Original (Pixels que limitam o objeto por fora).
 - HIT-OR-MISS (A ⊗ B)= Detecção de padrões baseada na erosão de A e do seu complemento.
"""


# Dilatação
def dilatacao(matriz, kernel_flat, modo_cinza=False):
    h, w = matriz.shape
    pad_mat = np.pad(matriz, pad_width=1, mode='constant', constant_values=0)
    shifts = []

    print(f"\n--- VERIFICAÇÃO DILATAÇÃO ({'CINZA' if modo_cinza else 'BINÁRIA'}) ---")

    # 1. Coleta dos blocos para o cálculo real
    k = 0
    for i in range(3):
        for j in range(3):
            valor_kernel = kernel_flat[k]
            if valor_kernel > 0:
                if modo_cinza:
                    # Lógica Nível de Cinza: f(x) + b(x)
                    bloco = pad_mat[i:i + h, j:j + w].astype(np.int16) + valor_kernel
                else:
                    # Lógica Binária: Apenas expande a vizinhança
                    bloco = pad_mat[i:i + h, j:j + w]
                shifts.append(bloco)
            k += 1

    # 2. Prints de verificação para os primeiros 5 pixels
    for px_j in range(5):
        vizinhanca = pad_mat[0:3, px_j:px_j + 3]
        calc_detalhado = []
        k_idx = 0

        for row in range(3):
            for col in range(3):
                v_kern = kernel_flat[k_idx]
                if v_kern > 0:
                    v_px = vizinhanca[row, col]
                    if modo_cinza:
                        calc_detalhado.append(f"({v_px} + {v_kern} = {v_px + v_kern})")
                    else:
                        calc_detalhado.append(f"{v_px}")
                k_idx += 1

        if modo_cinza:
            resultado_print = np.max([int(float(x.split('=')[1].replace(')', ''))) for x in calc_detalhado])
            print(f"Pixel (0, {px_j}): Cálculos (f + b): {', '.join(calc_detalhado)} -> Max: {resultado_print}")
        else:
            resultado_print = np.max([int(float(x)) for x in calc_detalhado])
            print(f"Pixel (0, {px_j}): Valores analisados: {', '.join(calc_detalhado)} -> Max: {resultado_print}")

    if not shifts: return matriz

    # O np.max aplica a regra do valor máximo para ambos os casos
    res_final = np.max(shifts, axis=0)
    return np.clip(res_final, 0, 255).astype(np.uint8)


# Erosão
def erosao(matriz, kernel_flat, modo_cinza=False):
    h, w = matriz.shape
    pad_mat = np.pad(matriz, pad_width=1, mode='constant', constant_values=255)
    shifts = []

    print(f"\n--- VERIFICAÇÃO EROSÃO ({'CINZA' if modo_cinza else 'BINÁRIA'}) ---")

    # 1. Coleta dos blocos (shifts) para o cálculo real
    k = 0
    for i in range(3):
        for j in range(3):
            valor_kernel = kernel_flat[k]
            if valor_kernel > 0:
                if modo_cinza:
                    # Lógica Nível de Cinza: f(x) - b(x)
                    bloco = pad_mat[i:i + h, j:j + w].astype(np.int16) - valor_kernel
                else:
                    # Lógica Binária: Apenas verifica a vizinhança
                    bloco = pad_mat[i:i + h, j:j + w]
                shifts.append(bloco)
            k += 1

    # 2. Prints de verificação para os primeiros 5 pixels (Depuração)
    for px_j in range(5):
        vizinhanca = pad_mat[0:3, px_j:px_j + 3]
        calc_detalhado = []
        k_idx = 0

        for row in range(3):
            for col in range(3):
                v_kern = kernel_flat[k_idx]
                if v_kern > 0:
                    v_px = vizinhanca[row, col]
                    if modo_cinza:
                        calc_detalhado.append(f"({v_px} - {v_kern} = {v_px - v_kern})")
                    else:
                        calc_detalhado.append(f"{v_px}")
                k_idx += 1

        if modo_cinza:
            resultado_print = np.min([int(float(x.split('=')[1].replace(')', ''))) for x in calc_detalhado])
            print(f"Pixel (0, {px_j}): Cálculos (f - b): {', '.join(calc_detalhado)} -> Min: {resultado_print}")
        else:
            resultado_print = np.min([int(float(x)) for x in calc_detalhado])
            print(f"Pixel (0, {px_j}): Valores analisados: {', '.join(calc_detalhado)} -> Min: {resultado_print}")

    if not shifts: return matriz

    res_final = np.min(shifts, axis=0)
    return np.clip(res_final, 0, 255).astype(np.uint8)


# Processar a morfologia : delega a operação a opção selecionada protegendo contra underflow de uint8
def processar_morfologia(matriz, operacao, kernel_flat):
    if operacao == "Original":
        return np.copy(matriz)
    elif operacao == "Complemento":
        return 255 - matriz

    uso_cinza = "(Cinza)" in operacao or operacao in ["Top Hat", "Bottom Hat", "Gradiente"]

    dil = lambda m: dilatacao(m, kernel_flat, modo_cinza=uso_cinza)
    ero = lambda m: erosao(m, kernel_flat, modo_cinza=uso_cinza)

    # Operações Base
    if operacao in ["Erosão", "Erosão (Cinza)"]:
        return ero(matriz)
    elif operacao in ["Dilatação", "Dilatação (Cinza)"]:
        return dil(matriz)
    elif operacao in ["Abertura", "Abertura (Cinza)"]:
        return dil(ero(matriz))
    elif operacao in ["Fechamento", "Fechamento (Cinza)"]:
        return ero(dil(matriz))

    # --- Prevenção de Underflow em Subtrações ---
    # Convertendo para int16 evitamos que resultados negativos virem números gigantes (ex: -5 vira 251)
    m_calc = matriz.astype(np.int16)

    # Variáveis para armazenar resultados intermediários para o print
    res = None
    info_debug = ""

    if operacao == "Contorno Externo":
        dilatada = dil(matriz).astype(np.int16)
        res = dilatada - m_calc
        info_debug = f"Cálculo: Dilatação - Original"
    elif operacao == "Contorno Interno":
        erosada = ero(matriz).astype(np.int16)
        res = m_calc - erosada
        info_debug = f"Cálculo: Original - Erosão"
    elif operacao == "Gradiente":
        dilatada = dil(matriz).astype(np.int16)
        erosada = ero(matriz).astype(np.int16)
        res = dilatada - erosada
        info_debug = f"Cálculo: Dilatação - Erosão"
    elif operacao == "Top Hat":
        abertura = dil(ero(matriz)).astype(np.int16)
        res = m_calc - abertura
        info_debug = f"Cálculo: Original - Abertura"
    elif operacao == "Bottom Hat":
        fechamento = ero(dil(matriz)).astype(np.int16)
        res = fechamento - m_calc
        info_debug = f"Cálculo: Fechamento - Original"
    elif operacao in ["Abertura", "Abertura (Cinza)", "Fechamento", "Fechamento (Cinza)"]:
        return np.clip(ero(dil(matriz)) if "Fechamento" in operacao else dil(ero(matriz)), 0, 255).astype(np.uint8)
    else:
        return np.copy(matriz)

    # --- PRINT DE VERIFICAÇÃO DE OPERAÇÕES COMPOSTAS ---
    print(f"\n=== TESTE DE OPERAÇÃO COMPOSTA: {operacao} ===")
    print(f"{info_debug}")
    for j in range(5):
        val_orig = m_calc[0, j]
        val_final = res[0, j]
        print(f"Pixel (0, {j}): Resultado Final na Matriz = {val_final} (ajustado para 0-255 no retorno)")

    # Retorna ao formato padrão de imagem cortando o que passou de 0 a 255
    return np.clip(res, 0, 255).astype(np.uint8)