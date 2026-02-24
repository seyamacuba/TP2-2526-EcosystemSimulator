package simulator.strategies;

import simulator.model.Animal;

import java.util.List;

public interface SelectionStrategy {
  Animal select(Animal a, List<Animal> as);
}
