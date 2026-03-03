package simulator.model;

import org.json.JSONObject;
import simulator.factories.Factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulator implements JSONable {
  //Esta clase tiene que recibir como atributos una factoría de animales y otra de regiones (ver el apartado Las Factorías).
  // Además tiene que llevar un gestor de regiones, una lista con todos los animales que están participando en la simulación, y el tiempo actual (double).

  private final Factory<Animal> animalsFactory;
  private final Factory<Region> regionsFactory;
  private final RegionManager regionMngr;
  private final List<Animal> animals;
  private double time;

  public Simulator(int cols, int rows, int width, int height,
                   Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
    if (cols <= 0 || rows <= 0 || width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Invalid map dimensions");
    }
    if (animalsFactory == null || regionsFactory == null) {
      throw new IllegalArgumentException("Null factories");
    }
    this.animalsFactory = animalsFactory;
    this.regionsFactory = regionsFactory;
    this.regionMngr = new RegionManager(cols, rows, width, height);
    this.animals = new ArrayList<>();
    this.time = 0.0;
  }

  //regions
  private void setRegion(int row, int col, Region r) {
    if (r == null) {
      throw new IllegalArgumentException("Null region");
    }
    regionMngr.setRegion(row, col, r);
  }

  public void setRegion(int row, int col, JSONObject rJson) {
    if (rJson == null) {
      throw new IllegalArgumentException("Null region JSON");
    }
    Region r = regionsFactory.createInstance(rJson); //aaa hice cosas y vaa
    setRegion(row, col, r);
  }

  //animals
  private void addAnimal(Animal a) {
    if (a == null) {
      throw new IllegalArgumentException("Null animal");
    }
    animals.add(a);
    regionMngr.registerAnimal(a);
  }

  public void addAnimal(JSONObject aJson) {
    if (aJson == null) {
      throw new IllegalArgumentException("Null animal JSON");
    }
    Animal a = animalsFactory.createInstance(aJson); //same que antes no se aiuda
    addAnimal(a);
  }

  //miau
  public MapInfo getMapInfo() {
    return regionMngr;
  }

  public List<? extends AnimalInfo> getAnimals() {
    return Collections.unmodifiableList(animals); //inmodificable :0
  }

  public double getTime() {
    return time;
  }

  public void advance(double dt) {
    if (dt <= 0) {
      throw new IllegalArgumentException("Invalid dt value, must be >0");
    }

    // Paso 1: incrementar tiempo
    time += dt; //incremento tiempo

    // Paso 2: quitar animales muertos
    List<Animal> muertos = new ArrayList<>();
    for (Animal a : animals) {
      if (a.getState() == State.DEAD) muertos.add(a);
    }
    for (Animal a : muertos) {
      regionMngr.unregisterAnimal(a);
      animals.remove(a);
    }


    // Paso 3: actualizar cada animal y su región
    for (Animal a : animals) {
      a.update(dt);
      regionMngr.updateAnimalRegion(a);
    }
    // Paso 4: actualizar todas las regiones
    regionMngr.updateAllRegions(dt);

    // Paso 5: hacer nacer babies
    List<Animal> littlebabies = new ArrayList<>();
    for (Animal a : animals) {
      if (a.isPregnant()) {
        Animal baby = a.deliverBaby();
        if (baby != null) littlebabies.add(baby);
      }
    }
    for (Animal baby : littlebabies) {
      addAnimal(baby);
    }
  }

  public JSONObject asJSON() {
    JSONObject o = new JSONObject();

    // limpiamos el número ANTES de dárselo al JSONObject
    double tiempoLimpio = Math.round(time * 1000.0) / 1000.0;

    // le pasamos el número ya limpio
    o.put("time", tiempoLimpio);
    o.put("state", regionMngr.asJSON());

    return o;
  }
  //estructura
  // {
  //   "time": t,
  //   "state": s
  //  }
}
