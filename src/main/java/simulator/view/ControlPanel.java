package simulator.view;

import simulator.control.Controller;

import javax.swing.*;
import java.awt.*;

class ControlPanel extends JPanel {

  private Controller ctrl;
  private ChangeRegionsDialog changeRegionsDialog;

  private JToolBar toolBar;
  private JFileChooser fc;
  private boolean stopped = true; // utilizado en los botones de run/stop
  private JButton quitButton;

  ControlPanel(Controller ctrl) {
    this.ctrl = ctrl;
    initGUI();
  }

  private void initGUI() { //el profe no sabe, toolabar huh
    setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    add(toolBar, BorderLayout.PAGE_START);

    // TODO crear los diferentes botones/atributos y añadirlos a la toolBar.
    //      Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
    //      this.toolaBar.addSeparator() para añadir la línea de separación vertical
    //      entre las componentes que lo necesiten.

    // Quit Button
    this.toolBar.add(Box.createGlue()); // this aligns the button to the right
    this.toolBar.addSeparator();
    this.quitButton = new JButton();
    this.quitButton.setToolTipText("Quit");
    // TODO cargar la imagen como un recurso usando el ClassLoader y NO usando una ruta absoluta o relativa
    this.quitButton.setIcon(new ImageIcon("..."));
    this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
    this.toolBar.add(quitButton);

    // TODO Inicializar this.fc con una instancia de JFileChooser. Para que siempre
    // abre en la carpeta de ejemplos puedes usar:
    //
    //   this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

    // TODO Inicializar this.changeRegionsDialog con instancias del diálogo de cambio
    // de regiones

  }
}
