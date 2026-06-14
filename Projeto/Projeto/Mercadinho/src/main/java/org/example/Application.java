package org.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Classe principal responsável por gerenciar a interface gráfica da aplicação.
 * Controla a navegação entre painéis, eventos de interface e a exibição visual do ponto de venda.
 *
 * @see #iniciarVenda()
 * @see #biparNoCarrinho(String)
 */
public class Application extends JFrame {

    private final JPanel panel;
    private final CardLayout cardLayout;
    private JTextField barcode, name, price, image;
    private JLabel imageRegister;
    private JTextField cpf, cleanBarcode;
    private JComboBox<String> paymentType;
    private JTable itemTable;
    private DefaultTableModel itemModel;
    private JLabel total, saleImage;

    private JTable productTable;
    private DefaultTableModel productModel;

    private JTextArea history;
    private JTextArea notaFiscalArea;

    private final Font fTitulo      = new Font("Segoe UI", Font.BOLD,  45);
    private final Font fSubtitulo   = new Font("Segoe UI", Font.BOLD,  40);
    private final Font fTxtTrocaTelas = new Font("Segoe UI", Font.PLAIN, 30);
    private final Font fTxtBtnFinal = new Font("Segoe UI", Font.PLAIN, 15);

    private final Color cBackground  = new Color(247, 245, 250);
    private final Color cPainel      = new Color(235, 230, 242);
    private final Color cTxtTitulo   = new Color(74,  59,  96);
    private final Color cTxtComum    = new Color(85,  80,  95);
    private final Color cBotaoPadrao = new Color(163, 147, 191);
    private final Color cBotaoFinal  = new Color(132, 107, 176);
    private final Color cBotaoRemover= new Color(200,  80,  80);
    private final Color cTxtBotao    = Color.WHITE;

    /**
     * Construtor da aplicação. Configura as dimensões da janela principal
     * e inicializa o gerenciador de layouts (CardLayout) com os painéis do sistema.
     *
     * @see #menuPrincipal()
     */
    public Application() {
        setTitle("Mercadinho do Zeca");
        setSize(1024, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);

        panel.add(menuPrincipal(),         "menuPrincipal");
        panel.add(productMenu(),           "produtos");
        panel.add(menuVendas(),            "vendas");
        panel.add(telaCadastroProduto(),   "cadastroProdutos");
        panel.add(telaListarProdutos(),    "listaProdutos");
        panel.add(telaNovaVenda(),         "vendaNova");
        panel.add(telaHistoricoVendas(),   "historicoVendas");
        panel.add(telaNotaFiscal(),        "notaFiscal");

        add(panel);
        cardLayout.show(panel, "menuPrincipal");
    }

