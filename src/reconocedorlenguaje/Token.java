
package reconocedorlenguaje;


public class Token {

    public String valorScanner; //resultado q retorna el scanner (EN VARIABLES VENDRÍA A SER EL NOMBRE DE LA VARIABLE)
    public String tipoDato; //ESTE VENDRÍA A SER EL TIPO DE DATO PARA VARIABLE (en primitivas y simbolos vendría a ser lo mismo que calidad)
    public String calidad; //está relacionado si es NE,NR,OP O ID
    public String valor;
    //La dirección de base vendría a ser el índice del arraylist
    //fn: -- ,debido a q solo analizamos variables
    public Token() {
        valorScanner="";
        tipoDato="";  //indica si es un NUM o un ID
        calidad="";
        valor="0";  // 0 es nuestro valor predeterminado
    }

    public String getValorScanner() {
        return valorScanner;
    }

    public void setValorScanner(String valorScanner) {
        this.valorScanner = valorScanner;
    }

  
    public String getCalidad() {
        return calidad;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
    }


    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }
  



    @Override
    public String toString() {
        return "Token{" + "tipo o símbolo=" + valorScanner + ", calidad=" + tipoDato + '}';
    }
    
}
