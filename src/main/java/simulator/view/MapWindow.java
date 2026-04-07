package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MapWindow extends JFrame implements EcoSysObserver {

  private final Controller ctrl;
  private final MapViewer mapViewer;

  public MapWindow(Controller ctrl) {
    super("[MAP VIEW]");
    this.ctrl = ctrl;

    this.mapViewer = new MapViewer();
    setContentPane(mapViewer);

    this.ctrl.addObserver(this);

    pack();
    setVisible(true);
  }

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    mapViewer.reset(time, map, animals);
    pack();
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    mapViewer.reset(time, map, animals);
    pack();
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    mapViewer.update(animals, time);
  }
}
