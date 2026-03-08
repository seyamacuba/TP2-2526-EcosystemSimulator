package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;

//La tabla animalets y cosas. Tengo que hacer la logica
class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {

  private Controller ctrl;
  //tengo que crear una estructura para mis bichos

  SpeciesTableModel(Controller ctrl) {
    this.ctrl = ctrl;
    ctrl.addObserver(this);
    //bichitos
  }

  @Override
  public int getRowCount() {
    return 0;
    //tamano de la estructura de bichitos
  }

  @Override
  public int getColumnCount() {
    return 0;
  }

  @Override
  public String getColumnName(int col) {
    return"";
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return null;
  }

  //amo el intelliJ
  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {}

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {}

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}

  @Override
  public void onRegionSet(int row, int col, MapInfo map, simulator.model.RegionInfo r) {}

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {}
}
