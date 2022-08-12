package reconocedorlenguaje;

import extra.ObjectInt;
import java.util.ArrayList;

public class Mecanismo {

    private int j;
    private String cadenaActual;
    public Token tok = new Token();
    public String resultado;
    public boolean existeError=false;
    public Token bufferVariable; //esto es una memoria temporal para identificar la variable actual
    public String bufferTipoDato; //memoria temporal para almacenar el tipo de dato
    public boolean comentarioMultilineas = false;
    public boolean banderaAntecedente = false;
    public ArrayList<String> cad_tok = new ArrayList(); //ESTO NO SÉ PA Q SIRVE LA VRD
    public ArrayList<Token> VariablesDeclaradas=new ArrayList<>(); //aqui se guarda los tokens declarados
    public ArrayList<String> arrayOperaciones=new ArrayList<>();
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
        tok = new Token();
 
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
            tok.setVariableOSimbolo(tok.getVariableOSimbolo()+ "Salto de linea");
            tok.setCalidad("L");//Linea
            j++;
            c = cad.charAt(j++);
        }

        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {   // Letra
            c = cad.toLowerCase().charAt(j);
            while ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                tok.setVariableOSimbolo(tok.getVariableOSimbolo() + c);
                j++;

                c = cad.charAt(j);
            }

            tok.setCalidad("ID");  // Identificador

        } else if (c >= '0' && c <= '9') {
            while ((c >= '0' && c <= '9') || c == '.') {

                if (c == '.') {
                    cantidadPuntos++;
                }
                //se guarda el valor
                tok.setVariableOSimbolo(tok.getVariableOSimbolo() + c);

                if (cantidadPuntos == 0) {
                    tok.setCalidad("NE");  // Numero entero
                    tok.setTipoDato("NE");
                } else {
                    tok.setCalidad("NR");  // Numero real
                    tok.setTipoDato("NR");
                }
                j++;
                c = cad.charAt(j);
            }

            if (cantidadPuntos > 1) {
                tok.setVariableOSimbolo("");
                tok.setCalidad(""); //SE OCASIONA FALLO POR DOBLE "." EN NUMERO
                tok.setTipoDato("");
            }
        } else if (c == ',' || c == '=' || c == '*' || c == '/' || c == '-' || c == '+'
                || c == '<' || c == '>' || c == '.' || c == '(' || c == ')') {  // Operador
            tok.setVariableOSimbolo(String.valueOf(c));
            if ((c == '<' && cad.charAt(j + 1) == '<')
                    || (c == '>' && cad.charAt(j + 1) == '>')) {
                tok.setVariableOSimbolo(tok.getVariableOSimbolo() + cad.charAt(j + 1));
                j++;
            }
            j++;

            tok.setCalidad("OP");  // Operador
            tok.setTipoDato("OP");
        } else if (c == '$') { // fin de cadena
            tok.setVariableOSimbolo(String.valueOf(c));

        }
        return tok.getVariableOSimbolo();

    }

    //para comentarios lineales y multilineales
    public void comentarios(ObjectInt q, String cadena) {
        if (this.banderaAntecedente == true) {
            this.comentarioMultilineas = true;
        }
        if (tok.variableOSimbolo.equalsIgnoreCase("/") && cadena.charAt(j) == '/') {  //para comentario lineal
            tok.setVariableOSimbolo("$");
            tok.setCalidad("");
            tok.setTipoDato("");
            if (cadena.charAt(0) == '/') {
                q.numero = 100;
            }
        }
        if (tok.variableOSimbolo.equalsIgnoreCase("/") && cadena.charAt(j) == '*') { //para comentario multilinea
            j++;
            while (!(tok.variableOSimbolo.equalsIgnoreCase("*") && cadena.charAt(j) == '/')) { //para cerrar
                scanner(cadena);
                //System.out.println(tok.variableOSimbolo);
                if (tok.variableOSimbolo.equalsIgnoreCase("$")) { //en caso no se llegase a encontrar "*/ en la misma linea"
                    this.banderaAntecedente = true;
                    if (cadena.charAt(0) == '/') {
                        q.numero = 100;
                    }
                    break;
                }
            }
            if (!tok.variableOSimbolo.equalsIgnoreCase("$")) {
                scanner(cadena);
                scanner(cadena);
                if (cadena.charAt(0) == '/' && tok.variableOSimbolo.equalsIgnoreCase("$")) {
                    q.numero = 100;
                }
            }

        } //aqui (EL IF DE ARRIBA) hacemos el caso de /*  */ en la misma linea y tambn cuando hay /* $ en una sola linea
        else {
            if (this.comentarioMultilineas == true) {
                while (!(tok.variableOSimbolo.equalsIgnoreCase("*") && cadena.charAt(j) == '/')) { //para cerrar
                    scanner(cadena);

                    if (tok.variableOSimbolo.equalsIgnoreCase("$")) { //en caso no se llegase a encontrar "*/ en la misma linea"
                        this.banderaAntecedente = true;
                        break;

                    }
                }
                if (!tok.variableOSimbolo.equalsIgnoreCase("$")) { //encontró el final
                    scanner(cadena);
                    scanner(cadena);
                    this.banderaAntecedente = false;
                    this.comentarioMultilineas = false;
                    if (tok.variableOSimbolo.equalsIgnoreCase("$")) {
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
        if (tok.variableOSimbolo.equalsIgnoreCase("$")) {
        } else {
            //System.out.println(this.tok.variableOSimbolo);
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
        if (tok.variableOSimbolo.equalsIgnoreCase(SD3[0]) || tok.variableOSimbolo.equalsIgnoreCase(SD3[1])) {
            X();
            W();
        } else {
            if (tok.variableOSimbolo.equalsIgnoreCase(SD4[0]) || tok.variableOSimbolo.equalsIgnoreCase(SD4[1])) {
                //lambda
            } else {
                this.errorLL1++;
            }
        }

    }

    public void X() {
        if (tok.variableOSimbolo.equalsIgnoreCase(SD5[0])) {
            scanner(this.cadenaActual);
            T();
        } else {
            if (tok.variableOSimbolo.equalsIgnoreCase(SD6[0])) {
                scanner(this.cadenaActual);
                T();
            } else {
                this.errorLL1++;
            }
        }

    }

    public void R() {
        if (tok.variableOSimbolo.equalsIgnoreCase(SD8[0]) || tok.variableOSimbolo.equalsIgnoreCase(SD8[1])) {
            Y();
            R();
        } else {
            if (tok.variableOSimbolo.equalsIgnoreCase(SD9[0]) || tok.variableOSimbolo.equalsIgnoreCase(SD9[1]) || tok.variableOSimbolo.equalsIgnoreCase(SD9[2]) || tok.variableOSimbolo.equalsIgnoreCase(SD9[3])) {
                //lambda
            } else {
                this.errorLL1++;
            }
        }

    }

    public void F() {
        if (tok.variableOSimbolo.equalsIgnoreCase(SD12[0])) {
            scanner(this.cadenaActual);
            E();
            if (tok.variableOSimbolo.equalsIgnoreCase(")")) {
                scanner(this.cadenaActual);
            } else {
                this.errorLL1++;
            }
        } else {

            if (tok.variableOSimbolo.equalsIgnoreCase(SD13[0])) {
                scanner(this.cadenaActual);
                F();
            } else {
                if (tok.calidad.equalsIgnoreCase(SD14[0]) || tok.calidad.equalsIgnoreCase(SD14[1]) || tok.calidad.equalsIgnoreCase(SD14[2])) {
                    scanner(this.cadenaActual);
                } else {
                    this.errorLL1++;
                }
            }
        }
    }

    public void Y() {
        if (tok.variableOSimbolo.equalsIgnoreCase(SD10[0])) {
            scanner(this.cadenaActual);
            F();
        } else {
            if (tok.variableOSimbolo.equalsIgnoreCase(SD11[0])) {
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

        ObjectInt q = new ObjectInt(0);
        this.j = 0;
        this.cadenaActual=cadena;
        while (q.numero != 100 && q.numero != -1) {

            scanner(cadena);
            //System.out.println(tok.variableOSimbolo);
            cad_tok.add(tok.variableOSimbolo);

            comentarios(q, cadena);

            if (this.comentarioMultilineas == false) {
                switch (q.numero) {

                    case 0:
                        if (tok.variableOSimbolo.equalsIgnoreCase("entero") || tok.variableOSimbolo.equalsIgnoreCase("real")) {
                            q.numero = 1;
                            if(tok.variableOSimbolo.equalsIgnoreCase("entero") ){
                               bufferTipoDato="Entero"; //guardo en buffer el tipo de dato de la futura variable
                            }  
                            if(tok.variableOSimbolo.equalsIgnoreCase("real") ){
                               bufferTipoDato="Real";  
                            }  
                        } else if (tok.variableOSimbolo.equalsIgnoreCase("lee")) {
                            q.numero = 5;

                        } else if (tok.variableOSimbolo.equalsIgnoreCase("escribe")) {
                            q.numero = 8;

                        } else if (tok.calidad.equalsIgnoreCase("ID")) {
                            q.numero = 11;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 1:
                        if (tok.calidad.equalsIgnoreCase("ID")) {
                            q.numero = 2;
                            tok.setTipoDato(bufferTipoDato);
                            VariablesDeclaradas.add(tok); //guardo el token declarado (variable)
                            bufferVariable=tok; //guardo en buffer a la variable
                            
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 2:
                        if (tok.variableOSimbolo.equalsIgnoreCase("$")) {
                            q.numero = 100; //estado final

                        } else {
                            if (tok.variableOSimbolo.equalsIgnoreCase("=")) {
                                q.numero = 3;
                            } else {
                                if (tok.variableOSimbolo.equalsIgnoreCase(",")) {
                                    q.numero = 1;
                                } else {

                                    q.numero = -1;
                                }
                            }
                        }
                        break;

                    case 3:
                        if (tok.calidad.equalsIgnoreCase("NE") || tok.calidad.equalsIgnoreCase("NR") || tok.calidad.equalsIgnoreCase("ID")) {
                            q.numero = 4;
                            if(tok.calidad.equalsIgnoreCase("NE") || tok.calidad.equalsIgnoreCase("NR")){
                                //para guardar valor
                                almacenarValorEnVariableSegunTipo(bufferVariable,tok.variableOSimbolo);
                             
                            }else{
                                if(tok.calidad.equalsIgnoreCase("ID")){
                                    //para guardar valor
                                   almacenarValorEnVariableSegunTipo(bufferVariable,devolverValorVariable(tok.variableOSimbolo));
                                  
                                    if(bufferVariable.valor==null){
                                        q.numero=-1;
                                    }
                                }
                            }
                            
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 4:
                        if (tok.variableOSimbolo.equalsIgnoreCase("$")) {
                            q.numero = 100; //estado final
                        } else {
                            if (tok.variableOSimbolo.equalsIgnoreCase(",")) {
                                q.numero = 1;
                            } else {
                                q.numero = -1;
                            }
                        }
                        break;
                    case 5:
                        if (tok.variableOSimbolo.equalsIgnoreCase(">>")) {
                            q.numero = 6;

                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 6: //Según el parser q tenemos, solo podemos leer variables
                        if (tok.calidad.equalsIgnoreCase("ID")) {
                            q.numero = 7;
                            if(devolverValorVariable(tok.variableOSimbolo)!=null){
                                if(existeError==false){
                                  System.out.println(devolverValorVariable(tok.variableOSimbolo)); //AQUI IMPRIMO EL VALOR DE VARIABLE    
                                }
                            }else{
                                q.numero=-1;
                            }
                            
                        } else {

                            q.numero = -1;
                        }
                        break;

                    case 7:
                        if (tok.variableOSimbolo.equalsIgnoreCase("$")) {
                            q.numero = 100;

                        } else if (tok.variableOSimbolo.equalsIgnoreCase(">>")) {
                            q.numero = 6;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 8:
                        if (tok.variableOSimbolo.equalsIgnoreCase("<<")) {
                            q.numero = 9;
                            
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 9:
                        if (tok.calidad.equalsIgnoreCase("ID")) {
                            q.numero = 10;
                            
                        } else {

                            q.numero = -1;
                        }
                        break;

                    case 10:
                        if (tok.variableOSimbolo.equalsIgnoreCase("$")) {
                            q.numero = 100;

                        } else if (tok.variableOSimbolo.equalsIgnoreCase("<<")) {
                            q.numero = 9;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 11:
                        if (tok.variableOSimbolo.equalsIgnoreCase("=")) {
                            q.numero = 12;
                        } else {
                            q.numero = -1;
                        }
                        break;

                    case 12:
                        q.numero=parserLL1(cadena);
                        break;
                }
            }
        }
        if (q.numero == 100) {
            resultado = "Sin errores";
        } else {
            resultado = "Error";
            existeError=true;
        }

    }
    public String devolverValorVariable(String nombre){
        String ValorRetorno = null;
        for(int i=0;i<VariablesDeclaradas.size();i++){
            if(VariablesDeclaradas.get(i).variableOSimbolo.equalsIgnoreCase(nombre)){
                ValorRetorno=VariablesDeclaradas.get(i).getValor();
            }
        }      
        return ValorRetorno;
    }
    public void almacenarValorEnVariableSegunTipo(Token token,String valor){
        String extra;
        if(valor==null){
            token.valor=null;
        }else{
              if(token.tipoDato.equalsIgnoreCase("Entero")){
            String[] resultados=valor.split("\\.");
            token.valor=resultados[0];
        }else{
            if(token.tipoDato.equalsIgnoreCase("Real")){
                extra=String.valueOf(Float.parseFloat(valor));
                token.valor=extra;
            }
        }
        }
      
    }
    public void setJ(int j) {
        this.j = j;
    }

    public int getJ() {
        return j;
    }

}
