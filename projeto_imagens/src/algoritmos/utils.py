import numpy as np
from PIL import Image
import customtkinter as ctk


def carregar_imagem_pgm(caminho):
    """Lê um arquivo PGM e retorna uma matriz NumPy."""
    # O Pillow (PIL) lê PGM nativamente
    img = Image.open(caminho).convert('L')  # 'L' garante que é em tons de cinza (Grayscale)
    matriz = np.array(img, dtype=np.float32)
    return matriz


def matriz_para_imagem(matriz):
    """Converte uma matriz NumPy de volta para um formato exibível na tela."""
    # Garante que os valores estão entre 0 e 255 e converte para inteiros de 8 bits
    matriz_clipada = np.clip(matriz, 0, 255).astype(np.uint8)
    img_pil = Image.fromarray(matriz_clipada, mode='L')

    # O CustomTkinter precisa do objeto CTkImage para mostrar na tela
    # O size=(256, 256) mantém o tamanho que usávamos no P5.js
    return ctk.CTkImage(light_image=img_pil, dark_image=img_pil, size=(256, 256))


def normalizar_matriz(matriz):
    """Espelha a função normalize() do seu JS. Mapeia os valores para 0-255."""
    min_val = np.min(matriz)
    max_val = np.max(matriz)

    if max_val - min_val == 0:
        return np.zeros_like(matriz)

    range_val = 255.0 / (max_val - min_val)
    matriz_norm = np.round(range_val * (matriz - min_val))

    # --- NOVO PRINT PARA VERIFICAÇÃO ---
    print("\n--- Processo de Normalização ---")
    print(f"Min: {min_val} | Max: {max_val} | Fator (Range): {range_val:.4f}")
    print("Bruto (5px):", matriz[0, :5])
    print("Normalizado (5px):", matriz_norm[0, :5])
    print("--------------------------------")

    return matriz_norm


def gerar_texto_tabela(matriz, is_processada=False):
    """Gera o texto com TODOS os números da matriz para as caixas de texto, separando por tabulação."""
    linhas = []
    for linha in matriz:
        linha_str = []
        for val in linha:
            # Se for a matriz processada e tiver casas decimais, mostra o float completo
            if is_processada and not float(val).is_integer():
                linha_str.append(str(val))
            else:
                # Se for inteiro (ou imagem original), mostra como int
                linha_str.append(str(int(val)))

        # Junta os números da linha usando Tab (\t) para simular as colunas da tabela web
        linhas.append("\t".join(linha_str))

    # Junta todas as linhas com quebra de linha
    return "\n".join(linhas)