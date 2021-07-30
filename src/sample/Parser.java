package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

public class Parser {

    static String url = "https://www.nytimes.com/crosswords/game/mini";

    public static ArrayList<String> getClues(ArrayList<String> clues) {
        try{
            Document doc = Jsoup.parse(new URL(url), 10000);
            Elements number = doc.getElementsByClass("Clue-label--2IdMY"); //get clue numbers
            Elements clue = doc.getElementsByClass("Clue-text--3lzl7"); //get clues

            for(int i=0; i<clue.size(); i++) {
                clues.add(number.get(i).text() + " " + clue.get(i).text()); //add clues to arraylist
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return clues;
    }

    public static ArrayList<Integer> getColors(ArrayList<Integer> colors){

        try {
            Document doc = Jsoup.parse(new URL(url), 10000);
            Elements box = doc.select("g[data-group=\"cells\"]");   //select puzzle cell group
            Elements cell = box.select("rect");  //select each small cell

            for(int i=0; i<cell.size(); i++) {
                if(cell.get(i).className().equals("Cell-block--1oNaD")){
                    colors.add(0); //black
                }
                else
                    colors.add(1); //white
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return colors;
    }
    public static String getPuzzleInfo(String puzzleInfo){
        try {
            Document doc = Jsoup.parse(new URL(url), 10000);
            Elements inf = doc.getElementsByClass("PuzzleDetails-byline--16J5w");

            puzzleInfo = inf.text().substring(0,18) + " " + inf.text().substring(18); //The Mini Crossword By JOEL FAGLIANO
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return puzzleInfo;

    }
    public static String getDate(String puzzledate){
        try {
            Document doc = Jsoup.parse(new URL(url), 10000);
            Elements date = doc.getElementsByClass("PuzzleDetails-date--1HNzj");

            puzzledate = date.text();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return puzzledate;

    }

    public static ArrayList<String> getSolutions(ArrayList<String> answers){

        try {
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\cansu\\IdeaProjects\\Jarvis2\\src\\sample\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();
            driver.get(url);
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[4]/div/main/div[2]/div/div[2]/div[2]/article/div[2]/button")).click(); //ok
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[4]/div/main/div[2]/div/div/ul/div[1]/li[2]/button")).click(); //reveal
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[4]/div/main/div[2]/div/div/ul/div[1]/li[2]/ul/li[3]/a")).click(); //puzzle
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/article/div[2]/button[2]/div")).click(); //reveal
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/span")).click(); //x

            List<WebElement> elements = driver.findElements(By.cssSelector("text")); //get secret <text> elements
            for(WebElement i : elements) {
                if(Character.isLetter(i.getText().charAt(0))) {
                    answers.add(i.getText());   //add letters to arraylist
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return answers;
    }
    public static ArrayList<String> getCellNumbers(ArrayList<String> cellNumbers) {

        try {
            Document doc = Jsoup.parse(new URL(url), 10000);
            Elements box = doc.select("g[data-group=\"cells\"]"); //select puzzle cell group
            Elements cell = box.select("g");

            for (int i = 0; i < cell.size(); i++) {
                if (!cell.get(i).text().isEmpty())
                    cellNumbers.add(cell.get(i).text()); //add cell number to arraylist
                else
                    cellNumbers.add("-1"); //if there is no cell number, add -1 to arraylist
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cellNumbers;
    }



}




