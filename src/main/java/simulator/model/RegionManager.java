package simulator.model;

import org.json.JSONObject;

import java.util.*;
import java.util.function.Predicate;

public class RegionManager implements AnimalMapView{
  int cols, rows, width, height, regionWidth, regionHeight;
  List<DefaultRegion> regions;
  Map<Animal, Region> animalRegion;
  public RegionManager(int cols, int rows, int width, int height){
    this.cols = cols;
    this.rows = rows;
    this.width = width;
    this.height = height;
    this.regionWidth = width/cols;
    this.regionHeight = height/rows;
    regions =  new ArrayList<DefaultRegion>();
    animalRegion = new HashMap<Animal, Region>();
  }
  @Override
  public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
    return List.of();
  }

  @Override
  public double getFood(AnimalInfo a, double dt) {
    return 0;
  }

  @Override
  public int getCols() {
    return this.cols;
  }

  @Override
  public int getRows() {
    return this.rows;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getRegionWidth() {
    return this.regionWidth;
  }

  @Override
  public int getRegionHeight() {
    return this.regionHeight;
  }

  public void setRegion(int row, int col, Region r){

  }
  void registerAnimal(Animal a){

  }
  //encuentra la región a la que tiene que pertenecer el animal (a partir de su posición) y lo añade a esa región y actualiza animalRegion. Además, llama al método init pasándole una referencia a sí mismo (el gestor de regiones).

  void unregisterAnimal(Animal a){}
  //quita el animal de la región a la que pertenece y actualiza animalRegion.

  void updateanimalRegion(Animal a){}
  //encuentra la región a la que tiene que pertenecer el animal (a partir de su posición actual), y si es distinta de su región actual lo añade a la nueva región, lo quita de la anterior, y actualiza animalRegion.

  public double getFood(AnimalInfo a, double dt){}
  //llama a getFood de la región a la que pertenece el animal y devuelve el valor correspondiente.

  void updateAllRegions(double dt){}
  //llama a update de todas la regiones en la matriz de regiones.

  public List<Animal> getAnimalsInRange(Animal a, Predicate<Animal> filter){}
  //devuelve un lista de todos los animales que están en el campo visual del animal a y cumplen la condición filter. Debe consultar sólo las regiones en el campo visual.

  public JSONObject asJSON(){
    return null;
    //devuelve una estructura JSON de la siguiente forma
    //
    //  {
    //    "regions": [o1,o2,...]
    //  }donde oi es una estructura JSON que corresponde a una región y tiene la siguiente forma
    //
    // {
    //   "row": i,
    //   "col": j,
    //   "data": r
    //}
    //donde r es lo que devuelve asJSON() de la región en la fila i y columna j.
  }
  //
}
