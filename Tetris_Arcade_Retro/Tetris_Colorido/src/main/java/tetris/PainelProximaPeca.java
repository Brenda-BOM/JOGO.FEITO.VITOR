package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class PainelProximaPeca extends JPanel {
    private int[][] forma; private Color cor;
    public PainelProximaPeca(){setPreferredSize(new Dimension(220,95));setBackground(new Color(18,58,91));}
    public void atualizar(int[][] novaForma,Color novaCor){forma=novaForma;cor=novaCor;repaint();}
    @Override protected void paintComponent(Graphics g){super.paintComponent(g);Graphics2D g2=(Graphics2D)g.create();
        g2.setColor(new Color(126,205,239));g2.setFont(new Font("Monospaced",Font.BOLD,11));g2.drawString("NEXT",10,16);
        if(forma!=null&&cor!=null){int tam=18,w=forma[0].length*tam,h=forma.length*tam,x=(getWidth()-w)/2,y=27+(55-h)/2;
            for(int l=0;l<forma.length;l++)for(int c=0;c<forma[l].length;c++)if(forma[l][c]!=0){g2.setColor(cor);g2.fillRect(x+c*tam+1,y+l*tam+1,tam-2,tam-2);g2.setColor(new Color(255,255,255,100));g2.drawRect(x+c*tam+2,y+l*tam+2,tam-4,tam-4);}}
        g2.dispose();}
}
