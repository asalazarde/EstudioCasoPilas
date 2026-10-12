import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Analiza expresiones aritméticas usando la clase Pila.
 * Pasos: separar en tokens, revisar paréntesis, pasar a postfija y evaluar.
 */
public class Analizador {

    // Variables numéricas que define el usuario desde el menú
    private final Map<String, Double> variables = new LinkedHashMap<>();

    public void definirVariable(String nombre, double valor) {
        if (!nombreValido(nombre)) {
            throw new IllegalArgumentException(
                    "El nombre debe empezar con una letra y solo llevar letras, números o _.");
        }
        variables.put(nombre, valor);
    }

    public Map<String, Double> getVariables() {
        return variables;
    }

    /**
     * Recorre la expresión carácter por carácter y arma la lista de tokens.
     * El signo menos se marca como "~" cuando es negativo y no una resta.
     */
    public List<Token> separarTokens(String expresion) {
        if (expresion == null || expresion.isBlank()) {
            throw new IllegalArgumentException("La expresión está vacía.");
        }
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expresion.length()) {
            char c = expresion.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                int inicio = i;
                int puntos = 0;
                while (i < expresion.length()
                        && (Character.isDigit(expresion.charAt(i)) || expresion.charAt(i) == '.')) {
                    if (expresion.charAt(i) == '.') {
                        puntos++;
                    }
                    i++;
                }
                String numero = expresion.substring(inicio, i);
                if (puntos > 1 || numero.equals(".")) {
                    throw new IllegalArgumentException("Número mal escrito: " + numero);
                }
                tokens.add(new Token(Token.Tipo.NUMERO, numero));
            } else if (Character.isLetter(c)) {
                int inicio = i;
                while (i < expresion.length() && (Character.isLetterOrDigit(expresion.charAt(i))
                        || expresion.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(new Token(Token.Tipo.VARIABLE, expresion.substring(inicio, i)));
            } else if (c == '(') {
                tokens.add(new Token(Token.Tipo.PARENTESIS_ABRE, "("));
                i++;
            } else if (c == ')') {
                tokens.add(new Token(Token.Tipo.PARENTESIS_CIERRA, ")"));
                i++;
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                // Es negativo si va al inicio, después de otro operador o después de "("
                boolean negativo = c == '-' && (tokens.isEmpty()
                        || tokens.get(tokens.size() - 1).getTipo() == Token.Tipo.OPERADOR
                        || tokens.get(tokens.size() - 1).getTipo() == Token.Tipo.PARENTESIS_ABRE);
                tokens.add(new Token(Token.Tipo.OPERADOR, negativo ? "~" : String.valueOf(c)));
                i++;
            } else {
                throw new IllegalArgumentException("Carácter no permitido: '" + c + "'");
            }
        }
        return tokens;
    }

    /**
     * Revisa que cada "(" tenga su ")". Cada apertura se apila y cada cierre
     * desapila una; si al final queda algo o falta algo, están desbalanceados.
     */
    public boolean parentesisBalanceados(List<Token> tokens) {
        Pila pila = new Pila();
        for (Token t : tokens) {
            if (t.getTipo() == Token.Tipo.PARENTESIS_ABRE) {
                pila.apilar(t);
            } else if (t.getTipo() == Token.Tipo.PARENTESIS_CIERRA) {
                if (pila.estaVacia()) {
                    return false;
                }
                pila.desapilar();
            }
        }
        return pila.estaVacia();
    }

    /**
     * Convierte la expresión de infija a postfija (algoritmo de Dijkstra).
     * Los operandos pasan directo a la salida y los operadores esperan en la pila
     * hasta que llegue uno de menor o igual prioridad o se cierre el paréntesis.
     */
    public List<Token> convertirAPostfija(List<Token> tokens, List<String> pasos) {
        validarOrden(tokens);
        if (!parentesisBalanceados(tokens)) {
            throw new IllegalArgumentException("Los paréntesis no están balanceados.");
        }
        Pila operadores = new Pila();
        List<Token> salida = new ArrayList<>();

        for (Token t : tokens) {
            switch (t.getTipo()) {
                case NUMERO:
                case VARIABLE:
                    salida.add(t);
                    break;
                case PARENTESIS_ABRE:
                    operadores.apilar(t);
                    break;
                case PARENTESIS_CIERRA:
                    while (operadores.verCima().getTipo() != Token.Tipo.PARENTESIS_ABRE) {
                        salida.add(operadores.desapilar());
                    }
                    operadores.desapilar(); // se descarta el "("
                    break;
                case OPERADOR:
                    // El negativo se agrupa de derecha a izquierda (- -3), por eso no saca a otro "~"
                    while (!operadores.estaVacia()
                            && operadores.verCima().getTipo() == Token.Tipo.OPERADOR
                            && !t.getTexto().equals("~")
                            && prioridad(operadores.verCima()) >= prioridad(t)) {
                        salida.add(operadores.desapilar());
                    }
                    operadores.apilar(t);
                    break;
            }
            if (pasos != null) {
                pasos.add(String.format("%-6s pila: %-22s salida: %s",
                        t, operadores.mostrar(), unir(salida)));
            }
        }
        while (!operadores.estaVacia()) {
            salida.add(operadores.desapilar());
        }
        if (pasos != null) {
            pasos.add(String.format("%-6s pila: %-22s salida: %s", "fin", operadores.mostrar(), unir(salida)));
        }
        return salida;
    }

