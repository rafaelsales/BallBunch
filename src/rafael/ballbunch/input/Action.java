package rafael.ballbunch.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Sales
 */
public abstract class Action {
	//protected boolean movendo;
	protected long ultimoMovimentoMillis = 0;
	protected List<Integer> tecladoEvents = new ArrayList<Integer>();
	protected MouseEvent mouseEvent = null;
	protected MouseEvent mouseMotionEvent = null;

	protected void mouseMovido(MouseEvent e) {
		mouseMotionEvent = e;
	}
	
	protected void mouseClicado(MouseEvent e) {
		mouseEvent = e;		
	}
	
	protected void teclaPressionada(KeyEvent e) {
		if (!tecladoEvents.contains((Integer)e.getKeyCode()))
			tecladoEvents.add(e.getKeyCode());
	}

	protected void teclaSolta(KeyEvent e) {
		tecladoEvents.remove((Integer)e.getKeyCode());
	}
	
	public int tempoUltimoMovimento() {
		return (int)((System.nanoTime() - ultimoMovimentoMillis) / 1000000L);
	}
	
	public abstract void update();
}
