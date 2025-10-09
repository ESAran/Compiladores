import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {

    private final Stack<Double> stack = new Stack<>();
    private final Stack<String> lvalues = new Stack<>();
    private final Map<String, Double> memory = new HashMap<>();

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 1: { // PUSH_INT
                stack.push(Double.parseDouble(token.getLexeme()));
                break;
            }
            case 2: { // PUSH_FLOAT
                stack.push(Double.parseDouble(token.getLexeme()));
                break;
            }
            case 3: { // PUSH_VAR (rvalue)
                String id = token.getLexeme();
                stack.push(memory.getOrDefault(id, 0.0));
                break;
            }
            case 4: { // PUSH_ID_LVALUE (para atribuição)
                lvalues.push(token.getLexeme());
                break;
            }
            case 5: { // ASSIGN
                ensureStack(1);
                if (lvalues.isEmpty()) throw new SemanticError("Lvalue ausente na atribuição");
                double value = stack.pop();
                String id = lvalues.pop();
                memory.put(id, value);
                break;
            }
            case 6: { // ADD
                double[] ab = pop2();
                stack.push(ab[0] + ab[1]);
                break;
            }
            case 7: { // SUB
                double[] ab = pop2();
                stack.push(ab[0] - ab[1]);
                break;
            }
            case 8: { // MUL
                double[] ab = pop2();
                stack.push(ab[0] * ab[1]);
                break;
            }
            case 9: { // DIV
                double[] ab = pop2();
                stack.push(ab[0] / ab[1]);
                break;
            }
            case 10: { // MOD
                double[] ab = pop2();
                stack.push(ab[0] % ab[1]);
                break;
            }
            case 11: { // POW
                double[] ab = pop2();
                stack.push(Math.pow(ab[0], ab[1]));
                break;
            }
            case 12: { // UPLUS
// no-op
                break;
            }
            case 13: { // UMINUS
                ensureStack(1);
                stack.push(-stack.pop());
                break;
            }
            case 14: { // PRINT (decimal)
                ensureStack(1);
                double v = stack.pop();
                if (isInt(v)) {
                    System.out.println((long) Math.rint(v));
                } else {
                    System.out.println(v);
                }
                break;
            }
            case 15: { // LOG (%%) -> log_a(b) = ln(b)/ln(a)
                double[] ab = pop2();
                double a = ab[0], b = ab[1];
                if (a <= 0.0 || a == 1.0 || b <= 0.0) {
                    throw new SemanticError("Argumentos inválidos em log: base>0, base!=1, valor>0");
                }
                stack.push(Math.log(b) / Math.log(a));
                break;
            }
            default:
                throw new SemanticError("Ação semântica desconhecida: " + action);
        }
    }

    private void ensureStack(int n) throws SemanticError {
        if (stack.size() < n) {
            throw new SemanticError("Pilha insuficiente para a operação (precisa de " + n + ")");
        }
    }

    private double[] pop2() throws SemanticError {
        ensureStack(2);
        double b = stack.pop();
        double a = stack.pop();
        return new double[]{a, b};
    }

    private boolean isInt(double v) {
        return Math.abs(v - Math.rint(v)) < 1e-9;
    }
}