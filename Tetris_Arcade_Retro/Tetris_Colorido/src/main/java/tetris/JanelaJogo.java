package tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class JanelaJogo extends JFrame {
    private static final Color FUNDO = new Color(8, 25, 47);
    private static final Color PAINEL = new Color(13, 43, 72);
    private static final Color AZUL = new Color(126, 205, 239);
    private static final Color AMARELO = new Color(255, 211, 66);
    private final PainelJogo painelJogo;
    private final JLabel movimentosLabel, dificuldadeLabel, linhasLabel, pontosLabel, recordeLabel, statusLabel;
    private final JComboBox<String> nivelEscolhido;
    private final PainelProximaPeca painelProxima;
    private final GraphicsDevice tela = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private Rectangle tamanhoAnterior;
    private boolean telaCheia;

    public JanelaJogo() {
        setTitle("TETRIS // ARCADE"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setResizable(true);
        getContentPane().setBackground(FUNDO); setLayout(new BorderLayout(16,16));
        JPanel topo=new JPanel(new BorderLayout()); topo.setBackground(FUNDO);
        JLabel titulo=new JLabel("TETRIS  //  ARCADE", SwingConstants.CENTER);
        titulo.setFont(new Font("Monospaced",Font.BOLD,27)); titulo.setForeground(AMARELO);
        titulo.setBorder(BorderFactory.createEmptyBorder(18,8,5,8)); topo.add(titulo,BorderLayout.CENTER);
        JLabel subtitulo=new JLabel("CLASSIC BLOCK PUZZLE",SwingConstants.CENTER);
        subtitulo.setFont(new Font("Monospaced",Font.PLAIN,11)); subtitulo.setForeground(AZUL);
        topo.add(subtitulo,BorderLayout.SOUTH); add(topo,BorderLayout.NORTH);

        painelJogo=new PainelJogo(this); add(painelJogo,BorderLayout.CENTER);
        JPanel lateral=new JPanel(); lateral.setPreferredSize(new Dimension(270,0));
        lateral.setBackground(PAINEL); lateral.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));
        lateral.setLayout(new BoxLayout(lateral,BoxLayout.Y_AXIS));
        JLabel painelTitulo=rotulo("STATUS / SCORE",AMARELO,15); lateral.add(painelTitulo);
        lateral.add(Box.createVerticalStrut(10));
        pontosLabel=info("PONTOS   0"); recordeLabel=info("RECORDE  0"); linhasLabel=info("LINHAS   0");
        movimentosLabel=info("MOVIMENTOS  0"); dificuldadeLabel=info("NÍVEL   -"); statusLabel=info("PRONTO PARA JOGAR");
        lateral.add(pontosLabel); lateral.add(recordeLabel); lateral.add(linhasLabel); lateral.add(movimentosLabel);
        lateral.add(dificuldadeLabel); lateral.add(statusLabel); lateral.add(Box.createVerticalStrut(12));
        lateral.add(rotulo("PRÓXIMA PEÇA",AZUL,12)); lateral.add(Box.createVerticalStrut(5));
        painelProxima=new PainelProximaPeca(); painelProxima.setMaximumSize(new Dimension(240,100));
        painelProxima.setAlignmentX(CENTER_ALIGNMENT); lateral.add(painelProxima); lateral.add(Box.createVerticalStrut(12));
        lateral.add(rotulo("DIFICULDADE",AZUL,12));
        nivelEscolhido=new JComboBox<>(new String[]{"Fácil","Médio","Difícil"});
        nivelEscolhido.setFont(new Font("Monospaced",Font.BOLD,13)); nivelEscolhido.setForeground(Color.WHITE);
        nivelEscolhido.setBackground(new Color(26,78,112)); nivelEscolhido.setMaximumSize(new Dimension(240,34));
        lateral.add(Box.createVerticalStrut(5)); lateral.add(nivelEscolhido); lateral.add(Box.createVerticalStrut(12));
        JButton iniciar=botao("▶  JOGAR / REINICIAR",true);
        iniciar.addActionListener(e->painelJogo.iniciarNovaPartida((String)nivelEscolhido.getSelectedItem())); lateral.add(iniciar);
        lateral.add(Box.createVerticalStrut(7));
        JButton pausar=botao("Ⅱ  PAUSAR / CONTINUAR",false); pausar.addActionListener(e->painelJogo.alternarPausa()); lateral.add(pausar);
        lateral.add(Box.createVerticalStrut(7));
        JButton telaCheiaBtn=botao("⛶  TELA CHEIA  F11",false); telaCheiaBtn.addActionListener(e->alternarTelaCheia()); lateral.add(telaCheiaBtn);
        lateral.add(Box.createVerticalStrut(16)); lateral.add(rotulo("CONTROLES",AMARELO,14));
        JLabel dica=new JLabel("TECLADO  ← →  mover   ↑ girar   ↓ descer   ESPAÇO queda rápida");
        dica.setForeground(AZUL); dica.setFont(new Font("Monospaced",Font.PLAIN,10)); dica.setAlignmentX(CENTER_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(5)); lateral.add(dica); lateral.add(Box.createVerticalStrut(8));
        JPanel grade=new JPanel(new GridLayout(2,2,8,8)); grade.setOpaque(false); grade.setMaximumSize(new Dimension(240,104)); grade.setAlignmentX(CENTER_ALIGNMENT);
        JButton esquerda=botao("◀ ESQUERDA",false); esquerda.addActionListener(e->painelJogo.mover(-1));
        JButton direita=botao("DIREITA ▶",false); direita.addActionListener(e->painelJogo.mover(1));
        JButton girar=botao("↻ GIRAR",true); girar.addActionListener(e->painelJogo.girar());
        JButton descer=botao("▼ DESCER",false); descer.addActionListener(e->painelJogo.descerUmaLinha());
        grade.add(esquerda); grade.add(direita); grade.add(girar); grade.add(descer); lateral.add(grade);
        lateral.add(Box.createVerticalStrut(7));
        JButton queda=botao("⬇  QUEDA RÁPIDA",true); queda.addActionListener(e->painelJogo.quedaRapida()); lateral.add(queda);
        add(lateral,BorderLayout.EAST);
        setMinimumSize(new Dimension(820,650)); setSize(1000,760); setLocationRelativeTo(null);
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F11,0),"fullscreen");
        getRootPane().getActionMap().put("fullscreen",new AbstractAction(){public void actionPerformed(java.awt.event.ActionEvent e){alternarTelaCheia();}});
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"exitfullscreen");
        getRootPane().getActionMap().put("exitfullscreen",new AbstractAction(){public void actionPerformed(java.awt.event.ActionEvent e){if(telaCheia)alternarTelaCheia();}});
    }
    private JLabel rotulo(String t,Color c,int s){JLabel l=new JLabel(t,SwingConstants.CENTER);l.setFont(new Font("Monospaced",Font.BOLD,s));l.setForeground(c);l.setAlignmentX(CENTER_ALIGNMENT);return l;}
    private JLabel info(String t){JLabel l=new JLabel(t,SwingConstants.CENTER);l.setFont(new Font("Monospaced",Font.BOLD,12));l.setForeground(AZUL);l.setOpaque(true);l.setBackground(new Color(18,58,91));l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(37,105,145)),BorderFactory.createEmptyBorder(7,4,7,4)));l.setAlignmentX(CENTER_ALIGNMENT);l.setMaximumSize(new Dimension(240,34));l.setPreferredSize(new Dimension(240,34));l.setMinimumSize(new Dimension(240,34));return l;}
    private BotaoArredondado botao(String t,boolean d){BotaoArredondado b=new BotaoArredondado(t,d);b.setAlignmentX(CENTER_ALIGNMENT);b.setMaximumSize(new Dimension(240,42));b.setPreferredSize(new Dimension(240,42));return b;}
    private void alternarTelaCheia(){if(!telaCheia){tamanhoAnterior=getBounds();dispose();setUndecorated(true);tela.setFullScreenWindow(this);telaCheia=true;setVisible(true);}else{tela.setFullScreenWindow(null);dispose();setUndecorated(false);telaCheia=false;setBounds(tamanhoAnterior);setVisible(true);}painelJogo.requestFocusInWindow();}
    public void atualizarInformacoes(int movimentos,int linhas,int pontos,int recorde,String dificuldade,String status){movimentosLabel.setText("MOVIMENTOS  "+movimentos);linhasLabel.setText("LINHAS   "+linhas);pontosLabel.setText("PONTOS   "+pontos);recordeLabel.setText("RECORDE  "+recorde);dificuldadeLabel.setText("NÍVEL   "+dificuldade.toUpperCase());statusLabel.setText(status.toUpperCase());}
    public void atualizarProximaPeca(int[][] forma,Color cor){painelProxima.atualizar(forma,cor);}
}
