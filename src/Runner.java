import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Runner {
    public static void main(String[] args) {
// Caminho fixo do arquivo a ser lido
        String caminho = "src/programa.txt";

        try {
// Lê todo o conteúdo do programa fonte
            String fonte = Files.readString(Paths.get(caminho));

// Instancia o analisador léxico, sintático e semântico
            Lexico lexico = new Lexico(fonte);
            Sintatico sintatico = new Sintatico();
            Semantico semantico = new Semantico();

// Faz a análise completa
            sintatico.parse(lexico, semantico);

            System.out.println("\n✅ Execução concluída com sucesso!");

        } catch (AnalysisError e) {
            System.err.println("\n❌ Erro de Análise: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("\n❌ Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ Erro inesperado: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
