/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fode14;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

/**
 *
 * @author trang
 */
public class Fode14 {
    // Private variables;
    private final double x0, y0, X, r0;
    private double[] x, yExact, yEuler, yImprovedEuler, yRungeKutta;
    
    // Contructors
    public Fode14(double x0, double y0, double X) throws Exception {
        // check validation of arguments
        // Condition: x != 0
        if (x0 == 0) {
            throw new Exception("x0 cannot be zero");
        }
        if (X == 0) {
            throw new Exception("X cannot be zero");
        }
        if (X*x0 < 0) {
            throw new Exception("Only support X and x0 with same sign");
        }
        if (X<=x0) {
            throw new Exception("X must be greater than x0");
        }
        
        // Condition: 1+y/x > 0
        double r00 = y0/x0;
        if (1+r00 <= 0) {
            throw new Exception("x0 and y0 are not suitable");
        }

        // initialize x0, y0, x, r0
        this.x0 = x0;
        this.y0 = y0;
        this.X = X;
        this.r0 = r00;
    }
    
    public Fode14() {
        // x0, y0, x to default values
        this.x0 = 1.0;
        this.y0 = 2.0;
        this.X = 6.0;
        this.r0 = 2.0;
    }
    
    // Private methods
    private void setExact(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("N must be positive");
        }
        
