package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.*;
import simulator.model.SelectionStrategy;

public class SheepBuilder extends Builder<Animal> {
  private Factory<SelectionStrategy> strategyFactory;
  public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("sheep", "Sheep animal");
    this.strategyFactory = strategyFactory;
  }

//  private Vector2D createRandomPosition(JSONObject posData){
//    if(!posData.has("min") || !posData.has("max")){
//      throw new IllegalArgumentException("Se necesitan los minimos y maximos");
//    }
//
//    JSONArray xRange = posData.getJSONArray("x_range");
//    JSONArray yRange = posData.getJSONArray("y_range");
//
//    if(xRange.length() != 2 || yRange.length() != 2){
//      throw new IllegalArgumentException("Los rangos deben tener dos elementos");
//    }
//
//    double xMin = xRange.getDouble(0);
//    double xMax = xRange.getDouble(1);
//    double yMin = yRange.getDouble(0);
//    double yMax = yRange.getDouble(1);
//
//    if(xMin > xMax || yMin > yMax){
//      throw new IllegalArgumentException("Rango incorrecto");
//    }
//
//    double x = xMin + Math.random() * (xMax - xMin); //Genera las coordenadas aleatorias en el rango.
//    double y = yMin + Math.random() * (yMax - yMin);
//
//    return new Vector2D(x, y);
//  }
  @Override
  protected Animal createInstance(JSONObject data) {
    // 1. POSICIÓN (O es un array exacto [x,y] o es null)
    Vector2D position = null;
    if (data.has("pos")) {
      JSONObject pos = data.getJSONObject("pos");

      JSONArray xRange = pos.getJSONArray("x_range");
      JSONArray yRange = pos.getJSONArray("y_range");

      double xMin = xRange.getDouble(0);
      double xMax = xRange.getDouble(1);
      double yMin = yRange.getDouble(0);
      double yMax = yRange.getDouble(1);

      // Generar posición aleatoria dentro del rango
      double x = xMin + Math.random() * (xMax - xMin);
      double y = yMin + Math.random() * (yMax - yMin);

      position = new Vector2D(x, y);
    }

    // 2. ESTRATEGIA MATE
    SelectionStrategy mateStrategy;
    if (data.has("mate_strategy")) {
      mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
    } else {
      // Valor por defecto: SelectFirst
      JSONObject defaultMate = new JSONObject();
      defaultMate.put("type", "first");
      mateStrategy = strategyFactory.createInstance(defaultMate);
    }


    // 3. ESTRATEGIA DANGER
    SelectionStrategy dangerStrategy;
    if (data.has("danger_strategy")) {
      dangerStrategy = strategyFactory.createInstance(data.getJSONObject("danger_strategy"));
    } else {
      // Valor por defecto: SelectFirst
      JSONObject defaultDanger = new JSONObject();
      defaultDanger.put("type", "first");
      dangerStrategy = strategyFactory.createInstance(defaultDanger);
    }

      return new Sheep(mateStrategy,dangerStrategy,position);

    }

  @Override
  protected  void fillInData(JSONObject o) {
    o.put("pos", "posicion opcional como array de dos double [x, y]");
    o.put("mate_strategy", "estrategia de apareamiento opcional (JSONObject)");
    o.put("sel_strategy", "estrategia de peligro opcional (JSONObject)");
  }

}
