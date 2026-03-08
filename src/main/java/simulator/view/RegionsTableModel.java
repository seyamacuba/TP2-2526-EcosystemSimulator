package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.table.AbstractTableModel;
import java.util.List;

//bueno es como la otra, falta poner logica
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

  private Controller ctrl;

  RegionsTableModel(Controller ctrl) {
    this.ctrl = ctrl;
    ctrl.addObserver(this);
  }

  @Override
  public int getRowCount() {
    return 0;
  }

  @Override
  public int getColumnCount() {
    return 0;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return null;
  }

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {

  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {

  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {

  }
  // TODO el resto de métodos van aquí…
}
