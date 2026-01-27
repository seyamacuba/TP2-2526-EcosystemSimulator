package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal{
  protected Sheep(SelectionStrategy dangerStrategy, SelectionStrategy mateStrategy, Vector2D pos) {
    super(dangerStrategy, mateStrategy, pos);
  }

  protected Sheep(Sheep p1, Animal p2) {
    super(p1, p2);
  }

  @Override
  public Vector2D getPosition() {
    return null;
  }

  @Override
  public Vector2D getDestination() {
    return null;
  }

  @Override
  public boolean isPregnant() {
    return false;
  }

  @Override
  public void update(double dt) {

  }
}
