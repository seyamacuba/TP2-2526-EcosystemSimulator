package simulator.model;

public interface MapInfo extends JSONable {
  int getCols();

  int getRows();

  int getWidth();

  int getHeight();

  int getRegionWidth();

  int getRegionHeight();
}
