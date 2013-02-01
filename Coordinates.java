/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package _curvestitch;

/**
 *
 * @author kenschiller
 */
public class Coordinates {
    public int x;
    public int y;
    Coordinates(int _x, int _y) {
        x = _x;
        y = _y;
    }
    public String toString() {
        return "(" + x + ", " + y;
    }
    public static double distance(Coordinates c1, Coordinates c2) {
        double dx = c2.x - c1.x, dy = c2.y - c1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public static double angleBetween(Coordinates c1, Coordinates c2) {
        double dx = c2.x - c1.x, dy = c2.y - c1.y;
        double slope = dy / dx;
        double angle = Math.atan(slope);
        if(dx < 0) angle = angle + Math.PI;
        if(angle < 0) angle += Math.PI * 2;
        return angle;
    }
}
