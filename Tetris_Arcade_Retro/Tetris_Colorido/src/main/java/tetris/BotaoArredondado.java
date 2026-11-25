package tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.ButtonModel;
import javax.swing.border.EmptyBorder;

public class BotaoArredondado extends JButton {
    private final boolean destaque;
    public BotaoArredondado(String texto) { this(texto, false); }
    public BotaoArredondado(String texto, boolean destaque) {
        super(texto);
        this.destaque = destaque;
        setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 14));
        setForeground(destaque ? new Color(10, 31, 56) : new Color(224, 242, 255));
        setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false); setOpaque(false);
        setBorder(new EmptyBorder(12, 10, 12, 10));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ButtonModel m=getModel();
        Color base=destaque ? new Color(255, 211, 66) : new Color(36, 105, 151);
        Color hover=destaque ? new Color(255, 226, 112) : new Color(54, 139, 190);
        Color pressed=destaque ? new Color(224, 174, 36) : new Color(22, 77, 119);
        g2.setColor(m.isPressed()?pressed:(m.isRollover()?hover:base));
        g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
        g2.setColor(destaque?new Color(255,239,170):new Color(111,190,225));
        g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
        g2.dispose(); super.paintComponent(g);
    }
}
