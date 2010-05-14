package rafael.ballbunch.sprite;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rafael.ballbunch.input.Action;
import rafael.ballbunch.resource.ImageLoader;
import rafael.ballbunch.state.Jogando;


/**
 * Esta classe representa o Sprite da bola.
 * @author Rafael Sales
 */
public class Bola extends Action implements ISprite {

	public enum Estado {
		Atirada, NaoAtirada, NoDestino,
	}

	// --- Sub-membros:
	public enum TiposBola {
		Azul, Amarela, Vermelha, Verde
	}

	// --- Membros Est�ticos:
	public static final int RAIO = 18;
	public static final int DIST_VERTICAL = (int) Math.floor(RAIO
			* Math.sqrt(3));
	public static int instancias = 0;
	private static List<Bola> sprites = new LinkedList<Bola>();

	public static List<Bola> getSprites() {
		return sprites;
	}

	public static void renderAll(Graphics2D g2d) {
		for (Bola sprite : sprites) {

			g2d.drawImage(sprite.getImagem(), (int)sprite.getX(), (int)sprite.getY(),
					null);
			if (sprite.estado == Estado.NaoAtirada) {
				/*
				 * g2d.drawLine(sprite.getX(), (int) sprite.getCentro().getY(),
				 * sprite.getX(), 0); g2d.drawLine(sprite.getX() +
				 * sprite.getRaio() * 2, (int) sprite .getCentro().getY(),
				 * sprite.getX() + sprite.getRaio() 2, 0);
				 */
			}
		}
	}

	// area � um retangulo que representa os limites da bola
	private Rectangle2D area = new Rectangle2D.Float();

	private boolean automatica;
	private Estado estado;
	private Image imagem;
	private Dimension size;
	private TiposBola tipo;
	private final int velocidade = 14;
	// --- Membros de Inst�ncia:
	// vizinhas reune as referencias de todas as instancias que rodeaiam esta
	// instancia
	private List<Bola> vizinhas = new ArrayList<Bola>(6);
	private double x;
	private double y;

	public Bola(int raio, boolean automatica) {
		this.automatica = automatica;
		if (automatica)
			 this.estado = Estado.NoDestino;
		else
			this.estado = Estado.NaoAtirada;
			
		sortearImagem();
		Bola.sprites.add(this);
		instancias++;
	}

