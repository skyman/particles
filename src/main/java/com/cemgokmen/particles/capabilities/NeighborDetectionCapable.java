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

package com.cemgokmen.particles.capabilities;

import com.cemgokmen.particles.models.Particle;
import com.cemgokmen.particles.models.ParticleGrid;

import java.util.List;
import java.util.function.Predicate;

public interface NeighborDetectionCapable extends ParticleCapability {
    List<Particle> getNeighborParticles(boolean includeNulls, Predicate<Particle> filter);
    List<Particle> getAdjacentPositionNeighborParticles(ParticleGrid.Direction d, boolean includeNulls, Predicate<Particle> filter);
    boolean isDirectionInBounds(ParticleGrid.Direction d);
}
