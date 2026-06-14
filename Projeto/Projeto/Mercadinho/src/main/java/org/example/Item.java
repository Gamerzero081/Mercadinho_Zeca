package org.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

/**
 * Representa um item selecionado em uma transação de venda,
 * contendo a referência do produto e a respectiva quantidade.
 *
 * @param product O produto selecionado.
 * @param count   Quantidade unitária do produto.
 */
public record Item(Product product, int count) {

    /**
     * Calcula o valor subtotal do item (preço unitário multiplicado pela quantidade).
     *
     * @return O valor monetário total do item na venda.
     * @throws NullPointerException Se o produto associado for nulo.
     */
    public double subtotal() {
        return product().price() * count;
    }

    /**
     * Componente customizado que implementa um botão com cantos arredondados.
     * * @see Application
     */
    static class BtnRedondo extends JButton {
        private final int raio;

        /**
         * Construtor do componente de botão customizado.
         *
         * @param texto  Texto a ser exibido no botão.
         * @param raio   Raio do arredondamento dos cantos.
         * @param cFundo Cor de fundo do botão.
         * @param cTexto Cor da fonte do texto.
         */
        public BtnRedondo(String texto, int raio, Color cFundo, Color cTexto) {
            super(texto);
            this.raio = raio;
            setBackground(cFundo);
            setForeground(cTexto);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        /**
         * Método sobrescrito para realizar a renderização customizada do componente,
         * aplicando o efeito de arredondamento nos cantos.
         *
         * @param g O contexto gráfico utilizado para o desenho.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}