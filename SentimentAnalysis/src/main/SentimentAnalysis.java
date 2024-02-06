package main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Token.Tipo;

public class SentimentAnalysis {

    private static String spamPattern = "\\b(?:gan(ar|aste)|grat(is|uito)|ofert(ón|on)|oportunidad|descuent(([a-zA-Z])*)|premi(([a-zA-Z])*)|promoci(ó|o)n|dinero|urgente|(u|ú)nic[oa]|regalo)\\b";
    private static String negPattern = "\\b(?:conf(i)?es([a-zA-Z])*|no|nunca|jam(á|a)s|horrible|terribl(([a-zA-Z])*)|mal(([a-zA-Z])*)?|p(e|é)sim(o|a)|desagradable|odi([a-zA-Z])*|aborre(([a-zA-Z])*)|detest(([a-zA-Z])*)|lamentable|atroz|doloros(([a-zA-Z])*)|insatisfactori(o|a)|insufrible|infernal|espantos[oa]|insoportable)\\b";
    private static String posPattern = "\\b(?:excelent(([a-zA-Z])*)|maravillos[oa]|fant(á|a)stic[oa]|incre[íi]ble|genial|buen[oa]|positiv[oa]|agradabl(([a-zA-Z])*)|feli(([a-zA-Z])*)|satisfactori[oa]|bien|perfect[oa]|mejor(([a-zA-Z])*)|encantad(or|ora)|notabl(([a-zA-Z])*)|admirabl(([a-zA-Z])*)|estupendo)\\b";
    private static String stopPattern = "\\b(?:de|tras|la|que|el|en|y|a|los|se|del|las|un|por|con|una|su|para|es|al|lo|como|más|o|pero|sus|le|ha|me|si|sin|sobre|este|ya|entre|cuando|todo|esta|ser|son|dos|también|fue|había|era|muy|hasta|desde|está|nos|durante|ni|contra|otro|fuera|esos|eso|ante|unos|ellas|esto|mí|antes|algunos|qué|unos|les|nos|esos|esos|que|ellos|sus|entonces|quien|donde|porque|esto|hasta|entre|antes|durante)\\b";

    //Creamos un array de arrays para la tabla de símbolos que guardaran los tokens
    public static ArrayList<ArrayList<Token>> tablaSimbolos = new ArrayList<>();

    //Array de palabras spam
    public static ArrayList<Token> spamWords = new ArrayList<>();
    private static final int SPAM_WORDS_INDEX = 0;

    //Array de palabras negativas
    public static ArrayList<Token> negWords = new ArrayList<>();
    private static final int NEG_WORDS_INDEX = 1;

    //Array de palabras positivas
    public static ArrayList<Token> posWords = new ArrayList<>();
    private static final int POS_WORDS_INDEX = 2;

    //Array de palabras Stop
    public static ArrayList<Token> stopWords = new ArrayList<>();
    private static final int STOP_WORDS_INDEX = 3;

    //Puntaje emocional en base a 100 donde 50 es neutro, debajo de 50 es negativo, debajo de 50 es positivo
    private static int puntajeEmocional = 50;

