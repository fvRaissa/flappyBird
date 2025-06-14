package flappyBird;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener{
	 int boardWidth = 360;
	 int boardHeigth = 640;
	 
	 Image backgroundImg;
	 Image birdImg;
	 Image topPipeImg;
	 Image bottomPipeImg;
	 
	 int birdX = boardWidth/8;
	 int birdY = boardHeigth/2;
	 int birdWidth = 34;
	 int birdHeigth = 24;
	 
	 class Bird{
		 int x = birdX;
		 int y = birdY;
		 int width = birdWidth;
		 int heigth = birdHeigth;
		 Image img;
		 
		 Bird(Image img){
			 this.img = img;
		 }
	 }
	 
	 //Pipes
	 int pipeX = boardWidth;
	 int pipeY = 0;
	 int pipeWidth = 64; //escala por 1/6
	 int pipeHeight = 512;
	 
	 class Pipe {
		 int x = pipeX;
		 int y = pipeY;
		 int width = pipeWidth;
		 int height = pipeHeight;
		 Image img;
		 boolean passed = false;
		 
		 Pipe(Image img) {
			 this.img = img;
		 }
	 }
	 
	 //lgc do jogo
	 Bird bird;
	 int velocityX = -4;
	 int velocityY = 0;
	 int gravity = 1;
	 
	 ArrayList<Pipe> pipes;
	 Random random = new Random();
	 
	 Timer gameLoop;
	 Timer placePipesTimer;
	 Timer test;
	 
	 boolean gameOver = false;
	 double score = 0;
	 int imageIndex = 0;

	 
	 FlappyBird(){
		 setPreferredSize(new Dimension(boardWidth, boardHeigth));
		 //setBackground(Color.blue);    
		 
		 setFocusable(true);
		 addKeyListener(this);
		 
		 backgroundImg = new ImageIcon(getClass().getResource("flappybirdbg.png")).getImage();
//		 birdImg = new ImageIcon(getClass().getResource("flappybird.png")).getImage();
		 topPipeImg = new ImageIcon(getClass().getResource("toppipe.png")).getImage();
		 bottomPipeImg = new ImageIcon(getClass().getResource("bottompipe.png")).getImage();
		 
		 String[] images = {"flappybird-0.png", "flappybird-1.png","flappybird-2.png"};
		 
		 Image[] icons = new Image[3];
		 for (int i = 0; i < 3; i++) {
			icons[i] = new ImageIcon(getClass().getResource(images[i])).getImage();
		}
		 
		 birdImg = icons[0];
		 
		 //Bird
		 bird = new Bird(birdImg);
		 pipes = new ArrayList<Pipe>();
		 
		 //Place Pipes timer
		 placePipesTimer = new Timer(1500, new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 placePipes();
			 }
		 });
		 placePipesTimer.start();
		 
		 test = new Timer(100, new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 bird.img = icons[imageIndex++];
				 imageIndex = imageIndex >= 3 ? 0 : imageIndex;
			 }
		 });
		 test.start();
		 
		 gameLoop = new Timer(1000/60, this);
		 gameLoop.start();
	 }
	 
	 public void placePipes() {
		 //(0-1) * pipeHeight/2 -> (0-256)
		 //128
		 //0 - 128 - (0-256) --> 1/4 pipeHeight -> 3/4 pipeHeight
		 
		 int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
		 int openingSpace = boardHeigth/4;
		 
		 Pipe topPipe = new Pipe(topPipeImg);
		 topPipe.y = randomPipeY;
		 pipes.add(topPipe);
		 
		 Pipe bottomPipe = new Pipe(bottomPipeImg);
		 bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
		 pipes.add(bottomPipe);
	 }
	 
	 public void paintComponent(Graphics g) {
		 super.paintComponent(g);
		 draw(g);
	 }
	 
	 public void draw(Graphics g) {
		 //fundo
		 g.drawImage(backgroundImg, 0 ,0, boardWidth, boardHeigth, null);
		 
		 //Bird
		 g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.heigth, null);
	 
		 //pipes
		 for (int i = 0; i < pipes.size(); i++) {
			 Pipe pipe = pipes.get(i);
			 g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
		 }
		  
		 //score
		 g.setColor(Color.white);
		 g.setFont(new Font("Arial", Font.PLAIN, 32));
		 if (gameOver) {
			 g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
		 }
		 else {
			 g.drawString(String.valueOf((int) score), 10, 35);
		 }
		 
	 }
	 
	 public void move() {
		 //bird
		 velocityY += gravity;
		 bird.y +=velocityY;
		 bird.y = Math.max(bird.y, 0);
		 
		 //pipes
		 for(int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			pipe.x += velocityX;
			
			if (!pipe.passed && bird.x > pipe.x + pipe.width) {
				pipe.passed = true;
				score += 0.5;
			}
			
			if(collision(bird, pipe)) {
				gameOver = true;
			}
		 }
		 
		 if (bird.y > boardHeigth) {
			 gameOver = true;
		 }
		  
	 }
	 
	 public boolean collision(Bird a, Pipe b) {
		 return a.x < b.x + b.width && 
				 a.x + a.width > b.x &&
				 a.y < b.y + b.height &&
				 a.y + a.heigth > b.y;
	 }
	 

	@Override
	public void actionPerformed(ActionEvent e) {
		move();
		repaint();
		
		if(gameOver) {
			placePipesTimer.stop();
			gameLoop.stop();
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			velocityY = -9;
			if (gameOver) {
				//restart the game
				bird.y = birdY;
				velocityY = 0;
				pipes.clear();
				score = 0;
				gameOver = false;
				gameLoop.start();
				placePipesTimer.start();
			}
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}
}
