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
public class AngleCurve extends Curve {
    private boolean frameless_ = false;
    private int skip_ = 0;
    public AngleCurve(Color color) {
        super(3, color);
    }
    /*public AngleCurve(Coordinates c, Color color) {
        super(3, color);
        addVertex(c);
    }*/
    /*public boolean lineContainsPoint(Coordinates a, Coordinates b, Coordinates c, double tolerance) {
        if((c.x > a.x && c.x > b.x)
                ||(c.x < a.x && c.x < b.x)
                ||(c.y > a.y && c.y > b.y)
                ||(c.y < a.y && c.y < b.y))
            return false;
        double slope = (double) (b.y - a.y) / (b.x - a.x);
        double predicted = a.y + (c.x - a.x) * slope;
        if(Math.abs(predicted - c.y) <= tolerance) {
            return true;
        }
        else {
            return false;
        }
    }*/
    public boolean lineContainsPoint(Coordinates a, Coordinates b, Coordinates c, double tolerance) {
        double m = (double) (b.y - a.y) / (b.x - a.x);
        double n = -1.0 / m; //slope of normal
        //collision line: y - a.y = m*(x - a.x);
        //normal line through c: y - c.y = n*(x - c.x)
        //intersection at...
            //m*(x - a.x) + a.y = n*(x - c.x) + c.y
            //m*x - m*a.x + a.y = n*x - n*c.x + c.y
            //x*(m - n) = m*a.x - n*c.x + c.y - a.y
            //x = (m*a.x - n*c.x + c.y - a.y) / (m - n)
        double x, y;
        if(m != 0.0 && m != 1.0/0.0 && m != -1.0/0.0) {
            x = (m*a.x - n*c.x + c.y - a.y) / (m - n);
            y = m*(x - a.x) + a.y;
        }
        else if(m == 0.0) {
            x = c.x;
            y = a.y;
        }
        else {
            x = a.x;
            y = c.y;
        }
        if(!((x < a.x && x < b.x) || (x > a.x && x > b.x)
                || (y < a.y && y < b.y) || (y > a.y && y > b.y))) {
            double dx = c.x - x;
            double dy = c.y - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if(distance <= tolerance) return true;
            return false;
        }
        else {
            if(Coordinates.distance(a, c) <= tolerance) return true;
            if(Coordinates.distance(b, c) <= tolerance) return true;
            return false;
        }
    }
    public boolean containsPoint(Coordinates c, double tolerance) {
        if(!frameless_) {
            for(int i = 1; i < vertexCount_; i++) {
                if(lineContainsPoint(vertices_[i - 1], vertices_[i], c, tolerance))
                    return true;
            }
        }
        if(isComplete()) {
            int x0 = vertices_[0].x, x1 = vertices_[1].x, x2 = vertices_[2].x;
            int y0 = vertices_[0].y, y1 = vertices_[1].y, y2 = vertices_[2].y;
            int ABx = x1 - x0, ABy = y1 - y0;
            int BCx = x2 - x1, BCy = y2 - y1;
            double dxa = (double) ABx / stitchCount_;
            double dya = (double) ABy / stitchCount_;
            double dxb = (double) BCx / stitchCount_;
            double dyb = (double) BCy / stitchCount_;
            double ax = x0 + dxa * skip_, ay = y0 + dya * skip_;
            double bx = x1 + dxb * (skip_ + 1), by = y1 + dyb * (skip_ + 1);
            for(int i = skip_; i < stitchCount_ - skip_; i++) {
                Coordinates a = new Coordinates((int) ax, (int) ay);
                Coordinates b = new Coordinates((int) bx, (int) by);
                //SHOULD USE DOUBLES FOR A AND B COORDINATES FOR PRECISION
                if(lineContainsPoint(a, b, c, tolerance))
                    return true;
                ax += dxa; ay += dya;
                bx += dxb; by += dyb;
            }
        }
        return false;
    }
    /*public void generateGraphData() {
        if(!isComplete()) return;
        int left = vertices_[0].x;
        int right = left;
        int top = vertices_[0].y;
        int bottom = top;
        for(int i = 1; i < vertexMax_; i++) {
            Coordinates v = vertices_[i];
            if(v.x < left) left = v.x;
            else if(v.x > right) right = v.x;
            if(v.y < top) top = v.y;
            else if(v.y > bottom) bottom = v.y;
        }
        graph_ = new GraphingData(left, top, right - left + 1, bottom - top + 1);
        System.out.println("x: " + left + " y: " + top + " w: " + (right - left) + " h: " + (bottom - top));
        //
        int x0 = vertices_[0].x, x1 = vertices_[1].x, x2 = vertices_[2].x;
        int y0 = vertices_[0].y, y1 = vertices_[1].y, y2 = vertices_[2].y;
        int ABx = x1 - x0, ABy = y1 - y0;
        int BCx = x2 - x1, BCy = y2 - y1;
        double dxa = (double) ABx / stitchCount_;
        double dya = (double) ABy / stitchCount_;
        double dxb = (double) BCx / stitchCount_;
        double dyb = (double) BCy / stitchCount_;
        double ax = x0 + dxa * skip_ - left, ay = y0 + dya * skip_ - top;
        double bx = x1 + dxb * (skip_ + 1) - left, by = y1 + dyb * (skip_ + 1) - top;
        for(int i = skip_; i < stitchCount_ - skip_; i++) {
            bresenham(graph_.data, (int) ax, (int) ay, (int) bx, (int) by);
            ax += dxa; ay += dya;
            bx += dxb; by += dyb;
        }
        graphed_ = true;
    }*/
    public void show(Graphics g) {
        show(g, 1.0f);
    }
    public void show(Graphics g, float alpha) {
        if(alpha == 1.0f) super.show(g);
        else super.show(g, alpha);
        if(!frameless_) {
            g.drawRect(vertices_[0].x, vertices_[0].y, 0, 0);
            for(int i = 1; i < vertexCount_; i++) {
                drawLine(g, vertices_[i - 1], vertices_[i]);
            }
        }
        if(isComplete()) {
            /*if(graphed_) {
                graphData(g);
                return;
            }*/
            int x0 = vertices_[0].x, x1 = vertices_[1].x, x2 = vertices_[2].x;
            int y0 = vertices_[0].y, y1 = vertices_[1].y, y2 = vertices_[2].y;
            int ABx = x1 - x0, ABy = y1 - y0;
            int BCx = x2 - x1, BCy = y2 - y1;
            double dxa = (double) ABx / stitchCount_;
            double dya = (double) ABy / stitchCount_;
            double dxb = (double) BCx / stitchCount_;
            double dyb = (double) BCy / stitchCount_;
            double ax = x0 + dxa * skip_, ay = y0 + dya * skip_;
            double bx = x1 + dxb * (skip_ + 1), by = y1 + dyb * (skip_ + 1);
            for(int i = skip_; i < stitchCount_ - skip_; i++) {
                Coordinates a = new Coordinates((int) ax, (int) ay);
                Coordinates b = new Coordinates((int) bx, (int) by);
                drawLine(g, a, b);
                ax += dxa; ay += dya;
                bx += dxb; by += dyb;
            }
        }
    }
    public String toString() {
        String result = "angle";
        if(vertexCount_ == 0) return result + " underfined";
        result += " A" + vertices_[0];
        if(vertexCount_ == 1) return result;
        result += " B" + vertices_[1];
        if(vertexCount_ == 2) return result;
        result += " C" + vertices_[2];
        return result;
    }
}
