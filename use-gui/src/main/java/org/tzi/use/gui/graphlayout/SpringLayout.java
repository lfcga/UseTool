/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id$

package org.tzi.use.gui.graphlayout;

import java.util.Iterator;
import java.util.List;

import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.gui.views.diagrams.Layoutable;

/**
 * A spring embedder layout algorithm. See, e.g. G. Di Battista et
 * al.: "Graph Drawing", pp. 303, Prentice Hall, 1999.
 *
 * @author      Mark Richters
 */
public class SpringLayout<N extends Layoutable> {
	/** the graph to be layouted **/
	protected DirectedGraph<N, ?> fGraph;
	/** maximum width of layout **/
	protected double fWidth;
	/** maximum height of layout **/
	protected double fHeight;
	/** margin on left/right side of the drawing area **/
    protected double fMarginX;
    /** margin on top/bottom side of the drawing area **/
    protected double fMarginY;
    private double fEdgeLen = 120.0;
    protected List<N> fNodes;
    protected double[] fXn;
    protected double[] fYn;

    /**
     * Constructs a new SpringLayouter.
     * 
     * @param width width of drawing area
     * @param height height of drawing area
     * @param marginx margin on left/right side of the drawing area
     * @param marginy margin on top/bottom side of the drawing area
     */
	public SpringLayout(DirectedGraph<N, ?> g, 
                        double width, double height,
                        double marginx, double marginy) {
        fGraph = g;
        fWidth = width;
        fHeight = height;
        fMarginX = marginx;
        fMarginY = marginy;
        
        fNodes = fGraph.getNodes();
        fXn = new double[fNodes.size()];
        fYn = new double[fNodes.size()];
    }

    /**
     * Sets a new default length for edges.
     */
    public void setEdgeLen(double len) {
        fEdgeLen = len;
    }

    /**
     * Calculates a layout. This method may be called repeatedly for
     * refining the layout if the graph does not change between calls.
     */
    public void layout() {
        final int N = fNodes.size();
        final double k1 = 1;
        final double k2 = 10000;

        double xc = 0.0;
        double yc = 0.0;
        for (int i = 0; i < N; i++) {
            N v = fNodes.get(i);
            
            double xv = v.getCenter().getX();
            double yv = v.getCenter().getY();

            // spring force
            Iterator<N> uIter = fGraph.sourceNodeSet(v).iterator();
            double sumfx1 = 0.0;
            double sumfy1 = 0.0;
            while (uIter.hasNext() ) {
                N u = uIter.next();
                
                double xu = u.getCenter().getX();
                double yu = u.getCenter().getY();
                double dx = xv - xu;
                double dy = yv - yu;
                double d = Math.sqrt(dx * dx + dy * dy);
                d = (d == 0) ? .0001 : d;
                double c = k1 * (d - fEdgeLen) / d;
                sumfx1 += c * dx;
                sumfy1 += c * dy;
            }

       
            // electrical force

            uIter = fGraph.iterator();
            double sumfx2 = 0.0;
            double sumfy2 = 0.0;
            while (uIter.hasNext() ) {
                N u = uIter.next();
                if (u == v )
                    continue;

                double xu = u.getCenter().getX();
                double yu = u.getCenter().getY();
                double dx = xv - xu;
                double dy = yv - yu;
                double d = dx * dx + dy * dy;
                if (d > 0 ) {
                    double c = k2 / (d * Math.sqrt(d));
                    sumfx2 += c * dx;
                    sumfy2 += c * dy;
                }
            }

            // store new positions
            fXn[i] = xv - Math.max(-5, Math.min(5, sumfx1 - sumfx2));
            fYn[i] = yv - Math.max(-5, Math.min(5, sumfy1 - sumfy2));

            // for determining the center of the graph
            xc += fXn[i];
            yc += fYn[i];
        }

        // offset from center of graph to center of drawing area
        double dx = fWidth / 2 - xc / N;
        double dy = fHeight / 2 - yc / N;

        // use only small steps for smooth animation
        dx = Math.max(-2.5, Math.min(2.5, dx));
        dy = Math.max(-2.5, Math.min(2.5, dy));

        // set new positions
        for (int i = 0; i < N; i++) {
            N v = fNodes.get(i);
            double halfWidth = v.getWidth() / 2;
            double halfHeight = v.getHeight() / 2;
            
            // move each node towards center of drawing area and keep
            // it within bounds
            double x = Math.max(fMarginX + halfWidth,  Math.min(fWidth - fMarginX - halfWidth, fXn[i] + dx));
            double y = Math.max(fMarginY + halfHeight, Math.min(fHeight - fMarginY - halfHeight, fYn[i] + dy));
            v.setCenter(x, y);
        }
    }
}

