package main;

public class Token {

    public TipoToken tipo;
    public String value;

    public Token(TipoToken tipo, String value) {
        this.tipo = tipo;
        this.value = value;
    }

    enum TipoToken {

        SPAM_WORD,
 
        NEG_WORD,

        POS_WORD,

        STOP_WORD;

    }

}
