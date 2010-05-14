package rafael.ballbunch.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import rafael.ballbunch.resource.ImageLoader;
import rafael.ballbunch.state.IEstado;
import rafael.ballbunch.state.Jogando;
import rafael.ballbunch.state.Menu;


/**
 * @author Rafael Sales
 */
public class CicloJogo extends JPanel implements Runnable, KeyListener {

	private static CicloJogo instancia;
	public static Rectangle area;
	private static final long serialVersionUID = 1L;
	private static IEstado proxEstado;
	private final int FPS_JOGANDO = 60;
	private final int FPS_ESTATICO = 20;
	private int periodo = this.calculaPeriodo(FPS_ESTATICO);
	private final int MAX_NO_DELAYS_PER_YELD = 16;
	private final int MAX_FRAME_SKIPS = 5;
	private Balls janelaPai;
	private Thread threadGame;
	private IEstado estado;
	private boolean looping;

	private Graphics doubleGc;
	private Image doubleImage = null;

	public CicloJogo(Balls janelaPai) {
		CicloJogo.instancia = this;
		this.janelaPai = janelaPai;
	}
	
	public void iniciar(){	
		// Carrega as imagens
		new ImageLoader("jogando.xml");
	
		/*
		 * Cria uma nova Thread para rodar o jogo e passa como argumento uma
		 * instancia de Game que roda o jogo
		 */
		threadGame = new Thread(this);
		threadGame.start();
	}

	int count = 0;

	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		this.iniciaCiclo();
	}
		
	private void iniciaCiclo(){
		System.out.print("Carregando.");
		try {
			while (!janelaPai.isVisible()){
				Thread.sleep(50);
				System.out.print('.');
			}
		} catch (Exception e) {}
		System.out.println();
		
		CicloJogo.area = new Rectangle(this.getLocation(), this.getSize());
		// Estado Inicial:
		
		long beforeTime;
		int sleepTime;
		int excess = 0;
		int noDelays = 0;
		int frameSkips;
		this.looping = true;
		
		CicloJogo.setProxEstado(new Menu());
		estado = CicloJogo.proxEstado;
		
		while (looping) {
			if (this.estado != CicloJogo.proxEstado){
				this.estado.finaliza();
				System.gc();
				this.estado = CicloJogo.proxEstado;
				if (estado instanceof Jogando)
					this.periodo = calculaPeriodo(this.FPS_JOGANDO);
				else
					this.periodo = calculaPeriodo(this.FPS_ESTATICO);
			}
			
			beforeTime = System.nanoTime();
			
			// Faz update do estado
			estado.loop();
			// Desenha os graficos no buffer:
			this.gameRender();

			// Desenha o buffer na tela:
			this.paintScreen();

			sleepTime = calcSleepTime(beforeTime);
			if (sleepTime > 0) {
				beforeTime = System.nanoTime();
				try {
					// P�ra a thread do jogo para permitir que outras fa�am o
					// que tem que fazer
					Thread.sleep(sleepTime);
				} catch (Exception e) {
				}
			} else {
				// System.out.println("sleepTime out");
				excess -= sleepTime;
				noDelays++;
				if (noDelays >= this.MAX_NO_DELAYS_PER_YELD) {
					noDelays = 0;
					// Da passagem a outras threads:
					Thread.yield();
				}
			}
			frameSkips = 0;
			while (excess > this.periodo && frameSkips < this.MAX_FRAME_SKIPS) {
				excess -= periodo;
				estado.loop();
				frameSkips++;
				//System.out.println(frameSkips + " FrameSkiping ");
			}
		}
	}
	
	public synchronized void sair(){
		this.looping = false;
		
		try {
			threadGame.join();
		} catch (Exception ex) {
			System.out.println("Erro ao sair");
		} finally {
			threadGame = null;
		}
		if (janelaPai != null)
			janelaPai.dispose();
		Balls.G_DEVICE.setFullScreenWindow(null);
		System.exit(0);
	}

	/** Desenha no Double Buffer.
	 */
	public void gameRender() {
		if (doubleImage == null) {
			doubleImage = this.createImage(this.getWidth(), this.getHeight());
			if (doubleImage == null) {
				System.err.println("dbImage is null");
				return;
			} else {
				doubleGc = doubleImage.getGraphics();
			}
		}
		// Limpa a tela para nao ficar com rastros:
		doubleGc.clearRect(0, 0, this.getWidth(), this.getHeight());

		/*
		 * Chama o render do estado passando um objeto Graphics n�o corrente
		 * para ser renderizado.
		 */
		estado.render((Graphics2D) doubleGc);
	}

	/** Desenha a imagem do Double Buffering na tela.
	 */
	private void paintScreen() {
		try {
			Graphics gc = this.getGraphics();
			if ((gc != null) && (doubleImage != null))
				gc.drawImage(doubleImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
			gc.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Calcula a duracao de um evento.<br>
	 * Usado para calcular a pausa da Thread de acordo com o FPS.
	 * @param beforeTime - nanoTime() obtido anteriormente ao evento.
	 * @return intervalo de tempo (em ms) entre <b>beforeTime</b> e o instante da chamada do m�todo. 
	 */
	public int calcSleepTime(long beforeTime) {
		long time = 0;
		time = (System.nanoTime() - beforeTime) / 1000000L;
		time = periodo - time;

		return (int) time;
	}
	
	//KeyListener...
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
				if (this.estado instanceof Menu)
					this.sair();
				else if (this.estado instanceof Jogando){
					CicloJogo.proxEstado = new Menu();
				}
			break;
		case KeyEvent.VK_P:
			if (!(this.estado instanceof Jogando))
				break;
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}

	public static void setProxEstado(IEstado proxEstado) {
		CicloJogo.proxEstado = proxEstado;
	}

	public static CicloJogo getInst() {
		return instancia;
	}

	public int calculaPeriodo(int novoFPS) {
		return (int)(1000d/novoFPS);
	}
}
