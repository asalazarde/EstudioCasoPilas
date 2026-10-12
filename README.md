# Estudio de caso: análisis de expresiones aritméticas con pilas

Universidad CENFOTEC · SOFT-10 Estructuras de Datos · Sección SCV3 · C3-2026  
Estudiante: Aylin Salazar Delgado

Programa en Java que analiza y evalúa expresiones aritméticas usando una pila
implementada con nodos. Cada nodo guarda un token de la expresión (número,
variable, operador o paréntesis).

## Clases

| Clase | Descripción |
| --- | --- |
| `Token` | Pieza de la expresión con su tipo y su texto. |
| `Nodo` | Guarda un token y la referencia al nodo de abajo. |
| `Pila` | Pila enlazada: apilar, desapilar, verCima, estaVacia, getTamanio, vaciar y mostrar. |
| `Analizador` | Separa los tokens, revisa paréntesis, convierte a postfija y evalúa. |
| `Main` | Rutina `main()` y el menú del programa. |

## Cómo ejecutarlo

Se necesita el JDK 17 o superior. Desde la carpeta del proyecto:

```
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

En Windows, si las tildes se ven mal en la consola, se puede ejecutar antes `chcp 65001`.

## Menú

1. Definir una variable
2. Ver variables definidas
3. Evaluar una expresión
4. Evaluar una expresión paso a paso
5. Revisar paréntesis de una expresión
6. Probar las operaciones de la pila
7. Ayuda
0. Salir

Ejemplo: definir `x = 4` con la opción 1 y evaluar `3 + x * (2 - 5)` con la
opción 4. La postfija es `3 x 2 5 - * +` y el resultado es `-9`.

Se permiten números enteros y decimales con punto, variables, los operadores
`+ - * /`, paréntesis y el signo negativo (que aparece como `~` en la postfija).
