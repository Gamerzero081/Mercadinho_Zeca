package org.example;

import lanta.math.Parser;

/**
 * Modelo imutável que representa um produto comercial do sistema.
 * <p>
 * Este registo encapsula os atributos essenciais para identificação,
 * fixação de preços de mercado e referência visual de itens geridos no stock.
 * </p>
 *
 * @param barCode O código de barras exclusivo do produto, usado como identificador chave.
 * @param name    O nome descritivo ou comercial do produto.
 * @param price   O valor monetário de venda unitária do item.
 * @param image   O nome do ficheiro ou caminho do recurso de imagem do produto.
 */
public record Product(String barCode, String name, double price, String image) {

    /**
     * Serializa os dados do produto em formato de linha delimitada.
     * <p>
     * Utiliza o ponto e vírgula ({@code ;}) como separador, seguindo o padrão
     * ideal para ficheiros de texto simples (CSV): {@code barcode;name;price;image}.
     * </p>
     *
     * @return Uma {@link String} contendo os atributos do produto linearizados.
     */
    @Override
    public String toString() {
        return barCode + ";" + name + ";" + price + ";" + image;
    }

    /**
     * Realiza o parse de uma string de dados estruturada para instanciar um produto.
     * <p>
     * O método analisa cadeias textuais brutas separadas por pontos e vírgulas.
     * Se os parâmetros essenciais de tamanho não forem atendidos, a inicialização falha de forma segura.
     * </p>
     *
     * @param linhaTxt A linha de texto recuperada de bases de dados ou ficheiros.
     * @return Um objeto {@link Product} populado ou {@code null} caso a entrada seja inválida.
     */
    public static Product parse(String linhaTxt) {
        if (linhaTxt == null || linhaTxt.isBlank()) {
            return null;
        }
        String[] data = linhaTxt.split(";");
        if (data.length < 3) {
            return null;
        }
        String imgPath = (data.length > 3) ? data[3] : "";
        
        return new Product(
            data[0], 
            data[1], 
            Parser.toNumber(data[2], Double::valueOf, 0.0), 
            imgPath
        );
    }
    /**
     * Cria uma nova cópia atualizada do produto mantendo o mesmo código de barras.
     *
     * @param name  O novo nome do produto.
     * @param price O novo preço praticado para venda.
     * @param image O novo identificador ou link de imagem.
     * @return Uma nova instância imutável de {@link Product} modificada.
     */
    public Product update(String name, double price, String image) {
        return new Product(this.barCode, name, price, image);
    }
}