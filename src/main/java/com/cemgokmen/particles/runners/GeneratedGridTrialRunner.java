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

package com.cemgokmen.particles.runners;

import com.cemgokmen.particles.algorithms.AlignmentAlgorithm;
import com.cemgokmen.particles.algorithms.ParticleAlgorithm;
import com.cemgokmen.particles.generators.RandomSystemGenerator;
import com.cemgokmen.particles.io.GridIO;
import com.cemgokmen.particles.io.SampleSystemMetadata;
import com.cemgokmen.particles.io.html.HTMLGenerator;
import com.cemgokmen.particles.models.Particle;
import com.cemgokmen.particles.models.ParticleGrid;
import com.cemgokmen.particles.models.amoebot.AmoebotGrid;
import com.cemgokmen.particles.models.amoebot.AmoebotParticle;
import com.cemgokmen.particles.models.amoebot.gridshapes.QuadrilateralAmoebotGrid;
import com.cemgokmen.particles.models.amoebot.gridshapes.ToroidalAmoebotGrid;
import com.cemgokmen.particles.models.amoebot.specializedparticles.DirectedAmoebotParticle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneratedGridTrialRunner {
    private static final int PARTICLE_COUNT = 10000;
    private static final int GRID_SIZE = 2 * (int) Math.sqrt(PARTICLE_COUNT);
    private static final Path basePath = Paths.get("/Users/cgokmen/research/results/thesis-alignment-random-4/");

    public static void main(String[] args) throws Exception {
        ToroidalAmoebotGrid grid = new ToroidalAmoebotGrid(15);
        final ParticleGrid.Compass compass = grid.getCompass();

        AlignmentAlgorithm algorithm = new AlignmentAlgorithm(20, 1, 1.1);

        List<Supplier<Particle>> directedParticleSuppliers = compass.getDirections().stream().map(d -> {
            return (Supplier<Particle>) () -> {
                DirectedAmoebotParticle p = new DirectedAmoebotParticle(compass, d, false);
                p.setAlgorithm(algorithm);
                return p;
            };
        }).collect(Collectors.toList());
        RandomSystemGenerator.addUniformWeightedParticles(grid, directedParticleSuppliers, 50);

        grid.assignAllParticlesAlgorithm(algorithm);

        int iterations = 1000000;
        int seconds = 20;
        int fps = 1; //24;

        int frames = seconds * fps;
        int step = iterations / frames;

        int[] stops = IntStream.rangeClosed(0, frames).map(k -> step * k).toArray();

        TrialUtils.runTrials(grid, stops, basePath, "pdf");
    }
}
