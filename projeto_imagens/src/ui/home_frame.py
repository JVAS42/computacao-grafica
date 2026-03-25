import customtkinter as ctk

class HomeFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        self.label = ctk.CTkLabel(self, text="Projeto de Processamento de Imagem 2026.1",
                                 font=ctk.CTkFont(size=24, weight="bold"))
        self.label.pack(pady=20)

        info_text = "Professor: Robson Pequeno de Sousa\nIntegrantes: ➜ Denis, ➜ Flávia, ➜ João, ➜ Raquel"
        self.info_label = ctk.CTkLabel(self, text=info_text, justify="left")
        self.info_label.pack(pady=10)