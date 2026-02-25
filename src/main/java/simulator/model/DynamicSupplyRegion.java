package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
  private final double growthFactor;
  private double food;

  public DynamicSupplyRegion(double food, double growthFactor) {
    super();
    if (food <= 0)
      throw new IllegalArgumentException("Initial food must be positive"); //cambio < a <= porque debe ser positivo, no puede ser 0
    if (growthFactor < 0) throw new IllegalArgumentException("Growth factor must be positive");
    this.food = food;
    this.growthFactor = growthFactor;
  }

  @Override
  public double getFood(AnimalInfo a, double dt) {
    double foodNeeded = calculateFood(a, dt);
    double foodConsumed = Math.min(food, foodNeeded);
    food -= foodConsumed;
    //Devuelve lo que el animal ha consumido, no lo que queda en la region
    return foodConsumed;
  }

  @Override
  public void update(double dt) {
    if (Utils.RAND.nextDouble() < 0.5) {
      food += growthFactor * dt;
    }
  }
}
