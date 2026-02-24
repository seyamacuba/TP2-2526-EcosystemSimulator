package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.List;

public abstract class Animal implements Entity,AnimalInfo {

  final static double INIT_ENERGY = 100.0;
  final static double MUTATION_TOLERANCE = 0.2;
  final static double NEARBY_FACTOR = 60.0;
  final static double COLLISION_RANGE = 8;
  final static double HUNGER_DECAY_EXP_FACTOR = 0.007;
  final static double MAX_ENERGY = 100.0;
  final static double MAX_DESIRE = 100.0;

  private String geneticCode;
  private Diet diet;
  private State state;
  private Vector2D pos;
  private Vector2D dest;
  private double energy;
  private double age;
  private double speed;
  private double desire;
  private double sightRange;
  private Animal mateTarget;
  private Animal baby;
  private AnimalMapView regionMngr;
  private SelectionStrategy mateStrategy;

  protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos){
    if(geneticCode == null || geneticCode.isEmpty()){
      throw new IllegalArgumentException("Genetic Code cannot be empty");
    }
    if(diet == null){
      throw new IllegalArgumentException("Diet cannot be null");
    }
    if(sightRange <= 0){
      throw new IllegalArgumentException("Sight Range must be positive");
    }
    if(initSpeed <= 0){
      throw new IllegalArgumentException("Initial Speed must be positive");
    }
    if(mateStrategy == null){
      throw new IllegalArgumentException("Mate Strategy cannot be null");
    }
    setState(State.NORMAL);
    setAge(0.0);
    setEnergy(INIT_ENERGY);
    setDesire(0.0);
    setDest(null);
    setMateTarget(null);
    setBaby(null);
    setRegionMngr(null);
    setGeneticCode(geneticCode);
    setDiet(diet);
    setSightRange(sightRange);
    setSpeed(Utils.getRandomizedParameter(initSpeed, 0.1));
    setMateStrategy(mateStrategy);
    setPos(pos);
  }
  protected Animal(Animal p1, Animal p2){
    setState(State.NORMAL);
    double energy = (p1.getEnergy()+p2.getEnergy())/2;
    setEnergy(energy);
    setDesire(0.0);
    setDest(null);
    setMateTarget(null);
    setBaby(null);
    setRegionMngr(null);
    setDiet(p1.getDiet());
    setGeneticCode(p1.getGeneticCode());
    setMateStrategy(p2.getMateStrategy());
    setPos(p1.getPosition().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1))));
    setSightRange(Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2,0.2));
    setSpeed(Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2));
    setAge(0.0);
  }
  @Override
  public String getGeneticCode() {
    return geneticCode;
  }

  protected void setGeneticCode(String geneticCode) {
    this.geneticCode = geneticCode;
  }

  @Override
  public Diet getDiet() {
    return diet;
  }

  protected void setDiet(Diet diet) {
    this.diet = diet;
  }

  @Override
  public State getState() {
    return state;
  }

  protected void setState(State state) {
    this.state = state;
    switch(this.state){
      case NORMAL:
        setNormalStateAction();
        break;
      case HUNGER:
        setHungerStateAction();
        break;
      case MATE:
        setMateStateAction();
        break;
      case DANGER:
        setDangerStateAction();
        break;
      case DEAD:
        setDeadStateAction();
        break;
      default: setNormalStateAction();
    }
  }

  public Vector2D getPos() {
    return pos;
  }

  protected void setPos(Vector2D pos) {
    this.pos = pos;
  }

  public Vector2D getDest() {
    return dest;
  }

  protected void setDest(Vector2D dest) {
    this.dest = dest;
  }

  @Override
  public double getEnergy() {
    return energy;
  }

  protected void setEnergy(double energy) {
    this.energy = energy;
  }

  @Override
  public double getAge() {
    return age;
  }

  protected void setAge(double age) {
    this.age = age;
  }

  @Override
  public double getSpeed() {
    return speed;
  }

  protected void setSpeed(double speed) {
    this.speed = speed;
  }

  public double getDesire() {
    return desire;
  }

  protected void setDesire(double desire) {
    this.desire = desire;
  }

  @Override
  public double getSightRange() {
    return sightRange;
  }

  protected void setSightRange(double sightRange) {
    this.sightRange = sightRange;
  }

  public Animal getMateTarget() {
    return mateTarget;
  }

  protected void setMateTarget(Animal mateTarget) {
    this.mateTarget = mateTarget;
  }

  public Animal getBaby() {
    return baby;
  }

  protected void setBaby(Animal baby) {
    this.baby = baby;
  }

  public AnimalMapView getRegionMngr() {
    return regionMngr;
  }

  protected void setRegionMngr(AnimalMapView regionMngr) {
    this.regionMngr = regionMngr;
  }

  public SelectionStrategy getMateStrategy() {
    return mateStrategy;
  }

  protected void setMateStrategy(SelectionStrategy mateStrategy) {
    this.mateStrategy = mateStrategy;
  }

  //faltan estos de AnimalInfo, aunque esten con otros nombres
  @Override
  public Vector2D getPosition() {
    return pos;
  }
  @Override
  public Vector2D getDestination() {
    return dest;
  }
  @Override
  public boolean isPregnant() {
    return baby != null;
  }



  //MÉTODOS:
  private Vector2D adjustPos(Vector2D pos, int width, int height){  //metodo de Ajustar Posiciones
    double x = pos.getX();
    double y = pos.getY();
    while (x>= width) x -= width;
    while (x < 0) x += width;
    while (y>= height) y -= height;
    while (y < 0) y += height;

    return new Vector2D(x,y);
  }

  public void init(AnimalMapView regMngr){ //Override del gestor
    setRegionMngr(regMngr);
    int width = regionMngr.getWidth();
    int height = regionMngr.getHeight();
    //si pos es null, pos aleatoria
    if(this.pos == null){
      double x = Utils.RAND.nextDouble() * (width - 1);
      double y = Utils.RAND.nextDouble() * (height - 1);
      this.pos = new Vector2D(x, y);
    }else { //si pos no es null, ajustar dentro del mapa si necesario
      this.pos = adjustPos(this.pos, width, height);
    }
    //elige pos aleatoria para dest
    double dx = Utils.RAND.nextDouble() * (width - 1);
    double dy = Utils.RAND.nextDouble() * (height - 1);
    this.dest = new Vector2D(dx, dy);
  }

  public Animal deliverBaby(){
    Animal b = baby;
    baby = null;
    return b;
  }

  protected void updateBasicAttributes(double dt, double energyLoss, double desireChange) {
    setAge(getAge() + dt);
    setEnergy(Math.max(0.0, Math.min(MAX_ENERGY, getEnergy() - energyLoss)));
    setDesire(Math.max(0.0, Math.min(MAX_DESIRE, getDesire() + desireChange)));
  }
  protected double calculateSpeed(double baseSpeed, double boost) {
    return baseSpeed * boost * Math.exp((getEnergy() - MAX_ENERGY) * HUNGER_DECAY_EXP_FACTOR);
  }
  protected Animal findTarget(SelectionStrategy strategy, String geneticCode) {
    List<Animal> candidates = getRegionMngr().getAnimalsInRange(this,
      a -> a.getGeneticCode().equals(geneticCode));
    return strategy.select(this, candidates);
  }

  protected Animal findPrey(SelectionStrategy strategy, Diet diet) {
    List<Animal> candidates = getRegionMngr().getAnimalsInRange(this,
      a -> a.getDiet() == diet);
    return strategy.select(this, candidates);
  }
  protected void move(double speed){
    pos = pos.plus(dest.minus(pos).direction().scale(speed));
  }

  @Override
  public JSONObject asJSON() { //CORREGIR.
    JSONObject obj = new JSONObject();
    obj.put("pos", new JSONArray(new double[]{pos.getX(), pos.getY()}));
    obj.put("gcode", geneticCode);
    obj.put("diet", diet.toString());
    obj.put("state", state.toString());
    return obj;
  }
  // MÉTODOS QUE NECESITAN IMPLEMENTAR LAS SUBCLASES:
  public abstract void update(double dt);
  protected abstract void setNormalStateAction();
  protected abstract void setHungerStateAction();
  protected abstract void setDeadStateAction();
  protected abstract void setDangerStateAction();
  protected abstract void setMateStateAction();
}
