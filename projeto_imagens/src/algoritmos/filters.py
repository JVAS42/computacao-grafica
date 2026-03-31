import numpy as np
import os

# ===============================
# Caminho das imagens
# ===============================
ASSETS_DIR = "assets"

IMAGES_PATH = {
    0: os.path.join(ASSETS_DIR, "lena.pgm"),
    1: os.path.join(ASSETS_DIR, "lenasalp.pgm"),
    2: os.path.join(ASSETS_DIR, "lenag.pgm"),
    3: os.path.join(ASSETS_DIR, "airplane.pgm")
}

# ===============================
# Constantes para cálculos de máscaras
# ===============================
NINTH = 1.0 / 9.0
SIXTEENTH = 1.0 / 16.0
EIGHTH = 1.0 / 8.0
FOURTH = 1.0 / 4.0

# ===============================
# Definição das máscaras (Kernels)
# ===============================
KERNELS = {
    "none": np.array([[0, 0, 0], [0, 1, 0], [0, 0, 0]]),
    "media": np.array([[NINTH, NINTH, NINTH], [NINTH, NINTH, NINTH], [NINTH, NINTH, NINTH]]),
    "passaAlto": np.array([[-1, -1, -1], [-1, 8, -1], [-1, -1, -1]]),
    "robertsX": np.array([[0, 0, 0], [0, 1, 0], [0, -1, 0]]),
    "robertsY": np.array([[0, 0, 0], [0, 1, -1], [0, 0, 0]]),
    "robertsCruzadoX": np.array([[0, 0, 0], [0, 1, 0], [0, 0, -1]]),
    "robertsCruzadoY": np.array([[0, 0, 0], [0, 0, 1], [0, -1, 0]]),
    "prewittX": np.array([[-1, -1, -1], [0, 0, 0], [1, 1, 1]]),
    "prewittY": np.array([[-1, 0, 1], [-1, 0, 1], [-1, 0, 1]]),
    "sobelX": np.array([[-1, -2, -1], [0, 0, 0], [1, 2, 1]]),
    "sobelY": np.array([[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]]),
    "laplace": np.array([[0, -1, 0], [-1, 4, -1], [0, -1, 0]]),
    "gaussianBlur": np.array([
        [SIXTEENTH, EIGHTH, SIXTEENTH],
        [EIGHTH, FOURTH, EIGHTH],
        [SIXTEENTH, EIGHTH, SIXTEENTH]
    ]),
}

# Mapeamento numérico para a UI
FILTER_MAP = {
    0: "none",
    9: "media",
    2: "passaAlto",
    3: "robertsX",
    4: "robertsY",
    5: "prewittX",
    6: "prewittY",
    17: "laplace",
    7: "sobelX",
    8: "sobelY",
    10: "gaussianBlur",
    12: "robertsCruzadoX",
    13: "robertsCruzadoY"
}


# =================================
# Função para ler o arquivo PGM e
# transforma os dados em uma matriz
# =================================

def load_pgm(path):
    """Lê um arquivo PGM (P2 ou P5) e retorna uma matriz numpy."""
    with open(path, 'rb') as f:
        # Lê a assinatura (P2 ou P5)
        header = f.readline().decode().strip()

        # Pula comentários
        line = f.readline().decode()
        while line.startswith('#'):
            line = f.readline().decode()

        # Pega dimensões (largura e altura)
        width, height = map(int, line.split())

        # Pega o valor máximo (geralmente 255)
        max_val = int(f.readline().decode())

        if header == 'P5':
            # Se for binário, lê os bytes diretamente
            data = np.fromfile(f, dtype=np.uint8)
        elif header == 'P2':
            # Se for ASCII (o seu caso), lê o resto do texto e converte para números
            all_pixels = f.read().decode().split()
            data = np.array(all_pixels, dtype=np.uint8)
        else:
            raise ValueError(f"Formato {header} não suportado. Use P2 ou P5.")

        return data.reshape((height, width))


# ===============================
# Funções de Processamento
# ===============================

