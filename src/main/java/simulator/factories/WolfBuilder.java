package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.*;
import simulator.model.Wolf;


public class WolfBuilder extends Builder<Animal>{

  private Factory<SelectionStrategy> strategyFactory;

  public WolfBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("wolf", "Wolf animal");
    this.strategyFactory = strategyFactory;
  }

  private Vector2D createRandomPosition(JSONObject posData){
    if(!posData.has("min") || !posData.has("max")){
      throw new IllegalArgumentException("Maximum and minimun needed");
    }

    JSONArray xRange = posData.getJSONArray("x_range");
    JSONArray yRange = posData.getJSONArray("y_range");

    if(xRange.length() != 2 || yRange.length() != 2){
      throw new IllegalArgumentException("Range must have two elems");
    }

    double xMin = xRange.getDouble(0);
    double xMax = xRange.getDouble(1);
    double yMin = yRange.getDouble(0);
    double yMax = yRange.getDouble(1);

    if(xMin > xMax || yMin > yMax){
      throw new IllegalArgumentException("Incorrect range");
    }

    double x = xMin + Math.random() * (xMax - xMin); //Genera las coordenadas aleatorias en el rango.
    double y = yMin + Math.random() * (yMax - yMin);

    return new Vector2D(x, y);
  }

    @Override
    protected Animal createInstance(JSONObject data) {
      if(!data.has("pos")){
        throw new IllegalArgumentException("JSON must contain position");
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

        SelectionStrategy huntStrategy;
        if(data.has("hunt_strategy")){
          JSONObject dangerData = data.getJSONObject("hunt_strategy");
          huntStrategy = createSelectionStrategy(dangerData);
        }else{
          huntStrategy = new SelectFirst();
        }

        return new Wolf(mateStrategy,huntStrategy,posicion);

      } catch (Exception e){
        throw new IllegalArgumentException("Error creating object");
      }
    }

    private SelectionStrategy createSelectionStrategy(JSONObject strategyData) {
      if(!strategyData.has("type")){
        throw new IllegalArgumentException("Must have a type of strategy");
      }

      String type = strategyData.getString("type");

      switch(type){
        case "first":
          return new SelectFirst();

        case "closest":
          if(!strategyData.has("pos")){
            throw new IllegalArgumentException("closest type needs a position");
          }
          JSONObject pos = strategyData.getJSONObject("pos");
          double x = pos.getDouble("x");
          double y = pos.getDouble("y");
          return new SelectClosest();

        case "youngest":
          return new SelectYoungest();

        default:
          throw new IllegalArgumentException("Strategy does not exist: " + type);
      }
    }

    @Override
    protected  void fillInData(JSONObject o) {
      o.put("pos", "posicion JSONObject con coordenadas x e y");
      o.put("mate_strategy", "mating strategy (JSONObject)");
      o.put("hunt_strategy", "hunting strategy (JSONObject)");
    }

}
