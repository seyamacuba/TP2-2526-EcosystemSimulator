package simulator.model;

import simulator.misc.Vector2D;

public interface AnimalInfo extends JSONable {
  State getState();

  Vector2D getPosition();

  String getGeneticCode();

  Diet getDiet();

  double getSpeed();

  double getSightRange();

  double getEnergy();

  double getAge();

  Vector2D getDestination();

  boolean isPregnant();
}
