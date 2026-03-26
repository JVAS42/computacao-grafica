import numpy as np


def dilatacao(matriz, kernel_flat):
    """Expande os pixels brancos baseando-se no kernel (Max Filter)."""
    h, w = matriz.shape
    # Adiciona borda de zeros para a janela não quebrar nos cantos
    pad_mat = np.pad(matriz, pad_width=1, mode='constant', constant_values=0)
    shifts = []

    k = 0
    for i in range(3):
        for j in range(3):
            # Se a posição do kernel for 1, consideramos aquele "deslizamento" da imagem
            if kernel_flat[k] > 0:
                shifts.append(pad_mat[i:i + h, j:j + w])
            k += 1

    # Empilha todos os deslocamentos válidos e pega o maior pixel em cada posição de uma vez só
    if not shifts: return matriz
    return np.max(shifts, axis=0)


def erosao(matriz, kernel_flat):
    """Encolhe os pixels brancos baseando-se no kernel (Min Filter)."""
    h, w = matriz.shape
    # Para a erosão, a borda deve ser 255 para não "comer" as bordas da imagem original
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
    """Delega a operação selecionada para as funções base."""
    if operacao == "Original":
        return np.copy(matriz)
    elif operacao == "Complemento":
        return 255.0 - matriz

    # Atalhos para não repetir código
    dil = lambda m: dilatacao(m, kernel_flat)
    ero = lambda m: erosao(m, kernel_flat)

    if operacao in ["Erosão", "Erosão (Cinza)"]:
        return ero(matriz)
    elif operacao in ["Dilatação", "Dilatação (Cinza)"]:
        return dil(matriz)
    elif operacao in ["Abertura", "Abertura (Cinza)"]:
        return dil(ero(matriz))
    elif operacao in ["Fechamento", "Fechamento (Cinza)"]:
        return ero(dil(matriz))

    # Contornos e Gradientes (Combinações matemáticas das operações base)
    elif operacao == "Contorno Externo":
        return np.clip(dil(matriz) - matriz, 0, 255)
    elif operacao == "Contorno Interno":
        return np.clip(matriz - ero(matriz), 0, 255)
    elif operacao == "Gradiente":
        return np.clip(dil(matriz) - ero(matriz), 0, 255)
    elif operacao == "Afinamento":
        return np.clip(matriz - (dil(matriz) - ero(matriz)), 0, 255)

    # Chapéus
    elif operacao == "Top Hat":
        return np.clip(matriz - dil(ero(matriz)), 0, 255)
    elif operacao == "Bottom Hat":
        return np.clip(ero(dil(matriz)) - matriz, 0, 255)

    return np.copy(matriz)