package simulator.model;

public interface MapInfo extends JSONable, Iterable<MapInfo.RegionData>{
  int getCols();

  int getRows();

  int getWidth();

  int getHeight();

  int getRegionWidth();

  int getRegionHeight();

  public record RegionData(int row, int col, RegionInfo r) {
  }
}
