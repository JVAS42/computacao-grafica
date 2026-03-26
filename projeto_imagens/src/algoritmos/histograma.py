import numpy as np
from PIL import Image, ImageDraw

def calcular_histograma(matriz):
    """Calcula a frequência dos níveis de cinza (0 a 255)."""
    # Achata a matriz para 1D e conta as ocorrências
    hist, _ = np.histogram(matriz.flatten(), bins=256, range=(0, 256))
    return hist


def equalizar_imagem(matriz):
    """Equaliza a matriz da imagem usando a FDA (Função de Distribuição Acumulada)."""
    # 1. Instancia o histograma
    hist = calcular_histograma(matriz)

    # 2. Calcula a probabilidade (histProb)
    prob = hist / matriz.size

    # 3. Calcula a FDA (Accumulated Proba) somando cumulativamente
    cdf = np.cumsum(prob)

    # 4. Escala para o range 0-255 (Scale Arr)
    mapa_escala = np.ceil(cdf * 255).astype(np.uint8)

    # 5. Aplica o mapeamento diretamente na matriz inteira de uma vez só!
    matriz_int = np.clip(matriz, 0, 255).astype(np.uint8)
    matriz_equalizada = mapa_escala[matriz_int]

    return matriz_equalizada.astype(np.float32)


def gerar_grafico_histograma(matriz, desenhar_cdf=False):
    """Desenha um gráfico de barras usando PIL puro para exibir na interface."""
    hist = calcular_histograma(matriz)
    max_val = np.max(hist) if np.max(hist) > 0 else 1

    largura, altura = 256, 150
    img_pil = Image.new("RGB", (largura, altura), color="#D3D3D3")
    draw = ImageDraw.Draw(img_pil)

    for x in range(256):
        h_barra = int((hist[x] / max_val) * (altura - 20))
        y0, y1 = altura - h_barra, altura
        draw.line([(x, y0), (x, y1)], fill="gray", width=1)

    if desenhar_cdf:
        prob = hist / matriz.size
        cdf = np.cumsum(prob)
        pontos = []
        for x in range(256):
            y = altura - int(cdf[x] * (altura - 20))
            pontos.append((x, y))
        draw.line(pontos, fill="black", width=2)

    return img_pil