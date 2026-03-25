from src.ui.main_window import MainWindow
import customtkinter as ctk

if __name__ == "__main__":
    # Define o tema (Escuro ou Claro)
    ctk.set_appearance_mode("dark")
    ctk.set_default_color_theme("blue")

    app = MainWindow()
    app.mainloop()