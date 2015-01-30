package tp_sig;

import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.LineString;
import geoexplorer.gui.MapPanel;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.awt.*;
import java.sql.*;

public class Request {


    private static PreparedStatement stmt;
    private static Statement st;
    private static ResultSet res;

    /**
     * Question 8
     * Recuperer les resultats d'une requete simple
     * @param tags
     * @param value
     * @throws SQLException
     */
    public static void question8(Connection connection, String tags, String value) throws SQLException {
        stmt = connection.prepareStatement("->'" + tags + "' like '%" +value + "%';");
        res = stmt.executeQuery();

        while (res.next()) {
            System.out.println("colonne 1 = " + res.getString(1));
        }
    }

    /**
     * Qestion 9
     * Affichant tous les noms et coordonnées géographiques des points dont le nom ressemble à (au sens du LIKE SQL) l'argument
     * @param connection
     * @param name
     */
    public static void question9(Connection connection, String name) throws SQLException {
        getAllNamesAndCoord(connection, name);
    }

    /**
     * Qestion 10a
     * L'ensemble des routes autour de Grenoble
     * @param connection
     */
    public static void question10a(Connection connection) throws SQLException{
        getGrenobleRoutes(connection);
    }

    /**
     * Qestion 10b
     * @param connection
     * @throws SQLException
     */
    public static void question10b(Connection connection) throws SQLException{
        getGrenobleBuilding(connection);
    }

