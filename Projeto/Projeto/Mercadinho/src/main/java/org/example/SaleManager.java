package org.example;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A classe mestre do caixa: gerencia os produtos carregados e controla a venda que tá aberta.
 *
 * @see #sell(String, String)
 * @see #finalizeSale()
 */
public class SaleManager {
    public static List<Product> products = ProductManager.loadProducts();
    public static Sale currentSale = null;

    /**
     * Cadastra um produto novo na nossa lista ou atualiza se o código de barras já tiver lá.
     *
     * @param barCode O código do produto.
     * @param name O nome pra tela.
     * @param price O preço em reais.
     * @param image A imagem do produto.
     * @return Não retorna nada.
     * @see ProductManager#saveProducts(List)
     * @exception RuntimeException Caso dê pau na hora de mandar salvar no arquivo.
     */
    public static void setProduct(String barCode, String name, double price, String image) {
        Product product = getProduct(barCode);
        if (product == null) {
            Product novo = new Product(barCode, name, price, image);
            products.add(novo);
        } else {
            products.remove(product);
            products.add(product.update(name, price, image));
        }
        ProductManager.saveProducts(products);
    }

    /**
     * Roda a lista de produtos pra achar um específico usando o código de barras.
     *
     * @param barCode O código que queremos achar.
     * @return O produto que ele encontrou ou nulo se não achar nada.
     * @see #setProduct(String, String, double, String)
     * @exception NullPointerException Se passarem um código completamente nulo na busca.
     */
    public static Product getProduct(String barCode) {
        for (Product product : products) {
            if (Objects.equals(product.barCode(), barCode)) return product;
        }
        return null;
    }

    /**
     * Tenta abrir o caixa para uma nova venda checando se o CPF digitado presta.
     *
     * @param cpf O CPF do cliente.
     * @param paymentType Se é pix, dinheiro, etc.
     * @return Retorna true se abriu a venda de boa, ou false se o CPF for barrado.
     * @see CPF#validate(String)
     * @exception RuntimeException Se travar na hora de criar o ID da venda.
     */
    public static boolean sell(String cpf, String paymentType) {
        boolean valid = CPF.validate(cpf);
        if (valid) {
            String ID = UUID.randomUUID().toString().substring(0, 5);
            currentSale = new Sale(ID, cpf, paymentType);
        }
        return valid;
    }

    /**
     * Bipa o produto e joga ele na venda que tá rolando agora.
     *
     * @param barCode O código que acabou de ser digitado ou lido pelo leitor.
     * @return Retorna o produto que foi pro carrinho, ou nulo se ele não existir.
     * @see Sale#sell(Item)
     * @exception Exception Se a venda for cancelada misteriosamente no meio da operação.
     */
    public static Product addProduct(String barCode) {
        Product product = getProduct(barCode);
        if (product != null && currentSale != null) {
            Item item = new Item(product, 1);
            currentSale.sell(item);
            return product;
        }
        return null;
    }

    /**
     * Bate o martelo na venda, gera a nota fiscal, salva tudo no histórico de texto e esvazia o caixa.
     *
     * @return O texto da nota fiscal gerada pra tela, ou nulo se não tinha venda pra fechar.
     * @see ProductManager#saveSale(Sale)
     * @exception RuntimeException Caso o arquivo não deixe salvar a venda final.
     */
    public static String finalizeSale() {
        if (currentSale == null) return null;
        String nota = currentSale.gerarNotaFiscal();
        ProductManager.saveSale(currentSale);
        currentSale = null;
        return nota;
    }
}