package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class StatusBar extends JPanel implements EcoSysObserver {
  //Su trabajo es solo pintar las cosas en el monitor.
  private JLabel dimensionesLabel;
  private JLabel timeLabel;
  private JLabel animalesLabel;

  StatusBar(Controller ctrl) {
    initGUI();
    ctrl.addObserver(this);
  }

  private void initGUI() {
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setBorder(BorderFactory.createBevelBorder(1));

    this.timeLabel = new JLabel("Time: 0.000");
    this.animalesLabel = new JLabel("Total Animals: 0");
    this.dimensionesLabel = new JLabel("Dimension: 0x0 0x0");

    this.add(this.timeLabel);
    this.add(createSeparator());

    this.add(this.animalesLabel);
    this.add(createSeparator());

    this.add(this.dimensionesLabel);
  }

  private JSeparator createSeparator() {
    JSeparator s = new JSeparator(JSeparator.VERTICAL);
    s.setPreferredSize(new Dimension(10, 20));
    return s;
  }

  private void updateInfo(double time, MapInfo map, List<AnimalInfo> animals) {
    this.timeLabel.setText("Time: " + String.format("%.3f", time));
    this.animalesLabel.setText("Total Animals: " + animals.size());
    this.dimensionesLabel.setText("Dimension: " + map.getWidth() + "x" + map.getHeight());
  }

  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    updateInfo(time, map, animals);
  }

  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    updateInfo(time, map, animals);
  }

  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
  }

  public void onRegionSet(int row, int col, MapInfo map, simulator.model.RegionInfo r) {
  }

  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    updateInfo(time, map, animals);
  }
}
