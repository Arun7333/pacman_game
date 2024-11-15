import java.awt.*;
import javax.swing.*;
import java.util.HashSet;
import java.awt.event.*;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener{
    
    private int rowCount = 21;
    private int colCount = 19;
    private int tileSize = 32;
    private int boardWidth = colCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image redGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;

    private Image PacmanUpImage;
    private Image PacmanDownImage;
    private Image PacmanLeftImage;
    private Image PacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    char[] directions = new char[] {'U', 'D', 'L', 'R'};
    Random random = new Random();

    //Block for storing walls, foods, ghosts and pacman
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(int x , int y , int w , int h , Image i){
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.image = i;
            this.startX = x;
            this.startY = y;
        }
        
        //update the direction of object
        void updateDirection(char dir){
            char prevDirection = this.direction;
            this.direction = dir;
            updateVelocity();

            //check whether there is wall in that direction
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall : walls){
                if(isCollision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    updateDirection(prevDirection);
                }
            }
        }

        //update the velocity of object according to direction
        void updateVelocity(){

            // velocity sholud be 1/4 times the tileSize
            if(this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = -(tileSize/4);
            }
            else if(this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if(this.direction == 'L'){
                this.velocityX = -(tileSize/4);
                this.velocityY = 0;
            }
            else if(this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }
    }

    //Hashset for managing blocks
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    //Timer to repaint the game in an interval
    Timer gameLoop;

    PacMan(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();

        PacmanUpImage = new ImageIcon(getClass().getResource("./PacmanUp.png")).getImage();
        PacmanDownImage = new ImageIcon(getClass().getResource("./PacmanDown.png")).getImage();
        PacmanLeftImage = new ImageIcon(getClass().getResource("./PacmanLeft.png")).getImage();
        PacmanRightImage = new ImageIcon(getClass().getResource("./PacmanRight.png")).getImage();

        //load the map
        loadmap();
        
        //for every 50 milliseconds it repaints the game
        //(1000/50) = 20fps(frames per second)
        gameLoop = new Timer(50, this);
        gameLoop.start();

    }

    private void loadmap(){
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for(int row = 0; row < rowCount; row ++){
            String curr=tileMap[row];
            
            for(int col = 0; col < colCount; col ++){
                char currChar = curr.charAt(col);
                int x = col * tileSize;
                int y = row * tileSize;

                if(currChar == 'X'){//walls
                    Block wall = new Block(x, y, tileSize, tileSize, wallImage);
                    walls.add(wall);
                }
                else if(currChar == ' '){//foods
                    Block food = new Block(x + 14, y + 14, 4, 4, null);
                    foods.add(food);
                }
                else if(currChar == 'b'){//blueGhost
                    Block blueGhost =new Block(x, y, tileSize, tileSize, blueGhostImage);
                    ghosts.add(blueGhost);
                }
                else if(currChar == 'r'){//redGhost
                    Block redGhost = new Block(x, y, tileSize, tileSize, redGhostImage);
                    ghosts.add(redGhost);
                }
                else if(currChar == 'p'){//pinkGhost
                    Block pinkGhost =new Block(x, y, tileSize, tileSize, pinkGhostImage);
                    ghosts.add(pinkGhost);
                }
                else if(currChar == 'o'){//orangeGhost
                    Block orangeGhost = new Block(x, y, tileSize, tileSize, orangeGhostImage);
                    ghosts.add(orangeGhost);
                }
                else if(currChar == 'P'){//Pacman
                    pacman = new Block(x, y, tileSize, tileSize, PacmanRightImage);
                }
            }
        }
    }

    //Automatically called by Swing
    public void paintComponent(Graphics g){
        //To clear the window by calling its super class
        super.paintComponent(g);
        draw(g);
    }

    //To draw the game map
    public void draw(Graphics g){
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost : ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall : walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        
        g.setColor(Color.WHITE);
        for(Block food : foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }
    }

    //to move the pacman
    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check all walls for collisions
        for(Block wall : walls){
            if(isCollision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }
    }

    //to check the collision of objects
    public boolean isCollision(Block a, Block b){
        return  (
                a.x < b.x + b.width &&
                b.x < a.x + a.width &&
                a.y < b.y + b.height &&
                b.y < a.y + a.height
            );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        //to get the key inputs and update the direction
        if(e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        }

        if(pacman.direction == 'U'){
            pacman.image = PacmanUpImage;
        }
        else if(pacman.direction == 'D'){
            pacman.image = PacmanDownImage;
        }
        else if(pacman.direction == 'L'){
            pacman.image = PacmanLeftImage;
        }
        else if(pacman.direction == 'R'){
            pacman.image = PacmanRightImage;
        }
    }

}
