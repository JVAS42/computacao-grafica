import pygame as pg
from pygame.locals import *
from OpenGL.GL import *
from OpenGL.GLU import *

# Para o caso I - [0,1]
def user_to_ndc_casoI(x, y, x_min, x_max, y_min, y_max):
    ndc_x = (x - x_min) / (x_max - x_min)
    ndc_y = (y - y_min) / (y_max - y_min)
    return ndc_x, ndc_y

def ndc_to_dc_casoI(ndc_x, ndc_y, ndh, ndv):
    dc_x = round(ndc_x * (ndh - 1))
    dc_y = round(ndc_y * (ndv - 1))
    return dc_x, dc_y

# Para o caso II - [-1,1]
def user_to_ndc_casoII(x, y, x_min, x_max, y_min, y_max):
    ndc_x = -1 + (x - x_min) / (x_max - x_min) * 2
    ndc_y = -1 + (y - y_min) / (y_max - y_min) * 2
    return ndc_x, ndc_y

def ndc_to_dc_casoII(ndc_x, ndc_y, ndh, ndv):
    dc_x = round(((ndc_x + 1) / 2) * (ndh - 1))
    dc_y = round(((ndc_y + 1) / 2) * (ndv - 1))
    return dc_x, dc_y

# Função para desenhar ponto com OpenGL
def setPixel(dc_x, dc_y, color=(1.0, 1.0, 1.0), size=5):
    glPointSize(size)
    glBegin(GL_POINTS)
    glColor3f(*color)
    glVertex2f(dc_x, dc_y)
    glEnd()

# Entrada do usuário
print("Digite o intervalo do mundo (coordenadas do usuário):")
x_min = float(input("x mínimo: "))
x_max = float(input("x máximo: "))
y_min = float(input("y mínimo: "))
y_max = float(input("y máximo: "))

# Pegar tamanho da tela automaticamente
pg.init()
info = pg.display.Info()
ndh = info.current_w
ndv = info.current_h
print(f"Tamanho da tela detectado: {ndh}x{ndv} pixels")

# Coordenadas X e Y
x_ponto = float(input("\nDigite a coordenada X do ponto: "))
y_ponto = float(input("Digite a coordenada Y do ponto: "))

# Caso I [0,1]
ndcx, ndcy = user_to_ndc_casoI(x_ponto, y_ponto, x_min, x_max, y_min, y_max)
pixel_x, pixel_y = ndc_to_dc_casoI(ndcx, ndcy, ndh, ndv)

print(f"\n-> Usando [0, 1]:")
print(f"   Coordenadas NDC: ({ndcx}, {ndcy})")
print(f"   Coordenadas DC (Pixel Final): ({pixel_x}, {pixel_y})")

# Caso II [-1,1]
ndcx_II, ndcy_II = user_to_ndc_casoII(x_ponto, y_ponto, x_min, x_max, y_min, y_max)
pixel_x_II, pixel_y_II = ndc_to_dc_casoII(ndcx_II, ndcy_II, ndh, ndv)

print(f"\n-> Usando NDC [-1, 1]:")
print(f"   Coordenadas NDC: ({ndcx_II}, {ndcy_II})")
print(f"   Coordenadas DC (Pixel Final): ({pixel_x_II}, {pixel_y_II})")
print("="*40)

# JANELA CASO I 
tela1 = pg.display.set_mode((ndh, ndv), DOUBLEBUF | OPENGL)
pg.display.set_caption("Caso I [0,1]")

glMatrixMode(GL_PROJECTION)
glLoadIdentity()
glOrtho(0, ndh, 0, ndv, -1, 1)
glMatrixMode(GL_MODELVIEW)

rodando = True
while rodando:
    for event in pg.event.get():
        if event.type == QUIT:
            rodando = False

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glLoadIdentity()
    setPixel(pixel_x, pixel_y, color=(1.0, 0.0, 0.0), size=10)

    pg.display.flip()

pg.quit()

#JANELA CASO II
pg.init()
tela2 = pg.display.set_mode((ndh, ndv), DOUBLEBUF | OPENGL)
pg.display.set_caption("Caso II [-1,1]")

glMatrixMode(GL_PROJECTION)
glLoadIdentity()
glOrtho(0, ndh, 0, ndv, -1, 1)
glMatrixMode(GL_MODELVIEW)

rodando = True
while rodando:
    for event in pg.event.get():
        if event.type == QUIT:
            rodando = False

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glLoadIdentity()
    setPixel(pixel_x_II, pixel_y_II, color=(0.0, 0.0, 1.0), size=10)

    pg.display.flip()

pg.quit()