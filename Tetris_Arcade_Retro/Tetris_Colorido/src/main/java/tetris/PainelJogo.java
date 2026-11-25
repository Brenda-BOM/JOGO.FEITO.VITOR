package tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PainelJogo extends JPanel {
    private static final int TAMANHO = 28;
    private final JanelaJogo janela;
    private final Tabuleiro tabuleiro = new Tabuleiro();
    private final Random aleatorio = new Random();
    private final Timer temporizador;

    private Peca pecaAtual;
    private int proximaForma;
    private int movimentos;
    private int linhas;
    private int pontos;
    private int recorde;
    private int velocidadeBase;
    private String dificuldade = "-";
    private boolean fimDeJogo;
    private boolean pausado;
    private boolean partidaIniciada;

    private final int[][][] formas = {
        {{1,1,1,1}},
        {{1,1},{1,1}},
        {{0,1,0},{1,1,1}},
        {{1,0,0},{1,1,1}},
        {{0,0,1},{1,1,1}},
        {{1,1,0},{0,1,1}},
        {{0,1,1},{1,1,0}}
    };

    private final Color[] cores = {
        new Color(92, 190, 231), new Color(255, 211, 66),
        new Color(56, 145, 199), new Color(134, 218, 245),
        new Color(255, 226, 112), new Color(37, 119, 173),
        new Color(174, 230, 250)
    };

    public PainelJogo(JanelaJogo janela) {
        this.janela = janela;
        setPreferredSize(new Dimension(Tabuleiro.COLUNAS * TAMANHO + 2,
                Tabuleiro.LINHAS * TAMANHO + 2));
        setBackground(new Color(7, 20, 38));
        setFocusable(true);

        temporizador = new Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (partidaIniciada && !fimDeJogo && !pausado) {
                    descerAutomaticamente();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!partidaIniciada || fimDeJogo || pausado) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> mover(-1);
                    case KeyEvent.VK_RIGHT -> mover(1);
                    case KeyEvent.VK_UP -> girar();
                    case KeyEvent.VK_DOWN -> descerUmaLinha();
                    case KeyEvent.VK_SPACE -> quedaRapida();
                }
            }
        });
    }

    public void iniciarNovaPartida(String nivel) {
        tabuleiro.limpar();
        movimentos = 0;
        linhas = 0;
        pontos = 0;
        fimDeJogo = false;
        pausado = false;
        partidaIniciada = true;
        dificuldade = nivel == null ? "Médio" : nivel;

        if (dificuldade.equals("Fácil")) {
            velocidadeBase = 850;
        } else if (dificuldade.equals("Difícil")) {
            velocidadeBase = 350;
        } else {
            velocidadeBase = 600;
        }

        pecaAtual = null;
        proximaForma = aleatorio.nextInt(formas.length);
        criarPeca();
        temporizador.setDelay(velocidadeBase);
        temporizador.setInitialDelay(velocidadeBase);
        temporizador.start();
        atualizarPainel();
        requestFocusInWindow();
        repaint();
    }

    public void recomecar() {
        iniciarNovaPartida(dificuldade);
    }

    public void alternarPausa() {
        if (!partidaIniciada || fimDeJogo) return;
        pausado = !pausado;
        atualizarPainel();
        repaint();
        requestFocusInWindow();
    }

    private void criarPeca() {
        int indice = proximaForma;
        pecaAtual = new Peca(formas[indice], cores[indice]);
        proximaForma = aleatorio.nextInt(formas.length);
        janela.atualizarProximaPeca(formas[proximaForma], cores[proximaForma]);
        if (!tabuleiro.podePosicionar(pecaAtual)) encerrarJogo();
    }

    public void mover(int direcao) {
        if (!podeJogar()) return;
        pecaAtual.mover(direcao, 0);
        if (!tabuleiro.podePosicionar(pecaAtual)) pecaAtual.mover(-direcao, 0);
        else registrarMovimento();
        repaint();
        requestFocusInWindow();
    }

    public void girar() {
        if (!podeJogar()) return;
        pecaAtual.girar();
        if (!tabuleiro.podePosicionar(pecaAtual)) {
            for (int i = 0; i < 3; i++) pecaAtual.girar();
        } else registrarMovimento();
        repaint();
        requestFocusInWindow();
    }

    public void descerUmaLinha() {
        if (!podeJogar()) return;
        pecaAtual.mover(0, 1);
        if (!tabuleiro.podePosicionar(pecaAtual)) {
            pecaAtual.mover(0, -1);
            fixarPeca();
        } else registrarMovimento();
        repaint();
        requestFocusInWindow();
    }

    public void quedaRapida() {
        if (!podeJogar()) return;
        int distancia = 0;
        while (true) {
            pecaAtual.mover(0, 1);
            if (!tabuleiro.podePosicionar(pecaAtual)) {
                pecaAtual.mover(0, -1);
                break;
            }
            distancia++;
        }
        pontos += distancia * 2;
        registrarMovimento();
        fixarPeca();
        repaint();
        requestFocusInWindow();
    }

    private boolean podeJogar() {
        return partidaIniciada && !fimDeJogo && !pausado && pecaAtual != null;
    }

    private void descerAutomaticamente() {
        pecaAtual.mover(0, 1);
        if (!tabuleiro.podePosicionar(pecaAtual)) {
            pecaAtual.mover(0, -1);
            fixarPeca();
        }
        repaint();
    }

    private void fixarPeca() {
        tabuleiro.fixar(pecaAtual);
        int removidas = tabuleiro.removerLinhasCompletas();
        linhas += removidas;

        if (removidas == 1) pontos += 100;
        else if (removidas == 2) pontos += 300;
        else if (removidas == 3) pontos += 500;
        else if (removidas >= 4) pontos += 800;

        if (pontos > recorde) recorde = pontos;

        int velocidadeAtual = Math.max(130, velocidadeBase - (linhas / 2) * 35);
        temporizador.setDelay(velocidadeAtual);

        criarPeca();
        atualizarPainel();
    }

    private void registrarMovimento() {
        movimentos++;
        atualizarPainel();
    }

    private void atualizarPainel() {
        String status;
        if (!partidaIniciada) status = "Pronto para jogar!";
        else if (fimDeJogo) status = "Fim de jogo!";
        else if (pausado) status = "Jogo pausado";
        else status = "Em andamento";
        janela.atualizarInformacoes(movimentos, linhas, pontos, recorde, dificuldade, status);
    }

    private void encerrarJogo() {
        fimDeJogo = true;
        temporizador.stop();
        atualizarPainel();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tamanho = Math.max(10, Math.min(getWidth() / Tabuleiro.COLUNAS,
                getHeight() / Tabuleiro.LINHAS));
        int larguraTabuleiro = Tabuleiro.COLUNAS * tamanho;
        int alturaTabuleiro = Tabuleiro.LINHAS * tamanho;
        int inicioX = (getWidth() - larguraTabuleiro) / 2;
        int inicioY = (getHeight() - alturaTabuleiro) / 2;

        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                int x = inicioX + coluna * tamanho;
                int y = inicioY + linha * tamanho;
                Color cor = tabuleiro.getCelulas()[linha][coluna];
                desenharBloco(g2, x, y, tamanho,
                        cor == null ? new Color(13, 39, 65) : cor);
            }
        }

        if (pecaAtual != null && !fimDeJogo && partidaIniciada) {
            int[][] forma = pecaAtual.getForma();
            for (int linha = 0; linha < forma.length; linha++) {
                for (int coluna = 0; coluna < forma[linha].length; coluna++) {
                    if (forma[linha][coluna] != 0) {
                        desenharBloco(g2, inicioX + (pecaAtual.getX() + coluna) * tamanho,
                                inicioY + (pecaAtual.getY() + linha) * tamanho,
                                tamanho, pecaAtual.getCor());
                    }
                }
            }
        }

        g2.setColor(new Color(75, 156, 197));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(inicioX, inicioY, larguraTabuleiro, alturaTabuleiro);

        if (!partidaIniciada) desenharMensagem(g2, "TETRIS COLORIDO", "Escolha a dificuldade e clique em Jogar");
        else if (fimDeJogo) desenharMensagem(g2, "FIM DE JOGO!", "Clique em Jogar / Reiniciar para tentar novamente");
        else if (pausado) desenharMensagem(g2, "PAUSADO", "Clique em Pausar / Continuar para voltar");
        g2.dispose();
    }

    private void desenharBloco(Graphics2D g2, int x, int y, int tamanho, Color cor) {
        g2.setColor(new Color(5, 17, 32));
        g2.fillRect(x, y, tamanho, tamanho);
        g2.setColor(cor);
        g2.fillRect(x + 2, y + 2, tamanho - 4, tamanho - 4);
        g2.setColor(new Color(255, 255, 255, 65));
        g2.drawLine(x + 3, y + 3, x + tamanho - 4, y + 3);
        g2.drawLine(x + 3, y + 3, x + 3, y + tamanho - 4);
        g2.setColor(new Color(4, 22, 39, 110));
        g2.drawLine(x + 3, y + tamanho - 4, x + tamanho - 4, y + tamanho - 4);
        g2.drawLine(x + tamanho - 4, y + 3, x + tamanho - 4, y + tamanho - 4);
    }

    private void desenharMensagem(Graphics2D g2, String titulo, String dica) {
        int largura = getWidth(), altura = getHeight();
        g2.setColor(new Color(5, 19, 35, 225));
        g2.fillRect(16, altura / 2 - 58, largura - 32, 116);
        g2.setColor(new Color(255, 211, 66));
        g2.drawRect(16, altura / 2 - 58, largura - 33, 115);
        g2.setFont(new Font("Monospaced", Font.BOLD, 25));
        int lt = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (largura - lt) / 2, altura / 2 - 8);
        g2.setColor(new Color(151, 218, 245));
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        int ld = g2.getFontMetrics().stringWidth(dica);
        g2.drawString(dica, (largura - ld) / 2, altura / 2 + 20);
    }

}
