package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.factories.Factory;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.Simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

  public class Controller {

    private Simulator sim;
    private Factory<Animal> animalsFactory;
    private Factory<Region> regionsFactory;

    public Controller(Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
      if (animalsFactory == null || regionsFactory == null) {
        throw new IllegalArgumentException("Null factories");
      }
      this.animalsFactory = animalsFactory;
      this.regionsFactory = regionsFactory;
      this.sim = null; // se creará en loadData
    }

    public Simulator getSimulator() {
      if (sim == null) {
        throw new IllegalStateException("Simulator not initialized. Call loadData first.");
      }
      return sim;
    }

    public void loadData(JSONObject data) {
      if (data == null) {
        throw new IllegalArgumentException("Null data");
      }

      int cols   = data.getInt("cols");
      int rows   = data.getInt("rows");
      int width  = data.getInt("width");
      int height = data.getInt("height");

      // Crear nuevo simulador con las factorías
      sim = new Simulator(cols, rows, width, height, animalsFactory, regionsFactory);

      // Configurar regiones
      JSONArray regions = data.getJSONArray("regions");
      for (int i = 0; i < regions.length(); i++) {
        JSONObject r = regions.getJSONObject(i);
        int row = r.getInt("row");
        int col = r.getInt("col");
        JSONObject rData = r.getJSONObject("data");
        sim.setRegion(row, col, rData);
      }

      // Crear animales
      JSONArray animals = data.getJSONArray("animals");
      for (int i = 0; i < animals.length(); i++) {
        JSONObject aData = animals.getJSONObject(i);
        sim.addAnimal(aData);
      }
    }

    public void run(double t, double dt, boolean sv, OutputStream out) throws IOException {
      if (sim == null) {
        throw new IllegalStateException("Simulator not initialized. Call loadData first.");
      }
      if (dt <= 0 || t < 0) {
        throw new IllegalArgumentException("Invalid time or dt");
      }

      JSONObject result = null;
      JSONArray outStates = null;

      if (sv) {
        // estado inicial
        result = new JSONObject();
        result.put("in", sim.asJSON());
        outStates = new JSONArray();
      }

      double elapsed = 0.0;
      while (elapsed < t) {
        sim.advance(dt);
        elapsed += dt;

        if (sv) {
          outStates.put(sim.asJSON());
        }
      }

      if (sv) {
        result.put("out", outStates);
        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        out.write(bytes);
      }
    }
  }
