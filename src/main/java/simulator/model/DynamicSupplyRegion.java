package simulator.model;

import simulator.misc.Utils;

import java.util.Random;

public class DynamicSupplyRegion implements Entity {
  private double initFood = 1.0;
  private double growthFactor = 0.0;
  private double food = initFood;
  public DynamicSupplyRegion(double initFood, double growthFactor) {
    this.initFood = initFood;
    this.growthFactor = growthFactor;
  }
  public double getFood(Animal animal, double dt) {
    if(animal.getDiet() == Diet.CARNIVORE) {
      return 0.0;
    }else{
      double foodConsumed = Math.min(food,60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt);
      food -= foodConsumed;
      return foodConsumed;
    }
  }
  @Override
  public void update(double dt) {
    int rand = Utils.RAND.nextInt(101);
    if(50 <= rand){
      this.food = this.food + growthFactor * dt;
    }

  }
}
