package simulator.model;

import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {
  public SheepBuilder() {
    super("sheep", "Sheep animal");
  }

  @Override
  protected Animal createInstance(JSONObject data) {
    if(!data.has("pos")){
      throw new IllegalArgumentException("El JSON debe contener pos");
    }

    try{
      JSONObject pos = data.getJSONObject("pos");

      if(!pos.has("x") || !pos.has("y")){
        throw new IllegalArgumentException("El JSON debe contener x e y");
      }

      double x = pos.getDouble("x");
      double y = pos.getDouble("y");
      Vector2D posicion = new Vector2D(x,y); //Obtengo la posición.

      SelectionStrategy mateStrategy;
      if(data.has("mate_strategy")){
        JSONObject dangerData = data.getJSONObject("mate_strategy");
        mateStrategy = createSelectionStrategy(dangerData);
      }else{
        mateStrategy = new SelectFirst(); //Por defecto lo he puesto
      }

      SelectionStrategy dangerStrategy;
      if(data.has("sel_strategy")){
        JSONObject dangerData = data.getJSONObject("sel_strategy");
        dangerStrategy = createSelectionStrategy(dangerData);
      }else{
        dangerStrategy = new SelectFirst();
      }

      return new Sheep(mateStrategy,dangerStrategy,posicion);

    } catch (Exception e){
      throw new IllegalArgumentException("Error al crear el objeto");
    }
  }

  private SelectionStrategy createSelectionStrategy(JSONObject strategyData) {
    if(!strategyData.has("type")){
      throw new IllegalArgumentException("Debe tener un tipo de estrategia");
    }

    String type = strategyData.getString("type");

    switch(type){
      case "first":
        return new SelectFirst();

        case "closest":
          if(!strategyData.has("pos")){
            throw new IllegalArgumentException("El tipo closest necesita una posicion");
          }
          JSONObject pos = strategyData.getJSONObject("pos");
          double x = pos.getDouble("x");
          double y = pos.getDouble("y");
          return new SelectClosest(new Vector2D(x,y));

          case "youngest":
            return new SelectYoungest();

            default:
              throw new IllegalArgumentException("Esa estrategia no existe" + type);
    }
  }

  @Override
  protected  void fillInData(JSONObject o) {
    o.put("pos", "posicion JSONObject con coordenadas x e y");
    o.put("mate_strategy", "mating strategy (JSONObject)");
    o.put("sel_strategy", "selection strategy (JSONObject)");
  }

}
