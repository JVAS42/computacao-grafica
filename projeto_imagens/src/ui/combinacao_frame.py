import customtkinter as ctk

class CombinacaoFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        self.label = ctk.CTkLabel(self, text="Combinação de Imagens", font=("Arial", 20, "bold"), text_color="#213555")
        self.label.pack(pady=20)

        # Botão genérico para você adaptar depois
        self.btn_executar = ctk.CTkButton(self, text="Executar Combinação", command=self.executar_acao)
        self.btn_executar.pack(pady=10)

    def executar_acao(self):
        print("Ação de Combinação solicitada!")