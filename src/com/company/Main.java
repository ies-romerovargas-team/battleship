package com.company;

import java.util.*;

public class Main {

    private static int nivel;
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN="\u001B[32m";
    private static final String ANSI_RED="\u001B[31m";
    private static final String ANSI_RESET="\u001B[0m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";

    public static void main(String[] args) {
        String[] menu = {"Partida Nueva", "Configurar Partida", "Ranking", "Ayuda"};
        Scanner sc = new Scanner(System.in);
        boolean salir = false;
        int opcion; //Guardamos la opcion del usuario
        nivel = 3;
        while (!salir) {
            imprimeMenu(menu, "Principal", ANSI_RED);
            try {
                System.out.print("Eliga opción: ");
                opcion = sc.nextInt();
                switch (opcion) {
                    case 1:
                        long tInicial = System.currentTimeMillis();
                        int[][] maquina = new int[8][8];
                        int[][] jugador = new int[8][8];
                        int[][] memoria = new int[8][8];
                        int[] numBarcos = {10, 10};
                        colocaFlota(maquina);   //azar
                        colocaFlota(jugador);   //azar
                        int resultado, turno = 1;
                        String jugada, ultimaJugada = "";
                        while(quedanBarcos(maquina) && quedanBarcos(jugador))
                        {
                            imprimeBarcos(maquina, jugador, memoria, numBarcos, true);
                            if(turno == 1) {
                                System.out.println("Turno para el JUGADOR... ");
                                System.out.println("Introduzca disparo tecleando coordenadas (A1): ");
                                jugada = sc.next();
                                resultado = disparaBarco(maquina, jugada);
                                if(resultado == 3) {
                                    numBarcos[0] = numBarcos[0] - 1;
                                }
                            }
                            else
                            {
                                System.out.println("                                           Turno para el ORDENADOR... ");
                                System.out.println();
                                jugada = inteligenciaArtificial(memoria, jugador, ultimaJugada);
                                pensando(jugada);
                                //System.out.print(jugada);
                                //a = sc.next();
                                resultado = disparaBarco(jugador, jugada);
                                if(resultado!=-1) {
                                    actualizaMemoria(memoria, jugada, resultado);
                                    if(resultado == 2)
                                    {
                                        ultimaJugada = jugada; // Sólo si es 2 (tocado) conserva la última jugada
                                    }
                                    if(resultado == 3) {
                                        numBarcos[1] = numBarcos[1] - 1;
                                    }
                                }
                            }
                            if(resultado == 1) turno *= -1; // Resultado = 'AGUA' ==> cambio de turno
                        }
                        System.out.print("    FIN DE LA PARTIDA");
                        if(numBarcos[0]==0) {
                            System.out.print(ANSI_GREEN + " HAS GANADO");
                            long tFinal = System.currentTimeMillis();
                            guardaRanking(numBarcos[1], tFinal - tInicial);
                        } else System.out.print(ANSI_RED + " HAS PERDIDO");
                        System.out.println(ANSI_RESET);

                        imprimeBarcos(maquina, jugador, memoria, numBarcos, true);
                        break;
                    case 2:
                        String[] submenu = {"Fácil", "Intermedio", "Experto"};
                        imprimeMenu(submenu, "Configura Partida", ANSI_GREEN);
                        System.out.print("Eliga opción: ");
                        nivel = sc.nextInt();
                        break;
                    case 3:
                        leeRanking();
                        break;
                    case 4:
                        imprimeAyuda();
                        break;
                    case 0:
                        salir = true;
                        break;
                    default:
                        System.out.println("ERROR: Opcion no válida");
                }
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Debes insertar un número");
                sc.next();
            }
        }
    }

    private static void guardaRanking(int puntuacion, long tiempo)
    {
        // Pendiente incluir escritura de archivos
    }

    private static void leeRanking()
    {
        // Pendiente incluir lectura de archivos
    }

