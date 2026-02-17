package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy{
  @Override
  public Animal select(Animal a, List<Animal> as) {
    if(!as.isEmpty()){
      Animal cercano = null;

      double distanciaMinima = Double.MAX_VALUE;
      for(Animal e: as){
        if(e.getPos().minus(a.getPos()).magnitude() < distanciaMinima){
          cercano = e;
        }
      }
      return cercano;
    }else{
      return null;
    }
  }
}
