package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.State;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
 // TODO Decidir si Map p List, borrar lo demás
//La tabla animalets y cosas. Tengo que hacer la logica
class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
  //Creo que es mejor usar un ListObject[]> porque no podemos hacer ese switch con los estados
  //Con List podemos crear una tabla y accedemos con getValueAt
  //Igualmente lo dejaré comentado por si me equivoco
  private List<Object[]> rows;
  //private Controller ctrl;
  //tengo que crear una estructura para mis bichos
  //private Map<String, int[]> species;

  /*SpeciesTableModel(Controller ctrl) {
    this.ctrl = ctrl;
    ctrl.addObserver(this);
    species = new HashMap<>();
  }*/
  SpeciesTableModel(Controller ctrl) {
    this.rows = new ArrayList<>();
    ctrl.addObserver(this);
  }
  @Override
  public int getRowCount() {
    return rows.size();
  }

  /*@Override
  public int getColumnCount() {
    return 6; //Feo me parece
  }*/
  @Override
  public int getColumnCount(){
    return State.values().length +1 ;
    //En la primera columna tenemos el nombre de la especie
    //En los demas los posibles estados
  }

  /*@Override
  public String getColumnName(int col) {
    switch(col){
      case 0: return "Species";
      case 1: return "NORMAL";
      case 2: return "MATE";
      case 3: return "CARNIVORE";
      case 4: return "HERBIVORE";
      default: return " ";
    }
  }*/
  @Override
  public String getColumnName(int col){
    if(col == 0) return "Species";
    return State.values()[col-1].name();
  }
  @Override
  public Object getValueAt(int rowIndex, int columnIndex){
    Object[] row = rows.get(rowIndex);
    return row[columnIndex];
  }

  public void updateTable(List<AnimalInfo> animals) {

    //Recorremos los animales y los agrupamos por codigo
    //Usamos una lista auxiliar de nombres para mantener el orden de insercion
    List<String> codes = new ArrayList<>();
    List<int[]> counts = new ArrayList<>();
    List<Object[]> rows = new ArrayList<>();

    for (AnimalInfo a : animals) {
      String code = a.getGeneticCode();
      int stateIndex = a.getState().ordinal();
      int pos = codes.indexOf(code);
      if (pos == -1) {
        // Nueva especie
        codes.add(code);
        int[] c = new int[State.values().length];
        c[stateIndex]++;
        counts.add(c);
      } else {
        counts.get(pos)[stateIndex]++;
      }
    }

    // Construimos las filas finales: [nombre, count0, count1, ...]
    for (int i = 0; i < codes.size(); i++) {
      Object[] row = new Object[State.values().length + 1];
      row[0] = codes.get(i);
      for (int j = 0; j < State.values().length; j++) {
        row[j + 1] = counts.get(i)[j];
      }
      rows.add(row);
    }

    SwingUtilities.invokeLater(() -> {
      this.rows = rows;
      fireTableDataChanged();
    });//cambió la tablitaaa
  }

  //amo el intelliJ
  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    updateTable(animals);
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    updateTable(animals);
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
    updateTable(animals);
  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, simulator.model.RegionInfo r) {
    //No cambia la tabla de especies
  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    updateTable(animals);
  }

}
