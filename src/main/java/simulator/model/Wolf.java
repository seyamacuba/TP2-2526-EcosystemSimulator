package simulator.model;

import simulator.misc.Vector2D;
import simulator.misc.Utils;

import java.util.List;

public class Wolf extends Animal {
  final static String WOLF_GENETIC_CODE = "Wolf";
  final static double INIT_SIGHT_WOLF = 50.0;
  final static double INIT_SPEED_WOLF = 60.0;
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

  private SelectionStrategy huntingStrategy;
  private Animal huntTarget;

  public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos) {
    super(WOLF_GENETIC_CODE, Diet.CARNIVORE, INIT_SIGHT_WOLF, INIT_SPEED_WOLF, mateStrategy, pos);
    this.huntingStrategy = huntingStrategy;
    this.huntTarget = null;
  }

  protected Wolf(Wolf p1, Animal p2) {
    super(p1, p2);
    this.huntingStrategy = p1.huntingStrategy;
    this.huntTarget = null;
  }

  private double limit(double val, double min, double max){
    return Math.max(min, Math.min(max, val));
  }

  @Override
  public Vector2D getPosition() {return getPos();}

  @Override
  public Vector2D getDestination() {return getDest();}

  @Override
  public boolean isPregnant() {return getBaby() != null;}

  //MÉTODO UPDATE GENERAL:
  @Override
  public void update(double dt) {
    if(this.getState() == State.DEAD) return;
    switch(getState()){
      case NORMAL -> updateNormal(dt);
      case MATE -> updateMate(dt);
      case HUNGER -> updateHunger(dt);
    }

    if (getPos().getX() < 0 || getPos().getX() >= getRegionMngr().getWidth() ||
      getPos().getY() < 0 || getPos().getY() >= getRegionMngr().getHeight()) {

      // Usar adjustPos
      setPos(adjustPos(getPos(), getRegionMngr().getWidth(), getRegionMngr().getHeight()));

      setState(State.NORMAL); //  Cambiar estado a NORMAL

    }

    //Si ya no tiene ganas de vivir o ya esta fosil, apaga y vamonos
    if(getEnergy() <= 0.0 || getAge() > MAX_AGE_WOLF){
      setState(State.DEAD);
    }

    //Si esta fallecido, apaga y vamonos
    if(getState() != State.DEAD){
      double food = getRegionMngr().getFood(this,dt);
      setEnergy(limit(this.getEnergy() + food, 0.0, 100.0));
    }
  }

  //Lista de updates, aunque yo lo pondria en animal, porque sheep tmb va a usar esto.
private void updateNormal(double dt){
  clearDestinationOrRandom();

  double velocidad = INIT_SPEED_WOLF*dt*Math.exp((getEnergy()-100.0) * 0.007);
  move(velocidad);

  updateBasicAttributes(dt, FOOD_DROP_RATE_WOLF * dt, DESIRE_INCREASE_RATE_WOLF * dt);

  if(getEnergy() < FOOD_THRSHOLD_WOLF){
    this.setState(State.HUNGER);
  }else if(getDesire() > DESIRE_THRESHOLD_WOLF){
    this.setState(State.MATE);
  }
}

