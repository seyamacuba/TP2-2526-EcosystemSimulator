package simulator.factories;

import org.json.JSONObject;

import java.util.*;

public class BuilderBasedFactory<T> implements Factory<T> {
  private Map<String, Builder<T>> builders; //Mapa de builders.
  private List<JSONObject> buildersInfo;

  public BuilderBasedFactory() {
    // Create a HashMap for builders, and a LinkedList buildersInfo
    // …
    this.builders = new HashMap<>();
    this.buildersInfo = new ArrayList<>();
  }

  //FALTA ACABAR LO DE ABAJO MIAU
  public BuilderBasedFactory(List<Builder<T>> builders) {
    this();

    // call addBuilder(b) for each builder b in builder
    // …
  }

  public void addBuilder(Builder<T> b) {
    // add an entry "b.getTypeTag() |−> b" to builders.
    // ...
    // add b.getInfo() to buildersInfo
    // ...
  }

  @Override
  public T createInstance(JSONObject info) { //Devuelveinstancia
    if (info == null) {
      throw new IllegalArgumentException("’info’ cannot be null");
    }

    if(!info.has("type")){
      throw new IllegalArgumentException("No type found");
    }

    String type = info.getString("type"); //Me guarda el tipo del objeto.
    Builder<T> builder = builders.get(type);

    if(builder == null){ //El objeto no se ha podido crear.
      throw new IllegalArgumentException("NO EXISTE EL TIPO" + type);
    }

    try{
      return builder.createInstance(info);
    }catch(Exception e){
      throw new IllegalArgumentException("No se pudo crear la instancia");
    }
  }

  @Override
  public List<JSONObject> getInfo() {
    return Collections.unmodifiableList(buildersInfo);
  }
}
