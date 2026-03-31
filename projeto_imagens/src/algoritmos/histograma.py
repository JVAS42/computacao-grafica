import numpy as np
from PIL import Image, ImageDraw


def garantir_escala_255(matriz):
    """
    Verifica se a matriz está normalizada entre 0.0 e 1.0 (ponto flutuante).
    Se sim, converte e escala corretamente para inteiros de 0 a 255.
    """
    # Se a imagem estiver usando valores decimais (0.0 a 1.0), multiplicamos por 255
    # para que ela use a "régua" padrão de pixels, onde 0 é preto e 255 é branco.
    if matriz.dtype in [np.float32, np.float64] and np.max(matriz) <= 1.0:
        return np.round(matriz * 255).astype(np.uint8)

    # Se já estiver entre 0 e 255, apenas "cortamos" valores fora do limite por segurança.
    return np.clip(matriz, 0, 255).astype(np.uint8)


def calcular_histograma(matriz):
    """Calcula a frequência dos níveis de cinza (0 a 255)."""
    # Garante a escala correta da imagem.
    matriz_ajustada = garantir_escala_255(matriz)

    # Faz o "censo": conta exatamente quantos pixels existem para cada tom de cinza,
    # organizando os resultados em 256 "caixas" (bins).
    hist, _ = np.histogram(matriz_ajustada.flatten(), bins=256, range=(0, 256))
    return hist


def equalizar_imagem(matriz):
    """
    Equaliza a matriz da imagem usando a FDA (Função de Distribuição Acumulada),
    em conformidade estrita com a teoria de Gonzalez & Woods.
    """
    # 1. Garante que os dados estejam na escala correta (0 a 255) antes de começar.
    matriz_ajustada = garantir_escala_255(matriz)

    # 2. Instancia o histograma (faz a contagem de cada tom de cinza).
    hist = calcular_histograma(matriz_ajustada)

    # 3. Calcula a probabilidade: divide a quantidade de pixels de cada tom pelo total de pixels da imagem.
    prob = hist / matriz_ajustada.size

    # 4. Calcula a FDA (Função de Distribuição Acumulada) fazendo uma soma progressiva das probabilidades.
    cdf = np.cumsum(prob)

    # 5. Cria um "mapa" multiplicando a FDA por 255. Isso diz para qual novo tom
    # (mais espaçado) cada tom antigo deve ser movido.
    mapa_escala = np.round(cdf * 255).astype(np.uint8)

    # 6. Aplica o mapeamento diretamente na matriz, "esticando" o contraste da imagem.
    matriz_equalizada = mapa_escala[matriz_ajustada]

    # Retorna como float32 (decimal) para manter compatibilidade com a interface gráfica.
    return matriz_equalizada.astype(np.float32)


def gerar_grafico_histograma(matriz, desenhar_cdf=False):
    """Desenha um gráfico de barras usando PIL puro para exibir na interface."""
    if matriz is None:
        return Image.new("RGB", (256, 150), color="#D3D3D3")

    matriz_ajustada = garantir_escala_255(matriz)
    hist = calcular_histograma(matriz_ajustada)

    # Encontra o tom de cinza mais frequente para definir a altura máxima do gráfico.
    max_val = np.max(hist) if np.max(hist) > 0 else 1

    largura, altura = 256, 150
    img_pil = Image.new("RGB", (largura, altura), color="#D3D3D3")
    draw = ImageDraw.Draw(img_pil)

    # Desenha o histograma: cria as barras verticais (em azul escuro) proporcionais à quantidade de pixels.
    for i in range(256):
        altura_barra = int((hist[i] / max_val) * altura)
        draw.line([(i, altura), (i, altura - altura_barra)], fill="#213555", width=1)

    # Desenha a linha da CDF (em vermelho) sobre o histograma, mostrando o crescimento acumulado.
    if desenhar_cdf:
        prob = hist / matriz_ajustada.size
        cdf = np.cumsum(prob)
        for i in range(255):
            y1 = altura - int(cdf[i] * altura)
            y2 = altura - int(cdf[i + 1] * altura)
            draw.line([(i, y1), (i + 1, y2)], fill="#E02401", width=2)

    return img_pil