package rafael.ballbunch.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import rafael.ballbunch.common.GfxUtil;
import rafael.ballbunch.core.Balls;
import rafael.ballbunch.input.Action;
import rafael.ballbunch.input.KeyManager;
import rafael.ballbunch.input.MouseManager;
import rafael.ballbunch.state.Jogando;


/**
 * Esta classe representa o Sprite do Canh�o al�m de
 * gerenciar o movimento da bola que est� com o jogador.
 * @author Rafael Sales.
 */
public class Canhao extends Action implements ISprite {

	private static Canhao instancia;

	public static Canhao getInst() {
		if (instancia == null)
			instancia = new Canhao();
		return instancia;
	}

	public static void renderAll(Graphics2D g2d) {
		Canhao c = Canhao.getInst();
		g2d.setColor(Color.WHITE);
		g2d.fill(c.area);
		g2d.setColor(Color.BLACK);
		g2d.drawLine(c.pAngulo.x, c.pAngulo.y, c.pDestino.x, c.pDestino.y);
		//g2d.drawLine(c.pBola.x, c.pBola.y, (int)(c.pBola.x + c.cosAng * 400),
		//		(int)(c.pBola.y - c.sinAng* 400));
		
		/*for (int i = 1; i < 10; i++) {
			g2d.drawOval((int)(c.pBola.x + c.cosAng*i*36), (int)(c.pBola.y - c.sinAng*i*36), 36, 36);
		}*/
	}

	private Rectangle area;
	//private AffineTransform aTrans;
	private Bola bola;
	private double cosAng;
	private final Point pAngulo;
	private final Point pBola;
	private Point pDestino = new Point(0,0);
	private double sinAng;
	//private Image imagem;
	private final int velocidade = 14;
	
	public Canhao() {
		//Pega o Retangulo que define a area interna da janela:
		Dimension tamTela = Jogando.areaDesenho.getSize();
		Point pTela = Jogando.areaDesenho.getLocation();
		//Define o tamanho do canh�o:
		Dimension dim = new Dimension(50, 50);
		//Define o ponto do canhao na tela:
		Point ponto = GfxUtil.centralizar(tamTela, pTela, dim);
		ponto.y = tamTela.height - dim.height;
		//Define a area do Canhao:
		this.area = new Rectangle(ponto, dim);
		//Define o ponto onde as bolas ficam no canh�o:
		this.pBola = GfxUtil.centralizar(this.area.getSize(), this.area.getLocation(), new Dimension(2*Bola.RAIO,2*Bola.RAIO));
		this.pAngulo = new Point();
		this.pAngulo.x = pBola.x + Bola.RAIO;
		this.pAngulo.y = pBola.y + Bola.RAIO;
	}

	private void calculaAngulacao(MouseEvent e){
		int xMouse = e.getX() - Balls.AREA.x;
		int yMouse = e.getY() - Balls.AREA.y;
		int xAng = xMouse - this.pBola.x;
		int yAng = this.pBola.y - yMouse;

		double tan = yAng*1d/xAng;
		double angulo = Math.toDegrees(Math.atan(tan));
		if (xAng < 0)
			angulo += 180;
		
		//Verifica se o mouse est� abaixo do canhao:
		if (angulo >= 175) {
			angulo = 175;		
		} else if (angulo <= 5) {
			angulo = 5;
		}
		//Apontador:
		if (angulo > 5 && angulo < 175) {
			this.pDestino.x = xMouse + Bola.RAIO;
			this.pDestino.y = yMouse + Bola.RAIO;
		}
		//System.out.printf("\n x = %d , y = %d  => tang = %f, Angulo = %f", xAng, yAng, tan, angulo);
		angulo = Math.toRadians(angulo);
		this.sinAng = Math.sin(angulo);
		this.cosAng = Math.cos(angulo);
	}

	public boolean colidiuCom(ISprite outroCorpo) {
		return false;
	}

	public Rectangle2D getArea() {
		return this.area;
	}

	public Bola getBola() {
		return bola;
	}

	public Image getImagem() {
		// TODO Auto-generated method stub
		return null;
	}

	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void moveBola(){
		bola.setX(bola.getX() + cosAng * velocidade);
		bola.setY(bola.getY() -  sinAng * velocidade);
		Rectangle area = Jogando.areaDesenho;
		if (bola.getX() <= area.x && this.cosAng < 0 ||
				bola.getX() + 2*Bola.RAIO >= area.x + area.width)
			cosAng *= -1;
	}

	public void setBola(Bola bola) {
		this.bola = bola;
		bola.setX(this.pBola.x);
		bola.setY(this.pBola.y);
		KeyManager.getInst().addCorpo(bola);
		MouseManager.getInst().addCorpoControlado(bola);
	}
	
	public void setImagem(Image imagem) {
	}
	
	/** "Executa" os movimentos referente as teclas que est�o/foram pressionadas */
	public void update() {
		if (mouseMotionEvent != null) {
			this.calculaAngulacao(mouseMotionEvent);
			mouseMotionEvent = null;
		}
		
		if (mouseEvent != null) {
			if (mouseEvent.getButton() == MouseEvent.BUTTON1){
				this.calculaAngulacao(mouseEvent);
				this.bola.setEstado(Bola.Estado.Atirada);
				KeyManager.getInst().removeCorpo(this.bola);
				MouseManager.getInst().removeCorpoControlado(this.bola);
			}
			mouseEvent = null;
		}
	}
}
