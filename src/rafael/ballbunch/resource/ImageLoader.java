package rafael.ballbunch.resource;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * @author Rafael Sales
 */
public class ImageLoader {
	
	private Map<String, ImageIcon> lista = new HashMap<String, ImageIcon>();
	private final String path = "imagens/";
	private final String ext = ".png";
	private static ImageLoader instancia;
	
	public ImageLoader(String resourceFile) {
		instancia = this;

		for(int i = 1; i <= 4; i++) {
			String nome;
			nome = "Bola_" + i + "_1";
			//Bola no estado normal:
			lista.put(nome, new ImageIcon(path + nome + ext));
			nome = "Bola_" + i + "_2";
			//Bola no estado abatido:
			lista.put(nome, new ImageIcon(path + nome +  ext));
			nome = null;
		}
		
		lista.put("Parede_1", new ImageIcon(path + "parede_1" + ext));
		lista.put("Parede_2", new ImageIcon(path + "parede_2" + ext));
	}
	
	public Image getImagem(String nomeImagem) {
		try {
			if (!lista.containsKey(nomeImagem))
				throw new Exception("Imagem \"" + nomeImagem + "\" nao encontrada.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista.get(nomeImagem).getImage();
	}
	
	public static ImageLoader getInst(){
		if (instancia != null)
			return instancia;
		else 
			throw new NullPointerException(instancia.getClass().getName() + " n�o instanciado.");
	}
}
