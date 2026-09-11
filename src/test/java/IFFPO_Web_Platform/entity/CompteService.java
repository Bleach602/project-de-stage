package IFFPO_Web_Platform.entity;

public class CompteService {

    public void retirer(int solde, int montant){

       if(montant > solde){
           throw new IllegalArgumentException("Solde insuffisant");
       }
    }
}
