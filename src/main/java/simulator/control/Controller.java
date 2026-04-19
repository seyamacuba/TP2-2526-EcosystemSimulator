package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Controller {

  private final Simulator sim;

  public Controller(Simulator sim) {
    if (sim == null) throw new IllegalArgumentException("Simulator cannot be null");
    this.sim = sim;
  }

  public void loadData(JSONObject data) {
    if (data == null) {
      throw new IllegalArgumentException("Null data");
    }
    applyRegions(data);

    if (data.has("animals")) {
      JSONArray animals = data.getJSONArray("animals");
      for (int i = 0; i < animals.length(); i++) {
        JSONObject entry = animals.getJSONObject(i);
        int amount = entry.getInt("amount");
        JSONObject spec = entry.getJSONObject("spec");

        for (int k = 0; k < amount; k++) {
          sim.addAnimal(spec);
        }
      }
    }
  }

  private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
    List<ObjInfo> ol = new ArrayList<>(animals.size());
    for (AnimalInfo a : animals) {
      ol.add(new ObjInfo(
        a.getGeneticCode(),
        (int) a.getPosition().getX(),
        (int) a.getPosition().getY(),
        (int) Math.round(a.getAge()) + 2
      ));
    }
    return ol;
  }

  private void applyRegions(JSONObject rs) {
    if (!rs.has("regions")) return;

    JSONArray regions = rs.getJSONArray("regions");
    for (int i = 0; i < regions.length(); i++) {
      JSONObject entry = regions.getJSONObject(i);

      JSONArray rowRange = entry.getJSONArray("row");
      JSONArray colRange = entry.getJSONArray("col");
      JSONObject spec = entry.getJSONObject("spec");

      int rowFrom = rowRange.getInt(0);
      int rowTo = rowRange.getInt(1);
      int colFrom = colRange.getInt(0);
      int colTo = colRange.getInt(1);

      for (int r = rowFrom; r <= rowTo; r++) {
        for (int c = colFrom; c <= colTo; c++) {
          sim.setRegion(r, c, spec);
        }
      }
    }
  }

  public void run(double t, double dt, boolean sv, OutputStream out) {
    if (dt <= 0 || t < 0) {
      throw new IllegalArgumentException("Invalid time or dt");
    }

    PrintStream p = new PrintStream(out);

    // Estado inicial — siempre se captura antes del bucle
    JSONObject initState = sim.asJSON();

    // Inicializar el visor si sv=true
    SimpleObjectViewer view = null;
    if (sv) {
      MapInfo m = sim.getMapInfo();
      view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());
      view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
    }

    // Bucle de simulación — condición uniforme
    while (sim.getTime() <= t) {
      sim.advance(dt);
      if (sv) {
        view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
      }
    }

    // Cerrar el visor
    if (sv) {
      view.close();
    }

    // Salida JSON — siempre en el formato { "in": ..., "out": ... }
    JSONObject finalState = sim.asJSON();
    JSONObject result = new JSONObject();
    result.put("in", initState);
    result.put("out", finalState);
    p.println(result);
  }

  //la GUI no toca el simulador directamente, pero si controller
  public void reset(int cols, int rows, int width, int height) {
    sim.reset(cols, rows, width, height);
  }

  public void advance(double dt) {
    sim.advance(dt);
  }

  public void addObserver(EcoSysObserver o) {
    sim.addObserver(o);
  }

  public void removeObserver(EcoSysObserver o) {
    sim.removeObserver(o);
  }

  public void setRegions(JSONObject rs) { //como addData
    if (rs == null) {
      throw new IllegalArgumentException("Null regions");
    }

    applyRegions(rs);
  }
  //Estructura
  //{
  // "regions":[
  //   {
  //     "row":[0,6],
  //     "col":[0,3],
  //     "spec":{...}
  //   }
  // ]
  //}
}
