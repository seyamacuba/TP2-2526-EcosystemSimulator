package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;

public abstract class AnimalBuilder extends Builder<Animal> {
  protected final Factory<SelectionStrategy> strategyFactory;

  protected AnimalBuilder(String typeTag, String desc,
                          Factory<SelectionStrategy> strategyFactory) {
    super(typeTag, desc);
    this.strategyFactory = strategyFactory;
  }

  // --- helpers comunes ---

  protected Vector2D parseRandomPos(JSONObject data) {
    if (!data.has("pos")) return null;

    JSONObject pos = data.getJSONObject("pos");
    JSONArray xRange = pos.getJSONArray("x_range");
    JSONArray yRange = pos.getJSONArray("y_range");

    double xMin = xRange.getDouble(0);
    double xMax = xRange.getDouble(1);
    double yMin = yRange.getDouble(0);
    double yMax = yRange.getDouble(1);

    double x = xMin + Utils.RAND.nextDouble() * (xMax - xMin);
    double y = yMin + Utils.RAND.nextDouble() * (yMax - yMin);

    return new Vector2D(x, y);
  }

  protected SelectionStrategy parseStrategy(JSONObject data, String key) {
    if (data.has(key)) {
      return strategyFactory.createInstance(data.getJSONObject(key));
    } else {
      JSONObject def = new JSONObject();
      def.put("type", "first");
      return strategyFactory.createInstance(def);
    }
  }
}
