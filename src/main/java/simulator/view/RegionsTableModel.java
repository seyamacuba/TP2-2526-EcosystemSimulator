package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//bueno es como la otra, falta poner logica
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

  private Controller ctrl;
  private Map<String, int[]> regions;

  RegionsTableModel(Controller ctrl) {
    this.ctrl = ctrl;
    ctrl.addObserver(this);
    regions = new HashMap<>();
  }

  @Override
  public int getRowCount() {
    return regions.size();
  }

  @Override
  public int getColumnCount() {
    return 5;
  }

  @Override
  public String getColumnName(int col) {
    switch(col){
      case 0: return "Row";
      case 1: return "Col";
      case 2: return "Desc.";
      case 3: return "HUNGER";
      case 4: return "DANGER";
      case 5: return "DEAD";
      default: return " ";
    }
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
}