    private static void actualizaMemoria(int[][] array, String jug, int res)
    {
        int c1 = (int) jug.charAt(0) - 65; // Letras
        int f1 = (int) jug.charAt(1) - 49;  // Números
        array[f1][c1] = res==1?1:2;
        if(nivel==3){
            if(res == 2 || res == 3)
            {
                // marcamos como "agua" (1) las casillas en las esquinas
                if(f1 > 0)
                {
                    if(c1 > 0)
                    {
                        array[f1 - 1][c1 - 1] = 1;
                    }
                }
                if(f1 < 7)
                {
                    if(c1 < 7)
                    {
                        array[f1 + 1][c1 + 1] = 1;
                    }
                }
                if(c1 > 0)
                {
                    if(f1 < 7)
                    {
                        array[f1 + 1][c1 - 1] = 1;
                    }
                }
                if(c1 < 7)
                {
                    if(f1 > 0)
                    {
                        array[f1 - 1][c1 + 1] = 1;
                    }
                }
            }
            if(res == 3)
            {
                // marcamos como "agua" (1) las casillas en adyacentes
                if(f1 > 0 && array[f1 - 1][c1] == 0)
                {
                    array[f1 - 1][c1] = 1;
                    int w = f1;
                    while(w <= 7 && array[w][c1] == 2){ w++;}
                    if(w <= 7 && array[w][c1] == 0) array[w][c1] = 1;
                }
                if(f1 < 7 && array[f1 + 1][c1] == 0)
                {
                    array[f1 + 1][c1] = 1;
                    int w = f1;
                    while(w >= 0 && array[w][c1] == 2){ w--;}
                    if(w >= 0 && array[w][c1] == 0) array[w][c1] = 1;
                }
                if(c1 > 0 && array[f1][c1 - 1] == 0)
                {
                    array[f1][c1 - 1] = 1;
                    int w = c1;
                    while(w <= 7 && array[f1][w] == 2){ w++;}
                    if(w <= 7 && array[f1][w] == 0) array[f1][w] = 1;
                }
                if(c1 < 7 && array[f1][c1 + 1] == 0)
                {
                    array[f1][c1 + 1] = 1;
                    int w = c1;
                    while(w >=0 && array[f1][w] == 2){ w--;}
                    if(w >= 0 && array[f1][w] == 0) array[f1][w] = 1;
                }
            }
        }
    }

    private static String inteligenciaArtificial(int[][]array, int[][] array2, String jug)
    {
        // NIVEL 1, 2, 3
        /* Nivel 1: Jugadas al azar
        Nivel 2: El ordenador centra ataques a barcos tocados
        Nivel 3: El ordenador tiene en cuenta, además los espacios no disponibles
        * */
        Random r = new Random();
        int col = 8;
        int fil = 8;
        int f1, c1;
        if(!jug.equals("")) {
            c1 = (int) jug.charAt(0) - 65; // Letras
            f1 = (int) jug.charAt(1) - 49;  // Números
            if(nivel > 1){
                // si ultimajugada coinciden con un Barco tocado, el siguiente disparo debe ser una casilla adyacente
                // pero si es hundido, no

                if(array[f1][c1] == 2 && !compruebaHundido(array2, f1, c1))
                {
                    if(f1 > 0 && array[f1 - 1][c1] == 2) // ¿la casilla superior = "tocado"?
                    {
                        // comprobamos hacia abajo
                        // hasta f1=7, encontremos 0 (no jugado)
                        int w = f1;
                        while(w <= 7 && array[w][c1] == 2)
                        {
                            w++;
                        }
                        // hemos recorrido todos los 2 hacia abajo, comprobamos que el siguiente no haya sido jugado
                        if(array[w][c1] == 0)
                        {
                            fil = w;
                            col = c1;
                        }
                    }
                    else if (f1 < 7 && array[f1 + 1][c1] == 2) // ¿la casilla inferior = "tocado"?
                    {
                        // comprobamos hacia arriba
                        // hasta f1=0, o encontremos 0 (no jugado)
                        int w = f1;
                        while(w >= 0 && array[w][c1] == 2)
                        {
                            w--;
                        }
                        // hemos recorrido todos los 2 hacia arriba, comprobamos que el siguiente no haya sido jugado
                        if(array[w][c1] == 0)
                        {
                            fil = w;
                            col = c1;
                        }
                    }
                    else if(c1 > 0 && array[f1][c1 - 1] == 2) // ¿está la casilla izquierda = "tocada"?
                    {
                        // comprobamos hacia la derecha
                        int w = c1;
                        while(w <= 7 && array[f1][w] == 2)
                        {
                            w++;
                        }
                        // hemos recorrido todos los 2 hacia la derecha, comprobamos que el siguiente no haya sido jugado
                        if(array[f1][w] == 0)
                        {
                            fil = f1;
                            col = w;
                        }
                    }
                    else if (c1 < 7 && array[f1][c1 + 1] == 2)  // ¿está la casilla de la derecha = "tocada"?
                    {
                        // comprobamos entonces hacia la izquierda
                        int w = c1;
                        while(w >= 0 && array[f1][w] == 2) // mmientras encontremos "tocadas" seguimos
                        {
                            w--;
                        }
                        // hemos recorrido todos los 2 hacia la izquierda, comprobamos que el siguiente no haya sido jugado
                        if(array[f1][w] == 0)
                        {
                            fil = f1;
                            col = w;
                        }
                    }
                    if (fil == 8 || col == 8)
                    {
                        // NO es posible detectar la dirección del barco, por lo que disparamos secuencialmente
                        // arriba >> derecha >> abajo >> izquierda >> azar
                        if(f1 > 0 && array[f1 - 1][c1] == 0) {
                            fil = f1 - 1;
                            col = c1;
                        } else if(c1 < 7 && array[f1][c1 + 1] == 0) {
                            col = c1 + 1;
                            fil = f1;
                        } else if(f1 < 7 && array[f1 + 1][c1] == 0) {
                            fil = f1 + 1;
                            col = c1;
                        } else if(c1 > 0 && array[f1][c1 - 1] == 0) {
                            fil = f1;
                            col = c1 - 1;
                        }
                    }
                }
            }
        }
        // Jugada al azar
        if (nivel == 1 || (fil == 8))
        {
            fil = r.nextInt(8); // VALORES DE 0 A 7
            col = r.nextInt(8); // VALORES DE 0 A 7
            while(array[fil][col] != 0)
            {
                fil = r.nextInt(8);
                col = r.nextInt(8);
            }
        }

        return (char)(col + 65) + Integer.toString(fil + 1);
    }

