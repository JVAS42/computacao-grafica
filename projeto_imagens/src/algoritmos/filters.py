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
    3: os.path.join(ASSETS_DIR, "airplane.pgm"),
    4: os.path.join(ASSETS_DIR, "Criança.pgm"),
    5: os.path.join(ASSETS_DIR, "Criança.pgm"),
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
    "robertsX": np.array([[1, 0, 0], [0, -1, 0], [0, 0, 0]]),
    "robertsY": np.array([[0, 1, 0], [-1, 0, 0], [0, 0, 0]]),
    "prewittX": np.array([[-1, 0, 1], [-1, 0, 1], [-1, 0, 1]]),
    "prewittY": np.array([[-1, -1, -1], [0, 0, 0], [1, 1, 1]]),
    "sobelX": np.array([[-1, 0, 1], [-2, 0, 2], [-1, 0, 1]]),
    "sobelY": np.array([[-1, -2, -1], [0, 0, 0], [1, 2, 1]]),
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
    10: "gaussianBlur"
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
    # Criamos a imagem expandida (com a borda de zeros)
    imagem_expandida = np.pad(imagem_matriz, ((1, 1), (1, 1)), mode='constant', constant_values=0)

    # IMPORTANTE: O resultado agora terá o tamanho da expandida (h+2, w+2)
    # para que os zeros da borda apareçam na tabela
    resultado = np.zeros(imagem_expandida.shape)

    # Fazemos o cálculo apenas no "miolo" (interior), deixando as bordas como 0
    for i in range(1, h + 1):
        for j in range(1, w + 1):
            vizinhanca = imagem_expandida[i - 1:i + 2, j - 1:j + 2]
            valor_calculado = np.sum(vizinhanca * kernel)
            resultado[i, j] = valor_calculado

    if normalizar:
        return normalizar_imagem(resultado)
    return np.clip(resultado, 0, 255).astype(np.uint8)


# Aplicando o filtro da Media
def aplicar_filtro_media(imagem_matriz, normalizar=False):
    """Aplica o filtro de média (suavização) usando a matriz 3x3."""
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
            # Ordena os valores (conforme a lógica de mediana)
            region_sorted = np.sort(region)
            # O valor central de 9 elementos é o de índice 4
            result[i, j] = region_sorted[4]

    return result


# Função para o Roberts Cruzado (Magnitude)
def aplicar_roberts_cruzado(image_data):
    # Calcula a derivada na diagonal X
    gx = aplicar_convolucao(image_data, KERNELS["robertsX"], normalizar=False)
    # Calcula a derivada na diagonal Y
    gy = aplicar_convolucao(image_data, KERNELS["robertsY"], normalizar=False)

    # Retorna a Magnitude (raiz da soma dos quadrados)
    return magnitude(gx.astype(float), gy.astype(float))


# Função para calcular a magnitude (Prewitt Cruzado)
def aplicar_prewitt_completo(image_data):
    # Usamos normalizar=False na convolução porque a magnitude cuidará disso depois
    gx = aplicar_convolucao(image_data, KERNELS["prewittX"], normalizar=False)
    gy = aplicar_convolucao(image_data, KERNELS["prewittY"], normalizar=False)

    # Se você corrigiu o nome para 'normalize', use normalize(mag)
    # Se manteve 'normalizar_imagem', use normalizar_imagem(mag)
    return magnitude(gx.astype(float), gy.astype(float))


# Função para o Sobel Cruzado (Magnitude)
def aplicar_sobel_cruzado(image_data):
    # Calcula as duas direções (X e Y)
    gx = aplicar_convolucao(image_data, KERNELS["sobelX"], normalizar=False)
    gy = aplicar_convolucao(image_data, KERNELS["sobelY"], normalizar=False)

    # Retorna a magnitude combinada usando a função que já corrigimos
    return magnitude(gx.astype(float), gy.astype(float))


def high_boost_filter(image_data, A=1.5):
    """
    Aplica o filtro High-Boost conforme a lógica do JS:
    Fórmula: original + A * (original - suavizada)
    """
    # Etapa 1: Obter versão suavizada (Passa-baixa)
    blurred = aplicar_convolucao(image_data, KERNELS["gaussianBlur"], normalizar=False)

    # CORREÇÃO CRÍTICA: Se blurred for (258, 258) e original for (256, 256)
    if blurred.shape != image_data.shape:
        # Corta 1 pixel de cada lado para voltar ao tamanho original
        blurred = blurred[1:-1, 1:-1]

    # Etapa 2: Calcular a máscara (High-pass)
    # Convertemos para float para evitar overflow em cálculos intermediários
    original = image_data.astype(float)
    smooth = blurred.astype(float)
    mask = original - smooth

    # Etapa 3: Boost
    boosted = original + (A * mask)

    # Garante intervalo [0, 255]
    return np.clip(boosted, 0, 255).astype(np.uint8)


# ===============================
# Funções de Normalização
# ===============================


def normalizar_imagem(matriz):
    """Lógica de normalização: traz os valores para a escala 0-255."""
    min_v = np.min(matriz)
    max_v = np.max(matriz)
    if max_v == min_v:
        return matriz.astype(np.uint8)

    res = (matriz - min_v) * (255.0 / (max_v - min_v))
    return res.astype(np.uint8)


def magnitude(data_x, data_y):
    """Calcula a magnitude de gradientes (usado em Sobel, Prewitt, Roberts)"""
    # Raiz quadrada da soma dos quadrados (Pitágoras)
    mag = np.sqrt(data_x ** 2 + data_y ** 2)
    return normalizar_imagem(mag)


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

