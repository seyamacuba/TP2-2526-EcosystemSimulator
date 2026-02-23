package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

  public class Controller {

    private Simulator sim;

    public Controller(Simulator sim) {
      if (sim == null) throw new IllegalArgumentException("Simulator cannot be null");
      this.sim = sim;
    }

    public void loadData(JSONObject data) {
      if (data == null) {
        throw new IllegalArgumentException("Null data");
      }
      // PRIMERO regiones (si las tiene)
      if (data.has("regions")) {
        JSONArray regions = data.getJSONArray("regions");
        for (int i = 0; i < regions.length(); i++) {
          JSONObject entry = regions.getJSONObject(i);
          JSONArray rowRange = entry.getJSONArray("row");
          JSONArray colRange = entry.getJSONArray("col");
          JSONObject spec = entry.getJSONObject("spec");
          int rowFrom = rowRange.getInt(0), rowTo = rowRange.getInt(1);
          int colFrom = colRange.getInt(0), colTo = colRange.getInt(1);
          for (int r = rowFrom; r <= rowTo; r++) {
            for (int c = colFrom; c <= colTo; c++) {
              sim.setRegion(r, c, spec);
            }
          }
        }
      }

    // DESPUÉS animales
    JSONArray animals = data.getJSONArray("animals");
        for (int i = 0; i < animals.length(); i++) {
      JSONObject entry = animals.getJSONObject(i);
      int amount       = entry.getInt("amount");
      JSONObject spec  = entry.getJSONObject("spec");
      for (int k = 0; k < amount; k++) {
        sim.addAnimal(spec);
      }
    }
  }

    public void run(double t, double dt, boolean sv, OutputStream out) throws IOException {

      if (dt <= 0 || t < 0) {
        throw new IllegalArgumentException("Invalid time or dt");
      }

      JSONObject initState = sim.asJSON();
      while (sim.getTime() <= t) {
        sim.advance(dt);
        // Estado final
        JSONObject finalState = sim.asJSON();
        // Escribir resultado
        JSONObject result = new JSONObject();
        result.put("in", initState);
        result.put("out", finalState);
        PrintStream p = new PrintStream(out);
        p.println(result.toString());
  }
