package simulator.model;

import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.Vector;

public abstract class Animal implements Entity,AnimalInfo {
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

  @Override
  public String getGeneticCode() {
    return geneticCode;
  }

  public void setGeneticCode(String geneticCode) {
    this.geneticCode = geneticCode;
  }

  @Override
  public Diet getDiet() {
    return diet;
  }

  public void setDiet(Diet diet) {
    this.diet = diet;
  }

  @Override
  public State getState() {
    return state;
  }

  public void setState(State state) {
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

  public void setPos(Vector2D pos) {
    this.pos = pos;
  }

  public Vector2D getDest() {
    return dest;
  }

  public void setDest(Vector2D dest) {
    this.dest = dest;
  }

  @Override
  public double getEnergy() {
    return energy;
  }

  public void setEnergy(double energy) {
    this.energy = energy;
  }

  @Override
  public double getAge() {
    return age;
  }

  public void setAge(double age) {
    this.age = age;
  }

  @Override
  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public double getDesire() {
    return desire;
  }

  public void setDesire(double desire) {
    this.desire = desire;
  }

  @Override
  public double getSightRange() {
    return sightRange;
  }

  public void setSightRange(double sightRange) {
    this.sightRange = sightRange;
  }

  public Animal getMateTarget() {
    return mateTarget;
  }

  public void setMateTarget(Animal mateTarget) {
    this.mateTarget = mateTarget;
  }

  public Animal getBaby() {
    return baby;
  }

  public void setBaby(Animal baby) {
    this.baby = baby;
  }

  public AnimalMapView getRegionMngr() {
    return regionMngr;
  }

  public void setRegionMngr(AnimalMapView regionMngr) {
    this.regionMngr = regionMngr;
  }

  public SelectionStrategy getMateStrategy() {
    return mateStrategy;
  }

  public void setMateStrategy(SelectionStrategy mateStrategy) {
    this.mateStrategy = mateStrategy;
  }

  protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos){
    setState(State.NORMAL);
    setEnergy(100.0);
    setDesire(0.0);
    setDest(null);
    setMateTarget(null);
    setBaby(null);
    setRegionMngr(null);
    setGeneticCode(geneticCode);
    setDiet(diet);
    setSightRange(sightRange);
    setSpeed(initSpeed);
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

  }

  //MÉTODOS:
  void init(AnimalMapView regMngr){ //Override del gestor

  }

  Animal deliverBaby(Animal mama){
    Animal baby = mama.baby;
    mama.baby = null;
    return baby;
  }

  protected void move(double speed){
    pos = pos.plus(dest.minus(pos).direction().scale(speed));
  }

  @Override
  public JSONObject asJSON() { //CORREGIR.
    return AnimalInfo.super.asJSON();
  }

  protected void setNormalStateAction(){

  }
  protected void setHungerStateAction(){

  }
  protected void setDeadStateAction(){

  }protected void setDangerStateAction(){

  }
  protected void setMateStateAction(){

  }
}
