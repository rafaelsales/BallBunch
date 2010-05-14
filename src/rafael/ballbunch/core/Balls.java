package rafael.ballbunch.core;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import rafael.ballbunch.input.KeyManager;
import rafael.ballbunch.input.MouseManager;


/**
 * @author Rafael Sales
 */
public class Balls extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final boolean FULLSCREEN = false;
	private static final boolean MOUSE_HIDE = true;
	private static final String OS_NAME = System.getProperty("os.name").toUpperCase();
	/** tamanho - Tamanho do frame do jogo */
	public static final Dimension TAMANHO = new Dimension(640, 480);
	/** area - Espaco de desenho na tela */
	public static final Rectangle AREA = new Rectangle(TAMANHO);
	/** dMode - Resolucao para modo FullScreen */
	private static final DisplayMode D_MODE = new DisplayMode(640, 480, 16, DisplayMode.REFRESH_RATE_UNKNOWN);
	public static final GraphicsEnvironment G_ENVRIOM = GraphicsEnvironment
			.getLocalGraphicsEnvironment();
	public static final GraphicsDevice G_DEVICE = G_ENVRIOM
			.getDefaultScreenDevice();
	private static Balls instancia = null;

	public static void main(String args[]) {
		// Cria a janela do jogo:
		Balls.instancia = new Balls(TAMANHO.width, TAMANHO.height);
	}
	
	public static Balls getInst(){
		return instancia;
	}

	private CicloJogo jogo;

	public Balls(int largura, int altura) {
		// c = ponto de centraliza��o da janela
		Point c = G_ENVRIOM.getCenterPoint();
		c.x -= largura / 2;
		c.y -= altura / 2;

		// Configura a janela:
		this.setLocation(c);
		this.setSize(largura, altura);
		this.setTitle("Balls - Por Rafael Sales");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setIgnoreRepaint(true);
		this.setResizable(false);

		if (MOUSE_HIDE)
			this.setCursor(this.setMouseHiding());

		// Chama o jogo:
		this.criaCicloJogo();

		if (FULLSCREEN && G_DEVICE.isFullScreenSupported())
			this.setFullScreen();
		else {
			if (FULLSCREEN) {
				System.err.println("Modo tela cheia n�o suportado!");
				System.out.println("Carregando no modo janela.");
			}
			// Retira as bordas da janela da area de desenho
			this.setVisible(true);
			Insets bordas = this.getInsets();
			Balls.AREA.height -= (bordas.top + bordas.bottom);
			Balls.AREA.width -= (bordas.right + bordas.left);
			Balls.AREA.x += bordas.left;
			Balls.AREA.y += bordas.top;
		}

		this.jogo.iniciar();
	}

	private synchronized void criaCicloJogo() {
		this.jogo = new CicloJogo(this);
		this.addKeyListener(KeyManager.getInst());
		this.addMouseListener(MouseManager.getInst());
		this.addMouseMotionListener(MouseManager.getInst());
		this.addKeyListener(jogo);
		this.add(jogo);
		
		if (Balls.OS_NAME.indexOf("LINUX") != -1)
			System.err.println("Rodando no Linux");
		else if (Balls.OS_NAME.indexOf("WINDOWS") != -1)
			System.err.println("Rodando no Windows");
	}

	private synchronized void setFullScreen() {
		this.setUndecorated(true);
		G_DEVICE.setFullScreenWindow(this);
		//Cria uma Thread para entrar no modo FullScreen.
		//Evita travamento em alguns PCs.
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					G_DEVICE.setDisplayMode(D_MODE);
					Thread.currentThread().join();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		);
		t.start();
		Thread.yield();
		
		/*DisplayMode[] dm = G_DEVICE.getDisplayModes();
		  System.out.println("Display Modes:"); for (int i = 0; i < dm.length;
		  i++) { System.out.println(i + " " + dm[i].getWidth() + " " +
		  dm[i].getHeight() + " " + dm[i].getBitDepth() + " " +
		  dm[i].getRefreshRate()); }*/
	}

	private Cursor setMouseHiding() {
		BufferedImage bufImgCursor = new BufferedImage(1, 1,
				BufferedImage.TRANSLUCENT);
		ImageIcon alphaImg = new ImageIcon(bufImgCursor);
		return Toolkit.getDefaultToolkit().createCustomCursor(
				alphaImg.getImage(), new Point(0, 0), "CursorTransparente");
	}
}