# Função de convolução aplicada no filtro da média, passa alto
def aplicar_convolucao(imagem_matriz, kernel, normalizar=False):
    h, w = imagem_matriz.shape
    # A imagem expandida (com a borda de zeros)
    imagem_expandida = np.pad(imagem_matriz, ((1, 1), (1, 1)), mode='constant', constant_values=0)

    resultado = np.zeros(imagem_expandida.shape)
    for i in range(1, h + 1):
        for j in range(1, w + 1):
            vizinhanca = imagem_expandida[i - 1:i + 2, j - 1:j + 2]
            valor_calculado = np.sum(vizinhanca * kernel)
            resultado[i, j] = valor_calculado

    if normalizar:
        return normalizar_imagem(resultado)

    return np.clip(resultado, 0, 255).astype(np.uint8)


# Função de convolução aplicada no filtro da roberts simples, roberts cruzado, prewitt e sobel
def aplicar_convolucao_roberts(imagem_matriz, kernel):
    h, w = imagem_matriz.shape
    imagem_expandida = np.pad(imagem_matriz, ((1, 1), (1, 1)), mode='constant', constant_values=0)
    resultado = np.zeros((h, w), dtype=np.float64)

    for i in range(1, h + 1):
        for j in range(1, w + 1):
            vizinhanca = imagem_expandida[i - 1:i + 2, j - 1:j + 2]
            valor_calculado = np.sum(vizinhanca * kernel)
            resultado[i - 1, j - 1] = valor_calculado

            # TESTE 1: Ver o Gx ou Gy bruto antes de qualquer coisa
            if i == 1 and j <= 5:  # Pega os 5 primeiros da primeira linha
                print(f"--- CONVOLUÇÃO BRUTA ---")
                print(f"Pixel ({i - 1},{j - 1}) calculou: {valor_calculado}")

    return resultado


# Aplicando o filtro da Media (suavização)
def aplicar_filtro_media(imagem_matriz, normalizar=False):
    return aplicar_convolucao(imagem_matriz, KERNELS["media"], normalizar)


# Aplicando o filtro da Mediana
def aplicar_filtro_mediana(image_data):
    h, w = image_data.shape
    padded = np.pad(image_data, ((1, 1), (1, 1)), mode='edge')
    result = np.zeros((h, w), dtype=np.uint8)

    for i in range(h):
        for j in range(w):
            # Extrai a vizinhança 3x3
            region = padded[i:i + 3, j:j + 3].flatten()
            # Ordena os valores
            region_sorted = np.sort(region)
            # O valor central de 9 elementos é o de índice 4
            result[i, j] = region_sorted[4]

    return result


# Implementa mag = |Z5 - Z8| + |Z5 - Z6|
def aplicar_roberts_simples(image_data):
    gx = aplicar_convolucao_roberts(image_data, KERNELS["robertsX"])
    gy = aplicar_convolucao_roberts(image_data, KERNELS["robertsY"])

    # Cálculo da magnitude
    mag = np.abs(gx) + np.abs(gy)

    # Print do cálculos dos primeiros 5 pixels
    print("\n--- DEBUG ROBERTS SIMPLES (Slide) ---")
    for k in range(5):
        print(f"P{k}: |{gx[0, k]}| + |{gy[0, k]}| = {mag[0, k]}")

    return mag


# Implementa mag = |Z5 - Z9| + |Z6 - Z8|
def aplicar_roberts_cruzado(image_data):
    gx = aplicar_convolucao_roberts(image_data, KERNELS["robertsCruzadoX"])
    gy = aplicar_convolucao_roberts(image_data, KERNELS["robertsCruzadoY"])

    # Cálculo da magnitude
    mag = np.abs(gx) + np.abs(gy)

    # Print do cálculos dos primeiros 5 pixels
    print("\n--- DEBUG ROBERTS CRUZADO (Slide) ---")
    for k in range(5):
        print(f"P{k}: |{gx[0, k]}| + |{gy[0, k]}| = {mag[0, k]}")

    return mag


