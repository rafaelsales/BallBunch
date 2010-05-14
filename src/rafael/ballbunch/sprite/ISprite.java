package rafael.ballbunch.sprite;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

/**
 * @author Rafael Sales
 * Interface que deve ser implementada pelos sprites.
 */
public interface ISprite {
	
	public Dimension getSize();

	public Image getImagem();

	public void setImagem(Image imagem);
	
	/**
	 * @return Retangulo que representa o limite do sprite
	 */
	public Rectangle2D getArea();
	
	/**
	 * @param outroCorpo
	 * @return true se o Sprite outroCorpo colidiu com o Sprite this
	 */
	public boolean colidiuCom(ISprite outroCorpo);

}
