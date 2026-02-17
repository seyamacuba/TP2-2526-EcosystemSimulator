package simulator.model;

import simulator.misc.Vector2D;
import simulator.misc.Utils;

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

  private SelectionStrategy huntingStrategy;
  private Animal huntTarget;

  protected Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos) {
    super(WOLF_GENETIC_CODE, Diet.CARNIVORE, INIT_SIGHT_WOLF, INIT_SPEED_WOLF, mateStrategy, pos);
    this.huntingStrategy = huntingStrategy;
    this.huntTarget = null;
  }

  protected Wolf(Wolf p1, Animal p2) {
    super(p1, p2);
    this.huntingStrategy = p1.huntingStrategy;
    this.huntTarget = null;
  }

  private double limitar(double val, double min, double max){
    return Math.max(min, Math.min(max, val));
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

  //Lista de updates, aunque yo lo pondria en animal, porque sheep tmb va a usar esto.
private void updateNormal(double dt){
  Vector2D distanciaVector = this.getDestination().minus(this.getPos()); //Obtengo la diferencia
  double distancia = distanciaVector.magnitude();

  if(distancia < 8.0){
    this.setDest(Vector2D.get_random_vector(0,getRegionMngr().getWidth())); //ns yo esto
  }
  double velocidad = INIT_SPEED_WOLF*dt*Math.exp((getEnergy()-100.0) * 0.007);
  move(velocidad);

  this.setAge(this.getAge() + dt);
  this.setEnergy(limitar(this.getEnergy() - 18.0 * dt, 0.0, 100.0));
  this.setDesire(limitar(getDesire() + 30.0 * dt, 0.0, 100.0));

  if(getEnergy() < FOOD_THRSHOLD_WOLF){
    this.setState(State.HUNGER);
  }else if(getDesire() > DESIRE_THRESHOLD_WOLF){
    this.setState(State.MATE);
  }
}

//updateHunger faltan implementar cosas y pulir
private void updateHunger(double dt){
    if(this.huntTarget != null && this.getState() == State.DEAD || getMateTarget().getPos().minus(getPos()).magnitude() > getSightRange()){
      setMateTarget(null);
    }

    //Si esta null.
  if(this.huntTarget == null){
    double velocidad = INIT_SPEED_WOLF*dt*Math.exp((getEnergy()-100.0) * 0.007);
    move(velocidad); //Avanzo
  }else{
    this.setDest(huntTarget.getPos());
    setAge(getAge()+dt);
    double velocidad = 3.0*INIT_SPEED_WOLF*dt*Math.exp((getEnergy()-100.0) * 0.007);
    move(velocidad); //Avanzo
    this.setEnergy(limitar(this.getEnergy() - 18.0 * dt, 0.0, 100.0));
    this.setDesire(limitar(getDesire() + 30.0 * dt, 0.0, 100.0));

    Vector2D VectorDistancia = this.huntTarget.getPos().minus(this.getPos());
    double distancia = VectorDistancia.magnitude(); //OBTENGO LA DISTANCIA.

    if(distancia < 8.0){
      huntTarget.setState(State.DEAD);
      this.huntTarget = null;
      setEnergy(limitar(getEnergy() + 50.0, 0.0, 100.0));
    }
  }
  if(getEnergy() > 50.0){
    if(getEnergy() < 65.0){
      setState(State.NORMAL);
    }else{
      setState(State.MATE);
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
      setMateTarget(getMateStrategy().select(this, getRegionMngr().getAnimalsInRange(this, a -> a.getGeneticCode().equals(WOLF_GENETIC_CODE))
      ));
    }

    //Tras la asignación
  if(getMateTarget() == null){
    //Si sigue siendo nulo, avanza.
    double velocidad = 3.0 * INIT_SPEED_WOLF * dt * Math.exp((getEnergy() - 100.0) * 0.007);
    move(velocidad);
  }else{
    setDest(getMateTarget().getPos());
    double velocidad = 3.0 * INIT_SPEED_WOLF * dt * Math.exp((getEnergy() - 100.0) * 0.007);
    move(velocidad);
    setAge(getAge() + dt);
    setEnergy(limitar(this.getEnergy() - (18.0 * 1.2) * dt, 0.0, 100.0));
    setDesire(limitar(getDesire() + 30.0 * dt, 0.0, 100.0));

    Vector2D distanciaMate = getMateTarget().getPos().minus(getPos());

    if(distanciaMate.magnitude() < 8.0){
      getMateTarget().setDesire(0.0);
      setDesire(0.0);

      if (getBaby() == null && Utils.RAND.nextDouble() < 0.9) {
        Wolf bebe = new Wolf(this, getMateTarget());
        setBaby(bebe);
      }

      this.setEnergy(limitar(this.getEnergy() - 10.0, 0.0, 100.0));
      setMateTarget(null);
    }

  }

  //Final check:
  if(getEnergy() < FOOD_THRSHOLD_WOLF){
    setState(State.HUNGER);
    setHungerStateAction();
  }else if(getDesire() < DESIRE_THRESHOLD_WOLF){
    setState(State.NORMAL);
    setNormalStateAction();
  }
}

//MÉTODO UPDATE GENERAL:
  @Override
  public void update(double dt) {
    if(this.getState() == State.DEAD) return;
    switch(getState()){
      case NORMAL -> {
        updateNormal(dt);
      }
      case MATE -> {
        updateMate(dt);
      }
      case HUNGER -> {
        updateHunger(dt);
      }
      case DANGER -> {
        //NO PUEDE ESTAR NUNCA EN DANGER.
      }
      case DEAD -> {
      }

    }

    //SI esta fuera del mapa, ajusta y cambia a normal.
    if(getPos().getX() >= getRegionMngr().getWidth() || getPos().getX() < 0 ||
      getPos().getY() >= getRegionMngr().getHeight() || getPos().getY() < 0){

      double xAjustada = limitar(getPos().getX(), 0, getRegionMngr().getWidth()-1);
      double yAjustada = limitar(getPos().getY(), 0, getRegionMngr().getHeight()-1);

      Vector2D posicionNueva = new Vector2D(xAjustada,yAjustada);
      this.setPos(posicionNueva);

      setState(State.NORMAL);
    }

    //Si ya no tiene ganas de vivir o ya esta fosil, apaga y vamonos
    if(getEnergy() <= 0.0 || getAge() > MAX_AGE_WOLF){
      setState(State.DEAD);
    }

    //Si esta fallecido, apaga y vamonos
    if(getState() != State.DEAD){
      double food = getRegionMngr().getFood(this,dt);
      setEnergy(limitar(this.getEnergy() + food, 0.0, 100.0));
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
