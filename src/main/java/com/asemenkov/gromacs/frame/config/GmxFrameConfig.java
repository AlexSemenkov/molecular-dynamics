package com.asemenkov.gromacs.frame.config;

import java.util.function.Supplier;

import com.asemenkov.gromacs.frame.GmxFrame;
import com.asemenkov.gromacs.frame.coordinates.*;
import com.asemenkov.gromacs.frame.structure.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import com.asemenkov.gromacs.particles.config.GmxParticlesConfig;
import com.asemenkov.utils.Logger;
import com.asemenkov.utils.Factories.DuoFactory;

/**
 * @author asemenkov
 * @since Apr 22, 2018
 */
@Configuration
@Import(GmxParticlesConfig.class)
public class GmxFrameConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrameStructureFromGroFileBuilder frameStructureFromGroFileBuilder() {
        return new GmxFrameStructureFromGroFileBuilder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrameStructureFromScratchBuilder frameStructureFromScratchBuilder() {
        return new GmxFrameStructureFromScratchBuilder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrameStructureFromArraysBuilder frameStructureFromArraysBuilder() {
        return new GmxFrameStructureFromArraysBuilder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrameCoordinatesFromGroFileBuilder frameCoordinatesFromGroFileBuilder() {
        return new GmxFrameCoordinatesFromGroFileBuilder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrameCoordinatesFromScratchBuilder frameCoordinatesFromScratchBuilder() {
        return new GmxFrameCoordinatesFromScratchBuilder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrameCoordinatesFromArraysBuilder frameCoordinatesFromArraysBuilder() {
        return new GmxFrameCoordinatesFromArraysBuilder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @SuppressWarnings("WeakerAccess")
    protected GmxFrame frame() {
        return new GmxFrame();
    }

    // ======== INTERFACE ========

    @Bean
    public Supplier<GmxFrameStructureFromGroFileBuilder> frameStructureFromGroFileBuilderSupplier() {
        return this::frameStructureFromGroFileBuilder;
    }

    @Bean
    public Supplier<GmxFrameStructureFromScratchBuilder> frameStructureFromScratchBuilderSupplier() {
        return this::frameStructureFromScratchBuilder;
    }

    @Bean
    public Supplier<GmxFrameStructureFromArraysBuilder> frameStructureFromArraysBuilderSupplier() {
        return this::frameStructureFromArraysBuilder;
    }

    @Bean
    public Supplier<GmxFrameCoordinatesFromGroFileBuilder> frameCoordinatesFromGroFileBuilderSupplier() {
        return this::frameCoordinatesFromGroFileBuilder;
    }

    @Bean
    public Supplier<GmxFrameCoordinatesFromScratchBuilder> frameCoordinatesFromScratchBuilderSupplier() {
        return this::frameCoordinatesFromScratchBuilder;
    }

    @Bean
    public Supplier<GmxFrameCoordinatesFromArraysBuilder> frameCoordinatesFromArraysBuilderSupplier() {
        return this::frameCoordinatesFromArraysBuilder;
    }

    @Bean
    public DuoFactory<GmxFrame, GmxFrameStructure, GmxFrameCoordinates> frameFactory() {
        return this::getFrame;
    }

    // ======== REALIZATION ========

    private GmxFrame getFrame(GmxFrameStructure structure, GmxFrameCoordinates coordinates) {
        GmxFrame frame = frame();
        frame.setFrameStructure(structure);
        frame.setFrameCoordinates(coordinates);

        frame.validateFrameStructureAtoms();
        if (structure.getResiduesNum() > 0) frame.validateFrameStructureResidues();

        frame.initAtoms();
        frame.initResidues();
        Logger.log("Frame No " + frame.getFrameNo() + " successfully created");
        return frame;
    }

}