	/**
	 * @param bola
	 * @return True se a bola passada colidiu com this
	 */
	/**
	 * @param bola
	 * @return
	 */
	private boolean colidiuCom(Bola bola) {
		boolean retorno = false;
		if (DetectorColisao.colisaoEntre(this, bola, true)) {
			boolean naDiagonal = false;
			Point2D c1 = this.getCentro();
			Point2D c2 = bola.getCentro();
			double tang = Math.abs((c1.getY() - c2.getY())
					/ (c1.getX() - c2.getX()));
			double novoX;
			double novoY;

			// Defini��o do X desta bola:
			if (this.getX() > bola.getX())
			// Esta bola ficar� � direita da outra
			{
				if (tang < 0.8)
					/*
					 * tangente 35� ~= 0.7; Se o centro desta bola vem depois do
					 * fim da outra bola, ela ficar� do lado direito da outra.
					 */
					novoX = bola.getX() + 2 * bola.getRaio();
				else {// Sen�o, ficar� na diagonal direita.
					novoX = bola.getX() + this.getRaio();
					naDiagonal = true;
				}
			} else
			// Esta bola ficar� � esquerda da outra
			{
				if (tang < 0.8)
					/*
					 * tangente 35� ~= 0.7; Se o centro desta bola fica at� o
					 * meio da outra bola, ela ficar� do lado esquerdo da outra.
					 */
					novoX = bola.getX() - 2 * this.getRaio();
				else {
					// Sen�o ela ficar� na diagonal esquerda.
					novoX = bola.getX() - this.getRaio();
					naDiagonal = true;
				}
			}

			// Definicao do Y desta bola:
			if (naDiagonal) {
				// novoY = (int) Math.floor(Math.sqrt(Math.pow(bola.getRaio(),
				// 2)
				// + 2 * bola.getRaio() * this.getRaio()));
				// novoY = (int)Math.floor(this.getRaio()*Math.sqrt(3));
				novoY = DIST_VERTICAL;
				if (this.getY() < bola.getY()) {
					// Se esta bola vem de cima (bola automatica):
					novoY = bola.getY() - novoY;
				} else {
					novoY = novoY + bola.getY();
				}
			} else {
				novoY = bola.getY();
			}

			this.setX(novoX);
			this.setY(novoY);

			retorno = true;
		}
		return retorno;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.balls.sprites.Sprite#colidiuCom(com.balls.sprites.Sprite)
	 */
	public boolean colidiuCom(ISprite outroCorpo) {
		boolean retorno = false;
		if (outroCorpo instanceof Bola) {
			retorno = colidiuCom((Bola) outroCorpo);
		} else if (!this.automatica) {
			retorno = colidiuCom((Parede) outroCorpo);
		}
		return retorno;
	}

	private boolean colidiuCom(Parede parede) {
		boolean retorno = false;
		// Se outroCorpo eh uma parede, verifica a colis�o:
		if (DetectorColisao.colisaoEntre(this, parede, true)) {
			retorno = true;
		}
		return retorno;
	}

	public void criaCachoBolasIguais(List<Bola> cachoADestruir) {
		for (Bola bolaVizinha : this.getVizinhas()) {
			if (!cachoADestruir.contains(bolaVizinha)) {
				if (bolaVizinha.getTipo() == this.getTipo()
						&& bolaVizinha.getY() >= (Jogando.areaDesenho.y - 2 * Bola.RAIO)) {
					cachoADestruir.add(bolaVizinha);
					bolaVizinha.criaCachoBolasIguais(cachoADestruir);
				}
			}
		}
	}

	public boolean criaCachoBolasSoltas(List<Bola> cachoADestruir,
			List<Bola> cachoSoltas) {
		List<Bola> vizinhaComThis = new ArrayList<Bola>(this.getVizinhas());
		vizinhaComThis.add(this);
		for (Bola bolaVizinha : vizinhaComThis) {
			if (!cachoSoltas.contains(bolaVizinha)
					&& !cachoADestruir.contains(bolaVizinha)) {
				if (bolaVizinha.getY() < (Jogando.areaDesenho.y - 2 * Bola.RAIO)) {
					cachoSoltas.clear();
					return false;
				} else {
					cachoSoltas.add(bolaVizinha);
					if (!bolaVizinha.criaCachoBolasSoltas(cachoADestruir,
							cachoSoltas)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void desce() {
		this.y += velocidade;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instancias--;
	}
	
	public boolean isAbaixoDoCanhao(){
		if (this.y + 2*RAIO >= Canhao.getInst().getArea().getY()){
			return true;
		} else {
			return false;
		}
		
	}

	public Rectangle2D getArea() {
		area.setFrame(new Point2D.Double(x, y), size);
		return area.getBounds2D();
	}

	public Point2D getCentro() {
		return new Point2D.Double(this.getX() + this.getRaio(),
				this.getY()	+ this.getRaio());
	}

	public Estado getEstado() {
		return estado;
	}

	public Image getImagem() {
		return imagem;
	}

	public int getRaio() {
		return size.height / 2;
	}

	public Dimension getSize() {
		return size;
	}

	public TiposBola getTipo() {
		return tipo;
	}

	public List<Bola> getVizinhas() {
		return vizinhas;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void limpaReferencias() {
		for (Bola bolaVizinha : vizinhas) {
			bolaVizinha.getVizinhas().remove(this);
		}
		this.vizinhas.clear();
	}

	public void setImagem(Image imagem) {
		this.imagem = imagem;
	}

	/** Altera a Imagem da bola.
	 * @param cor - n�mero da imagem da bola: de 1 � 4.
	 * @param estado - 1: bola no estado normal; 2: bola no estado abatido;. 
	 */
	public void setImagem(int cor, int estado) {
		this.setImagem(ImageLoader.getInst().getImagem("Bola_" + cor + "_" + estado));
		this.size = new Dimension(imagem.getWidth(null), imagem.getHeight(null));
		this.tipo = TiposBola.values()[cor - 1];
	}

	public void setVizinhas(List<Bola> vizinhas) {
		this.vizinhas = vizinhas;
	}

	public void setX(double x) {
		Rectangle area = Jogando.areaDesenho;
		if (x >= area.x - Bola.RAIO && x + Bola.RAIO <= area.x + area.width) {
			this.x = x;
		}
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public void setCentro(int x, int y) {
		this.x = x - Bola.RAIO;
		this.y = y - Bola.RAIO;
	}

	public void sobe() {
		this.y -= velocidade;
	}

	/** Sorteia uma imagem para a bola aleatoriamente.
	 * 
	 */
	public void sortearImagem() {
		Random rand = new Random();
		int num = 1 + rand.nextInt(4);
		this.setImagem(num, 1);
	}
	

	public String toString() {
		double coluna = (this.x - Jogando.areaDesenho.x) / (RAIO * 2)
				+ 1;
		double linha = (this.y - Jogando.areaDesenho.y) / (RAIO * 2) + 1;
		return tipo + " - Col: " + coluna + " - Linha: " + linha
				+ " - No Viz: " + vizinhas.size();
	}

	public void trocaRefsBolaVizinha(Bola bolaVizinha) {
		if (!this.vizinhas.contains(bolaVizinha)) {
			this.vizinhas.add(bolaVizinha);
		}
		if (!bolaVizinha.getVizinhas().contains(this)) {
			bolaVizinha.getVizinhas().add(this);
		}

	}

	/**
	 * "Executa" os movimentos referente as teclas que est�o/foram pressionadas
	 */
	public void update() {
		/*
		 * if (mouseMotionEvent != null) { if (this.x !=
		 * mouseMotionEvent.getX()) this.setX(mouseMotionEvent.getX() -
		 * Bola.RAIO); mouseMotionEvent = null; }
		 * 
		 * if (mouseEvent != null) { if (mouseEvent.getButton() ==
		 * MouseEvent.BUTTON1) this.estado = Estado.Atirada; mouseEvent = null; }
		 */

		if (!tecladoEvents.isEmpty()) {
			if (tecladoEvents.contains(KeyEvent.VK_1)) {
				this.setImagem(1, 1);
			}
			if (tecladoEvents.contains(KeyEvent.VK_2)) {
				this.setImagem(2, 1);
			}
			if (tecladoEvents.contains(KeyEvent.VK_3)) {
				this.setImagem(3, 1);
			}
			if (tecladoEvents.contains(KeyEvent.VK_4)) {
				this.setImagem(4, 1);
			}
		}
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
}
