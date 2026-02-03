package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal{
  final static String SHEEP_GENETIC_CODE = "Sheep";
  final static double INIT_SIGHT_SHEEP = 40;
  final static double INIT_SPEED_SHEEP = 35;
  final static double BOOST_FACTOR_SHEEP = 2.0;
  final static double MAX_AGE_SHEEP = 8;
  final static double FOOD_DROP_BOOST_FACTOR_SHEEP = 1.2;
  final static double FOOD_DROP_RATE_SHEEP = 20.0;
  final static double DESIRE_THRESHOLD_SHEEP = 65.0;
  final static double DESIRE_INCREASE_RATE_SHEEP = 40.0;
  final static double PREGNANT_PROBABILITY_SHEEP = 0.9;

  protected Sheep(SelectionStrategy dangerStrategy, SelectionStrategy mateStrategy, Vector2D pos) {
    super(SHEEP_GENETIC_CODE, Diet.HERBIVORE, INIT_SIGHT_SHEEP, INIT_SPEED_SHEEP, mateStrategy, pos);
    //Terminar Constructor
  }

  protected Sheep(Sheep p1, Animal p2) {
    super(p1, p2); //Llamada a la super clase.
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
    if(this.getState() == State.DEAD){ //Si esta morío bye
      return;
    }

    if(this.getEnergy() == 0.0 && this.getAge() > 8.0){
      setState(State.DEAD); //Lo vuelvo a matar
    }

    //Posicion fuera del mapa.
    //Actualizar el objeto segun el estado del animal.
    //Pide food
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
