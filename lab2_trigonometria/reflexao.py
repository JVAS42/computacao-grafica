def reflexao(eixo="x"):
    eixo = eixo.lower()
    if eixo == "x":
        return [[1, 0, 0], [0, -1, 0], [0, 0, 1]]
    elif eixo == "y":
        return [[-1, 0, 0], [0, 1, 0], [0, 0, 1]]
    elif eixo == "origem":
        return [[-1, 0, 0], [0, -1, 0], [0, 0, 1]]
