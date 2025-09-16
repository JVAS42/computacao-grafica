def aplicar_sequencia(sequencia, matriz_identidade=[[1,0,0],[0,1,0],[0,0,1]]):
    resultado = matriz_identidade
    for passo in sequencia:
        resultado = multiplicar_matrizes(passo['matriz'], resultado)
    return resultado

def multiplicar_matrizes(A, B):
    resultado = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
    for i in range(3):
        for j in range(3):
            resultado[i][j] = sum(A[i][k] * B[k][j] for k in range(3))
    return resultado