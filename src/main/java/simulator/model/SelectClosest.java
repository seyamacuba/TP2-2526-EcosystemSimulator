package simulator.model;

import simulator.misc.Vector2D;

import java.util.List;

public class SelectClosest implements SelectionStrategy{
  private Vector2D pos;
  public SelectClosest(Vector2D pos){
    if(pos == null) throw new IllegalArgumentException("La posicion no puede ser nula" );
    this.pos = pos;
  }

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
