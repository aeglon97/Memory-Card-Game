package memoryCards;

import java.util.ArrayList;
import java.util.Collections;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MemoryCardGame extends Application {
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;
	private static final int CARD_SIZE = 200;
	
	private static final int NUM_X_CARDS = WIDTH/CARD_SIZE;
	private static final int NUM_Y_CARDS = HEIGHT/CARD_SIZE;
	
	private static final int NUM_CARDS = NUM_X_CARDS * NUM_Y_CARDS;
	private static final int NUM_OF_PAIRS = NUM_CARDS/2;
	
	ArrayList<Card> Cards = new ArrayList<Card>();
	ArrayList<Card> openCards = new ArrayList<Card>();
	
	private Card secondCardOpened;
	int NUM_CLICKS = 2;
	private Stage primaryStage;
	Pane root = new Pane();
	Scene scene;
	Text finishText = new Text("Game over.");
	

	private Parent createGame (Pane root){
		root.setPrefSize(WIDTH + 7, HEIGHT + 7);
		finishText.setText("Game over.");
		finishText.setFont(Font.font(10));
		
		char cardChar = '友';
		
		for (int i = 0; i < NUM_OF_PAIRS; i++) {
			Cards.add(new Card(String.valueOf(cardChar)));
			Cards.add(new Card(String.valueOf(cardChar)));
			cardChar++;
		}
		
		putDownCards();
	
	
		return root;
		
	}
	
	public void putDownCards (){
		
	Collections.shuffle(Cards);
		
		for (int i = 0; i < Cards.size(); i++){
			Cards.get(i).setTranslateX(CARD_SIZE * (i % NUM_X_CARDS));
			Cards.get(i).setTranslateY(CARD_SIZE * (i / NUM_Y_CARDS));
			root.getChildren().add(Cards.get(i));
		}

	}
	
	private class Card extends StackPane {
		String inside;
		Rectangle perimeter;
		Text text = new Text();
		
		public Card (String inside) {
			this.inside = inside;
			
			perimeter = new Rectangle (CARD_SIZE, CARD_SIZE);
			perimeter.setStroke(Color.GRAY);
			perimeter.setStrokeWidth(10);
			
			
			getChildren().addAll(perimeter, text);
			text.setText(inside);
			text.setFont(Font.font(70));
			

			setOnMouseClicked (e -> {
				if (NUM_CLICKS == 0 || Cards.size() == openCards.size() || isOpen()) return;
				NUM_CLICKS--;
				
				
				//1st click
				if (secondCardOpened == null){
					secondCardOpened = this;
					open (() -> {});	//open with empty runnable (dont do anything)
				}
				//2nd click
				else {
					open (() ->{
						if (!hasSameInside(secondCardOpened)) {
							secondCardOpened.close();
							this.close();
						}
						else {
						openCards.add(this);
						openCards.add(secondCardOpened);
						
						}
						secondCardOpened = null;
						NUM_CLICKS = 2;
					});	
				}
			
					
			});
			
			close();
		}
		public boolean isOpen (){
			return this.perimeter.getFill()== Color.MEDIUMPURPLE;
		}
		
		public boolean hasSameInside (Card secondClickedCard) {
			return (this.inside.equals(secondClickedCard.inside));
		}
		
		public void open (Runnable runnable){
			perimeter.setFill(Color.MEDIUMPURPLE);
			FadeTransition ft = new FadeTransition(Duration.seconds(0.5), text);
			ft.setToValue(1);
			ft.setOnFinished(e -> runnable.run());
			ft.play();
		}
		
		public void close(){
			perimeter.setFill(Color.HOTPINK);
			FadeTransition ft = new FadeTransition(Duration.seconds(0.5), text);
			ft.setToValue(0);
			ft.play();
		}	
		
		
	}
	
	public void restart () {
	
		Cards.clear();
		openCards.clear();
		for (int i = 0; i < Cards.size(); i++) {
			Cards.get(i).close();
		}
		createGame(new Pane());
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		this.primaryStage = primaryStage;
		
		scene = new Scene(createGame(root));
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				//Text text = new Text("You won! Press SPACE to play again.");
				if (event.getCode().equals(KeyCode.SPACE)){
					if (Cards.size() == openCards.size()) {
						restart();
					}
				}
			}
		});

		primaryStage.setTitle("My First JavaFX Project: Memory Card Game");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main (String[]args){
		launch(args);
	}
}
