package simulator.strategies;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.misc.Vector2D;
import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {
  public SelectClosestBuilder() {
    super("closest", "Closest animal selection stategy");
  }

  @Override
  protected SelectionStrategy createInstance(JSONObject data) {
    if(!data.has("pos")){
      throw new IllegalArgumentException("El JSON debe contener pos");
    }

    try{
      JSONObject pos = data.getJSONObject("pos");

      if(!pos.has("x") || !pos.has("y")){
        throw new IllegalArgumentException("El JSON debe contener x e y");
      }

      double x = pos.getDouble("x");
      double y = pos.getDouble("y");

      return new SelectClosest();

    } catch (Exception e){
      throw new IllegalArgumentException("Error al crear el objeto");
    }
  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("pos","Necesita un JSONOBJECT con x e y");
  }
}
