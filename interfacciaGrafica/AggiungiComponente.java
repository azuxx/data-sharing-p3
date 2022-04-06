package interfacciaGrafica;

import java.awt.Component;
import java.awt.Container;

/*contiene un metodo statico che aggiunge un oggetto grafico generico (component) in un contenitore generico (container)
 nella posizione (x,y) del container e avente dimensione width x height*/
class AggiungiComponente{
	public static void aggiungi(Container container, Component c,int x, int y, int width, int height) {
		c.setBounds(x,y,width,height);
		container.add(c);	
	}
}