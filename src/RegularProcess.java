/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package _curvestitch;

import java.util.*;

/**
 *
 * @author kenschiller
 */
public class RegularProcess extends TimerTask {
    private Runnable owner_;
    public RegularProcess(Runnable owner) {
        owner_ = owner;
    }
    public void run() {
        owner_.run();
    }
}
