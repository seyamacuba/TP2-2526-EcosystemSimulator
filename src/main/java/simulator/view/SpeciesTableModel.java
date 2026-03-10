package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

//La tabla animalets y cosas. Tengo que hacer la logica
class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {

  private Controller ctrl;
  //tengo que crear una estructura para mis bichos
  private Map<String, int[]> species;

  SpeciesTableModel(Controller ctrl) {
    this.ctrl = ctrl;
    ctrl.addObserver(this);
    species = new HashMap<>();
  }

  @Override
  public int getRowCount() {
    return species.size();
  }

  @Override
  public int getColumnCount() {
    return 6;
  }

  @Override
  public String getColumnName(int col) {
    switch(col){
      case 0: return "Species";
      case 1: return "NORMAL";
      case 2: return "MATE";
      case 3: return "CARNIVORE";
      case 4: return "HERBIVORE";
      default: return " ";
    }
  }

  public void UpdateTable(List<AnimalInfo> animals) {
    species.clear();
    for (AnimalInfo a : animals) {
      String especie = a.getGeneticCode();
      int state = a.getState().ordinal();
      if(!species.containsKey(especie)){
        species.put(especie, new int[5]);
      }
      species.get(especie)[state]++;
    }
    fireTableDataChanged(); //cambió la tablitaaa
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
