package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {
  protected List<Animal> animales;

  protected Region() {
    this.animales = new ArrayList<>();
  }

  //MÉTODOS:
  final void addAnimal(Animal a) { //NO override.
    this.animales.add(a);
  }

  final void removeAnimal(Animal a) {
    this.animales.remove(a);
  }

  final List<Animal> getAnimals() {
    return Collections.unmodifiableList(this.animales);
  }

  @Override
  public JSONObject asJSON() {
    JSONObject obj = new JSONObject();
    JSONArray animalsArray = new JSONArray();

    for (Animal a : animales) {
      animalsArray.put(a.asJSON());
    }
    obj.put("animals", animalsArray);
    return obj;
  }

  protected double calculateFood(AnimalInfo a, double dt) {
    if (a.getDiet() == Diet.CARNIVORE) {
      return 0.0;
    } else {
      //Contar herbívoros de la región
      int n = 0;
      for (Animal animal : animales) {
        if (animal.getDiet() == Diet.HERBIVORE) {
          n++;
        }
      }
      //Funcion dada en el enunciado
      return 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt;
    }
  }

  public List<AnimalInfo> getAnimalsInfo() {
    return new ArrayList<>(animales); // se puede usar Collections.unmodifiableList(animals);
  }
  //Explicar porque hacer "return animals" no funciona.
}
