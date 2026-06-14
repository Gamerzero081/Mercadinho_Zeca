package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma transação de venda realizada no sistema.
 * Esta classe gerencia a lista de itens, o cálculo do valor total,
 * a identificação do cliente (CPF) e a geração do cupom fiscal.
 */
public class Sale {
    private final String ID;
    private final String CPF;
    private final String paymentType;
    private final List<Item> Items = new ArrayList<>();
    private double total = 0.0;
    private final LocalDateTime dateTime;

    /**
     * Construtor para inicializar uma nova transação de venda.
     *
     * @param ID          O identificador único da venda.
     * @param CPF         O número do CPF do cliente (validação esperada prévia).
     * @param paymentType O tipo de pagamento utilizado (ex: PIX, Débito).
     */
    public Sale(String ID, String CPF, String paymentType) {
        this.ID = ID;
        this.CPF = CPF;
        this.paymentType = paymentType;
        this.dateTime = LocalDateTime.now();
    }

    /**
     * Adiciona um item à venda ou incrementa a quantidade se o produto já existir no carrinho.
     * Atualiza o valor total da venda automaticamente.
     *
     * @param itemNovo O objeto {@link Item} a ser adicionado à lista de compras.
     * @throws NullPointerException caso o parâmetro {@code itemNovo} seja nulo.
     */
    public void sell(Item itemNovo) {
        for (int i = 0; i < Items.size(); i++) {
            Item existente = Items.get(i);
            if (existente.product().barCode().equals(itemNovo.product().barCode())) {
                Items.set(i, new Item(existente.product(), existente.count() + itemNovo.count()));
                this.total += itemNovo.subtotal();
                return;
            }
        }
        this.Items.add(itemNovo);
        this.total += itemNovo.subtotal();
    }

    /**
     * Remove um item da lista de compras com base no código de barras.
     * Atualiza o valor total da venda caso o item seja removido.
     *
     * @param barCode O código de barras do produto a ser removido.
     * @return {@code true} se o item foi encontrado e removido, {@code false} caso contrário.
     */
    public boolean removeItem(String barCode) {
        for (Item item : Items) {
            if (item.product().barCode().equals(barCode)) {
                this.total -= item.subtotal();
                Items.remove(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna uma lista contendo os itens atuais da venda.
     *
     * @return Uma nova lista contendo os itens do carrinho (cópia defensiva).
     */
    public List<Item> getItems() {
        return new ArrayList<>(Items);
    }

    /**
     * Obtém o valor total acumulado da venda.
     *
     * @return O valor total em reais (R$).
     */
    public double getTotal() {
        return total;
    }

    /**
     * Formata os dados da venda em um modelo de cupom fiscal (texto).
     *
     * @return Uma {@link String} contendo o cupom fiscal formatado.
     */
    public String gerarNotaFiscal() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        String linha = "=".repeat(45);
        String linhaDivisoria = "-".repeat(45);
        
        sb.append(linha).append("\n");
        sb.append("          MERCADINHO DO ZECA LTDA          \n");
        sb.append("   AV. DA COMPUTACAO, 3o PERIODO - UNIT    \n");
        sb.append(linha).append("\n");
        sb.append(String.format("NOTA FISCAL - ID VENDA: #%s%n", ID));
        sb.append(String.format("DATA/HORA: %s%n", fmt.format(dateTime)));
        sb.append(String.format("CLIENTE CPF: %s%n", formatarCPF(CPF)));
        sb.append(String.format("PAGAMENTO: %s%n", paymentType));
        sb.append(linha).append("\n");
        sb.append(String.format("%-25s %3s %14s%n", "PRODUTO", "QTD", "PRECO(R$)"));
        sb.append(linhaDivisoria).append("\n");

        for (Item item : Items) {
            String nomeProd = item.product().name();
            if (nomeProd.length() > 24) nomeProd = nomeProd.substring(0, 21) + "...";
            sb.append(String.format("%-25s %3d %14.2f%n", nomeProd, item.count(), item.subtotal()));
        }

        sb.append(linha).append("\n");
        sb.append(String.format("  TOTAL: R$ %.2f%n", total));
        sb.append(linha).append("\n");
        sb.append("    Obrigado pela compra!\n");
        sb.append("        Volte sempre! :)\n");
        sb.append(linha).append("\n");

        return sb.toString();
    }

    /**
     * Aplica máscara de formatação a uma string de CPF.
     *
     * @param cpf O CPF como string de dígitos (apenas números).
     * @return O CPF formatado (ex: 000.000.000-00), ou a string original caso inválida.
     */
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}