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

package com.cemgokmen.particles.models.amoebot;

import com.cemgokmen.particles.util.Utils;
import com.cemgokmen.particles.models.Particle;
import com.cemgokmen.particles.models.ParticleGrid;
import com.google.common.collect.ImmutableList;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.matrix.dense.Basic2DMatrix;

import java.util.HashMap;
import java.util.stream.Stream;

public abstract class AmoebotGrid extends ParticleGrid {
    public static class AmoebotCompass extends Compass {
        private static final Direction SE = new Direction(Utils.getVector(1, 0));
        private static final Direction NE = new Direction(Utils.getVector(1, -1));
        private static final Direction N = new Direction(Utils.getVector(0, -1));
        private static final Direction NW = new Direction(Utils.getVector(-1, 0));
        private static final Direction SW = new Direction(Utils.getVector(-1, 1));
        private static final Direction S = new Direction(Utils.getVector(0, 1));

        private static final Direction[] directions = {N, NW, SW, S, SE, NE};
        private final HashMap<Direction, Integer> directionOrder;

        public AmoebotCompass() {
            this.directionOrder = new HashMap<>();
            for (int i = 0; i < directions.length; i++) {
                this.directionOrder.put(directions[i], i);
            }
        }

        @Override
        public ImmutableList<Direction> getDirections() {
            return ImmutableList.copyOf(directions);
        }

        @Override
        public Direction shiftDirectionCounterclockwise(Direction d, double times) {
            int index = this.directionOrder.get(d);
            return directions[Math.floorMod(index + (int) times, directions.length)];
        }

        public int getMinorArcLength(Direction a, Direction b) {
            int diff = Math.abs(this.directionOrder.get(a) - this.directionOrder.get(b));
            if (diff > directions.length / 2) {
                diff = directions.length - diff;
            }

            return diff;
        }

        public double getAngleFromMinorArcLength(int minorArcLength) {
            return minorArcLength * Math.PI / 3;
        }

        @Override
        public double getAngleBetweenDirections(Direction a, Direction b) {
            return this.getAngleFromMinorArcLength(this.getMinorArcLength(a, b));
        }
    }

    private final AmoebotCompass compass = new AmoebotCompass();

    @Override
    public Compass getCompass() {
        return this.compass;
    } // TODO: Delegate this! Compass should be assigned to particles

    @Override
    public boolean isParticleValid(Particle p) {
        return p instanceof AmoebotParticle;
    }

    private static final Matrix axialToPixel = new Basic2DMatrix(new double[][]{
            {3.0 / 2.0, 0.0},
            {Math.sqrt(3) / 2.0, Math.sqrt(3)}
    });

    @Override
    public Vector getUnitPixelCoordinates(Vector in) {
        return convertAxialToCartesian(in);
    }

    public static Vector convertAxialToCartesian(Vector in) {
        return axialToPixel.multiply(in);
    }

    @Override
    public Vector getRandomPosition(Particle particle) {
        int cnt = (int) this.getValidPositions().count();
        long idx = Utils.random.nextInt(cnt - 1);
        return this.getValidPositions().skip(idx).findFirst().get();
    }
}