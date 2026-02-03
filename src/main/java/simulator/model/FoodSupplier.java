package simulator.model;
//Para pedir comida para el animal a durante dt segundos.
public interface FoodSupplier {
  double getFood(AnimalInfo a, double dt);
}
