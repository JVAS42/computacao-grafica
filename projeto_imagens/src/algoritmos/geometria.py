import numpy as np
from PIL import Image


def normalizar_imagem(matriz):
    """
    Aplica a normalização Min-Max para garantir que os tons de cinza fiquem
    visíveis no intervalo padrão de telas (0 a 255).
    Se a imagem for muito escura, isso 'estica' os valores para usar todo o espectro.
    """
    min_val = np.min(matriz)
    max_val = np.max(matriz)

    # Se a imagem for de uma cor só (máximo igual ao mínimo), não há o que normalizar.
    if max_val == min_val:
        return matriz.astype(np.uint8)

    # Fórmula: (valor - mínimo) / (máximo - mínimo) * 255
    matriz_norm = ((matriz - min_val) / (max_val - min_val)) * 255
    return matriz_norm.astype(np.uint8)


# ================= MATRIZES DE TRANSFORMAÇÃO =================
# Em Computação Gráfica 2D, usamos matrizes 3x3 (Coordenadas Homogêneas).
# Isso nos permite combinar (multiplicar) várias transformações, incluindo a
# translação, que não seria possível com matrizes 2x2.

def get_matriz_identidade():
    """A matriz identidade é como o 'número 1' das matrizes. Multiplicar por ela não altera nada."""
    return np.eye(3)


def matriz_translacao(tx, ty):
    """Move a imagem. 'tx' desloca na horizontal, 'ty' na vertical."""
    return np.array([
        [1, 0, tx],
        [0, 1, ty],
        [0, 0, 1]
    ])


def matriz_escala(sx, sy):
    """Altera o tamanho. sx e sy multiplicam a largura e altura. Evitamos o 0 absoluto para não 'sumir' com a imagem."""
    if sx == 0: sx = 0.001
    if sy == 0: sy = 0.001
    return np.array([
        [sx, 0, 0],
        [0, sy, 0],
        [0, 0, 1]
    ])


def matriz_rotacao(angulo_graus):
    """Gira a imagem usando trigonometria (seno e cosseno). O computador calcula em radianos."""
    rad = np.radians(angulo_graus)
    c, s = np.cos(rad), np.sin(rad)
    return np.array([
        [c, -s, 0],
        [s, c, 0],
        [0, 0, 1]
    ])


def matriz_cisalhamento(hx, hy):
    """Entorta (inclina) a imagem, como se empurrássemos o topo de um baralho de cartas para o lado."""
    return np.array([
        [1, hx, 0],
        [hy, 1, 0],
        [0, 0, 1]
    ])


def matriz_reflexao(eixo):
    """Espelha a imagem invertendo o sinal de uma das coordenadas."""
    if eixo == "Horizontal":
        return np.array([[-1, 0, 0], [0, 1, 0], [0, 0, 1]])  # Inverte o X
    else:
        return np.array([[1, 0, 0], [0, -1, 0], [0, 0, 1]])  # Inverte o Y


def matriz_gato_arnold():
    """Transformação matemática caótica que estica e dobra a imagem continuamente."""
    return np.array([
        [1, 1, 0],
        [1, 2, 0],
        [0, 0, 1]
    ])


# ================= RENDERIZAÇÃO (O MOTOR GRÁFICO) =================

