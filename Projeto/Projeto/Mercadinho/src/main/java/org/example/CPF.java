package org.example;

import lanta.utils.LantaLogger;

/**
 * Classe utilitária para validação de Cadastro de Pessoas Físicas (CPF).
 * <p>
 * Fornece algoritmos estáticos baseados em cálculos matemáticos oficiais 
 * para verificar a integridade estrutural e de dígitos verificadores de um CPF.
 * </p>
 *
 */
public final class CPF {
    private static final LantaLogger logger = new LantaLogger(CPF.class);

    /**
     * Valida se a String informada representa um CPF estruturalmente correto.
     * <p>
     * O método remove caracteres especiais, valida se o tamanho resultante 
     * possui exatamente 11 dígitos e descarta sequências de números idênticos.
     * Na sequência, realiza o cálculo aritmético dos dígitos verificadores.
     * </p>
     *
     * @param CPF A string contendo o CPF a ser validado (aceita máscaras com pontos e hífen).
     * @return {@code true} se o CPF for válido segundo o algoritmo oficial da Receita;
     * {@code false} caso contrário ou se a entrada for nula/com tamanho incorreto.
     */
    public static boolean validate(String CPF) {
        if (CPF == null) return false;
        String finalCPF = CPF.replaceAll("[^0-9]", "");
        if ((finalCPF.length() != 11) || finalCPF.chars().allMatch(c -> c == finalCPF.charAt(0))) return false;

        try {
            int digitoVerificador1 = getDigitoVerificador1(finalCPF);
            int digitoVerificador2 = getDigitoVerificador2(finalCPF);

            int d1 = (finalCPF.charAt(9) - '0');
            int d2 = (finalCPF.charAt(10) - '0');

            return digitoVerificador1 == d1 && digitoVerificador2 == d2;

        } catch (Exception e) {
            logger.logCatch(new RuntimeException(e));
            return false;
        }
    }

    /**
     * Calcula o primeiro dígito verificador do CPF.
     * * @param CPF O CPF limpo (apenas números).
     * @return O valor inteiro correspondente ao primeiro dígito verificador.
     */
    private static int getDigitoVerificador1(String CPF) {
        int pesoVerificador1 = 10;
        int somaTotal1 = 0;
        for (int i = 0; i < 9; i++) {
            int num = CPF.charAt(i) - '0';
            somaTotal1 = somaTotal1 + (num * pesoVerificador1);
            pesoVerificador1--;
        }

        int restoCalculado1 = 11 - (somaTotal1 % 11);
        return (restoCalculado1 == 10 || restoCalculado1 == 11) ? 0 : restoCalculado1;
    }

    /**
     * Calcula o segundo dígito verificador do CPF.
     * * @param CPF O CPF limpo (apenas números).
     * @return O valor inteiro correspondente ao segundo dígito verificador.
     */
    private static int getDigitoVerificador2(String CPF) {
        int pesoVerificador2 = 11;
        int somaTotal2 = 0;
        for (int i = 0; i < 10; i++) {
            int num = (CPF.charAt(i) - '0');
            somaTotal2 = somaTotal2 + (num * pesoVerificador2);
            pesoVerificador2--;
        }

        int restoCalculado2 = 11 - (somaTotal2 % 11);
        return (restoCalculado2 == 10 || restoCalculado2 == 11) ? 0 : restoCalculado2;
    }
}