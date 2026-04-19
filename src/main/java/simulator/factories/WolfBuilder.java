package simulator.factories;

import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;


public class WolfBuilder extends AnimalBuilder {

  public WolfBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("wolf", "Wolf animal", strategyFactory);
  }

  @Override
  protected Animal createInstance(JSONObject data) {
    // 1. POSICIÓN (O es un array exacto [x,y] o es null)
    Vector2D position = parseRandomPos(data);

    // 2. ESTRATEGIA MATE
    SelectionStrategy mateStrategy = parseStrategy(data, "mate_strategy");

    // 3. ESTRATEGIA HUNT
    SelectionStrategy huntStrategy = parseStrategy(data, "hunt_strategy");

    return new Wolf(mateStrategy, huntStrategy, position);
  }

  @Override
  protected void fillInData(JSONObject o) {
    o.put("pos", "posicion JSONObject con coordenadas x e y");
    o.put("mate_strategy", "mating strategy (JSONObject)");
    o.put("hunt_strategy", "hunting strategy (JSONObject)");
  }

}
