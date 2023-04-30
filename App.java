package com.murray;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

/******************************************************************************************
* Final Project, FLAPPY MURRAY
* Author: Walter McCarthy
* Project Purpose: Create a run based game entitled Flappy Murray
* Input: Program accepts input via a Button Control called Restart, and through 
*   the SPACE key.
* Desired Output:  When the program is run, the user will be able to play a Flappy
*   Bird clone using the space key. When the user fails, they will be given the 
*   final score they earned and a button control to restart.
* Variables and Classes: There are 4 methods, addColumn, Collision, Jump, and 
*   startOver. On top of this, there is the timeline Class used to hold two keyframes,
*   which hold column generation/scoring, another for player movement via Space key
*   and collision detection
* Testing: The program is tested by playing. If the player avatar can successfully
*   pass through the gaps in the columns, and the difficulty curve is high but fair,
*   then the game is working correctly.
* April 25, 2023
**********************************************************************************************/

public class App extends Application {

        //Labels
        Label scoreLabel = new Label();          //Label to hold score
        Label loser = new Label();              //Label to hold Game Over message
        Label start = new Label();              //Label for starting message
        int tick;                               //tick int for timeline
        int score;                              //int to hold score
        int ymotion;                            //int to determine falling motion
        int x, y;                               //int to hold cloud positions
        Scene scene;                            //Declare scene 
        Group root;                             //Declare root
        Button reset;                           //Reset Button
        Ellipse bird;                           //Flappy Bird (Player Character)
        Image cloud;                            //Declare cloud image
        ImageView cloudView;                    //Declare cloud imageView
        boolean gameOver;                       //Boolean to hold fail state
        Rectangle ground;                       //Rectangle to populate Ground
        Timeline time;                          //Hold time for animation
        Stage stage;                            //Declare Stage
        public final int HEIGHT = 800;          //int to set Height of stage
        public final int WIDTH = 640;           //int to set width of stage
        ArrayList<Rectangle> columns;           //List of Columns
        IntegerStringConverter str;             //Convert int to String
        