private void updateHunger(double dt){
    if(this.huntTarget != null && (huntTarget.getState() == State.DEAD || huntTarget.getPos().minus(getPos()).magnitude() > getSightRange())){
      huntTarget = null;
    }

    //Si no tiene objetivo, buscar uno
  if(this.huntTarget == null){
    List<Animal> targets = getRegionMngr().getAnimalsInRange(this, a ->a.getDiet() == Diet.HERBIVORE);
    huntTarget = huntingStrategy.select(this, targets);
  }

  if(this.huntTarget == null){
    //No hay presas, moverse normal
    clearDestinationOrRandom();//bug animales bailarines
    double velocidad = INIT_SPEED_WOLF*dt*Math.exp((getEnergy()-100.0) * 0.007);
    move(velocidad); //Avanzo
    updateBasicAttributes(dt, FOOD_DROP_RATE_WOLF * dt, DESIRE_INCREASE_RATE_WOLF * dt);
  }else{
    //Perseguir presa
    this.setDest(huntTarget.getPos());
    double velocidad = BOOST_FACTOR_WOLF*INIT_SPEED_WOLF*dt*Math.exp((getEnergy()-100.0) * 0.007);
    move(velocidad); //Avanzo
    updateBasicAttributes(dt, FOOD_DROP_RATE_WOLF * dt, DESIRE_INCREASE_RATE_WOLF * dt);
    Vector2D VectorDistancia = this.huntTarget.getPos().minus(this.getPos());
    double distancia = VectorDistancia.magnitude(); //OBTENGO LA DISTANCIA.

    if(distancia < COLLISION_RANGE){
      huntTarget.setState(State.DEAD);
      this.huntTarget = null;
      setEnergy(limit(getEnergy() + FOOD_EAT_VALUE_WOLF, 0.0, MAX_ENERGY));
    }
  }

  //Cambios de estado
  if(getEnergy() > FOOD_THRSHOLD_WOLF){
    if(getDesire() > DESIRE_THRESHOLD_WOLF){
      setState(State.MATE);
    }else{
      setState(State.NORMAL);
    }
  }
}

private void updateMate(double dt){
    //Si la pareja existe, pero esta muy lejos nos olvidamos.
  if (this.getMateTarget() != null && (getMateTarget().getState() == State.DEAD ||
    getMateTarget().getPos().minus(getPos()).magnitude() > getSightRange())) {
    setMateTarget(null);
  }

    if(getMateTarget() == null){
      //Conseguimos pareja nueva, hacer uso del lambda!!!!
      setMateTarget(findTarget(getMateStrategy(), WOLF_GENETIC_CODE));
    }

    //Tras la asignación
  if(getMateTarget() == null){
    //Si sigue siendo nulo, avanza.
    clearDestinationOrRandom();
    double velocidad = 3.0 * INIT_SPEED_WOLF * dt * Math.exp((getEnergy() - 100.0) * 0.007);
    move(velocidad);
    updateBasicAttributes(dt, FOOD_DROP_RATE_WOLF * dt, DESIRE_INCREASE_RATE_WOLF * dt);
  }else{
    setDest(getMateTarget().getPos());
    double velocidad = 3.0 * INIT_SPEED_WOLF * dt * Math.exp((getEnergy() - 100.0) * 0.007);
    move(velocidad);
    updateBasicAttributes(dt, FOOD_DROP_RATE_WOLF * FOOD_DROP_BOOST_FACTOR_WOLF * dt, DESIRE_INCREASE_RATE_WOLF * dt);

    Vector2D distanciaMate = getMateTarget().getPos().minus(getPos());

    if(distanciaMate.magnitude() < 8.0){
      getMateTarget().setDesire(0.0);
      setDesire(0.0);

      if (getBaby() == null && Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_WOLF) { //Que poco te gusta usar las constantes
        Wolf bebe = new Wolf(this, getMateTarget());
        setBaby(bebe);
      }

      this.setEnergy(limit(this.getEnergy() - 10.0, 0.0, 100.0));
      setMateTarget(null);
    }

  }

  //Final check:
  if(getEnergy() < FOOD_THRSHOLD_WOLF){
    setState(State.HUNGER);
  }else if(getDesire() < DESIRE_THRESHOLD_WOLF){
    setState(State.NORMAL);
    //Era innecesaria hacer el setHUnger/setNormal ya lo hace la funcion
  }
}



  @Override
  protected void setNormalStateAction() {
    this.huntTarget = null;
    this.setMateTarget(null);
  }

  @Override
  protected void setHungerStateAction() {
    this.setMateTarget(null);
  }

  @Override
  protected void setDeadStateAction() {
    this.huntTarget = null;
    this.setMateTarget(null);
  }

  @Override
  protected void setDangerStateAction() {
//Nunca puede estar en DANGER.
  }

  @Override
  protected void setMateStateAction() {
    huntTarget = null;
  }
}
