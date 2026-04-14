package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
  private List<Object[]> rows;

  RegionsTableModel(Controller ctrl) {
    this.rows = new ArrayList<>();
    ctrl.addObserver(this);
  }

  @Override
  public int getRowCount() {
    return rows.size();
  }

  @Override
  public int getColumnCount() {
    return 3 + Diet.values().length;
  }

  @Override
  public String getColumnName(int col) {
    return switch (col) {
      case 0 -> "Row";
      case 1 -> "Col";
      case 2 -> "Desc";
      default -> Diet.values()[col - 3].name();
    };
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Object[] row = rows.get(rowIndex);
    return row[columnIndex];
  }

  public void updateTable(MapInfo map) {
    List<Object[]> newRows = new ArrayList<>();

    // EMPIEZA EL BUCLE
    for (MapInfo.RegionData rd : map) {

      // Contar animales por dieta en esta región
      int[] counts = new int[Diet.values().length];
      for (AnimalInfo a : rd.r().getAnimalsInfo()) {
        counts[a.getDiet().ordinal()]++;
      }

      // Construimos la fila: [row, col, descripcion, count0, count1, ...]
      Object[] row = new Object[3 + Diet.values().length];
      row[0] = rd.row();
      row[1] = rd.col();
      row[2] = rd.r().toString();
      for (int i = 0; i < Diet.values().length; i++) {
        row[3 + i] = counts[i];
      }

      // Añadimos la fila a nuestra lista temporal
      newRows.add(row);

    }

    // 2. Sustituyo la lista original por la nueva de forma segura en el hilo de Swing
    SwingUtilities.invokeLater(() -> {
      this.rows = newRows;
      fireTableDataChanged();
    });
  }

  /*
    public void updateTable(MapInfo map) {
    rows.clear();

    for(MapInfo.RegionData rd : map) {
      //contar animales por dieta en esta región
      int[] counts = new int[Diet.values().length];

      for (AnimalInfo a : rd.r().getAnimalsInfo()) {
        counts[a.getDiet().ordinal()]++;
      }
      // Construimos la fila: [row, col, descripcion, count0, count1, ...]
      Object[] row = new Object[3 + Diet.values().length];
      row[0] = rd.row();
      row[1] = rd.col();
      row[2] = rd.r().toString();
      for (int i = 0; i < Diet.values().length; i++) {
        row[3 + i] = counts[i];
      }
        rows.add(row);
      }

    fireTableDataChanged();
  }

   */

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    updateTable(map);
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    updateTable(map);
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
    updateTable(map);
  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
    updateTable(map);
  }

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    updateTable(map);
  }
}
