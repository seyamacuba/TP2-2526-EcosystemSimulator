package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.*;
import java.util.List;

public class MapWindow extends JFrame implements EcoSysObserver {

  private final MapViewer mapViewer;

  public MapWindow(Controller ctrl) {
    super("[MAP VIEW]");

    this.mapViewer = new MapViewer();
    setContentPane(mapViewer);

    ctrl.addObserver(this);

    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent e) {
        ctrl.removeObserver(MapWindow.this);
        dispose();
      }
    });

    pack();
    setVisible(true);
  }

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    SwingUtilities.invokeLater(() -> {
      mapViewer.reset(time, map, animals);
      pack();
    });
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    SwingUtilities.invokeLater(() -> {
      mapViewer.reset(time, map, animals);
      pack();
    });
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    SwingUtilities.invokeLater(() -> {
      mapViewer.update(animals, time);
      pack();
    });
  }
}
