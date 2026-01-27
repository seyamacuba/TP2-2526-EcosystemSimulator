package simulator.model;

import simulator.misc.Vector2D;

public class Wolf extends Animal {
  final static String WOLF_GENETIC_CODE = "Wolf";
  final static double INIT_SIGHT_WOLF = 50;
  final static double INIT_SPEED_WOLF = 60;
  final static double BOOST_FACTOR_WOLF = 3.0;
  final static double MAX_AGE_WOLF = 14.0;
  final static double FOOD_THRSHOLD_WOLF = 50.0;
  final static double FOOD_DROP_BOOST_FACTOR_WOLF = 1.2;
  final static double FOOD_DROP_RATE_WOLF = 18.0;
  final static double FOOD_DROP_DESIRE_WOLF = 10.0;
  final static double FOOD_EAT_VALUE_WOLF = 50.0;
  final static double DESIRE_THRESHOLD_WOLF = 65.0;
  final static double DESIRE_INCREASE_RATE_WOLF = 30.0;
  final static double PREGNANT_PROBABILITY_WOLF = 0.75;

  protected Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos) {
    super(WOLF_GENETIC_CODE, Diet.CARNIVORE, INIT_SIGHT_WOLF, INIT_SPEED_WOLF, mateStrategy, pos);
    //Terminar Constructor
  }

  protected Wolf(Wolf p1, Animal p2) {
    super(p1, p2);
  }

  @Override
  public Vector2D getPosition() {
    return getPos();
  }

  @Override
  public Vector2D getDestination() {
    return getDest();
  }

  @Override
  public boolean isPregnant() {
    return getBaby() != null;
  }

  @Override
  public void update(double dt) {

  }

  @Override
  protected void setNormalStateAction() {

  }

  @Override
  protected void setHungerStateAction() {

  }

  @Override
  protected void setDeadStateAction() {

  }

  @Override
  protected void setDangerStateAction() {

  }

  @Override
  protected void setMateStateAction() {

  }
}
