package rafael.ballbunch.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Sales
 */
public class MouseManager implements MouseListener, MouseMotionListener {

	private static MouseManager instancia;

	public static MouseManager getInst() {
		if (instancia == null)
			instancia = new MouseManager();
		return instancia;
	}

	private List<Action> listCorpos = new ArrayList<Action>();

	public void addCorpoControlado(Action corpo) {
		listCorpos.add(corpo);
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseDragged(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		if (!listCorpos.isEmpty()) {
			for (Action corpo : listCorpos) {
				corpo.mouseMovido(e);
			}
		}
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {
		if (!listCorpos.isEmpty()) {
			for (Action corpo : listCorpos) {
				corpo.mouseClicado(e);
			}
		}
	}

	public void removeCorpoControlado(Action corpo) {
		listCorpos.remove(corpo);
	}

}