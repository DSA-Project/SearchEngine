


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import opennlp.tools.stemmer.PorterStemmer;
import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


public class Main extends Application{
    private static PorterStemmer stemmer = new PorterStemmer();
    public static ArrayList<Integer> result = new ArrayList<>();
    private static Search read;
    private static Engine SearchEngine;
    static NumberFormat  formatter;
    static long start,end;
    public static void main(String[] args) throws IOException, InterruptedException {
        SearchEngine = new Engine("A:\\DataSet");
        formatter = new DecimalFormat("#0.00000");
        start=System.currentTimeMillis();
        /* This will load the index and check for any changes in the
        dataset and change the hashmap according to that
         */
        SearchEngine.loadIndex();
        //SearchEngine.append();
        //SearchEngine.saveFiles("A:\\Index\\Lexicons.json", SearchEngine.getWordId());
        //SearchEngine.saveFiles("A:\\Index\\DocID.json", SearchEngine.getdocID());
        //SearchEngine.saveFiles("A:\\Index\\ForwardIndex.json", SearchEngine.getforward());
        SearchEngine.createReverseIndex();
        end= System.currentTimeMillis();
        System.out.println("Execution time for indexing and loading program is " + formatter.format((end - start) / 1000d) + " seconds\n");
        read=new Search(SearchEngine.getWordId(),SearchEngine.getdocID(),SearchEngine.getInvert());
        launch(args);//Launches GUI interface
}
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Search Engine");//SearchEngine
        GridPane grid=new GridPane();
        GridPane grid1=new GridPane();
/* This is basically the main page with the textfields and buttons
everything on the main page is used here , the css files are also shown here.
 */
        grid1.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        HBox h=new HBox(10);
        TextField searchT=new TextField();
        Label title=new Label("Birch");
        grid.setPadding(new Insets(25,25,25,25));
        searchT.setPromptText("Enter your query");
        searchT.setMinWidth(300);
        Button search=new Button("Search");

        FileChooser chooser=new FileChooser();
        Button addFiles=new Button("Add Files to Search");


        grid1.add(search,0,0);
        grid.add(title,0,0);
        grid.add(grid1,0,2);
        grid.add(searchT,0,1);
        grid.add(addFiles,0,5);
/* If file chooser is selected then the code will auto
act to detect changes and save them.
 */

        addFiles.setOnAction(e->{
           File s=chooser.showOpenDialog(primaryStage);
            start=System.currentTimeMillis();
           SearchEngine.append();
            try {

                //SearchEngine.saveFiles("A:\\Index\\Lexicons.json", SearchEngine.getWordId());
                //SearchEngine.saveFiles("A:\\Index\\DocID.json", SearchEngine.getdocID());
                //SearchEngine.saveFiles("A:\\Index\\ForwardIndex.json", SearchEngine.getforward());
                SearchEngine.createReverseIndex();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            end=System.currentTimeMillis();
            System.out.println("Execution time for indexing is " + formatter.format((end - start) / 1000d) + " seconds\n");
            System.out.println("Files Loaded");

        });

        search.setOnAction(e->{
            ArrayList<String> results= null;
            try {

                 results=read.Search(searchT.getText(),primaryStage);
            } catch (IOException|InterruptedException ex) {
                ex.printStackTrace();
            }
            try {
                resultsPage(primaryStage,results);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        });
        Scene mainPage=new Scene(grid,500,500);

        mainPage.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(mainPage);
        primaryStage.show();
    }
    public EventHandler<ActionEvent> showDoc(String s){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                getHostServices().showDocument(s);
            }
        };
        return event;
    }

    public  void resultsPage(Stage primaryStage, ArrayList<String> results) throws InterruptedException {
/* Results page
uses buttons arrays to disp;ay result
on clicking the buttons , the respective
document shall open
 */
        ScrollPane pane=new ScrollPane();
        GridPane grid=new GridPane();
        HBox h=new HBox();
        Button result[]=new Button[results.size()];
        pane.setContent(grid);
        Scene scene=new Scene(pane,500,500);
        scene.getStylesheets().addAll(Main.class.getResource("resultStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        Button back=new Button("Back");
        back.setOnAction(e->{
            try {
                start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Label label=new Label("Number of blogs returned : "+results.size());
        h.getChildren().addAll(back,label);
        grid.add(h,0,0);
        grid.setAlignment(Pos.CENTER);
        int gridder=1;
        for(int i=0;i<results.size();i++){
            result[i]=new Button(results.get(i));
            result[i].setOnAction(showDoc(results.get(i)));
            result[i].setMinSize(500,40);
            grid.add(result[i],0,gridder);
            if(i==1000){break;}
            gridder++;

        }
    }

}