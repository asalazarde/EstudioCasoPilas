import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Programa principal. Muestra el menú para analizar expresiones aritméticas
 * y para probar las operaciones básicas de la pila.
 */
public class Main {

    private static final Scanner entrada = new Scanner(System.in);
    private static final Analizador analizador = new Analizador();
    // Pila aparte para el submenú de práctica, no se usa en los cálculos
    private static final Pila pilaPractica = new Pila();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   ANÁLISIS DE EXPRESIONES ARITMÉTICAS CON PILAS");
        System.out.println("=================================================");
        menu();
        entrada.close();
    }

    public static void menu() {
        int opcion;
        do {
            System.out.println();
            System.out.println("------------- MENÚ PRINCIPAL -------------");
            System.out.println("1. Definir una variable");
            System.out.println("2. Ver variables definidas");
            System.out.println("3. Evaluar una expresión");
            System.out.println("4. Evaluar una expresión paso a paso");
            System.out.println("5. Revisar paréntesis de una expresión");
            System.out.println("6. Probar las operaciones de la pila");
            System.out.println("7. Ayuda");
            System.out.println("0. Salir");
            opcion = leerOpcion();

            // Si algo sale mal en una opción se muestra el error y se vuelve al menú
            try {
                switch (opcion) {
                    case 1: definirVariable(); break;
                    case 2: verVariables(); break;
                    case 3: evaluar(false); break;
                    case 4: evaluar(true); break;
                    case 5: revisarParentesis(); break;
                    case 6: menuPila(); break;
                    case 7: ayuda(); break;
                    case 0: System.out.println("Programa finalizado."); break;
                    default: System.out.println("Opción no válida, intente de nuevo.");
                }
            } catch (IllegalArgumentException | IllegalStateException | ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void definirVariable() {
        System.out.print("Nombre de la variable: ");
        String nombre = entrada.nextLine().trim();
        double valor = leerNumero("Valor de " + nombre + ": ");
        analizador.definirVariable(nombre, valor);
        System.out.println("Variable guardada: " + nombre + " = " + Analizador.formato(valor));
    }

    private static void verVariables() {
        Map<String, Double> variables = analizador.getVariables();
        if (variables.isEmpty()) {
            System.out.println("No hay variables definidas.");
            return;
        }
        for (Map.Entry<String, Double> v : variables.entrySet()) {
            System.out.println("  " + v.getKey() + " = " + Analizador.formato(v.getValue()));
        }
    }

    private static void evaluar(boolean pasoAPaso) {
        System.out.print("Expresión: ");
        String expresion = entrada.nextLine();

        List<Token> tokens = analizador.separarTokens(expresion);
        System.out.println("Tokens: " + Analizador.unir(tokens));

        List<String> pasosConversion = pasoAPaso ? new ArrayList<>() : null;
        List<String> pasosEvaluacion = pasoAPaso ? new ArrayList<>() : null;
        List<Token> postfija = analizador.convertirAPostfija(tokens, pasosConversion);

        if (pasoAPaso) {
            System.out.println("\nConversión a postfija (la cima es el primer elemento):");
            imprimir(pasosConversion);
        }
        System.out.println("Postfija: " + Analizador.unir(postfija));

        double resultado = analizador.evaluarPostfija(postfija, pasosEvaluacion);
        if (pasoAPaso) {
            System.out.println("\nEvaluación de la postfija:");
            imprimir(pasosEvaluacion);
        }
        System.out.println("Resultado: " + Analizador.formato(resultado));
    }

    private static void revisarParentesis() {
        System.out.print("Expresión: ");
        List<Token> tokens = analizador.separarTokens(entrada.nextLine());
        if (analizador.parentesisBalanceados(tokens)) {
            System.out.println("Los paréntesis están balanceados.");
        } else {
            System.out.println("Los paréntesis NO están balanceados.");
        }
    }

    // Submenú para usar la pila directamente con tokens escritos por el usuario
    private static void menuPila() {
        int opcion;
        do {
            System.out.println();
            System.out.println("---------- OPERACIONES DE LA PILA ----------");
            System.out.println("Pila actual (cima a la izquierda): " + pilaPractica.mostrar());
            System.out.println("1. Apilar (push)");
            System.out.println("2. Desapilar (pop)");
            System.out.println("3. Ver cima (peek)");
            System.out.println("4. Ver tamaño y si está vacía");
            System.out.println("5. Vaciar la pila");
            System.out.println("0. Volver al menú principal");
            opcion = leerOpcion();
            try {
                switch (opcion) {
                    case 1:
                        System.out.print("Token a apilar (número, variable, operador o paréntesis): ");
                        List<Token> tokens = analizador.separarTokens(entrada.nextLine());
                        if (tokens.size() != 1) {
                            System.out.println("Escriba un solo token.");
                        } else {
                            pilaPractica.apilar(tokens.get(0));
                            System.out.println("Se apiló: " + tokens.get(0));
                        }
                        break;
                    case 2: System.out.println("Se desapiló: " + pilaPractica.desapilar()); break;
                    case 3: System.out.println("Cima: " + pilaPractica.verCima()); break;
                    case 4:
                        System.out.println("Tamaño: " + pilaPractica.getTamanio());
                        System.out.println("¿Está vacía?: " + (pilaPractica.estaVacia() ? "sí" : "no"));
                        break;
                    case 5: pilaPractica.vaciar(); System.out.println("La pila quedó vacía."); break;
                    case 0: break;
                    default: System.out.println("Opción no válida, intente de nuevo.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void ayuda() {
        System.out.println("Operadores permitidos: +  -  *  /  y paréntesis ( )");
        System.out.println("Se pueden usar números enteros o decimales con punto (2.5)");
        System.out.println("y variables definidas con la opción 1 (por ejemplo x, total, precio_1).");
        System.out.println("El signo negativo se muestra como ~ en la postfija.");
        System.out.println("Ejemplo: con x = 4, la expresión 3 + x * (2 - 5) da -9.");
    }

    private static void imprimir(List<String> lineas) {
        for (String linea : lineas) {
            System.out.println("  " + linea);
        }
    }

    // Lee una opción del menú; si no es un número devuelve -1
    private static int leerOpcion() {
        System.out.print("Seleccione una opción: ");
        if (!entrada.hasNextLine()) {
            return 0; // se terminó la entrada, se sale del programa
        }
        String texto = entrada.nextLine().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double leerNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            try {
                double valor = Double.parseDouble(texto);
                if (!Double.isNaN(valor) && !Double.isInfinite(valor)) {
                    return valor;
                }
                System.out.println("El valor debe ser un número finito.");
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número (use punto para decimales).");
            }
        }
    }
}
