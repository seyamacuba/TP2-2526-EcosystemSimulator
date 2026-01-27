package simulator.model;

public interface AnimalMapView extends MapInfo, FoodSupplier{
  public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter);
}
