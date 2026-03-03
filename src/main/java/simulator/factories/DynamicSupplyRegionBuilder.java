package simulator.factories;

import org.json.JSONObject;
import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

  public DynamicSupplyRegionBuilder() {
    super("dynamic", "Dynamic food supply");
  }

  @Override
  protected Region createInstance(JSONObject data) {
    // extraer food, es opcional, si no hay, por defecto 1000.0
    double food;
    if (data.has("food")) {
      food = data.getDouble("food");
    } else {
      food = 1000.0;
    }
    //extraer factor, es opcional, si no hay es 2.0
    double factor;
    if (data.has("factor")) {
      factor = data.getDouble("factor");
    } else {
      factor = 2.0;
    }

    if (factor < 0) {
      throw new IllegalArgumentException("El factor de crecimiento no puede ser negativo");
    }
    if (food <= 0) {
      throw new IllegalArgumentException("La comida inicial debe ser un valor positivo");
    }
    return new DynamicSupplyRegion(food, factor);
  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("factor", "food increase factor (optional, default 2.0)");
    o.put("food", "initial amount of food (optional, default 100.0)");
  }
}
