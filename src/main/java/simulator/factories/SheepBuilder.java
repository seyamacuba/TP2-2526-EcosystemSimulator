package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.factories.Builder;
import simulator.factories.Factory;
import simulator.misc.Vector2D;
import simulator.model.*;

public class SheepBuilder extends Builder<Animal> {
  private Factory<SelectionStrategy> strategyFactory;
  public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("sheep", "Sheep animal");
    this.strategyFactory = strategyFactory;
  }

  private Vector2D createRandomPosition(JSONObject posData){
    if(!posData.has("min") || !posData.has("max")){
      throw new IllegalArgumentException("Se necesitan los minimos y maximos");
    }

    JSONArray xRange = posData.getJSONArray("x_range");
    JSONArray yRange = posData.getJSONArray("y_range");

    if(xRange.length() != 2 || yRange.length() != 2){
      throw new IllegalArgumentException("Los rangos deben tener dos elementos");
    }

    double xMin = xRange.getDouble(0);
    double xMax = xRange.getDouble(1);
    double yMin = yRange.getDouble(0);
    double yMax = yRange.getDouble(1);

    if(xMin > xMax || yMin > yMax){
      throw new IllegalArgumentException("Rango incorrecto");
    }

    double x = xMin + Math.random() * (xMax - xMin); //Genera las coordenadas aleatorias en el rango.
    double y = yMin + Math.random() * (yMax - yMin);

    return new Vector2D(x, y);
  }
  @Override
  protected Animal createInstance(JSONObject data) {
    if(!data.has("pos")){
      throw new IllegalArgumentException("El JSON debe contener pos");
    }

    try{

      Vector2D posicion = null;
      if(data.has("pos")){
        JSONObject posData = data.getJSONObject("pos");
        posicion = createRandomPosition(posData);
      }

      SelectionStrategy mateStrategy; //Hace mateStrategy
      if(data.has("mate_strategy")){
        JSONObject dangerData = data.getJSONObject("mate_strategy");
        mateStrategy = createSelectionStrategy(dangerData);
      }else{
        mateStrategy = new SelectFirst(); //Por defecto lo he puesto
      }

      SelectionStrategy dangerStrategy;
      if(data.has("danger_strategy")){
        JSONObject dangerData = data.getJSONObject("danger_strategy");
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
          return new SelectClosest();

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
