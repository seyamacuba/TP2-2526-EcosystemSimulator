package simulator.view;

import simulator.control.Controller;
import simulator.launcher.Main;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.io.File;
import java.util.Objects;


class ControlPanel extends JPanel {

  private final Controller ctrl;
  private ChangeRegionsDialog changeRegionsDialog;
  private JSpinner delaySpinner; //Creo un nuevo spinner.

  private JFileChooser fc;
  //private boolean stopped = true; // utilizado en los botones de run/stop

  private JButton quitButton;
  private JButton openButton;
  private JButton mapButton;
  private JButton regionsButton;
  private JButton runButton;
  private JButton stopButton;
  private JSpinner stepsSpinner;
  private JTextField deltaTimeField;
  volatile Thread thread;

  ControlPanel(Controller ctrl) {
    this.ctrl = ctrl;
    initGUI();
  }

  private void initGUI() { //el profe no sabe, toolabar huh
    setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    add(toolBar, BorderLayout.PAGE_START);
    //INICIALIZACIONES:

    this.fc = new JFileChooser();
    this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

    this.changeRegionsDialog = new ChangeRegionsDialog(this.ctrl); //Falta implementar.

    this.openButton = new JButton();
    this.openButton.setToolTipText("Load an input file");
    this.openButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/open.png"))));
    this.openButton.addActionListener(e -> loadFile());
    toolBar.add(this.openButton);

    toolBar.addSeparator();

    this.mapButton = new JButton();
    this.mapButton.setToolTipText("Open Map Viewer");
    this.mapButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/viewer.png"))));
    this.mapButton.addActionListener(e -> new MapWindow(this.ctrl));
    toolBar.add(this.mapButton);

    this.regionsButton = new JButton();
    this.regionsButton.setToolTipText("Change Regions");
    this.regionsButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/regions.png"))));
    this.regionsButton.addActionListener(e -> this.changeRegionsDialog.open(ViewUtils.getWindow(this))); //Terminar, falta change regions para q funsione open
    toolBar.add(this.regionsButton);

    toolBar.addSeparator();

    this.runButton = new JButton();
    this.runButton.setToolTipText("Run Simulation");
    this.runButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/run.png"))));
    this.runButton.addActionListener(e -> startSimulation());
    toolBar.add(this.runButton);

    toolBar.addSeparator();

    this.stopButton = new JButton();
    this.stopButton.setToolTipText("Stop Simulation");
    this.stopButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/stop.png"))));
    this.stopButton.addActionListener(e -> {
      if(this.thread != null){
        this.thread.interrupt();
      }
    });
    toolBar.add(this.stopButton);

    toolBar.add(new JLabel(" Steps: "));
    this.stepsSpinner = new JSpinner(new SpinnerNumberModel(10000, 1, 100000, 100));
    this.stepsSpinner.setMaximumSize(new Dimension(80, 40));
    toolBar.add(this.stepsSpinner);

    toolBar.add(new JLabel(" Delta-Time: "));
    this.deltaTimeField = new JTextField(String.valueOf(Main.deltaTime));
    this.deltaTimeField.setMaximumSize(new Dimension(80, 40));
    toolBar.add(this.deltaTimeField);

    toolBar.add(new JLabel("Delay:"));

    this.delaySpinner = new JSpinner(new SpinnerNumberModel(0,0,1000,1));
    this.delaySpinner.setMaximumSize(new Dimension(80,40));
    this.delaySpinner.setToolTipText("Retardo entre pasos de simulación consecutivos");
    toolBar.add(this.delaySpinner);

    // Quit Button
    toolBar.add(Box.createGlue()); // this aligns the button to the right
    toolBar.addSeparator();
    this.quitButton = new JButton();
    this.quitButton.setToolTipText("Quit");
    this.quitButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/exit.png"))));
    this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
    toolBar.add(quitButton);
  }

  private void loadFile() {
    int returnVal = this.fc.showOpenDialog(ViewUtils.getWindow(this));//devuelve un código numérico que devuelve la ventana para decirte que boton se ha pulsado.
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = this.fc.getSelectedFile();

      try {
        java.io.InputStream is = new java.io.FileInputStream(file);
        org.json.JSONObject jsonInput = new org.json.JSONObject(new org.json.JSONTokener(is));

        int cols = jsonInput.getInt("cols");
        int rows = jsonInput.getInt("rows");
        int width = jsonInput.getInt("width");
        int height = jsonInput.getInt("height");

        this.ctrl.reset(cols, rows, width, height);
        this.ctrl.loadData(jsonInput);

      } catch (Exception e) {
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

  private void startSimulation() {

    setButtonsEnabled(false); //Deshabilita todos los botones al empezar.
    this.stopButton.setEnabled(true); //El único que puede funcionar.

    this.thread = new Thread(() -> { //Abro hilo secundario
      try{
        double dt= Double.parseDouble(this.deltaTimeField.getText());
        int steps = (Integer) this.stepsSpinner.getValue();

        runSim(steps,dt);
      }catch(NumberFormatException e){
        ViewUtils.showErrorMsg("Formato Delta Time inválido.");
      } catch(Exception e){
        ViewUtils.showErrorMsg("Error en la inicialización de la simulación.");
      } finally{
        SwingUtilities.invokeLater(() -> { //Rehabilito la interfaz, cuando acaba.
          setButtonsEnabled(true);
          this.thread = null;
        });
      }
    });

    this.thread.start();
  }

  private void runSim(int n, double dt) {
    //int delay = (Integer) this.delaySpinner.getValue(); //Obtengo el valor del delay del spinner antes de empezar

    //HILO SECUNDARIO
    while (n > 0 && !Thread.currentThread().isInterrupted()) { //Este es el hilo que se ejecuta el modelo, la simulación.
      try {
        this.ctrl.advance(dt); //Aqui se ejecuta :b

        int delay = (Integer) this.delaySpinner.getValue(); //Permite cambiar mejor el delay mientras se ejecuta.
        if(delay > 0){
          Thread.sleep(delay);
        }
        //SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
      } catch (InterruptedException e) {
        //Si el hilo es interrumpido mientras esta sleep, nos salimos.
        Thread.currentThread().interrupt();
        break;
      }catch(Exception e){
        ViewUtils.showErrorMsg("Error during the simulation " + e.getMessage());
        Thread.currentThread().interrupt();
        break;
      }
      n--; //Decremento el contador de pasos zzz
    }
  }
}
