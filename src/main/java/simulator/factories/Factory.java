package simulator.factories;

import org.json.JSONObject;

import java.util.List;

public interface Factory<T> {
  T createInstance(JSONObject info);

  List<JSONObject> getInfo();
}
