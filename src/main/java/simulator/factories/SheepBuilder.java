package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {
  private final Factory<SelectionStrategy> strategyFactory;

  public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("sheep", "Sheep animal");
    this.strategyFactory = strategyFactory;
  }

  @Override
  protected Animal createInstance(JSONObject data) {
    // 1. POSICIÓN (O es un array exacto [x,y] o es null)
    Vector2D position = null;
    if (data.has("pos")) {
      JSONObject pos = data.getJSONObject("pos");

      JSONArray xRange = pos.getJSONArray("x_range");
      JSONArray yRange = pos.getJSONArray("y_range");

      double xMin = xRange.getDouble(0);
      double xMax = xRange.getDouble(1);
      double yMin = yRange.getDouble(0);
      double yMax = yRange.getDouble(1);

      // Generar posición aleatoria dentro del rango
      double x = xMin + Utils.RAND.nextDouble() * (xMax - xMin);
      double y = yMin + Utils.RAND.nextDouble() * (yMax - yMin);

      position = new Vector2D(x, y);
    }

    // 2. ESTRATEGIA MATE
    SelectionStrategy mateStrategy;
    if (data.has("mate_strategy")) {
      mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
    } else {
      // Valor por defecto: SelectFirst
      JSONObject defaultMate = new JSONObject();
      defaultMate.put("type", "first");
      mateStrategy = strategyFactory.createInstance(defaultMate);
    }

    // 3. ESTRATEGIA DANGER
    SelectionStrategy dangerStrategy;
    if (data.has("danger_strategy")) {
      dangerStrategy = strategyFactory.createInstance(data.getJSONObject("danger_strategy"));
    } else {
      // Valor por defecto: SelectFirst
      JSONObject defaultDanger = new JSONObject();
      defaultDanger.put("type", "first");
      dangerStrategy = strategyFactory.createInstance(defaultDanger);
    }

    return new Sheep(mateStrategy, dangerStrategy, position);
  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("pos", "posicion opcional como array de dos double [x, y]");
    o.put("mate_strategy", "estrategia de apareamiento opcional (JSONObject)");
    o.put("sel_strategy", "estrategia de peligro opcional (JSONObject)");
  }

}
