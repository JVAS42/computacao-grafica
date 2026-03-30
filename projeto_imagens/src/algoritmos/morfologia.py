import numpy as np

def dilatacao(matriz, kernel_flat):
    """Expande os pixels brancos. Reflete o kernel para manter o rigor matemático."""
    h, w = matriz.shape
    pad_mat = np.pad(matriz, pad_width=1, mode='constant', constant_values=0)
    shifts = []

    # Reflexão do elemento estruturante (obrigatório na definição de Dilatação)
    kernel_refletido = kernel_flat[::-1]

    k = 0
    for i in range(3):
        for j in range(3):
            if kernel_refletido[k] > 0:
                shifts.append(pad_mat[i:i + h, j:j + w])
            k += 1

    if not shifts: return matriz
    return np.max(shifts, axis=0)

def erosao(matriz, kernel_flat):
    """Encolhe os pixels brancos baseando-se no kernel (Min Filter)."""
    h, w = matriz.shape
    pad_mat = np.pad(matriz, pad_width=1, mode='constant', constant_values=255)
    shifts = []

    k = 0
    for i in range(3):
        for j in range(3):
            if kernel_flat[k] > 0:
                shifts.append(pad_mat[i:i + h, j:j + w])
            k += 1

    if not shifts: return matriz
    return np.min(shifts, axis=0)

def processar_morfologia(matriz, operacao, kernel_flat):
    """Delega a operação selecionada protegendo contra underflow de uint8."""
    if operacao == "Original":
        return np.copy(matriz)
    elif operacao == "Complemento":
        # uint8 lida bem com a subtração de 255
        return 255 - matriz

    dil = lambda m: dilatacao(m, kernel_flat)
    ero = lambda m: erosao(m, kernel_flat)

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

    if operacao == "Contorno Externo":
        res = dil(matriz).astype(np.int16) - m_calc
    elif operacao == "Contorno Interno":
        res = m_calc - ero(matriz).astype(np.int16)
    elif operacao == "Gradiente":
        res = dil(matriz).astype(np.int16) - ero(matriz).astype(np.int16)
    elif operacao == "Top Hat":
        abertura = dil(ero(matriz)).astype(np.int16)
        res = m_calc - abertura
    elif operacao == "Bottom Hat":
        fechamento = ero(dil(matriz)).astype(np.int16)
        res = fechamento - m_calc
    else:
        return np.copy(matriz)

    # Retorna ao formato padrão de imagem cortando o que passou de 0 a 255
    return np.clip(res, 0, 255).astype(np.uint8)