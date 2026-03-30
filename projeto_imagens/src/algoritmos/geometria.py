import numpy as np
from PIL import Image


def normalizar_imagem(matriz):
    """Aplica a normalização Min-Max para o intervalo de 0 a 255."""
    min_val = np.min(matriz)
    max_val = np.max(matriz)

    if max_val == min_val:
        return matriz.astype(np.uint8)

    matriz_norm = ((matriz - min_val) / (max_val - min_val)) * 255
    return matriz_norm.astype(np.uint8)


# ================= MATRIZES =================

def get_matriz_identidade(): return np.eye(3)


def matriz_translacao(tx, ty):
    return np.array([[1, 0, tx], [0, 1, ty], [0, 0, 1]])


def matriz_escala(sx, sy):
    if sx == 0: sx = 0.001
    if sy == 0: sy = 0.001
    return np.array([[sx, 0, 0], [0, sy, 0], [0, 0, 1]])


def matriz_rotacao(angulo_graus):
    rad = np.radians(angulo_graus)
    c, s = np.cos(rad), np.sin(rad)
    return np.array([[c, -s, 0], [s, c, 0], [0, 0, 1]])


def matriz_cisalhamento(hx, hy):
    return np.array([[1, hx, 0], [hy, 1, 0], [0, 0, 1]])


def matriz_reflexao(eixo):
    if eixo == "Horizontal":
        return np.array([[-1, 0, 0], [0, 1, 0], [0, 0, 1]])
    else:
        return np.array([[1, 0, 0], [0, -1, 0], [0, 0, 1]])


def matriz_gato_arnold():
    """Transformação Clássica do Gato de Arnold (Cisalhamento duplo)."""
    return np.array([
        [1, 1, 0],
        [1, 2, 0],
        [0, 0, 1]
    ])


# ================= RENDERIZAÇÃO =================

def renderizar_imagem(matriz_img, matriz_transformacao, modo="auto"):
    """
    Modos:
    - 'auto': Expande o canvas dinamicamente para caber a transformação.
    - 'original': Mantém o tamanho original (corta o que sair).
    - 'modular': Mantém o tamanho original, mas aplica Aritmética Modular (wrap-around).
    """

    # 1. NORMALIZAÇÃO (Atua na Cor/Intensidade)
    matriz_img_normalizada = normalizar_imagem(matriz_img)
    h_orig, w_orig = matriz_img_normalizada.shape

    # 2. DEFINIÇÃO DA JANELA (Bounding Box)
    if modo == "auto":
        cantos = np.array([
            [-w_orig / 2, w_orig / 2, w_orig / 2, -w_orig / 2],
            [h_orig / 2, h_orig / 2, -h_orig / 2, -h_orig / 2],
            [1, 1, 1, 1]
        ])
        cantos_transf = matriz_transformacao @ cantos
        max_x = max(abs(cantos_transf[0].min()), abs(cantos_transf[0].max()))
        max_y = max(abs(cantos_transf[1].min()), abs(cantos_transf[1].max()))
        tamanho_w = max(1, int(np.ceil(max_x * 2)))
        tamanho_h = max(1, int(np.ceil(max_y * 2)))
    else:
        # Modos 'original' e 'modular' usam o tamanho estrito da imagem
        tamanho_w, tamanho_h = w_orig, h_orig

    if tamanho_w > 4000 or tamanho_h > 4000:
        return Image.new("RGB", (w_orig, h_orig), "#000000")

    img_fundo = Image.new("RGB", (tamanho_w, tamanho_h), "#000000")
    centro_w, centro_h = tamanho_w // 2, tamanho_h // 2
    y_idx, x_idx = np.indices((tamanho_h, tamanho_w))

    x_c = x_idx - centro_w
    y_c = centro_h - y_idx
    coords_destino = np.stack((x_c.flatten(), y_c.flatten(), np.ones(tamanho_w * tamanho_h)))

    try:
        matriz_inv = np.linalg.inv(matriz_transformacao)
    except np.linalg.LinAlgError:
        return img_fundo

    coords_origem = matriz_inv @ coords_destino

    u_img = coords_origem[0] + w_orig / 2.0
    v_img = (h_orig / 2.0) - coords_origem[1]

    # 3. MAPEAMENTO ESPACIAL (Aritmética Modular vs Clipping)
    matriz_rgba = np.zeros((tamanho_h * tamanho_w, 4), dtype=np.uint8)

    if modo == "modular":
        # A MÁGICA DA ARITMÉTICA MODULAR AQUI
        # O operador % força as coordenadas a "darem a volta" na imagem
        u_int = np.round(u_img).astype(int) % w_orig
        v_int = np.round(v_img).astype(int) % h_orig

        # Como usamos o módulo, nenhum pixel cai fora. Pegamos todos os valores normalizados.
        pixels_finais = np.clip(matriz_img_normalizada[v_int, u_int], 0, 255).astype(np.uint8)

        matriz_rgba[:, 0] = pixels_finais
        matriz_rgba[:, 1] = pixels_finais
        matriz_rgba[:, 2] = pixels_finais
        matriz_rgba[:, 3] = 255

    else:  # Modos 'auto' ou 'original' (Cortam o que sai da tela)
        u_int = np.round(u_img).astype(int)
        v_int = np.round(v_img).astype(int)
        mask = (u_int >= 0) & (u_int < w_orig) & (v_int >= 0) & (v_int < h_orig)

        pixels_clipados = np.clip(matriz_img_normalizada[v_int[mask], u_int[mask]], 0, 255).astype(np.uint8)

        matriz_rgba[mask, 0] = pixels_clipados
        matriz_rgba[mask, 1] = pixels_clipados
        matriz_rgba[mask, 2] = pixels_clipados
        matriz_rgba[mask, 3] = 255

    matriz_rgba = matriz_rgba.reshape((tamanho_h, tamanho_w, 4))
    img_transformada = Image.fromarray(matriz_rgba, "RGBA")
    img_fundo.paste(img_transformada, (0, 0), img_transformada)

    return img_fundo