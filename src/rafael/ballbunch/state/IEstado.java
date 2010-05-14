package rafael.ballbunch.state;

import java.awt.Graphics2D;

/**
 * @author Rafael Sales
 */
public interface IEstado {
	public static IEstado proxEstado = null;		
	public abstract void loop();
	public abstract void render(Graphics2D g2d);
	public abstract void finaliza();
}
