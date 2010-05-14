package rafael.ballbunch.state;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;

import rafael.ballbunch.common.GfxUtil;
import rafael.ballbunch.core.Balls;
import rafael.ballbunch.input.MouseManager;
import rafael.ballbunch.sprite.Bola;
import rafael.ballbunch.sprite.Canhao;
import rafael.ballbunch.sprite.DetectorColisao;
import rafael.ballbunch.sprite.Parede;


/**
 * Esta classe representa o estado Jogando.
 * 
 * @author Rafael Sales
 */
public class Jogando implements IEstado, ActionListener {

	private class Fases {

		private class Fase {
			// Numero de bolas para ir pro pr�ximo level:
			private final int numMaxBolas;
			// Velocidade usada para calcular o downRate desse level:
			private final int velocidade;

			public Fase(int numMaxBolas, int velocidade) {
				this.numMaxBolas = numMaxBolas;
				this.velocidade = velocidade;
			}
		}

		private Fase[] fases = new Fase[4];
		private int faseAtual;
		private int bolasNaTela;

		public Fases() {
			fases[0] = new Fase(COLUNAS_BOLAS * 20, 6);
			fases[1] = new Fase(COLUNAS_BOLAS * 40, 8);
			fases[2] = new Fase(COLUNAS_BOLAS * 80, 9);
			fases[3] = new Fase(COLUNAS_BOLAS * 160, 10);
			this.setFaseAtual(0);
			for (int i = 0; i < fases.length; i++) {
				System.out.printf(
						"Fase %d - Maximo de Bolas: %d; Velocidade: %d\n",
						i + 1, fases[i].numMaxBolas, fases[i].velocidade);
			}
		}

		private synchronized void loop() {
			// this.bolasNaTela = Bola.getSprites().size();
			if (pontuacao.bolasDestruidas >= fases[faseAtual].numMaxBolas) {
				if (faseAtual + 1 < fases.length) { // Verifica se h� outra fase
					this.setFaseAtual(faseAtual++);
					System.err.println("Nova Fase: " + faseAtual);
				}
			}
		}

		private void setFaseAtual(int faseAtual) {
			this.faseAtual = faseAtual;
			setDownRate(this.fases[faseAtual].velocidade);
		}
	}

	private class Logica {

		private Bola bolaAtual = null;
		private List<Bola> cachoADestruir;
		private boolean linhaMaior = true;

		private synchronized void desceBolasADestuir() {
			Iterator<Bola> iteratorCacho = this.cachoADestruir.iterator();
			Bola bola;
			while (iteratorCacho.hasNext()) {
				bola = iteratorCacho.next();
				if (bola.isAbaixoDoCanhao()) {
					Bola.getSprites().remove(bola);
					iteratorCacho.remove();
				} else {
					bola.setY(bola.getY() + 10);
				}
			}
		}

		private synchronized void geraBolasAutomaticas() {
			List<Bola> listBolasAutomaticas = new ArrayList<Bola>(
					COLUNAS_BOLAS + 1);
			Bola bolaNova;
			boolean linhaMaior = this.isLinhaMaior();
			int x, y, colunas = COLUNAS_BOLAS;

			if (linhaMaior)
				colunas = colunas + 1;

			for (int j = 0; j < colunas; j++) {
				bolaNova = new Bola(Bola.RAIO, true);
				x = areaDesenho.x + j * Bola.RAIO * 2;
				y = (areaDesenho.y - 2 * Bola.RAIO) - Bola.DIST_VERTICAL;
				if (linhaMaior)
					// A posicao das bolas fica 1xBola.RAIO a menos
					x = x - Bola.RAIO;
				bolaNova.setX(x);
				bolaNova.setY(y);
				listBolasAutomaticas.add(bolaNova);
			}
			for (Bola bola : listBolasAutomaticas) {
				this.testaColisoes(bola);
			}
			listBolasAutomaticas.clear();
			listBolasAutomaticas = null;
			bolaNova = null;
		}

