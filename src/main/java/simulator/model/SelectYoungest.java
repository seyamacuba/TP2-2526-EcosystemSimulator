package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {
  @Override
  public Animal select(Animal a, List<Animal> as) {
    if (!as.isEmpty()) {
      Animal peque = as.get(0);
      for (Animal e : as) {
        if (e.getAge() < peque.getAge()) {
          peque = e;
        }
      }
      return peque;
    } else {
      return null;
    }
  }
}
