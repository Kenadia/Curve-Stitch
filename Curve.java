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
public abstract class Curve {
    protected Coordinates [] vertices_;
    protected int vertexCount_;
    protected int vertexMax_;
    protected int stitchCount_;
    protected Color color_;
    //protected GraphingData graph_;
    //protected boolean graphed_;
    protected Curve(int vertexMax, Color color) {
        vertexCount_ = 0;
        vertexMax_ = vertexMax;
        vertices_ = new Coordinates [vertexMax];
        stitchCount_ = 20;
        color_ = color;
        //graph_ = null;
        //graphed_ = false;
    }
    public void setTo(Curve curve) {
        vertices_ = curve.vertices_;
        vertexCount_ = curve.vertexCount_;
        vertexMax_ = curve.vertexMax_;
        stitchCount_ = curve.stitchCount_;
    }
    public boolean isComplete() {
        return vertexCount_ == vertexMax_;
    }
    public Coordinates [] getVertices() {
        return vertices_;
    }
    public int countVertices() {
        return vertexCount_;
    }
    public Coordinates lastVertex() {
        return vertices_[vertexCount_ - 1];
    }
    public Color getColor() {
        return color_;
    }
    public void addVertex(Coordinates c) {
        vertices_[vertexCount_++] = c;
    }
    public void setColor(Color color) {
        color_ = color;
    }
    //public abstract Curve duplicate();
    public abstract boolean containsPoint(Coordinates c, double tolerance);
    //public abstract void generateGraphData();
    /*protected void graphData(Graphics g) {
        int x = graph_.x;
        int y = graph_.y;
        int w = graph_.w;
        int h = graph_.h;
        for(int i = 0; i < w; i++)
            for(int j = 0; j < h; j++)
                if(graph_.data[i][j])
                    g.fillRect(x + i, y + j, 1, 1);
    }*/
    public void show(Graphics g) {
        g.setColor(color_);
    }
    public void show(Graphics g, float alpha) {
        Color color = CurvePanel.relativelyTransparent(color_, alpha);
        g.setColor(color);
    }
    public static void drawLine(Graphics g, Coordinates c1, Coordinates c2) {
        g.drawLine(c1.x, c1.y, c2.x, c2.y);
    }
    public static void bresenham(boolean [] [] graph, int x1, int y1, int x2, int y2) {
        int dx, dy, sx, sy;
        if(x1 < x2) {
            dx = x2 - x1;
            sx = 1;
        }
        else {
            dx = x1 - x2;
            sx = -1;
        }
        if(y1 < y2) {
            dy = y2 - y1;
            sy = 1;
        }
        else {
            dy = y1 - y2;
            sy = -1;
        }
        int error = dx - dy;
        while(!(x1 == x2 && y1 == y2)) {
            graph[x1][y1] = true;
            int e2 = 2 * error;
            if(e2 > -dy) {
                error -= dy;
                x1 += sx;
            }
            if(e2 < dx) {
                error += dx;
                y1 += sy;
            }
        }
        graph[x2][y2] = true;
    }
}
