//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class Compilador extends JFrame {
    private JTextArea textArea;
    private JButton executeButton;
    private JButton showTableButton;
    private JTextArea consoleArea;
    private JTextArea generatedCodeArea;
    private JTable table;
    private DefaultTableModel tableModel;

    public Compilador() {
        this.setTitle("IDE");
        this.setDefaultCloseOperation(3);
        this.setExtendedState(6);
        this.setMinimumSize(new Dimension(1024, 768));
        this.initComponents();
        this.setupLayout();
        this.setVisible(true);
    }

    private void initComponents() {
        Font sourceFont = new Font("Consolas", 0, 18);
        Font generatedFont = new Font("Consolas", 0, 14);
        Font consoleFont = new Font("Consolas", 0, 14);
        new Font("Arial", 1, 14);
        this.textArea = new JTextArea();
        this.textArea.setFont(sourceFont);
        this.generatedCodeArea = new JTextArea();
        this.generatedCodeArea.setFont(generatedFont);
        this.generatedCodeArea.setEditable(false);
        this.consoleArea = new JTextArea();
        this.consoleArea.setFont(consoleFont);
        this.consoleArea.setEditable(false);
        this.executeButton = new JButton("Executar");
        this.executeButton.addActionListener((e) -> this.executeProgram());
        this.showTableButton = new JButton("Mostrar Tabela de Símbolos");
        this.showTableButton.addActionListener((e) -> this.showTableDialog());
        String[] columnNames = new String[]{"ID", "Tipo", "Escopo", "Inicializado", "Utilizado", "Parametro", "Função", "Vetor"};
        this.tableModel = new DefaultTableModel(columnNames, 0);
        this.table = new JTable(this.tableModel);
    }

    private void setupLayout() {
        JSplitPane codeSplitPane = new JSplitPane(1);
        codeSplitPane.setLeftComponent(this.createPanelWithLabel(this.textArea, "Código Fonte"));
        codeSplitPane.setRightComponent(this.createPanelWithLabel(this.generatedCodeArea, "Código Gerado"));
        codeSplitPane.setResizeWeight(0.6);
        codeSplitPane.setDividerLocation(0.6);
        codeSplitPane.setDividerSize(10);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.executeButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(this.showTableButton);
        JPanel consolePanel = this.createPanelWithLabel(this.consoleArea, "Console");
        consolePanel.setPreferredSize(new Dimension(this.getWidth(), (int)((double)this.getHeight() * 0.35)));
        JPanel codeAndButtonsPanel = new JPanel(new BorderLayout());
        codeAndButtonsPanel.add(codeSplitPane, "Center");
        codeAndButtonsPanel.add(buttonPanel, "South");
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(codeAndButtonsPanel, "Center");
        mainPanel.add(consolePanel, "South");
        this.add(mainPanel);
    }

    private JPanel createPanelWithLabel(JTextArea textArea, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(" " + title + " ");
        label.setFont(new Font("Arial", 1, 14));
        panel.setBorder(BorderFactory.createTitledBorder(label.getText()));
        panel.add(new JScrollPane(textArea), "Center");
        return panel;
    }

    private void showTableDialog() {
        JDialog dialog = new JDialog(this, "Tabela de Símbolos", true);
        dialog.setSize(900, 400);
        dialog.setLocationRelativeTo(this);
        JScrollPane scrollPane = new JScrollPane(this.table);
        dialog.add(scrollPane, "Center");
        dialog.setVisible(true);
    }

    private void executeProgram() {
        this.tableModel.setRowCount(0);
        this.consoleArea.setText("");
        this.generatedCodeArea.setText("");
        String input = this.textArea.getText();
        Lexico lexico = new Lexico();
        Sintatico sintatico = new Sintatico();
        Semantico semantico = new Semantico();
        lexico.setInput(input);

        try {
            sintatico.parse(lexico, semantico);

            for(Simbolo sim : semantico.getTabelaSimbolos().getListaSimbolos()) {
                this.populateTable(sim.getId(), sim.getTipo(), sim.getEscopo(), sim.getFlagInicializada(), sim.getFlagUsada(), sim.getFlagParametro(), sim.getFlagFuncao(), sim.getFlagVetor());
            }

            this.consoleArea.append("Compilado com sucesso\n");
            this.consoleArea.append(semantico.warnings);
            String dataSection = semantico.GeraDataSeccion();
            String codeSection = semantico.codigoGerado;
            this.generatedCodeArea.setText(semantico.getCodigoGerado());
        } catch (LexicalError e) {
            this.consoleArea.setText("Lexical Error: " + e.getMessage() + "\n");
        } catch (SyntacticError e) {
            this.consoleArea.setText("Syntatic Error: " + e.getMessage() + "\n");
        } catch (SemanticError e) {
            this.consoleArea.setText("Semantic Error: " + e.getMessage() + "\n");
        }

    }

    public void populateTable(String id, String tipo, int escopo, boolean inicializado, boolean utilizado, boolean parametro, boolean funcao, boolean vetor) {
        Object[] row = new Object[]{id, tipo, escopo, inicializado, utilizado, parametro, funcao, vetor};
        this.tableModel.addRow(row);
    }

    public static void main(String[] args) {
        new Compilador();
    }
}
