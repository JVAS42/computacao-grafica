package main.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeilerAtherton {

    public static class PontoWA {
        public double x, y;
        public PontoWA(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Classe interna para representar os vértices no grafo de listas encadeadas
    private static class No {
        double x, y;
        boolean isIntersecao;
        boolean isEntrando;
        boolean visitado;
        double alpha; // Armazena a distância relativa para ordenação na aresta
        No proximo;
        No anterior;
        No correspondente; // Liga a interseção na lista do Sujeito à da lista do Clip

        No(double x, double y, boolean isIntersecao) {
            this.x = x;
            this.y = y;
            this.isIntersecao = isIntersecao;
            this.visitado = false;
        }
    }

    public static List<List<PontoWA>> clipPolygon(List<PontoWA> subjectPolyOriginal, double xMin, double xMax, double yMin, double yMax) {
        List<List<PontoWA>> resultado = new ArrayList<>();
        if (subjectPolyOriginal.size() < 3) return resultado;

        // Cópia para forçar o sentido horário, essencial para a matemática do Weiler-Atherton
        List<PontoWA> subjectPoly = new ArrayList<>(subjectPolyOriginal);
        forcarSentidoHorario(subjectPoly);

        // 1. Inicializar listas Sujeito (Polígono desenhado) e Clip (Janela)
        List<No> listaSujeito = new ArrayList<>();
        for (PontoWA p : subjectPoly) listaSujeito.add(new No(p.x, p.y, false));

        List<No> listaClip = new ArrayList<>();
        // Janela em sentido horário (Canto Superior-Esq -> Superior-Dir -> Inferior-Dir -> Inferior-Esq)
        listaClip.add(new No(xMin, yMin, false));
        listaClip.add(new No(xMax, yMin, false));
        listaClip.add(new No(xMax, yMax, false));
        listaClip.add(new No(xMin, yMax, false));

        ligarListaCircular(listaSujeito);
        ligarListaCircular(listaClip);

        // 2. Encontrar Interseções e preencher o grafo
        boolean temIntersecoes = false;

        No noS = listaSujeito.get(0);
        do {
            No proximoS = noS.proximo;
            if (noS.isIntersecao) { noS = noS.proximo; continue; }

            List<No> intersecoesS = new ArrayList<>();

            No noC = listaClip.get(0);
            do {
                No proximoC = noC.proximo;
                if (noC.isIntersecao) { noC = noC.proximo; continue; }

                // Calcula matematicamente o cruzamento entre a aresta do Sujeito e a da Janela
                double[] inter = calcularIntersecao(noS.x, noS.y, proximoS.x, proximoS.y,
                        noC.x, noC.y, proximoC.x, proximoC.y);

                if (inter != null) {
                    temIntersecoes = true;
                    No isNo = new No(inter[0], inter[1], true);
                    isNo.alpha = inter[2]; // Posição na linha do sujeito

                    No icNo = new No(inter[0], inter[1], true);
                    icNo.alpha = inter[3]; // Posição na linha da janela

                    // Cria a ponte (link) entre as duas listas
                    isNo.correspondente = icNo;
                    icNo.correspondente = isNo;

                    // Verifica se a linha do Sujeito está a "Entrar" na janela (testa um ponto logo a seguir à interseção)
                    double midX = inter[0] + (proximoS.x - noS.x) * 0.01;
                    double midY = inter[1] + (proximoS.y - noS.y) * 0.01;
                    isNo.isEntrando = isPontoDentro(midX, midY, xMin, xMax, yMin, yMax);
                    icNo.isEntrando = isNo.isEntrando;

                    intersecoesS.add(isNo);

                    // Insere imediatamente na lista do Clip, ordenando pela distância (alpha)
                    inserirNoClip(noC, proximoC, icNo);
                }
                noC = proximoC;
            } while (noC != listaClip.get(0));

            // Insere as interseções na lista do Sujeito, ordenadas
            if (!intersecoesS.isEmpty()) {
                intersecoesS.sort((n1, n2) -> Double.compare(n1.alpha, n2.alpha));
                No atual = noS;
                for (No n : intersecoesS) {
                    atual.proximo = n;
                    n.anterior = atual;
                    n.proximo = proximoS;
                    proximoS.anterior = n;
                    atual = n;
                }
            }
            noS = proximoS;
        } while (noS != listaSujeito.get(0));

        // 3. Casos em que as formas não se cruzam (Totalmente dentro ou fora)
        if (!temIntersecoes) {
            if (isPontoDentro(listaSujeito.get(0).x, listaSujeito.get(0).y, xMin, xMax, yMin, yMax)) {
                resultado.add(subjectPolyOriginal); // O polígono desenhado está todo lá dentro
            } else if (isPoligonoDentro(listaClip, subjectPolyOriginal)) {
                // A janela é que está toda engolida pelo polígono desenhado
                List<PontoWA> clipPoly = new ArrayList<>();
                clipPoly.add(new PontoWA(xMin, yMin)); clipPoly.add(new PontoWA(xMax, yMin));
                clipPoly.add(new PontoWA(xMax, yMax)); clipPoly.add(new PontoWA(xMin, yMax));
                resultado.add(clipPoly);
            }
            return resultado; // Retorna sem precisar colher nós
        }

        // 4. Rastreamento (Harvesting) para criar os novos polígonos recortados
        No inicio = listaSujeito.get(0);
        No atual = inicio;
        do {
            if (atual.isIntersecao && atual.isEntrando && !atual.visitado) {
                List<PontoWA> polyRecortado = new ArrayList<>();
                No rastreador = atual;
                boolean noSujeito = true;

                int limitadorLoops = 0; // Proteção estrita contra ciclos matemáticos infinitos
                do {
                    rastreador.visitado = true;
                    if (rastreador.correspondente != null) rastreador.correspondente.visitado = true;

                    polyRecortado.add(new PontoWA(rastreador.x, rastreador.y));

                    if (noSujeito) {
                        // Percorre a lista do Sujeito até encontrar a próxima interseção (que será de saída)
                        rastreador = rastreador.proximo;
                        while (!rastreador.isIntersecao) {
                            polyRecortado.add(new PontoWA(rastreador.x, rastreador.y));
                            rastreador = rastreador.proximo;
                        }
                        noSujeito = false; // Vai mudar para a janela
                        rastreador = rastreador.correspondente; // Pula a ponte para a lista do Clip
                    } else {
                        // Percorre a lista do Clip (Janela) até encontrar a próxima interseção (de entrada)
                        rastreador = rastreador.proximo;
                        while (!rastreador.isIntersecao) {
                            polyRecortado.add(new PontoWA(rastreador.x, rastreador.y));
                            rastreador = rastreador.proximo;
                        }
                        noSujeito = true; // Volta para o polígono do utilizador
                        rastreador = rastreador.correspondente;
                    }
                    limitadorLoops++;
                } while (rastreador != atual && rastreador.correspondente != atual && limitadorLoops < 500);

                resultado.add(polyRecortado);
            }
            atual = atual.proximo;
        } while (atual != inicio);

        return resultado;
    }

    // --- FUNÇÕES AUXILIARES MATEMÁTICAS ---

    private static void forcarSentidoHorario(List<PontoWA> poly) {
        double soma = 0;
        for (int i = 0; i < poly.size(); i++) {
            PontoWA p1 = poly.get(i);
            PontoWA p2 = poly.get((i + 1) % poly.size());
            soma += (p2.x - p1.x) * (p2.y + p1.y);
        }
        // No Java Swing (Eixo Y cresce para baixo), o sentido horário puro gera uma soma negativa.
        if (soma > 0) {
            Collections.reverse(poly);
        }
    }

    private static void ligarListaCircular(List<No> lista) {
        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).proximo = lista.get((i + 1) % lista.size());
            lista.get(i).anterior = lista.get((i - 1 + lista.size()) % lista.size());
        }
    }

    private static void inserirNoClip(No inicio, No fim, No novoNo) {
        No atual = inicio;
        while (atual.proximo != fim && atual.proximo.alpha < novoNo.alpha) {
            atual = atual.proximo;
        }
        novoNo.proximo = atual.proximo;
        atual.proximo.anterior = novoNo;
        atual.proximo = novoNo;
        novoNo.anterior = atual;
    }

    private static double[] calcularIntersecao(double x1, double y1, double x2, double y2,
                                               double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0) return null; // Linhas paralelas não se cruzam

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        // Tolerância (0.0001) evita que vértices exatos gerem duplas interseções no código
        if (ua > 0.0001 && ua < 0.9999 && ub > 0.0001 && ub < 0.9999) {
            double x = x1 + ua * (x2 - x1);
            double y = y1 + ua * (y2 - y1);
            return new double[]{x, y, ua, ub};
        }
        return null;
    }

    private static boolean isPontoDentro(double x, double y, double xMin, double xMax, double yMin, double yMax) {
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax;
    }

    // Ray casting (Lançamento de raios) para verificar se o polígono da janela está engolido
    private static boolean isPoligonoDentro(List<No> clipPol, List<PontoWA> subjPoly) {
        double testX = clipPol.get(0).x;
        double testY = clipPol.get(0).y;
        boolean dentro = false;
        for (int i = 0, j = subjPoly.size() - 1; i < subjPoly.size(); j = i++) {
            double xi = subjPoly.get(i).x, yi = subjPoly.get(i).y;
            double xj = subjPoly.get(j).x, yj = subjPoly.get(j).y;
            if (((yi > testY) != (yj > testY)) && (testX < (xj - xi) * (testY - yi) / (yj - yi) + xi)) {
                dentro = !dentro;
            }
        }
        return dentro;
    }
}