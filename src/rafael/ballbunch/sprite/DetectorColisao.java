package rafael.ballbunch.sprite;

import java.awt.geom.Point2D;

/**
 * @author Rafael Sales
 */
public class DetectorColisao {
	
	public static boolean colisaoEntre(Bola bola, Parede parede, boolean comPrecisao) {
		boolean retorno;
		if (comPrecisao) {
			retorno = bola.getArea().intersects(parede.getArea());
		} else {
			int limiteParede = parede.getSize().height;
			retorno = (bola.getY() <= limiteParede);
		}
		bola = null;
		parede = null;
		return retorno;
	}
	
	public static boolean colisaoEntre(Bola bola1, Bola bola2, boolean comPrecisao){
		//Pega o ponto do centro das bolas
		Point2D c1 = bola1.getCentro();
		Point2D c2 = bola2.getCentro();
		int raio1 = bola1.getRaio() + 1;
		int raio2 = bola2.getRaio() + 1;
		bola1 = null;
		bola2 = null;
		if (!comPrecisao) {
			raio1 += 2;
			raio2 += 1;
		}
		
		//Calcula a distancia entre os centros
		int distancia = (int)Math.round(c1.distance(c2));

		if (distancia <= (raio1 + raio2)) {
			return true;
		}
		return false;
	}

}
