import numpy as np
from src.algoritmos.utils import normalizar_matriz

# Realiza operações aritméticas e lógicas entre duas matrizes de imagem
def combinar_imagens(img_a, img_b, operacao, normalizar=False):
    h = min(img_a.shape[0], img_b.shape[0])
    w = min(img_a.shape[1], img_b.shape[1])

    a = img_a[:h, :w]
    b = img_b[:h, :w]

    # Converte para float32 ANTES das operações matemáticas para evitar overflow/underflow
    a_calc = a.astype(np.float32)
    b_calc = b.astype(np.float32)

    if operacao == "+":
        res = a_calc + b_calc

    elif operacao == "-":
        res = a_calc - b_calc

    elif operacao == "*":
        res = a_calc * b_calc

    elif operacao == "/":
        b_safe = np.where(b_calc == 0, 1, b_calc)
        res = np.where(b_calc == 0, 0, a_calc / b_safe)

    elif operacao in ["OR", "AND", "XOR"]:
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
        res = np.copy(a_calc)

    # --- PRINT PARA VERIFICAÇÃO ---
    print(f"\nOperação: {operacao}")
    print("A (5px):", a_calc[0, :5])
    print("B (5px):", b_calc[0, :5])
    print("Resultado Bruto (5px):", res[0, :5])
    # ------------------------------

    # Se a normalização estiver ativa, passa a matriz com os valores reais (ex: negativos ou >255)
    if normalizar:
        return normalizar_matriz(res)

    # Se não for normalizar, corta os valores excedentes para caber em uma imagem válida
    return np.clip(res, 0, 255)