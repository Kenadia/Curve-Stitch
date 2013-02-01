/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package _curvestitch;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.color.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author kenschiller
 */
public class CurvePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    private java.util.Timer refreshTimer_;
    private RegularProcess refreshProcess_;
    private boolean paused_;
    public static final int width_ = 512;
    public static final int height_ = 512;
    public static final int FPS = 30;
    //
    private ArrayList<Curve> curveList_;
    private ArrayList<Curve> selectedCurves_;
    private Curve currentCurve_;
    private AngleCurvePreview futureCurve_;
    private Coordinates currentMouse_;
    private String textDisplay_;
    private String display2_;
    private int repeater_;
    private final int repeaterPeriod_ = 45;
    //
    private final int border_ = 2;
    private final int textLeft_ = border_;
    private final int textUpper_ = 16 + border_;
    private final int textLower_ = height_ - border_;
    //
    private boolean gridSnap_; //shift
    private boolean angleSnap_; //control
    private boolean vertexSnap_; //option
    private boolean selectionMode_; //z
    private boolean colorSelectMode_; //x
    private int gridSize_;
    private int degreesSnap_;
    private final int [] degreesSnapList_ = {10, 15, 18, 24, 30, 36, 45, 60};
    private int degreesSnapPointer_;
    private int vertexSnapDistance_;
    //
    private boolean colorMode_;
    private Color currentColor_;
    private final int colorIncrement_ = 17;
    private boolean colorModMode_;
    //debugging
    //private boolean collisionTestOn_ = false;
    //private boolean collisionTestFinished_ = false;
    public CurvePanel() {
        paused_ = true;
        refreshTimer_ = new java.util.Timer();
        refreshProcess_ = new RegularProcess(this);
        refreshTimer_.schedule(refreshProcess_, 0, 1000 / FPS);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        //initialize member variables
        curveList_ = new ArrayList<Curve>();
        selectedCurves_ = new ArrayList<Curve>();
        currentCurve_ = null;
        futureCurve_ = null;
        currentMouse_ = null;
        textDisplay_ = null;
        display2_ = null;
        repeater_ = 0;
        //
        gridSnap_ = false;
        angleSnap_ = false;
        vertexSnap_ = false;
        selectionMode_ = false;
        gridSize_ = 32;
        degreesSnap_ = 30;
        degreesSnapPointer_ = 3;
        vertexSnapDistance_ = 16;
        //
        colorMode_ = false;
        currentColor_ = new Color(0, 0, 0, 136);
        colorModMode_ = false;
        //
        paused_ = false;
    }
    public void run() {
        if(paused_) {
            return;
        }
        if(++repeater_ == repeaterPeriod_) repeater_ = 0;
        repaint();
    }
    public void paintComponent(Graphics g) {
        /*if(collisionTestFinished_) {
            return;
        }
        if(collisionTestOn_) {
            g.setColor(new Color(0, 0, 0, 160));
            for(int i = 0; i < width_; i++) {
                for(int j = 0; j < height_; j++) {
                    if(!curveList_.get(0).containsPoint(new Coordinates(i, j), 3)) {
                        g.fillRect(i, j, 1, 1);
                    }
                }
            }
            collisionTestFinished_ = true;
            return;
        }*/
        //background
        g.drawString("", 0, 0);
        g.setColor(Color.white);
        g.fillRect(0, 0, width_, height_);
        if(colorMode_) {
            g.setColor(currentColor_);
            g.fillRect(0, 0, width_, height_);
        }
        //help/pause menu
        if(paused_) {
            g.setColor(Color.gray);
            /*drawLines(g, "shift: hold for grid snapping\n" +
                    "control: hold for angle snapping\n" +
                    "shift + control: hold for angle and length snapping\n" +
                    "escape: finish curve\n" +
                    "\n" +
                    "z: hold for selection tool\n" +
                    "   - backspace: delete selected curve(s)\n" +
                    "x: hold for color selection tool\n" +
                    "c: toggle color selection mode\n" +
                    "   - W,E,R,T,S,D,F,G: adjust color components\n" +
                    "   - shift: hold for prcise adjustments", 32, 40);*/
            drawLines(g, "hold shift: grid snapping\n" +
                    "hold control: angle snapping\n" +
                    "hold shift and control: angle and length snapping\n" +
                    "escape: finish curve\n" +
                    "\n" +
                    "hold z: selection tool\n" +
                    "   - backspace: delete selected curve(s)\n" +
                    "hold x: color selection tool\n" +
                    "c: toggle color selection mode\n" +
                    "   - use the W,E,R,T,S,D,F,G keys to adjust color components\n" +
                    "   - hold shift for prcise adjustments", 32, 40);
            return;
        }
        //curves and color select
        //g.setColor(brightenByFactor(currentColor_, 2.0f));
        if(colorSelectMode_ || colorMode_) {
            g.setColor(currentColor_);
            g.fillRect(currentMouse_.x - 2, currentMouse_.y - 2, 5, 5);
        }
        if(futureCurve_ != null && !(currentMouse_ == null && futureCurve_.countVertices() == 1)) {
            if(!(selectionMode_ || colorSelectMode_ || colorMode_)) {
                futureCurve_.show(g);
            }
        }
        float pulseAlpha = (float) (0.75 + 0.25 * Math.sin(Math.PI * 2 * repeater_ / repeaterPeriod_));
        for(Curve curve : curveList_) {
            if(selectedCurves_.contains(curve)) curve.show(g, pulseAlpha);
            else curve.show(g);
        }
        //text display 1
        g.setColor(Color.blue);
        if(textDisplay_ != null)
            textOver_width(g, Color.white, textDisplay_, textLeft_, textUpper_);
        //text display 2
        if(colorMode_ || colorModMode_) {
            int x = textLeft_;
            String red = "" + currentColor_.getRed();
            String green = "" + currentColor_.getGreen();
            String blue = "" + currentColor_.getBlue();
            String alpha = "" + currentColor_.getAlpha();
            g.setColor(Color.red);
            x += textOver_width(g, Color.black, red, x, textLower_);
            g.setColor(Color.green);
            x += textOver_width(g, Color.black, green, x, textLower_);
            g.setColor(Color.blue);
            x += textOver_width(g, Color.black, blue, x, textLower_);
            g.setColor(Color.white);
            textOver_width(g, Color.black, alpha, x, textLower_);
        }
        else {
            if(display2_ != null)
                textOver_width(g, Color.white, display2_, textLeft_, textLower_);
            else if(colorSelectMode_)
                textOver_width(g, Color.white, "color selection tool", textLeft_, textLower_);
            else if(selectionMode_)
                textOver_width(g, Color.white, "selection tool", textLeft_, textLower_);
        }
    }
    public void helpMode(boolean on) {
        paused_ = on;
        repaint();
    }
    public void colorSelectedCurves(Color newColor) {
        for(Curve curve : selectedCurves_) {
            curve.setColor(newColor);
        }
    }
    public Coordinates closestVertex(Coordinates c) {
        Coordinates closest = null; double distanceToClosest = 9001.0;
        for(Curve curve : curveList_) {
            Coordinates [] vertices = curve.getVertices();
            for(int i = 0; i < curve.countVertices(); i++) {
                double distance = Coordinates.distance(vertices[i], c);
                if(distance < distanceToClosest) {
                    closest = vertices[i];
                    distanceToClosest = distance;
                }
            }
        }
        return closest;
    }
    public void generateCurvePreview() {
        textDisplay_ = null;
        if(currentMouse_ != null) {
            if(currentCurve_ != null) futureCurve_.setTo(currentCurve_);
            else futureCurve_ = new AngleCurvePreview(brightenByFactor(currentColor_, 2.0f));
            Coordinates newVertex = currentMouse_;
            //snapping
            Coordinates closest = null; double distanceToClosest = 9001.0;
            if(vertexSnap_) {
                closest = closestVertex(newVertex);
                if(closest != null) {
                    distanceToClosest = Coordinates.distance(newVertex, closest);
                }
            }
            if(vertexSnap_ && distanceToClosest <= vertexSnapDistance_) {
                newVertex = new Coordinates(closest.x, closest.y);
            }
            else {
                if(angleSnap_ && currentCurve_ != null) {
                    Coordinates c1 = currentCurve_.lastVertex(), c2 = currentMouse_;
                    double length = Coordinates.distance(c1, c2);
                    if(gridSnap_) {
                        length = roundNearest(length, gridSize_);
                        textDisplay_ = "" + (int) (length / gridSize_);
                    }
                    double angle = Coordinates.angleBetween(c1, c2);
                    double degrees = angle * 180 / Math.PI;
                    double adjustedDegrees = roundNearest(degrees, degreesSnap_);
                    //System.out.println("degrees: " + degrees + " adjusted: " + adjustedDegrees);
                    double adjustedAngle = adjustedDegrees * Math.PI / 180;
                    int newX = c1.x + (int) (length * Math.cos(adjustedAngle));
                    int newY = c1.y + (int) (length * Math.sin(adjustedAngle));
                    newVertex = new Coordinates(newX, newY);
                }
                else if(gridSnap_) {
                    int newX = roundNearest(newVertex.x, gridSize_);
                    int newY = roundNearest(newVertex.y, gridSize_);
                    newVertex = new Coordinates(newX, newY);
                }
            }
            //
            futureCurve_.addVertex(newVertex);
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {
        currentMouse_ = null;
    }
    public void mouseMoved(MouseEvent e) {
        currentMouse_ = new Coordinates(e.getX(), e.getY());
        generateCurvePreview();
    }
    public void mousePressed(MouseEvent e) {
        if(paused_) return;
        display2_ = null;
        Coordinates c = new Coordinates(e.getX(), e.getY());
        //
        if(selectionMode_ || colorSelectMode_ || colorMode_) {
            Curve selectedCurve = null;
            double selectionTolerance = (colorSelectMode_ || colorMode_)? 6.0 : 3.0;
            for(Curve curve : curveList_) {
                if(curve.containsPoint(c, selectionTolerance)) {
                    selectedCurve = curve;
                    break;
                }
            }
            if(selectedCurve != null) {
                if(colorSelectMode_ || colorMode_) {
                    currentColor_ = selectedCurve.getColor();
                }
                else {
                    if(selectedCurves_.contains(selectedCurve)) {
                        selectedCurves_.remove(selectedCurve);
                        if(selectedCurves_.isEmpty()) {
                            colorModMode_ = false;
                        }
                    }
                    else {
                        selectedCurves_.add(selectedCurve);
                    }
                }
            }
            return;
        }
        //
        if(currentCurve_ == null) {
            if(futureCurve_ != null) {
                currentCurve_ = new AngleCurve(currentColor_);
                currentCurve_.setTo(futureCurve_);
                curveList_.add(currentCurve_);
            }
        }
        else {
            //currentCurve_.addVertex(c);
            currentCurve_.setTo(futureCurve_);
            if(currentCurve_.isComplete()) {
                //currentCurve_.generateGraphData();
                currentCurve_ = null;
            }
        }
        generateCurvePreview();
    }
    public void mouseReleased(MouseEvent e) {
        if(paused_) return;
    }
    public void mouseDragged(MouseEvent e) {}
    public void keyPressed(KeyEvent e) {
        key(e.getKeyCode(), true);
    }
    public void keyReleased(KeyEvent e) {
        key(e.getKeyCode(), false);
    }
    public void keyTyped(KeyEvent e) {}
    public void key(int keyCode, boolean keyDown) {
        if(paused_) {
            switch(keyCode) {
                case KeyEvent.VK_H:
                    if(keyDown) helpMode(false);
                    break;
            }
            return;
        }
        if(keyDown) display2_ = null;
        switch(keyCode) {
            case KeyEvent.VK_H:
                if(keyDown) helpMode(true);
                break;
            case KeyEvent.VK_SHIFT:
                gridSnap_ = keyDown;
                break;
            case KeyEvent.VK_CONTROL:
                angleSnap_ = keyDown;
                break;
            case KeyEvent.VK_ALT:
                vertexSnap_ = keyDown;
                break;
            case KeyEvent.VK_ESCAPE:
                if(keyDown && currentCurve_ != null && currentCurve_.countVertices() == 1) {
                    curveList_.remove(currentCurve_);
                }
                currentCurve_ = null;
                break;
            case KeyEvent.VK_BACK_SPACE:
                for(Curve curve : selectedCurves_) {
                    curveList_.remove(curve);
                }
                //intentional lack of break
            case KeyEvent.VK_SPACE:
                //collisionTestOn_ = true;
                selectedCurves_.clear();
                colorModMode_ = false;
                break;
            case KeyEvent.VK_Z:
                if(!colorMode_) selectionMode_ = keyDown;
                break;
            case KeyEvent.VK_X:
                colorSelectMode_ = keyDown;
                break;
            case KeyEvent.VK_Q:
                if(keyDown && !colorMode_ && !colorModMode_) {
                    if(degreesSnapPointer_ != degreesSnapList_.length - 1) {
                        degreesSnapPointer_++;
                        degreesSnap_ = degreesSnapList_[degreesSnapPointer_];
                    }
                    display2_ = "degrees snap: " + degreesSnap_;
                }
                break;
            case KeyEvent.VK_A:
                if(keyDown && !colorMode_&& !colorModMode_) {
                    if(degreesSnapPointer_ != 0) {
                        degreesSnapPointer_--;
                        degreesSnap_ = degreesSnapList_[degreesSnapPointer_];
                    }
                    display2_ = "degrees snap: " + degreesSnap_;
                }
                break;
            case KeyEvent.VK_C:
                if(keyDown) {
                    if(selectedCurves_.isEmpty()) {
                        colorMode_ = !colorMode_;
                    }
                    else {
                        colorModMode_ = !colorModMode_;
                    }
                }
                break;
            case KeyEvent.VK_W:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int newRed = currentColor_.getRed();
                    int g = currentColor_.getGreen();
                    int b = currentColor_.getBlue();
                    int a = currentColor_.getAlpha();
                    if(gridSnap_) newRed++;
                    else newRed += colorIncrement_;
                    if(newRed > 255) newRed = 255;
                    currentColor_ = new Color(newRed, g, b, a);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_S:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int newRed = currentColor_.getRed();
                    int g = currentColor_.getGreen();
                    int b = currentColor_.getBlue();
                    int a = currentColor_.getAlpha();
                    if(gridSnap_) newRed--;
                    else newRed -= colorIncrement_;
                    if(newRed < 0) newRed = 0;
                    currentColor_ = new Color(newRed, g, b, a);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_E:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int r = currentColor_.getRed();
                    int newGreen = currentColor_.getGreen();
                    int b = currentColor_.getBlue();
                    int a = currentColor_.getAlpha();
                    if(gridSnap_) newGreen++;
                    else newGreen += colorIncrement_;
                    if(newGreen > 255) newGreen = 255;
                    currentColor_ = new Color(r, newGreen, b, a);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_D:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int r = currentColor_.getRed();
                    int newGreen = currentColor_.getGreen();
                    int b = currentColor_.getBlue();
                    int a = currentColor_.getAlpha();
                    if(gridSnap_) newGreen--;
                    else newGreen -= colorIncrement_;
                    if(newGreen < 0) newGreen = 0;
                    currentColor_ = new Color(r, newGreen, b, a);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_R:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int r = currentColor_.getRed();
                    int g = currentColor_.getGreen();
                    int newBlue = currentColor_.getBlue();
                    int a = currentColor_.getAlpha();
                    if(gridSnap_) newBlue ++;
                    else newBlue += colorIncrement_;
                    if(newBlue > 255) newBlue = 255;
                    currentColor_ = new Color(r, g, newBlue, a);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_F:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int r = currentColor_.getRed();
                    int g = currentColor_.getGreen();
                    int newBlue = currentColor_.getBlue();
                    int a = currentColor_.getAlpha();
                    if(gridSnap_) newBlue--;
                    else newBlue -= colorIncrement_;
                    if(newBlue < 0) newBlue = 0;
                    currentColor_ = new Color(r, g, newBlue, a);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_T:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int r = currentColor_.getRed();
                    int g = currentColor_.getGreen();
                    int b = currentColor_.getBlue();
                    int newAlpha = currentColor_.getAlpha();
                    if(gridSnap_) newAlpha++;
                    else newAlpha += colorIncrement_;
                    if(newAlpha > 255) newAlpha = 255;
                    currentColor_ = new Color(r, g, b, newAlpha);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            case KeyEvent.VK_G:
                if(keyDown && (colorMode_ || colorModMode_)) {
                    int r = currentColor_.getRed();
                    int g = currentColor_.getGreen();
                    int b = currentColor_.getBlue();
                    int newAlpha = currentColor_.getAlpha();
                    if(gridSnap_) newAlpha--;
                    else newAlpha -= colorIncrement_;
                    if(newAlpha < 0) newAlpha = 0;
                    currentColor_ = new Color(r, g, b, newAlpha);
                    if(colorModMode_) colorSelectedCurves(currentColor_);
                }
                break;
            default:
                break;
        }
        generateCurvePreview();
    }
    public static int roundNearest(double value, int mod) {
        return mod * (int) ((value + (double) mod / 2) / mod);
    }
    public static void drawLines(Graphics g, String string, int x, int y) {
        Scanner sc = new Scanner(string);
        sc.useDelimiter("\n");
        while(sc.hasNext()) {
            g.drawString(sc.next(), x, y);
            y += 16;
        }
        sc.close();
    }
    public static int textOver_width(Graphics g, Color bg, String string, int x, int y) {
        int border = 3;
        Color color = g.getColor();
        int w = g.getFontMetrics().stringWidth(string) + border * 2;
        int h = g.getFontMetrics().getHeight();
        g.setColor(bg);
        g.fillRect(x, y - h, w, h);
        g.setColor(color);
        g.drawString(string, x + border, y - border);
        return w;
    }
    public static Color relativelyTransparent(Color original, float alpha) {
        ColorSpace srbg = ICC_ColorSpace.getInstance(ColorSpace.CS_sRGB);
        double originalAlpha = 1.0 * original.getAlpha() / 255;
        alpha *= originalAlpha;
        return new Color(srbg, original.getColorComponents(null), alpha);
    }
    public static float [] getHSB(Color color) {
        float [] rgb = new float [4];
        float [] hsb = new float [3];
        color.getComponents(rgb);
        int r = (int) (rgb[0] * 256);
        int g = (int) (rgb[1] * 256);
        int b = (int) (rgb[2] * 256);
        Color.RGBtoHSB(r, g, b, hsb);
        return hsb;
    }
    /*public static int [] getRGBInts(Color color) {
        int [] rgb = new int[3];
        rgb[0] = color.getRed();
        rgb[1] = color.getGreen();
        rgb[2] = color.getBlue();
        return rgb;
    }*/
    public static Color brightenByFactor1(Color original, float factor) {
        float [] hsb = getHSB(original);
        hsb[2] = 1.0f - (1.0f - hsb[2]) / factor;
        if(hsb[2] < 0.0f) hsb[2] = 0.0f;
        else if(hsb[2] > 1.0f) hsb[2] = 1.0f;
        int newRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return new Color(newRGB);
    }
    public static Color brightenByFactor(Color original, float factor) {
        //factor > 1.0f
        int r = original.getRed();
        int g = original.getGreen();
        int b = original.getBlue();
        r = 255 - (int) ((255 - r) / factor);
        g = 255 - (int) ((255 - g) / factor);
        b = 255 - (int) ((255 - b) / factor);
        return new Color(r, g, b);
    }
}
