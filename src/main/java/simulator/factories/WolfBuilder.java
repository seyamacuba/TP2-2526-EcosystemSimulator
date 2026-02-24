package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.*;
import simulator.strategies.SelectFirst;
import simulator.strategies.SelectClosest;
import simulator.strategies.SelectYoungest;
import simulator.strategies.SelectionStrategy;
import simulator.model.Wolf;


public class WolfBuilder extends Builder<Animal>{

  private Factory<SelectionStrategy> strategyFactory;

  public WolfBuilder(Factory<SelectionStrategy> strategyFactory) {
    super("wolf", "Wolf animal");
    this.strategyFactory = strategyFactory;
  }

    @Override
    protected Animal createInstance(JSONObject data) {
      // 1. POSICIÓN (O es un array exacto [x,y] o es null)
      Vector2D position = null;
      if (data.has("pos")) {
        JSONArray posArray = data.getJSONArray("pos");
        position = new Vector2D(posArray.getDouble(0), posArray.getDouble(1));
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

      // 3. ESTRATEGIA HUNT
      SelectionStrategy huntStrategy;
      if (data.has("hunt_strategy")) {
        huntStrategy = strategyFactory.createInstance(data.getJSONObject("hunt_strategy"));
      } else {
        // Valor por defecto: SelectFirst
        JSONObject defaultHunt = new JSONObject();
        defaultHunt.put("type", "first");
        huntStrategy = strategyFactory.createInstance(defaultHunt);
      }

      return new Wolf(mateStrategy, huntStrategy, position);
    }
//Para que tenemos las factorias? esto no tiene sentido
//    private SelectionStrategy createSelectionStrategy(JSONObject strategyData) {
//      if(!strategyData.has("type")){
//        throw new IllegalArgumentException("Must have a type of strategy");
//      }
//
//      String type = strategyData.getString("type");
//
//      switch(type){
//        case "first":
//          return new SelectFirst();
//
//        case "closest":
//          if(!strategyData.has("pos")){
//            throw new IllegalArgumentException("closest type needs a position");
//          }
//          JSONObject pos = strategyData.getJSONObject("pos");
//          double x = pos.getDouble("x");
//          double y = pos.getDouble("y");
//          return new SelectClosest();
//
//        case "youngest":
//          return new SelectYoungest();
//
//        default:
//          throw new IllegalArgumentException("Strategy does not exist: " + type);
//      }
//    }

    @Override
    protected  void fillInData(JSONObject o) {
      o.put("pos", "posicion JSONObject con coordenadas x e y");
      o.put("mate_strategy", "mating strategy (JSONObject)");
      o.put("hunt_strategy", "hunting strategy (JSONObject)");
    }

}
