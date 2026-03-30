import os
from PIL import Image

def converter_para_pgm_p2(caminho_entrada, caminho_saida):
    try:
        # Abre a imagem e converte para escala de cinza
        img = Image.open(caminho_entrada).convert("L")
        
        largura, altura = img.size
        pixels = list(img.getdata())
        
        # Salva no formato PGM ASCII (P2)
        with open(caminho_saida, "w") as f:
            f.write("P2\n")
            f.write(f"{largura} {altura}\n")
            f.write("255\n")
            
            # Escreve os pixels em blocos de linhas para melhor organização no arquivo
            for i in range(altura):
                linha = pixels[i * largura : (i + 1) * largura]
                f.write(" ".join(map(str, linha)) + "\n")
        
        print(f"Sucesso: {os.path.basename(caminho_entrada)} -> {os.path.basename(caminho_saida)}")
    
    except Exception as e:
        print(f"Erro ao processar {caminho_entrada}: {e}")

if __name__ == "__main__":
    # Define o caminho para a pasta assets
    # Como o script está em src/algoritmos, voltamos dois níveis (../../assets)
    diretorio_atual = os.path.dirname(os.path.abspath(__file__))
    pasta_assets = os.path.join(diretorio_atual, "..", "..", "assets")

    # Lista das imagens que você quer converter
    imagens_para_converter = []

    print("Iniciando conversão...")

    for nome_arquivo in imagens_para_converter:
        caminho_full_entrada = os.path.join(pasta_assets, nome_arquivo)
        
        # Verifica se o arquivo original existe antes de tentar converter
        if os.path.exists(caminho_full_entrada):
            # Define o nome de saída trocando .jpg por .pgm
            nome_saida = nome_arquivo.replace(".jpg", ".pgm")
            caminho_full_saida = os.path.join(pasta_assets, nome_saida)
            
            converter_para_pgm_p2(caminho_full_entrada, caminho_full_saida)
        else:
            print(f"Aviso: Arquivo não encontrado: {nome_arquivo}")

    print("Processo finalizado!")