# Implementa Prewitt |Gx| + |Gy| = |(Z7 + Z8 + Z9) - (Z1 + Z2 + Z3) | + |(Z3 + Z6 + Z9) - (Z1 + Z4 + Z7)|
def aplicar_prewitt(image_data):
    gx = aplicar_convolucao_roberts(image_data, KERNELS["prewittX"])
    gy = aplicar_convolucao_roberts(image_data, KERNELS["prewittY"])

    # Cálculo da magnitude absoluta
    mag = np.abs(gx.astype(float)) + np.abs(gy.astype(float))

    # Print do cálculos dos primeiros 5 pixels
    print("\n--- DEBUG PREWITT (Slide) ---")
    for k in range(5):
        print(f"P{k}: |{gx[0, k]}| + |{gy[0, k]}| = {mag[0, k]}")

    return mag


# Implementa Sobel |Gx| + |Gy| =  |(Z7 + 2Z8 + Z9) - (Z1 + 2Z2 + Z3)| + |(Z3 + 2Z6 + Z9) - (Z1 + 2Z4 + Z7)|
def aplicar_sobel_slide(image_data):
    gx = aplicar_convolucao_roberts(image_data, KERNELS["sobelX"])
    gy = aplicar_convolucao_roberts(image_data, KERNELS["sobelY"])

    # Cálculo da magnitude absoluta (Slide)
    mag = np.abs(gx) + np.abs(gy)

    # LOG para você conferir a "explosão" de valores
    print("\n--- DEBUG SOBEL (Slide) ---")
    print(f"Maior valor encontrado (Max): {np.max(mag)}")
    for k in range(5):
        print(f"P{k}: |{gx[0, k]}| + |{gy[0, k]}| = {mag[0, k]}")

    return mag


# Implementa Alto Reforço - Fórmula: original + A * (original - suavizada)
def high_boost_filter(image_data, A=1.5):
    # Obter versão suavizada (Passa-baixa) - KERNELS["gaussianBlur"], que é uma matriz 3x3 que calcula a média ponderada dos vizinhos para remover detalhes finos.
    blurred = aplicar_convolucao(image_data, KERNELS["gaussianBlur"], normalizar=False)

    # Este corte garante que a imagem borrada tenha o mesmo tamanho da original
    if blurred.shape != image_data.shape:
        # Corta 1 pixel de cada lado para voltar ao tamanho original para que possamos subtrair um pixel do outro perfeitamente.
        blurred = blurred[1:-1, 1:-1]

    # Calcular a máscara
    original = image_data.astype(float)
    smooth = blurred.astype(float)
    mask = original - smooth

    # FORMULA g = Original + (A * Detalhes)
    boosted = original + (A * mask)

    # Garante intervalo [0, 255]
    return np.clip(boosted, 0, 255).astype(np.uint8)


# ===============================
# Funções de Normalização
# ===============================

# Lógica de normalização: traz os valores para a escala 0-255
# Normalizado = (bruto - min) X 255/ Max - Min
def normalizar_imagem(matriz):
    min_v = np.min(matriz)
    max_v = np.max(matriz)

    # ADICIONE ESTE PRINT:
    print(f"DEBUG NORM: Min={min_v}, Max={max_v}")

    if max_v == min_v:
        return matriz.astype(np.uint8)

    print("Conversão dos 5 primeiros pixels:")
    for i in range(5):
        bruto = matriz[0, i]
        normalizado = (bruto - min_v) * (255.0 / (max_v - min_v))
        print(f"P{i}: Bruto {bruto:.2f} -> Normalizado: {int(normalizado)}")

    res = (matriz - min_v) * (255.0 / (max_v - min_v))
    return res.astype(np.uint8)


# =================================
# Função para ler o arquivo PGM e
# retorna matriz
# =================================

def carregar_pgm(caminho):
    with open(caminho, 'rb') as f:
        header = f.readline().decode().strip()
        line = f.readline().decode()
        while line.startswith('#'): line = f.readline().decode()
        width, height = map(int, line.split())
        max_val = int(f.readline().decode())
        if header == 'P5':
            data = np.fromfile(f, dtype=np.uint8)
        else:
            data = np.array(f.read().decode().split(), dtype=np.uint8)
        return data.reshape((height, width))