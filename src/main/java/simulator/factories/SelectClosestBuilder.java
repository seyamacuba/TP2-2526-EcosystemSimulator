package simulator.factories;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.misc.Vector2D;
import simulator.strategies.SelectClosest;
import simulator.strategies.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {
  public SelectClosestBuilder() {
    super("closest", "Closest animal selection stategy");
  }

  @Override
  protected SelectionStrategy createInstance(JSONObject data) {
      return new SelectClosest();
  }
}
