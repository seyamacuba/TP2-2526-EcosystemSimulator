package simulator.model;

import java.util.List;

//Informacion de las regiones.
public interface RegionInfo extends JSONable{
  public List<AnimalInfo> getAnimalsInfo();
}
