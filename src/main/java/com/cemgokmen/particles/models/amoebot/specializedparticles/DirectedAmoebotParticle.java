/*
 * Particles, a self-organizing particle system simulator.
 * Copyright (C) 2018  Cem Gokmen.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.cemgokmen.particles.models.amoebot.specializedparticles;

import com.cemgokmen.particles.capabilities.WrappedNormalRandomDirectionCapable;
import com.cemgokmen.particles.capabilities.SpinCapable;
import com.cemgokmen.particles.models.ParticleGrid;
import com.cemgokmen.particles.models.amoebot.AmoebotGrid;
import com.cemgokmen.particles.models.amoebot.AmoebotParticle;
import com.cemgokmen.particles.models.continuous.ContinuousParticleGrid;
import com.cemgokmen.particles.util.Utils;
import org.la4j.Vector;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.function.Function;

public class DirectedAmoebotParticle extends AmoebotParticle implements SpinCapable, WrappedNormalRandomDirectionCapable {
    private ParticleGrid.Direction direction;
    private final AmoebotGrid.Compass compass;

    public static DirectedAmoebotParticle chosenOne;

    public DirectedAmoebotParticle(AmoebotGrid.Compass compass, ParticleGrid.Direction direction, boolean greyscale) {
        this.compass = compass;
        this.direction = direction;

        if (chosenOne == null) chosenOne = this;
    }

    public ParticleGrid.Direction getDirection() {
        return this.direction;
    }

    public void setDirection(ParticleGrid.Direction direction) {
        this.direction = direction;
    }

    @Override
    public ParticleGrid.Compass getCompass() {
        return compass;
    }

    public static final Color ARROW_COLOR = Color.BLACK;
    public static final int ARROW_THICKNESS = 4;
    public static final BasicStroke ARROW_STROKE = new BasicStroke(ARROW_THICKNESS, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);

    @Override
    public void drawParticle(Graphics2D graphics, Vector screenPosition, int edgeLength, Function<Vector, Vector> gridToScreenCoords) {
        //super.drawParticle(graphics, screenPosition, edgeLength);

        // Calculate the arrow
        // Note that we need our current position for non-linear transformations
        Vector towardsArrowTipOnGrid = this.direction.getVector();
        Vector position = this.grid.getParticlePosition(this);

        Vector arrowTipOnGrid = position.add(towardsArrowTipOnGrid);
        Vector arrowTipOnScreen = gridToScreenCoords.apply(arrowTipOnGrid);

        Vector towardsArrowTip = arrowTipOnScreen.subtract(screenPosition);
        towardsArrowTip = towardsArrowTip.multiply(CIRCLE_RADIUS / (towardsArrowTip.norm() * 1.1));

        Vector towardsArrowLeft = Vector.fromArray(new double[]{towardsArrowTip.get(1), -towardsArrowTip.get(0)});
        Vector towardsArrowRight = towardsArrowLeft.multiply(-1);
        Vector towardsArrowBase = towardsArrowTip.multiply(-1);

        Vector arrowLeft = screenPosition.add(towardsArrowLeft);
        Vector arrowTip = screenPosition.add(towardsArrowTip);
        Vector arrowRight = screenPosition.add(towardsArrowRight);
        Vector arrowBase = screenPosition.add(towardsArrowBase);

        // Now make the polygon
        Line2D.Double[] lines = new Line2D.Double[]{
            new Line2D.Double(arrowBase.get(0), arrowBase.get(1), arrowTip.get(0), arrowTip.get(1)),
            new Line2D.Double(arrowLeft.get(0), arrowLeft.get(1), arrowTip.get(0), arrowTip.get(1)),
            new Line2D.Double(arrowRight.get(0), arrowRight.get(1), arrowTip.get(0), arrowTip.get(1)),
        };

        //graphics.setColor(this == chosenOne ? Color.BLUE : Color.RED);
        graphics.setColor(ARROW_COLOR);
        graphics.setStroke(ARROW_STROKE);
        Arrays.stream(lines).forEach(graphics::draw);

        //graphics.setColor(Color.BLACK);
        //graphics.drawString(((ToroidalAmoebotGrid) this.grid).getParticleLevel(this).toString(), (int) screenPosition.get(0), (int) screenPosition.get(1));
    }

    @Override
    public ParticleGrid.Direction getWrappedNormalRandomDirection(ParticleGrid.Direction mean, double standardDeviation) {
        // Default range is -Pi to Pi. Let's switch that to our range of [-3, 3].
        double sample = Utils.randomWrappedNorm(standardDeviation) * 3 / (Math.PI);
        int steps = (int) Math.round(sample);
        return this.compass.shiftDirectionCounterclockwise(mean, steps);
    }

    @Override
    public boolean shouldDrawEdges() {
        return false;
    }
}
