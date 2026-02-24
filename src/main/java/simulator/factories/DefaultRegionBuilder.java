package simulator.factories;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {
  public DefaultRegionBuilder() {
    super("default", "Default region");
  }

  @Override
  protected Region createInstance(JSONObject data) {
    try{
      //Para una region por defecto no hace falta nada.
      return new DefaultRegion();
    } catch (Exception e){
      throw new IllegalArgumentException("Error creando el objeto");
    }
  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("info", "No requiere parámetros adicionales");
  }
}
