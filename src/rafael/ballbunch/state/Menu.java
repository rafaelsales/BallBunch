package rafael.ballbunch.state;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import rafael.ballbunch.common.GfxUtil;
import rafael.ballbunch.core.Balls;
import rafael.ballbunch.core.CicloJogo;


/**
 * @author Rafael Sales
 */
public class Menu implements IEstado {

	private final String[] opcoes = { "INICIAR JOGO", "HALL OF FAME", "OP��ES",
			"SAIR" };
	private final int MAX_OPCOES = opcoes.length;
	private int opcao;
	private final MenuKeyListener menuKeyListener;
	private final MenuMouseListener menuMouseListener;
	private final MenuMouseMotionListener menuMouseMotionListener;

	public Menu() {
		this.opcao = 0;
		menuKeyListener = new Menu.MenuKeyListener(this);
		menuMouseMotionListener = new Menu.MenuMouseMotionListener(this);
		menuMouseListener = new Menu.MenuMouseListener(this);
		Balls.getInst().addKeyListener(this.menuKeyListener);
		Balls.getInst().addMouseListener(this.menuMouseListener);
		Balls.getInst().addMouseMotionListener(this.menuMouseMotionListener);
	}

	public void finaliza() {
		Balls.getInst().removeKeyListener(this.menuKeyListener);
		Balls.getInst().removeMouseListener(this.menuMouseListener);
		Balls.getInst().removeMouseMotionListener(this.menuMouseMotionListener);
	}

	private void setOpcao(int i) {
		if (i >= MAX_OPCOES)
			i = 0;
		else if (i < 0)
			i = MAX_OPCOES - 1;
		this.opcao = i;
	}

