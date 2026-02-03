package simulator.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{
  protected List<Animal> animales;
  protected Region(){
    this.animales = new ArrayList<>();
  }

  //MÉTODOS:
  final void addAnimal(Animal a){ //NO override.
    this.animales.add(a);
  }

  final void removeAnimal(Animal a){
    this.animales.remove(a);
  }

  final List<Animal> getAnimals(){
    return Collections.unmodifiableList(this.animales);
  }

  //public JSONObject asJSON(){

  //}
}

