package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.peer.SystemTrayPeer;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Controller {
    @FXML private GridPane baseGrid;
    @FXML private VBox vboxAcross;
    @FXML private VBox vboxDown;
    @FXML private Button revealButton;
    @FXML private Label puzzleDate;

    @FXML private VBox vboxAcrossAlt;
    @FXML private VBox vboxDownAlt;

    private Pane panes[][] = new Pane[5][5];
    public Label letters[][] = new Label[5][5];
    private Label userAnswers[][] = new Label[5][5];
    private boolean blackBoxes[][] = new boolean[5][5];
    private Text questions[] = new Text[10];

    public String words[] = new String[10]; // test

    private String puzzleInfo="";
    private String puzzledate="";
    private ArrayList<String> clues = new ArrayList<>();
    private ArrayList<String> answers = new ArrayList<>();
    private ArrayList<String> cellNumbers = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private ArrayList<String> altClues = new ArrayList<>();
    private ArrayList<String> acrosses = new ArrayList<>();
    private ArrayList<String> downs = new ArrayList<>();
    private boolean hOrV = true; // horizontal or vertical

    private int prevI = 5;
    private int prevJ = 5;

    // This function sets the grid, adds 2 labels and a pane for each gridpane element
    @FXML protected void setGrid(){


        int qNum = 1;
        for(int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++) {

                int numI = i;
                int numJ = j;

                Pane pane = new Pane();
                pane.setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY))); // white
                baseGrid.add(pane, i, j);
                panes[i][j] = pane;
                //paneNum++;
                pane.setOnMousePressed(event -> setGridBackground(pane, numJ, numI)); // function assigned to the mouse click

                Label number = new Label("");
                boolean addNumber = true;

                // Since the data comes in an ArrayList, this part will convert double array [][] indexes to single array [] index
                int iPj = 0;

                if(j == 0){
                    iPj = i + 1;
                }else if(j == 1){
                    iPj = i + 6;
                }else if(j == 2){
                    iPj = i + 11;
                }else if(j == 3){
                    iPj = i + 16;
                }else if(j == 4){
                    iPj = i + 21;
                }

                System.out.println("i equals to " + i + " iPj is " + iPj);

                if(cellNumbers.get(iPj).equals("-1")){
                    addNumber = false;
                }else{
                    number.setText(cellNumbers.get(iPj));
                }

                // fonts are changed
                number.setFont(Font.font("sans_serif", 33.33));
                Label letter = new Label("");
                letter.setFont(Font.font("sans-serif", 66.67));
                letters[i][j] = letter;

                // user may not pressed the pane, so that this will recover the click
                number.setOnMousePressed(event -> setGridBackground(pane, numJ, numI)); // function assigned to the mouse click
                letter.setOnMousePressed(event -> setGridBackground(pane, numJ, numI)); // function assigned to the mouse click

                // label alignments are made
                GridPane.setHalignment(number, HPos.LEFT);
                GridPane.setValignment(number, VPos.TOP);
                GridPane.setHalignment(letter, HPos.CENTER);

                // background color assigned
                baseGrid.setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));

                if(addNumber){
                    baseGrid.add(number, i, j);
                }

                baseGrid.add(letter, i, j);
            }

        // Grid lines enabled here
        baseGrid.setGridLinesVisible(true);
        for(int i = 0; i < answers.size(); i++){
            System.out.println(answers.get(i));
        }
        setBlackSquares(); // this function called, because black squares not yet assigned
    }

    // This function sets the across questions
    @FXML protected void setQuestionsAcross(){
        for(int i = 0; i < 5; i++){
            //System.out.println("burda across");
            Text qText = new Text(" " + clues.get(i) + "\n");
            qText.wrappingWidthProperty().bind(vboxAcross.widthProperty()); // wrapping enabled
            qText.setFont(Font.font("franklin", 13)); // font and size changed

            vboxAcross.getChildren().add(qText); // added to the GUI
        }
    }

    // This function sets the down questions
    @FXML protected void setQuestionsDown(){

        for(int i = 0; i < 5; i++){
            //System.out.println("burda down");
            Text qText = new Text(" " + clues.get(i+5) + "\n");
            qText.wrappingWidthProperty().bind(vboxDown.widthProperty()); // wrapping enabled
            qText.setFont(Font.font("franklin", 13)); // font and size changed

            vboxDown.getChildren().add(qText); // added to the GUI
        }
    }

    // This function sets the alternative across questions
    @FXML protected void setQuestionsAltAcross(){
        for(int i = 0; i < 5; i++){
            //System.out.println("burda across");
            Text qText = new Text(" " + words[i].substring(0,1) + " " + altClues.get(i) + "\n");
            qText.wrappingWidthProperty().bind(vboxAcrossAlt.widthProperty()); // wrapping enabled
            qText.setFont(Font.font("franklin", 13)); // font and size changed

            vboxAcrossAlt.getChildren().add(qText); // added to the GUI
        }
    }

    // This function sets the alternative down questions
    @FXML protected void setQuestionsAltDown(){

        for(int i = 0; i < 5; i++){
            //System.out.println("burda down");
            Text qText = new Text(" " + words[i+5].substring(0,1) + " " + altClues.get(i+5) + "\n");
            qText.wrappingWidthProperty().bind(vboxDownAlt.widthProperty()); // wrapping enabled
            qText.setFont(Font.font("franklin", 13)); // font and size changed

            vboxDownAlt.getChildren().add(qText); // added to the GUI
        }
    }

    // This method sets the black squares according to the incoming data
    private void setBlackSquares(){
        for(int i = 0; i < colors.size(); i++){
            // Since the data comes in an ArrayList, this part will convert double array [][] indexes to single array [] index
            int locJ = i % 5;
            int locI = 0;
            if(i > 19){
                locI = 4;
            }else if(i > 14){
                locI = 3;
            }else if(i > 9){
                locI = 2;
            }else if(i > 4){
                locI = 1;
            }

            // The box, which is gonna painted black, painted in here.
            if(colors.get(i) == 0){
                panes[locJ][locI].setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
                blackBoxes[locJ][locI] = true; // to use easily, boxes are in an array
            }else{
                blackBoxes[locJ][locI] = false;
            }

        }
    }

    // This method is called every click on the grid
    // This method will paint grid both yellow and blue
    private void setGridBackground(Pane pane, int locJ, int locI){
        if(!blackBoxes[locI][locJ]){
            pane.setBackground(new Background(new BackgroundFill(Color.web("#ffda00"), CornerRadii.EMPTY, Insets.EMPTY))); // clicked pane will be yellow
            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 5; j++){
                    if(panes[i][j] != pane){
                        if(!blackBoxes[i][j]){
                            panes[i][j].setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY))); // if it is not a black box, pane will be white, and the clicked one remains white
                        }
                    }
                }
            }

            // horizontal or vertical
            if(locI == prevI && locJ == prevJ){
                hOrV = !hOrV; // horizontal if true, otherwise vertical if false
            }
            prevI = locI;
            prevJ = locJ;

            // this part will paint clicked cube as horizontal or vertical
            if(hOrV){
                for(int i = 0; i < 5; i++){
                    if(panes[i][locJ] != pane){
                        if(!blackBoxes[i][locJ]){
                            panes[i][locJ].setBackground(new Background(new BackgroundFill(Color.web("#a7d8ff"), CornerRadii.EMPTY, Insets.EMPTY))); // blue color
                        }
                    }
                }
            }else{
                for(int j = 0; j < 5; j++){
                    if(panes[locI][j] != pane){
                        if(!blackBoxes[locI][j]){
                            panes[locI][j].setBackground(new Background(new BackgroundFill(Color.web("#a7d8ff"), CornerRadii.EMPTY, Insets.EMPTY))); // blue color
                        }

                    }
                }
            }
        }

    }
    public void setDate(){
        puzzleDate.setText(puzzledate);
    } // This function will set the puzzle's date

    // This function will reveal the answers if user pressed the reveal button
    public void revealAnswers(){
        int blackBoxNum = 0;
        for(int j = 0; j < 5; j++){
            for(int i = 0; i < 5; i++){
                // if it is not a black box, this part will fill the pane with the correct letter
                if(!blackBoxes[i][j]){
                    System.out.println("no black box: " + blackBoxNum);
                    // Since the data comes in an ArrayList, this part will convert double array [][] indexes to single array [] index
                    int iPj = 0;

                    if(j == 0){
                        iPj = i;
                    }else if(j == 1){
                        iPj = i + 5;
                    }else if(j == 2){
                        iPj = i + 10;
                    }else if(j == 3){
                        iPj = i + 15;
                    }else if(j == 4){
                        iPj = i + 20;
                    }
                    letters[i][j].setText(answers.get(iPj - blackBoxNum));
                }else{
                    // otherwise this counter will increase for debug reasons
                    blackBoxNum++;
                    System.out.println("Black box number: " + blackBoxNum);
                }
            }
        }
    }

    // When user presses a key, this function will be called
    // This function moves the yellow box to other box so that user will know where to write
    public void setLetters(String str, boolean deletion){

        if(!blackBoxes[prevI][prevJ]){
            letters[prevI][prevJ].setText(str);

            if(!deletion){
                //System.out.println("PrevI = " + prevI + " PrevJ = " + prevJ);
                panes[prevI][prevJ].setBackground(new Background(new BackgroundFill(Color.web("#a7d8ff"), CornerRadii.EMPTY, Insets.EMPTY))); // blue
                if(hOrV){
                    if(prevI < 4){
                        prevI++;
                    }
                }else{
                    if(prevJ < 4){
                        prevJ++;
                    }
                }
                if(!blackBoxes[prevI][prevJ]){
                    panes[prevI][prevJ].setBackground(new Background(new BackgroundFill(Color.web("#ffda00"), CornerRadii.EMPTY, Insets.EMPTY))); // yellow
                }else{
                    if(hOrV){
                        prevI--;
                    }else{
                        prevJ--;
                    }
                    panes[prevI][prevJ].setBackground(new Background(new BackgroundFill(Color.web("#ffda00"), CornerRadii.EMPTY, Insets.EMPTY))); // yellow
                }
                //if(prevI != 4 && prevJ != 4)
                //setLetters(letters[prevI][prevJ].getText());
            }
        }

    }

    // This function will take the data from the Main class so that we can use it in here
    public void takeData(String info, String date, ArrayList<String> clues, ArrayList<String> answers, ArrayList<String> cellNumbers, ArrayList<Integer> colors){
        puzzleInfo = info;
        puzzledate = date;
        this.clues = clues;
        this.answers = answers;
        this.cellNumbers = cellNumbers;
        this.colors = colors;
    }

    public void createWordArray(){

        // acrosses
        for(int i = 0; i < 5; i++){
            String temp = "";
            int check = 0;
            for(int j = 0; j < 5; j++){
                if(!blackBoxes[j][i]){
                    temp = temp + letters[j][i].getText();

                    int iPj = 0;
                    if (check == 0) {
                        if(i == 0){
                            iPj = j + 1;
                        }else if(i == 1){
                            iPj = j + 6;
                        }else if(i == 2){
                            iPj = j + 11;
                        }else if(i == 3){
                            iPj = j + 16;
                        }else if(i == 4){
                            iPj = j + 21;
                        }

                        temp = cellNumbers.get(iPj) + " " + temp;
                        check++;
                    }

                }
            }
            words[i] = temp;
            //System.out.println(words[i]);
        }

        // downs
        //String tempWords[] = new String[5];
        for(int j = 0; j < 5; j++){
            String temp = "";
            int check = 0;
            for(int i = 0; i < 5; i++){
                if(!blackBoxes[j][i]){
                    temp = temp + letters[j][i].getText();

                    int iPj = 0;
                    if (check == 0) {
                        if(i == 0){
                            iPj = j + 1;
                        }else if(i == 1){
                            iPj = j + 6;
                        }else if(i == 2){
                            iPj = j + 11;
                        }else if(i == 3){
                            iPj = j + 16;
                        }else if(i == 4){
                            iPj = j + 21;
                        }

                        temp = cellNumbers.get(iPj) + " " + temp;
                        check++;
                    }

                }
            }
            downs.add(temp);
        }
        Collections.sort(downs);
        for(int i = 0; i < 5; i++){
            words[i+5] = downs.get(i);
        }

        for (int i = 0; i < 10; i++){
            System.out.println(words[i]);
        }

    }

    public void findAlternativeClues(){
        String urlx  ="http://wordnetweb.princeton.edu/perl/webwn?s=";
        System.out.println();
        System.out.println("Looking for clues - WORDNET");

        for(int k=0; k<words.length; k++){
            try {
                ArrayList<String> arr = new ArrayList<>();
                String curWord = words[k].substring(2);
                curWord = curWord.toLowerCase(Locale.ENGLISH);
                System.out.println("Word is: " + curWord);
                Document doc = Jsoup.parse(new URL(urlx + curWord), 10000);
                Elements nu = doc.getElementsByTag("li");
                for (int i = 0; i < nu.size(); i++) {
                    String descr = nu.get(i).text();
                    String alterclue = descr.substring(6);
                    int countOpen = 0;
                    int countClose = 0;
                    int firstCloseInd = 0;
                    int exopen = 0;
                    char[] charr = alterclue.toCharArray();
                    String parant = "";
                    for (int s = charr.length - 1; s >= 0; s--) {
                        if(charr[s]=='"'){
                            exopen++;
                        }
                        if (charr[s] == '(' && exopen%2==0)
                            countOpen++;
                        if (charr[s] == ')' && exopen%2==0) {
                            if (countClose == 0)
                                firstCloseInd = s;
                            countClose++;
                        }
                        if (countOpen == countClose && countOpen != 0 ) {
                            parant = alterclue.substring(s + 1, firstCloseInd);
                            if(parant.length()!=0)
                                parant = parant.substring(0, 1).toUpperCase() + parant.substring(1);
                            String temp = parant;

                            if (!temp.toLowerCase().contains(curWord)) {
                                if (temp.length() <= 100 && temp.length()>0)
                                    arr.add(parant);
                            }
                            break;
                        }
                    }

                }
                if(!arr.isEmpty()){
                    int number = (int)(Math.random()*arr.size());
                    String toAdd = arr.get(number);
                    toAdd = toAdd.substring(0, 1).toUpperCase() + toAdd.substring(1);
                    altClues.add(k,toAdd);

                }else{
                    altClues.add(k, "");
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int u = 0; u < altClues.size(); u++) {
            System.out.println("altClue after WORDNET: " + altClues.get(u));
        }
        System.out.println();
        findAlternativeCluesThesaurus();
    }


    public void findAlternativeCluesThesaurus(){
        String url = "https://www.merriam-webster.com/thesaurus/";
        System.out.println();
        System.out.println("Looking for clues - MERRIAM WEBSTER THESAURUS");
        for(int i=0; i<altClues.size(); i++) {
            String wordy = altClues.get(i);
            if(wordy.equals("")) {
                try {
                    String wword = words[i].toLowerCase(Locale.ENGLISH).substring(2);
                    System.out.println("Word is: " + wword);
                    Document doc = Jsoup.parse(new URL(url + wword), 10000);
                    Elements synonyms = doc.getElementsByClass("thes-list syn-list");
                    String syns = synonyms.text();

                    syns = syns.substring(12+wword.length());
                    System.out.println("All the synonyms of " + wword + " are: " + syns);

                    String[] tempArray;
                    String delimiter = ",";
                    tempArray = syns.split(delimiter);


                    int randomIndex = (int) ((Math.random() * (tempArray.length / 4)+1));
                    altClues.remove(i);
                    String result = tempArray[randomIndex];
                    if(result.substring(0,1).equals(" ")){
                        result = result.substring(1);
                    }
                    altClues.add(i,"Synonym of " + result);

                } catch (Exception e) {
                    //e.printStackTrace();
                    //altClues.remove(i);
                    //altClues.add(i, "");
                    System.out.println("Adding spaces and searching again in MERRIAM WEBSTER THESAURUS");
                    System.out.println("The word is : " + words[i].toLowerCase(Locale.ENGLISH).substring(2));
                    findAlternativeCluesSeperate(words[i].toLowerCase(Locale.ENGLISH).substring(2),0,url,"%20","thes-list syn-list",i);
                    System.out.println();
                }
            }
        }
        for (int u = 0; u < altClues.size(); u++) {
            System.out.println("altClue after MERRIAM WEBSTER THESAURUS: " + altClues.get(u));
        }
        findAlternativeAbbreviations();

    }
    public void findAlternativeCluesSeperate(String word, int i, String url,String appendix,String tag, int index){
        String temp = word;

        if(temp.length() > 2 && i < temp.length()-1){
            temp = temp.substring(0, i+1) + appendix + temp.substring(i+1);
            System.out.println("Word modified as: " + temp);
            try {
                Document doc = Jsoup.parse(new URL(url + temp), 100000);
                Elements synonyms = doc.getElementsByClass(tag);
                //System.out.println("seperate : " + synonyms.text() + " size : " + synonyms.text().length());
                if (synonyms.text().length() == 0) throw new Exception();
                //altClues.remove(i);
                //altClues.add(i,synonyms.get(0).text());
                String toAdd = synonyms.get(0).text();
                toAdd = toAdd.substring(0, 1).toUpperCase() + toAdd.substring(1);
                altClues.set(index, toAdd);

            }
            catch (Exception e) {
                //.printStackTrace();
                System.out.println("Not found!");
                findAlternativeCluesSeperate(word, i+1, url,appendix,tag,index);
            }
        }else{
            return;
        }

    }
    public void findAlternativeAbbreviations(){
        String url = "https://www.abbreviations.com/";
        System.out.println();
        System.out.println("Looking for clues - ABBREVIATIONS");
        for(int i=0; i<altClues.size(); i++) {
            String wordy = altClues.get(i);
            if(wordy.equals("")) {
                try {
                    String wword = words[i].toUpperCase(Locale.ENGLISH).substring(2);
                    System.out.println("The word is: " + wword);
                    Document doc = Jsoup.parse(new URL(url + wword), 10000);
                    Elements synonyms = doc.getElementsByClass("desc");
                    if (synonyms.size() > 1){
                        //System.out.println("Text : " + synonyms.get(1).text());
                        String toAdd = synonyms.get(1).text();
                        toAdd = toAdd.substring(0, 1).toUpperCase() + toAdd.substring(1);
                        altClues.set(i, toAdd);
                    }
                    //System.out.println("abbreviations: " + wordy);
                    //System.out.println("Abbreviations ---------------" + synonyms.text());

                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println("Not found!");
                }
            }
        }
        for (int u = 0; u < altClues.size(); u++) {
            System.out.println("altClue after ABBREVIATIONS: " + altClues.get(u));
        }
        findAlternativeDefinitionFromAbbreviations();
        System.out.println();
    }

    public void findAlternativeDefinitionFromAbbreviations(){
        String url = "https://www.definitions.net/definition/";
        System.out.println();
        System.out.println("Looking for clues - ABBREVIATIONS DEFINITIONS");
        for(int i=0; i<altClues.size(); i++) {
            String wordy = altClues.get(i);
            if(wordy.equals("")) {
                try {
                    String wword = words[i].toLowerCase(Locale.ENGLISH).substring(2);
                    System.out.println("The word is: " + wword);
                    Document doc = Jsoup.parse(new URL(url + wword), 10000);
                    // Document doc = Jsoup.parse(new URL(url ), 10000);
                    Elements synonyms = doc.getElementsByClass("desc");
                    //System.out.println("wholeee: " + synonyms.text() + "size: " + synonyms.text().length());
                    System.out.println("*******");
                    String syns = synonyms.get(0).text();
                    System.out.println("syns: " + syns);
                    String[] tempArray;
                    String delimiter = "[\\.]";
                    tempArray = syns.split(delimiter);
                    String tempString = tempArray[0];
                    String[] tempArray2;
                    tempArray2 = tempString.split(" ");
                    for(int e=0; e<tempArray2.length; e++){
                        if(tempArray2[e].toLowerCase().equals(wword)){
                            tempArray2[e] = "___";
                        }
                    }
                    String result = "";
                    for(int u=0; u<tempArray2.length; u++){
                        result += tempArray2[u] + " ";
                    }
                    System.out.println("result: " + result);

                    System.out.println("*******");

                    if (synonyms.size() > 1){
                        String toAdd = result;
                        toAdd = toAdd.substring(0, 1).toUpperCase() + toAdd.substring(1);
                        altClues.set(i, toAdd);
                    }
                    if(synonyms.text().length()==0){
                        throw new Exception();
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println("Adding spaces and searching again in ABBREVIATIONS DEFINITIONS");
                    System.out.println("The word is : " + words[i].toLowerCase(Locale.ENGLISH).substring(2));
                    findAlternativeCluesSeperate(words[i].toLowerCase(Locale.ENGLISH).substring(2),0,url,"+","desc",i);
                    System.out.println();
                }
            }
        }
        for (int u = 0; u < altClues.size(); u++) {
            System.out.println("altClue after ABBREVIATIONS DEFINITIONS: " + altClues.get(u));
        }
        findClueFromDataMuse();


    }
    public void findClueFromDataMuse() {
        String url = "https://api.datamuse.com/words?ml=";
        System.out.println();
        System.out.println("Looking for clues - DATAMUSE");
        for (int i = 0; i < altClues.size(); i++) {
            String wordy = altClues.get(i);
            if (wordy.equals("")) {
                try {
                    String wword = words[i].toUpperCase(Locale.ENGLISH).substring(2);
                    System.out.println("The word is: " + wword);

                    JSONObject jsonObject = readJsonFromUrl(url + wword);
                    if (jsonObject != null && jsonObject.length() != 0) {
                        altClues.set(i, "__ " + jsonObject.get("word").toString());
                    }
                    else throw new Exception();
                } catch (Exception e) {
                    System.out.println("Adding spaces and searching again in DATAMUSE");
                    System.out.println("The word is : " + words[i].toUpperCase(Locale.ENGLISH).substring(2));
                    findClueFromDataMuseRecursive(0, i);
                }
            }
        }
        for (int u = 0; u < altClues.size(); u++) {
            System.out.println("altClue after DATAMUSE: " + altClues.get(u));
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            JSONObject json = new JSONObject(jsonText.substring(1, jsonText.length()));

            return json;
        } finally {
            is.close();
        }
    }

    public void findClueFromDataMuseRecursive(int i, int index) {
        String url = "https://api.datamuse.com/words?ml=";

        try {
            String wword = words[index].toUpperCase(Locale.ENGLISH).substring(2);
            System.out.println("The word modified as: " + wword.substring(0, i+1) + "+" + wword.substring(i+1));
            JSONObject jsonObject = readJsonFromUrl(url + wword.substring(0, i+1) + "+" + wword.substring(i+1));
            System.out.println();
            if (jsonObject != null && jsonObject.length() != 0) {
                String toAdd = jsonObject.get("word").toString();
                toAdd = toAdd.substring(0, 1).toUpperCase() + toAdd.substring(1);
                altClues.set(index, toAdd);
            }
            else throw  new Exception();
        } catch (Exception e) {
            System.out.println("Not found!");
            findClueFromDataMuseRecursive(i+1, index);
        }
    }
}

