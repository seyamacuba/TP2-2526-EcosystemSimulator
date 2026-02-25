package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {
  public SelectClosest() {
  }

  @Override
  public Animal select(Animal a, List<Animal> as) {
    if (as.isEmpty()) return null;

    Animal cercano = null;
    double distanciaMinima = Double.MAX_VALUE;

    for (Animal e : as) {
      double d = a.getPosition().distanceTo(e.getPosition());
      if (d < distanciaMinima) {
        distanciaMinima = d;
        cercano = e;
      }
    }
    return cercano;
  }
}
