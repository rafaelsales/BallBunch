package rafael.ballbunch.common;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

public final class GfxUtil {
	
	private GfxUtil(){}
	
	public static Point centralizar(Dimension dimBase, Dimension dimObjeto) {
		return GfxUtil.centralizar(dimBase, new Point(0,0), dimObjeto);
	}
	
	public static Point centralizar(Dimension dimBase,
			Point pBase, Dimension dimObjeto) {
		/* pontoObjeto guarda o ponto em que deve ficar o objeto
		 * para que ele fique centralizado em relacao à base;
		 */
		Point pontoObjeto = new Point();

		pontoObjeto.x = pBase.x + dimBase.width/2 - dimObjeto.width/2;
		pontoObjeto.y = pBase.y + dimBase.height/2 - dimObjeto.height/2;
		
		//Zera o x e/ou y se forem negativo(s).
		if (pontoObjeto.x <= 0)
			pontoObjeto.x = 0;
		if (pontoObjeto.y <= 0)
			pontoObjeto.y = 0;
		
		return pontoObjeto;
	}

	public static Font criaFonte(boolean trueType, String caminho){
		Font fonte = null;
		try {
			File fileFont = new File(caminho);
			int type;
			if (trueType)
				type = Font.TRUETYPE_FONT;
			else
				type = Font.TYPE1_FONT;
			
			fonte = Font.createFont(type, fileFont);
			
		} catch (FontFormatException e) {
			//e.printStackTrace();	
		} catch (IOException e) {
			//e.printStackTrace();
		}
		if (fonte == null)
			fonte = new Font(Font.SERIF, Font.PLAIN, 10);
		return fonte;
	}
}

