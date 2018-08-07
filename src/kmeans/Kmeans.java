/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.Buffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Matheus
 */
public class Kmeans {

    public static final int N_CASAS = 4;
    public static final int N_PROTOTIPOS = 15;
    public static double funcao = 0;

    public static void mostrarMatriz(Double[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    public static int tamLinhaTxt(Scanner in) {
        int count = 0;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String vetCodigoFonte[] = line.split(" ");
            count++;
        }
        return count;
    }

    public static List vetorCodFont(String[] listCodFont) {
        List<Double> cont = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < listCodFont.length; i++) {
            if (!listCodFont[i].isEmpty()) {
                cont.add(Double.parseDouble(listCodFont[i]));
            }
        }
        return cont;
    }

    public static Double[][] normalizacao(Double[][] matriz) {
        double[] vetorMatriz = new double[matriz.length];
        Double[][] matrizAux = new Double[matriz.length][matriz[0].length];

        for (int j = 0; j < matriz[0].length; j++) {
            for (int i = 0; i < matriz.length; i++) {
                vetorMatriz[i] = matriz[i][j];
            }
            for (int i = 0; i < matriz.length; i++) {
                matrizAux[i][j] = truncar((vetorMatriz[i] - min(vetorMatriz)) / (max(vetorMatriz) - min(vetorMatriz)), N_CASAS);
            }
        }
        return matrizAux;
    }

    public static double max(double[] vetor) {
        double x = 0;
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] > x) {
                x = vetor[i];
            }
        }
        return x;
    }

    public static double truncar(double d, int casas_decimais) {
        int var1 = (int) d;   // Remove a parte decimal do número... 2.3777 fica 2
        double var2 = var1 * Math.pow(10, casas_decimais); // adiciona zeros..2.0 fica 200.0
        double var3 = (d - var1) * Math.pow(10, casas_decimais);

        int var4 = (int) var3; // Remove a parte decimal da var3, ficando 37
        int var5 = (int) var2; // Só para não haver erros de precisão: 200.0 passa a 200
        int resultado = var5 + var4; // O resultado será 200+37 = 237
        double resultado_final = resultado / Math.pow(10, casas_decimais); // Finalmente divide-se o resultado pelo número de casas decimais, 237/100 = 2.37
        return resultado_final; // Retorna o resultado_final :P 
    }

    public static double min(double[] vetor) {
        double x = max(vetor);
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] < x) {
                x = vetor[i];
            }
        }
        return x;
    }

    public static int indexMin(double[] vetor) {
        double x = max(vetor);
        int cont = 0;

        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] < x) {
                x = vetor[i];
                cont = i;
            }
        }
        return cont;
    }

    private static int[][] atualizarMatriz(Double[][] x, Double[][] baseDados, int[][] matrizPertinencia) {
        double[] totalDistancia = new double[x.length];

        for (int i = 0; i < baseDados.length; i++) { //para cada objeto
            for (int j = 0; j < x.length; j++) { //para cada prototipo
                totalDistancia[j] = euclidean(criarVetorDaMatriz(baseDados, i), criarVetorDaMatriz(x, j));
            }
            matrizPertinencia[i][indexMin(totalDistancia)] = 1;
        }
        return matrizPertinencia;
    }

    public static double euclidean(double[] x, double[] y) {
        double sum = 0;
        for (int i = 0; i < Math.min(x.length, y.length); i++) {
            sum += Math.pow(x[i] - y[i], 2);
        }

        return Math.sqrt(sum);
    }

    public static double[] criarVetorDaMatriz(Double[][] matriz, int i) {
        double[] vetor = new double[matriz[0].length];

        for (int j = 0; j < matriz[0].length; j++) {
            vetor[j] = matriz[i][j];
        }
        return vetor;
    }

    public static Double[][] atualizacaoCentroides(int[][] matrizPertinencia, Double[][] x, Double[][] baseDados) {

        for (int k = 0; k < matrizPertinencia[0].length; k++) {
            for (int i = 0; i < baseDados.length; i++) {
                for (int j = 0; j < baseDados[0].length; j++) {
                    x[k][j] += baseDados[i][j] * matrizPertinencia[i][k];
                }
            }
            for (int j = 0; j < baseDados[0].length; j++) {
                x[k][j] = truncar((x[k][j] / baseDados.length), N_CASAS);
            }
        }
        return x;
    }

    public static double j(int k, Double[][] bancoDados, int[][] matrizPertinencia, Double[][] x) {
        double aux = 0;

        for (int i = 0; i < bancoDados.length; i++) {
            for (int j = 0; j < k; j++) {
                aux += matrizPertinencia[i][j] * Math.pow(euclidean(criarVetorDaMatriz(bancoDados, i), criarVetorDaMatriz(x, j)), 2);
            }
        }
        return aux;
    }

    public static boolean vericacao(double f, double aux) {
        if ( f - aux > 0.01) {
            return true;
        }
        return false;
    }

    public static void kMeans(Double[][] baseDados, int k) {
        Double[][] x = new Double[k][baseDados[0].length];
        Random r = new Random();
        int[][] matrizPertinencia = new int[baseDados.length][k];

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < baseDados[0].length; j++) {
                x[i][j] = truncar(r.nextDouble(), N_CASAS);
            }
        }

        matrizPertinencia = atualizarMatriz(x, baseDados, matrizPertinencia);

        boolean condicao = true;
        double aux = 0;

        while (condicao) {
            x = atualizacaoCentroides(matrizPertinencia, x, baseDados);
            
            aux = funcao;
            funcao = truncar(j(k, baseDados, matrizPertinencia, x), N_CASAS);
            condicao = vericacao(funcao, aux);
            
            System.out.println("Kmeans: ");
            mostrarMatriz(x);
            System.out.println("");
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {

        Scanner in = new Scanner(new FileReader("Glass.txt"));
        Scanner cod = new Scanner(new FileReader("Glass.txt"));

        int i = 1;
        int tamLinha = tamLinhaTxt(cod);
        List<Double> vetorCodFont = vetorCodFont(in.nextLine().split(" "));
        Double[][] matriz = new Double[tamLinha][vetorCodFont.size()];

        for (int j = 0; j < vetorCodFont.size(); j++) {
            matriz[0][j] = vetorCodFont.get(j);
        }

        while (in.hasNext()) {
            String[] listCodFont = in.nextLine().split(" ");

            vetorCodFont = vetorCodFont(listCodFont);
            for (int j = 0; j < vetorCodFont.size(); j++) {
                matriz[i][j] = vetorCodFont.get(j);
            }
            i++;
        }

        kMeans(normalizacao(matriz), N_PROTOTIPOS);
        System.out.println("Função de Custo: " + funcao);
    }
}
