package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.List;

public class Sheep extends Animal {
  final static String SHEEP_GENETIC_CODE = "Sheep";
  final static double INIT_SIGHT_SHEEP = 40.0;
  final static double INIT_SPEED_SHEEP = 35.0;
  final static double BOOST_FACTOR_SHEEP = 2.0;
  final static double MAX_AGE_SHEEP = 8.0;
  final static double FOOD_DROP_BOOST_FACTOR_SHEEP = 1.2;
  final static double FOOD_DROP_RATE_SHEEP = 20.0;
  final static double DESIRE_THRESHOLD_SHEEP = 65.0;
  final static double DESIRE_INCREASE_RATE_SHEEP = 40.0;
  final static double PREGNANT_PROBABILITY_SHEEP = 0.9;

  private Animal dangerSource;
  private SelectionStrategy dangerStrategy;

  public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
    super(SHEEP_GENETIC_CODE, Diet.HERBIVORE, INIT_SIGHT_SHEEP, INIT_SPEED_SHEEP, mateStrategy, pos);
    this.dangerStrategy = dangerStrategy;
    this.dangerSource = null;
  }

  protected Sheep(Sheep p1, Animal p2) {
    super(p1, p2); //Llamada a la super clase.
    this.dangerStrategy = p1.dangerStrategy;
    this.dangerSource = null;
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

  //En caso alguna de las actualizaciones deje valores fuera de rango
  private double limit(double val, double min, double max) {
    return Math.max(min, Math.min(max, val));
  }

  @Override
  public void update(double dt) {
    if (this.getState() == State.DEAD) return; //Si esta morío bye

    //Actualizar según estado
    switch (getState()) {
      case NORMAL -> updateNormal(dt);
      case DANGER -> updateDanger(dt);
      case MATE -> updateMate(dt);
    }

    // Ajustar si se sale del mapa
    if (getPos().getX() < 0 || getPos().getX() >= getRegionMngr().getWidth() ||
      getPos().getY() < 0 || getPos().getY() >= getRegionMngr().getHeight()) {

      double newX = limit(getPos().getX(), 0, getRegionMngr().getWidth() - 1);
      double newY = limit(getPos().getY(), 0, getRegionMngr().getHeight() - 1);
      setPos(new Vector2D(newX, newY));
      setState(State.NORMAL);
    }

    if (this.getEnergy() == 0.0 || getAge() > MAX_AGE_SHEEP) {
      setState(State.DEAD); //Lo vuelvo a matar
    }

    // Obtener comida si no está muerto
    if (getState() != State.DEAD) {
      double food = getRegionMngr().getFood(this, dt);
      setEnergy(limit(getEnergy() + food, 0.0, MAX_ENERGY));
    }
  }

  private void updateNormal(double dt) {
    // Moverse
    if (getDest() == null || getPos().distanceTo(getDest()) < COLLISION_RANGE) {
      double dx = Utils.RAND.nextDouble() * (getRegionMngr().getWidth() - 1);
      double dy = Utils.RAND.nextDouble() * (getRegionMngr().getHeight() - 1);
      setDest(new Vector2D(dx, dy));
    }

    double speed = INIT_SPEED_SHEEP * dt * Math.exp((getEnergy() - MAX_ENERGY) * HUNGER_DECAY_EXP_FACTOR);
    move(speed);

    // Actualizar atributos
    updateBasicAttributes(dt, FOOD_DROP_RATE_SHEEP * dt, DESIRE_INCREASE_RATE_SHEEP * dt);

    // Cambios de estado
    if(dangerSource == null) {
      List<Animal> wolves = getRegionMngr().getAnimalsInRange(this,
        a -> a.getDiet() == Diet.CARNIVORE);
      dangerSource = dangerStrategy.select(this, wolves); //ahora guardo mi lobito
    }

    //if (!wolves.isEmpty()) {
    if(dangerSource != null) {
      setState(State.DANGER);
    } else if (getDesire() > DESIRE_THRESHOLD_SHEEP) {
      setState(State.MATE);
    }
  }


  private void updateDanger(double dt) {
    // Comprobar si hay peligro
    if (dangerSource != null && (dangerSource.getState() == State.DEAD ||
      getPos().distanceTo(dangerSource.getPos()) > getSightRange())) {
      dangerSource = null;
    }

    if (dangerSource == null) {
      List<Animal> wolves = getRegionMngr().getAnimalsInRange(this,
        a -> a.getDiet() == Diet.CARNIVORE);
      dangerSource = dangerStrategy.select(this, wolves);
    }

    if (dangerSource == null) {
      // No hay peligro, volver a NORMAL
      double speed = BOOST_FACTOR_SHEEP * INIT_SPEED_SHEEP * dt *
        Math.exp((getEnergy() - MAX_ENERGY) * HUNGER_DECAY_EXP_FACTOR);
      move(speed);
      updateBasicAttributes(dt, FOOD_DROP_RATE_SHEEP * dt, DESIRE_INCREASE_RATE_SHEEP * dt);
    } else {
      // Huir del peligro
      setDest(getPos().minus(dangerSource.getPos().minus(getPos())));
      double speed = calculateSpeed(INIT_SPEED_SHEEP * dt, BOOST_FACTOR_SHEEP);
      move(speed);
      updateBasicAttributes(dt, FOOD_DROP_RATE_SHEEP * FOOD_DROP_BOOST_FACTOR_SHEEP * dt, DESIRE_INCREASE_RATE_SHEEP * dt);
    }

    // Cambio de estado
    if (dangerSource == null) {
      if (getDesire() < DESIRE_THRESHOLD_SHEEP) {
        setState(State.NORMAL);
      } else {
        setState(State.MATE);
      }
    }
  }


  private void updateMate(double dt) {
    // Comprobar pareja
    if (getMateTarget() != null && (getMateTarget().getState() == State.DEAD ||
      getPos().distanceTo(getMateTarget().getPos()) > getSightRange())) {
      setMateTarget(null);
    }

    if (getMateTarget() == null) {
      setMateTarget(findTarget(getMateStrategy(), SHEEP_GENETIC_CODE));
    }

    if (getMateTarget() == null) {
      // No hay pareja
      double speed = BOOST_FACTOR_SHEEP * INIT_SPEED_SHEEP * dt *
        Math.exp((getEnergy() - MAX_ENERGY) * HUNGER_DECAY_EXP_FACTOR);
      move(speed);
      updateBasicAttributes(dt, FOOD_DROP_RATE_SHEEP * dt, DESIRE_INCREASE_RATE_SHEEP * dt);
    } else {
      // Ir hacia la pareja
      setDest(getMateTarget().getPos());
      double speed = BOOST_FACTOR_SHEEP * INIT_SPEED_SHEEP * dt *
        Math.exp((getEnergy() - MAX_ENERGY) * HUNGER_DECAY_EXP_FACTOR);
      move(speed);
      updateBasicAttributes(dt, FOOD_DROP_RATE_SHEEP * FOOD_DROP_BOOST_FACTOR_SHEEP * dt, DESIRE_INCREASE_RATE_SHEEP * dt);
      // Si está cerca, reproducirse
      if (getPos().distanceTo(getMateTarget().getPos()) < COLLISION_RANGE) {
        setDesire(0.0);
        getMateTarget().setDesire(0.0);

        if (getBaby() == null && Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_SHEEP) {
          setBaby(new Sheep(this, getMateTarget()));
        }

        setEnergy(limit(getEnergy() - 10.0, 0.0, MAX_ENERGY));
        setMateTarget(null);
      }
    }


    // Cambios de estado
    List<Animal> wolves = getRegionMngr().getAnimalsInRange(this,
      a -> a.getDiet() == Diet.CARNIVORE);
    if (!wolves.isEmpty()) {
      setState(State.DANGER);
    } else if (getDesire() < DESIRE_THRESHOLD_SHEEP) {
      setState(State.NORMAL);
    }
  }

  @Override
  protected void setNormalStateAction() {
    dangerSource = null;
    setMateTarget(null);
  }

  @Override
  protected void setHungerStateAction() {
    //Sheep nunca entra en hunger
  }

  @Override
  protected void setDeadStateAction() {
    dangerSource = null;
    setMateTarget(null);
  }

  @Override
  protected void setDangerStateAction() {
    setMateTarget(null);
  }

  @Override
  protected void setMateStateAction() {
    dangerSource = null;
  }
}
