package reconocedorlenguaje;

import extra.ObjectInt;
import java.util.ArrayList;

public class Mecanismo {

    private int j;
    private String cadenaActual;
    public Token tok = new Token();
    public String resultado;
    public boolean comentarioMultilineas = false;
    public boolean banderaAntecedente = false;
    public ArrayList<String> cad_tok = new ArrayList();
    int a;

    //para el LL1
    String[] SD3 = {"+", "-"};
    String[] SD4 = {"$", ")"};
    String[] SD5 = {"+"};
    String[] SD6 = {"-"};
    String[] SD8 = {"*", "/"};
    String[] SD9 = {"+", "-", "$", ")"};
    String[] SD10 = {"*"};
    String[] SD11 = {"/"};
    String[] SD12 = {"("};
    String[] SD13 = {"-"};
    String[] SD14 = {"ID", "NE", "NR"};  //estos 3 valores son equivalentes a "ID" o "numero"

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

        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {   // Letra
            c = cad.toLowerCase().charAt(j);
            while ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                tok.setNom(tok.getNom() + c);
                j++;

                c = cad.charAt(j);
            }

            tok.setTipo("ID");  // Identificador

        } else if (c >= '0' && c <= '9') {
            while ((c >= '0' && c <= '9') || c == '.') {

                if (c == '.') {
                    cantidadPuntos++;
                }
                //se guarda el valor
                tok.setNom(tok.getNom() + c);

                if (cantidadPuntos == 0) {
                    tok.setTipo("NE");  // Numero entero
                } else {
                    tok.setTipo("NR");  // Numero real
                }
                j++;
                c = cad.charAt(j);
            }

            if (cantidadPuntos > 1) {
                tok.setNom("");
                tok.setTipo(""); //SE OCASIONA FALLO POR DOBLE "." EN NUMERO
            }
        } else if (c == ',' || c == '=' || c == '*' || c == '/' || c == '-' || c == '+'
                || c == '<' || c == '>' || c == '.' || c == '(' || c == ')') {  // Operador
            tok.setNom(String.valueOf(c));
            if ((c == '<' && cad.charAt(j + 1) == '<')
                    || (c == '>' && cad.charAt(j + 1) == '>')) {
                tok.setNom(tok.getNom() + cad.charAt(j + 1));
                j++;
            }
            j++;

            tok.setTipo("OP");  // Operador
        } else if (c == '$') { // fin de cadena
            tok.setNom(String.valueOf(c));

        }
        return tok.getNom();

    }

    //para comentarios lineales y multilineales
    public void comentarios(ObjectInt q, String cadena) {
        if (this.banderaAntecedente == true) {
            this.comentarioMultilineas = true;
        }
        if (tok.nom.equalsIgnoreCase("/") && cadena.charAt(j) == '/') {  //para comentario lineal
            tok.setNom("$");
            tok.setTipo("");
            if (cadena.charAt(0) == '/') {
                q.numero = 100;
            }
        }
        if (tok.nom.equalsIgnoreCase("/") && cadena.charAt(j) == '*') { //para comentario multilinea
            j++;
            while (!(tok.nom.equalsIgnoreCase("*") && cadena.charAt(j) == '/')) { //para cerrar
                scanner(cadena);
                System.out.println(tok.nom);
                if (tok.nom.equalsIgnoreCase("$")) { //en caso no se llegase a encontrar "*/ en la misma linea"
                    this.banderaAntecedente = true;
                    if (cadena.charAt(0) == '/') {
                        q.numero = 100;
                    }
                    break;
                }
            }
            if (!tok.nom.equalsIgnoreCase("$")) {
                scanner(cadena);
                scanner(cadena);
                if (cadena.charAt(0) == '/' && tok.nom.equalsIgnoreCase("$")) {
                    q.numero = 100;
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
                    this.banderaAntecedente = false;
                    this.comentarioMultilineas = false;
                    if (tok.nom.equalsIgnoreCase("$")) {
                        q.numero = 100;
                    }
                }
            }
        }
    }

    //PARA PARSER LL1 *******************************************************************************************************************************
    private int errorLL1 = 0;

    public void S() {
        E();
        if (tok.nom.equalsIgnoreCase("$")) {
        } else {
            System.out.println(this.tok.nom);
            this.errorLL1++;
        }
    }

    public void E() {
        T();
        W();
    }

    public void T() {
        F();
        R();
    }

    public void W() {
        if (tok.nom.equalsIgnoreCase(SD3[0]) || tok.nom.equalsIgnoreCase(SD3[1])) {
            X();
            W();
        } else {
            if (tok.nom.equalsIgnoreCase(SD4[0]) || tok.nom.equalsIgnoreCase(SD4[1])) {
                //lambda
            } else {
                this.errorLL1++;
            }
        }

    }

    public void X() {
        if (tok.nom.equalsIgnoreCase(SD5[0])) {
            scanner(this.cadenaActual);
            T();
        } else {
            if (tok.nom.equalsIgnoreCase(SD6[0])) {
                scanner(this.cadenaActual);
                T();
            } else {
                this.errorLL1++;
            }
        }

    }

    public void R() {
        if (tok.nom.equalsIgnoreCase(SD8[0]) || tok.nom.equalsIgnoreCase(SD8[1])) {
            Y();
            R();
        } else {
            if (tok.nom.equalsIgnoreCase(SD9[0]) || tok.nom.equalsIgnoreCase(SD9[1]) || tok.nom.equalsIgnoreCase(SD9[2]) || tok.nom.equalsIgnoreCase(SD9[3])) {
                //lambda
            } else {
                this.errorLL1++;
            }
        }

    }

    public void F() {
        if (tok.nom.equalsIgnoreCase(SD12[0])) {
            scanner(this.cadenaActual);
            E();
            if (tok.nom.equalsIgnoreCase(")")) {
                scanner(this.cadenaActual);
            } else {
                this.errorLL1++;
            }
        } else {

            if (tok.nom.equalsIgnoreCase(SD13[0])) {
                scanner(this.cadenaActual);
                F();
            } else {
                if (tok.tipo.equalsIgnoreCase(SD14[0]) || tok.tipo.equalsIgnoreCase(SD14[1]) || tok.tipo.equalsIgnoreCase(SD14[2])) {
                    scanner(this.cadenaActual);
                } else {
                    this.errorLL1++;
                }
            }
        }
    }

    public void Y() {
        if (tok.nom.equalsIgnoreCase(SD10[0])) {
            scanner(this.cadenaActual);
            F();
        } else {
            if (tok.nom.equalsIgnoreCase(SD11[0])) {
                scanner(this.cadenaActual);
                F();
            } else {
                this.errorLL1++;
            }
        }

    }
       public int parserLL1(String cadena) {
        this.cadenaActual=cadena;
        this.errorLL1=0;
        S();
        if (this.errorLL1 > 0) {
            return -1;
        } else {
            return 100;
        }

    }
    public void parser(String cadena) { //En el parametro va la cadena de cada linea
//        int q = 0;
        ObjectInt q = new ObjectInt(0);
        this.j = 0;
        this.cadenaActual=cadena;
        while (q.numero != 100 && q.numero != -1) {

            scanner(cadena);
            System.out.println(tok.nom);
            cad_tok.add(tok.nom);

            comentarios(q, cadena);

            if (this.comentarioMultilineas == false) {
                switch (q.numero) {

                    case 0:
                        if (tok.nom.equalsIgnoreCase("entero") || tok.nom.equalsIgnoreCase("real")) {
                            q.numero = 1;
                        } else if (tok.nom.equalsIgnoreCase("lee")) {
                            q.numero = 5;

                        } else if (tok.nom.equalsIgnoreCase("escribe")) {
                            q.numero = 8;

                        } else if (tok.tipo.equalsIgnoreCase("ID")) {
                            q.numero = 11;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 1:
                        if (tok.tipo.equalsIgnoreCase("ID")) {
                            q.numero = 2;

                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 2:
                        if (tok.nom.equalsIgnoreCase("$")) {
                            q.numero = 100; //estado final

                        } else {
                            if (tok.nom.equalsIgnoreCase("=")) {
                                q.numero = 3;
                            } else {
                                if (tok.nom.equalsIgnoreCase(",")) {
                                    q.numero = 1;
                                } else {

                                    q.numero = -1;
                                }
                            }
                        }
                        break;

                    case 3:
                        if (tok.tipo.equalsIgnoreCase("NE") || tok.tipo.equalsIgnoreCase("NR") || tok.tipo.equalsIgnoreCase("ID")) {
                            q.numero = 4;

                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 4:
                        if (tok.nom.equalsIgnoreCase("$")) {
                            q.numero = 100; //estado final
                        } else {
                            if (tok.nom.equalsIgnoreCase(",")) {
                                q.numero = 1;
                            } else {
                                q.numero = -1;
                            }
                        }
                        break;
                    case 5:
                        if (tok.nom.equalsIgnoreCase(">>")) {
                            q.numero = 6;

                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 6:
                        if (tok.tipo.equalsIgnoreCase("ID")) {
                            q.numero = 7;

                        } else {

                            q.numero = -1;
                        }
                        break;

                    case 7:
                        if (tok.nom.equalsIgnoreCase("$")) {
                            q.numero = 100;

                        } else if (tok.nom.equalsIgnoreCase(">>")) {
                            q.numero = 6;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 8:
                        if (tok.nom.equalsIgnoreCase("<<")) {
                            q.numero = 9;

                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 9:
                        if (tok.tipo.equalsIgnoreCase("ID")) {
                            q.numero = 10;

                        } else {

                            q.numero = -1;
                        }
                        break;

                    case 10:
                        if (tok.nom.equalsIgnoreCase("$")) {
                            q.numero = 100;

                        } else if (tok.nom.equalsIgnoreCase("<<")) {
                            q.numero = 9;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 11:
                        if (tok.nom.equalsIgnoreCase("=")) {
                            q.numero = 12;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 12:
//                        if (tok.tipo.equalsIgnoreCase("ID")) {
//                            q.numero = 13;
//                        } else if (tok.tipo.equalsIgnoreCase("NE") || tok.tipo.equalsIgnoreCase("NR")) {
//                            q.numero = 16;
//
//                        } else {
//                            q.numero = -1;
//                        }
                        q.numero=parserLL1(cadena);
                        break;

//                    case 13:
//                        if (tok.nom.equalsIgnoreCase("+") || tok.nom.equalsIgnoreCase("-") || tok.nom.equalsIgnoreCase("*") || tok.nom.equalsIgnoreCase("/")) {
//                            q.numero = 14;
//                        } else if (tok.nom.equalsIgnoreCase("$")) {
//                            q.numero = 100; //estado final
//                        } else {
//                            q.numero = -1;
//                        }
//                        break;

//                    case 14:
//                        if (tok.tipo.equalsIgnoreCase("ID") || tok.tipo.equalsIgnoreCase("NE") || tok.tipo.equalsIgnoreCase("NR")) {
//                            q.numero = 15;
//                        } else {
//                            q.numero = -1;
//                        }
//                        break;
//
//                    case 15:
//                        if (tok.nom.equalsIgnoreCase("$")) {
//                            q.numero = 100; //estado final                   
//                        } else {
//                            q.numero = -1;
//                        }
//                        break;

//                    case 16:
//                        if (tok.nom.equalsIgnoreCase("+") || tok.nom.equalsIgnoreCase("-") || tok.nom.equalsIgnoreCase("*") || tok.nom.equalsIgnoreCase("/")) {
//                            q.numero = 17;
//                        } else if (tok.nom.equalsIgnoreCase("$")) {
//                            q.numero = 100; //estado final                   
//                        } else {
//                            q.numero = -1;
//                        }
//                        break;
//
//                    case 17:
//                        if (tok.tipo.equalsIgnoreCase("ID") || tok.tipo.equalsIgnoreCase("NE") || tok.tipo.equalsIgnoreCase("NR")) {
//                            q.numero = 18;
//                        } else {
//                            q.numero = -1;
//                        }
//                        break;
//
//                    case 18:
//                        if (tok.nom.equalsIgnoreCase("$")) {
//                            q.numero = 100; //estado final                   
//                        } else {
//                            q.numero = -1;
//                        }
//                        break;
                }
            }
        }
        if (q.numero == 100) {
            resultado = "Sin errores";
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
