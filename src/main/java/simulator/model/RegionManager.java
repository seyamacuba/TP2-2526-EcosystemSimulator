package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;

import java.util.*;
import java.util.function.Predicate;

public class RegionManager implements AnimalMapView{
  int cols, rows, width, height, regionWidth, regionHeight;
  Region[][] regions; //Matriz de regiones mejor.
  Map<Animal, Region> animalRegion;
  public RegionManager(int cols, int rows, int width, int height){
    this.cols = cols;
    this.rows = rows;
    this.width = width;
    this.height = height;
    this.regionWidth = width/cols;
    this.regionHeight = height/rows;
    this.regions = new Region[rows][cols]; // Creo la matriz de regiones
    animalRegion = new HashMap<Animal, Region>();

    //Lleno la lista de regiones
    for(int i = 0; i < rows; i++){
      for(int j = 0; j < cols; j++){
        regions[i][j] = new DefaultRegion(); //HAY QUE AÑADIR PARÁMETROS AL DEFAULT REGION.
      }
    }
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

  public void setRegion(int row, int col, Region r){ //Sustituye la region.
    Region antiguo = regions[row][col];

    for(Animal a : antiguo.getAnimals()){
      r.addAnimal(a);
      animalRegion.put(a,r);
    }
    regions[row][col] = r; //Actualizo a la nueva region.
  }

  private Region getRegion(double x, double y){ //Método auxiliar privado, que obtiene la region a partir de un punto.
    int columna = (int) (x/regionWidth);
    int fila = (int) (y/regionHeight); //Obtengo los índices de la matriz.

    if(columna >= this.cols) columna = this.cols -1;
    if(columna < 0) columna = 0;
    if(fila >= this.rows) fila = this.rows -1;
    if(fila < 0) fila = 0;

    return regions[fila][columna];
  }
  void registerAnimal(Animal a){ //x e y son iguales a pixeles.
    Region actual = getRegion(a.getPos().getX(), a.getPos().getY()); //Obtengo la region a la que pertenece el animal.
    actual.addAnimal(a); //Lo añade a la región.
    animalRegion.put(a, actual); //Guarda la relación animal - región.
    a.init(this); //Seteo el regionManager el animal como el actual.
  }

  //encuentra la región a la que tiene que pertenecer el animal (a partir de su posición) y lo añade a esa región y actualiza animalRegion. Además, llama al método init pasándole una referencia a sí mismo (el gestor de regiones).

  void unregisterAnimal(Animal a){
    Region actual = animalRegion.get(a);
    if(actual != null){ //Si esta en una región.
      actual.removeAnimal(a);
      animalRegion.remove(a); //Lo elimino del animal region.
    }
  }
  //quita el animal de la región a la que pertenece y actualiza animalRegion.

  void updateanimalRegion(Animal a){
    Region pertenece = animalRegion.get(a); //Obtengo la region a la que pertenece.
    Region actual = getRegion(a.getPos().getX(), a.getPos().getY()); //Obtengo su región actual.

    if(pertenece != actual){
      pertenece.removeAnimal(a);
      actual.addAnimal(a);
      animalRegion.put(a, actual); //LO ELIMINA EL ANTERIOR AUTOMÁTICAMENTE.
    }
  }
  //encuentra la región a la que tiene que pertenecer el animal (a partir de su posición actual), y si es distinta de su región actual lo añade a la nueva región, lo quita de la anterior, y actualiza animalRegion.

  @Override
  public double getFood(AnimalInfo a, double dt){
    Region regionAnimal = animalRegion.get((Animal)a); //Obtengo la región del animal.
    return regionAnimal.getFood(a,dt);
  }
  //llama a getFood de la región a la que pertenece el animal y devuelve el valor correspondiente.

  public void updateAllRegions(double dt){
    for(int i = 0; i < this.rows; i++){
      for(int j = 0; j < this.cols; j++){
        regions[i][j].update(dt);

      }
    }
  }
  //llama a update de todas la regiones en la matriz de regiones.

  @Override
  public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter){
    List<Animal> animalsRange = new ArrayList<>();

    double sightRange = e.getSightRange();
    Vector2D posicion = e.getPos();

    //Calculo el rango donde puede ver el animal
    double minX = posicion.getX()-sightRange;
    double minY = posicion.getY()-sightRange;
    double maxX= posicion.getX()+sightRange;
    double maxY = posicion.getY()+sightRange;

    //Convierto a indices de la matriz.
    int xmin = (int) (minX/this.regionWidth);
    int xmax = (int) (maxX / this.regionWidth);
    int ymin = (int) (minY/this.regionHeight);
    int ymax = (int) (maxY/this.regionHeight);

    //Aseguro que no me salgo.
    xmin= Math.max(0, xmin);
    xmax = Math.min(this.cols-1, xmax);

    ymin= Math.max(0,ymin);
    ymax = Math.min(this.rows-1, ymax);

    for(int i = xmin; i <= xmax; i++){
      for(int j = ymin; j <= ymax; j++){
        Region region = this.regions[j][i]; //Obtengo la region.

        for(Animal a : region.getAnimals()){
          //Recorro todos los animales de esa region.

          if(a.getPos().minus(e.getPos()).magnitude() <= sightRange && a != e && filter.test(a)){
            animalsRange.add(a);
          }
        }
      }
    }

    return animalsRange;
  }
  //devuelve un lista de todos los animales que están en el campo visual del animal a y cumplen la condición filter. Debe consultar sólo las regiones en el campo visual.

  public JSONObject asJSON(){
    JSONObject json = new JSONObject();
    JSONArray arrayRegiones = new JSONArray();

    for(int i = 0; i < this.rows; i++){
      for(int j = 0; j < this.cols; j++){
        JSONObject region = new JSONObject();
        region.put("row", i);
        region.put("col", j);
        region.put("data", regions[i][j].asJSON());

        arrayRegiones.put(region); //Añado la region.
      }
    }

    json.put("regions",arrayRegiones); //Lo añado al json final.

    return json;

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
