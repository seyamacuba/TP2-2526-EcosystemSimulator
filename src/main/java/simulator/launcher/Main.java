package simulator.launcher;

import org.apache.commons.cli.*;
import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.control.Controller;
import simulator.factories.*;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

  // default values for some parameters
  //
  private final static Double DEFAULT_TIME = 10.0; // in seconds
  private final static Double DEFAULT_DELTA_TIME = 0.03; // in seconds
  public static Double deltaTime = DEFAULT_DELTA_TIME;
  // Factorías
  public static Factory<SelectionStrategy> selectionStrategyFactory;
  public static Factory<Region> regionsFactory;
  public static Factory<Animal> animalsFactory;
  // some attributes to stores values corresponding to command-line parameters
  //
  private static Double time = null;
  private static String inFile = null;
  private static String outFile = null;
  private static boolean simpleViewer = false;
  private static ExecMode mode = ExecMode.GUI;

  private static void parseArgs(String[] args) {

    // define the valid command line options
    //
    Options cmdLineOptions = buildOptions();

    // parse the command line as provided in args
    //
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine line = parser.parse(cmdLineOptions, args);
      parseHelpOption(line, cmdLineOptions);
      parseModeOption(line);
      parseInFileOption(line);
      parseOutFileOption(line);
      parseTimeOption(line);
      parseDeltaTimeOption(line);
      parseSimpleViewerOption(line);

      // if there are some remaining arguments, then something wrong is
      // provided in the command line!
      //
      String[] remaining = line.getArgs();
      if (remaining.length > 0) {
        StringBuilder error = new StringBuilder("Illegal arguments:");
        for (String o : remaining)
          error.append(" ").append(o);
        throw new ParseException(error.toString());
      }

    } catch (ParseException e) {
      System.err.println(e.getLocalizedMessage());
      System.exit(1);
    }

  }

  private static Options buildOptions() {
    Options cmdLineOptions = new Options();

    // help
    cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

    // input file
    cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());
    // output file
    cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

    // steps
    cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
      .desc("An real number representing the total simulation time in seconds. Default value: "
        + DEFAULT_TIME + ".")
      .build());
    // delta-time
    cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
      .desc("A double representing actual time, in seconds, per simulation step. Default value: " + DEFAULT_DELTA_TIME + ".")
      .build());
    // simple viewer
    cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());
    //GUI
    cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
      .desc("Execution mode. Possible values: 'batch' (Batch mode), 'gui' (Graphical User Interface mode). Default value: 'gui'.").build());
    return cmdLineOptions;
  }

  private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
    if (line.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
      System.exit(0);
    }
  }

  private static void parseInFileOption(CommandLine line) throws ParseException {
    inFile = line.getOptionValue("i");
    if (mode == ExecMode.BATCH && inFile == null) {
      throw new ParseException("In batch mode an input configuration file is required");
    }
  }

  private static void parseOutFileOption(CommandLine line) throws ParseException {
    outFile = line.getOptionValue("o");
    if (mode == ExecMode.BATCH && outFile == null) {
      // En modo batch, necesitamos outFile para escribir el JSON (a no ser que quieras usar System.out)
      // throw new ParseException("In batch mode an output configuration file is required");
    }
  }

  private static void parseTimeOption(CommandLine line) throws ParseException {
    String t = line.getOptionValue("t", DEFAULT_TIME.toString());
    try {
      time = Double.parseDouble(t);
      assert (time >= 0);
    } catch (Exception e) {
      throw new ParseException("Invalid value for time: " + t);
    }
  }

  private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
    String dt = line.getOptionValue("dt", DEFAULT_DELTA_TIME.toString());
    try {
      deltaTime = Double.parseDouble(dt);
      if (deltaTime <= 0) throw new Exception();
    } catch (Exception e) {
      throw new ParseException("Invalid value for delta-time: " + dt);
    }
  }

  private static void parseSimpleViewerOption(CommandLine line) {
    simpleViewer = line.hasOption("sv");
  }

  private static void parseModeOption(CommandLine line) throws ParseException {
    String modeStr = line.getOptionValue("m", ExecMode.GUI.getTag());
    for (ExecMode m : ExecMode.values()) {
      if (m.getTag().equals(modeStr)) {
        mode = m;
        return;
      }
    }
    throw new ParseException("Invalid mode: " + modeStr);
  }

  private static void initFactories() {
    // 1) Initialize the strategies factory
    List<Builder<SelectionStrategy>> selectionStrategyBuilders = new ArrayList<>();
    selectionStrategyBuilders.add(new SelectFirstBuilder());
    selectionStrategyBuilders.add(new SelectClosestBuilder());
    selectionStrategyBuilders.add(new SelectYoungestBuilder());
    selectionStrategyFactory = new BuilderBasedFactory<>(selectionStrategyBuilders);

    // 2) Initialize the regions factory
    List<Builder<Region>> regionBuilders = new ArrayList<>();
    regionBuilders.add(new DefaultRegionBuilder());
    regionBuilders.add(new DynamicSupplyRegionBuilder());
    regionsFactory = new BuilderBasedFactory<>(regionBuilders);

    // 3) Initialize the animals factory (necesitan la de estrategias)
    List<Builder<Animal>> animalBuilders = new ArrayList<>();
    animalBuilders.add(new SheepBuilder(selectionStrategyFactory));
    animalBuilders.add(new WolfBuilder(selectionStrategyFactory));
    animalsFactory = new BuilderBasedFactory<>(animalBuilders);
  }

  private static JSONObject loadJSONFile(InputStream in) {
    return new JSONObject(new JSONTokener(in));
  }

  private static void start_batch_mode() throws Exception {
    // 1. Cargar archivo de entrada
    InputStream is = new FileInputStream(new File(inFile));
    JSONObject inputData = loadJSONFile(is);
    is.close();

    // 2. Crear archivo de salida
    OutputStream os;
    if (outFile != null) {
      os = new FileOutputStream(new File(outFile));
    } else {
      os = System.out; // Si no se especifica -o lo tiramos por consola
    }

    // 3. Obtener dimensiones del JSON para el Simulator
    int cols = inputData.getInt("cols");
    int rows = inputData.getInt("rows");
    int width = inputData.getInt("width");
    int height = inputData.getInt("height");

    // 4. Crear instancia de Simulator
    Simulator sim = new Simulator(cols, rows, width, height, animalsFactory, regionsFactory);

    // 5. Crear controlador
    Controller ctrl = new Controller(sim);

    // 6. Cargar datos
    // internamente llamará a sim.setRegion y sim.addAnimal.
    ctrl.loadData(inputData);

    // 7. Llamar al run
    ctrl.run(time, deltaTime, simpleViewer, os);

    // 8. Cerrar archivo
    if (outFile != null) {
      os.close();
    }
  }

  private static void start_GUI_mode() throws Exception {
    // inFile es opcional en GUI
    JSONObject inputData = null;
    if (inFile != null) {
      InputStream is = new FileInputStream(new File(inFile));
      inputData = loadJSONFile(is);
      is.close();
    }

    // Dimensiones por defecto si no hay fichero
    int cols = inputData != null ? inputData.getInt("cols") : 20;
    int rows = inputData != null ? inputData.getInt("rows") : 15;
    int width = inputData != null ? inputData.getInt("width") : 800;
    int height = inputData != null ? inputData.getInt("height") : 600;

    Simulator sim = new Simulator(cols, rows, width, height, animalsFactory, regionsFactory);
    Controller ctrl = new Controller(sim);

    SwingUtilities.invokeAndWait(() -> new MainWindow(ctrl));

    if (inputData != null) {
      ctrl.loadData(inputData);
    }

    // Arrancar la GUI en el hilo de Swing
    final JSONObject data = inputData;

  }

  private static void start(String[] args) throws Exception {
    initFactories();
    parseArgs(args);
    switch (mode) {
      case BATCH:
        start_batch_mode();
        break;
      case GUI:
        start_GUI_mode();
        break;
    }
  }

  public static void main(String[] args) {
    Utils.RAND.setSeed(2147483647L);
    try {
      start(args);
    } catch (Exception e) {
      System.err.println("Something went wrong ...");
      System.err.println();
      e.printStackTrace();
    }
  }

  private enum ExecMode {
    BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

    private final String tag;
    private final String desc;

    ExecMode(String modeTag, String modeDesc) {
      tag = modeTag;
      desc = modeDesc;
    }

    public String getTag() {
      return tag;
    }

    public String getDesc() {
      return desc;
    }
  }
}
