package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {
  @Override
  public Animal select(Animal a, List<Animal> as) {
    if (!as.isEmpty()) {
      return as.get(0);
    } else {
      return null;
    }
  }

}
