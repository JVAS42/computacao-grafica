# algoritmos/coordenadas.py

def inp_to_ndc(x, y, width, height):
    """Converte coordenadas de pixel para NDC"""
    return {
        "ndcx": x / (width - 1) if width > 1 else 0,
        "ndcy": y / (height - 1) if height > 1 else 0
    }

def ndc_to_wd(ndcx, ndcy, xmax, xmin, ymax, ymin):
    """Converte coordenadas NDC para coordenadas do mundo"""
    return {
        "worldX": ndcx * (xmax - xmin) + xmin,
        "worldY": ndcy * (ymax - ymin) + ymin
    }

def wd_to_ndc_central(x, y, xmax, xmin, ymax, ymin):
    """Converte coordenadas do mundo para NDC centralizada na origem"""
    return {
        "ndccx": 2 * ((x - xmin) / (xmax - xmin)) - 1 if xmax != xmin else 0,
        "ndccy": 2 * ((y - ymin) / (ymax - ymin)) - 1 if ymax != ymin else 0
    }

def ndc_central_to_dc(ndcx, ndcy, width, height):
    """Converte NDC centralizada para coordenadas de dispositivo (pixel)"""
    return {
        "dcx": round(ndcx * (width - 1)),
        "dcy": round((1 - ndcy) * (height - 1)) # Y invertido
    }
