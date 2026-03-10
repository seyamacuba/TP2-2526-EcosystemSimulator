package simulator.view;

import simulator.control.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class ControlPanel extends JPanel {

  private Controller ctrl;
  private ChangeRegionsDialog changeRegionsDialog;

  private JToolBar toolBar;
  private JFileChooser fc;
  private boolean stopped = true; // utilizado en los botones de run/stop

  private JButton quitButton;
  private JButton openButton;
  private JButton mapButton;
  private JButton regionsButton;
  private JButton runButton;
  private JButton stopButton;
  private JSpinner stepsSpinner;
  private JTextField deltaTimeField;

  ControlPanel(Controller ctrl) {
    this.ctrl = ctrl;
    initGUI();
  }

  private void initGUI() { //el profe no sabe, toolabar huh
    setLayout(new BorderLayout());
    this.toolBar = new JToolBar();
    add(this.toolBar, BorderLayout.PAGE_START);

    // TODO crear los diferentes botones/atributos y añadirlos a la toolBar.
    //      Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
    //      this.toolaBar.addSeparator() para añadir la línea de separación vertical
    //      entre las componentes que lo necesiten.

    //INICIALIZACIONES:

    this.fc = new JFileChooser();
    this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

    this.changeRegionsDialog = new ChangeRegionsDialog(this.ctrl); //Falta implementar.

    this.openButton = new JButton();
    this.openButton.setToolTipText("Load an input file");
    this.openButton.setIcon(new ImageIcon(getClass().getResource("enunciados/practica2/open.png")));
    this.openButton.addActionListener(e-> loadFile());
    this.toolBar.add(this.openButton);

    this.toolBar.addSeparator();

    this.mapButton = new JButton();
    this.mapButton.setToolTipText("Open Map Viewer");
    this.mapButton.setIcon(new ImageIcon(getClass().getResource("enunciados/practica2/MapWindow.png")));
    this.mapButton.addActionListener(e-> new MapWindow(this.ctrl));
    this.toolBar.add(this.mapButton);

    this.regionsButton = new JButton();
    this.regionsButton.setToolTipText("Change Regions");
    this.regionsButton.setIcon(new ImageIcon(getClass().getResource("enunciados/practica2/regions.png")));
    this.regionsButton.addActionListener(e->this.changeRegionsDialog.open(ViewUtils.getWindow(this))); //Terminar, falta change regions para q funsione open
    this.toolBar.add(this.regionsButton);

    this.toolBar.addSeparator();

    this.runButton = new JButton();
    this.runButton.setToolTipText("Run Simulation");
    this.runButton.setIcon(new ImageIcon(getClass().getResource("enunciados/practica2/run.png")));
    this.runButton.addActionListener(e->startSimulation());
    this.toolBar.add(this.runButton);

    this.toolBar.addSeparator();

    this.stopButton = new JButton();
    this.stopButton.setToolTipText("Stop Simulation");
    this.stopButton.setIcon(new ImageIcon(getClass().getResource("enunciados/practica2/stop.png")));
    this.stopButton.addActionListener(e -> this.stopped = true);
    this.toolBar.add(this.stopButton);

    this.toolBar.add(new JLabel(" Steps: "));
    this.stepsSpinner = new JSpinner(new SpinnerNumberModel(10000, 1, 100000, 100));
    this.stepsSpinner.setMaximumSize(new Dimension(80, 40));
    this.toolBar.add(this.stepsSpinner);

    this.toolBar.add(new JLabel(" Delta-Time: "));
    this.deltaTimeField = new JTextField("2500.0");
    this.deltaTimeField.setMaximumSize(new Dimension(80, 40));
    this.toolBar.add(this.deltaTimeField);

    // Quit Button
    this.toolBar.add(Box.createGlue()); // this aligns the button to the right
    this.toolBar.addSeparator();
    this.quitButton = new JButton();
    this.quitButton.setToolTipText("Quit");
    // TODO cargar la imagen como un recurso usando el ClassLoader y NO usando una ruta absoluta o relativa
    this.quitButton.setIcon(new ImageIcon(getClass().getResource("enunciados/practica2/exit.png")));
    this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
    this.toolBar.add(quitButton);
  }

  private void loadFile(){
    int returnVal = this.fc.showOpenDialog(ViewUtils.getWindow(this));//devuelve un código numérico que devuelve la ventana para decirte que boton se ha pulsado.
    if(returnVal == JFileChooser.APPROVE_OPTION){
      File file = this.fc.getSelectedFile();

      try{
        java.io.InputStream is = new java.io.FileInputStream(file);
        org.json.JSONObject jsonInput = new org.json.JSONObject(new org.json.JSONTokener(is));

        int cols = jsonInput.getInt("cols");
        int rows = jsonInput.getInt("rows");
        int width = jsonInput.getInt("width");
        int height = jsonInput.getInt("height");

        this.ctrl.reset(cols,rows,width,height);
        this.ctrl.loadData(jsonInput);

      }catch(Exception e){
        ViewUtils.showErrorMsg("Error loading file" + e.getMessage());
      }
    }
  }

  // Método auxiliar para no repetir código al activar/desactivar botones
  private void setButtonsEnabled(boolean enabled) {
    this.openButton.setEnabled(enabled);
    this.mapButton.setEnabled(enabled);
    this.regionsButton.setEnabled(enabled);
    this.runButton.setEnabled(enabled);
    this.quitButton.setEnabled(enabled);

    this.stopButton.setEnabled(true);
  }

  private void startSimulation(){
    this.stopped = false;
    setButtonsEnabled(false); //Deshabilita todos los botones al empezar.

    try{
      double dt = Double.parseDouble(this.deltaTimeField.getText());
      int steps = (Integer) this.stepsSpinner.getValue();
      runSim(steps,dt);

    }catch(NumberFormatException e){
      ViewUtils.showErrorMsg("Invalid Delta time format");
      this.stopped = true;
      setButtonsEnabled(true);
    }catch(Exception e){
      ViewUtils.showErrorMsg("Error starting the simulation.");
      this.stopped = true;
      setButtonsEnabled(true);
    }
  }

  private void runSim(int n, double dt) {
    if (n > 0 && !this.stopped) {
      try {
        this.ctrl.advance(dt);
        SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
      } catch (Exception e) {
        // TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
        //      que corresponda
        // TODO activar todos los botones
        ViewUtils.showErrorMsg("Error during the simulation " + e.getMessage());
        this.stopped = true;
        setButtonsEnabled(true);
      }
    } else {
      // TODO activar todos los botones
      setButtonsEnabled(true);
      this.stopped = true;
    }
  }
}
