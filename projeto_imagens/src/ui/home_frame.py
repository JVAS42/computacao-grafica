import customtkinter as ctk

class HomeFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        self.label = ctk.CTkLabel(self, text="Projeto de Processamento de Imagem 2026.1",
                                  font=ctk.CTkFont(size=30, weight="bold"),
                                  text_color="#213555")
        self.label.pack(pady=20)

        info_text = ("Professor: Robson Pequeno de Sousa"
                     "\nIntegrantes:\n"
                     "\n• Denis William Muniz de Sousa"
                     "\n• Flavia Vitoria Goncalves de Queiroz"
                     "\n• Joao Victor de Araujo Silva"
                     "\n• Raquel Melo de Queiroz")

        self.info_label = ctk.CTkLabel(self, text=info_text, justify="left",text_color="#213555", font=ctk.CTkFont(size=16))
        self.info_label.pack(pady=10)