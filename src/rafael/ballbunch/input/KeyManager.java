package rafael.ballbunch.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Sales
 */
public class KeyManager implements KeyListener {

	private static KeyManager instancia;

	public static KeyManager getInst() {
		if (instancia == null)
			instancia = new KeyManager();
		return instancia;
	}

	private List<Action> listCorpos = new ArrayList<Action>();

	public void addCorpo(Action corpo) {
		listCorpos.add(corpo);
	}

	public void keyPressed(KeyEvent e) {
		if (!listCorpos.isEmpty()) {
			for (Action corpo : listCorpos) {
				corpo.teclaPressionada(e);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (!listCorpos.isEmpty()) {
			for (Action corpo : listCorpos) {
				corpo.teclaSolta(e);
			}
		}
	}
	
	public void keyTyped(KeyEvent arg0) {}

	public void removeCorpo(Action corpo) {
		listCorpos.remove(corpo);
	}

}