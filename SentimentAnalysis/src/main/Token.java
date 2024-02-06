package main;

public class Token {

    public Tipo tipo;
    public String value;

    public Token(Tipo tipo, String value) {
        this.tipo = tipo;
        this.value = value;
    }

    enum Tipo {

        SPAM_WORD,
 
        NEG_WORD,

        POS_WORD,

        STOP_WORD;

    }

}
