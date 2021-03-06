package com.asemenkov.gromacs.frame.structure;

import com.asemenkov.gromacs.frame.exceptions.GmxFrameException;
import com.asemenkov.gromacs.particles.GmxResidue;
import com.asemenkov.gromacs.particles.annotations.ResidueAtom;
import com.asemenkov.utils.io.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author asemenkov
 * @since Aug 01, 2018
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GmxFrameStructureFromScratchBuilder
        extends GmxAbstractFrameStructureBuilder<GmxFrameStructureFromScratchBuilder> {

    private final Map<String, Integer> freeAtomsCountMap = new HashMap<>();
    private final Map<String, Integer> residuesCountMap = new HashMap<>();

    // ======== INTERFACE =========

    public GmxFrameStructureFromScratchBuilder withFreeAtoms(String atomAbbreviation, int count) {
        Integer atomsCount = freeAtomsCountMap.get(atomAbbreviation);
        this.freeAtomsCountMap.put(atomAbbreviation, atomsCount == null ? count : atomsCount + count);
        return this;
    }

    public GmxFrameStructureFromScratchBuilder withResidues(String residueAbbreviation, int count) {
        Integer residuesCount = residuesCountMap.get(residueAbbreviation);
        this.residuesCountMap.put(residueAbbreviation, residuesCount == null ? count : residuesCount + count);
        return this;
    }

    @Override
    public GmxFrameStructure build() {
        validateAtLeastOneMapIsNotEmpty();
        validateBox();

        int atomsNum = calculateAtomsNumFromMaps();
        GmxFrameStructure frameStructure = new GmxFrameStructure(atomsNum);
        frameStructure.setDescription(description);
        frameStructure.setBox(box);

        AtomicInteger atomsCounter = new AtomicInteger(0);
        AtomicInteger residuesCounter = new AtomicInteger(0);

        validateFreeAtomsMap();
        freeAtomsCountMap.forEach((key, value) -> IntStream.range(0, value).parallel().forEach(i -> {
            int atomNo = atomsCounter.getAndIncrement();
            frameStructure.setAtomsClass(atomNo, atomReflectionData.getAtomClass(key));
            frameStructure.setAtomAbbreviation(atomNo, key);
        }));

        validateResiduesMap();
        residuesCountMap.forEach((key, value) -> {
            Class<? extends GmxResidue> residueClass = residueReflectionData.getResidueClass(key);
            String[] abbreviations = Arrays.stream(residueReflectionData.getResidueAtomsFields(residueClass))
                    .map(field -> field.getAnnotation(ResidueAtom.class).value()) //
                    .toArray(String[]::new);

            int[] residueIndexes = IntStream.range(0, value).parallel().map(i -> {
                int residueLength = abbreviations.length;
                int residueNo = residuesCounter.getAndIncrement();
                int atomNo = atomsCounter.getAndAdd(residueLength);
                int sum = atomNo + residueLength;

                for (int j = 0; j < residueLength; j++) {
                    int index = atomNo + j;
                    frameStructure.setAtomsClass(index, atomReflectionData.getAtomClass(abbreviations[j]));
                    frameStructure.setAtomAbbreviation(index, abbreviations[j]);
                }

                frameStructure.setResidueAtoms(residueNo, IntStream.range(atomNo, sum).toArray());
                return residueNo;
            }).toArray();

            frameStructure.setResidueIndexes(residueClass, residueIndexes);
        });

        Logger.log("Frame structure successfully created from scratch");
        return frameStructure;
    }

    // ======== VALIDATION METHODS ========

    private void validateAtLeastOneMapIsNotEmpty() {
        if (freeAtomsCountMap.isEmpty() && residuesCountMap.isEmpty()) //
            throw new GmxFrameException("Both maps (atoms and residues) are empty.");
    }

    private void validateFreeAtomsMap() {
        List<String> errorAbbrs = freeAtomsCountMap.keySet().stream() //
                .filter(atomReflectionData::isAbbreviationAbsent) //
                .collect(Collectors.toList());

        if (errorAbbrs.size() > 0) //
            throw new GmxFrameException("The following GmxAtom abbreviations aren't recognized: " + //
                    errorAbbrs.stream().collect(Collectors.joining(", ")));
    }

    private void validateResiduesMap() {
        List<String> errorAbbrs = residuesCountMap.keySet().stream() //
                .filter(residueReflectionData::isAbbreviationAbsent) //
                .collect(Collectors.toList());

        if (errorAbbrs.size() > 0) //
            throw new GmxFrameException("The following GmxResidue abbreviations aren't recognized: " + //
                    errorAbbrs.stream().collect(Collectors.joining(", ")));
    }

    // ======== SUPPORT METHODS ========

    private int calculateAtomsNumFromMaps() {
        int freeAtoms = freeAtomsCountMap.values().stream().mapToInt(Integer::intValue).sum();
        int residueAtoms = residuesCountMap.entrySet().stream().mapToInt(entry -> residueReflectionData //
                .getResidueAtomsFields(residueReflectionData.getResidueClass(entry.getKey())).length //
                * entry.getValue()).sum();
        return freeAtoms + residueAtoms;
    }

}
