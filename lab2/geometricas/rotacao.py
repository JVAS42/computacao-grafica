import math

def rotacao(theta_graus):
    theta = math.radians(theta_graus)
    cos_t = math.cos(theta)
    sin_t = math.sin(theta)
    return [[cos_t, -sin_t, 0], [sin_t, cos_t, 0], [0, 0, 1]]
