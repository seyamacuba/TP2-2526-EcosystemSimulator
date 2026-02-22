package simulator.strategies;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

  public SelectYoungestBuilder() {
    super("youngest", "Youngest animal selection strategy");
  }

  @Override
  protected SelectionStrategy createInstance(JSONObject data) {
    return new SelectYoungest();
  }

  @Override
  protected void fillInData(JSONObject o) {
  }
}
