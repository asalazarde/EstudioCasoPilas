/**
 * Representa una pieza de la expresión: un número, una variable,
 * un operador o un paréntesis. Es el dato que se guarda en cada nodo.
 */
public class Token {

    public enum Tipo { NUMERO, VARIABLE, OPERADOR, PARENTESIS_ABRE, PARENTESIS_CIERRA }

    private final Tipo tipo;
    private final String texto;

    public Token(Tipo tipo, String texto) {
        this.tipo = tipo;
        this.texto = texto;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getTexto() {
        return texto;
    }

    public boolean esOperando() {
        return tipo == Tipo.NUMERO || tipo == Tipo.VARIABLE;
    }

    // Solo tiene sentido en los tokens de tipo NUMERO
    public double getValor() {
        return Double.parseDouble(texto);
    }

    @Override
    public String toString() {
        return texto;
    }
}
