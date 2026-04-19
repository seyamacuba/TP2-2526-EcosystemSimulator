package simulator.view;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import javax.swing.*;
import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractMapViewer extends JComponent {

  public abstract void update(List<AnimalInfo> objs, Double time);

  public abstract void reset(double time, MapInfo map, List<AnimalInfo> animals);
}