    //Function to Add new Column obstacle
    void addColumn()
    {
        //space creates a consistent space between columns
        int space = 450;
        int width = 100;
        //height uses the random function from the math class to create variation
        //between columns
        int height =  50 + (int)(Math.random() * 300);
        //Bottom Column
        columns.add(new Rectangle(WIDTH + width + (columns.size() * 200),
                                    HEIGHT - height - 120, width, height));
        //Top Column
        columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 200, 
                                    0, width, HEIGHT - height - space));
    }
    
    //Function to detect Collisions
    void Collision() {
        //Run through columns
        for(Rectangle column : columns)
        {
            //If columns bounds intersects with Player Character
            if((column.getBoundsInParent().intersects(bird.getBoundsInParent()))) {
                //Game fail
                gameOver = true;
                if(bird.getCenterX() <= column.getX()) 
                {
                    bird.setCenterX(column.getX() - 2 * bird.getRadiusX() + 10);
                }
                else
                {
                    if(column.getY() != 0)
                    {
                        bird.setCenterY(column.getY() - 2 * bird.getRadiusY());
                    }
                    else if(bird.getCenterY() > column.getHeight())
                    {
                        bird.setCenterY(column.getHeight());
                    }
                }
            }
        }
        //If player goes outside of bounds of box, then fail state
        if(bird.getCenterY() > HEIGHT - 120 || bird.getCenterY() < 0)
        {
            gameOver = true;
        }
        //Fail Screen
        if(gameOver)
        {
            bird.setCenterY(HEIGHT - 120 - bird.getRadiusY());
            loser.setText("GAME OVER\nSCORE:" + str.toString(score));
            loser.setFont(new Font("Comic Sans", 70));
            loser.setLayoutX(stage.getWidth() / 2 - 250);
            loser.setLayoutY(stage.getHeight() / 2 - 100);
            loser.setTextFill(Color.WHITE);
        }
    }
    
    //Function to jump
    void Jump() {
        if(!gameOver) {
            //Stop Character falling
            if(ymotion > 0)
            {
                ymotion = 0;
            }
            //Character goes up in increments of 15
            //(remember, y is counted down in the group, so
            //in order for character to go up, the ymotion needs to be 
            //negative
            ymotion = ymotion - 15;
        }
    }
    
    
    void startOver() {
        //reset bird
        bird.setCenterX(WIDTH / 2 - 20);
        bird.setCenterY(HEIGHT / 2 - 10);
        //gameOver reversed
        gameOver = false;
        //reset character motion and score
        ymotion = 0;
        score = 0;
        scoreLabel.setText("Score:  " + str.toString(score));
        root.getChildren().remove(reset);
        root.getChildren().removeAll(columns);
        columns.clear();
        int i = 0;
        while(i < 100)
        {
            addColumn();
            i++;
        }
            start.setText("Press SPACE key to start!");
            start.setFont(new Font("Comic Sans", 50));
            start.setLayoutX(stage.getWidth() / 2 - 250);
            start.setLayoutY(stage.getHeight() / 2 - 100);
            start.setTextFill(Color.WHITE);
            
            time.pause();
            root.getChildren().add(start);
            
            scene.setOnKeyReleased(k -> {
                String code = k.getCode().toString();
                if(code.equals("SPACE"))
                {
                    root.getChildren().addAll(columns);
                    root.getChildren().remove(start);
                    time.play();
                }
            });
        }
    
        
    @Override
    public void start(Stage window) {
        //Build initial stage
        stage = window;
        stage.setTitle("Flappy Murray");
        stage.setHeight(HEIGHT);        //Fixed Height
        stage.setWidth(WIDTH);          //Fixed Width
        root = new Group();
        
        //Build the "bird" lol
        Image img = new Image("MURRAY.png");
        ImagePattern ip = new ImagePattern(img);
        bird = new Ellipse();
        bird.setFill(ip);
        bird.setRadiusX((img.getWidth() / 2));
        bird.setRadiusY((img.getHeight() / 2));
        bird.setCenterX(WIDTH / 2 - 10);
        bird.setCenterY(HEIGHT / 2 - 10);
        
        //Build the clouds
        cloud = new Image("cloud.png");
        cloudView = new ImageView(cloud);
        x = WIDTH + (int)cloud.getWidth();
        cloudView.setX(x);
        y = 10 + (int)(Math.random() * 100);
        cloudView.setY(y);
        //Set movement to zero
        ymotion = 0;
        //initialize stringconverter
        str = new IntegerStringConverter();
        //initialize columns
        columns = new ArrayList<Rectangle>();
        //initialize timeline
        time = new Timeline();
        time.setCycleCount(Animation.INDEFINITE);
        //build restart button
        reset = new Button();
        reset.setText("RESTART?");
        reset.setTranslateX(300);
        reset.setTranslateY(600);
        reset.setTextFill(Color.BLUE);
        reset.setFont(new Font("Comic Sans", 20));
        //Keyframes for timeline. This one handles player movement and detects
        //collisions leading to gameOver state changing
        KeyFrame kf = new KeyFrame(Duration.millis(20), e -> {
            tick++;
            if (tick%2 == 0 && ymotion < 15)
            {
                ymotion += 2;
            }
            x = x - 2;
            cloudView.setX(x);
            if(x < (0 - (int)cloud.getWidth()))
            {
                x = WIDTH + (int)cloud.getWidth();
                cloudView.setX(x);
                y = 10 + (int)(Math.random() * 100);
                cloudView.setY(y);
            }
            int y2 = (int)bird.getCenterY() + ymotion;
            bird.setCenterY(y2);
            scene.setOnKeyReleased(k ->
            {
                String code = k.getCode().toString();
                if(code.equals("SPACE"))
                {
                    Jump();
                }
            });
            Collision();
            if(gameOver)
            {
                if(!(root.getChildren().contains(loser)))
                    root.getChildren().addAll(loser, reset);
                reset.setOnMouseClicked(k -> {
                   root.getChildren().remove(loser);
                   startOver();
                });
            }
        });
        
        //keyframe for column movement and scoring
        KeyFrame kf2 = new KeyFrame(Duration.millis(20), e-> {
           for(int i=0; i < columns.size(); i++)
           {
               Rectangle column = columns.get(i);
               column.setFill(Color.GOLD);
               column.setX((column.getX() - 5));
               //Once bird object has passed column, score iterates
               if(column.getY() == 0 
                       && bird.getCenterX() + bird.getRadiusX() > 
                          column.getX() + column.getWidth() / 2 - 5
                       && bird.getCenterX() + bird.getRadiusX() <
                          column.getX() + column.getWidth() / 2 + 5)
               {
                   score++;
                   scoreLabel.setText("Score: " + str.toString(score));
                   scoreLabel.setTextFill(Color.DARKBLUE);
               }
           }
           //once columns have passed the window of the stage, they are removed
           for(int i = 0; i < columns.size(); i++)
           {
               Rectangle column = columns.get(i);
               
               if((column.getX() + column.getWidth()) < 0)
               {
                   columns.remove(i);
               }
           }
        });
        //Add keyframes to timeline
        time.getKeyFrames().addAll(kf, kf2);
        
        scoreLabel.setFont(new Font("Arial", 20));
        //Build scene
        ground = new Rectangle(0, HEIGHT - 120, WIDTH, 120);
        ground.setFill(Color.GREEN);
        root.getChildren().add(cloudView);
        root.getChildren().add(scoreLabel);
        root.getChildren().add(ground);
        root.getChildren().add(bird);
        
        scene = new Scene(root);  
        scene.setFill(Color.LIGHTBLUE); //Fill in skybackground
        startOver();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}