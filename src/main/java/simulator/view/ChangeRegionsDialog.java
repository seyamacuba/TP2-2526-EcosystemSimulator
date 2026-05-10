package simulator.view;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

  private DefaultComboBoxModel<String> regionsModel;
  private DefaultComboBoxModel<String> fromRowModel;
  private DefaultComboBoxModel<String> toRowModel;
  private DefaultComboBoxModel<String> fromColModel;
  private DefaultComboBoxModel<String> toColModel;


  private DefaultTableModel dataTableModel;
  private final Controller ctrl;
  private List<JSONObject> regionsInfo;

  private final String[] headers = {"Key", "Value", "Description"};

  private JComboBox<String> regionsCombo; // atributo de clase para acceder desde el listener

  ChangeRegionsDialog(Controller ctrl) {
    super((Frame) null, true);
    this.ctrl = ctrl;
    initGUI();
    ctrl.addObserver(this);
  }

  private void initGUI() {
    setTitle("Change Regions");
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setContentPane(mainPanel);

    //Panel 1: texto de ayuda
    JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    helpPanel.add(new JLabel(
      "Select a region type and the rows/cols range, then click OK to apply."
    ));
    mainPanel.add(helpPanel);

    // this.regionsInfo se usará para establecer la información en la tabla
    this.regionsInfo = Main.regionsFactory.getInfo();

    // this.dataTableModel es un modelo de tabla que incluye todos los parámetros de la region
    this.dataTableModel = new DefaultTableModel() {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 1;
      }
    };
    this.dataTableModel.setColumnIdentifiers(this.headers);

    // this.regionsModel es un modelo de combobox que incluye los tipos de regiones
    this.regionsModel = new DefaultComboBoxModel<>();
    for (JSONObject j : regionsInfo) {
      regionsModel.addElement(j.getString("desc"));
    }

    this.fromRowModel = new DefaultComboBoxModel<>();
    this.toRowModel = new DefaultComboBoxModel<>();
    this.fromColModel = new DefaultComboBoxModel<>();
    this.toColModel = new DefaultComboBoxModel<>();

    //Panel 2: tabla de parametros de la regió
    JTable dataTable = new JTable(this.dataTableModel);
    JScrollPane scroll = new JScrollPane(dataTable); // tabla DENTRO del scroll
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(scroll, BorderLayout.CENTER);
    mainPanel.add(tablePanel);

    //Panel 3: combobox de regiones y rango de filas/columnas
    this.regionsCombo = new JComboBox<>(regionsModel);
    // al cambiar el tipo de region, actualizamos la tabla de parametros
    this.regionsCombo.addActionListener(e -> updateTableForRegion(regionsCombo.getSelectedIndex()));

    JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    comboPanel.add(new JLabel("Region type:"));
    comboPanel.add(this.regionsCombo);
    comboPanel.add(new JLabel("From row:"));
    comboPanel.add(new JComboBox<>(fromRowModel));
    comboPanel.add(new JLabel("To row:"));
    comboPanel.add(new JComboBox<>(toRowModel));
    comboPanel.add(new JLabel("From col:"));
    comboPanel.add(new JComboBox<>(fromColModel));
    comboPanel.add(new JLabel("To col:"));
    comboPanel.add(new JComboBox<>(toColModel));
    mainPanel.add(comboPanel);

    //Panel 4: botones OK y Cancelar
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton okButton = new JButton("OK");
    okButton.addActionListener(e -> {   // ← aquí
      try {
        int fromRow = Integer.parseInt((String) fromRowModel.getSelectedItem());
        int toRow = Integer.parseInt((String) toRowModel.getSelectedItem());
        int fromCol = Integer.parseInt((String) fromColModel.getSelectedItem());
        int toCol = Integer.parseInt((String) toColModel.getSelectedItem());
        //Construimos data con los valores de la tabla
        JSONObject data = new JSONObject();
        for (int i = 0; i < dataTableModel.getRowCount(); i++) {
          String key = (String) dataTableModel.getValueAt(i, 0);
          String val = (String) dataTableModel.getValueAt(i, 1);
          if (val != null && !val.isEmpty()) {
            data.put(key, Double.parseDouble(val));
          }
        }
        //Construir spec con el tipo y los datos
        JSONObject spec = new JSONObject();
        spec.put("type", regionsInfo.get(this.regionsCombo.getSelectedIndex()).getString("type"));
        spec.put("data", data);

        // Construir el JSON final y aplicarlo
        JSONObject region = new JSONObject();
        region.put("row", new JSONArray(new int[]{fromRow, toRow}));
        region.put("col", new JSONArray(new int[]{fromCol, toCol}));
        region.put("spec", spec);

        JSONObject rs = new JSONObject();
        rs.put("regions", new JSONArray().put(region));

        ctrl.setRegions(rs);
        setVisible(false);
      } catch (Exception ex) {
        ViewUtils.showErrorMsg("Error applying regions: " + ex.getMessage());
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> setVisible(false));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    mainPanel.add(buttonPanel);

    //setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
    updateTableForRegion(0); //cargar parametros de la primera region al arrancar
    pack();
    setResizable(false);
    setVisible(false);
  }

  public void open(Frame parent) {
    setLocation(
      parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
      parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
    pack();
    setVisible(true);
  }

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    updateRangeModels(map);
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    updateRangeModels(map);
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

  //Rellena los comboboxes de rango segun las dimensiones del mapa
  private void updateRangeModels(MapInfo map) {
    SwingUtilities.invokeLater(() -> {
      fromRowModel.removeAllElements();
      toRowModel.removeAllElements();
      fromColModel.removeAllElements();
      toColModel.removeAllElements();
      for (int i = 0; i < map.getRows(); i++) {
        fromRowModel.addElement(String.valueOf(i));
        toRowModel.addElement(String.valueOf(i));
      }
      for (int i = 0; i < map.getCols(); i++) {
        fromColModel.addElement(String.valueOf(i));
        toColModel.addElement(String.valueOf(i));
      }
    });
  }

  //Rellena la tabla con los parametros del tipo de region seleccionado
  private void updateTableForRegion(int index) {
    dataTableModel.setRowCount(0);
    JSONObject data = regionsInfo.get(index).getJSONObject("data");
    for (String key : data.keySet()) {
      this.dataTableModel.addRow(new Object[]{key, "", data.getString(key)});
    }
  }
}
