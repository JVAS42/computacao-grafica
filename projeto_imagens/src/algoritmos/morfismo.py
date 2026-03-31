import numpy as np


def morfismo_temporal(matriz_a, matriz_b, t):
    """
    Realiza a transição mágica (morfismo) entre a Imagem A (Criança) e a Imagem B (Adulto).
    O parâmetro 't' é a nossa linha do tempo:
    - Quando t = 0.0, vemos apenas a Criança.
    - Quando t = 0.5, vemos uma mistura exata de metade Criança e metade Adulto.
    - Quando t = 1.0, vemos apenas o Adulto.
    """

    # --- PREPARAÇÃO DAS IMAGENS ---
    # Para misturar as imagens, elas precisam ter exatamente o mesmo tamanho.
    # Aqui, pegamos a menor largura e a menor altura entre as duas.
    h = min(matriz_a.shape[0], matriz_b.shape[0])
    w = min(matriz_a.shape[1], matriz_b.shape[1])

    # Cortamos as imagens (se necessário) para que fiquem com dimensões idênticas.
    img_a = matriz_a[:h, :w].astype(np.float32)
    img_b = matriz_b[:h, :w].astype(np.float32)

    # Atalho para poupar processamento: se o tempo for o início ou o fim exato,
    # não precisamos fazer cálculos complexos, basta devolver a imagem original.
    if t <= 0: return img_a
    if t >= 1: return img_b

    # --- PASSO 1 e 2: O ESQUELETO DA IMAGEM (Vértices) ---
    # Imagine que colocamos 5 "alfinetes" de ancoragem na foto: um em cada canto e um no meio.
    # 'v' são os alfinetes na foto da Criança.
    v = np.array([[0, 0], [w - 1, 0], [w - 1, h - 1], [0, h - 1], [w // 2, h // 2]], dtype=np.float32)

    # 'w_pts' são os alfinetes na foto do Adulto (neste caso simples, eles começam nas mesmas posições).
    w_pts = np.array([[0, 0], [w - 1, 0], [w - 1, h - 1], [0, h - 1], [w // 2, h // 2]], dtype=np.float32)

    # --- PASSO 3 e 5: CRIANDO A MALHA (Triangulação) ---
    # Para deformar a imagem, não mexemos pixel por pixel, mexemos em "pedaços".
    # Ligamos os alfinetes para formar 4 triângulos (como se fosse uma teia de aranha).
    triangulos = [[0, 1, 4], [1, 2, 4], [2, 3, 4], [3, 0, 4]]

    # --- PASSO 4: DEFORMAÇÃO NO TEMPO ---
    # Aqui a geometria se move! Calculamos onde cada "alfinete" deve estar no tempo 't'.
    # Se t=0.5, o alfinete estará exatamente no meio do caminho entre a posição da Criança e do Adulto.
    u = (1 - t) * v + t * w_pts

    # Preparamos uma tela em branco (uma matriz de zeros) para pintar o nosso frame intermediário.
    res = np.zeros((h, w), dtype=np.float32)

    # --- PREPARAÇÃO PARA PINTAR (Otimização) ---
    # Em vez de olhar para cada pixel individualmente usando um 'for' demorado,
    # criamos um mapa gigante com as coordenadas X e Y de todos os pixels da imagem de uma vez.
    grid_x, grid_y = np.meshgrid(np.arange(w), np.arange(h))
    coords = np.stack([grid_x, grid_y, np.ones_like(grid_x)], axis=-1)

    # Agora vamos olhar para cada um dos 4 "pedaços" (triângulos) que criamos.
    for tri in triangulos:
        ut = u[tri]  # Pontos do triângulo deformado no frame atual (tempo t)
        vt = v[tri]  # Pontos do triângulo na foto original da Criança
        wt = w_pts[tri]  # Pontos do triângulo na foto original do Adulto

        # --- PASSO 6 e 7: O GPS DOS PIXELS (Coordenadas Baricêntricas) ---
        # Precisamos saber onde cada pixel do nosso frame atual estava nas imagens originais.
        # Criamos uma matriz matemática que funciona como um conversor de "GPS".
        M_inv = np.linalg.inv(np.array([
            [ut[0, 0], ut[1, 0], ut[2, 0]],
            [ut[0, 1], ut[1, 1], ut[2, 1]],
            [1, 1, 1]
        ]))

        # Aplicamos nosso GPS em todos os pixels da tela ao mesmo tempo.
        pesos = coords @ M_inv.T

        # Como aplicamos em toda a tela, precisamos separar apenas os pixels que
        # realmente caem dentro do triângulo que estamos analisando agora.
        mask = np.all(pesos >= -0.001, axis=-1)

        if np.any(mask):
            p = pesos[mask]  # Pegamos apenas as coordenadas dos pixels válidos

            # --- PASSO 8: BUSCANDO A COR ORIGINAL ---
            # Usando nosso GPS, dizemos: "Pixel (x,y) do triângulo atual, de onde você veio
            # na foto da Criança (A)?"
            src_a_x = (p[:, 0] * vt[0, 0] + p[:, 1] * vt[1, 0] + p[:, 2] * vt[2, 0]).astype(int).clip(0, w - 1)
            src_a_y = (p[:, 0] * vt[0, 1] + p[:, 1] * vt[1, 1] + p[:, 2] * vt[2, 1]).astype(int).clip(0, h - 1)

            # E de onde você veio na foto do Adulto (B)?
            src_b_x = (p[:, 0] * wt[0, 0] + p[:, 1] * wt[1, 0] + p[:, 2] * wt[2, 0]).astype(int).clip(0, w - 1)
            src_b_y = (p[:, 0] * wt[0, 1] + p[:, 1] * wt[1, 1] + p[:, 2] * wt[2, 1]).astype(int).clip(0, h - 1)

            # --- PASSO 9: A MISTURA DE CORES (Cross-dissolve) ---
            # Por fim, misturamos a cor da Criança com a cor do Adulto dependendo do tempo 't'.
            # Se t=0.1, a cor será 90% Criança e 10% Adulto. É isso que cria o efeito suave!
            res[mask] = (1 - t) * img_a[src_a_y, src_a_x] + t * img_b[src_b_y, src_b_x]

    # Devolvemos a imagem pintada para ser exibida na tela.
    return res.astype(np.float32)