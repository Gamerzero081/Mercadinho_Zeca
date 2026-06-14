package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma transação de venda realizada no sistema.
 * Esta classe gerencia a lista de itens, o cálculo do valor total,
 * a identificação do cliente (CPF) e a formatação do cupom fiscal.
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
     * @param CPF         O número do CPF do cliente (apenas números).
     * @param paymentType O tipo de pagamento utilizado (ex: PIX, Débito).
     */
    public Sale(String ID, String CPF, String paymentType) {
        this.ID = ID;
        this.CPF = CPF;
        this.paymentType = paymentType;
        this.dateTime = LocalDateTime.now();
    }

    /**
     * Adiciona um item à venda ou incrementa a quantidade se o produto já existir.
     * Atualiza o valor total da transação automaticamente.
     *
     * @param itemNovo O objeto {@link Item} a ser adicionado.
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
     * Atualiza o valor total da venda caso a remoção seja bem-sucedida.
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
     * Retorna uma cópia da lista de itens contidos nesta venda.
     *
     * @return Uma lista contendo os itens atuais da venda.
     */
    public List<Item> getItems() {
        return new ArrayList<>(Items);
    }

    /** @return O identificador único da venda. */
    public String getID() { return ID; }

    /** @return O CPF do cliente associado à venda. */
    public String getCPF() { return CPF; }

    /** @return O método de pagamento escolhido. */
    public String getPaymentType() { return paymentType; }

    /** @return A data e hora em que a venda foi realizada. */
    public LocalDateTime getDateTime() { return dateTime; }

    /**
     * Formata os dados da venda em um modelo de cupom fiscal para exibição.
     *
     * @return Uma {@link String} contendo o cupom fiscal formatado.
     */
    public String gerarNotaFiscal() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        String linha = "=".repeat(42);
        String linhaDivisora = "-".repeat(42);

        sb.append(linha).append("\n");
        sb.append("        MERCADINHO DO ZECA\n");
        sb.append("      CNPJ: 40.028.922/0001-00\n");
        sb.append("     Unit, 123 - Farolandia\n");
        sb.append(linha).append("\n");
        sb.append(String.format("  Pedido: %-10s  %s%n", ID, fmt.format(dateTime)));
        sb.append(String.format("  CPF: %s%n", formatarCPF(CPF)));
        sb.append(String.format("  Pagamento: %s%n", paymentType));
        sb.append(linhaDivisora).append("\n");
        sb.append(String.format("  %-20s %5s %8s%n", "PRODUTO", "QTD", "SUBTOTAL"));
        sb.append(linhaDivisora).append("\n");

        for (Item item : Items) {
            sb.append(String.format("  %-20s %5dx R$%6.2f%n",
                    item.product().name(), item.count(), item.subtotal()));
            sb.append(String.format("  %-26s R$%6.2f un%n", "", item.product().price()));
        }

        sb.append(linha).append("\n");
        sb.append(String.format("  TOTAL: R$ %.2f%n", total));
        sb.append(linha).append("\n");
        sb.append("    Obrigado pela preferência!\n");
        sb.append("        Volte sempre! :)\n");
        sb.append(linha).append("\n");

        return sb.toString();
    }

    /**
     * Aplica máscara de formatação (000.000.000-00) em uma string de CPF.
     *
     * @param cpf O CPF como string de dígitos.
     * @return O CPF formatado ou a string original caso o formato seja inválido.
     */
    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    /**
     * Serializa a venda em formato de texto delimitado por ponto e vírgula para persistência.
     *
     * @return String formatada: ID;CPF;Pagamento;Total;Data;[Itens]
     */
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder text = new StringBuilder();
        text.append(ID).append(";")
                .append(CPF).append(";")
                .append(paymentType).append(";")
                .append(String.format("%.2f", total)).append(";")
                .append(fmt.format(dateTime)).append(";[");

        int totalDeItens = this.Items.size();
        for (int i = 0; i < totalDeItens; i++) {
            Item itemAtual = this.Items.get(i);
            text.append(itemAtual.product().name()).append("x").append(itemAtual.count());
            if (i < totalDeItens - 1) text.append("|");
        }
        text.append("]");
        return text.toString();
    }

    /** @return O valor total da venda. */
    public double getTotal() { 
        return this.total;
    }
}