/**
 * Pila implementada con nodos enlazados (LIFO).
 * Todas las operaciones trabajan sobre la cima.
 */
public class Pila {

    private Nodo cima;
    private int tamanio;

    public Pila() {
        cima = null;
        tamanio = 0;
    }

    // push: el nuevo nodo queda encima del que era la cima
    public void apilar(Token token) {
        if (token == null) {
            throw new IllegalArgumentException("No se puede apilar un token vacío.");
        }
        Nodo nuevo = new Nodo(token);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamanio++;
    }

    // pop: saca el token de la cima y lo devuelve
    public Token desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía.");
        }
        Token token = cima.getToken();
        cima = cima.getSiguiente();
        tamanio--;
        return token;
    }

    // peek: devuelve la cima sin sacarla
    public Token verCima() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía.");
        }
        return cima.getToken();
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void vaciar() {
        cima = null;
        tamanio = 0;
    }

    // Muestra el contenido desde la cima hasta el fondo
    public String mostrar() {
        if (estaVacia()) {
            return "[ ]";
        }
        StringBuilder sb = new StringBuilder("[ ");
        Nodo actual = cima;
        while (actual != null) {
            sb.append(actual.getToken());
            if (actual.getSiguiente() != null) {
                sb.append(" | ");
            }
            actual = actual.getSiguiente();
        }
        return sb.append(" ]").toString();
    }
}
