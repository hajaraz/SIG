package tp_sig;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import database.Utils;
import geoexplorer.gui.CoordinateConverter;
import geoexplorer.gui.GeoMainFrame;
import geoexplorer.gui.LineString;
import geoexplorer.gui.MapPanel;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class Request {

    private Connection connection = Utils.getConnection();
    private PreparedStatement stmt;
    private Statement st;
    private ResultSet res;

    private MapPanel map = new MapPanel(100,100,8000);
    private GeoMainFrame geo = new GeoMainFrame("Map", map);

    /**
     * Recuperer les resultats d'une requete simple
     * @param tags
     * @param value
     * @throws SQLException
     */
    public void getSimpleRequest(String tags, String value) throws SQLException {
        stmt = connection.prepareStatement("SELECT tags FROM ways WHERE tags->'" + tags + "' like '%" +value + "%';");
        res = stmt.executeQuery();

        while (res.next()) {
            System.out.println("colonne 1 = " + res.getString(1));
        }
    }

    /**
     * Recuperer les noms et coordonnées d'un nom particulier - Méthode 1
     * @param name
     * @throws SQLException
     */
    public void getAllNamesAndCoordBis(String name) throws SQLException{
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
    public ArrayList<Point> getAllNamesAndCoord(String name) throws SQLException {
        stmt = connection.prepareStatement("SELECT geom,tags->'name' as name FROM nodes where tags->'name' like '"+ name + "';");
        res = stmt.executeQuery();

        ArrayList<Point> listPt = new ArrayList<Point>();
        Geometry g;
        Point p;
        CoordinateConverter c;

        while (res.next()) {
            g = ((PGgeometry) res.getObject(1)).getGeometry();
            p = g.getPoint(0);
            listPt.add(p);
            //c = new CoordinateConverter(800, 800, p.getX(), p.getY(), 800);

            System.out.print("Nom = " + res.getString("name"));
            System.out.print("  Longitude = " + p.getX());
            System.out.println("    Latitude = " + p.getY());
        }
        return listPt;
    }

    public ArrayList<Point> getGrenobleRoutes() throws SQLException {

        st = connection.createStatement();
        //prepareStatement("SELECT linestring, tags->'highway' as highway FROM ways WHERE tags?'highway' LIMIT 1;");
        //result limited to 3 right now
        res = st.executeQuery("SELECT linestring, tags->'highway' as highway FROM ways WHERE tags?'highway' LIMIT 3;");

        while (res.next()) {
            Geometry g = ((PGgeometry) res.getObject(1)).getGeometry();
            Point p = null;
            geoexplorer.gui.Point drawedPoint = null;
            LineString drawedLineString = new LineString();

            for (int i = 0; i < g.numPoints(); i++){
                p=g.getPoint(i);
                if(p.getX() < 5.8 && p.getX() > 5.7){
                    if(p.getY() < 45.2 && p.getY() > 45.1){
                        drawedPoint = new geoexplorer.gui.Point(p.getX(),p.getY(), Color.darkGray);
                        map.addPrimitive(drawedPoint);
                        drawedLineString.addPoint(drawedPoint);
                    }
                }
                //Print all the points
                System.out.println("Nom = " + res.getString("highway"));
                System.out.println("\t\tLongitude = " + p.getX());
                System.out.println("\t\tLatitude = " + p.getY());
            }
        }
        return null;
    }

    /**
     * Affiche sur la map une liste de points
     * @param listPoints
     */
    public void displayResultsMap(ArrayList<Point> listPoints){
        Point p;

        for(int i = 0; i < listPoints.size();i++){
            p = listPoints.get(i);
            map.addPrimitive(new geoexplorer.gui.Point(p.getX(), p.getY(), Color.red));
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("-----DEBUT-----");
            Request req = new Request();
            ArrayList<Point> points;
            //req.getSimpleRequest("amenity","university");

            if (args.length > 0) {
                points = req.getAllNamesAndCoord(args[0]);
                req.displayResultsMap(points);
            }else{
                points = req.getAllNamesAndCoord("Dom__ne _niversit%");
                req.displayResultsMap(points);
            }

            req.map.autoAdjust();

            System.out.println("-----FIN-----");
        } catch (SQLException ex) {
            System.out.println("Pb execution requete");
        }

    }

}