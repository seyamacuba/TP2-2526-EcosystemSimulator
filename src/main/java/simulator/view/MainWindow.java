package simulator.view;

import simulator.control.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {

  private final Controller ctrl; //para hablar con el controller

  public MainWindow(Controller ctrl) {
    super("[ECOSYSTEM SIMULATOR]");
    this.ctrl = ctrl;
    initGUI();
  }

  //MainWindow  tiene:
  //ControlPanel
  //ContentPanel :  SpeciesTable
  //                RegionsPanel
  //Statusbar
  private void initGUI() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    setContentPane(mainPanel);

    ControlPanel cp = new ControlPanel(ctrl); //ControlPanel
    mainPanel.add(cp, BorderLayout.PAGE_START);

    StatusBar sb = new StatusBar(ctrl);//StatusBar
    mainPanel.add(sb, BorderLayout.PAGE_END);

    //Definición del panel de tablas (usa un BoxLayout vertical)
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(ctrl)); //SpeciesTableModel
    speciesTable.setPreferredSize(new Dimension(500, 250));
    contentPanel.add(speciesTable);

    InfoTable regionsTable = new InfoTable("Regions", new RegionsTableModel(ctrl)); //RegionsTableModel
    regionsTable.setPreferredSize(new Dimension(500, 250));
    contentPanel.add(regionsTable);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ViewUtils.quit(MainWindow.this);
      }
    });

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    pack();
    setVisible(true);
  }
}
