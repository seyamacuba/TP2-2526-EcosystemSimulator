package simulator.factories;

import org.json.JSONObject;

import java.util.*;

public class BuilderBasedFactory<T> implements Factory<T> {
  private final Map<String, Builder<T>> builders; //Mapa de builders.
  private final List<JSONObject> buildersInfo;

  public BuilderBasedFactory() {
    // Create a HashMap for builders, and a LinkedList buildersInfo
    // …
    this.builders = new HashMap<>();
    this.buildersInfo = new LinkedList<>();
  }

  //FALTA ACABAR LO DE ABAJO MIAU
  public BuilderBasedFactory(List<Builder<T>> builders) {
    this();
    if (builders == null) {
      throw new IllegalArgumentException("Builders cannot be null");
    }
    // call addBuilder(b) for each builder b in builder
    for (Builder<T> b : builders) {
      addBuilder(b);
    }
  }

  public void addBuilder(Builder<T> b) {
    // add an entry "b.getTypeTag() |−> b" to builders.
    if (b == null) {
      throw new IllegalArgumentException("Builder cannot be null");
    }
    //add an entry "b.getTypeTag()" to builders
    String tag = b.getTypeTag();
    if (builders.containsKey(tag)) {
      throw new IllegalArgumentException("Builder for type '" + tag + "' already exists");
    }
    builders.put(tag, b);
    // add b.getInfo() to buildersInfo
    buildersInfo.add(b.getInfo());

  }

  @Override
  public T createInstance(JSONObject info) { //Devuelveinstancia
    if (info == null) {
      throw new IllegalArgumentException("’info’ cannot be null");
    }

    if (!info.has("type")) {
      throw new IllegalArgumentException("No type found");
    }

    String type = info.getString("type"); //Me guarda el tipo del objeto.
    Builder<T> builder = builders.get(type);

    if (builder == null) { //El objeto no se ha podido crear.
      throw new IllegalArgumentException("NO EXISTE EL TIPO" + type);
    }

    JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();
    T instance = builder.createInstance(data);

    if (instance == null) {
      throw new IllegalArgumentException("Builder returned null for info: " + info);
    }

    return instance;
  }

  @Override
  public List<JSONObject> getInfo() {
    return Collections.unmodifiableList(buildersInfo);
  }
}
