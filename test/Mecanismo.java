

import reconocedorlenguaje.*;

public class Mecanismo {

    private int j;
    public Token tok = new Token();
    public String resultado;
    public boolean comentarioMultilineas = false;
    public boolean banderaAntecedente = false;

    public Mecanismo() {
    }

    // "$" SOLO SE USA AL FINAL DEL TODO, ESTE MARCA EL FINAL
    public String scanner(String cad) {//que la llamada a la funcion cad+'$'
        tok.setNom("");
        tok.setTipo("");
        int cantidadPuntos = 0;
        //String cad = cad_cand.get(i)+'\0';
        while (cad.charAt(j) == ' ') { // Ignorar espacios en blanco
            j++;
        }
        char c = cad.charAt(j);

        if (j >= cad.length()) {
            c = '$';  // fin de cadena
        }

        if (c == '\r' && cad.charAt(j + 1) == '\n') {
            System.out.println("Salto de linea");
            tok.setNom(tok.getNom() + "Salto de linea");
            tok.setTipo("L");//Linea
            j++;
            c = cad.charAt(j++);
        }

        if (c >= 'a' && c <= 'z') {   // Letra
            while ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                tok.setNom(tok.getNom() + c);
                j++;

                c = cad.charAt(j);
            }

            tok.setTipo("I");  // Identificador
        } else if (c >= '0' && c <= '9') {  // Numero  real
            while ((c >= '0' && c <= '9') || c == '.') {

                if (c == '.' && cantidadPuntos == 0) {
                    cantidadPuntos++;
                }
                //se guarda el valor
                tok.setNom(tok.getNom() + c);

                if (cantidadPuntos == 0) {
                    tok.setTipo("NE");  // Numero entero
                } else {
                    tok.setTipo("NR");  // Numero real
                }

                if (cantidadPuntos > 1) {
                    tok.setNom("");
                    tok.setTipo(""); //SE OCASIONA FALLO POR DOBLE "." EN NUMERO
                    break;
                }
                j++;
                c = cad.charAt(j);
            }

        } else if (c == ',' || c == '(' || c == ')' || c == '=' || c == '*' || c == '/' || c == '-' || c == '+'
                || c == '<' || c == '>' || c == ';' || c == '{' || c == '}' || c == '[' || c == ']' || c == '.') {  // Operador
            tok.setNom(String.valueOf(c));
            if ((c == '-' && cad.charAt(j + 1) == '-')
                    || (c == '+' && cad.charAt(j + 1) == '+')
                    || (c == '<' && cad.charAt(j + 1) == '=')
                    || (c == '>' && cad.charAt(j + 1) == '=')) {
                tok.setNom(tok.getNom() + cad.charAt(j + 1));
                j++;
            }
            j++;

            tok.setTipo("O");  // Operador
        } else if (c == '$') { // fin de cadena
            tok.setNom(String.valueOf(c));

        }
        return tok.getNom();

    }

    public void automata(String cadena) { //Se debe de llamar a este
        int q = 0;

        this.j = 0;

        while (q != 100 && q != -1) {
            if (this.banderaAntecedente == true) {
                this.comentarioMultilineas = true;
            }

            scanner(cadena);
            System.out.println(tok.nom);

            if (tok.nom.equalsIgnoreCase("/") && cadena.charAt(j) == '/') {  //para comentario lineal
                tok.setNom("$");
                tok.setTipo("");
                if (cadena.charAt(0) == '/') {
                    q = 100;
                }
            }
            if (tok.nom.equalsIgnoreCase("/") && scanner(cadena).equalsIgnoreCase("*")) { //para comentario multilinea

                while (!(tok.nom.equalsIgnoreCase("*") && cadena.charAt(j) == '/')) { //para cerrar
                    scanner(cadena);
                    System.out.println(tok.nom);
                    if (tok.nom.equalsIgnoreCase("$")) { //en caso no se llegase a encontrar "*/ en la misma linea"
                        this.banderaAntecedente = true;
                        if (cadena.charAt(0) == '/') {
                            q = 100;
                        }
                        break;
                    }
                }
                if (!tok.nom.equalsIgnoreCase("$")) {
                    scanner(cadena);
                    scanner(cadena);
                    if (cadena.charAt(0) == '/'&&tok.nom.equalsIgnoreCase("$")) {
                            q = 100;
                        }
                }
              
            } //aqui (EL IF DE ARRIBA) hacemos el caso de /*  */ en la misma linea y tambn cuando hay /* $ en una sola linea
            
            else {
                if (this.comentarioMultilineas == true) {
                    while (!(tok.nom.equalsIgnoreCase("*") && cadena.charAt(j) == '/')) { //para cerrar
                        scanner(cadena);

                        if (tok.nom.equalsIgnoreCase("$")) { //en caso no se llegase a encontrar "*/ en la misma linea"
                            this.banderaAntecedente = true;
                            break;
                          
                        }
                    }
                    if (!tok.nom.equalsIgnoreCase("$")) { //encontró el final
                        scanner(cadena);
                        scanner(cadena);
                        this.banderaAntecedente=false;
                        this.comentarioMultilineas=false;
                        if(tok.nom.equalsIgnoreCase("$")){
                            q=100;
                        }
                    }
                }
            }

            if (this.comentarioMultilineas == false) {
                switch (q) {

                    case 0: {
                        if (tok.nom.equalsIgnoreCase("entero") || tok.nom.equalsIgnoreCase("real")) {
                            q = 1;

                        } else {

                            q = -1;
                        }
                        break;
                    }
                    case 1: {
                        if (tok.tipo.equalsIgnoreCase("I")) {
                            q = 2;

                        } else {

                            q = -1;
                        }
                        break;
                    }
                    case 2: {
                        if (tok.nom.equalsIgnoreCase("$")) {
                            q = 100; //estado final

                        } else {
                            if (tok.nom.equalsIgnoreCase("=")) {
                                q = 3;
                            } else {
                                if (tok.nom.equalsIgnoreCase(",")) {
                                    q = 1;
                                } else {

                                    q = -1;
                                }
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (tok.tipo.equalsIgnoreCase("NE") || tok.tipo.equalsIgnoreCase("NR") || tok.tipo.equalsIgnoreCase("I")) {
                            q = 4;

                        } else {

                            q = -1;
                        }

                        break;
                    }
                    case 4: {
                        if (tok.nom.equalsIgnoreCase("$")) {
                            q = 100; //estado final
                        } else {
                            if (tok.nom.equalsIgnoreCase(",")) {
                                q = 1;
                            } else {

                                q = -1;
                            }
                        }

                        break;
                    }
                }
            }
        }
        if (q == 100) {
            resultado = "Reconoce";
        } else {
            resultado = "Error";
        }

    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getJ() {
        return j;
    }

}