    public static void question10c(Connection connection) throws SQLException{
        getGrenobleAdministratif(connection);
    }
    /**
     * Question 11a : Les quartiers de grenoble
     * @param connection
     */
/*    public static void question11a(Connection connection){
        try {
            getGrenobleSchool(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Question 11b : Tracer une carte des nuisances sonores
     * @param connection
     */
    public static void question11b(Connection connection){
        try {
            getNoiseArea(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recuperer les noms et coordonnées d'un nom particulier - Méthode 1
     * @param name
     * @throws SQLException
     */
    public void getAllNamesAndCoordBis(Connection connection, String name) throws SQLException{
        stmt = connection.prepareStatement("SELECT ST_X(geom), ST_Y(geom) FROM nodes where tags->'name' like '"+ name + "';");
        res = stmt.executeQuery();

        while (res.next()) {
            System.out.print("Longitude = " + res.getString(1));
            System.out.println("Latitude = " + res.getString(2));
        }
    }

    /**
     * Recuperer les noms et coordonnées d'un nom particulier - Méthode 2
     * @param name
     * @throws SQLException
     */
    public static void getAllNamesAndCoord(Connection connection, String name) throws SQLException {
        stmt = connection.prepareStatement("SELECT geom,tags->'name' as name FROM nodes where tags->'name' like '"+ name + "';");
        res = stmt.executeQuery();

        Geometry g;
        Point p;

        while (res.next()) {
            g = ((PGgeometry) res.getObject(1)).getGeometry();
            p = g.getPoint(0);

            System.out.print("Nom = " + res.getString("name"));
            System.out.print("  Longitude = " + p.getX());
            System.out.println("    Latitude = " + p.getY());
        }
    }

    /**
     * Recuperer les routes de Grenoble
     * @param connection
     * @return
     * @throws SQLException
     */
    public static void getGrenobleRoutes(Connection connection) throws SQLException {

        MapPanel  map = new MapPanel(4.75,44.01,0.1);
        GeoMainFrame geo = new GeoMainFrame("Map", map);
        st = connection.createStatement();
        //prepareStatement("SELECT linestring, tags->'highway' as highway FROM ways WHERE tags?'highway' LIMIT 1;");
        //result limited to 3 right now
        res = st.executeQuery("SELECT linestring, tags->'highway' as highway FROM ways WHERE tags?'highway' AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));");

        paintLine(res, map, Color.BLACK);
    }

    /**
     * Recuperer les batiments de Grenoble
     * @param connection
     * @throws SQLException
     */
    public static void getGrenobleBuilding(Connection connection) throws SQLException {
        MapPanel map = new MapPanel(4.75,44.01,0.1);
        GeoMainFrame geo = new GeoMainFrame("Map", map);

        st = connection.createStatement();
        //prepareStatement("SELECT linestring, tags->'highway' as highway FROM ways WHERE tags?'highway' LIMIT 1;");
        //result limited to 3 right now
        res = st.executeQuery("SELECT linestring, tags?'building' as building FROM ways WHERE tags?'building' AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));");

        paintPolygon(res, map, Color.gray);
    }

    /**
     * Recuperer les zone administrative
     * @param connection
     * @throws SQLException
     */
    public static void getGrenobleAdministratif(Connection connection) throws SQLException {
        MapPanel map = new MapPanel(4.75,44.01,0.1);
        GeoMainFrame geo = new GeoMainFrame("Map", map);

        st = connection.createStatement();
        //prepareStatement("SELECT linestring, tags->'highway' as highway FROM ways WHERE tags?'highway' LIMIT 1;");
        //result limited to 3 right now
        res = st.executeQuery("SELECT linestring FROM ways WHERE tags->'boundary'='administrative' AND tags->'admin_level' in ('0','1','2','3','4','5','6','7');");

        paintLine(res, map, Color.GRAY);
    }

    /**
     * afficher les zone nuisances sonores
     * @param connection
     * @throws SQLException
     */
    public static void getNoiseArea(Connection connection) throws SQLException {

        MapPanel map = new MapPanel(4.75,44.01,0.1);
        GeoMainFrame geo = new GeoMainFrame("Map", map);

        float proximity = 0.005f;
        String allAeroWays= "SELECT ST_Buffer(geometry(ways.linestring)," + proximity + ") FROM ways WHERE tags?'aeroway' AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";

        proximity = 0.001f;
        String allRailways= "SELECT ways.linestring FROM ways WHERE tags?'railway'AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
        String rails = "SELECT ST_Buffer(geometry(ways.linestring)," + proximity + ") FROM ways WHERE tags?'railway' AND ways.tags->'railway'='tram'AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
        String trams = "SELECT ST_Buffer(geometry(ways.linestring)," + proximity + ") FROM ways WHERE tags?'railway' AND ways.tags->'railway'='rail'AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
        String subways = "SELECT ST_Buffer(geometry(ways.linestring)," + proximity + ") FROM ways WHERE tags?'railway' AND ways.tags->'railway'='subway'AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";
        String constructions = "SELECT ST_Buffer(geometry(ways.linestring)," + proximity + ") FROM ways WHERE tags?'railway' AND ways.tags->'railway'='construction'AND ST_Intersects(ways.bbox,ST_SetSRID(ST_MakeBox2D(ST_Point(5.7,45.1),ST_Point(5.8,45.2)),4326));";


        st = connection.createStatement();
        res = st.executeQuery(allAeroWays);
        paintPolygon(res, map, Color.gray);

        st = connection.createStatement();
        res = st.executeQuery(rails);
        paintLine(res, map, Color.blue);

        st = connection.createStatement();
        res = st.executeQuery(allRailways);
        paintLine(res, map, Color.black);

        st = connection.createStatement();
        res = st.executeQuery(trams);
        paintLine(res, map, Color.green);

        st = connection.createStatement();
        res = st.executeQuery(subways);
        paintLine(res, map, Color.cyan);

        st = connection.createStatement();
        res = st.executeQuery(constructions);
        paintLine(res, map, Color.orange);


    }

    public static void paintPolygon(ResultSet res, MapPanel map, Color color) throws SQLException{
        while (res.next()) {
            Geometry g = ((PGgeometry) res.getObject(1)).getGeometry();
            Point p = null;
            geoexplorer.gui.Point drawedPoint = null;
            geoexplorer.gui.Polygon drawedPolygon = new geoexplorer.gui.Polygon(color, color);

            for (int i = 0; i < g.numPoints(); i++){
                p=g.getPoint(i);
                drawedPoint = new geoexplorer.gui.Point(p.getX(),p.getY(), color);
                drawedPolygon.addPoint(drawedPoint);
            }
            map.addPrimitive(drawedPolygon);
        }
        map.autoAdjust();
    }

    public static void paintLine(ResultSet res, MapPanel map, Color color) throws SQLException{
        while (res.next()) {
            Geometry g = ((PGgeometry) res.getObject(1)).getGeometry();
            Point p = null;
            geoexplorer.gui.Point drawedPoint = null;
            LineString drawedLine = new LineString(color);

            for (int i = 0; i < g.numPoints(); i++){
                p=g.getPoint(i);
                drawedPoint = new geoexplorer.gui.Point(p.getX(),p.getY(), color);
                drawedLine.addPoint(drawedPoint);
            }
            map.addPrimitive(drawedLine);
        }
        map.autoAdjust();
    }
}