    private static void pensando(String jug) {
        System.out.print(jug);
        for (int i = 0; i < 6; i++) {
            try {
                Thread.sleep(500);
                System.out.print(" .");
            }
            catch (InterruptedException a){
                System.out.println(a.getMessage());
            }
        }
    }

    private static void colocaFlota(int[][] array)
    {
        // Todas las casillas a 0
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = 0;
            }
        }

        colocaBarcos(array, 4);
        //escribeArrayBi(array);
        colocaBarcos(array, 3);
        //escribeArrayBi(array);
        colocaBarcos(array, 3);
        //escribeArrayBi(array);
        colocaBarcos(array, 2);
        //escribeArrayBi(array);
        colocaBarcos(array, 2);
        //escribeArrayBi(array);
        colocaBarcos(array, 2);
        //escribeArrayBi(array);
        colocaBarcos(array, 1);
        //escribeArrayBi(array);
        colocaBarcos(array, 1);
        //escribeArrayBi(array);
        colocaBarcos(array, 1);
        //escribeArrayBi(array);
        colocaBarcos(array, 1);
        //escribeArrayBi(array);
        // elimina las marcas de adyacencia utilizadas para evitar colocar barcos pegados
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8 ; j++) {
                if(array[i][j] == 2) array[i][j] = 0;
            }
        }
    }

    private static void colocaBarcos(int[][] array, int tam)
    {
        Random r = new Random();
        int fil = r.nextInt(8); // VALORES DE 0 A 7
        int col = r.nextInt(8); // VALORES DE 0 A 7
        int dir = r.nextInt(4); // VALORES DE 0 A 3
        int cont = 0;
        while(noCabe(array, fil, col, dir, tam))
        {
            fil = r.nextInt(8);
            col = r.nextInt(8);
            dir = r.nextInt(3);
            cont++;
            if (cont==250){
                // Exceso de intentos
                // empezamos de nuevo
                colocaFlota(array);
                return;
            }
        }
        // generar barco y marca con el valor '2' las casillas adyacentes
        //System.out.println(fil +" "+ col + " " + tam + " " + dir);
        int w;
        switch (dir)
        {
            case 0: // Norte
                for (w = tam; w > 0; w--)
                {
                    array[fil - w + 1][col] = 1;
                    if(col > 0 && array[fil - w + 1][col - 1] == 0) array[fil - w + 1][col - 1] = 2;
                    if(col < 7 && array[fil - w + 1][col + 1] == 0) array[fil - w + 1][col + 1] = 2;
                }
                // marcar las casillas adyacentes por arriba para evitar colocar barcos ahí
                if(fil - tam >= 0) {
                    if(array[fil - tam][col] == 0) array[fil - tam][col] = 2;
                    if(col > 0 && array[fil - tam][col - 1] == 0) array[fil - tam][col - 1] = 2;
                    if(col < 7 && array[fil - tam][col + 1] == 0) array[fil - tam][col + 1] = 2;
                }
                // marcar las casillas adyacentes por debajo para evitar colocar barcos ahí
                if(fil + 1 <= 7) {
                    if(array[fil + 1][col] == 0) array[fil + 1][col] = 2;
                    if(col > 0 && array[fil + 1][col - 1] == 0) array[fil + 1][col - 1] = 2;
                    if(col < 7 && array[fil + 1][col + 1] == 0) array[fil + 1][col + 1] = 2;
                }
                break;
            case 1: // Este
                for (w = 0; w < tam; w++)
                {
                    array[fil][col + w] = 1;
                    if(fil > 0 && array[fil - 1][col + w] == 0) array[fil - 1][col + w] = 2;
                    if(fil < 7 && array[fil + 1][col + w] == 0) array[fil + 1][col + w] = 2;
                }
                // marcar las casillas adyacentes por la izquierda para evitar colocar barcos ahí
                if(col - 1 >= 0) {
                    if(array[fil][col - 1] == 0) array[fil][col - 1] = 2;
                    if(fil > 0 && array[fil - 1][col - 1] == 0) array[fil - 1][col - 1] = 2;
                    if(fil < 7 && array[fil + 1][col - 1] == 0) array[fil + 1][col - 1] = 2;
                }
                // marcar las casillas adyacentes por la derecha para evitar colocar barcos ahí
                if(col + tam <= 7) {
                    if(array[fil][col + tam] == 0) array[fil][col + tam] = 2;
                    if(fil > 0 && array[fil - 1][col + tam] == 0) array[fil - 1][col + tam] = 2;
                    if(fil < 7 && array[fil + 1][col + tam] == 0) array[fil + 1][col + tam] = 2;
                }
                break;
            case 2: // Sur
                for (w = 0; w < tam; w++)
                {
                    array[fil + w][col] = 1;
                    if(col > 0 && array[fil + w][col - 1] == 0) array[fil + w][col - 1] = 2;
                    if(col < 7 && array[fil + w][col + 1] == 0) array[fil + w][col + 1] = 2;
                }
                // marcar las casillas adyacentes por arriba para evitar colocar barcos ahí
                if(fil - 1 >= 0) {
                    if(array[fil - 1][col] == 0) array[fil - 1][col] = 2;
                    if(col > 0 && array[fil - 1][col - 1] == 0) array[fil - 1][col - 1] = 2;
                    if(col < 7 && array[fil - 1][col + 1] == 0) array[fil - 1][col + 1] = 2;
                }
                // marcar las casillas adyacentes por debajo para evitar colocar barcos ahí
                if(fil + tam <= 7) {
                    if(array[fil + tam][col] == 0) array[fil + tam][col] = 2;
                    if(col > 0 && array[fil + tam][col - 1] == 0) array[fil + tam][col - 1] = 2;
                    if(col < 7 && array[fil + tam][col + 1] == 0) array[fil + tam][col + 1] = 2;
                }
                break;
            case 3: // West
                for (w = 0; w < tam; w++)
                {
                    array[fil][col - w] = 1;
                    if(fil > 0 && array[fil - 1][col - w] == 0) array[fil - 1][col - w] = 2;
                    if(fil < 7 && array[fil + 1][col - w] == 0) array[fil + 1][col - w] = 2;
                }
                // marcar las casillas adyacentes a la izquierda para evitar colocar barcos ahí
                if(col - tam >= 0) {
                    if(array[fil][col - tam] == 0) array[fil][col - tam] = 2;
                    if(fil > 0 && array[fil - 1][col - tam] == 0) array[fil - 1][col - tam] = 2;
                    if(fil < 7 && array[fil + 1][col - tam] == 0) array[fil + 1][col - tam] = 2;
                }
                // marcar las casillas adyacentes a la derecha para evitar colocar barcos ahí
                if(col + 1 <= 7) {
                    if(array[fil][col + 1] == 0) array[fil][col + 1] = 2;
                    if(fil > 0 && array[fil - 1][col + 1] == 0) array[fil - 1][col + 1] = 2;
                    if(fil < 7 && array[fil + 1][col + 1] == 0) array[fil + 1][col + 1] = 2;
                }
                break;
        }
    }

    private static boolean noCabe(int[][] array, int fil, int col, int dir, int tam)
    {
        if(tam == 1) {
            return array[fil][col] != 0;
        }
        switch (dir)
        {
            case 0: // norte
                if (fil - tam <= 0) return true;
                for (int i = 0; i < tam; i++)
                {
                    if(array[fil - i][col] != 0) return true;
                }
                break;
            case 1: //este
                if (col + tam >= 7) return true;
                for (int i = 0; i < tam; i++)
                {
                    if(array[fil][col + i] != 0) return true;
                }
                break;
            case 2: // sur
                if(fil + tam >= 7) return true;
                for (int i = 0; i < tam; i++)
                {
                    if(array[fil + i][col] != 0) return true;
                }
                break;
            case 3: //west
                if(col - tam <= 0) return true;
                for (int i = tam; i > 0; i--)
                {
                    if(array[fil][col - i] != 0) return true;
                }
                break;
        }
        return false;
    }

    private static int disparaBarco(int[][] array, String a)
    {
        // efecto visual desplazamiento de lineas de consola
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
        if(a.length()!=2)
        {
            System.out.println(ANSI_RED + "    ENTRADA NO VÁLIDA" + ANSI_RESET);
            return -1;
        }
        a = a.toUpperCase();
        int col = (int) a.charAt(0) - 65; // Letras
        int fil = (int) a.charAt(1) - 49;  // Números
        int valor;
        if (col < 0 || col > 7 || fil < 0 || fil > 7)
        {
            System.out.println(ANSI_RED + "    COORDENADAS INCORRECTAS" + ANSI_RESET);
            return -1;
        }
        else {
            valor = array[fil][col]; // Recogemos el valor de la casilla elegida
            if (valor == 0) {
                array[fil][col] = 100; // Marcamos la casilla como "agua"
                System.out.println(ANSI_GREEN + "    AGUA" + ANSI_RESET);
            }
            if (valor == 1) {
                array[fil][col] = 101;  // Marcamos la casilla como "tocado"
                if (compruebaHundido(array, fil, col)) {
                    System.out.println(ANSI_RED + "    TOCADO Y HUNDIDO" + ANSI_RESET);
                    valor = 2;
                } else {
                    System.out.println(ANSI_RED + "    TOCADO" + ANSI_RESET);
                }
            }
            valor = valor + 1;   // 1:Agua || 2:Tocado
        }
        return valor;
    }

    private static boolean compruebaHundido(int[][] array, int fil, int col)
    {
        /* Esta función simplemente comprueba que no queda ningún trozo de barco (=1) alrededor de la casilla enviada
        En el momento en que encuentre algun 1, devuelve false
        */
        // hacia arriba desde fil
        if(fil > 0)
        {
            // superior
            int w = fil;
            while(w > 0 && array[w][col] == 101)
            {
                w--;
                if (array[w][col] == 1) return false;
            }
        }
        if(fil<7)
        {
            // inferior
            int w = fil;
            while(w < 7 && array[w][col] == 101)
            {
                w++;
                if (array[w][col] == 1) return false;
            }
        }
        if(col > 0)
        {
            // izquierda
            int w = col;
            while(w > 0 && array[fil][w] == 101)
            {
                w--;
                if (array[fil][w] == 1) return false;
            }
        }
        if(col < 7)
        {
            // derecha
            int w = col;
            while(w < 7 && array[fil][w] == 101)
            {
                w++;
                if (array[fil][w] == 1) return false;
            }
        }
        return true;
    }

    private static void imprimeBarcos(int[][] array1, int[][] array2, int[][] array3, int[] contador, boolean verProceso)
    {
        //escribeArrayBi(array1);
        //escribeArrayBi(array2);
        //escribeArrayBi(array3);
        int i, j;
        System.out.print("   Nivel: " + ANSI_BLUE + nivel + ANSI_RESET);
        System.out.println("      Quedan: " + ANSI_BLUE + contador[0] + ANSI_RESET + " barcos                     Quedan: "+ ANSI_BLUE + contador[1] + ANSI_RESET + " barcos");
        System.out.print("   A    B   C   D   E   F   G   H       A    B   C   D   E   F   G   H  ");//     A    B   C   D   E   F   G   H");
        if(verProceso){
            System.out.println("      A    B   C   D   E   F   G   H");
        } else System.out.println();
        System.out.print("  ┌───┬───┬───┬───┬───┬───┬───┬───┐    ┌───┬───┬───┬───┬───┬───┬───┬───┐");//   ┌───┬───┬───┬───┬───┬───┬───┬───┐");
        if(verProceso){
            System.out.println("    ┌───┬───┬───┬───┬───┬───┬───┬───┐");
        } else System.out.println();
        for(i = 0; i < 8; i++)
        {
            System.out.print(i + 1 + " │");
            for(j = 0; j < 8; j++)
            {
                switch (array1[i][j])
                {
                    case 100:
                        System.out.print(" " + ANSI_BLUE + ANSI_CYAN_BACKGROUND + "·" + ANSI_RESET + " │");
                        break;
                    case 101:
                        System.out.print(ANSI_RED + " x" + ANSI_RESET + " │");
                        break;
                    default:
                        System.out.print("   │");
                }
            }

            System.out.print("  " + (i + 1) + " │");
            for(j = 0; j < 8; j++)
            {
                switch (array2[i][j])
                {
                    case 1:
                        System.out.print(ANSI_BLUE + " x" + ANSI_RESET + " │");
                        break;
                    case 100:
                        System.out.print(" " + ANSI_BLUE + ANSI_CYAN_BACKGROUND + "·" + ANSI_RESET + " │");
                        break;
                    case 101:
                        System.out.print(ANSI_RED + " x" + ANSI_RESET + " │");
                        break;
                    default:
                        System.out.print("   │");
                }
            }

if(verProceso) {
    System.out.print("  " + (i + 1) + " │");
    for (j = 0; j < 8; j++) {
        switch (array3[i][j]) {
            case 3:
                System.out.print(ANSI_BLUE + " x" + ANSI_RESET + " │");
                break;
            case 2:
                System.out.print(ANSI_BLUE + " x" + ANSI_RESET + " │");
                break;
            case 1:
                System.out.print(" " + ANSI_BLUE + ANSI_CYAN_BACKGROUND + "·" + ANSI_RESET + " │");
                break;
            default:
                System.out.print("   │");
        }
    }
}
            System.out.println();
            if(i < 7)
            {
                System.out.print("  ├───┼───┼───┼───┼───┼───┼───┼───┤    ├───┼───┼───┼───┼───┼───┼───┼───┤  ");//  ├───┼───┼───┼───┼───┼───┼───┼───┤");
                if(verProceso){
                    System.out.println("  ├───┼───┼───┼───┼───┼───┼───┼───┤");
                } else System.out.println();
            }
            else
            {
                System.out.print("  └───┴───┴───┴───┴───┴───┴───┴───┘    └───┴───┴───┴───┴───┴───┴───┴───┘  ");//  └───┴───┴───┴───┴───┴───┴───┴───┘");
                if(verProceso){
                    System.out.println("  └───┴───┴───┴───┴───┴───┴───┴───┘");
                } else System.out.println();
            }
        }
        /*Scanner sc = new Scanner(System.in);
        System.out.println("Introduce una letra + INTRO para continuar");
        String a = sc.next();*/
    }

    private static boolean quedanBarcos(int[][] array)
    {
        int i, j;
        for(i = 0; i < 8; i++)
        {
            for(j = 0; j < 8; j++)
            {
                if(array[i][j] == 1)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void escribeArrayBi(int[][] array)
    {
        // Esta función escribe correctamente tabulados los valores comprendidos entre -99 y 999
        int i, j;
        int numFilas = array.length;
        int numCols = array[0].length;
        for (i = 0; i < numFilas; i++)
        {
            for (j = 0; j < numCols; j++)
            {
                if (i == 0 && j == 0) // primera fila
                {
                    System.out.print("╔");
                }
                else if (i == numFilas - 1 && j == 0) // ultima fila
                {
                    System.out.print("╚");
                }
                else if (j == 0)
                {
                    System.out.print("║");
                }
                // asumimos un máximo de 3 espacios para cada número
                if(array[i][j]<100 && array[i][j]> -100) System.out.print(" ");
                if(array[i][j]<10 && array[i][j]> -10) System.out.print(" ");
                if(array[i][j]>=0) System.out.print(" ");
                System.out.print(array[i][j]);
                System.out.print(" ");

                if (i == 0 && j == numCols - 1) // ultima fila
                {
                    System.out.print("╗");
                }
                else if (i == numFilas - 1 && j == numCols - 1) // ultima fila
                {
                    System.out.print("╝");
                }
                else if (j == numCols - 1)
                {
                    System.out.print("║");
                }
            }
            System.out.println();
        }
    }

    public static void imprimeMenu(String[] opciones, String titulo, String color)
    {
        System.out.println(color +"╔══════════════════════════════════╗");
        System.out.println("║              M E N U             ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.print(color + "║" + ANSI_RESET);
        System.out.print(ANSI_BLACK + ANSI_WHITE_BACKGROUND + titulo);
        for (int j = 0; j < 34 - titulo.length(); j++) {
            System.out.print(" ");
        }
        System.out.print(ANSI_RESET + color + "║" + ANSI_RESET);
        System.out.println();
        for (int i = 1; i <= opciones.length; i++)
        {
            System.out.print(color + "║" + ANSI_RESET);
            if(i < 10) System.out.print(" ");
            System.out.print(i + ". " + opciones[i-1]);
            for (int j = 0; j < 30 - opciones[i-1].length(); j++) {
                System.out.print(" ");
            }
            System.out.print(color + "║" + ANSI_RESET);
            System.out.println();
        }
        System.out.println(color + "╠══════════════════════════════════╣");
        //System.out.print("\u001B[101m" + "\u001B[97m");
        System.out.println("║ " + ANSI_RESET + "0. Salir                         " + color + "║");
        //System.out.println("\u001B[0m");
        System.out.println("╚══════════════════════════════════╝" + ANSI_RESET);
    }

    private static void imprimeAyuda()
    {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("          ## Bienvenidos al popular juego de "+ ANSI_BLUE +"Hundir la Flota" + ANSI_RESET + " ##");
        System.out.println();
        System.out.println("   El objetivo del juego es conseguir localizar los barcos enemigos antes de que");
        System.out.println("el ordenador encuentre los tuyos");
        System.out.println();
        System.out.println("   Pulsa  en el  menú principal  la opción 1 para comenzar una nueva partida. La ");
        System.out.println("colocación  de  los  barcos  es  automática. Después  tienes  que \"disparar\"  por");
        System.out.println("turnos  sobre la  columna y fila en la  que crees  que hay un  barco  enemigo ");
        System.out.println("utilizando  el sistema  de coordenadas letra-número  (A1, B5, etc, por ejemplo...)");
        System.out.println();
        System.out.println("   Si alcanzas un barco enemigo, verás el mensaje " + ANSI_RED + "TOCADO" + ANSI_RESET + " o en su caso,");
        System.out.println( ANSI_RED + "TOCADO Y HUNDIDO" + ANSI_RESET +" conservando entonces tu turno en el juego.");
        System.out.println();
        System.out.println("    Si en la casilla seleccionada no hay ningún barco, verás el mensaje: " + ANSI_GREEN + "AGUA" + ANSI_RESET +" y ");
        System.out.println("pasarás el turno al PC para que pueda atacarte.");
        System.out.println();
        System.out.println("    Los barcos existentes al inicio del juego son los siguientes:");
        System.out.println("    - 1 Portaaviones (ocupa 4 casillas)");
        System.out.println("    - 2 Acorazados (ocupan 3 casillas)");
        System.out.println("    - 3 Destructores (ocupan 2 casillas)");
        System.out.println("    - 4 Fragatas (ocupan 1 casilla)");
        System.out.println();
        System.out.println("    Ten en cuenta que los barcos no pueden tocarse entre sí, ni siquiera en sus esquinas");
        System.out.println();
        System.out.println("Puedes elegir previamente el nivel de juego, para conseguir que tu contrincante sea ");
        System.out.println("más o menos inteligente.");
        System.out.println();
        System.out.println("En el nivel Fácil, el PC sólo hará disparos al azar. En el nivel Intermedio, ");
        System.out.println("centrará los disparos sobre el barco que haya sido \"tocado\". Por último, en ");
        System.out.println("el nivel Experto, el PC descartará disparar sobre casillas adyacentes");
        System.out.println();
        System.out.println("Se conservan los 10 mejores resultados en cada nivel. Puedes consultar la tabla ");
        System.out.println("de resultados en la opción 3 del menú principal");
        System.out.println();
        System.out.println();
        System.out.println("© 2020 || David Bermúdez");
        System.out.println();
        System.out.println();
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce una letra + INTRO para regresar al menú principal");
        String a = sc.next();
    }
}