		private synchronized void geraBolasIniciais() {
			List<Bola> listBolasIniciais = new ArrayList<Bola>(
					(COLUNAS_BOLAS + 1) * LINHAS_BOLAS_INICIAIS);
			Bola bolaNova;
			boolean linhaMaior;
			int x, y, colunas;
			for (int i = LINHAS_BOLAS_INICIAIS - 1; i >= 0; i--) {
				colunas = COLUNAS_BOLAS;
				linhaMaior = this.isLinhaMaior();
				if (linhaMaior)
					colunas = COLUNAS_BOLAS + 1;

				for (int j = 0; j < colunas; j++) {
					bolaNova = new Bola(Bola.RAIO, true);
					x = areaDesenho.x + j * Bola.RAIO * 2;
					y = (areaDesenho.y - 2 * Bola.RAIO) + i
							* Bola.DIST_VERTICAL;
					if (linhaMaior)
						// A posicao das bolas fica 1xBola.RAIO a menos
						x = x - Bola.RAIO;

					bolaNova.setX(x);
					bolaNova.setY(y);
					listBolasIniciais.add(bolaNova);
				}
			}
			for (Bola bola : listBolasIniciais) {
				this.testaColisoes(bola);
			}
			listBolasIniciais.clear();
			listBolasIniciais = null;
			bolaNova = null;
		}

		private synchronized void incrementaBolas() {
			boolean maisBolas = true;
			for (Bola bola : Bola.getSprites()) {
				if (bola != this.bolaAtual)
					bola.setY(bola.getY() + 1);
				if (bola.getY() < areaDesenho.y - 2 * Bola.RAIO) {
					maisBolas = false;
				}
			}
			if (maisBolas) {
				geraBolasAutomaticas();
			}
		}

		private boolean isLinhaMaior() {
			if (this.linhaMaior) {
				this.linhaMaior = false;
				return true;
			} else {
				this.linhaMaior = true;
				return false;
			}
		}

		private synchronized void loop() {
			/*
			 * Se houverem bolas a serem destuidas que ainda n�o desapareceram,
			 * desce elas at� que saiam da tela.
			 */
			if (this.cachoADestruir != null && !this.cachoADestruir.isEmpty()) {
				desceBolasADestuir();
				return;
			}
			/*
			 * Se a bola anterior chegou ao seu destino ou Nenhuma bola foi
			 * criada, outra Bola criada:
			 */
			if (bolaAtual == null
					|| bolaAtual.getEstado() == Bola.Estado.NoDestino) {
				bolaAtual = new Bola(Bola.RAIO, false);
				Canhao.getInst().setBola(bolaAtual);
			}

			// Se a bola ainda n�o foi atirada e ainda estiver com jogador
			if (bolaAtual.getEstado() == Bola.Estado.NaoAtirada) {
				bolaAtual.update();
				Canhao.getInst().update();
			}

			// Se a bola foi atirada e ainda est� subindo
			if (bolaAtual.getEstado() == Bola.Estado.Atirada) {
				Canhao.getInst().moveBola();
				this.testaColisoes(bolaAtual);
			}

			/*
			 * Se a bola chegou no destino E se ela n�o est� acima do topo da
			 * tela. A bola pode chegar a ficar acima do topo da tela se n�o
			 * houverem bolas l�.
			 */
			if (bolaAtual.getEstado() == Bola.Estado.NoDestino
					&& bolaAtual.getY() >= areaDesenho.y) {
				this.removeBolas();
			}
		}

		private synchronized void removeBolas() {
			/*
			 * Verifica as colisoes entre as bolas. Se houver um cacho de bolas
			 * com pelo menos duas da mesma cor, destr�i:
			 */
			cachoADestruir = new ArrayList<Bola>();
			bolaAtual.criaCachoBolasIguais(cachoADestruir);

			// Verifica se existem pelo menos 3 bolas iguais.
			// Se nao houver, nao remove bolas.
			if (cachoADestruir.size() < 3) {
				this.cachoADestruir.clear();
				return;
			}

			// Exclui a referencia de 'bola' de suas vizinhas
			for (Bola bola : Bola.getSprites()) {
				bola.getVizinhas().removeAll(cachoADestruir);
			}

			List<Bola> cachoSoltas = new ArrayList<Bola>();
			for (Bola bola : new ArrayList<Bola>(cachoADestruir)) {
				for (Bola bolaViz : bola.getVizinhas()) {
					bolaViz.criaCachoBolasSoltas(cachoADestruir, cachoSoltas);
					cachoADestruir.addAll(cachoSoltas);
					cachoSoltas.clear();
				}
			}

			// Altera a imagem das bolas:
			for (Bola bola : cachoADestruir) {
				bola.setImagem(1 + bola.getTipo().ordinal(), 2);
				bola.getVizinhas().clear();
			}
			// Faz a limpeza de referencias:
			for (Bola bola : Bola.getSprites())
				bola.getVizinhas().removeAll(cachoADestruir);

			cachoSoltas.clear();
			cachoSoltas = null;
			bolaAtual = null;

			// Incrementa os Scores:
			pontuacao.incBolasDestruidas(this.cachoADestruir.size());
		}