    /**
     * Constrói o painel do menu principal da aplicação.
     *
     * @return O painel configurado para exibição inicial.
     * @see #productMenu()
     */
    private JPanel menuPrincipal() {
        JPanel painel = new JPanel(new GridLayout(3, 2, 20, 20));
        painel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));
        painel.setBackground(cBackground);

        JLabel titulo = new JLabel("MERCADINHO DO ZECA", SwingConstants.CENTER);
        titulo.setFont(fTitulo);
        titulo.setForeground(cTxtTitulo);
        painel.add(titulo);

        JButton btnProdutos = new Item.BtnRedondo("Produtos", 150, cBotaoPadrao, cTxtBotao);
        btnProdutos.setFont(fTxtTrocaTelas);
        btnProdutos.addActionListener(_ -> cardLayout.show(panel, "produtos"));

        JButton btnVendas = new Item.BtnRedondo("Vendas", 150, cBotaoPadrao, cTxtBotao);
        btnVendas.setFont(fTxtTrocaTelas);
        btnVendas.addActionListener(_ -> cardLayout.show(panel, "vendas"));

        painel.add(btnProdutos);
        painel.add(btnVendas);
        return painel;
    }

    /**
     * Inicializa e configura o painel contendo o menu de opções para a gestão de produtos.
     *
     * @return O painel do menu de produtos.
     * @see #telaCadastroProduto()
     */
    private JPanel productMenu() {
        JPanel painel = new JPanel(new GridLayout(4, 1, 15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(60, 200, 60, 200));
        painel.setBackground(cBackground);

        JLabel tittle = new JLabel("PRODUTOS", SwingConstants.CENTER);
        tittle.setFont(fSubtitulo);
        tittle.setForeground(cTxtTitulo);
        painel.add(tittle);

        JButton register = new Item.BtnRedondo("Cadastrar / Alterar Produto", 150, cBotaoPadrao, cTxtBotao);
        register.setFont(fTxtTrocaTelas);
        register.addActionListener(_ -> {
            barcode.setText(""); name.setText(""); price.setText(""); image.setText("");
            imageRegister.setIcon(null); imageRegister.setText("Sem Foto Carregada");
            barcode.setEditable(true);
            cardLayout.show(panel, "cadastroProdutos");
        });

        JButton btnListar = new Item.BtnRedondo("Listar Produtos Cadastrados", 150, cBotaoPadrao, cTxtBotao);
        btnListar.setFont(fTxtTrocaTelas);
        btnListar.addActionListener(_ -> { atualizarTabelaProdutos(); cardLayout.show(panel, "listaProdutos"); });

        JButton btnVoltar = new Item.BtnRedondo("<- Voltar ao menu principal", 150, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtTrocaTelas);
        btnVoltar.addActionListener(_ -> cardLayout.show(panel, "menuPrincipal"));

        painel.add(register);
        painel.add(btnListar);
        painel.add(btnVoltar);
        return painel;
    }

    /**
     * Inicializa e configura o painel com as opções para iniciar uma venda ou consultar o histórico.
     *
     * @return O painel de gestão de vendas.
     * @see #telaNovaVenda()
     */
    private JPanel menuVendas() {
        JPanel painel = new JPanel(new GridLayout(4, 1, 15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(60, 200, 60, 200));
        painel.setBackground(cBackground);

        JLabel titulo = new JLabel("VENDA", SwingConstants.CENTER);
        titulo.setFont(fSubtitulo);
        titulo.setForeground(cTxtTitulo);
        painel.add(titulo);

        JButton btnNovaVenda = new Item.BtnRedondo("Iniciar nova venda", 150, cBotaoPadrao, cTxtBotao);
        btnNovaVenda.setFont(fTxtTrocaTelas);
        btnNovaVenda.addActionListener(_ -> {
            itemModel.setRowCount(0);
            total.setText("TOTAL: R$ 0.00");
            saleImage.setIcon(null);
            saleImage.setText("Sem imagem");
            cpf.setText("");
            cleanBarcode.setText("");
            cardLayout.show(panel, "vendaNova");
        });

        JButton btnHistorico = new Item.BtnRedondo("Histórico de vendas", 150, cBotaoPadrao, cTxtBotao);
        btnHistorico.setFont(fTxtTrocaTelas);
        btnHistorico.addActionListener(_ -> { historicoNoTexto(); cardLayout.show(panel, "historicoVendas"); });

        JButton btnVoltar = new Item.BtnRedondo("<- Voltar ao Menu Principal", 150, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtTrocaTelas);
        btnVoltar.addActionListener(_ -> cardLayout.show(panel, "menuPrincipal"));

        painel.add(btnNovaVenda);
        painel.add(btnHistorico);
        painel.add(btnVoltar);
        return painel;
    }

    /**
     * Constrói o painel contendo o formulário para cadastro e alteração de produtos.
     *
     * @return O painel de formulário de produtos.
     * @see #salvarProduto()
     */
    private JPanel telaCadastroProduto() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        painel.setBackground(cBackground);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBackground(cPainel);
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblBc = new JLabel("Código de Barras:"); lblBc.setFont(fTxtTrocaTelas); lblBc.setForeground(cTxtComum);
        form.add(lblBc);
        barcode = new JTextField(); barcode.setFont(fTxtTrocaTelas);
        form.add(barcode);

        JLabel lblNm = new JLabel("Nome do Produto:"); lblNm.setFont(fTxtTrocaTelas); lblNm.setForeground(cTxtComum);
        form.add(lblNm);
        name = new JTextField(); name.setFont(fTxtTrocaTelas);
        form.add(name);

        JLabel lblPr = new JLabel("Preço (R$):"); lblPr.setFont(fTxtTrocaTelas); lblPr.setForeground(cTxtComum);
        form.add(lblPr);
        price = new JTextField(); price.setFont(fTxtTrocaTelas);
        form.add(price);

        JLabel lblIm = new JLabel("Nome da Imagem:"); lblIm.setFont(fTxtTrocaTelas); lblIm.setForeground(cTxtComum);
        form.add(lblIm);
        image = new JTextField(); image.setFont(fTxtTrocaTelas);
        form.add(image);

        image.addActionListener(_ -> mostrarImagemNoLabel(image.getText().trim(), imageRegister));
        image.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                mostrarImagemNoLabel(image.getText().trim(), imageRegister);
            }
        });

        imageRegister = new JLabel("Sem Foto Carregada", SwingConstants.CENTER);
        imageRegister.setFont(fTxtTrocaTelas);
        imageRegister.setForeground(cTxtComum);
        imageRegister.setBorder(BorderFactory.createLineBorder(cBotaoPadrao));
        imageRegister.setPreferredSize(new Dimension(180, 180));

        JPanel botaoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoPanel.setBackground(cBackground);
        JButton btnVoltar = new Item.BtnRedondo("Voltar", 15, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtBtnFinal);
        btnVoltar.addActionListener(_ -> cardLayout.show(panel, "produtos"));
        JButton btnSalvar = new Item.BtnRedondo("Confirmar e Salvar", 15, cBotaoFinal, cTxtBotao);
        btnSalvar.setFont(fTxtBtnFinal);
        btnSalvar.addActionListener(_ -> salvarProduto());
        botaoPanel.add(btnVoltar);
        botaoPanel.add(btnSalvar);

        JLabel topoTitulo = new JLabel("CADASTRO E ALTERAÇÃO DE PRODUTOS", SwingConstants.CENTER);
        topoTitulo.setFont(fSubtitulo);
        topoTitulo.setForeground(cTxtTitulo);

        painel.add(topoTitulo, BorderLayout.NORTH);
        painel.add(form, BorderLayout.CENTER);
        painel.add(imageRegister, BorderLayout.EAST);
        painel.add(botaoPanel, BorderLayout.SOUTH);
        return painel;
    }

    /**
     * Constrói o painel contendo a tabela de listagem dos produtos cadastrados no sistema.
     *
     * @return O painel com a tabela de produtos.
     * @see #carregarProdutoParaEditar()
     */
    private JPanel telaListarProdutos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painel.setBackground(cBackground);

        productModel = new DefaultTableModel(new Object[]{"Código", "Nome", "Preço", "Imagem"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productModel);
        productTable.setFont(fTxtBtnFinal);
        productTable.setRowHeight(22);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(cBackground);
        JButton btnAlterar = new Item.BtnRedondo("Alterar Produto Selecionado", 15, cBotaoPadrao, cTxtBotao);
        btnAlterar.setFont(fTxtBtnFinal);
        btnAlterar.addActionListener(_ -> carregarProdutoParaEditar());
        JButton btnVoltar = new Item.BtnRedondo("Voltar ao Menu de Produtos", 15, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtBtnFinal);
        btnVoltar.addActionListener(_ -> cardLayout.show(panel, "produtos"));
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnVoltar);

        JLabel topoTitulo = new JLabel("PRODUTOS CADASTRADOS", SwingConstants.CENTER);
        topoTitulo.setFont(fSubtitulo);
        topoTitulo.setForeground(cTxtTitulo);

        painel.add(topoTitulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);
        return painel;
    }

    /**
     * Constrói o painel do ponto de venda (PDV), contendo os campos de identificação 
     * do cliente, forma de pagamento e a listagem de itens da venda atual.
     *
     * @return O painel correspondente ao caixa de vendas.
     * @see #iniciarVenda()
     */
    private JPanel telaNovaVenda() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painel.setBackground(cBackground);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topo.setBackground(cPainel);

        JLabel lblCpf = new JLabel("CPF Cliente:"); lblCpf.setFont(fTxtBtnFinal); lblCpf.setForeground(cTxtComum);
        topo.add(lblCpf);
        cpf = new JTextField(11); cpf.setFont(fTxtBtnFinal);
        topo.add(cpf);

        JLabel lblPg = new JLabel("Forma Pagamento:"); lblPg.setFont(fTxtBtnFinal); lblPg.setForeground(cTxtComum);
        topo.add(lblPg);
        String[] opcoes = {"a vista", "crédito", "débito", "pix"};
        paymentType = new JComboBox<>(opcoes); paymentType.setFont(fTxtBtnFinal);
        topo.add(paymentType);

        JButton btnIniciar = new Item.BtnRedondo("Iniciar venda", 15, cBotaoFinal, cTxtBotao);
        btnIniciar.setFont(fTxtBtnFinal);
        btnIniciar.addActionListener(_ -> iniciarVenda());
        topo.add(btnIniciar);

        painel.add(topo, BorderLayout.NORTH);

        JPanel painelCarrinho = new JPanel(new BorderLayout(5, 5));
        painelCarrinho.setBackground(cBackground);

        JPanel biparPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        biparPanel.setBackground(cBackground);
        JLabel lblBp = new JLabel("Bipar Código de Barras (Enter):"); lblBp.setFont(fTxtBtnFinal); lblBp.setForeground(cTxtComum);
        biparPanel.add(lblBp);
        cleanBarcode = new JTextField(12); cleanBarcode.setFont(fTxtBtnFinal);
        cleanBarcode.addActionListener(_ -> {
            String cod = cleanBarcode.getText().trim();
            if (!cod.isEmpty()) {
                biparNoCarrinho(cod);
                cleanBarcode.setText("");
            }
        });
        biparPanel.add(cleanBarcode);
        painelCarrinho.add(biparPanel, BorderLayout.NORTH);

        itemModel = new DefaultTableModel(new Object[]{"Código", "Produto", "Qtd", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        itemTable = new JTable(itemModel);
        itemTable.setFont(fTxtBtnFinal);
        itemTable.setRowHeight(22);
        painelCarrinho.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        painel.add(painelCarrinho, BorderLayout.CENTER);

        JPanel painelLateralDireita = new JPanel(new BorderLayout(10, 10));
        painelLateralDireita.setBackground(cPainel);
        painelLateralDireita.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saleImage = new JLabel("Sem imagem", SwingConstants.CENTER);
        saleImage.setFont(fTxtBtnFinal);
        saleImage.setForeground(cTxtComum);
        saleImage.setPreferredSize(new Dimension(150, 150));
        saleImage.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(cBotaoPadrao),
                "Item Selecionado", 0, 0, fTxtBtnFinal, cTxtComum));
        painelLateralDireita.add(saleImage, BorderLayout.NORTH);

        total = new JLabel("TOTAL: R$ 0.00", SwingConstants.CENTER);
        total.setFont(fSubtitulo);
        total.setForeground(cTxtTitulo);
        total.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        painelLateralDireita.add(total, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 5, 8));
        btnPanel.setBackground(cPainel);

        JButton btnRemover = new Item.BtnRedondo("Remover Item", 15, cBotaoRemover, cTxtBotao);
        btnRemover.setFont(fTxtBtnFinal);
        btnRemover.addActionListener(_ -> removerItemSelecionado());

        JButton btnFecharVenda = new Item.BtnRedondo("Finalizar Venda", 15, cBotaoFinal, cTxtBotao);
        btnFecharVenda.setFont(fTxtBtnFinal);
        btnFecharVenda.addActionListener(_ -> finalizarVenda());

        JButton btnVoltar = new Item.BtnRedondo("Voltar ao Menu", 15, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtBtnFinal);
        btnVoltar.addActionListener(_ -> {
            if (SaleManager.currentSale != null) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Tem uma venda em andamento. Deseja cancelar e voltar?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                SaleManager.currentSale = null;
            }
            cardLayout.show(panel, "vendas");
        });

        btnPanel.add(btnVoltar);
        btnPanel.add(btnRemover);
        btnPanel.add(btnFecharVenda);

        btnPanel.add(btnRemover);
        btnPanel.add(btnFecharVenda);
        painelLateralDireita.add(btnPanel, BorderLayout.SOUTH);

        painel.add(painelLateralDireita, BorderLayout.EAST);

        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && itemTable.getSelectedRow() != -1) {
                String codItem = itemTable.getValueAt(itemTable.getSelectedRow(), 0).toString();
                Product p = SaleManager.getProduct(codItem);
                if (p != null) mostrarImagemNoLabel(p.image(), saleImage);
            }
        });

        return painel;
    }

    /**
     * Constrói o painel de histórico, exibindo o registro textual das vendas
     * finalizadas anteriormente no sistema.
     *
     * @return O painel configurado com o componente de histórico.
     * @see #historicoNoTexto()
     */
    private JPanel telaHistoricoVendas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painel.setBackground(cBackground);

        history = new JTextArea();
        history.setEditable(false);
        history.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JButton btnVoltar = new Item.BtnRedondo("Voltar ao Menu de Vendas", 15, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtBtnFinal);
        btnVoltar.addActionListener(_ -> cardLayout.show(panel, "vendas"));

        JLabel topoTitulo = new JLabel("HISTÓRICO", SwingConstants.CENTER);
        topoTitulo.setFont(fSubtitulo);
        topoTitulo.setForeground(cTxtTitulo);

        painel.add(topoTitulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(history), BorderLayout.CENTER);
        painel.add(btnVoltar, BorderLayout.SOUTH);
        return painel;
    }

    /**
     * Constrói o painel para exibição e impressão do comprovante fiscal da venda recém-finalizada.
     *
     * @return O painel da nota fiscal.
     * @see #imprimirNota()
     */
    private JPanel telaNotaFiscal() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painel.setBackground(cBackground);

        JLabel topoTitulo = new JLabel("NOTA FISCAL", SwingConstants.CENTER);
        topoTitulo.setFont(fSubtitulo);
        topoTitulo.setForeground(cTxtTitulo);

        notaFiscalArea = new JTextArea();
        notaFiscalArea.setEditable(false);
        notaFiscalArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        notaFiscalArea.setBackground(new Color(255, 255, 255));
        notaFiscalArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(cBackground);

        JButton btnImprimir = new Item.BtnRedondo("Imprimir / Salvar PDF", 15, cBotaoFinal, cTxtBotao);
        btnImprimir.setFont(fTxtBtnFinal);
        btnImprimir.addActionListener(_ -> imprimirNota());

        JButton btnVoltar = new Item.BtnRedondo("Voltar ao Menu de Vendas", 15, cBotaoPadrao, cTxtBotao);
        btnVoltar.setFont(fTxtBtnFinal);
        btnVoltar.addActionListener(_ -> cardLayout.show(panel, "vendas"));

        btnPanel.add(btnImprimir);
        btnPanel.add(btnVoltar);

        painel.add(topoTitulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(notaFiscalArea), BorderLayout.CENTER);
        painel.add(btnPanel, BorderLayout.SOUTH);
        return painel;
    }

    /**
     * Localiza e renderiza a imagem do produto no componente visual especificado.
     * Em caso de falha na localização ou formato, exibe um indicativo textual de ausência.
     *
     * @param nomeImagem O nome do arquivo de imagem a ser localizado nos recursos.
     * @param label      O componente {@link JLabel} onde a imagem será renderizada.
     * @see SaleManager#getProduct(String)
     */
    private void mostrarImagemNoLabel(String nomeImagem, JLabel label) {
        if (nomeImagem == null || nomeImagem.trim().isEmpty()) {
            label.setIcon(null); label.setText("Sem imagem"); return;
        }
        String[] extensoes = {".jpeg", ".jpg", ".png"};
        java.net.URL urlImg = null;
        if (nomeImagem.toLowerCase().endsWith(".png") ||
                nomeImagem.toLowerCase().endsWith(".jpg") ||
                nomeImagem.toLowerCase().endsWith(".jpeg")) {
            urlImg = getClass().getClassLoader().getResource("img/" + nomeImagem);
        } else {
            for (String ext : extensoes) {
                urlImg = getClass().getClassLoader().getResource("img/" + nomeImagem + ext);
                if (urlImg != null) break;
            }
        }
        try {
            if (urlImg != null) {
                ImageIcon icon = new ImageIcon(urlImg);
                Image redimensionada = icon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(redimensionada));
                label.setText("");
            } else {
                label.setIcon(null); label.setText("Não achou a imagem");
            }
        } catch (Exception err) {
            System.out.println("Erro na imagem");
        }
    }

    /**
     * Carrega os dados do produto selecionado na tabela e preenche os campos do formulário
     * de cadastro para possibilitar sua edição pelo usuário.
     *
     * @see #telaCadastroProduto()
     */
    private void carregarProdutoParaEditar() {
        int linha = productTable.getSelectedRow();
        if (linha != -1) {
            String codigo = productTable.getValueAt(linha, 0).toString();
            Product p = SaleManager.getProduct(codigo);
            if (p != null) {
                barcode.setText(p.barCode()); barcode.setEditable(false);
                name.setText(p.name());
                price.setText(String.valueOf(p.price()));
                image.setText(p.image());
                mostrarImagemNoLabel(p.image(), imageRegister);
                cardLayout.show(panel, "cadastroProdutos");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto da lista!");
        }
    }

    /**
     * Valida os dados inseridos no formulário de produtos e os encaminha para persistência
     * no gerenciador de negócios.
     *
     * @see SaleManager#setProduct(String, String, double, String)
     */
    private void salvarProduto() {
        try {
            String cod  = barcode.getText().trim();
            String nome = name.getText().trim();
            double preco = Double.parseDouble(price.getText().trim().replace(",", "."));
            String img  = image.getText().trim();
            if (cod.isEmpty() || nome.isEmpty() || img.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!"); return;
            }
            SaleManager.setProduct(cod, nome, preco, img);
            JOptionPane.showMessageDialog(null, "Produto salvo com sucesso!");
            atualizarTabelaProdutos();
            cardLayout.show(panel, "listaProdutos");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Preço inválido. Use ponto ou vírgula como separador decimal.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro nos dados digitados.");
        }
    }

    /**
     * Atualiza os registros contidos no modelo da tabela de produtos, refletindo as 
     * alterações recentes efetuadas no cadastro.
     *
     * @see #telaListarProdutos()
     */
    private void atualizarTabelaProdutos() {
        productModel.setRowCount(0);
        for (Product p : SaleManager.products) {
            productModel.addRow(new Object[]{p.barCode(), p.name(), "R$ " + p.price(), p.image()});
        }
    }

    /**
     * Valida os dados obrigatórios do cliente (CPF) e a forma de pagamento selecionada
     * para inicializar uma nova transação comercial.
     *
     * @see SaleManager#sell(String, String)
     */
    private void iniciarVenda() {
        String cpfTxt = this.cpf.getText().trim();
        String pgto   = Objects.requireNonNull(paymentType.getSelectedItem()).toString();
        if (SaleManager.sell(cpfTxt, pgto)) {
            itemModel.setRowCount(0);
            total.setText("TOTAL: R$ 0.00");
            saleImage.setIcon(null);
            saleImage.setText("Aguardando Bip");
            JOptionPane.showMessageDialog(null, "Venda iniciada!");
        } else {
            JOptionPane.showMessageDialog(null, "CPF inválido!");
        }
    }

    /**
     * Processa a entrada do código de barras inserido no sistema e aciona a inclusão 
     * do respectivo produto no carrinho de compras ativo.
     *
     * @param codigo A sequência alfanumérica que identifica o produto.
     * @see SaleManager#addProduct(String)
     */
    private void biparNoCarrinho(String codigo) {
        Product p = SaleManager.addProduct(codigo);
        if (p != null) {
            atualizarTabelaCarrinho();
            double tot = SaleManager.currentSale.getTotal();
            this.total.setText("TOTAL: R$ " + String.format("%.2f", tot));
            mostrarImagemNoLabel(p.image(), saleImage);
        } else {
            if (SaleManager.currentSale == null) {
                JOptionPane.showMessageDialog(null, "Inicie a venda primeiro!");
            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado.");
            }
        }
    }

    /**
     * Sincroniza o componente de tabela visual (carrinho) com a lista interna de itens 
     * da venda em andamento, atualizando quantidades e subtotais.
     *
     * @see Sale#getItems()
     */
    private void atualizarTabelaCarrinho() {
        itemModel.setRowCount(0);
        for (Item item : SaleManager.currentSale.getItems()) {
            itemModel.addRow(new Object[]{
                    item.product().barCode(),
                    item.product().name(),
                    item.count(),
                    String.format("R$ %.2f", item.subtotal())
            });
        }
    }

    /**
     * Remove o item selecionado pelo usuário na tabela da transação em andamento
     * e recalcula o valor total da venda.
     *
     * @see Sale#removeItem(String)
     */
    private void removerItemSelecionado() {
        if (SaleManager.currentSale == null) {
            JOptionPane.showMessageDialog(null, "Nenhuma venda iniciada."); return;
        }
        int linha = itemTable.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(null, "Selecione um item para remover."); return;
        }
        String codigo = itemTable.getValueAt(linha, 0).toString();
        SaleManager.currentSale.removeItem(codigo);
        atualizarTabelaCarrinho();
        this.total.setText("TOTAL: R$ " + String.format("%.2f", SaleManager.currentSale.getTotal()));
    }

    /**
     * Conclui o processo de venda em andamento, gera o comprovante fiscal estruturado
     * e redireciona a interface para o painel de nota fiscal.
     *
     * @see SaleManager#finalizeSale()
     */
    private void finalizarVenda() {
        if (SaleManager.currentSale == null) {
            JOptionPane.showMessageDialog(null, "Nenhuma venda iniciada."); return;
        }
        if (SaleManager.currentSale.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Carrinho vazio! Adicione produtos antes de finalizar."); return;
        }
        String nota = SaleManager.finalizeSale();
        notaFiscalArea.setText(nota);
        notaFiscalArea.setCaretPosition(0);
        cardLayout.show(panel, "notaFiscal");
    }

    /**
     * Aciona a API de impressão do Java para direcionar o conteúdo da nota fiscal 
     * para o serviço de impressão padrão configurado no sistema operacional.
     *
     * @throws Exception Caso ocorra uma falha na comunicação com o serviço de impressão.
     * @see #telaNotaFiscal()
     */
    private void imprimirNota() {
        try {
            notaFiscalArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao imprimir: " + e.getMessage());
        }
    }

    /**
     * Realiza a leitura do arquivo contendo o histórico de transações e atualiza o 
     * componente textual encarregado de sua exibição no painel.
     *
     * @see ProductManager#loadSales()
     */
    private void historicoNoTexto() {
        List<String> linhas = ProductManager.loadSales();
        StringBuilder sb = new StringBuilder();
        for (String linha : linhas) sb.append(linha).append("\n");
        history.setText(sb.toString());
    }

    /**
     * Ponto de entrada da aplicação. Inicializa a interface gráfica do sistema 
     * utilizando a thread de despacho de eventos do Swing (EDT).
     *
     * @param args Argumentos da linha de comando fornecidos durante a execução.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Application tela = new Application();
            tela.setVisible(true);
        });
    }
}