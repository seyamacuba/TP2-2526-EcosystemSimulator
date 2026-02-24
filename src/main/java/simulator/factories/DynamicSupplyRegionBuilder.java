package simulator.factories;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

  public DynamicSupplyRegionBuilder() {
    super("dynamic", "dynamic supply region");
  }

  @Override
  protected Region createInstance(JSONObject data) {
    try{
      if(!data.has("factor")){
        throw new IllegalArgumentException("El factor es obligatorio");
      }
      double factor = data.getDouble("factor");

      if(!data.has("food")){
        throw new IllegalArgumentException("El JSON debe de contener food.");
      }
      double food = data.getDouble("food");

      if(factor <= 0){
        throw new IllegalArgumentException("El factor debe de ser mayor que 0");
      }
      if(food < 0){
        throw new IllegalArgumentException("La comida debe de ser mayor o igual que 0");
      }

      return new DynamicSupplyRegion(food, factor);
    }catch (Exception e){
      throw new IllegalArgumentException("Error creando el objeto");
    }
  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("food", "cantidad de comida inicial (double)");
    o.put("factor", "factor de crecimiento (double)");
  }
}
