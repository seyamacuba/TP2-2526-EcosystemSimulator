package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class StatusBar extends JPanel implements EcoSysObserver {

  // TODO Añadir los atributos necesarios.

  StatusBar(Controller ctrl) {
    initGUI();
    ctrl.addObserver(this);
  }

  private void initGUI() {
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setBorder(BorderFactory.createBevelBorder(1));

    // TODO Crear varios JLabel para el tiempo, el número de animales, y la
    //      dimensión y añadirlos al panel. Puedes utilizar el siguiente código
    //      para añadir un separador vertical:
    //
    //     JSeparator s = new JSeparator(JSeparator.VERTICAL);
    //     s.setPreferredSize(new Dimension(10, 20));
    //     this.add(s);
  }

  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {}

  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {}

  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}

  public void onRegionSet(int row, int col, MapInfo map, simulator.model.RegionInfo r) {}

  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {}
}
