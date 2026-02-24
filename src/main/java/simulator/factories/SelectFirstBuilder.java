package simulator.factories;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.strategies.SelectFirst;
import simulator.strategies.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {
  public SelectFirstBuilder() {
    super("first", "First animal selection strategy ");
  }

  @Override
  protected SelectionStrategy createInstance(JSONObject data) {
    return new SelectFirst();
  }
}