        if (x == null || x.length != n+1) {
            double h = (X-x0)/n;
            // set x
            this.x = new double[n+1];
            for (int i = 0; i <= n; ++i) {
                x[i] = x0 + i*h;
            }
            // set yExact
            // exact solution: y = x*(1+y0/y0)^(x/x0) - x
            yExact = new double[n+1];
            yExact[0] = y0;
            for (int i = 1; i <= n; ++i) {
                yExact[i] = x[i]*Math.pow(1.0+r0, x[i]/x0) - x[i];
            }
        } 
    }
    
    private void setYEuler(int n) throws Exception {
        setExact(n);
        
        if (yEuler == null || yEuler.length != n+1) {
            double h = (X-x0)/n;
            yEuler = new double[n+1];
            yEuler[0] = y0;

            for (int i = 0; i < n; ++i) {
                yEuler[i+1] = yEuler[i] + h*getF(x[i], yEuler[i]);
            }
        }
    }
    
    private void setYImprovedEuler(int n) throws Exception {
        setExact(n);
        
        if (yImprovedEuler == null || yImprovedEuler.length != n+1) {
            yImprovedEuler = new double[n+1];
            yImprovedEuler[0] = y0;

            double k1, k2;
            double h = (X-x0)/n;
            for (int i = 0; i < n; ++i) {
                k1 = getF(x[i], yImprovedEuler[i]);
                k2 = getF(x[i+1], yImprovedEuler[i] + h*k1);
                yImprovedEuler[i+1] = yImprovedEuler[i] + h*(k1+k2)/2.0;
            }
        }
    }
    
    private void setYRungeKutta(int n) throws Exception {
        setExact(n);
        
        if (yRungeKutta == null || yRungeKutta.length != n+1) {
            yRungeKutta = new double[n+1];
            yRungeKutta[0] = y0;

            double k1, k2, k3, k4;
            double h = (X-x0)/n;
            for (int i = 0; i < n; ++i) {
                k1 = getF(x[i], yRungeKutta[i]);
                k2 = getF(x[i] + h/2.0, yRungeKutta[i] + h*k1/2.0);
                k3 = getF(x[i] + h/2.0, yRungeKutta[i] + h*k2/2.0);
                k4 = getF(x[i] + h, yRungeKutta[i] + h*k3);
                yRungeKutta[i+1] = yRungeKutta[i] + h*(k1+2*k2+2*k3+k4)/6.0;
            }
        }
    }
    
    // public methods
    public Series getExactSolutionSeries(int n) throws Exception {
        Series exact = new Series();
        exact.setName("Exact");
        setExact(n);
        for (int i = 0; i <= n; ++i) {
            exact.getData().add(new XYChart.Data(x[i], yExact[i]));
        }
        
        return exact;
    }
    
    public Series getEulerSeries(int n) throws Exception {
        Series euler = new Series();
        euler.setName("Euler");
        
        setYEuler(n);
        for (int i = 0; i <= n; ++i) {
            euler.getData().add(new XYChart.Data(x[i], yEuler[i]));
        }
        
        return euler;
    }
    
    public Series getEulerErrorSeries(int n) throws Exception {
        Series euler = new Series();
        euler.setName("Euler");
        
        setYEuler(n);
        for (int i = 0; i <= n; ++i) {
            euler.getData().add(new XYChart.Data(x[i], yExact[i] - yEuler[i]));
        }
        
        return euler;
    }
    
    public Series getEulerGlobalErrorSeries(int n1, int n2, int step) throws Exception {
        Series euler = new Series();
        euler.setName("Euler");
        
        if (n1 <= 0 || n2 <= 0 || n1 >= n2 || step < 0) {
            throw new Exception("Invalid Grid range input");
        }
        
        for (int i = n1; i <= n2; i+=step) {
            double error = getEulerGlobalError(i);
            euler.getData().add(new XYChart.Data(i, error));
        }
        
        return euler;
    }
    
    public double getEulerGlobalError(int n) throws Exception {
        setYEuler(n);
        
        return yExact[n] - yEuler[n];
    }
    
    public Series getImprovedEulerSeries(int n) throws Exception {
        Series improvedEuler = new Series();
        improvedEuler.setName("Improved Euler");
        
        setYImprovedEuler(n);
        
        for (int i = 0; i <= n; ++i) {
            improvedEuler.getData().add(new XYChart.Data(x[i], yImprovedEuler[i]));
        }
        return improvedEuler;
    }
    
    public Series getImprovedEulerErrorSeries(int n) throws Exception {
        Series improvedEuler = new Series();
        improvedEuler.setName("Improved Euler");
        
        setYImprovedEuler(n);
        
        for (int i = 0; i <= n; ++i) {
            improvedEuler.getData().add(new XYChart.Data(x[i], yExact[i] - yImprovedEuler[i]));
        }
        return improvedEuler;
    }
    
    public Series getImprovedEulerGlobalErrorSeries(int n1, int n2, int step) throws Exception {
        if (n1 <= 0 || n2 <= 0 || n1 >= n2 || step <= 0) {
            throw new Exception("Invalid Grid range input");
        }
        Series improvedEuler = new Series();
        improvedEuler.setName("Improved Euler");
        
        for (int i = n1; i <= n2; i += step) {
            double error = getImprovedEulerGlobalError(i);
            improvedEuler.getData().add(new XYChart.Data(i, error));
        }
        
        return improvedEuler;
    }
    
    public double getImprovedEulerGlobalError(int n) throws Exception {
        setYImprovedEuler(n);
        
        return yExact[n] - yImprovedEuler[n];
    }
    
    public Series getRungeKuttaSeries(int n) throws Exception {
        Series rungeKutta = new Series();
        rungeKutta.setName("Runge-Kutta");
        
        setYRungeKutta(n);
        for (int i = 0; i <= n; ++i) {
            rungeKutta.getData().add(new XYChart.Data(x[i], yRungeKutta[i]));
        }
        return rungeKutta;
    }
    
    public Series getRungeKuttaErrorSeries(int n) throws Exception {
        Series rungeKutta = new Series();
        rungeKutta.setName("Runge-Kutta");
        
        setYRungeKutta(n);
        for (int i = 0; i <= n; ++i) {
            rungeKutta.getData().add(new XYChart.Data(x[i], yExact[i] - yRungeKutta[i]));
        }
        return rungeKutta;
    }
    
    public Series getRungeKuttaGlobalErrorSeries(int n1, int n2, int step) throws Exception {
        if (n1 <= 0 || n2 <= 0 || n1 >= n2 || step <= 0) {
            throw new Exception("Invalid Grid range input");
        }
        Series rungeKutta = new Series();
        rungeKutta.setName("Runge-Kutta");
        
        for (int i = n1; i <= n2; i += step) {
            rungeKutta.getData().add(new XYChart.Data(i, getRungeKuttaGlobalError(i)));
        }
        
        return rungeKutta;
    }
    
    public double getRungeKuttaGlobalError(int n) throws Exception {
        setYRungeKutta(n);
        
        return yExact[n] - yRungeKutta[n];
    }
    
    public double getF(double x, double y) {
        // y' = f(x,y) = (1+y/x)ln(1+y/x) + y/x
        double r = y/x;
        return (1 + r) * Math.log(1 + r) + r;
    }
}
