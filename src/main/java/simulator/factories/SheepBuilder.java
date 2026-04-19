package simulator.factories;

import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends AnimalBuilder {

  public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("sheep", "Sheep animal", strategyFactory);
  }

  @Override
  protected Animal createInstance(JSONObject data) {
    // 1. POSICIÓN (O es un array exacto [x,y] o es null)
    Vector2D position = parseRandomPos(data);

    // 2. ESTRATEGIA MATE
    SelectionStrategy mateStrategy = parseStrategy(data, "mate_strategy");

    // 3. ESTRATEGIA DANGER
    SelectionStrategy dangerStrategy = parseStrategy(data, "danger_strategy");
    return new Sheep(mateStrategy, dangerStrategy, position);

  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("pos", "posicion opcional como array de dos double [x, y]");
    o.put("mate_strategy", "estrategia de apareamiento opcional (JSONObject)");
    o.put("danger_strategy", "estrategia de peligro opcional (JSONObject)");
  }

}
