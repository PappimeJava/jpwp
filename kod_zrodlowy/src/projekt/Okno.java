package projekt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Okno extends Canvas {
    public Okno(int szer, int wys, String tyt, final Main gra){
        JFrame frame = new JFrame(tyt);

        frame.setPreferredSize(new Dimension(szer, wys));
        frame.setMaximumSize(new Dimension(szer, wys));
        frame.setMinimumSize(new Dimension(szer, wys));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(gra);
        frame.setVisible(true);
         
        gra.start();
    }
}
