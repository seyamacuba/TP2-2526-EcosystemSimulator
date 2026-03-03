package simulator.model;

//Solo comida a los animales herbívoros
public class DefaultRegion extends Region {
  @Override
  public void update(double dt) {
    //Lo dejo vacio, porque dice que el update no hace nada.
  }

  @Override
  public double getFood(AnimalInfo a, double dt) {
    return calculateFood(a, dt);
  }
  public String toString() {
    return "DefaultRegion";
  }
}


