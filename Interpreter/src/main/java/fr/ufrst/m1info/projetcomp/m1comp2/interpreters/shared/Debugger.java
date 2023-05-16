package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared;

import fr.ufrst.m1info.projetcomp.m1comp2.Quad;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.ASTJCStop;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.jjc.InterpreterJjc;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.mjj.InterpreterMjj;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Debugger {
    private List<Observer> listeners;
    private Thread thread;
    private int state;
    private InterpreterJjc interpreterJjc;
    private InterpreterMjj interpreterMjj;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSED = 1;
    private Set<Integer> breakPoints;

    private int currentId;

    public Debugger(){
        breakPoints = new TreeSet<>();
        currentId = 1;
        listeners = new ArrayList<>();
    }

    public Thread getThread() {
        return thread;
    }

    public void addBreakPoint(int i){
        breakPoints.add(i);
    }

    public void removeBreakPoint(int i) {
        breakPoints.remove(i);
    }

    public void addBreakPoints(Set<Integer> points){
        breakPoints.addAll(points);
    }

    public void addObserver(Observer ob){
        listeners.add(ob);
    }

    public void removeObserver(Observer ob){
        listeners.remove(ob);
    }

    public void alertObservers(){
        List<String> stack = new ArrayList<>();
        if(interpreterMjj != null){
            for(Quad quad : interpreterMjj.getMemory().getStack().getValues()){
                stack.add(quad.toString());
            }
        }
        if(interpreterJjc != null){
            for(Quad quad : interpreterJjc.getMemory().getStack().getValues()){
                stack.add(quad.toString());
            }
        }
        for(Observer ob : listeners){
            ob.notifyChange(stack);
            if(interpreterMjj != null){
                //ob.highlightMjj(currentId);
            }else{
                ob.highlightJjc(currentId);
            }
        }
    }

    public void clearBreakPoint(){
        breakPoints.clear();
    }

    public void endButton(){
        for(Observer ob : listeners){
            ob.hideButtons();
        }
    }

    public void alertObservers(String s, boolean mjj){
        for(Observer ob : listeners){
            if(mjj){
                ob.printInMiniJajaCodeOutput(s);
                //ob.highlightMjj(currentId);
            }else{
                ob.printInJajaCodeOutput(s);
                ob.highlightJjc(currentId);
            }
        }
    }

    public void setInterpreter(InterpreterJjc interpreter){
        interpreterMjj = null;
        interpreterJjc = interpreter;
    }

    public void setInterpreter(InterpreterMjj interpreter){
        interpreterJjc = null;
        interpreterMjj = interpreter;
    }

    public void debugJJC(){
        thread = new Thread(() -> {
            try {
                interpreterJjc.interpret();
            } catch (VisitorException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    public void debugMJJ(){
        thread = new Thread(() -> {
            try {
                interpreterMjj.interpret();
            } catch (VisitorException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    public void stop() {
        if(thread != null){
            thread.interrupt();
            thread = null;
        }
    }

    public synchronized void next(){
        state = STATE_RUNNING;
        notifyAll();
        alertObservers();
    }

    private int getMinBreak(){
        if(breakPoints.isEmpty()){
            return -1;
        }
        return breakPoints.iterator().next();
    }

    public synchronized void visit(Node node) throws InterruptedException {
        ++currentId;
        if(node instanceof ASTJCStop){
            stop();
            return;
        }
        state = STATE_PAUSED;
        alertObservers();
        if(getMinBreak() < currentId){
            wait();
        }
    }

    public synchronized void moveTo(int x){
        currentId = x;
        //System.out.println("current "+currentId);
    }

    public synchronized void visit(fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.Node node) throws InterruptedException {
        ++currentId;
        if(node instanceof ASTJCStop){
            stop();
            return;
        }
        state = STATE_PAUSED;
        alertObservers();
        if(getMinBreak() < currentId){
            wait();
        }
    }
}
