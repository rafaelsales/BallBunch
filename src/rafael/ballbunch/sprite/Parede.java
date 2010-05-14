package rafael.ballbunch.sprite;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import rafael.ballbunch.core.Balls;
import rafael.ballbunch.resource.ImageLoader;
import rafael.ballbunch.state.Jogando;


/**
 * Esta classe representa o Sprite das paredes.
 * @author Rafael Sales
 */
public class Parede implements ISprite {
	public enum Posicao {
		Esquerda,
		Direita,
		Topo
	}
	private static List<Parede> sprites = new ArrayList<Parede>();
	
	/**
	 * Cria e posiciona as paredes do cen�rio. 
	 */
	public static void geraParedes(){
		Parede parede;
		int alturaTela = Balls.AREA.getSize().height;
		
		// Parede do topo
		/*parede = new Parede(larguraTela, 20);
		parede.posicao = Posicao.Topo;
		parede.setImagem("Parede_2");
		sprites.add(parede);
		paredeTopo = parede;*/
		
		// Parede esquerda
		parede = new Parede(50, alturaTela);
		parede.posicao = Posicao.Esquerda;
		parede.setImagem("Parede_1");
		sprites.add(parede);
		
		// Parede direita
		parede = new Parede(50, alturaTela);
		parede.posicao = Posicao.Direita;
		parede.setImagem("Parede_1");
		sprites.add(parede);
	}
	
	public static void posicionaParedes(){
		int larguraTela = Balls.AREA.getSize().width;
		int xTela = Jogando.areaDesenho.x;
		for (Parede parede : Parede.getSprites()) {
			if (parede.getPosicao() == Posicao.Esquerda){
				parede.setX(xTela - parede.getSize().width);
			} else if (parede.getPosicao() == Posicao.Direita){
				parede.setX(larguraTela - 50);
			}
			parede.setArea();
		}
	}
	
	public static List<Parede> getSprites() {
		return sprites;
	}
	public static void renderAll(Graphics2D g2d) {
		for (Parede sprite : sprites) {
			g2d.drawImage(sprite.getImagem(), sprite.getX(), sprite.getY(),
					sprite.getSize().width, sprite.getSize().height, null);
		}
	}
	public static Parede paredeTopo;
	private Dimension size;
	private int x = 0;
	private int y = 0;
	private Posicao posicao;
	private Image imagem;
	private Rectangle2D area = new Rectangle2D.Float();
	
	public Parede(int largura, int altura) {
		this.size = new Dimension(largura, altura);
		this.setImagem(ImageLoader.getInst().getImagem("Parede_1"));
	}

	public boolean colidiuCom(ISprite outroCorpo) {
		return false;
	}

	public Rectangle2D getArea(){
		return area;
	}

	public Image getImagem() {
		return imagem;
	}

	public Dimension getSize() {
		return size;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	private void setArea(){
		area.setFrame(new Point(x,y), size);
	}

	public void setImagem(String idImagem) {
		setImagem(ImageLoader.getInst().getImagem(idImagem));
	}
	
	public void setImagem(Image imagem) {
		this.imagem = imagem;
		if (imagem != null) {
			this.size = new Dimension(
					imagem.getWidth(null),
					imagem.getHeight(null));
		}
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}

	public Posicao getPosicao() {
		return posicao;
	}
}
