/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package _curvestitch;

/**
 *
 * @author kenschiller
 */
public class GraphingData {
    public int x;
    public int y;
    public int w;
    public int h;
    public boolean [] [] data;
    public GraphingData(int _x, int _y, int _w, int _h) {
        x = _x;
        y = _y;
        w = _w;
        h = _h;
        data = new boolean [w] [h];
    }
    public void on(int i, int j) {
        data[i][j] = true;
    }
}
