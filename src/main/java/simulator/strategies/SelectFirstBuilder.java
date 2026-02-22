package simulator.strategies;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {
  public SelectFirstBuilder() {
    super("first", " ");
  }

  @Override
  protected void fillInData(JSONObject o) {
  }

  @Override
  protected SelectionStrategy createInstance(JSONObject data) {
    return new SelectFirst();
  }
}