def renderizar_imagem(matriz_img, matriz_transformacao, modo="auto"):
    """
    Aplica a matemática na imagem usando MAPEAMENTO INVERSO.
    Em vez de pegar um pixel da origem e ver onde ele cai no destino (o que deixa buracos),
    nós varremos a tela de destino e perguntamos: "De qual pixel da origem você veio?".
    """
    matriz_img_normalizada = normalizar_imagem(matriz_img)
    h_orig, w_orig = matriz_img_normalizada.shape

    # 1. CALCULAR O TAMANHO DA NOVA TELA (Bounding Box)
    if modo == "auto":
        # Pega as 4 pontas da imagem original e aplica a transformação para ver onde elas vão parar
        cantos = np.array([
            [-w_orig / 2, w_orig / 2, w_orig / 2, -w_orig / 2],
            [h_orig / 2, h_orig / 2, -h_orig / 2, -h_orig / 2],
            [1, 1, 1, 1]
        ])
        cantos_transf = matriz_transformacao @ cantos

        # Descobre o novo limite máximo de X e Y para criar um canvas que caiba tudo
        max_x = max(abs(cantos_transf[0].min()), abs(cantos_transf[0].max()))
        max_y = max(abs(cantos_transf[1].min()), abs(cantos_transf[1].max()))
        tamanho_w = max(1, int(np.ceil(max_x * 2)))
        tamanho_h = max(1, int(np.ceil(max_y * 2)))
    else:
        # Se não for 'auto', a tela de destino terá exatamente o mesmo tamanho da original
        tamanho_w, tamanho_h = w_orig, h_orig

    # Trava de segurança para não explodir a memória do computador
    if tamanho_w > 4000 or tamanho_h > 4000:
        return Image.new("RGB", (w_orig, h_orig), "#000000")

    img_fundo = Image.new("RGB", (tamanho_w, tamanho_h), "#000000")
    centro_w, centro_h = tamanho_w // 2, tamanho_h // 2

    # Cria uma grade com as coordenadas de todos os pixels da tela de destino
    y_idx, x_idx = np.indices((tamanho_h, tamanho_w))

    # Move a origem (0,0) para o centro da imagem
    x_c = x_idx - centro_w
    y_c = centro_h - y_idx
    coords_destino = np.stack((x_c.flatten(), y_c.flatten(), np.ones(tamanho_w * tamanho_h)))

    # 2. O MAPEAMENTO INVERSO
    # Para saber de onde o pixel veio, invertemos a matriz de transformação
    try:
        matriz_inv = np.linalg.inv(matriz_transformacao)
    except np.linalg.LinAlgError:
        return img_fundo  # Se a matriz não puder ser invertida, retorna tela preta

    # Multiplica as coordenadas do destino pela matriz inversa para achar as coordenadas originais
    coords_origem = matriz_inv @ coords_destino

    # Retorna o (0,0) para o canto superior esquerdo (padrão de imagens no computador)
    u_img = coords_origem[0] + w_orig / 2.0
    v_img = (h_orig / 2.0) - coords_origem[1]

    matriz_rgba = np.zeros((tamanho_h * tamanho_w, 4), dtype=np.uint8)

    # 3. LIDANDO COM OS PIXELS (Aritmética Modular vs Corte)
    if modo == "modular":
        # Efeito "Pac-Man": o que sai por um lado da tela volta pelo outro
        u_int = np.round(u_img).astype(int) % w_orig
        v_int = np.round(v_img).astype(int) % h_orig

        pixels_finais = np.clip(matriz_img_normalizada[v_int, u_int], 0, 255).astype(np.uint8)

        matriz_rgba[:, 0] = pixels_finais
        matriz_rgba[:, 1] = pixels_finais
        matriz_rgba[:, 2] = pixels_finais
        matriz_rgba[:, 3] = 255

    else:
        # Modo 'auto' ou 'original': Se o pixel caiu fora da área da imagem original, ele some (Corte/Clipping)
        u_int = np.round(u_img).astype(int)
        v_int = np.round(v_img).astype(int)

        # 'mask' é uma máscara booleana: só é Verdadeiro se o pixel calculado existir na imagem original
        mask = (u_int >= 0) & (u_int < w_orig) & (v_int >= 0) & (v_int < h_orig)

        pixels_clipados = np.clip(matriz_img_normalizada[v_int[mask], u_int[mask]], 0, 255).astype(np.uint8)

        # Preenche apenas os pixels que passaram no filtro da máscara
        matriz_rgba[mask, 0] = pixels_clipados
        matriz_rgba[mask, 1] = pixels_clipados
        matriz_rgba[mask, 2] = pixels_clipados
        matriz_rgba[mask, 3] = 255  # Canal Alpha (Totalmente opaco)

    # Converte o array de volta para o formato de imagem final
    matriz_rgba = matriz_rgba.reshape((tamanho_h, tamanho_w, 4))
    img_transformada = Image.fromarray(matriz_rgba, "RGBA")
    img_fundo.paste(img_transformada, (0, 0), img_transformada)

    return img_fundo