		private synchronized void testaColisoes(Bola bola) {
			boolean colisao = false;
			// Verifica colisao da bolaAtual com as outras bolas:
			for (Bola outraBola : Bola.getSprites()) {
				if (bola != outraBola && bola.colidiuCom(outraBola)) {
					bola.trocaRefsBolaVizinha(outraBola);
					colisao = true;
					break;
				}
			}
			if (colisao) {
				// Faz colisao com as bolas vizinhas...
				for (Bola outraBola : Bola.getSprites()) {
					if (bola != outraBola
							&& !bola.getVizinhas().contains(outraBola)
							&& DetectorColisao.colisaoEntre(bola, outraBola,
									false)) {
						bola.trocaRefsBolaVizinha(outraBola);
					}
				}
			}
			if (colisao)
				bola.setEstado(Bola.Estado.NoDestino);
		}
	}

	private class Pontuacao {
		// ---Dados
		private final int SCORE_POR_BOLA = 10;
		private int bolasDestruidas = 0;
		private int score = 0;
		// ---Dados

		// ---Graficos
		private final Rectangle areaDesenho = new Rectangle();
		private Rectangle rectTexto = new Rectangle();
		Font fonte;
		FontMetrics fMet;
		private String txtNumBolas = "Bolas destru�das";
		private String txtScore = "Pontuacao";

		// ---Graficos

		public Pontuacao() {
			// Calcula a area onde ficam os scores:
			this.setAreaDesenho();

			fonte = new Font(Font.SANS_SERIF, Font.BOLD, 24);
		}

		/**
		 * Incrementa o n�mero de bolas destruidas. <br>
		 * Chamado por removeBolas a partir uma inst�ncia de Logica.
		 * 
		 * @param quantidadeBolas -
		 *            Numero de bolas destru�das.
		 */
		private void incBolasDestruidas(int quantidadeBolas) {
			this.bolasDestruidas += quantidadeBolas;
			this.incScore(quantidadeBolas);
		}

		/**
		 * Incrementa o Score. <br>
		 * 
		 * @param quantidadeBolas -
		 *            Numero de bolas destru�das.
		 */
		private void incScore(int quantidadeBolas) {
			int potencia = (fases.faseAtual + 1)
					* Math.round((quantidadeBolas * 1f) / 3);
			this.score += SCORE_POR_BOLA * quantidadeBolas * potencia;
			System.out.println("Score: " + this.score + " - Bolas Destruidas: "
					+ this.bolasDestruidas);
		}

		public void render(Graphics2D g) {
			//Margem para o topo da tela:
			Dimension size = new Dimension();
			Point p = new Point();
			int proximoY = 20;
			g.setColor(new Color(80, 80, 110));
			g.fill(areaDesenho);

			// Pega uma instancia de FontMetrics para medir o tamanho dos textos:
			fMet = g.getFontMetrics(fonte);
			g.setFont(fonte);
			int tamLinha = fMet.getAscent();
			//int disLinha = 2 * fMet.getDescent();

			// ---Score
			proximoY += fMet.getHeight();
			// Dados:
			size.setSize(10 * fMet.charWidth('0'), fMet.getHeight());
			p.setLocation(GfxUtil.centralizar(
					this.areaDesenho.getSize(),
					this.rectTexto.getSize()));
			
			this.rectTexto.setSize(size);
			this.rectTexto.setLocation(p);
			this.rectTexto.y = proximoY;
			
			g.setColor(new Color(20, 20, 20));
			g.fillRoundRect(rectTexto.x, rectTexto.y, rectTexto.width, rectTexto.height, 20, 20);
			
			//Texto:
			/*g.setColor(new Color(250,250,250));
			g.drawString(this.txtNumBolas,
					rectTexto.y - tamLinha,
					rectTexto.x);*/
			// ---Score
			
			// ---Numero de bolas destruidas:
			// Dados:
			proximoY += fMet.getHeight();
			proximoY += fMet.getHeight();
			
			this.rectTexto.setSize(10 * fMet.charWidth('0'), fMet.getHeight());
			this.rectTexto.setLocation(GfxUtil.centralizar(
							this.areaDesenho.getSize(),
							this.rectTexto.getSize()));
			this.rectTexto.y = proximoY;
			
			g.setColor(new Color(20,20,20));
			g.fillRoundRect(rectTexto.x, rectTexto.y, rectTexto.width, rectTexto.height, 20, 20);
			
			//Texto:
			/*proximoY += fMet.getHeight();
			g.setColor(new Color(250,250,250));
			g.drawString(this.txtNumBolas,
					proximoY - tamLinha,
					rectTexto.x);
			*/
			// ---Numero de bolas destruidas:
		}

