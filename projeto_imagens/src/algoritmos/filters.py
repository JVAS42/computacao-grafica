import cv2

def aplicar_grayscale(imagem_path):
    # Lógica pura de processamento de imagem
    img = cv2.imread(imagem_path)
    cinza = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    return cinza