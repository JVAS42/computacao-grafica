import customtkinter as ctk



class FilterFrame(ctk.CTkFrame):
    def __init__(self, master):
        super().__init__(master, fg_color="transparent")

        self.label = ctk.CTkLabel(self, text="Painel de Filtros", font=("Arial", 20))
        self.label.pack(pady=20)

        # Botão que chama a lógica
        self.btn_executar = ctk.CTkButton(self, text="Aplicar Cinza", command=self.executar_filtro)
        self.btn_executar.pack(pady=10)

    def executar_filtro(self):
        # Aqui você chamaria a função que está na pasta de algoritmos
        print("Filtro solicitado!")