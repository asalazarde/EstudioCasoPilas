/**
 * Nodo de la pila. Guarda un token y la referencia al nodo que está debajo.
 */
public class Nodo {

    private Token token;
    private Nodo siguiente;

    public Nodo(Token token) {
        this.token = token;
        this.siguiente = null;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}
