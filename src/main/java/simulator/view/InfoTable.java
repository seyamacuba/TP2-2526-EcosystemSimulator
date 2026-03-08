package simulator.view;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class InfoTable extends JPanel {

  private String title;
  private TableModel tableModel;

  InfoTable(String title, TableModel tableModel) {
    this.title = title;
    this.tableModel = tableModel;
    initGUI();
  }

  private void initGUI() { //comments del profe
    setLayout(new BorderLayout()); //cambiar el layout del panel a BorderLayout()
    setBorder(BorderFactory.createTitledBorder(title)); //añadir un borde con título al JPanel, con el texto this.title
    JTable table = new JTable(tableModel); //añadir un JTable (con barra de desplazamiento vertical) que use this.tableModel
    JScrollPane scroll = new JScrollPane(table);
    add(scroll, BorderLayout.CENTER);
  }
}
