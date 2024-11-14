import java.awt.*;
import javax.swing.*;
import java.util.HashSet;
import java.awt.event.*;
import java.util.Random;

public class PacMan extends JPanel{
    
    private int rowCount = 21;
    private int colCount = 19;
    private int tileSize = 32;
    private int boardWidth = colCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    PacMan(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
    }



}
