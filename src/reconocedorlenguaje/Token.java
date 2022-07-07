
package reconocedorlenguaje;


public class Token {

    public String nom;
    public String tipo;
   
    public Token() {
        nom="";
        tipo="";
    }
  
    public String getNom() {
        return nom;
    }

    public String getTipo() {
        return tipo;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Token{" + "nom=" + nom + ", tipo=" + tipo + '}';
    }
    
}
