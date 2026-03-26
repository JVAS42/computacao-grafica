import numpy as np
from src.algoritmos.utils import normalizar_matriz


def combinar_imagens(img_a, img_b, operacao, normalizar=False):
    """Realiza operações aritméticas e lógicas entre duas matrizes de imagem."""

    # Garante que as imagens tenham o mesmo tamanho cortando as sobras
    # (Evita erro caso o usuário selecione imagens de tamanhos diferentes)
    h = min(img_a.shape[0], img_b.shape[0])
    w = min(img_a.shape[1], img_b.shape[1])

    a = img_a[:h, :w]
    b = img_b[:h, :w]

    if operacao == "+":
        res = a + b
    elif operacao == "-":
        res = a - b
    elif operacao == "*":
        res = a * b
    elif operacao == "/":
        # Evita a temida divisão por zero substituindo os zeros de 'b' por 1 temporariamente
        b_safe = np.where(b == 0, 1, b)
        res = np.where(b == 0, 0, a / b_safe)

    elif operacao in ["OR", "AND", "XOR"]:
        # Operadores bit a bit exigem números inteiros
        a_int = a.astype(np.uint8)
        b_int = b.astype(np.uint8)

        if operacao == "OR":
            res = np.bitwise_or(a_int, b_int)
        elif operacao == "AND":
            res = np.bitwise_and(a_int, b_int)
        elif operacao == "XOR":
            res = np.bitwise_xor(a_int, b_int)

        res = res.astype(np.float32)
    else:
        res = np.copy(a)

    if normalizar:
        return normalizar_matriz(res)

    # Limita os valores entre 0 e 255 para não quebrar a imagem final
    return np.clip(res, 0, 255)