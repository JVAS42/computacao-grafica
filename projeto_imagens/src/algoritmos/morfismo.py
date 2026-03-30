import numpy as np

# Realiza o morfismo entre matriz_a (Criança) e matriz_b (Adulto) no instante t. Segue o procedimento de 9 passos descrito no livro.
   
def morfismo_temporal(matriz_a, matriz_b, t):
    # Prepara as dimensões (Garante que as matrizes tenham o mesmo tamanho para o cálculo)
    h = min(matriz_a.shape[0], matriz_b.shape[0])
    w = min(matriz_a.shape[1], matriz_b.shape[1])
    
    # Redimensiona para garantir tamanhos iguais
    img_a = matriz_a[:h, :w].astype(np.float32)
    img_b = matriz_b[:h, :w].astype(np.float32)
    
    # Se t for 0 ou 1, retorna direto para poupar processamento
    if t <= 0: return img_a
    if t >= 1: return img_b

    # PASSO 1 e 2: Definição de pontos de vértice v e w
    # Aqui usamos 5 pontos (4 cantos + centro) para criar a estrutura básica.
    # v representa os pontos na imagem inicial (criança) e w na final (adulto).
    v = np.array([[0,0], [w-1,0], [w-1,h-1], [0,h-1], [w//2, h//2]], dtype=np.float32)
    w_pts = np.array([[0,0], [w-1,0], [w-1,h-1], [0,h-1], [w//2, h//2]], dtype=np.float32)
    
    # PASSO 3 e 5: Triangulação
    # Dividimos o retângulo em 4 triângulos conectando os cantos ao centro.
    # No livro (Fig. 10.20.6), são usados 179 triângulos para maior precisão.
    triangulos = [[0,1,4], [1,2,4], [2,3,4], [3,0,4]]
    
    # PASSO 4: Encontrar pontos de vértice u(t) no instante t
    # Implementa a Equação (9): u_i(t) = (1 - t)v_i + t*w_i
    # Isso move a "geometria" da imagem ao longo do tempo.
    u = (1 - t) * v + t * w_pts
    
    res = np.zeros((h, w), dtype=np.float32)
    
   # Preparação para o processamento vetorizado (Gera grade de pixels x, y)
    grid_x, grid_y = np.meshgrid(np.arange(w), np.arange(h))
    coords = np.stack([grid_x, grid_y, np.ones_like(grid_x)], axis=-1)

    for tri in triangulos:
        ut = u[tri] # Vértices do triângulo deformado no tempo t
        vt = v[tri] # Vértices na imagem A
        wt = w_pts[tri] # Vértices na imagem B

        # PASSO 6 e 7: Combinação Convexa e Coordenadas Baricêntricas
        # Invertemos a matriz de vértices para encontrar os coeficientes c1, c2, c3.
        # Isso resolve as Equações (10) e (11) para todos os pixels do triângulo de uma vez.
        M_inv = np.linalg.inv(np.array([
            [ut[0,0], ut[1,0], ut[2,0]],
            [ut[0,1], ut[1,1], ut[2,1]],
            [1, 1, 1]
        ]))

        # Calcula os pesos baricêntricos para TODOS os pixels de uma vez
        pesos = coords @ M_inv.T
        
        # Filtra apenas os pixels que pertencem ao triângulo atual (c1, c2, c3 >= 0)
        mask = np.all(pesos >= -0.001, axis=-1)

        if np.any(mask):
            p = pesos[mask] # Pesos apenas dos pixels dentro do triângulo
            
            # PASSO 8: Determinar a localização do ponto u nas imagens original e final
            # Equação (12): v = c1*v_i + c2*v_j + c3*v_k (Posição de onde tirar a cor em A)
            src_a_x = (p[:,0]*vt[0,0] + p[:,1]*vt[1,0] + p[:,2]*vt[2,0]).astype(int).clip(0, w-1)
            src_a_y = (p[:,0]*vt[0,1] + p[:,1]*vt[1,1] + p[:,2]*vt[2,1]).astype(int).clip(0, h-1)

            # Equação (13): w = c1*w_i + c2*w_j + c3*w_k (Posição de onde tirar a cor em B)
            src_b_x = (p[:,0]*wt[0,0] + p[:,1]*wt[1,0] + p[:,2]*wt[2,0]).astype(int).clip(0, w-1)
            src_b_y = (p[:,0]*wt[0,1] + p[:,1]*wt[1,1] + p[:,2]*wt[2,1]).astype(int).clip(0, h-1)

            # PASSO 9: Média ponderada dos níveis de cinza (Densidade de Imagem)
            # Implementa a Equação (14): p_t(u) = (1 - t)p_0(v) + t*p_1(w)
            # Esta é a mistura final das cores após a deformação geométrica.
            res[mask] = (1 - t) * img_a[src_a_y, src_a_x] + t * img_b[src_b_y, src_b_x]

    return res.astype(np.float32)