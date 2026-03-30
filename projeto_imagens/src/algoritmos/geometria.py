import numpy as np
from PIL import Image, ImageDraw


def get_matriz_identidade():
    """Matriz base que não altera a imagem."""
    return np.eye(3)


def matriz_translacao(tx, ty):
    return np.array([
        [1, 0, tx],
        [0, 1, ty],
        [0, 0, 1]
    ])


def matriz_escala(sx, sy):
    # Evita fator zero que causaria erro de matriz singular
    if sx == 0: sx = 0.001
    if sy == 0: sy = 0.001
    return np.array([
        [sx, 0, 0],
        [0, sy, 0],
        [0, 0, 1]
    ])


def matriz_rotacao(angulo_graus):
    rad = np.radians(angulo_graus)
    c, s = np.cos(rad), np.sin(rad)
    return np.array([
        [c, -s, 0],
        [s, c, 0],
        [0, 0, 1]
    ])


def matriz_cisalhamento(hx, hy):
    return np.array([
        [1, hx, 0],
        [hy, 1, 0],
        [0, 0, 1]
    ])


def matriz_reflexao(eixo):
    if eixo == "Horizontal":  # Inverte o eixo Y
        return np.array([[-1, 0, 0], [0, 1, 0], [0, 0, 1]])
    else:  # Inverte o eixo X
        return np.array([[1, 0, 0], [0, -1, 0], [0, 0, 1]])


def renderizar_no_plano(matriz_img, matriz_transformacao, tamanho_canvas=600):
    """
    Gera o plano cartesiano branco liso apenas com os eixos X e Y e projeta a imagem.
    """
    # 1. Desenha o fundo totalmente branco
    img_plano = Image.new("RGB", (tamanho_canvas, tamanho_canvas), "#FFFFFF")
    draw = ImageDraw.Draw(img_plano)

    centro = tamanho_canvas // 2

    # Desenha apenas os Eixos X e Y principais (cruz no centro)
    draw.line([(centro, 0), (centro, tamanho_canvas)], fill="#213555", width=2)
    draw.line([(0, centro), (tamanho_canvas, centro)], fill="#213555", width=2)

    # 2. Prepara as coordenadas (Vetorização)
    h_orig, w_orig = matriz_img.shape
    y_idx, x_idx = np.indices((tamanho_canvas, tamanho_canvas))

    # Converte os índices da tela para o padrão do Plano Cartesiano (origem no meio)
    x_c = x_idx - centro
    y_c = centro - y_idx

    # Matriz [x, y, 1]
    coords_destino = np.stack((x_c.flatten(), y_c.flatten(), np.ones(tamanho_canvas * tamanho_canvas)))

    # 3. Mapeamento Inverso da Matriz
    try:
        matriz_inv = np.linalg.inv(matriz_transformacao)
    except np.linalg.LinAlgError:
        return img_plano

    coords_origem = matriz_inv @ coords_destino

    # 4. Converte de volta para achar os pixels originais da imagem PGM
    u_img = coords_origem[0] + w_orig / 2.0
    v_img = (h_orig / 2.0) - coords_origem[1]

    u_int = np.round(u_img).astype(int)
    v_int = np.round(v_img).astype(int)

    # Filtra os pixels que caem dentro da imagem original
    mask = (u_int >= 0) & (u_int < w_orig) & (v_int >= 0) & (v_int < h_orig)

    # 5. Pinta os pixels (Usando RGBA para manter o fundo do plano visível onde não houver imagem)
    matriz_rgba = np.zeros((tamanho_canvas * tamanho_canvas, 4), dtype=np.uint8)
    pixels_clipados = np.clip(matriz_img[v_int[mask], u_int[mask]], 0, 255).astype(np.uint8)

    matriz_rgba[mask, 0] = pixels_clipados  # R
    matriz_rgba[mask, 1] = pixels_clipados  # G
    matriz_rgba[mask, 2] = pixels_clipados  # B
    matriz_rgba[mask, 3] = 255  # Alpha (Transparência)

    matriz_rgba = matriz_rgba.reshape((tamanho_canvas, tamanho_canvas, 4))
    img_transformada = Image.fromarray(matriz_rgba, "RGBA")

    # Cola a imagem distorcida por cima do plano liso
    img_plano.paste(img_transformada, (0, 0), img_transformada)
    return img_plano