		/**
		 * Define a �rea de desenho dos scores:
		 */
		private void setAreaDesenho() {
			this.areaDesenho.width = Jogando.areaDesenho.x;
			this.areaDesenho.height = Jogando.areaDesenho.height;
			for (Parede parede : Parede.getSprites()) {
				if (parede.getPosicao() == Parede.Posicao.Esquerda) {
					this.areaDesenho.width -= parede.getSize().width;
					/**
					 * Aumenta a largura para que n�o haja espa�amento entre a
					 * parede e o fundo da area de scores:
					 */
					this.areaDesenho.width += 5;
				}
			}
		}

	}

	public static final Rectangle areaDesenho = new Rectangle();
	private final int LINHAS_BOLAS_INICIAIS = 4;
	public final int COLUNAS_BOLAS = 8;
	private Jogando.Logica logica = this.new Logica();
	private Jogando.Fases fases = this.new Fases();
	private final Jogando.Pontuacao pontuacao;

	private Timer timer;

	private int velocidadeNormal = 6;

	private int downRate;

	public Jogando() {
		// Configura sprites iniciais... paredes, bolas iniciais...
		Parede.geraParedes();
		// Calcula a area onde fica as bolas:
		this.setAreaDesenho();

		this.pontuacao = this.new Pontuacao();

		// Gera bolas iniciais:
		logica.geraBolasIniciais();

		// Adiciona o canhao ao gerenciador do mouse:
		MouseManager.getInst().addCorpoControlado(Canhao.getInst());

		// Inicia o timer para incremento de bolas:
		this.setDownRate(this.velocidadeNormal);
		this.timer = new Timer(this.downRate, this);
		this.timer.start();
	}

	public synchronized void actionPerformed(ActionEvent e) {
		logica.incrementaBolas();
	}

	public void finaliza() {
		logica.bolaAtual = null;
		MouseManager.getInst().removeCorpoControlado(Canhao.getInst());
		this.timer.stop();
		this.timer = null;
		for (Bola bola : Bola.getSprites()) {
			bola.getVizinhas().clear();
			bola.setVizinhas(null);
			bola.setImagem(null);
		}
		Bola.getSprites().clear();
		for (Parede parede : Parede.getSprites()) {
			parede.setImagem((Image) null);
		}
		Parede.getSprites().clear();
	}

	public synchronized void loop() {
		logica.loop();
		fases.loop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.balls.states.IEstado#render(java.awt.Graphics2D)
	 */
	public synchronized void render(Graphics2D g2d) {
		g2d.setBackground(new Color(180, 190, 210));
		g2d.setColor(Color.red);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(new Font("Garamond", Font.BOLD, 14));

		this.pontuacao.render(g2d);
		Canhao.renderAll(g2d);
		Bola.renderAll(g2d);
		Parede.renderAll(g2d);
	}

	private void setAreaDesenho() {
		/*
		 * Define a largura de acordo com o numero de colunas de bolas
		 * pr�-definido
		 */
		double novaLargura = COLUNAS_BOLAS * 2 * Bola.RAIO;
		double novaAltura = Balls.AREA.getHeight();
		double novoX = Balls.AREA.getWidth() - novaLargura;
		double novoY = 0;
		for (Parede parede : Parede.getSprites()) {
			if (parede.getPosicao() == Parede.Posicao.Direita) {
				novoX -= parede.getSize().getWidth();
			} else if (parede.getPosicao() == Parede.Posicao.Topo) {
				novaAltura -= parede.getSize().getHeight();
				novoY = parede.getSize().getHeight();
			}
		}
		areaDesenho.setLocation((int) novoX, (int) novoY);
		areaDesenho.setSize((int) novaLargura, (int) novaAltura);
		Parede.posicionaParedes();
	}

	private void setDownRate(int velocidade) {
		this.downRate = (int) Math.round(1000d / velocidade);
	}
}
