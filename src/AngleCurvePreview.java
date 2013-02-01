/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package _curvestitch;

import java.awt.*;

/**
 *
 * @author kenschiller
 */
public class AngleCurvePreview extends AngleCurve {
    public AngleCurvePreview(Color color) {
        super(color);
    }
    public void show(Graphics g) {
        if(vertexCount_ == 1) {
            g.setColor(color_);
            g.drawRect(vertices_[0].x - 2, vertices_[0].y - 2, 4, 4);
        }
        else if(vertexCount_ > 1) {
            super.show(g);
            g.setColor(CurvePanel.brightenByFactor(color_, 1.5f));
            Coordinates a = vertices_[vertexCount_ - 2];
            Coordinates b = vertices_[vertexCount_ - 1];
            int r = (int) (Coordinates.distance(a, b) + 0.5);
            g.drawOval(a.x - r, a.y - r, r * 2, r * 2);
        }
    }
}