	public synchronized void render(Graphics2D g) {
		Rectangle rMenu;
		Dimension dim = new Dimension();
		Point p = new Point();
		String texto;
		int i;
		Font fonte;
		FontMetrics fMet;
		int tamLinha;
		int disLinha;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setBackground(new Color(0, 0, 0));

		// INICIO---FUNDO DO MENU
		// Define a fonte das op��es do menu:
		fonte = new Font(Font.SANS_SERIF, Font.BOLD, 24);
		// Pega uma instancia de FontMetrics para medir o tamanho dos textos:
		fMet = g.getFontMetrics(fonte);
		tamLinha = fMet.getAscent();
		disLinha = 2 * fMet.getDescent();

		dim.setSize(260, MAX_OPCOES * (tamLinha + disLinha) + 2 * disLinha);
		p = GfxUtil.centralizar(Balls.AREA.getSize(), new Point(0, 0), dim);
		rMenu = new Rectangle(p.x, p.y, dim.width, dim.height);
		g.fillRoundRect(p.x, p.y, dim.width, dim.height, 10, 10);
		g.setColor(new Color(100, 100, 140));
		g.fillRoundRect(p.x, p.y, dim.width, dim.height, 10, 10);
		g.setColor(new Color(140, 140, 180)); // Borda:
		g.drawRoundRect(p.x, p.y, dim.width, dim.height, 10, 10);
		// FIM---FUNDO DO MENU

		// INICIO---FUNDO DA OP��O SELECIONADA
		i = this.opcao;
		texto = opcoes[i];
		i += 1;
		dim = new Dimension(fMet.stringWidth(texto), fMet.getHeight());
		dim.width += 20; // Margem
		p = GfxUtil.centralizar(rMenu.getSize(), rMenu.getLocation(), dim);
		g.setColor(new Color(60, 60, 100));
		g.fillRoundRect(p.x, rMenu.y + i * (disLinha) + (i - 1) * (tamLinha),
				dim.width, dim.height, 40, 40);
		g.setColor(new Color(160, 160, 200)); // Borda:
		g.drawRoundRect(p.x, rMenu.y + i * (disLinha) + (i - 1) * (tamLinha),
				dim.width, dim.height, 40, 40);
		// FIM---FUNDO DA OP��O SELECIONADA

		// INICIO---TEXTOS DO MENU
		g.setColor(new Color(240,240,255));
		g.setFont(fonte);
		i = 0;
		// Texto Iniciar:
		texto = opcoes[i++];
		dim = new Dimension(fMet.stringWidth(texto), fMet.getHeight());
		p = GfxUtil.centralizar(rMenu.getSize(), rMenu.getLocation(), dim);
		g.drawString(texto, p.x, rMenu.y + i * (tamLinha + disLinha));

		// Texto Hall Of Fame:
		texto = opcoes[i++];
		dim = new Dimension(fMet.stringWidth(texto), fMet.getHeight());
		p = GfxUtil.centralizar(rMenu.getSize(), rMenu.getLocation(), dim);
		g.drawString(texto, p.x, rMenu.y + i * (tamLinha + disLinha));

		// Texto Opcoes:
		texto = opcoes[i++];
		dim = new Dimension(fMet.stringWidth(texto), fMet.getHeight());
		p = GfxUtil.centralizar(rMenu.getSize(), rMenu.getLocation(), dim);
		g.drawString(texto, p.x, rMenu.y + i * (tamLinha + disLinha));

		// Texto Sair:
		texto = opcoes[i++];
		dim = new Dimension(fMet.stringWidth(texto), fMet.getHeight());
		p = GfxUtil.centralizar(rMenu.getSize(), rMenu.getLocation(), dim);
		g.drawString(texto, p.x, rMenu.y + i * (tamLinha + disLinha));
		// FIM---TEXTOS DO MENU

		// INICIO---T�TULO DO JOGO
		// Define a fonte do t�tulo:
		fonte = GfxUtil.criaFonte(true, "comic.ttf");
		fonte = fonte.deriveFont(Font.BOLD, 60);
		g.setFont(fonte);
		// Pega uma instancia de FontMetrics para medir o tamanho dos textos:
		fMet = g.getFontMetrics(fonte);
		tamLinha = fMet.getAscent();
		disLinha = 2 * fMet.getDescent();
		texto = "BALLz";
		dim = new Dimension(fMet.stringWidth(texto), fMet.getHeight());
		p = GfxUtil.centralizar(rMenu.getSize(), rMenu.getLocation(), dim);
		g.setColor(new Color(180, 180, 250)); // Sombra:
		g.drawString(texto, p.x + 2, Balls.AREA.y + tamLinha + 2);
		g.setColor(new Color(250, 220, 110));
		g.drawString(texto, p.x, Balls.AREA.y + tamLinha);
		// FIM---T�TULO DO JOGO
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.balls.states.StateLoop#update()
	 */
	public synchronized void loop() {
	}

	private void entraOpcao() {
		System.out.println("Opcao escolhida no menu: " + this.opcoes[opcao]);
		switch (this.opcao) {
		case 0:
			CicloJogo.setProxEstado(new Jogando());
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			CicloJogo.getInst().sair();
			break;
		}
		this.finaliza();
	}

	private class MenuKeyListener extends KeyAdapter {
		private final Menu menu;

		public MenuKeyListener(Menu menu) {
			this.menu = menu;
		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				menu.setOpcao(menu.opcao + 1);
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				menu.setOpcao(menu.opcao - 1);

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				menu.entraOpcao();
			}
		}
	}

	private class MenuMouseListener extends MouseAdapter {
		private final Menu menu;

		public MenuMouseListener(Menu menu) {
			this.menu = menu;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				menu.entraOpcao();
		}
	}

	private class MenuMouseMotionListener extends MouseMotionAdapter {
		private final Menu menu;
		private final int velocidade = 25;
		private int yAnterior = 0;
		private int yDiferenca;

		public MenuMouseMotionListener(Menu menu) {
			this.menu = menu;
		}

		public void mouseMoved(MouseEvent e) {
			yDiferenca = e.getY() - yAnterior;
			if (Math.abs(yDiferenca) < velocidade)
				return;
			if (yDiferenca < 0)
				menu.setOpcao(menu.opcao - 1);
			else
				menu.setOpcao(menu.opcao + 1);

			yAnterior = e.getY();
		}
	}

}
