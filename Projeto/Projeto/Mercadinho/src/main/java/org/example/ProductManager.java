package org.example;

import lanta.utils.LantaLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelo gerenciamento da persistência de dados em arquivos de texto.
 * Controla as operações de leitura e escrita para produtos e histórico de vendas.
 */
public final class ProductManager {
    private static final LantaLogger logger = new LantaLogger(ProductManager.class);
    private static FileWriter fw;
    private static FileReader fr;
    private static BufferedWriter writer;
    private static BufferedReader reader;
    private static final File products = new File("products.txt");
    private static final File sales = new File("sales.txt");

    /**
     * Sobrescreve o arquivo "products.txt" com a lista atualizada de produtos.
     *
     * @param productList A lista de objetos {@link Product} a serem salvos.
     * @see #loadProducts()
     */
    public static void saveProducts(List<Product> productList) {
        try {
            fw = new FileWriter(products, false);
            writer = new BufferedWriter(fw);
            for (Product product : productList) {
                writer.write(product.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            logger.logCatch(new RuntimeException(e));
        } finally {
            closeWriter();
        }
    }

    /**
     * Adiciona uma nova venda ao final do arquivo "sales.txt".
     *
     * @param sale O objeto {@link Sale} contendo os detalhes da venda a ser registrada.
     * @see #loadSales()
     */
    public static void saveSale(Sale sale) {
        try {
            fw = new FileWriter(sales, true);
            writer = new BufferedWriter(fw);
            writer.write(sale.toString());
            writer.newLine();
        } catch (IOException e) {
            logger.logCatch(new RuntimeException(e));
        } finally {
            closeWriter();
        }
    }

    /**
     * Carrega todos os produtos armazenados no arquivo "products.txt".
     *
     * @return Uma lista de objetos {@link Product} lidos do arquivo. Retorna uma lista vazia caso o arquivo não exista ou esteja vazio.
     * @see #saveProducts(List)
     */
    public static List<Product> loadProducts() {
        List<Product> productList = new ArrayList<>();
        if (!products.exists()) return productList;
        try {
            fr = new FileReader(products);
            reader = new BufferedReader(fr);
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Product p = Product.parse(line);
                if (p != null) productList.add(p);
            }
        } catch (IOException e) {
            logger.logCatch(new RuntimeException(e));
        } finally {
            closeReader();
        }
        return productList;
    }

    /**
     * Carrega todas as linhas de venda armazenadas no arquivo "sales.txt".
     *
     * @return Uma lista de {@link String} contendo os dados brutos das vendas.
     * @see #saveSale(Sale)
     */
    public static List<String> loadSales() {
        List<String> salesList = new ArrayList<>();
        if (!sales.exists()) return salesList;
        try {
            fr = new FileReader(sales);
            reader = new BufferedReader(fr);
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                salesList.add(line);
            }
        } catch (IOException e) {
            logger.logCatch(new RuntimeException(e));
        } finally {
            closeReader();
        }
        return salesList;
    }

    /**
     * Fecha os recursos de escrita ({@link BufferedWriter} e {@link FileWriter}) de forma segura.
     */
    private static void closeWriter() {
        if (writer != null) {
            try { writer.close(); } catch (IOException e) { logger.logCatch(new RuntimeException(e)); }
            finally { writer = null; }
        }
        if (fw != null) {
            try { fw.close(); } catch (IOException e) { logger.logCatch(new RuntimeException(e)); }
            finally { fw = null; }
        }
    }

    /**
     * Fecha os recursos de leitura ({@link BufferedReader} e {@link FileReader}) de forma segura.
     */
    private static void closeReader() {
        if (reader != null) {
            try { reader.close(); } catch (IOException e) { logger.logCatch(new RuntimeException(e)); }
            finally { reader = null; }
        }
        if (fr != null) {
            try { fr.close(); } catch (IOException e) { logger.logCatch(new RuntimeException(e)); }
            finally { fr = null; }
        }
    }
}