package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;

public class Main extends Application {

    Controller C = new Controller();
    Parser parse = new Parser();
    boolean isOnline = false;
    boolean createFile = true;
    String textFilePath = "Sunday March 3, 2019"; // this will be changed if user wants another puzzle to open

    String puzzleInfo="";
    String puzzledate="";
    ArrayList<String> clues = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();
    ArrayList<String> cellNumbers = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();

    //@FXML private Button openFromFile;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml")); // fxml file loaded -- this is the GUI
        Parent root = loader.load();
        C = loader.getController();
        primaryStage.setTitle("NYTimes Word Puzzle");


        primaryStage.setScene(new Scene(root, 1000, 700)); // scene created
        primaryStage.show();

        //final FileChooser fileChooser = new FileChooser();

        // if we want to get the todays puzzle isOnline must be false, otherwise it will read from the file
        if(!isOnline){
            readFromFile("C:\\Users\\cansu\\Desktop\\ai\\JarvisOldFiles\\" + textFilePath + ".txt");
        }else{
            puzzleInfo = parse.getPuzzleInfo(puzzleInfo);
            puzzledate = parse.getDate(puzzledate);
            clues = parse.getClues(clues);
            answers = parse.getSolutions(answers);
            colors = parse.getColors(colors);
            cellNumbers = parse.getCellNumbers(cellNumbers);
        }

        // this part is debug, only accessed from console
        System.out.println(puzzleInfo);
        System.out.println(puzzledate);
        System.out.println("------clues-----");
        for(int i=0; i<clues.size(); i++) {
            //System.out.println(clues.get(i));
        }
        System.out.println("------answers-----");
        for(int i=0; i<answers.size(); i++){
            //System.out.println(answers.get(i));
        }
        System.out.println("------colors-----");
        for(int i=0; i<colors.size(); i++) {
            //System.out.println(colors.get(i));
        }
        System.out.println("------numbers-----");
        for(int i=0; i<cellNumbers.size(); i++) {
            //System.out.println(cellNumbers.get(i));
        }

        C.takeData(puzzleInfo,puzzledate,clues,answers,cellNumbers,colors); // data is taken

        // This part writes the data into a txt file
        if(createFile){
            try{
                File file = new File("C:\\Users\\cansu\\Desktop\\ai\\JarvisOldFiles" + puzzledate + ".txt");
                FileWriter writer = new FileWriter(file);
                writer.write(puzzleInfo);
                writer.write("\n" + puzzledate);
                for(int i=0; i<clues.size(); i++) {
                    writer.write("\n" + clues.get(i));
                }
                //writer.write("\n" + "------answers-----");
                for(int i=0; i<answers.size(); i++){
                    writer.write("\n" + answers.get(i));
                }
                //writer.write("\n" + "------colors-----");
                for(int i=0; i<colors.size(); i++) {
                    writer.write("\n" + colors.get(i));
                }
                //writer.write("\n" + "------numbers-----");
                for(int i=0; i<cellNumbers.size(); i++) {
                    writer.write("\n" + cellNumbers.get(i));
                }

                writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        // puzzle is prepared here
        C.setGrid();
        C.setQuestionsAcross();
        C.setQuestionsDown();
        C.setDate();
        C.revealAnswers();
        C.createWordArray();
        C.findAlternativeClues();
        C.setQuestionsAltAcross();
        C.setQuestionsAltDown();


        // This part will get the keyboard input
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED , new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.BACK_SPACE){
                    C.setLetters(t.getText().toUpperCase(), true);
                }else{
                    C.setLetters(t.getText().toUpperCase(), false);
                }
            }
        });

    }


    // This function reads the file from a path and creates the needed ArrayLists
    public void readFromFile(String path){
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            puzzleInfo = br.readLine();
            puzzledate = br.readLine();

            //System.out.println("DEBUG " + puzzleInfo + " " + puzzledate);

            //file clues to arraylist
            int countClue=0;
            while (countClue<10 && (line = br.readLine()) != null) {
                clues.add(line);
                countClue++;
            }
            //file answers to arraylist
            while ((line = br.readLine()) != null && Character.isLetter(line.charAt(0))) {
                answers.add(line);
            }
            //file colors to arraylist
            int countcolor=1;
            colors.add(Integer.parseInt(line));
            while (countcolor<25 && (line = br.readLine()) != null) {
                colors.add(Integer.parseInt(line));
                countcolor++;
            }
            //file cell numbers to arraylist
            while ((line = br.readLine()) != null) {
                cellNumbers.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static void main(String[] args) {
        // THIS IS WORKING System.out.println("TESTESTEST");

        launch(args);

    }
}
