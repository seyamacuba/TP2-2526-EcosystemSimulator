package simulator.view;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

  int rWidth;
  int rHeight;

  State currentState; // Mostramos sólo animales con este estado. Los posibles valores de currState
  // son null, y los valores de Animal.State.values(). Si es null mostramos tot.

  Map<String, SpeciesInfo> kindsInfo = new HashMap<>();// Un mapa para la información sobre las especies.

  private int width;// Anchura/altura de la simulación -- se supone que siempre van a ser iguales al tamaño del componente
  private int height; // Número de filas/columnas de la simulación (regiones)

  private int rows;
  private int cols;

  volatile private Collection<AnimalInfo> objs;// En estos atributos guardamos la lista de animales y el tiempo que hemos

  volatile private Double time;// recibido la última vez para dibujarlos.

  private final Font textFont = new Font("Arial", Font.BOLD, 12);

  private boolean showHelp;// Indica si mostramos el texto la ayuda o no.

  public MapViewer() {
    initGUI();
  }

  private void initGUI() {

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
          case 'h':
            showHelp = !showHelp;
            repaint();
            break;
          case 's':
            // Cambio circular: null → states[0] → states[1] → ... → null
            State[] states = State.values();
            if (currentState == null) {
              currentState = states[0];
            } else {
              int idx = currentState.ordinal();
              currentState = (idx + 1 < states.length) ? states[idx + 1] : null;
            }
            //      Change currState to the next option (in a circular way). After null
            //      comes the first element of Animal.State.values(), and after the last of
            //      these values comes null.
            //
            repaint();
          default:
        }
      }

    });

    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) { // Esto es necesario para capturar las teclas cuando el ratón está sobre este componente.
        requestFocus();
      }
    });

    // Por defecto mostramos todos los animales.
    currentState = null;

    // Por defecto mostramos el texto de ayuda.
    showHelp = true;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D gr = (Graphics2D) g;
    gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g.setFont(textFont);// Cambiar el font para dibujar texto.
    gr.setBackground(Color.WHITE);
    gr.clearRect(0, 0, width, height);


    if (objs != null) // Dibujar los animales, el tiempo, información sobre las especies, etc.
      drawObjects(gr, objs, time);

    // Texto de ayuda
    if (showHelp) {
      g.setColor(Color.RED);
      g.drawString("h: toggle help", 5, 15);
      g.drawString("s: show animals of a specific state", 5, 30);
    }
    //      Show a 'help' text if showHelp is true. The text should be the following
    //      in two separated lines:
    //
    // h: toggle help
    // s: show animals of a specific state

  }

  private boolean visible(AnimalInfo a) {
    //      return true of the animal is visible, i.e., currState is null or its
    //      state is equal to currState.
    //
    return currentState == null || a.getState() == currentState;
  }

  private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

    // Grid de regiones
    g.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i <= cols; i++) g.drawLine(i * rWidth, 0, i * rWidth, height);
    for (int i = 0; i <= rows; i++) g.drawLine(0, i * rHeight, width, i * rHeight);

    for (AnimalInfo a : animals) {  // Dibujar los animales.

      if (!visible(a))// Si no es visible saltamos la iteración.
        continue;

      SpeciesInfo speciesInfo = kindsInfo.get(a.getGeneticCode());// La información sobre la especie de 'a'.

      // Si no existe la especie, la añadimos
      if (speciesInfo == null) {
        speciesInfo = new SpeciesInfo(ViewUtils.getColor(a.getGeneticCode()));
        kindsInfo.put(a.getGeneticCode(), speciesInfo);
      }

      // Incrementar contador
      speciesInfo.count++;

      // Dibujar animal — tamaño relativo a la edad
      int size = (int) (a.getAge() / 2 + 2);
      int x = (int) a.getPosition().getX() - size / 2;
      int y = (int) a.getPosition().getY() - size / 2;
      g.setColor(speciesInfo.color);
      g.fillOval(x, y, size, size);
    }

    // Etiqueta del estado visible
    if (currentState != null) {
      g.setColor(Color.RED);
      drawStringWithRect(g, 5, height - 35, "State: " + currentState.toString());
    }

    // Etiqueta del tiempo
    g.setColor(Color.BLACK);
    drawStringWithRect(g, 5, height - 15, String.format("%.3f", time));

    // Información de especies y reset del contador
    int yOffset = 50;
    for (Entry<String, SpeciesInfo> e : kindsInfo.entrySet()) {
      g.setColor(e.getValue().color);
      drawStringWithRect(g, 5, yOffset, e.getKey() + ": " + e.getValue().count);
      yOffset += 20;
      e.getValue().count = 0; // reset para el siguiente repaint
    }
  }

  // Un méto do que dibujar un texto con un rectángulo.
  void drawStringWithRect(Graphics2D g, int x, int y, String s) {
    Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
    g.drawString(s, x, y);
    g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
  }

  @Override
  public void update(List<AnimalInfo> objs, Double time) {
    //      Store objs and time in the corresponding fields, and call repaint() to
    //      redraw the component.
    SwingUtilities.invokeLater(() -> {
      this.objs = objs;
      this.time = time;
      repaint();
    });
  }

  @Override
  public void reset(double time, MapInfo map, List<AnimalInfo> animals) { //aqui no invokelater que se buguea el mapa
      this.width = map.getWidth();
      this.height = map.getHeight();
      this.cols = map.getCols();
      this.rows = map.getRows();
      this.rWidth = width / cols;
      this.rHeight = height / rows;

      // Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
      // porque en MapWindow llamamos a pack() después de llamar a reset.
      setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));

      // Dibuja el estado.
      update(animals, time);
  }

  // Una clase auxiliar para almacenar información sobre una especie.
  private static class SpeciesInfo {
    private Integer count;
    private final Color color;

    SpeciesInfo(Color color) {
      count = 0;
      this.color = color;
    }
  }

}