    public static void main(String[] args) {
        String input = "Confieso que el fichaje de Jovic no me generó muchas expectativas: el Milan estaba fichando nombres importantes, futbolistas con valor en el mercado tras la dolorosa salida de Tonali al Newcastle. Lo recaudado en el centrocampista italiano se invirtió en llegadas como las de Pulisic, Chukwueze, Loftus-Cheek, Reijnders, Yunus Musah, Okafor… y de repente, Luka Jovic. El mismo Jovic que había protagonizado un paso por el Madrid casi fantasmal tenía de nuevo una oportunidad en un grande de Europa. Hasta ahora no había dado señales de vida por San Siro, tan sólo se escuchaban algunos pasos en la lejanía, pero tras marcar en los dos últimos partidos, los aficionados ya ponen cara a ese fantasma que deambulaba por el banquillo rossonero hasta el punto de considerarlo una de las mejores noticias en un momento complicado para el equipo.";

        SentimentAnalysis.insertarArraysATabla();

        System.out.println("Input original");
        System.out.println(input);
        System.out.println("");

        SentimentAnalysis.searchWords(stopPattern, input, Tipo.STOP_WORD, tablaSimbolos);
        SentimentAnalysis.searchWords(negPattern, input, Tipo.NEG_WORD, tablaSimbolos);
        SentimentAnalysis.searchWords(posPattern, input, Tipo.POS_WORD, tablaSimbolos);
        SentimentAnalysis.searchWords(spamPattern, input, Tipo.SPAM_WORD, tablaSimbolos);

        SentimentAnalysis.eliminateStopWords(input);

        SentimentAnalysis.mostrarTablaSimbolos(tablaSimbolos);
        
        SentimentAnalysis.calcularPolaridadTexto();

    }

    public static void insertarArraysATabla() {
        //Insertar spamWords a la tabla de simbolos
        tablaSimbolos.add(spamWords);
        //Insertar el array negWords a la tabla
        tablaSimbolos.add(negWords);
        //Insertar posWords a la tabla
        tablaSimbolos.add(posWords);
        //Insertar stopWords a la tabla
        tablaSimbolos.add(stopWords);
    }

    public static void searchWords(String wordPattern, String input, Token.Tipo wordCat, ArrayList<ArrayList<Token>> tablaSimbolos) {

        //Buscaremos las palabras para añadirlas a la tabla de simbolos
        //CASE_INSENSITIVE para que no distinga mayusculas y minusculas. UNICODE_CASE para que considere UNICODE y no solo ASCII
        Pattern p = Pattern.compile(wordPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(input);
        int wordArrayIndex = getWordArrayIndex(wordCat);

        while (m.find()) {
            String wordValue = m.group();
            Token token = new Token(wordCat, wordValue);
            tablaSimbolos.get(wordArrayIndex).add(token);
        }
    }

    public static int getWordArrayIndex(Token.Tipo wordCat) {

        int wordArrayIndex = -1;

        switch (wordCat) {
            case STOP_WORD ->
                wordArrayIndex = STOP_WORDS_INDEX;
            case SPAM_WORD ->
                wordArrayIndex = SPAM_WORDS_INDEX;
            case NEG_WORD ->
                wordArrayIndex = NEG_WORDS_INDEX;
            case POS_WORD ->
                wordArrayIndex = POS_WORDS_INDEX;
        }

        return wordArrayIndex;

    }

    public static void eliminateStopWords(String input) {
        //Eliminamos las stopWords de nuestro input y las reemplazamos con un empty string
        input = input.replaceAll(stopPattern, "");
        //Luego para formatar el input buscamos dos o mas espacios en blanco y lo reemplazos por uno solo
        input = input.replaceAll("\\s{2,}", " ");
        System.out.println("Input sin stop words");
        System.out.println(input);
        System.out.println("");
    }

    public static void mostrarTablaSimbolos(ArrayList<ArrayList<Token>> tablaSimbolos) {
        System.out.println("Tabla de Simbolos: ");

        for (ArrayList<Token> wordGroup : tablaSimbolos) {
            for (Token token : wordGroup) {
                System.out.println("[" + token.value + " | " + token.tipo + "]");
            }
            System.out.println("");
        }
    }
    
    public static void calcularPolaridadTexto(){
        
        //Actualizamos el puntaje anteriormente inicializado en 50;
        puntajeEmocional = puntajeEmocional - negWords.size() + posWords.size();
        
        System.out.println("Puntaje emocional " + puntajeEmocional + "\nEste texto presenta una " +(puntajeEmocional==50?" Polaridad neutra": puntajeEmocional<50?" Polaridad negativa" : " Polaridad positiva") );
    }

}