    /**
     * Evalúa la expresión postfija. Los operandos se apilan; cada operador
     * desapila sus operandos, hace la cuenta y apila el resultado.
     */
    public double evaluarPostfija(List<Token> postfija, List<String> pasos) {
        Pila pila = new Pila();
        for (Token t : postfija) {
            if (t.esOperando()) {
                pila.apilar(new Token(Token.Tipo.NUMERO, formato(valorDe(t))));
            } else if (t.getTexto().equals("~")) {
                double a = pila.desapilar().getValor();
                pila.apilar(new Token(Token.Tipo.NUMERO, formato(-a)));
            } else {
                // Sale primero el operando derecho
                double b = pila.desapilar().getValor();
                double a = pila.desapilar().getValor();
                pila.apilar(new Token(Token.Tipo.NUMERO, formato(operar(a, b, t.getTexto()))));
            }
            if (pasos != null) {
                pasos.add(String.format("%-6s pila: %s", t, pila.mostrar()));
            }
        }
        return pila.desapilar().getValor();
    }

    // Hace todo el proceso de una vez y deja los pasos en las listas si no son null
    public double evaluar(String expresion, List<String> pasosConversion, List<String> pasosEvaluacion) {
        List<Token> postfija = convertirAPostfija(separarTokens(expresion), pasosConversion);
        return evaluarPostfija(postfija, pasosEvaluacion);
    }

    /**
     * Revisa que los tokens estén en un orden válido antes de usar las pilas,
     * por ejemplo que no haya dos números seguidos o un operador al final.
     */
    private void validarOrden(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("La expresión está vacía.");
        }
        boolean esperaOperando = true;
        for (Token t : tokens) {
            Token.Tipo tipo = t.getTipo();
            if (esperaOperando) {
                if (tipo == Token.Tipo.OPERADOR && !t.getTexto().equals("~")) {
                    throw new IllegalArgumentException("Falta un operando antes de '" + t + "'.");
                }
                if (tipo == Token.Tipo.PARENTESIS_CIERRA) {
                    throw new IllegalArgumentException("Hay un paréntesis vacío o un operador antes de ')'.");
                }
                if (t.esOperando()) {
                    esperaOperando = false;
                }
            } else {
                if (t.esOperando() || tipo == Token.Tipo.PARENTESIS_ABRE) {
                    throw new IllegalArgumentException("Falta un operador antes de '" + t + "'.");
                }
                if (tipo == Token.Tipo.OPERADOR) {
                    esperaOperando = true;
                }
            }
        }
        if (esperaOperando) {
            throw new IllegalArgumentException("La expresión termina sin un operando.");
        }
    }

    private double valorDe(Token t) {
        if (t.getTipo() == Token.Tipo.NUMERO) {
            return t.getValor();
        }
        Double valor = variables.get(t.getTexto());
        if (valor == null) {
            throw new IllegalArgumentException("La variable '" + t + "' no está definida.");
        }
        return valor;
    }

    private double operar(double a, double b, String operador) {
        switch (operador) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("No se puede dividir entre cero.");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Operador desconocido: " + operador);
        }
    }

    private int prioridad(Token operador) {
        switch (operador.getTexto()) {
            case "~": return 3;
            case "*":
            case "/": return 2;
            default: return 1;
        }
    }

    private boolean nombreValido(String nombre) {
        return nombre != null && nombre.matches("[A-Za-z][A-Za-z0-9_]*");
    }

    public static String unir(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(t);
        }
        return sb.toString();
    }

    // Muestra 4.0 como 4 y deja los decimales cuando hacen falta
    public static String formato(double valor) {
        if (valor == Math.rint(valor) && Math.abs(valor) < 1e15) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }
}
