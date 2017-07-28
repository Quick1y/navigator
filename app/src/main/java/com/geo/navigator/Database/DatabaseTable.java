package com.geo.navigator.Database;

/**
 * Created by nikita on 14.07.17.
 */

public class DatabaseTable {
    public static final String MAPS = "Maps";
    public static final String POINTS = "Points";
    public static final String EDGES = "Edges";

    public class Column{
        public static final String MAPS_ID = "id";
        public static final String MAPS_IMG_PATH = "img_path";
        public static final String MAPS_DESC = "description";
        public static final String MAPS_BUILDING_ID = "building_id";

        public static final String POINTS_ID = "id";
        public static final String POINTS_ID_MAP = "id_maps";
        public static final String POINTS_COORD_X = "coord_x";
        public static final String POINTS_COORD_Y = "coord_y";
        public static final String POINTS_DESC = "description";
        public static final String POINTS_NUM_ON_GRAPH = "num_on_graph";
        public static final String POINTS_VIS_ON_MAP = "visible_on_map";
        public static final String POINTS_META = "meta";

     //   public static final String EDGES_ID = "id";
        public static final String EDGES_ID_A = "id_a";
        public static final String EDGES_ID_B = "id_b";
        public static final String EDGES_WEIGHT = "weight";
        public static final String EDGES_IDMAP = "id_map";
        public static final String EDGES_DESC = "description";

    }

}
